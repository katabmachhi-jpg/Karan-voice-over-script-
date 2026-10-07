package com.example.player

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "AudioPlayerManager"

data class AudioPlayerState(
    val currentFilePath: String? = null,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val volume: Float = 1.0f
) {
    val progressFraction: Float
        get() = if (durationMs > 0) {
            (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val formattedCurrentTime: String
        get() = formatTime(currentPositionMs)

    val formattedDuration: String
        get() = formatTime(durationMs)

    private fun formatTime(ms: Int): String {
        val totalSeconds = (ms / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

class AudioPlayerManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var mediaPlayer: MediaPlayer? = null
    private var progressTrackingJob: Job? = null

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    fun play(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            Log.e(TAG, "File does not exist: $filePath")
            return
        }

        try {
            stop()

            val player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                val currentVol = _playerState.value.volume
                setVolume(currentVol, currentVol)
                prepare()
                start()
            }
            mediaPlayer = player

            val duration = player.duration.coerceAtLeast(100)
            _playerState.value = _playerState.value.copy(
                currentFilePath = filePath,
                isPlaying = true,
                isPaused = false,
                currentPositionMs = 0,
                durationMs = duration
            )

            player.setOnCompletionListener {
                stopTracking()
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    isPaused = false,
                    currentPositionMs = duration
                )
            }

            startTracking()
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio file: $filePath", e)
            stop()
        }
    }

    fun pause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                stopTracking()
                _playerState.value = _playerState.value.copy(
                    isPlaying = false,
                    isPaused = true,
                    currentPositionMs = player.currentPosition
                )
            }
        }
    }

    fun resume() {
        mediaPlayer?.let { player ->
            if (_playerState.value.isPaused) {
                player.start()
                _playerState.value = _playerState.value.copy(
                    isPlaying = true,
                    isPaused = false
                )
                startTracking()
            }
        }
    }

    fun stop() {
        stopTracking()
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing player", e)
        }
        mediaPlayer = null
        _playerState.value = _playerState.value.copy(
            isPlaying = false,
            isPaused = false,
            currentPositionMs = 0
        )
    }

    fun replay() {
        val path = _playerState.value.currentFilePath
        if (path != null) {
            play(path)
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let { player ->
            val safePos = positionMs.coerceIn(0, player.duration)
            player.seekTo(safePos)
            _playerState.value = _playerState.value.copy(
                currentPositionMs = safePos
            )
        }
    }

    fun setVolume(volume: Float) {
        val safeVol = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(safeVol, safeVol)
        _playerState.value = _playerState.value.copy(volume = safeVol)
    }

    private fun startTracking() {
        stopTracking()
        progressTrackingJob = scope.launch {
            while (isActive && mediaPlayer != null && _playerState.value.isPlaying) {
                mediaPlayer?.let { player ->
                    try {
                        if (player.isPlaying) {
                            _playerState.value = _playerState.value.copy(
                                currentPositionMs = player.currentPosition,
                                durationMs = player.duration
                            )
                        }
                    } catch (e: Exception) {
                        // Player might be releasing
                    }
                }
                delay(80)
            }
        }
    }

    private fun stopTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = null
    }

    fun exportAudio(context: Context, filePath: String, title: String) {
        val file = File(filePath)
        if (!file.exists()) {
            Log.e(TAG, "Cannot export missing file: $filePath")
            return
        }

        try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/wav"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "Generated with Karan Voice AI - $title")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Export Voice Audio").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting audio", e)
        }
    }

    fun release() {
        stop()
    }
}
