package com.jetbrains.kmpapp

import com.jetbrains.kmpapp.data.repository.PokemonRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// iOS 这边不直接用 Koin 解析 ViewModel，而是从 Koin 拿到 repository，
// 再在 Swift 里手动 new 出 ViewModel（配合 @StateViewModel）
class KoinDependencies : KoinComponent {
    val pokemonRepository: PokemonRepository by inject()
}
