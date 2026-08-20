package com.datadock.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_usage_table")
data class DailyUsageEntity(
    @PrimaryKey
    val dateString: String, // Format: "YYYY-MM-DD" acts as the unique ID
    val wifiBytesTotal: Long,
    val mobileBytesTotal: Long
)