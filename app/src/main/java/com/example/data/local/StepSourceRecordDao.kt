package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StepSourceRecordDao {
    @Query("SELECT * FROM step_source_records WHERE date = :date ORDER BY startTime ASC")
    fun observeRecordsForDate(date: String): Flow<List<StepSourceRecord>>

    @Query("SELECT * FROM step_source_records WHERE date = :date ORDER BY startTime ASC")
    suspend fun getRecordsForDate(date: String): List<StepSourceRecord>

    @Query("SELECT COUNT(*) FROM step_source_records WHERE isOverlapping = 1")
    suspend fun getOverlappingCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<StepSourceRecord>)

    @Query("DELETE FROM step_source_records WHERE endTime < :cutoffTimestamp")
    suspend fun pruneRecordsOlderThan(cutoffTimestamp: Long): Int

    @Query("DELETE FROM step_source_records WHERE date = :date")
    suspend fun deleteForDate(date: String)

    @Query("DELETE FROM step_source_records")
    suspend fun deleteAll()
}
