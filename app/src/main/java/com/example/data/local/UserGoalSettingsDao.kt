package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserGoalSettingsDao {
    @Query("SELECT * FROM user_goal_settings WHERE id = 1 LIMIT 1")
    fun observeSettings(): Flow<UserGoalSettings?>

    @Query("SELECT * FROM user_goal_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): UserGoalSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: UserGoalSettings)

    @Query("DELETE FROM user_goal_settings")
    suspend fun resetToDefault()
}
