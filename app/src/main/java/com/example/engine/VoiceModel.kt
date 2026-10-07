package com.example.engine

enum class VoiceGender {
    MALE,
    FEMALE
}

enum class VoiceCategory {
    HINDI,
    ENGLISH,
    STYLED,
    CREATOR
}

data class VoiceProfile(
    val id: String,
    val name: String,
    val subtitle: String,
    val languageCode: String,      // "hi-IN", "en-IN", "en-US", etc.
    val languageDisplayName: String,
    val gender: VoiceGender,
    val category: VoiceCategory,
    val basePitch: Float,          // Default baseline pitch for this style
    val baseSpeed: Float,          // Default baseline speed for this style
    val badge: String,             // e.g. "Hindi", "Gaming", "Story"
    val sampleScript: String,
    val description: String
)

object VoiceCatalog {
    val HINDI_MALE = VoiceProfile(
        id = "hindi_male",
        name = "Hindi Male",
        subtitle = "Natural & authentic Indian voice",
        languageCode = "hi-IN",
        languageDisplayName = "Hindi (India)",
        gender = VoiceGender.MALE,
        category = VoiceCategory.HINDI,
        basePitch = 0.95f,
        baseSpeed = 1.0f,
        badge = "Hindi",
        sampleScript = "नमस्ते दोस्तों! करण वॉइस एआई में आपका स्वागत है।",
        description = "Clear, resonant Hindi pronunciation ideal for daily conversations and announcements."
    )

    val HINDI_FEMALE = VoiceProfile(
        id = "hindi_female",
        name = "Hindi Female",
        subtitle = "Warm, melodious & expressive",
        languageCode = "hi-IN",
        languageDisplayName = "Hindi (India)",
        gender = VoiceGender.FEMALE,
        category = VoiceCategory.HINDI,
        basePitch = 1.25f,
        baseSpeed = 1.02f,
        badge = "Hindi",
        sampleScript = "सुप्रभात! आपका दिन मंगलमय और ऊर्जावान हो।",
        description = "Gentle and articulate tone suitable for tutorials, audiobooks, and warm narrations."
    )

    val ENGLISH_MALE = VoiceProfile(
        id = "english_male",
        name = "English Male",
        subtitle = "Crisp, articulate standard voice",
        languageCode = "en-IN",
        languageDisplayName = "English (Indian/Global)",
        gender = VoiceGender.MALE,
        category = VoiceCategory.ENGLISH,
        basePitch = 0.98f,
        baseSpeed = 1.0f,
        badge = "English",
        sampleScript = "Welcome to Karan Voice AI. Convert any script into high fidelity voiceover instantly.",
        description = "Balanced, professional articulation for podcasts, explainer videos, and reels."
    )

    val ENGLISH_FEMALE = VoiceProfile(
        id = "english_female",
        name = "English Female",
        subtitle = "Polished, smooth & engaging",
        languageCode = "en-IN",
        languageDisplayName = "English (Indian/Global)",
        gender = VoiceGender.FEMALE,
        category = VoiceCategory.ENGLISH,
        basePitch = 1.22f,
        baseSpeed = 1.0f,
        badge = "English",
        sampleScript = "Transform your imagination into studio-quality audio in just seconds.",
        description = "Silky and confident delivery engineered for presentations and social content."
    )

    val DEEP_MALE = VoiceProfile(
        id = "deep_male",
        name = "Deep Male",
        subtitle = "Cinematic, baritone gravitas",
        languageCode = "hi-IN",
        languageDisplayName = "Hinglish / Cinematic",
        gender = VoiceGender.MALE,
        category = VoiceCategory.STYLED,
        basePitch = 0.72f,
        baseSpeed = 0.94f,
        badge = "Cinematic",
        sampleScript = "रात के अंधेरे में एक आवाज़ गूंजी... और पूरी कहानी बदल गई।",
        description = "Deep bass and commanding presence for movie trailers, horror, and suspense stories."
    )

    val ENERGETIC_MALE = VoiceProfile(
        id = "energetic_male",
        name = "Energetic Male",
        subtitle = "High-octane, punchy & hyped",
        languageCode = "hi-IN",
        languageDisplayName = "Hinglish / Hype",
        gender = VoiceGender.MALE,
        category = VoiceCategory.STYLED,
        basePitch = 1.08f,
        baseSpeed = 1.18f,
        badge = "High Energy",
        sampleScript = "अरे भाई! ये क्या गेमप्ले था! वीडियो को तुरंत लाइक और सब्सक्राइब करो!",
        description = "Fast-paced, vibrant delivery tailored for YouTube shorts, promotions, and action."
    )

    val CALM_MALE = VoiceProfile(
        id = "calm_male",
        name = "Calm Male",
        subtitle = "Soothing, relaxed & meditative",
        languageCode = "hi-IN",
        languageDisplayName = "Hindi / Relaxed",
        gender = VoiceGender.MALE,
        category = VoiceCategory.STYLED,
        basePitch = 0.88f,
        baseSpeed = 0.88f,
        badge = "Peaceful",
        sampleScript = "गहरी सांस लें, शांत रहें और इस पल का आनंद महसूस करें।",
        description = "Soft cadence and gentle breath pauses for meditation, wellness, and bedtime tales."
    )

    val STORYTELLING = VoiceProfile(
        id = "storytelling",
        name = "Storytelling Voice",
        subtitle = "Dramatic cadence & lore narration",
        languageCode = "hi-IN",
        languageDisplayName = "Hindi / Drama",
        gender = VoiceGender.MALE,
        category = VoiceCategory.STYLED,
        basePitch = 0.90f,
        baseSpeed = 0.96f,
        badge = "Narrator",
        sampleScript = "बहुत समय पहले की बात है, पहाड़ों के पार एक ऐसा रहस्य छिपा था...",
        description = "Expressive inflection with rich pauses engineered for mythology, history, and stories."
    )

    val YOUTUBE_GAMING = VoiceProfile(
        id = "youtube_gaming",
        name = "YouTube Voiceover",
        subtitle = "Gaming creator mode (Hindi & Hinglish)",
        languageCode = "hi-IN",
        languageDisplayName = "Hinglish Gaming",
        gender = VoiceGender.MALE,
        category = VoiceCategory.CREATOR,
        basePitch = 1.05f,
        baseSpeed = 1.14f,
        badge = "Creator Mode",
        sampleScript = "Yo guys! Aaj hum karne wale hain ultimate 1v4 clutch gameplay. Ready ho jao!",
        description = "Optimized for gaming creators with clear Hindi/Hinglish pacing, punchy cadence, and natural flow."
    )

    val allVoices: List<VoiceProfile> = listOf(
        HINDI_MALE,
        HINDI_FEMALE,
        ENGLISH_MALE,
        ENGLISH_FEMALE,
        DEEP_MALE,
        ENERGETIC_MALE,
        CALM_MALE,
        STORYTELLING,
        YOUTUBE_GAMING
    )

    fun findById(id: String): VoiceProfile {
        return allVoices.find { it.id == id } ?: HINDI_MALE
    }
}
