package com.jetbrains.kmpapp.data.repository

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Chapter
import com.jetbrains.kmpapp.data.model.HotKey
import com.jetbrains.kmpapp.data.remote.WanAndroidApi
import com.jetbrains.kmpapp.data.remote.toApiResult
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

// 体系/公众号/热搜等静态数据仓库
// 这些数据变化频率低，拉过一次就缓存，切 Tab 回来直接用缓存
class SystemRepository(
    private val api: WanAndroidApi,
    private val json: Json,
) {
    private var treeCache: List<Chapter>? = null
    private var wxAccountsCache: List<Chapter>? = null
    private var hotKeysCache: List<HotKey>? = null

    suspend fun getSystemTree(): ApiResult<List<Chapter>> {
        treeCache?.let { return ApiResult(data = it, errorCode = 0) }
        val result = api.getSystemTree().toApiResult(json, ListSerializer(Chapter.serializer()))
        if (result.isSuccess && result.data != null) treeCache = result.data
        return result
    }

    suspend fun getWxAccounts(): ApiResult<List<Chapter>> {
        wxAccountsCache?.let { return ApiResult(data = it, errorCode = 0) }
        val result = api.getWxAccounts().toApiResult(json, ListSerializer(Chapter.serializer()))
        if (result.isSuccess && result.data != null) wxAccountsCache = result.data
        return result
    }

    suspend fun getHotKeys(): ApiResult<List<HotKey>> {
        hotKeysCache?.let { return ApiResult(data = it, errorCode = 0) }
        val result = api.getHotKeys().toApiResult(json, ListSerializer(HotKey.serializer()))
        if (result.isSuccess && result.data != null) hotKeysCache = result.data
        return result
    }
}
