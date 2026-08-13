package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.Page
import com.jetbrains.kmpapp.data.repository.ArticleRepository

// 搜索文章
class SearchArticlesUseCase(private val repo: ArticleRepository) {
    suspend operator fun invoke(page: Int, keyword: String): ApiResult<Page<Article>> =
        repo.searchArticles(page, keyword)
}
