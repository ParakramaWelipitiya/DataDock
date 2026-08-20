package com.datadock.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.datadock.app.ui.theme.DarkGreenGradientEnd
import com.datadock.app.ui.theme.DarkGreenGradientStart

@Composable
fun UsageCard(
    icon: ImageVector,
    iconTint: Color,
    totalUsage: String,
    sent: String,
    received: String,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(DarkGreenGradientStart, DarkGreenGradientEnd)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .border(1.dp, Color(0xFF1B5E20), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = totalUsage, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = sent, color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Sent", color = Color.Gray, fontSize = 10.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = received, color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Received", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }
    }
}