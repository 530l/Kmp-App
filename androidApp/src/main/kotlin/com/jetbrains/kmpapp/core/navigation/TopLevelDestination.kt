package com.jetbrains.kmpapp.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

// 底部 Tab 标识（仅用于 MainTabsScreen 内部分发，不注册到 NavDisplay）
@Serializable data object HomeTab : AppRoute
@Serializable data object SystemTab : AppRoute
@Serializable data object WxTab : AppRoute
@Serializable data object SearchTab : AppRoute
@Serializable data object ProfileTab : AppRoute

// 底部导航栏 UI 配置
data class TopLevelDestination(val route: AppRoute, val label: String, val selectedIcon: ImageVector, val icon: ImageVector)

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(HomeTab, "首页", Icons.Filled.Home, Icons.Outlined.Home),
    TopLevelDestination(SystemTab, "体系", Icons.Filled.Star, Icons.Outlined.Star),
    TopLevelDestination(WxTab, "公众号", Icons.Filled.Email, Icons.Outlined.Email),
    TopLevelDestination(SearchTab, "搜索", Icons.Filled.Search, Icons.Outlined.Search),
    TopLevelDestination(ProfileTab, "我的", Icons.Filled.Person, Icons.Outlined.Person),
)
