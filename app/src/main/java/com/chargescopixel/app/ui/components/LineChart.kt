package com.chargescopixel.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun LineChart(
    points: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
    gridColor: Color = color.copy(alpha = 0.22f)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(color.copy(alpha = 0.16f), color.copy(alpha = 0.04f))
                )
            )
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
    ) {
        if (points.isEmpty()) return@Canvas

        val maxValue = points.maxOrNull() ?: return@Canvas
        val minValue = points.minOrNull() ?: return@Canvas
        val range = max(1f, maxValue - minValue)
        val xStep = if (points.size > 1) size.width / (points.size - 1) else size.width

        // Horizontal grid lines
        val horizontalLines = 4
        repeat(horizontalLines + 1) { i ->
            val y = (size.height - 6f) * (i / horizontalLines.toFloat())
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }

        // Vertical grid lines
        val verticalLines = 6
        repeat(verticalLines + 1) { i ->
            val x = size.width * (i / verticalLines.toFloat())
            drawLine(
                color = gridColor.copy(alpha = 0.8f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
        }

        val path = Path()
        points.forEachIndexed { index, value ->
            val x = index * xStep
            val normalized = (value - minValue) / range
            val y = size.height - (normalized * (size.height - 12f)) - 6f

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        val lastIndex = points.lastIndex
        if (lastIndex >= 0) {
            val lastX = lastIndex * xStep
            val normalized = (points[lastIndex] - minValue) / range
            val lastY = size.height - (normalized * (size.height - 12f)) - 6f
            drawCircle(color = color, radius = 7f, center = Offset(lastX, lastY))
            drawCircle(color = Color.White.copy(alpha = 0.65f), radius = 3f, center = Offset(lastX, lastY))
        }

        drawLine(
            color = color.copy(alpha = 0.3f),
            start = Offset(0f, size.height - 6f),
            end = Offset(size.width, size.height - 6f),
            strokeWidth = 2f
        )
    }
}
