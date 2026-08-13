package com.jetbrains.kmpapp.data.repository

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.LoginData
import com.jetbrains.kmpapp.data.remote.WanAndroidApi
import com.jetbrains.kmpapp.storage.SessionManager

// 账户仓库：登录/注册/收藏，操作完会更新 SessionManager
class AccountRepository(
    private val api: WanAndroidApi,
    private val sessionManager: SessionManager,
) {

    // 登录，成功后保存登录态
    suspend fun login(username: String, password: String): ApiResult<LoginData> {
        val result = api.login(username, password)
        if (result.isSuccess && result.data != null) {
            sessionManager.saveLogin(result.data)
        }
        return result
    }

    // 注册，成功后自动登录态
    suspend fun register(
        username: String,
        password: String,
        repassword: String,
    ): ApiResult<LoginData> {
        val result = api.register(username, password, repassword)
        if (result.isSuccess && result.data != null) {
            sessionManager.saveLogin(result.data)
        }
        return result
    }

    // 退出登录，清空本地登录态
    fun logout() {
        sessionManager.clear()
    }

    // 收藏文章
    suspend fun collectArticle(id: Int): ApiResult<String> =
        api.collectArticle(id)

    // 取消收藏
    suspend fun uncollectArticle(id: Int): ApiResult<String> =
        api.uncollectArticle(id)

    // 是否已登录
    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    // 获取当前登录用户名
    fun getUsername(): String = sessionManager.getUsername()
}
