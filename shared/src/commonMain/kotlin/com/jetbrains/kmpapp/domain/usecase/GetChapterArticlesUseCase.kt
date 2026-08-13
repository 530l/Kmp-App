package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.Page
import com.jetbrains.kmpapp.data.repository.ArticleRepository

// 获取某分类下的文章
class GetChapterArticlesUseCase(private val repo: ArticleRepository) {
    suspend operator fun invoke(page: Int, cid: Int): ApiResult<Page<Article>> =
        repo.getChapterArticles(page, cid)
}
