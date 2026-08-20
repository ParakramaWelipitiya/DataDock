package com.datadock.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.datadock.app.R
import com.datadock.app.data.NetworkMonitor
import com.datadock.app.data.local.AppDatabase
import com.datadock.app.ui.components.UsageCard
import com.datadock.app.ui.theme.NeonGreen
import com.datadock.app.ui.theme.SoftGreen
import com.datadock.app.utils.PermissionUtils

@Composable
fun DashboardScreen() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val networkMonitor = remember { NetworkMonitor(context) }

    val db = remember { AppDatabase.getDatabase(context).usageDao() }
    val last7DaysData by db.getLastSevenDays().collectAsState(initial = emptyList())

    var hasPermission by remember { mutableStateOf(PermissionUtils.hasUsageStatsPermission(context)) }
    var wifiData by remember { mutableStateOf(networkMonitor.getTodayWifiUsage()) }
    var mobileData by remember { mutableStateOf(networkMonitor.getTodayMobileUsage()) }

    LaunchedEffect(Unit) {
        hasPermission = PermissionUtils.hasUsageStatsPermission(context)
        if (hasPermission) {
            wifiData = networkMonitor.getTodayWifiUsage()
            mobileData = networkMonitor.getTodayMobileUsage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Fixes the background issue!
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 100.dp)
    ) {
        // App Logo
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "Data", color = MaterialTheme.colorScheme.onBackground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(text = "Dock", color = NeonGreen, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.dash_today_usage),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (hasPermission) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                UsageCard(
                    icon = Icons.Default.Phone,
                    iconTint = Color.Red,
                    totalUsage = NetworkMonitor.formatBytes(mobileData.totalBytes),
                    sent = NetworkMonitor.formatBytes(mobileData.sentBytes),
                    received = NetworkMonitor.formatBytes(mobileData.receivedBytes),
                    modifier = Modifier.weight(1f)
                )
                UsageCard(
                    icon = Icons.Default.Wifi,
                    iconTint = NeonGreen,
                    totalUsage = NetworkMonitor.formatBytes(wifiData.totalBytes),
                    sent = NetworkMonitor.formatBytes(wifiData.sentBytes),
                    received = NetworkMonitor.formatBytes(wifiData.receivedBytes),
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Permission Required", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Data Dock needs Usage Access to calculate your daily bandwidth.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))
                Button(
                    onClick = { context.startActivity(PermissionUtils.getUsageSettingsIntent()) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Text("Grant Access", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(id = R.string.dash_overview),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        UsageBarChart(last7DaysData)
    }
}

@Composable
fun UsageBarChart(recentData: List<com.datadock.app.data.local.DailyUsageEntity> = emptyList()) {
    val usageData = MutableList(7) { 0f }
    val days = MutableList(7) { "-" }

    recentData.reversed().forEachIndexed { index, entity ->
        if (index < 7) {
            val totalBytes = entity.mobileBytesTotal + entity.wifiBytesTotal
            val totalGb = (totalBytes / (1024f * 1024f * 1024f))
            usageData[index] = totalGb
            days[index] = entity.dateString.takeLast(5)
        }
    }

    Row(modifier = Modifier.fillMaxWidth().height(220.dp)) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(bottom = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("20GB", "15GB", "10GB", "5GB", "0GB").forEach { label ->
                Text(text = label, color = Color.Gray, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Canvas(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val barWidth = size.width / (usageData.size * 2)
                val spacing = size.width / usageData.size

                drawLine(color = Color.DarkGray, start = Offset(0f, size.height), end = Offset(size.width, size.height), strokeWidth = 2f)

                usageData.forEachIndexed { index, gigabytes ->
                    val barHeightFraction = (gigabytes / 20f).coerceIn(0f, 1f)
                    val barHeight = size.height * barHeightFraction
                    val topLeftX = (index * spacing) + (spacing - barWidth) / 2
                    val topLeftY = size.height - barHeight

                    drawRoundRect(
                        color = SoftGreen,
                        topLeft = Offset(topLeftX, topLeftY),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                days.forEach { day ->
                    Text(text = day, color = MaterialTheme.colorScheme.onBackground, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}