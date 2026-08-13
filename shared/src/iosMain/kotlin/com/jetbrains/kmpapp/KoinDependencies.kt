package com.jetbrains.kmpapp

import com.jetbrains.kmpapp.data.repository.AccountRepository
import com.jetbrains.kmpapp.data.repository.ArticleRepository
import com.jetbrains.kmpapp.data.repository.SystemRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// iOS 这边从 Koin 拿 Repository，在 Swift 里手动 new ViewModel
class KoinDependencies : KoinComponent {
    val accountRepository: AccountRepository by inject()
    val articleRepository: ArticleRepository by inject()
    val systemRepository: SystemRepository by inject()
}
