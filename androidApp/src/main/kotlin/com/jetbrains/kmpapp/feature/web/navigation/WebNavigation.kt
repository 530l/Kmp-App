package com.jetbrains.kmpapp.feature.web.navigation

import com.jetbrains.kmpapp.core.navigation.AppRoute
import kotlinx.serialization.Serializable

// WebView 路由 key，带 url 和 title
@Serializable
data class WebKey(
    val url: String,
    val title: String = "",
) : AppRoute
