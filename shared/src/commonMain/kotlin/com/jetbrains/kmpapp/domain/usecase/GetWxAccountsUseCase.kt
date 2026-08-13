package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Chapter
import com.jetbrains.kmpapp.data.repository.SystemRepository

// 获取公众号列表
class GetWxAccountsUseCase(private val repo: SystemRepository) {
    suspend operator fun invoke(): ApiResult<List<Chapter>> = repo.getWxAccounts()
}
