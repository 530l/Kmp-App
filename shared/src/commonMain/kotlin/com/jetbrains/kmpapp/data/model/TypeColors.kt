package com.jetbrains.kmpapp.data.model

/**
 * 18 种属性的代表色，ARGB Long 形式。
 * 双端 UI 的唯一数据源：Android 转 Color(argb)、iOS 转 UIColor，避免两端各维护一份色表。
 */
object PokemonTypeColors {
    private val colors: Map<String, Long> = mapOf(
        "normal" to 0xFFA8A878,
        "fire" to 0xFFF08030,
        "water" to 0xFF6890F0,
        "grass" to 0xFF78C850,
        "electric" to 0xFFF8D030,
        "ice" to 0xFF98D8D8,
        "fighting" to 0xFFC03028,
        "poison" to 0xFFA040A0,
        "ground" to 0xFFE0C068,
        "flying" to 0xFFA890F0,
        "psychic" to 0xFFF85888,
        "bug" to 0xFFA8B820,
        "rock" to 0xFFB8A038,
        "ghost" to 0xFF705898,
        "dragon" to 0xFF7038F8,
        "dark" to 0xFF705848,
        "steel" to 0xFFB8B8D0,
        "fairy" to 0xFFEE99AC,
    )

    /** 拿不到就回退普通系灰色 */
    fun argb(type: String): Long = colors[type.lowercase()] ?: 0xFFA8A878
}
