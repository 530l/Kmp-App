package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.Page
import com.jetbrains.kmpapp.data.repository.ArticleRepository

// 获取某公众号的文章列表，page 从 1 开始
class GetWxArticlesUseCase(private val repo: ArticleRepository) {
    suspend operator fun invoke(id: Int, page: Int): ApiResult<Page<Article>> =
        repo.getWxArticles(id, page)
}
