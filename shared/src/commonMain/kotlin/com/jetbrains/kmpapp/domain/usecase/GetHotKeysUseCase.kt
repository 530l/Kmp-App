package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.HotKey
import com.jetbrains.kmpapp.data.repository.SystemRepository

// 获取热搜词
class GetHotKeysUseCase(private val repo: SystemRepository) {
    suspend operator fun invoke(): ApiResult<List<HotKey>> = repo.getHotKeys()
}
