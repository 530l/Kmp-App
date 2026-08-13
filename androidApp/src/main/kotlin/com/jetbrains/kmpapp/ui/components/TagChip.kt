package com.jetbrains.kmpapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 小标签胶囊：实心或描边两种风格
@Composable
fun TagChip(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    outlined: Boolean = false,
) {
    val shape = RoundedCornerShape(4.dp)
    if (outlined) {
        androidx.compose.material3.Surface(
            shape = shape,
            border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
            color = Color.Transparent,
        ) {
            Text(
                text,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                maxLines = 1,
            )
        }
    } else {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            maxLines = 1,
            modifier = modifier
                .clip(shape)
                .background(color)
                .padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}
