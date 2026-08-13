package com.jetbrains.kmpapp.data.remote

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

// wanandroid 开放 API，统一返回 HttpResponse，由 Repository 用 Json 手动解析 ApiResult 外壳
interface WanAndroidApi {

    // ---- 首页 ----
    @GET("article/list/{page}/json")
    suspend fun getHomeArticles(@Path("page") page: Int): HttpResponse

    @GET("article/top/json")
    suspend fun getTopArticles(): HttpResponse

    // ---- 体系 ----
    @GET("tree/json")
    suspend fun getSystemTree(): HttpResponse

    @GET("article/list/{page}/json")
    suspend fun getChapterArticles(
        @Path("page") page: Int,
        @Query("cid") cid: Int,
    ): HttpResponse

    // ---- 公众号 ----
    @GET("wxarticle/chapters/json")
    suspend fun getWxAccounts(): HttpResponse

    @GET("wxarticle/list/{id}/{page}/json")
    suspend fun getWxArticles(
        @Path("id") id: Int,
        @Path("page") page: Int,
    ): HttpResponse

    // ---- 搜索 ----
    @GET("hotkey/json")
    suspend fun getHotKeys(): HttpResponse

    @FormUrlEncoded
    @POST("article/query/{page}/json")
    suspend fun searchArticles(
        @Path("page") page: Int,
        @Field("k") keyword: String,
    ): HttpResponse

    // ---- 登录/注册 ----
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): HttpResponse

    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String,
    ): HttpResponse

    // ---- 收藏 ----
    @POST("lg/collect/{id}/json")
    suspend fun collectArticle(@Path("id") id: Int): HttpResponse

    @POST("lg/uncollect_originId/{id}/json")
    suspend fun uncollectArticle(@Path("id") id: Int): HttpResponse

    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectList(@Path("page") page: Int): HttpResponse
}
