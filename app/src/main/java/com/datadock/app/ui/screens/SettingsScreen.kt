package com.datadock.app.ui.screens

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.datadock.app.R
import com.datadock.app.data.worker.DailyDataWorker
import com.datadock.app.ui.theme.NeonGreen
import com.datadock.app.utils.PreferencesManager
import java.util.concurrent.TimeUnit

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val workManager = remember { WorkManager.getInstance(context) }

    var isLoggingEnabled by remember { mutableStateOf(prefs.isDataLoggingEnabled()) }
    var isDarkMode by remember { mutableStateOf(prefs.isDarkModeEnabled()) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Use MaterialTheme colors so they automatically switch between Light and Dark mode
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground
    val subTextColor = if (isDarkMode) Color.Gray else Color.DarkGray

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // Adapts to theme
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "Data", color = textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(text = "Dock", color = NeonGreen, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Theme Toggle
        SettingsRow(
            icon = Icons.Default.DarkMode,
            title = stringResource(id = R.string.settings_theme_title),
            subtitle = if (isDarkMode) stringResource(R.string.settings_theme_sub_dark) else stringResource(R.string.settings_theme_sub_light),
            hasToggle = true,
            isChecked = isDarkMode,
            surfaceColor = surfaceColor,
            textColor = textColor,
            subTextColor = subTextColor,
            onToggle = {
                isDarkMode = it
                prefs.setDarkModeEnabled(it)
                (context as? Activity)?.recreate() // Instantly redraws the whole app!
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Language Button
        SettingsRow(
            icon = Icons.Default.Language,
            title = stringResource(id = R.string.settings_lang_title),
            subtitle = stringResource(id = R.string.settings_lang_sub),
            hasToggle = false,
            isChecked = false,
            surfaceColor = surfaceColor,
            textColor = textColor,
            subTextColor = subTextColor,
            onToggle = { showLanguageDialog = true }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Save Results Toggle
        SettingsRow(
            icon = Icons.Default.Update,
            title = stringResource(id = R.string.settings_save_title),
            subtitle = stringResource(id = R.string.settings_save_sub),
            hasToggle = true,
            isChecked = isLoggingEnabled,
            surfaceColor = surfaceColor,
            textColor = textColor,
            subTextColor = subTextColor,
            onToggle = { enabled ->
                isLoggingEnabled = enabled
                prefs.setDataLoggingEnabled(enabled)
                if (enabled) {
                    val workRequest = PeriodicWorkRequestBuilder<DailyDataWorker>(12, TimeUnit.HOURS).build()
                    workManager.enqueueUniquePeriodicWork("DailyDataLog", ExistingPeriodicWorkPolicy.KEEP, workRequest)
                } else {
                    workManager.cancelUniqueWork("DailyDataLog")
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Banner Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, NeonGreen, RoundedCornerShape(16.dp))
                .background(Color(0xFF06180D)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Data", color = Color.Gray, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = "Dock", color = Color(0xFF4DD0E1), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Real-time Data\nMonitoring", color = Color.White, fontSize = 12.sp)
            }
        }
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(text = "Select Language", color = textColor) },
            containerColor = surfaceColor,
            text = {
                Column {
                    LanguageOption("English", "en", prefs, context) { showLanguageDialog = false }
                    Spacer(modifier = Modifier.height(8.dp))
                    LanguageOption("සිංහල", "si", prefs, context) { showLanguageDialog = false }
                    Spacer(modifier = Modifier.height(8.dp))
                    LanguageOption("தமிழ்", "ta", prefs, context) { showLanguageDialog = false }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Cancel", color = NeonGreen) }
            }
        )
    }
}

@Composable
fun LanguageOption(
    label: String,
    langCode: String,
    prefs: PreferencesManager,
    context: android.content.Context,
    onDismiss: () -> Unit
) {
    Text(
        text = label,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // 1. Save the choice
                prefs.setLanguage(langCode)

                // 2. Force Android to change the locale
                val locale = java.util.Locale(langCode)
                java.util.Locale.setDefault(locale)
                val resources = context.resources
                val configuration = resources.configuration
                configuration.setLocale(locale)
                resources.updateConfiguration(configuration, resources.displayMetrics)

                // 3. Close dialog and reload screen
                onDismiss()
                (context as? Activity)?.recreate()
            }
            .padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    hasToggle: Boolean,
    isChecked: Boolean,
    surfaceColor: Color,
    textColor: Color,
    subTextColor: Color,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .clickable { if (!hasToggle) onToggle(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = subTextColor, fontSize = 10.sp)
            }
        }
        if (hasToggle) {
            Switch(
                checked = isChecked,
                onCheckedChange = { onToggle(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NeonGreen,
                )
            )
        } else {
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = textColor)
        }
    }
}