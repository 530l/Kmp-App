package com.jetbrains.kmpapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * 精灵球轮廓水印：卡片和详情 header 放一个半透明精灵球当装饰，
 * 用 Canvas 画，不引图片资源。
 */
@Composable
fun PokeballBackground(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    alpha: Float = 0.15f,
) {
    Canvas(modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f * 0.9f
        val stroke = r * 0.13f
        val col = color.copy(alpha = alpha)
        // 外圈
        drawCircle(col, radius = r, center = c, style = Stroke(width = stroke))
        // 中间横线
        drawLine(col, Offset(c.x - r, c.y), Offset(c.x + r, c.y), strokeWidth = stroke)
        // 中圈
        drawCircle(col, radius = r * 0.3f, center = c, style = Stroke(width = stroke))
        // 中心点
        drawCircle(Color.White.copy(alpha = alpha * 0.6f), radius = r * 0.16f, center = c)
    }
}
