package com.jetbrains.kmpapp.data.model

import kotlinx.serialization.Serializable

// 热搜词
@Serializable
data class HotKey(
    val id: Int,
    val name: String = "",
    val link: String = "",
    val order: Int = 0,
    val visible: Int = 1,
)
