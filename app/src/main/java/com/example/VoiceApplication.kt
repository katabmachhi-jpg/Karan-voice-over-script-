package com.example

import android.app.Application
import com.example.data.local.VoiceDatabase
import com.example.data.preferences.AppSettings
import com.example.engine.AiScriptGenerator
import com.example.engine.TtsSynthesizer
import com.example.player.AudioPlayerManager

class VoiceApplication : Application() {

    lateinit var database: VoiceDatabase
        private set

    lateinit var settings: AppSettings
        private set

    lateinit var ttsSynthesizer: TtsSynthesizer
        private set

    lateinit var audioPlayerManager: AudioPlayerManager
        private set

    lateinit var aiScriptGenerator: AiScriptGenerator
        private set

    override fun onCreate() {
        super.onCreate()
        database = VoiceDatabase.getInstance(this)
        settings = AppSettings(this)
        ttsSynthesizer = TtsSynthesizer(this)
        audioPlayerManager = AudioPlayerManager(this)
        aiScriptGenerator = AiScriptGenerator()
    }

    override fun onTerminate() {
        super.onTerminate()
        ttsSynthesizer.shutdown()
        audioPlayerManager.release()
    }
}
