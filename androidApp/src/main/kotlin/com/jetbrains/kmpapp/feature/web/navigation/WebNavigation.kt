package com.jetbrains.kmpapp.feature.web.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jetbrains.kmpapp.feature.web.WebViewScreen
import kotlinx.serialization.Serializable

// WebView 路由 key，带 url 和 title
@Serializable
data class WebKey(
    val url: String,
    val title: String = "",
) : NavKey

fun EntryProviderScope<NavKey>.webEntries(
    onBack: () -> Unit,
) {
    entry<WebKey> { key ->
        WebViewScreen(url = key.url, title = key.title, onBack = onBack)
    }
}
