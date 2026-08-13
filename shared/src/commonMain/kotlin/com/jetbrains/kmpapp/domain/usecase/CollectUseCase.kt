package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.repository.AccountRepository

// 收藏文章
class CollectUseCase(private val repo: AccountRepository) {
    suspend operator fun invoke(id: Int): ApiResult<String> = repo.collectArticle(id)
}
