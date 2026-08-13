package com.jetbrains.kmpapp.data.remote

import com.jetbrains.kmpapp.data.model.ApiResult
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// 把 HttpResponse 解析成 ApiResult<T>
// 网络读取 + JSON 解析全切到 IO 线程，不阻塞主线程
suspend fun <T> HttpResponse.toApiResult(
    json: Json,
    serializer: KSerializer<T>,
): ApiResult<T> = withContext(Dispatchers.IO) {
    val body = bodyAsText()
    val root = json.parseToJsonElement(body).jsonObject
    val errorCode = root["errorCode"]?.jsonPrimitive?.intOrNull ?: -1
    val errorMsg = root["errorMsg"]?.jsonPrimitive?.contentOrNull()
    val dataElement = root["data"]
    val data: T? = if (dataElement != null && !dataElement.toString().equals("null", ignoreCase = true)) {
        json.decodeFromString(serializer, dataElement.toString())
    } else {
        null
    }
    ApiResult(data = data, errorCode = errorCode, errorMsg = errorMsg)
}

// JsonPrimitive 安全取字符串值
private fun kotlinx.serialization.json.JsonPrimitive.contentOrNull(): String? =
    if (isString) content else null
