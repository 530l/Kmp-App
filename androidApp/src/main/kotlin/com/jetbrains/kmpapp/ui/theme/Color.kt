package com.jetbrains.kmpapp.ui.theme

import androidx.compose.ui.graphics.Color
import com.jetbrains.kmpapp.data.model.PokemonTypeColors

// 品牌主色：精灵球红
val PokeRed = Color(0xFFEE1515)
val PokeRedDark = Color(0xFFC00000)

// 18 种属性色唯一来源在 shared 的 PokemonTypeColors，这里只做 Compose Color 转换，
// 避免和 iOS 各维护一份导致双端颜色漂移
fun colorForType(name: String): Color = Color(PokemonTypeColors.argb(name))
