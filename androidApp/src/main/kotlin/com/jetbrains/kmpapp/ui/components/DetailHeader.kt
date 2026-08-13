package com.jetbrains.kmpapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jetbrains.kmpapp.ui.theme.colorForType

/**
 * 详情页沉浸式 header：主属性色渐变铺到状态栏后，返回键浮左上，
 * 居中大图 + 白色名字 + 属性胶囊（白底彩字，在彩色背景上对比清晰）。
 */
@Composable
fun DetailHeader(
    name: String,
    artworkUrl: String?,
    types: List<String>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    // 用于和列表卡片图做共享元素转场；没有图时（如属性详情）传进来也不会生效
    sharedId: Int? = null,
) {
    val base = colorForType(types.firstOrNull() ?: "normal")
    val gradient = Brush.verticalGradient(listOf(base, lerp(base, Color.White, 0.35f)))
    Box(
        modifier
            .fillMaxWidth()
            .background(gradient),
    ) {
        PokeballBackground(
            Modifier.align(Alignment.TopEnd).size(220.dp),
            alpha = 0.15f,
        )
        Column(Modifier.statusBarsPadding().padding(bottom = 24.dp)) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White,
                )
            }
            if (artworkUrl != null) {
                AsyncImage(
                    model = artworkUrl,
                    contentDescription = name,
                    // 共享元素：key 和列表卡片图一致，进/出详情时图在两端尺寸间过渡
                    modifier = Modifier
                        .size(220.dp)
                        .align(Alignment.CenterHorizontally)
                        .then(if (sharedId != null) Modifier.artworkTransition(sharedId) else Modifier),
                )
            }
            Text(
                text = name.replaceFirstChar { it.uppercase() },
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                types.forEach { TypeChip(type = it, onTint = true) }
            }
        }
    }
}
