package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.repository.AccountRepository

// 取消收藏
class UncollectUseCase(private val repo: AccountRepository) {
    suspend operator fun invoke(id: Int): ApiResult<String> = repo.uncollectArticle(id)
}
