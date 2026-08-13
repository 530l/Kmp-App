package com.jetbrains.kmpapp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.jetbrains.kmpapp.di.initKoin
import com.jetbrains.kmpapp.presentation.DetailViewModel
import com.jetbrains.kmpapp.presentation.PokedexViewModel
import com.jetbrains.kmpapp.presentation.TypeDetailViewModel
import com.jetbrains.kmpapp.presentation.TypesViewModel
import org.koin.dsl.module

class MuseumApp : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            listOf(
                module {
                    factory { PokedexViewModel(get()) }
                    factory { DetailViewModel(get()) }
                    factory { TypesViewModel(get()) }
                    factory { TypeDetailViewModel(get()) }
                },
            ),
        )
    }

    // 给 Coil 接上 Ktor 网络栈，默认就带内存+磁盘缓存，图只下一次
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .crossfade(true)
            .build()
}
