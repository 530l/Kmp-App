package com.jetbrains.kmpapp.feature.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jetbrains.kmpapp.feature.search.SearchScreen
import kotlinx.serialization.Serializable

// 搜索路由 key
@Serializable
data object SearchKey : NavKey

fun EntryProviderScope<NavKey>.searchEntries(
    onArticleClick: (String, String) -> Unit,
) {
    entry<SearchKey> {
        SearchScreen(onArticleClick = onArticleClick)
    }
}
