package com.jetbrains.kmpapp.storage

import android.content.Context
import com.jetbrains.kmpapp.data.model.LoginData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Android 端用 SharedPreferences 持久化登录态
// Context 通过 Koin 注入，构造时从本地读取已有登录信息
actual class SessionManager actual constructor() {

    private val prefs = appContext.getSharedPreferences("wan_session", Context.MODE_PRIVATE)

    private val _loginState = MutableStateFlow<LoginData?>(loadFromLocal())
    actual val loginState: StateFlow<LoginData?> = _loginState.asStateFlow()

    actual fun saveLogin(data: LoginData) {
        prefs.edit()
            .putString("username", data.username)
            .putString("password", data.password)
            .putBoolean("logged_in", true)
            .apply()
        _loginState.value = data
    }

    actual fun isLoggedIn(): Boolean = prefs.getBoolean("logged_in", false)

    actual fun getUsername(): String = prefs.getString("username", "") ?: ""

    actual fun clear() {
        prefs.edit().clear().apply()
        _loginState.value = null
    }

    private fun loadFromLocal(): LoginData? {
        if (!prefs.getBoolean("logged_in", false)) return null
        return LoginData(
            username = prefs.getString("username", "") ?: "",
            password = prefs.getString("password", "") ?: "",
        )
    }

    companion object {
        // 由 Application 在 Koin 初始化前设置，供 SessionManager 获取
        lateinit var appContext: Context
    }
}
