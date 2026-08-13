package com.jetbrains.kmpapp.data.model

import kotlinx.serialization.Serializable

// 体系/分类树节点：一级分类有 children（二级分类），公众号列表也用这个结构
@Serializable
data class Chapter(
    val id: Int,
    val name: String = "",
    val order: Int = 0,
    val parentChapterId: Int = 0,
    val visible: Int = 1,
    val children: List<Chapter> = emptyList(),
)
