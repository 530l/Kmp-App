package com.jetbrains.kmpapp.navigation

import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.jetbrains.kmpapp.feature.home.navigation.HomeKey
import com.jetbrains.kmpapp.feature.home.navigation.homeEntries
import com.jetbrains.kmpapp.feature.profile.navigation.ProfileKey
import com.jetbrains.kmpapp.feature.profile.navigation.profileEntries
import com.jetbrains.kmpapp.feature.search.navigation.SearchKey
import com.jetbrains.kmpapp.feature.search.navigation.searchEntries
import com.jetbrains.kmpapp.feature.system.navigation.SystemKey
import com.jetbrains.kmpapp.feature.system.navigation.systemEntries
import com.jetbrains.kmpapp.feature.web.navigation.WebKey
import com.jetbrains.kmpapp.feature.web.navigation.webEntries
import com.jetbrains.kmpapp.feature.wx.navigation.WxKey
import com.jetbrains.kmpapp.feature.wx.navigation.wxEntries
import com.jetbrains.kmpapp.ui.theme.KmpTheme

// 五个顶层 Tab 的初始 key
private val tabKeys = listOf(HomeKey, SystemKey, WxKey, SearchKey, ProfileKey)

private data class TabItem(val label: String, val selectedIcon: ImageVector, val icon: ImageVector)

// 用 core 包自带的图标，避免引入 material-icons-extended
private val tabs = listOf(
    TabItem("首页", Icons.Filled.Home, Icons.Outlined.Home),
    TabItem("体系", Icons.Filled.Star, Icons.Outlined.Star),
    TabItem("公众号", Icons.Filled.Email, Icons.Outlined.Email),
    TabItem("搜索", Icons.Filled.Search, Icons.Outlined.Search),
    TabItem("我的", Icons.Filled.Person, Icons.Outlined.Person),
)

@Composable
fun App() {
    KmpTheme {
        // 每个 Tab 一份独立 backstack，切来切去各自的历史都保留着
        val backStacks = remember {
            List(tabKeys.size) { index ->
                mutableStateListOf<NavKey>(tabKeys[index])
            }
        }
        var currentTab by remember { mutableIntStateOf(0) }
        val currentStack = backStacks[currentTab]

        Scaffold(
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = currentTab == index,
                            onClick = { currentTab = index },
                            icon = {
                                Icon(
                                    if (currentTab == index) tab.selectedIcon else tab.icon,
                                    contentDescription = tab.label,
                                )
                            },
                            label = { Text(tab.label) },
                        )
                    }
                }
            },
        ) { padding ->
            NavDisplay(
                backStack = currentStack,
                onBack = { if (currentStack.size > 1) currentStack.removeLastOrNull() },
                modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
                // 顶层只做组合：每个 feature 提供自己的 entry 声明
                entryProvider = entryProvider {
                    // 统一的 WebView 入口（所有 Tab 都可能跳文章详情）
                    webEntries(onBack = { if (currentStack.size > 1) currentStack.removeLastOrNull() })

                    homeEntries(onArticleClick = { url, title ->
                        currentStack.add(WebKey(url, title))
                    })
                    systemEntries(onArticleClick = { url, title ->
                        currentStack.add(WebKey(url, title))
                    })
                    wxEntries(onArticleClick = { url, title ->
                        currentStack.add(WebKey(url, title))
                    })
                    searchEntries(onArticleClick = { url, title ->
                        currentStack.add(WebKey(url, title))
                    })
                    profileEntries(
                        onNavigate = { key -> currentStack.add(key) },
                        onArticleClick = { url, title ->
                            currentStack.add(WebKey(url, title))
                        },
                        onLoginSuccess = {
                            // 登录成功后弹出登录页，回到我的页
                            currentStack.removeAll { it != ProfileKey }
                        },
                    )
                },
            )
        }
    }
}
