package com.datadock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.datadock.app.R
import com.datadock.app.ui.theme.NeonGreen
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleScreen() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedResetDay by remember { mutableIntStateOf(17) }

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value
    val emptyDaysBeforeStart = firstDayOfWeek - 1
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Adapts to theme
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 100.dp)
    ) {
        // App Logo
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "Data", color = MaterialTheme.colorScheme.onBackground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(text = "Dock", color = NeonGreen, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Translated Titles
        Text(text = stringResource(id = R.string.schedule_title), color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.schedule_sub), color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = currentMonth.format(monthFormatter),
            color = Color(0xFF1B5E20),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Days of week header
        val daysOfWeek = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            daysOfWeek.forEachIndexed { index, day ->
                val color = if (index >= 5) Color(0xFF1E88E5) else NeonGreen
                Text(text = day, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(emptyDaysBeforeStart) { Spacer(modifier = Modifier.size(36.dp)) }

            items(daysInMonth) { dayIndex ->
                val day = dayIndex + 1
                val isSelected = day == selectedResetDay
                val dayOfWeek = (emptyDaysBeforeStart + dayIndex) % 7

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { selectedResetDay = day },
                    contentAlignment = Alignment.Center
                ) {
                    // Adapt text color based on selection and theme
                    val color = if (isSelected) MaterialTheme.colorScheme.onBackground
                    else if (dayOfWeek == 5 || dayOfWeek == 6) Color(0xFF1E88E5)
                    else NeonGreen

                    Text(text = day.toString(), color = color, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface) // Adapts to theme
                    .clickable { currentMonth = currentMonth.minusMonths(1) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.btn_previous), color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0F3D1C)) // Keeps brand dark green
                    .clickable { currentMonth = currentMonth.plusMonths(1) }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(id = R.string.btn_next), color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}