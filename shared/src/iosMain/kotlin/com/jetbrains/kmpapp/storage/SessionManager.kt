package com.jetbrains.kmpapp.storage

import com.jetbrains.kmpapp.data.model.LoginData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

// iOS 端用 NSUserDefaults 持久化登录态
actual class SessionManager actual constructor() {

    private val defaults = NSUserDefaults.standardUserDefaults()

    private val _loginState = MutableStateFlow<LoginData?>(loadFromLocal())
    actual val loginState: StateFlow<LoginData?> = _loginState.asStateFlow()

    actual fun saveLogin(data: LoginData) {
        defaults.setObject(data.username, forKey = "username")
        defaults.setObject(data.password, forKey = "password")
        defaults.setBool(true, forKey = "logged_in")
        defaults.synchronize()
        _loginState.value = data
    }

    actual fun isLoggedIn(): Boolean = defaults.boolForKey("logged_in")

    actual fun getUsername(): String =
        defaults.stringForKey("username") ?: ""

    actual fun clear() {
        defaults.removeObjectForKey("username")
        defaults.removeObjectForKey("password")
        defaults.removeObjectForKey("logged_in")
        defaults.synchronize()
        _loginState.value = null
    }

    private fun loadFromLocal(): LoginData? {
        if (!defaults.boolForKey("logged_in")) return null
        return LoginData(
            username = defaults.stringForKey("username") ?: "",
            password = defaults.stringForKey("password") ?: "",
        )
    }
}
