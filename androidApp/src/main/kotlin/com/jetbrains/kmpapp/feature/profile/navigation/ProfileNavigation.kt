package com.jetbrains.kmpapp.feature.profile.navigation

import com.jetbrains.kmpapp.core.navigation.AppRoute
import kotlinx.serialization.Serializable

// 登录页路由
@Serializable
data object LoginKey : AppRoute

// 收藏列表路由
@Serializable
data object CollectionKey : AppRoute
