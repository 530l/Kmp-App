package com.jetbrains.kmpapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetbrains.kmpapp.ui.theme.colorForType

/**
 * 属性胶囊。在属性色背景上（如彩色 header / 卡片）用白底彩字对比清晰，
 * 在中性背景上用彩底白字。两端样式统一。
 */
@Composable
fun TypeChip(
    type: String,
    modifier: Modifier = Modifier,
    onTint: Boolean = false,
) {
    val typeColor = colorForType(type)
    val bg = if (onTint) Color.White.copy(alpha = 0.92f) else typeColor
    val fg = if (onTint) typeColor else Color.White
    Text(
        text = type.replaceFirstChar { it.uppercase() },
        color = fg,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    )
}
