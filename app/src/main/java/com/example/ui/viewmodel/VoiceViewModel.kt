package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.VoiceApplication
import com.example.data.local.VoiceHistoryEntity
import com.example.engine.AiScriptGenerator
import com.example.engine.ScriptLength
import com.example.engine.ScriptTone
import com.example.engine.VoiceCatalog
import com.example.engine.VoiceProfile
import com.example.player.AudioPlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class GeneratorUiState(
    val text: String = "",
    val selectedVoice: VoiceProfile = VoiceCatalog.HINDI_MALE,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val volume: Float = 1.0f,
    val isCreatorMode: Boolean = false,
    val isGenerating: Boolean = false,
    val generationProgress: Float = 0f,
    val lastGeneratedVoice: VoiceHistoryEntity? = null,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    // AI Script Generator state
    val isGeneratingScript: Boolean = false,
    val scriptTopic: String = "",
    val selectedTone: ScriptTone = ScriptTone.GAMING_HYPE,
    val selectedLength: ScriptLength = ScriptLength.MEDIUM,
    val aiGeneratedScript: String? = null,
    val isApiKeyConfigured: Boolean = false
)

class VoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val voiceApp = application as VoiceApplication
    private val db = voiceApp.database
    private val settings = voiceApp.settings
    private val synthesizer = voiceApp.ttsSynthesizer
    private val scriptGenerator = voiceApp.aiScriptGenerator
    val playerManager = voiceApp.audioPlayerManager

    val playerState: StateFlow<AudioPlayerState> = playerManager.playerState
    val isEngineReady: StateFlow<Boolean> = synthesizer.isInitialized
    val isSpeakingPreview: StateFlow<Boolean> = synthesizer.isSpeakingPreview

    val historyList: StateFlow<List<VoiceHistoryEntity>> = db.voiceDao().getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(
        GeneratorUiState(
            selectedVoice = VoiceCatalog.findById(settings.defaultVoiceId),
            speed = settings.defaultSpeed,
            pitch = settings.defaultPitch,
            isCreatorMode = settings.creatorModeActive,
            isApiKeyConfigured = scriptGenerator.isApiKeyConfigured()
        )
    )
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

    fun updateText(newText: String) {
        _uiState.value = _uiState.value.copy(text = newText, errorMessage = null)
    }

    fun selectVoice(voice: VoiceProfile) {
        _uiState.value = _uiState.value.copy(
            selectedVoice = voice,
            errorMessage = null
        )
    }

    fun updateSpeed(speed: Float) {
        _uiState.value = _uiState.value.copy(speed = speed)
    }

    fun updatePitch(pitch: Float) {
        _uiState.value = _uiState.value.copy(pitch = pitch)
    }

    fun updateVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(volume = volume)
        playerManager.setVolume(volume)
    }

    fun toggleCreatorMode(enabled: Boolean) {
        val voice = if (enabled) VoiceCatalog.YOUTUBE_GAMING else _uiState.value.selectedVoice
        _uiState.value = _uiState.value.copy(
            isCreatorMode = enabled,
            selectedVoice = voice,
            speed = if (enabled) 1.15f else 1.0f,
            pitch = if (enabled) 1.05f else 1.0f
        )
        settings.creatorModeActive = enabled
    }

    fun loadSampleScript(script: String) {
        _uiState.value = _uiState.value.copy(text = script)
    }

    fun updateScriptTopic(topic: String) {
        _uiState.value = _uiState.value.copy(scriptTopic = topic)
    }

    fun selectScriptTone(tone: ScriptTone) {
        _uiState.value = _uiState.value.copy(selectedTone = tone)
    }

    fun selectScriptLength(length: ScriptLength) {
        _uiState.value = _uiState.value.copy(selectedLength = length)
    }

    fun generateAiScript(customInstruction: String = "") {
        val topic = _uiState.value.scriptTopic.trim().ifEmpty { "Gaming Highlights & Clutch Play" }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGeneratingScript = true,
                errorMessage = null
            )
            val result = scriptGenerator.generateScript(
                topic = topic,
                tone = _uiState.value.selectedTone,
                length = _uiState.value.selectedLength,
                customInstruction = customInstruction
            )
            result.fold(
                onSuccess = { generatedScript ->
                    _uiState.value = _uiState.value.copy(
                        isGeneratingScript = false,
                        aiGeneratedScript = generatedScript,
                        text = generatedScript, // Also set as active text
                        infoMessage = "Voiceover script generated successfully!"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isGeneratingScript = false,
                        errorMessage = error.localizedMessage ?: "Failed to generate AI script."
                    )
                }
            )
        }
    }

    fun previewVoice(voice: VoiceProfile) {
        synthesizer.speakPreview(voice)
    }

    fun stopVoicePreview() {
        synthesizer.stopPreview()
    }

    fun generateVoice() {
        val currentText = _uiState.value.text.trim()
        if (currentText.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter some text in Hindi, English, or Hinglish to generate voice."
            )
            return
        }

        if (!_uiState.value.isGenerating) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(
                    isGenerating = true,
                    generationProgress = 0.15f,
                    errorMessage = null,
                    infoMessage = null
                )

                val progressJob = launch {
                    for (step in 20..85 step 15) {
                        delay(120)
                        if (_uiState.value.isGenerating) {
                            _uiState.value = _uiState.value.copy(generationProgress = step / 100f)
                        }
                    }
                }

                val voice = _uiState.value.selectedVoice
                val speed = _uiState.value.speed
                val pitch = _uiState.value.pitch
                val isCreator = _uiState.value.isCreatorMode

                val result = synthesizer.synthesizeToFile(
                    rawText = currentText,
                    voice = voice,
                    speedMultiplier = speed,
                    pitchMultiplier = pitch,
                    isCreatorMode = isCreator
                )

                progressJob.cancel()

                result.fold(
                    onSuccess = { output ->
                        _uiState.value = _uiState.value.copy(generationProgress = 1.0f)
                        delay(150)

                        val title = currentText.take(40).replace("\n", " ").trim() + if (currentText.length > 40) "..." else ""
                        val snippet = currentText.take(120).replace("\n", " ").trim()

                        val historyEntity = VoiceHistoryEntity(
                            title = title,
                            snippet = snippet,
                            fullText = currentText,
                            voiceId = voice.id,
                            voiceName = voice.name,
                            languageTag = voice.languageDisplayName,
                            filePath = output.audioFile.absolutePath,
                            fileSizeBytes = output.fileSizeBytes,
                            durationMs = output.durationMs,
                            speed = speed,
                            pitch = pitch,
                            isCreatorMode = isCreator,
                            createdAt = System.currentTimeMillis()
                        )

                        var savedEntity = historyEntity
                        if (settings.autoSave) {
                            val id = db.voiceDao().insertVoice(historyEntity)
                            savedEntity = historyEntity.copy(id = id)
                        }

                        _uiState.value = _uiState.value.copy(
                            isGenerating = false,
                            generationProgress = 0f,
                            lastGeneratedVoice = savedEntity,
                            infoMessage = "Voice generated successfully!"
                        )

                        playerManager.play(output.audioFile.absolutePath)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isGenerating = false,
                            generationProgress = 0f,
                            errorMessage = error.localizedMessage ?: "Failed to generate speech. Please try again."
                        )
                    }
                )
            }
        }
    }

    fun playAudio(filePath: String) {
        playerManager.play(filePath)
    }

    fun pauseAudio() {
        playerManager.pause()
    }

    fun resumeAudio() {
        playerManager.resume()
    }

    fun stopAudio() {
        playerManager.stop()
    }

    fun replayAudio() {
        playerManager.replay()
    }

    fun seekAudio(ms: Int) {
        playerManager.seekTo(ms)
    }

    fun exportAudio(context: Context, item: VoiceHistoryEntity) {
        playerManager.exportAudio(context, item.filePath, item.title)
    }

    fun deleteHistoryItem(item: VoiceHistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(item.filePath)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                // Ignore file delete errors
            }
            db.voiceDao().deleteVoice(item)

            if (_uiState.value.lastGeneratedVoice?.id == item.id) {
                playerManager.stop()
                _uiState.value = _uiState.value.copy(lastGeneratedVoice = null)
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            playerManager.stop()
            db.voiceDao().clearAll()
            val dir = File(voiceApp.filesDir, "voices")
            dir.listFiles()?.forEach { it.delete() }
            _uiState.value = _uiState.value.copy(lastGeneratedVoice = null)
        }
    }

    fun saveSettings(defaultVoiceId: String, speed: Float, pitch: Float, autoSave: Boolean) {
        settings.defaultVoiceId = defaultVoiceId
        settings.defaultSpeed = speed
        settings.defaultPitch = pitch
        settings.autoSave = autoSave
        _uiState.value = _uiState.value.copy(
            selectedVoice = VoiceCatalog.findById(defaultVoiceId),
            speed = speed,
            pitch = pitch
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, infoMessage = null)
    }
}
