package com.jetbrains.kmpapp.core.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jetbrains.kmpapp.feature.profile.CollectionScreen
import com.jetbrains.kmpapp.feature.profile.LoginScreen
import com.jetbrains.kmpapp.feature.profile.navigation.CollectionKey
import com.jetbrains.kmpapp.feature.profile.navigation.LoginKey
import com.jetbrains.kmpapp.feature.web.WebViewScreen
import com.jetbrains.kmpapp.feature.web.navigation.WebKey

// 所有详情页的 entry 注册（非 Tab 页）
object AppEntryProviders {

    // 返回一个 lambda，在 entryProvider { } DSL 里调用
    fun installAll(): Array<EntryProviderScope<NavKey>.(AppNavigator) -> Unit> = arrayOf(
        { navigator ->
            entry<WebKey> { key ->
                WebViewScreen(
                    url = key.url,
                    title = key.title,
                    onBack = { navigator.back() },
                )
            }
            entry<LoginKey> {
                LoginScreen(
                    onBack = { navigator.popUpTo(inclusive = true) { it is LoginKey } },
                    onSuccess = { navigator.popUpTo(inclusive = true) { it is LoginKey } },
                )
            }
            entry<CollectionKey> {
                CollectionScreen(
                    onBack = { navigator.back() },
                    onArticleClick = { url, title -> navigator.navigate(WebKey(url, title)) },
                )
            }
        },
    )
}
