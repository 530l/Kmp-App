package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.data.remote.WanAndroidApi
import com.jetbrains.kmpapp.data.remote.createWanAndroidApi
import com.jetbrains.kmpapp.data.repository.AccountRepository
import com.jetbrains.kmpapp.data.repository.ArticleRepository
import com.jetbrains.kmpapp.data.repository.SystemRepository
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

    // Json 实例单独暴露，Repository 手动解析 ApiResult 外壳时用
    single { Json { ignoreUnknownKeys = true } }

    single<WanAndroidApi> {
        Ktorfit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .httpClient(get<HttpClient>())
            .build()
            .createWanAndroidApi()
    }

    single { SessionManager() }
    single { ArticleRepository(get(), get()) }
    single { SystemRepository(get(), get()) }
    single { AccountRepository(get(), get(), get()) }
}

fun initKoin() = initKoin(emptyList())

fun initKoin(extraModules: List<Module>) {
    startKoin {
        modules(dataModule, *extraModules.toTypedArray())
    }
}
