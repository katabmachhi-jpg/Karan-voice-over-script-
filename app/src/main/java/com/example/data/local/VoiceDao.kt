package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceDao {
    @Query("SELECT * FROM voice_history ORDER BY createdAt DESC")
    fun getAllHistory(): Flow<List<VoiceHistoryEntity>>

    @Query("SELECT * FROM voice_history ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<VoiceHistoryEntity>>

    @Query("SELECT * FROM voice_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): VoiceHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoice(item: VoiceHistoryEntity): Long

    @Delete
    suspend fun deleteVoice(item: VoiceHistoryEntity)

    @Query("DELETE FROM voice_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM voice_history")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM voice_history")
    fun getCount(): Flow<Int>
}
