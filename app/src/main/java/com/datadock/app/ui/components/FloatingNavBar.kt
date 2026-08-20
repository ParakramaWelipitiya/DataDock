package com.datadock.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.datadock.app.ui.theme.NeonGreen

@Composable
fun FloatingNavBar(
    selectedScreen: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val icons = listOf(
        Icons.Default.Home,         // Dashboard
        Icons.Default.Refresh,      // History
        Icons.Default.DateRange,    // Schedule Limits
        Icons.Default.Settings      // Settings
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(36.dp))
            .background(NeonGreen),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icons.forEachIndexed { index, icon ->
                val isSelected = selectedScreen == index

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        // Add a subtle dark circle behind the actively selected icon
                        .background(if (isSelected) Color.Black.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { onTabSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Nav Icon $index",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}