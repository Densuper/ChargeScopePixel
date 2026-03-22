package com.chargescopixel.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun LineChart(
    points: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.08f))
    ) {
        if (points.isEmpty()) return@Canvas

        val maxValue = points.maxOrNull() ?: return@Canvas
        val minValue = points.minOrNull() ?: return@Canvas
        val range = max(1f, maxValue - minValue)
        val xStep = if (points.size > 1) size.width / (points.size - 1) else size.width

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
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )

        drawLine(
            color = color.copy(alpha = 0.3f),
            start = Offset(0f, size.height - 6f),
            end = Offset(size.width, size.height - 6f),
            strokeWidth = 2f
        )
    }
}
