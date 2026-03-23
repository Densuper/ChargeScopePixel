package com.chargescopixel.app.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.max

object WidgetSparklineRenderer {
    fun render(
        points: List<Float>,
        width: Int = 360,
        height: Int = 120,
        lineColor: Int = Color.parseColor("#8FD3FF"),
        gridColor: Int = Color.parseColor("#44FFFFFF")
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = gridColor
            strokeWidth = 1f
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = lineColor
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeWidth = 4f
        }
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = lineColor
            style = Paint.Style.FILL
        }

        val pad = 8f
        val graphW = width - pad * 2
        val graphH = height - pad * 2

        repeat(5) { i ->
            val y = pad + (graphH * i / 4f)
            canvas.drawLine(pad, y, width - pad, y, gridPaint)
        }
        repeat(7) { i ->
            val x = pad + (graphW * i / 6f)
            canvas.drawLine(x, pad, x, height - pad, gridPaint)
        }

        if (points.size < 2) return bitmap

        val maxV = points.maxOrNull() ?: return bitmap
        val minV = points.minOrNull() ?: return bitmap
        val range = max(1f, maxV - minV)
        val xStep = graphW / (points.size - 1)

        val path = Path()
        points.forEachIndexed { idx, v ->
            val x = pad + idx * xStep
            val normalized = (v - minV) / range
            val y = height - pad - (normalized * graphH)
            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        canvas.drawPath(path, linePaint)

        val lastX = pad + (points.lastIndex * xStep)
        val lastNorm = (points.last() - minV) / range
        val lastY = height - pad - (lastNorm * graphH)
        canvas.drawCircle(lastX, lastY, 5f, dotPaint)

        return bitmap
    }
}
