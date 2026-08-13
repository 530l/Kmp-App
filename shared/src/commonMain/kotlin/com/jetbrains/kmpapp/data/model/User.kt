package com.jetbrains.kmpapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 登录返回的用户信息
@Serializable
data class UserInfo(
    val id: Int = 0,
    val username: String = "",
    val nickname: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("icon") val icon: String = "",
    @SerialName("admin") val admin: Boolean = false,
    @SerialName("chapterTops") val chapterTops: List<Int> = emptyList(),
    @SerialName("collectIds") val collectIds: List<Int> = emptyList(),
) {
    // 展示名优先昵称，没有就用用户名
    val displayName: String get() = nickname.ifBlank { username }
}

// 登录/注册接口直接把用户信息包在 data 里
@Serializable
data class LoginData(
    val username: String = "",
    @SerialName("password") val password: String = "",
    @SerialName("token") val token: String? = null,
    @SerialName("userInfo") val userInfo: UserInfo? = null,
)
