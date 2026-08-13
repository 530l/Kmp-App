package com.jetbrains.kmpapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// 标题加粗放大一点，更有图鉴 App 的辨识度；其余沿用 Material3 默认
val AppTypography = Typography(
    titleLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold),
)
