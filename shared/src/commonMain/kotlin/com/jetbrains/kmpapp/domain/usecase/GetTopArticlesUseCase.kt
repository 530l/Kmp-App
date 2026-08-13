package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.repository.ArticleRepository

// 获取首页置顶文章
class GetTopArticlesUseCase(private val repo: ArticleRepository) {
    suspend operator fun invoke(): ApiResult<List<Article>> = repo.getTopArticles()
}
