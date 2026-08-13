package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Chapter
import com.jetbrains.kmpapp.data.repository.SystemRepository

// 获取体系树
class GetSystemTreeUseCase(private val repo: SystemRepository) {
    suspend operator fun invoke(): ApiResult<List<Chapter>> = repo.getSystemTree()
}
