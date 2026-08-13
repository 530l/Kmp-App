package com.jetbrains.kmpapp.data.model

import kotlinx.serialization.Serializable

// 分页外壳：首页/体系/公众号/搜索/收藏列表都返回这个结构
@Serializable
data class Page<T>(
    val curPage: Int = 0,
    val pageCount: Int = 0,
    val total: Int = 0,
    val over: Boolean = false,
    val datas: List<T> = emptyList(),
)
