package com.jetbrains.kmpapp.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.jetbrains.kmpapp.feature.detail.PokemonDetailScreen
import com.jetbrains.kmpapp.feature.pokedex.PokedexScreen
import com.jetbrains.kmpapp.feature.settings.SettingsScreen
import com.jetbrains.kmpapp.feature.types.TypeDetailScreen
import com.jetbrains.kmpapp.feature.types.TypesScreen
import com.jetbrains.kmpapp.ui.components.LocalSharedTransitionScope
import com.jetbrains.kmpapp.ui.theme.KmpTheme
import kotlinx.serialization.Serializable

// 三个顶层 Tab + 两个详情页，统一做成可序列化的导航 key
@Serializable
data object PokedexKey : NavKey

@Serializable
data object TypesKey : NavKey

@Serializable
data object SettingsKey : NavKey

@Serializable
data class PokemonDetailKey(val id: Int) : NavKey

@Serializable
data class TypeDetailKey(val id: Int) : NavKey

private data class TabItem(val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem("图鉴", Icons.Default.Search),
    TabItem("属性", Icons.Default.Star),
    TabItem("设置", Icons.Default.Settings),
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun App() {
    KmpTheme {
        // 每个 Tab 一份独立 backstack，切来切去各自的历史都保留着
        val backStacks = remember {
            listOf(
                mutableStateListOf<NavKey>(PokedexKey),
                mutableStateListOf<NavKey>(TypesKey),
                mutableStateListOf<NavKey>(SettingsKey),
            )
        }
        var currentTab by remember { mutableIntStateOf(0) }
        val currentStack = backStacks[currentTab]

        // SharedTransitionLayout 提供 scope，列表图↔详情图转场靠它 + NavDisplay 自带的 animated scope
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this@SharedTransitionLayout) {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            tabs.forEachIndexed { index, tab ->
                                NavigationBarItem(
                                    selected = currentTab == index,
                                    onClick = { currentTab = index },
                                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                                    label = { Text(tab.label) },
                                )
                            }
                        }
                    },
                ) { padding ->
                    NavDisplay(
                        backStack = currentStack,
                        onBack = { if (currentStack.size > 1) currentStack.removeLastOrNull() },
                        modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                        entryProvider = { key ->
                            when (key) {
                                is PokedexKey -> NavEntry(key) {
                                    PokedexScreen(onPokemonClick = { id -> currentStack.add(PokemonDetailKey(id)) })
                                }
                                is PokemonDetailKey -> NavEntry(key) {
                                    PokemonDetailScreen(pokemonId = key.id, onBack = { currentStack.removeLastOrNull() })
                                }
                                is TypesKey -> NavEntry(key) {
                                    TypesScreen(onTypeClick = { id -> currentStack.add(TypeDetailKey(id)) })
                                }
                                is TypeDetailKey -> NavEntry(key) {
                                    TypeDetailScreen(typeId = key.id, onBack = { currentStack.removeLastOrNull() })
                                }
                                is SettingsKey -> NavEntry(key) { SettingsScreen() }
                                else -> error("未知导航 key: $key")
                            }
                        },
                    )
                }
            }
        }
    }
}
