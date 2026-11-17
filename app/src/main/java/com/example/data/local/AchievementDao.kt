package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement_unlocks")
    fun observeAllUnlocks(): Flow<List<AchievementUnlock>>

    @Query("SELECT * FROM achievement_unlocks")
    suspend fun getAllUnlocks(): List<AchievementUnlock>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlock(unlock: AchievementUnlock)

    @Query("DELETE FROM achievement_unlocks")
    suspend fun deleteAll()
}
