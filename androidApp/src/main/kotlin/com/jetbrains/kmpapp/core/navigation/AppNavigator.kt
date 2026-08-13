package com.jetbrains.kmpapp.core.navigation

import androidx.compose.runtime.snapshots.Snapshot
import androidx.navigation3.runtime.NavKey

// App 级导航门面，封装对 root backStack 的操作
// 栈底固定 MainTabsRoute；底部 Tab 切换由 MainTabsScreen 内部管理，不经过此类
class AppNavigator internal constructor(
    private val backStack: MutableList<NavKey>,
) {
    // 入栈，栈顶重复时忽略（防抖）
    fun navigate(route: AppRoute) {
        if (backStack.lastOrNull() == route) return
        backStack.add(route)
    }

    // 弹栈，返回 false 表示已到栈底，调用方应退出
    fun back(): Boolean {
        if (backStack.size <= 1) return false
        backStack.removeAt(backStack.lastIndex)
        return true
    }

    // 原子替换栈顶
    fun replaceTop(route: AppRoute) {
        Snapshot.withMutableSnapshot {
            if (backStack.isNotEmpty()) backStack.removeAt(backStack.lastIndex)
            backStack.add(route)
        }
    }

    // 弹回满足 predicate 的锚点
    fun popUpTo(inclusive: Boolean = false, predicate: (NavKey) -> Boolean): Boolean {
        val anchorIndex = backStack.indexOfLast(predicate)
        if (anchorIndex < 0) return false
        val removeFromIndex = if (inclusive) anchorIndex else anchorIndex + 1
        if (removeFromIndex >= backStack.size) return false
        for (index in backStack.lastIndex downTo removeFromIndex) {
            backStack.removeAt(index)
        }
        return true
    }

    val snapshot: List<NavKey> get() = backStack.toList()
}
