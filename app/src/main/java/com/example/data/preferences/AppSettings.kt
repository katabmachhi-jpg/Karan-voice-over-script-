package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("karan_voice_settings", Context.MODE_PRIVATE)

    var defaultVoiceId: String
        get() = prefs.getString(KEY_DEFAULT_VOICE, "hindi_male") ?: "hindi_male"
        set(value) = prefs.edit().putString(KEY_DEFAULT_VOICE, value).apply()

    var defaultSpeed: Float
        get() = prefs.getFloat(KEY_DEFAULT_SPEED, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_DEFAULT_SPEED, value).apply()

    var defaultPitch: Float
        get() = prefs.getFloat(KEY_DEFAULT_PITCH, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_DEFAULT_PITCH, value).apply()

    var autoSave: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SAVE, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_SAVE, value).apply()

    var creatorModeActive: Boolean
        get() = prefs.getBoolean(KEY_CREATOR_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_CREATOR_MODE, value).apply()

    var themeVariant: String
        get() = prefs.getString(KEY_THEME_VARIANT, "CYBER_DARK") ?: "CYBER_DARK"
        set(value) = prefs.edit().putString(KEY_THEME_VARIANT, value).apply()

    companion object {
        private const val KEY_DEFAULT_VOICE = "default_voice_id"
        private const val KEY_DEFAULT_SPEED = "default_speed"
        private const val KEY_DEFAULT_PITCH = "default_pitch"
        private const val KEY_AUTO_SAVE = "auto_save"
        private const val KEY_CREATOR_MODE = "creator_mode"
        private const val KEY_THEME_VARIANT = "theme_variant"
    }
}
