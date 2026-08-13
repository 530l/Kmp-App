package com.jetbrains.kmpapp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.jetbrains.kmpapp.di.initKoin
import com.jetbrains.kmpapp.presentation.CollectionViewModel
import com.jetbrains.kmpapp.presentation.HomeViewModel
import com.jetbrains.kmpapp.presentation.LoginViewModel
import com.jetbrains.kmpapp.presentation.ProfileViewModel
import com.jetbrains.kmpapp.presentation.SearchViewModel
import com.jetbrains.kmpapp.presentation.SystemViewModel
import com.jetbrains.kmpapp.presentation.WxViewModel
import com.jetbrains.kmpapp.storage.SessionManager
import org.koin.dsl.module

class WanApp : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        // SessionManager 需要 Context，在 Koin 初始化前设置
        SessionManager.appContext = this
        initKoin(
            listOf(
                module {
                    // 各 ViewModel 通过 Koin 注入 UseCase（自动解析）
                    factory { HomeViewModel(get(), get()) }
                    factory { SystemViewModel(get(), get()) }
                    factory { WxViewModel(get(), get()) }
                    factory { SearchViewModel(get(), get()) }
                    factory { LoginViewModel(get(), get()) }
                    factory { CollectionViewModel(get()) }
                    factory { ProfileViewModel(get()) }
                },
            ),
        )
    }

    // 给 Coil 接上 Ktor 网络栈，默认带内存+磁盘缓存，图只下一次
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .crossfade(true)
            .build()
}
