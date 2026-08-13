package com.jetbrains.kmpapp.feature.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.kmpapp.ui.components.SectionCard

@Composable
fun SettingsScreen() {
    LazyColumn(
        modifier = Modifier.statusBarsPadding(),
        contentPadding = PaddingValues(16.dp),
    ) {
        item {
            SectionCard(title = "外观", modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                ListItem(
                    headlineContent = { Text("主题") },
                    supportingContent = { Text("跟随系统（亮/暗自动切换）") },
                )
            }
        }
        item {
            SectionCard(title = "关于", modifier = Modifier.fillMaxWidth()) {
                ListItem(headlineContent = { Text("数据来源") }, supportingContent = { Text("PokeAPI · pokeapi.co") })
                ListItem(headlineContent = { Text("技术栈") }, supportingContent = { Text("KMP · Compose · Navigation 3 · Ktorfit · Coil3") })
                ListItem(headlineContent = { Text("图片素材") }, supportingContent = { Text("PokeAPI Sprites") })
            }
        }
    }
}
