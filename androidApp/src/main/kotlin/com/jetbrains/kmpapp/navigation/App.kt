package com.jetbrains.kmpapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.jetbrains.kmpapp.MainTabsScreen
import com.jetbrains.kmpapp.core.navigation.AppEntryProviders
import com.jetbrains.kmpapp.core.navigation.AppNavigator
import com.jetbrains.kmpapp.core.navigation.MainTabsRoute
import com.jetbrains.kmpapp.ui.theme.KmpTheme
import androidx.compose.runtime.Composable

@Composable
fun App() {
    KmpTheme {
        // 单条 root backstack，栈底固定 MainTabsRoute
        val rootStack = remember { mutableStateListOf<NavKey>(MainTabsRoute) }
        val navigator = remember(rootStack) { AppNavigator(rootStack) }
        val installers = AppEntryProviders.installAll()

        NavDisplay(
            backStack = rootStack,
            onBack = { navigator.back() },
            modifier = Modifier.fillMaxSize(),
            entryProvider = entryProvider {
                // 栈底：Tab 容器
                entry<MainTabsRoute> {
                    MainTabsScreen(navigator = navigator)
                }
                // 所有 feature 的详情页 entry
                installers.forEach { installer -> installer(navigator) }
            },
        )
    }
}
