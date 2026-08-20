package com.datadock.app.data

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import java.util.Calendar

// Data class to hold our parsed byte values
data class DailyUsage(val sentBytes: Long, val receivedBytes: Long) {
    val totalBytes: Long get() = sentBytes + receivedBytes
}

class NetworkMonitor(context: Context) {

    private val networkStatsManager =
        context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

    // Calculates today's usage from 12:00 AM to right now
    fun getTodayWifiUsage(): DailyUsage {
        val (startTime, endTime) = getTodayTimeRange()
        return getUsageByType(ConnectivityManager.TYPE_WIFI, startTime, endTime)
    }

    fun getTodayMobileUsage(): DailyUsage {
        val (startTime, endTime) = getTodayTimeRange()
        return getUsageByType(ConnectivityManager.TYPE_MOBILE, startTime, endTime)
    }

    // Queries the system buckets for a specific network type
    private fun getUsageByType(networkType: Int, startTime: Long, endTime: Long): DailyUsage {
        return try {
            val bucket = networkStatsManager.querySummaryForDevice(
                networkType,
                null, // Passing null queries the entire device regardless of subscriber ID
                startTime,
                endTime
            )
            DailyUsage(sentBytes = bucket.txBytes, receivedBytes = bucket.rxBytes)
        } catch (e: Exception) {
            // If permission is missing or an error occurs, safely return 0
            DailyUsage(0L, 0L)
        }
    }

    // Utility to get epoch timestamps for midnight and the current time
    private fun getTodayTimeRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        return Pair(startTime, endTime)
    }

    companion object {
        // Helper to convert raw bytes into a formatted String (e.g., "3.64 GB" or "281 MB")
        fun formatBytes(bytes: Long): String {
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0

            return when {
                gb >= 1.0 -> String.format("%.2f GB", gb)
                mb >= 1.0 -> String.format("%.2f MB", mb)
                kb >= 1.0 -> String.format("%.2f KB", kb)
                else -> "$bytes B"
            }
        }
    }
}