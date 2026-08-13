package com.jetbrains.kmpapp.data.model

/** 列表上展示的精简项；primaryType 异步填充，给卡片背景按属性着色用 */
data class PokemonListItem(
    val id: Int,
    val name: String,
    val artworkUrl: String,
    val primaryType: String? = null,
)
