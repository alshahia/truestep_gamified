package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class StepHistoryAggregate(
    val totalDays: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val maxSteps: Int = 0
)

@Dao
interface StepDao {
    @Query("SELECT * FROM daily_steps WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailySteps?

    @Query("SELECT * FROM daily_steps WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<DailySteps?>

    @Query("SELECT * FROM daily_steps ORDER BY date DESC")
    fun observeAll(): Flow<List<DailySteps>>

    @Query("SELECT * FROM daily_steps ORDER BY date DESC")
    suspend fun getAllList(): List<DailySteps>

    @Query("SELECT * FROM daily_steps ORDER BY date DESC LIMIT 1")
    suspend fun getMostRecent(): DailySteps?

    @Query("SELECT * FROM daily_steps ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentDays(limit: Int): List<DailySteps>

    @Query("SELECT * FROM daily_steps WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getRange(startDate: String, endDate: String): List<DailySteps>

    @Query("SELECT COUNT(*) as totalDays, COALESCE(SUM(cumulativeKm), 0.0) as totalDistanceKm, COALESCE(MAX(totalSteps), 0) as maxSteps FROM daily_steps")
    suspend fun getHistoryAggregate(): StepHistoryAggregate

    @Query("SELECT COUNT(*) FROM daily_steps WHERE totalSteps >= minGoal")
    suspend fun getMetGoalDaysCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(dailySteps: DailySteps)

    @Query("UPDATE daily_steps SET notes = :notes WHERE date = :date")
    suspend fun updateNotes(date: String, notes: String)

    @Query("DELETE FROM daily_steps")
    suspend fun deleteAll()
}
