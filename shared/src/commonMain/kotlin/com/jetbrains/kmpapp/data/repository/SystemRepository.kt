package com.jetbrains.kmpapp.data.repository

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Chapter
import com.jetbrains.kmpapp.data.model.HotKey
import com.jetbrains.kmpapp.data.remote.WanAndroidApi

// 体系/公众号/热搜等静态数据仓库
class SystemRepository(private val api: WanAndroidApi) {

    // 体系树（含二级分类）
    suspend fun getSystemTree(): ApiResult<List<Chapter>> =
        api.getSystemTree()

    // 公众号列表
    suspend fun getWxAccounts(): ApiResult<List<Chapter>> =
        api.getWxAccounts()

    // 热搜词
    suspend fun getHotKeys(): ApiResult<List<HotKey>> =
        api.getHotKeys()
}
