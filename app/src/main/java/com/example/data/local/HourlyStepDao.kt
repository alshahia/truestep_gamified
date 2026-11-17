package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HourlyStepDao {
    @Query("SELECT * FROM hourly_steps WHERE date = :date ORDER BY hourOfDay ASC")
    fun observeHourlyForDate(date: String): Flow<List<HourlyStepRecord>>

    @Query("SELECT * FROM hourly_steps WHERE date = :date ORDER BY hourOfDay ASC")
    suspend fun getHourlyForDate(date: String): List<HourlyStepRecord>

    @Query("SELECT * FROM hourly_steps WHERE date = :date AND hourOfDay = :hour LIMIT 1")
    suspend fun getHourlyRecord(date: String, hour: Int): HourlyStepRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: HourlyStepRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<HourlyStepRecord>)

    @Query("DELETE FROM hourly_steps")
    suspend fun deleteAll()
}
