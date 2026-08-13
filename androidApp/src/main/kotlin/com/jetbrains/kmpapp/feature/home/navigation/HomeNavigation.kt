package com.jetbrains.kmpapp.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jetbrains.kmpapp.feature.home.HomeScreen
import kotlinx.serialization.Serializable

// 首页路由 key
@Serializable
data object HomeKey : NavKey

// 首页 entryProvider：接收导航回调，点击文章跳 WebView
fun EntryProviderScope<NavKey>.homeEntries(
    onArticleClick: (String, String) -> Unit, // (url, title) -> 跳 WebView
) {
    entry<HomeKey> {
        HomeScreen(onArticleClick = onArticleClick)
    }
}
