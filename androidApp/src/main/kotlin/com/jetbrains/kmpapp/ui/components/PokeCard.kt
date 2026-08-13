package com.jetbrains.kmpapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jetbrains.kmpapp.data.model.PokemonListItem
import com.jetbrains.kmpapp.ui.theme.colorForType

/**
 * 列表卡片：主属性色渐变背景 + 半透明精灵球水印 + 居中大图 + 白字编号和名字。
 * primaryType 还没预取到时回退主题中性色，加载后自动变属性色。
 */
@Composable
fun PokeCard(
    item: PokemonListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val base = item.primaryType?.let { colorForType(it) }
        ?: MaterialTheme.colorScheme.surfaceVariant
    val gradient = Brush.verticalGradient(listOf(base, lerp(base, Color.White, 0.3f)))
    Box(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .clickable(onClick = onClick),
    ) {
        PokeballBackground(Modifier.align(Alignment.TopEnd).size(110.dp), alpha = 0.15f)
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = item.artworkUrl,
                contentDescription = item.name,
                // 共享元素：和详情 header 的图用同一个 key（pokemon id），点击时放大飞过去
                modifier = Modifier.size(110.dp).artworkTransition(item.id),
            )
            Text(
                "#${item.id.toString().padStart(3, '0')}",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                item.name.replaceFirstChar { it.uppercase() },
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
