package com.datadock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.datadock.app.ui.components.FloatingNavBar
import com.datadock.app.ui.theme.PureBlack
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.datadock.app.ui.theme.NeonGreen

@Composable
fun MainScreen() {
    // This state remembers which tab is currently selected (0 to 3)
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        // This makes the entire app background change between black and light gray!
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(NeonGreen) // Restores the solid brand color
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icons = listOf(Icons.Default.Home, Icons.Default.History, Icons.Default.CalendarMonth, Icons.Default.Settings)

                    icons.forEachIndexed { index, icon ->
                        val isSelected = selectedTabIndex == index
                        val iconTint = if (isSelected) NeonGreen else Color(0xFF0F3D1C)

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF06180D) else Color.Transparent)
                                .clickable { selectedTabIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // This Box acts as the container for the actual screens
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PureBlack),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder text for now; we will replace these with the real UI later
            when (selectedTabIndex) {
                0 -> DashboardScreen()
                1 -> HistoryScreen()
                2 -> ScheduleScreen()
                3 -> SettingsScreen()
            }
        }
    }
}