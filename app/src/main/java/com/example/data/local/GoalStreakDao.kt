package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalStreakDao {
    @Query("SELECT * FROM goal_streaks WHERE id = 1 LIMIT 1")
    fun observeStreak(): Flow<GoalStreakCounter?>

    @Query("SELECT * FROM goal_streaks WHERE id = 1 LIMIT 1")
    suspend fun getStreak(): GoalStreakCounter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(streak: GoalStreakCounter)

    @Query("DELETE FROM goal_streaks")
    suspend fun deleteAll()
}
