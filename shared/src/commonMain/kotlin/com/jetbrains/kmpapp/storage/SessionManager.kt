package com.jetbrains.kmpapp.storage

import com.jetbrains.kmpapp.data.model.LoginData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 登录态管理：expect/actual，Android 用 SharedPreferences，iOS 用 UserDefaults
// 双端共用同一个接口，登录状态通过 StateFlow 暴露给 ViewModel 观察
expect class SessionManager() {

    // 登录状态变化流，UI 订阅它即可感知登录/退出
    val loginState: StateFlow<LoginData?>

    // 保存登录信息
    fun saveLogin(data: LoginData)

    // 当前是否已登录
    fun isLoggedIn(): Boolean

    // 获取当前用户名
    fun getUsername(): String

    // 清除登录态（退出登录）
    fun clear()
}

// 帮 ViewModel 构造默认 StateFlow 的便捷工具
fun emptyLoginState(): MutableStateFlow<LoginData?> = MutableStateFlow(null)
