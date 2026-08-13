package com.jetbrains.kmpapp.domain.usecase

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.LoginData
import com.jetbrains.kmpapp.data.repository.AccountRepository

// 注册
class RegisterUseCase(private val repo: AccountRepository) {
    suspend operator fun invoke(
        username: String,
        password: String,
        repassword: String,
    ): ApiResult<LoginData> = repo.register(username, password, repassword)
}
