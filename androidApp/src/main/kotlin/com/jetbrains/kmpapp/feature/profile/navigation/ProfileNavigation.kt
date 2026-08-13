package com.jetbrains.kmpapp.feature.profile.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.jetbrains.kmpapp.feature.profile.CollectionScreen
import com.jetbrains.kmpapp.feature.profile.LoginScreen
import com.jetbrains.kmpapp.feature.profile.ProfileScreen
import kotlinx.serialization.Serializable

// 我的路由 key
@Serializable
data object ProfileKey : NavKey

// 登录页 key
@Serializable
data object LoginKey : NavKey

// 收藏列表 key
@Serializable
data object CollectionKey : NavKey

fun EntryProviderScope<NavKey>.profileEntries(
    onNavigate: (NavKey) -> Unit,
    onArticleClick: (String, String) -> Unit,
    onLoginSuccess: () -> Unit, // 登录成功后回到我的页
) {
    entry<ProfileKey> {
        ProfileScreen(
            onLogin = { onNavigate(LoginKey) },
            onCollection = { onNavigate(CollectionKey) },
        )
    }
    entry<LoginKey> {
        LoginScreen(
            onBack = { onNavigate(ProfileKey) },
            onSuccess = onLoginSuccess,
        )
    }
    entry<CollectionKey> {
        CollectionScreen(onArticleClick = onArticleClick)
    }
}
