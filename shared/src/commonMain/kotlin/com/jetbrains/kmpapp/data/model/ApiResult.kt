package com.jetbrains.kmpapp.data.model

// wanandroid 统一响应外壳：errorCode=0 表示成功
// 泛型类不用于自动反序列化，Repository 里用 Json 手动解析
data class ApiResult<T>(
    val data: T? = null,
    val errorCode: Int = -1,
    val errorMsg: String? = null,
) {
    val isSuccess: Boolean get() = errorCode == 0
}
