package com.datadock.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageDao {
    // Inserts today's data. If the date already exists, it updates it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(usage: DailyUsageEntity)

    // Gets the most recent 7 days for the Dashboard Bar Chart
    @Query("SELECT * FROM daily_usage_table ORDER BY dateString DESC LIMIT 7")
    fun getLastSevenDays(): Flow<List<DailyUsageEntity>>

    // Gets everything for the History Screen
    @Query("SELECT * FROM daily_usage_table ORDER BY dateString DESC")
    fun getAllHistory(): Flow<List<DailyUsageEntity>>
}