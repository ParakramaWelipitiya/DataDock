package com.datadock.app.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.datadock.app.data.NetworkMonitor
import com.datadock.app.data.local.AppDatabase
import com.datadock.app.data.local.DailyUsageEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyDataWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Initialize our tools
            val networkMonitor = NetworkMonitor(applicationContext)
            val database = AppDatabase.getDatabase(applicationContext)

            // 2. Fetch the exact byte counts from the Android system
            val wifiData = networkMonitor.getTodayWifiUsage()
            val mobileData = networkMonitor.getTodayMobileUsage()

            // 3. Get today's date formatted as "YYYY-MM-DD"
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayString = dateFormat.format(Date())

            // 4. Create the database record
            val entity = DailyUsageEntity(
                dateString = todayString,
                wifiBytesTotal = wifiData.totalBytes,
                mobileBytesTotal = mobileData.totalBytes
            )

            // 5. Save it to Room
            database.usageDao().insertUsage(entity)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}