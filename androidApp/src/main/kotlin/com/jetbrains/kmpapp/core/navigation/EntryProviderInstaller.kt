package com.jetbrains.kmpapp.core.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

// Feature 模块提供此扩展函数，向 root entryProvider 注册路由
// 显式传递 AppNavigator 使跳转行为在调用点可追溯
// 用扩展函数而非 typealias，IDE 能正确解析 entry<T>() 方法
interface AppEntryInstaller {
    fun EntryProviderScope<NavKey>.install(navigator: AppNavigator)
}
