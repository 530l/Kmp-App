package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.data.remote.WanAndroidApi
import com.jetbrains.kmpapp.data.remote.createWanAndroidApi
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
import com.jetbrains.kmpapp.storage.SessionManager
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

// 数据层：HttpClient + Ktorfit Api + Repository + SessionManager
val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) { json(json) }
        }
    }

    single<WanAndroidApi> {
        Ktorfit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .httpClient(get<HttpClient>())
            .build()
            .createWanAndroidApi()
    }

    single { SessionManager() }
    single { ArticleRepository(get()) }
    single { SystemRepository(get()) }
    single { AccountRepository(get(), get()) }
}

// 领域层：全部 UseCase
val domainModule = module {
    single { GetHomeArticlesUseCase(get()) }
    single { GetTopArticlesUseCase(get()) }
    single { GetSystemTreeUseCase(get()) }
    single { GetChapterArticlesUseCase(get()) }
    single { GetWxAccountsUseCase(get()) }
    single { GetWxArticlesUseCase(get()) }
    single { SearchArticlesUseCase(get()) }
    single { GetHotKeysUseCase(get()) }
    single { LoginUseCase(get()) }
    single { RegisterUseCase(get()) }
    single { CollectUseCase(get()) }
    single { UncollectUseCase(get()) }
    single { GetCollectionsUseCase(get()) }
}

fun initKoin() = initKoin(emptyList())

fun initKoin(extraModules: List<Module>) {
    startKoin {
        modules(dataModule, domainModule, *extraModules.toTypedArray())
    }
}
