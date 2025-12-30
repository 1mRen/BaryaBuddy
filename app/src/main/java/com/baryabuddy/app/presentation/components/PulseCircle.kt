package com.baryabuddy.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baryabuddy.app.domain.model.SafeSpendStatus
import com.baryabuddy.app.ui.theme.Green
import com.baryabuddy.app.ui.theme.Red
import com.baryabuddy.app.ui.theme.Yellow

@Composable
fun PulseCircle(
    dailySafeSpend: Double,
    status: SafeSpendStatus,
    currency: String,
    modifier: Modifier = Modifier
) {
    val statusColor = when (status) {
        SafeSpendStatus.GREEN -> Green
        SafeSpendStatus.YELLOW -> Yellow
        SafeSpendStatus.RED -> Red
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = 20.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Draw the circle
            drawCircle(
                color = statusColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Center text with DSS amount
        Text(
            text = "$currency${String.format("%.2f", dailySafeSpend)}",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

