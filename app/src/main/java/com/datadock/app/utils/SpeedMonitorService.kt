package com.datadock.app.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.datadock.app.MainActivity
import com.datadock.app.R
import com.datadock.app.data.NetworkMonitor
import kotlinx.coroutines.*

class SpeedMonitorService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private val channelId = "DataDockSpeedChannel"
    private val notificationId = 1

    private var lastRxBytes: Long = 0
    private var lastTxBytes: Long = 0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Baseline for calculating speed
        lastRxBytes = TrafficStats.getTotalRxBytes()
        lastTxBytes = TrafficStats.getTotalTxBytes()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("Calculating...", "Initializing data...")

        // Start the service in the foreground immediately
        startForeground(notificationId, notification)

        // Launch the continuous loop
        startMonitoring()

        return START_STICKY
    }

    private fun startMonitoring() {
        val networkMonitor = NetworkMonitor(applicationContext)

        serviceScope.launch {
            while (isActive) {
                // Calculate live speeds
                val currentRxBytes = TrafficStats.getTotalRxBytes()
                val currentTxBytes = TrafficStats.getTotalTxBytes()

                val rxDiff = currentRxBytes - lastRxBytes
                val txDiff = currentTxBytes - lastTxBytes

                lastRxBytes = currentRxBytes
                lastTxBytes = currentTxBytes

                val downloadSpeed = formatSpeed(rxDiff)
                val uploadSpeed = formatSpeed(txDiff)

                // Get today's total usage
                val wifiData = networkMonitor.getTodayWifiUsage().totalBytes
                val mobileData = networkMonitor.getTodayMobileUsage().totalBytes
                val todayTotal = NetworkMonitor.formatBytes(wifiData + mobileData)

                // Update the notification
                val speedText = "↓ $downloadSpeed  ↑ $uploadSpeed"
                val usageText = "Today's Usage: $todayTotal"

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(notificationId, createNotification(speedText, usageText))

                // Wait exactly 1 second before the next calculation
                delay(1000)
            }
        }
    }

    private fun createNotification(speedText: String, usageText: String): Notification {
        // Tapping the notification opens the app
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(speedText)
            .setContentText(usageText)
            .setSmallIcon(R.drawable.ic_signal) // Your custom signal icon
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true) // Prevents the phone from vibrating every second
            .setOngoing(true) // Cannot be swiped away
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Live Network Speed",
                NotificationManager.IMPORTANCE_LOW // Low importance prevents sound/popups on every update
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun formatSpeed(bytesPerSecond: Long): String {
        val kb = bytesPerSecond / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1.0 -> String.format("%.1f MB/s", mb)
            kb >= 1.0 -> String.format("%.1f KB/s", kb)
            else -> "$bytesPerSecond B/s"
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Clean up the loop to prevent battery drain if stopped
    }
}