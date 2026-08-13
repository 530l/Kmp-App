package com.jetbrains.kmpapp.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope

// 把 SharedTransitionScope 通过 CompositionLocal 往下传，组件按需取，不用每个参数都带
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

/**
 * 列表卡片图 ↔ 详情 header 图 的共享元素转场：两端用同一个 key（pokemon id），
 * 点卡片时图从卡片尺寸放大飞到详情头部。scope 不在（预览/未接入）时原样返回。
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.artworkTransition(id: Int): Modifier {
    val shared = LocalSharedTransitionScope.current ?: return this
    val animated = LocalNavAnimatedContentScope.current ?: return this
    return with(shared) {
        this@artworkTransition.sharedElement(
            rememberSharedContentState(key = "artwork-$id"),
            animatedVisibilityScope = animated,
        )
    }
}
