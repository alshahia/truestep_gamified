package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkingSessionDao {
    @Query("SELECT * FROM walking_sessions ORDER BY startTime DESC")
    fun observeAll(): Flow<List<WalkingSession>>

    @Query("SELECT * FROM walking_sessions ORDER BY startTime DESC")
    suspend fun getAll(): List<WalkingSession>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WalkingSession): Long

    @Query("DELETE FROM walking_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    @Query("DELETE FROM walking_sessions")
    suspend fun deleteAll()
}
