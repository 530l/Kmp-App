package com.jetbrains.kmpapp.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// 所有路由的基接口。非 sealed，允许 feature 包跨 package 实现
interface AppRoute : NavKey

// Root 栈底常驻路由，承载底部 Tab + HorizontalPager
@Serializable
data object MainTabsRoute : AppRoute
