package com.jetbrains.kmpapp.feature.wx.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jetbrains.kmpapp.feature.wx.WxScreen
import kotlinx.serialization.Serializable

// 公众号路由 key
@Serializable
data object WxKey : NavKey

fun EntryProviderScope<NavKey>.wxEntries(
    onArticleClick: (String, String) -> Unit,
) {
    entry<WxKey> {
        WxScreen(onArticleClick = onArticleClick)
    }
}
