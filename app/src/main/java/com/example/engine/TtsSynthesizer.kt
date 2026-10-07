package com.example.engine

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import java.util.UUID

private const val TAG = "KaranTtsSynthesizer"

data class SynthesisOutput(
    val audioFile: File,
    val durationMs: Long,
    val fileSizeBytes: Long
)

class TtsSynthesizer(private val context: Context) {

    private var tts: TextToSpeech? = null

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _availableEngines = MutableStateFlow<List<String>>(emptyList())
    val availableEngines: StateFlow<List<String>> = _availableEngines.asStateFlow()

    private val _isSpeakingPreview = MutableStateFlow(false)
    val isSpeakingPreview: StateFlow<Boolean> = _isSpeakingPreview.asStateFlow()

    init {
        initEngine()
    }

    private fun initEngine() {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                Log.d(TAG, "TextToSpeech initialized successfully")
                tts?.let { engine ->
                    try {
                        val engines = engine.engines?.map { it.name } ?: emptyList()
                        _availableEngines.value = engines
                    } catch (e: Exception) {
                        Log.w(TAG, "Could not query TTS engines", e)
                    }
                }
                _isInitialized.value = true
            } else {
                Log.e(TAG, "TextToSpeech initialization failed with status $status")
                _isInitialized.value = false
            }
        }
    }

    /**
     * Synthesizes [rawText] with parameters configured for [voice] into a local WAV audio file.
     * Uses Android TextToSpeech.synthesizeToFile which runs on a background worker thread.
     */
    suspend fun synthesizeToFile(
        rawText: String,
        voice: VoiceProfile,
        speedMultiplier: Float,
        pitchMultiplier: Float,
        isCreatorMode: Boolean
    ): Result<SynthesisOutput> = withContext(Dispatchers.IO) {
        val engine = tts
        if (engine == null || !_isInitialized.value) {
            return@withContext Result.failure(IllegalStateException("Speech engine is not initialized yet."))
        }

        val cleanedText = prepareTextForVoice(rawText, voice, isCreatorMode)
        if (cleanedText.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Text cannot be empty."))
        }

        // Prepare target audio file
        val outputDir = File(context.filesDir, "voices").apply {
            if (!exists()) mkdirs()
        }
        val fileName = "karan_voice_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.wav"
        val targetFile = File(outputDir, fileName)

        // Configure engine language and voice
        configureEngineForVoice(engine, voice, speedMultiplier, pitchMultiplier, isCreatorMode)

        val utteranceId = "synth_${System.currentTimeMillis()}"
        val deferred = CompletableDeferred<Boolean>()

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d(TAG, "Synthesis started for $utteranceId")
            }

            override fun onDone(utteranceId: String?) {
                Log.d(TAG, "Synthesis completed for $utteranceId")
                deferred.complete(true)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                Log.e(TAG, "Synthesis error for $utteranceId")
                deferred.complete(false)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.e(TAG, "Synthesis error for $utteranceId, code: $errorCode")
                deferred.complete(false)
            }
        })

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        val synthStatus = engine.synthesizeToFile(cleanedText, params, targetFile, utteranceId)
        if (synthStatus != TextToSpeech.SUCCESS) {
            return@withContext Result.failure(IllegalStateException("TTS synthesizeToFile rejected with status: $synthStatus"))
        }

        val success = deferred.await()
        if (!success || !targetFile.exists() || targetFile.length() == 0L) {
            return@withContext Result.failure(IllegalStateException("Audio file could not be generated."))
        }

        // Measure duration
        val durationMs = extractDuration(targetFile)

        Result.success(
            SynthesisOutput(
                audioFile = targetFile,
                durationMs = durationMs,
                fileSizeBytes = targetFile.length()
            )
        )
    }

    /**
     * Plays a quick short preview of the voice profile directly to the speaker.
     */
    fun speakPreview(voice: VoiceProfile, onComplete: () -> Unit = {}) {
        val engine = tts ?: return
        if (!_isInitialized.value) return

        _isSpeakingPreview.value = true
        configureEngineForVoice(engine, voice, 1.0f, 1.0f, voice.category == VoiceCategory.CREATOR)

        val utteranceId = "preview_${System.currentTimeMillis()}"
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                _isSpeakingPreview.value = false
                onComplete()
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeakingPreview.value = false
                onComplete()
            }
        })

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        engine.speak(voice.sampleScript, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stopPreview() {
        tts?.stop()
        _isSpeakingPreview.value = false
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.w(TAG, "Error shutting down TTS", e)
        }
    }

    private fun configureEngineForVoice(
        engine: TextToSpeech,
        voice: VoiceProfile,
        speedMultiplier: Float,
        pitchMultiplier: Float,
        isCreatorMode: Boolean
    ) {
        // Select Locale
        val locale = when (voice.languageCode) {
            "hi-IN" -> Locale.forLanguageTag("hi-IN")
            "en-IN" -> Locale.forLanguageTag("en-IN")
            "en-US" -> Locale.US
            else -> {
                if (voice.languageCode.startsWith("hi")) Locale.forLanguageTag("hi-IN") else Locale.ENGLISH
            }
        }

        val langResult = engine.setLanguage(locale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback to general English if Hindi is missing on device TTS
            Log.w(TAG, "Locale $locale not directly supported, trying fallback")
            engine.setLanguage(Locale.forLanguageTag("en-IN"))
        }

        // Try to match voice gender if voices are available
        try {
            val availableVoices = engine.voices
            if (!availableVoices.isNullOrEmpty()) {
                val matched = availableVoices.find { v ->
                    val matchesLang = v.locale.language == locale.language
                    val matchesGender = if (voice.gender == VoiceGender.FEMALE) {
                        v.name.contains("female", ignoreCase = true) || v.name.contains("f0", ignoreCase = true)
                    } else {
                        v.name.contains("male", ignoreCase = true) || v.name.contains("m0", ignoreCase = true)
                    }
                    matchesLang && matchesGender
                } ?: availableVoices.find { it.locale.language == locale.language }

                if (matched != null) {
                    engine.voice = matched
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not set specific voice profile", e)
        }

        // Compute effective pitch and speed
        var finalPitch = voice.basePitch * pitchMultiplier
        var finalSpeed = voice.baseSpeed * speedMultiplier

        if (isCreatorMode) {
            // YouTube Gaming voiceover tuning: energetic delivery & slightly faster pacing
            finalSpeed *= 1.08f
            finalPitch = (finalPitch * 1.04f).coerceIn(0.6f, 1.8f)
        }

        finalPitch = finalPitch.coerceIn(0.5f, 2.0f)
        finalSpeed = finalSpeed.coerceIn(0.5f, 2.0f)

        engine.setPitch(finalPitch)
        engine.setSpeechRate(finalSpeed)
    }

    private fun prepareTextForVoice(
        input: String,
        voice: VoiceProfile,
        isCreatorMode: Boolean
    ): String {
        var processed = input.trim()

        // Replace common gaming / Hinglish abbreviations with spoken forms for crystal clear pronunciation
        val gamingReplacements = mapOf(
            "\\bOP\\b" to "overpowered",
            "\\bGG\\b" to "good game",
            "\\bBGMI\\b" to "Battlegrounds Mobile India",
            "\\bGTA\\b" to "G T A",
            "\\bHP\\b" to "health",
            "\\bFPS\\b" to "F P S",
            "\\b1v4\\b" to "one versus four",
            "\\b1v1\\b" to "one versus one",
            "\\bKD\\b" to "K D ratio",
            "\\bPOV\\b" to "P O V",
            "\\bEZ\\b" to "easy"
        )

        for ((pattern, replacement) in gamingReplacements) {
            processed = processed.replace(Regex(pattern, RegexOption.IGNORE_CASE), replacement)
        }

        if (isCreatorMode) {
            // Add natural pauses for gaming commentary: commas and ellipsis ensure natural cadence
            processed = processed
                .replace("!", "! ")
                .replace("?", "? ")
                .replace(Regex("\\s+"), " ")
        }

        return processed
    }

    private fun extractDuration(file: File): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            durationStr?.toLongOrNull() ?: 3000L
        } catch (e: Exception) {
            3000L
        }
    }
}
