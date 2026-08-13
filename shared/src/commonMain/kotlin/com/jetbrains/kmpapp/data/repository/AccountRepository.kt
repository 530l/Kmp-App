package com.jetbrains.kmpapp.data.repository

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.LoginData
import com.jetbrains.kmpapp.data.remote.WanAndroidApi
import com.jetbrains.kmpapp.data.remote.toApiResult
import com.jetbrains.kmpapp.storage.SessionManager
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

// 账户仓库：登录/注册/收藏，操作完会更新 SessionManager
class AccountRepository(
    private val api: WanAndroidApi,
    private val json: Json,
    private val sessionManager: SessionManager,
) {

    // 登录，成功后保存登录态
    suspend fun login(username: String, password: String): ApiResult<LoginData> {
        val result = api.login(username, password).toApiResult(json, LoginData.serializer())
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
        val result = api.register(username, password, repassword).toApiResult(json, LoginData.serializer())
        if (result.isSuccess && result.data != null) {
            sessionManager.saveLogin(result.data)
        }
        return result
    }

    // 退出登录，清空本地登录态
    fun logout() {
        sessionManager.clear()
    }

    // 收藏文章（data 字段无意义，用 String serializer 占位）
    suspend fun collectArticle(id: Int): ApiResult<String> =
        api.collectArticle(id).toApiResult(json, String.serializer())

    // 取消收藏
    suspend fun uncollectArticle(id: Int): ApiResult<String> =
        api.uncollectArticle(id).toApiResult(json, String.serializer())

    // 是否已登录
    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    // 获取当前登录用户名
    fun getUsername(): String = sessionManager.getUsername()
}
