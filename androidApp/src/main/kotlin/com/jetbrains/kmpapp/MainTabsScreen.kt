package com.jetbrains.kmpapp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jetbrains.kmpapp.core.navigation.AppNavigator
import com.jetbrains.kmpapp.core.navigation.AppRoute
import com.jetbrains.kmpapp.core.navigation.HomeTab
import com.jetbrains.kmpapp.core.navigation.ProfileTab
import com.jetbrains.kmpapp.core.navigation.SearchTab
import com.jetbrains.kmpapp.core.navigation.SystemTab
import com.jetbrains.kmpapp.core.navigation.TOP_LEVEL_DESTINATIONS
import com.jetbrains.kmpapp.core.navigation.WxTab
import com.jetbrains.kmpapp.feature.home.HomeScreen
import com.jetbrains.kmpapp.feature.profile.ProfileScreen
import com.jetbrains.kmpapp.feature.profile.navigation.CollectionKey
import com.jetbrains.kmpapp.feature.profile.navigation.LoginKey
import com.jetbrains.kmpapp.feature.search.SearchScreen
import com.jetbrains.kmpapp.feature.system.SystemScreen
import com.jetbrains.kmpapp.feature.web.navigation.WebKey
import com.jetbrains.kmpapp.feature.wx.WxScreen

// 主 Tab 容器：用 when 切换，只有当前页面活跃
// ViewModel 在 Koin 中是 factory，但通过 rememberSaveable 保存 Tab 状态
// 切回来时 ViewModel 重新创建，但数据请求很快（本地有缓存的话）
private const val HOME_TAB_INDEX = 0

@Composable
fun MainTabsScreen(navigator: AppNavigator) {
    val destinations = TOP_LEVEL_DESTINATIONS
    var selectedIndex by rememberSaveable { mutableIntStateOf(HOME_TAB_INDEX) }

    // 非首页 Tab 按返回先回首页
    BackHandler(enabled = selectedIndex != HOME_TAB_INDEX) {
        selectedIndex = HOME_TAB_INDEX
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                if (selectedIndex == index) tab.selectedIcon else tab.icon,
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
        ) {
            MainTabContent(
                route = destinations[selectedIndex].route,
                navigator = navigator,
            )
        }
    }
}

@Composable
private fun MainTabContent(route: AppRoute, navigator: AppNavigator) {
    when (route) {
        HomeTab -> HomeScreen(onArticleClick = { url, title -> navigator.navigate(WebKey(url, title)) })
        SystemTab -> SystemScreen(onArticleClick = { url, title -> navigator.navigate(WebKey(url, title)) })
        WxTab -> WxScreen(onArticleClick = { url, title -> navigator.navigate(WebKey(url, title)) })
        SearchTab -> SearchScreen(onArticleClick = { url, title -> navigator.navigate(WebKey(url, title)) })
        ProfileTab -> ProfileScreen(
            onLogin = { navigator.navigate(LoginKey) },
            onCollection = { navigator.navigate(CollectionKey) },
        )
        else -> error("Route $route is not a main tab route.")
    }
}
