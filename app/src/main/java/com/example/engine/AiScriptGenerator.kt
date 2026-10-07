package com.example.engine

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private const val TAG = "AiScriptGenerator"
private const val MODEL_NAME = "gemini-3.5-flash"
private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

enum class ScriptTone(val displayName: String, val promptDescription: String) {
    GAMING_HYPE("Gaming Hype", "High energy, punchy Hinglish gamer slang, fast-paced commentary suitable for BGMI/GTA/FreeFire"),
    STORY_DRAMA("Story & Drama", "Dramatic, suspenseful narration in authentic Hindi with rich emotional pauses"),
    SHORTS_HOOK("Shorts Viral Hook", "Ultra-engaging 30-second hook with fast retention lines and call to action"),
    TECH_REVIEW("Tech Review", "Clear, articulate, conversational Hindi/English explaining gadget features objectively"),
    MOTIVATIONAL("Motivational", "Inspiring, deep voiceover with strong impact and uplifting cadence")
}

enum class ScriptLength(val displayName: String, val targetWordCount: Int) {
    SHORT("Short (~30s)", 65),
    MEDIUM("Medium (~60s)", 150),
    LONG("Long (~2 min)", 300)
}

class AiScriptGenerator {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun isApiKeyConfigured(): Boolean {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun generateScript(
        topic: String,
        tone: ScriptTone,
        length: ScriptLength,
        customInstruction: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (!isApiKeyConfigured()) {
            // Provide intelligent fallback script based on tone & topic if key is not configured
            val fallback = generateOfflineScriptTemplate(topic, tone, length)
            return@withContext Result.success(fallback)
        }

        try {
            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$key"

            val systemInstruction = """
                You are the master voiceover scriptwriter for 'KARAN VOICE AI'.
                Your task is to write a ready-to-speak voiceover script based on the user's topic.
                Style Guidelines:
                1. Tone: ${tone.promptDescription}
                2. Target word count: approximately ${length.targetWordCount} words.
                3. Language: Hindi, Hinglish, or English as requested by context.
                4. CRITICAL: Do NOT output stage directions, camera angles, sound effects, or brackets like [Music], (Intro), [Sound of explosion].
                5. Output ONLY the pure words that will be spoken aloud by the voice actor.
                6. Use punctuation (commas, ellipsis ..., exclamation marks !) strategically to create natural breathing pauses for TTS engines.
                ${if (customInstruction.isNotBlank()) "Special note: $customInstruction" else ""}
            """.trimIndent()

            val prompt = "Write a voiceover script about: $topic"

            val rootJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val systemInstructionObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", systemInstructionObj)

                val generationConfig = JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                    put("maxOutputTokens", 1200)
                }
                put("generationConfig", generationConfig)
            }

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini API error ${response.code}: $responseString")
                return@withContext Result.failure(Exception("Gemini API error (HTTP ${response.code})"))
            }

            val jsonResponse = JSONObject(responseString)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (text.isNullOrBlank()) {
                return@withContext Result.failure(Exception("No script generated."))
            }

            // Clean up any stray markdown headers or brackets
            val cleaned = text
                .replace(Regex("(?m)^#+ .*$"), "")
                .replace(Regex("\\[.*?\\]"), "")
                .replace(Regex("\\(.*?\\)"), "")
                .trim()

            Result.success(cleaned)
        } catch (e: Exception) {
            Log.e(TAG, "Error generating AI script", e)
            Result.failure(e)
        }
    }

    private fun generateOfflineScriptTemplate(
        topic: String,
        tone: ScriptTone,
        length: ScriptLength
    ): String {
        val topicClean = topic.ifBlank { "Gaming Highlights" }
        return when (tone) {
            ScriptTone.GAMING_HYPE -> {
                "Arey bhai bhai bhai! $topicClean me kya hi clutch moment hua hai aaj! " +
                "Zone poora shrink ho chuka tha, aage poori squad ready baithi thi... " +
                "Lekin humne smoke daalke sidha push kiya! Ek knock, do knock... aur ye ho gaya ultimate clutch! " +
                "Video ko turant like karo aur channel ko subscribe karna mat bhoolna!"
            }
            ScriptTone.STORY_DRAMA -> {
                "रात के सन्नाटे में, $topicClean की एक ऐसी अनसुनी दास्तान सामने आई, जिसने सबको चौंका दिया... " +
                "पुराने पन्नों में दर्ज यह रहस्य सदियों से दबा हुआ था। " +
                "लेकिन जब सच सामने आया, तो हर किसी की आंखें फटी की फटी रह गईं। सुनिए यह अद्भुत कहानी।"
            }
            ScriptTone.SHORTS_HOOK -> {
                "Ruko ruko! Agar tum $topicClean ke baare me nahi jaante, toh ye 30 seconds tumhari life badal denge! " +
                "Secret number one: Kabhi bhi bina planning ke rush mat karo. " +
                "Secret number two: Sahi setting lagao aur dekho result! Video save karlo abhi ke abhi!"
            }
            ScriptTone.TECH_REVIEW -> {
                "Welcome back everyone! Today we are taking a deep look at $topicClean. " +
                "The build quality is solid, the performance is super smooth, and the value for money is hard to beat. " +
                "Let us test out all the features and see if it is worth your upgrade!"
            }
            ScriptTone.MOTIVATIONAL -> {
                "हर मुश्किल रास्ते के पार एक नई मंज़िल इंतज़ार कर रही है। $topicClean का सफर आसान नहीं था, " +
                "लेकिन हौसले कभी नहीं हारे। जब दुनिया कहे रुक जाओ, तभी अपनी सबसे तेज़ दौड़ लगाओ। " +
                "विश्वास रखो, जीत तुम्हारी ही होगी।"
            }
        }
    }
}
