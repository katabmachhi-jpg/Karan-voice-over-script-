package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_history")
data class VoiceHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val snippet: String,
    val fullText: String,
    val voiceId: String,
    val voiceName: String,
    val languageTag: String,
    val filePath: String,
    val fileSizeBytes: Long = 0,
    val durationMs: Long = 0,
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val isCreatorMode: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
