package com.jetbrains.kmpapp

import com.jetbrains.kmpapp.data.repository.AccountRepository
import com.jetbrains.kmpapp.data.repository.ArticleRepository
import com.jetbrains.kmpapp.data.repository.SystemRepository
import com.jetbrains.kmpapp.domain.usecase.CollectUseCase
import com.jetbrains.kmpapp.domain.usecase.GetChapterArticlesUseCase
import com.jetbrains.kmpapp.domain.usecase.GetCollectionsUseCase
import com.jetbrains.kmpapp.domain.usecase.GetHomeArticlesUseCase
import com.jetbrains.kmpapp.domain.usecase.GetHotKeysUseCase
import com.jetbrains.kmpapp.domain.usecase.GetSystemTreeUseCase
import com.jetbrains.kmpapp.domain.usecase.GetTopArticlesUseCase
import com.jetbrains.kmpapp.domain.usecase.GetWxAccountsUseCase
import com.jetbrains.kmpapp.domain.usecase.GetWxArticlesUseCase
import com.jetbrains.kmpapp.domain.usecase.LoginUseCase
import com.jetbrains.kmpapp.domain.usecase.RegisterUseCase
import com.jetbrains.kmpapp.domain.usecase.SearchArticlesUseCase
import com.jetbrains.kmpapp.domain.usecase.UncollectUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// iOS 这边不直接用 Koin 解析 ViewModel，而是从 Koin 拿到 UseCase / Repository，
// 再在 Swift 里手动 new 出 ViewModel（配合 @StateViewModel）
class KoinDependencies : KoinComponent {
    // ---- Repository（Profile/Account 直接用）----
    val accountRepository: AccountRepository by inject()
    val articleRepository: ArticleRepository by inject()
    val systemRepository: SystemRepository by inject()

    // ---- UseCase（各 ViewModel 用）----
    val getHomeArticles: GetHomeArticlesUseCase by inject()
    val getTopArticles: GetTopArticlesUseCase by inject()
    val getSystemTree: GetSystemTreeUseCase by inject()
    val getChapterArticles: GetChapterArticlesUseCase by inject()
    val getWxAccounts: GetWxAccountsUseCase by inject()
    val getWxArticles: GetWxArticlesUseCase by inject()
    val searchArticles: SearchArticlesUseCase by inject()
    val getHotKeys: GetHotKeysUseCase by inject()
    val login: LoginUseCase by inject()
    val register: RegisterUseCase by inject()
    val collect: CollectUseCase by inject()
    val uncollect: UncollectUseCase by inject()
    val getCollections: GetCollectionsUseCase by inject()
}
