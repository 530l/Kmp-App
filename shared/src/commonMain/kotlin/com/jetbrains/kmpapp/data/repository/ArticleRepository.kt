package com.jetbrains.kmpapp.data.repository

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.Page
import com.jetbrains.kmpapp.data.remote.WanAndroidApi
import com.jetbrains.kmpapp.data.remote.toApiResult
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

// 文章数据仓库：toApiResult 内部已切 IO 线程
class ArticleRepository(
    private val api: WanAndroidApi,
    private val json: Json,
) {
    private val pageSerializer = Page.serializer(Article.serializer())
    private val listSerializer = ListSerializer(Article.serializer())

    suspend fun getHomeArticles(page: Int): ApiResult<Page<Article>> =
        api.getHomeArticles(page).toApiResult(json, pageSerializer)

    suspend fun getTopArticles(): ApiResult<List<Article>> =
        api.getTopArticles().toApiResult(json, listSerializer)

    suspend fun getChapterArticles(page: Int, cid: Int): ApiResult<Page<Article>> =
        api.getChapterArticles(page, cid).toApiResult(json, pageSerializer)

    suspend fun getWxArticles(id: Int, page: Int): ApiResult<Page<Article>> =
        api.getWxArticles(id, page).toApiResult(json, pageSerializer)

    suspend fun searchArticles(page: Int, keyword: String): ApiResult<Page<Article>> =
        api.searchArticles(page, keyword).toApiResult(json, pageSerializer)

    suspend fun getCollectList(page: Int): ApiResult<Page<Article>> =
        api.getCollectList(page).toApiResult(json, pageSerializer)
}
