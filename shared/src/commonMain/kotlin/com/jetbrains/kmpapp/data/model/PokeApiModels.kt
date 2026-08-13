package com.jetbrains.kmpapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// PokeAPI 里到处都是这种「名字 + url」的资源引用（列表项、属性、招式都共用）
@Serializable
data class NamedApiResource(
    val name: String,
    val url: String,
)

// 列表接口的统一外壳，next 为 null 表示翻到底了
@Serializable
data class NamedApiResourceList(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<NamedApiResource> = emptyList(),
)

// ---- 宝可梦本体 ----
@Serializable
data class Pokemon(
    val id: Int,
    val name: String,
    val height: Int = 0,            // PokeAPI 里单位是分米
    val weight: Int = 0,            // 单位是百克
    @SerialName("base_experience") val baseExperience: Int = 0,
    val types: List<PokemonType> = emptyList(),
    val stats: List<PokemonStat> = emptyList(),
    val sprites: PokemonSprites = PokemonSprites(),
)

@Serializable
data class PokemonType(
    val slot: Int = 0,
    val type: NamedApiResource,
)

@Serializable
data class PokemonStat(
    @SerialName("base_stat") val baseStat: Int = 0,
    val effort: Int = 0,
    val stat: NamedApiResource,
)

// 官方美术图藏在 sprites.other."official-artwork".front_default，套了三层且 key 带横杠
@Serializable
data class PokemonSprites(
    @SerialName("front_default") val frontDefault: String? = null,
    val other: SpriteOthers? = null,
)

@Serializable
data class SpriteOthers(
    @SerialName("official-artwork") val officialArtwork: Artwork? = null,
)

@Serializable
data class Artwork(
    @SerialName("front_default") val frontDefault: String? = null,
)

// ---- 种族资料：取多语言名字和简介 ----
@Serializable
data class PokemonSpecies(
    val id: Int,
    val name: String,
    val names: List<PokemonName> = emptyList(),
    val genera: List<PokemonGenus> = emptyList(),
    @SerialName("flavor_text_entries") val flavorTextEntries: List<PokemonFlavorText> = emptyList(),
)

@Serializable
data class PokemonName(
    val name: String,
    val language: NamedApiResource,
)

@Serializable
data class PokemonGenus(
    val genus: String,
    val language: NamedApiResource,
)

@Serializable
data class PokemonFlavorText(
    @SerialName("flavor_text") val flavorText: String,
    val language: NamedApiResource,
)

// ---- 属性相克 ----
@Serializable
data class TypeDetail(
    val id: Int,
    val name: String,
    @SerialName("damage_relations") val damageRelations: TypeRelations = TypeRelations(),
)

@Serializable
data class TypeRelations(
    @SerialName("double_damage_to") val doubleDamageTo: List<NamedApiResource> = emptyList(),
    @SerialName("half_damage_to") val halfDamageTo: List<NamedApiResource> = emptyList(),
    @SerialName("no_damage_to") val noDamageTo: List<NamedApiResource> = emptyList(),
    @SerialName("double_damage_from") val doubleDamageFrom: List<NamedApiResource> = emptyList(),
    @SerialName("half_damage_from") val halfDamageFrom: List<NamedApiResource> = emptyList(),
    @SerialName("no_damage_from") val noDamageFrom: List<NamedApiResource> = emptyList(),
)

// url 形如 ".../type/4/"，尾巴那个数字就是 id，列表跳详情要用，Android/iOS 共用
val NamedApiResource.id: Int
    get() = url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
