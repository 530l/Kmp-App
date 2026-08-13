package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.Page
import com.jetbrains.kmpapp.data.repository.ArticleRepository

// 获取首页文章列表
class GetHomeArticlesUseCase(private val repo: ArticleRepository) {
    suspend operator fun invoke(page: Int): ApiResult<Page<Article>> = repo.getHomeArticles(page)
}
