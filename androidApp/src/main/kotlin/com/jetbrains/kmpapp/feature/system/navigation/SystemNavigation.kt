package com.jetbrains.kmpapp.feature.system.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jetbrains.kmpapp.feature.system.SystemScreen
import kotlinx.serialization.Serializable

// 体系路由 key
@Serializable
data object SystemKey : NavKey

fun EntryProviderScope<NavKey>.systemEntries(
    onArticleClick: (String, String) -> Unit,
) {
    entry<SystemKey> {
        SystemScreen(onArticleClick = onArticleClick)
    }
}
