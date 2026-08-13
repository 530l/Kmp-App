package com.jetbrains.kmpapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 文章模型：首页/体系/公众号/搜索/收藏列表共用
@Serializable
data class Article(
    val id: Int,
    val title: String = "",
    val desc: String = "",
    val author: String = "",
    val shareUser: String = "",
    val link: String = "",
    @SerialName("chapterName") val chapterName: String = "",
    @SerialName("superChapterName") val superChapterName: String = "",
    @SerialName("niceDate") val niceDate: String = "",
    @SerialName("niceShareDate") val niceShareDate: String = "",
    @SerialName("publishTime") val publishTime: Long = 0,
    @SerialName("envelopePic") val envelopePic: String = "",
    @SerialName("collect") val collect: Boolean = false,
    @SerialName("fresh") val fresh: Boolean = false,
    @SerialName("tags") val tags: List<ArticleTag> = emptyList(),
    @SerialName("userId") val userId: Int = 0,
) {
    // 作者优先显示 author，没有则用 shareUser
    val displayAuthor: String get() = author.ifBlank { shareUser }

    // 分类名：优先用「上级分类·分类」格式
    val displayChapter: String
        get() = if (superChapterName.isNotBlank() && chapterName.isNotBlank()) {
            "$superChapterName·$chapterName"
        } else {
            superChapterName.ifBlank { chapterName }
        }
}

@Serializable
data class ArticleTag(
    val name: String = "",
    val url: String = "",
)
