package com.datadock.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.*
import com.datadock.app.data.worker.DailyDataWorker
import com.datadock.app.ui.screens.MainScreen
import com.datadock.app.ui.theme.DataDockTheme
import com.datadock.app.utils.SpeedMonitorService
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    // Permission launcher for Android 13+ Notifications
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startSpeedService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleDailyDataLogging()
        checkAndStartService()

        setContent {
            DataDockTheme {
                MainScreen()
            }
        }
    }

    private fun checkAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                startSpeedService()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startSpeedService()
        }
    }

    private fun startSpeedService() {
        val serviceIntent = Intent(this, SpeedMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun scheduleDailyDataLogging() {
        val constraints = Constraints.Builder().build()
        val workRequest = PeriodicWorkRequestBuilder<DailyDataWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "DailyDataLog",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}