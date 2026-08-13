package com.jetbrains.kmpapp.data.repository

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.Page
import com.jetbrains.kmpapp.data.remote.WanAndroidApi

// 文章数据仓库：首页、置顶、体系文章、公众号文章、搜索
class ArticleRepository(private val api: WanAndroidApi) {

    // 首页文章列表，page 从 0 开始
    suspend fun getHomeArticles(page: Int): ApiResult<Page<Article>> =
        api.getHomeArticles(page)

    // 首页置顶文章
    suspend fun getTopArticles(): ApiResult<List<Article>> =
        api.getTopArticles()

    // 某分类下的文章
    suspend fun getChapterArticles(page: Int, cid: Int): ApiResult<Page<Article>> =
        api.getChapterArticles(page, cid)

    // 某公众号的文章列表，page 从 1 开始
    suspend fun getWxArticles(id: Int, page: Int): ApiResult<Page<Article>> =
        api.getWxArticles(id, page)

    // 搜索文章
    suspend fun searchArticles(page: Int, keyword: String): ApiResult<Page<Article>> =
        api.searchArticles(page, keyword)

    // 收藏列表
    suspend fun getCollectList(page: Int): ApiResult<Page<Article>> =
        api.getCollectList(page)
}
