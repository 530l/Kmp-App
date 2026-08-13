package com.jetbrains.kmpapp.data.remote

import com.jetbrains.kmpapp.data.model.ApiResult
import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.Chapter
import com.jetbrains.kmpapp.data.model.HotKey
import com.jetbrains.kmpapp.data.model.LoginData
import com.jetbrains.kmpapp.data.model.Page
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

// wanandroid 开放 API，用 Ktorfit 注解描述，baseUrl 在 Koin 里统一配
interface WanAndroidApi {

    // ---- 首页 ----

    // 首页文章列表，page 从 0 开始
    @GET("article/list/{page}/json")
    suspend fun getHomeArticles(
        @Path("page") page: Int,
    ): ApiResult<Page<Article>>

    // 首页置顶文章
    @GET("article/top/json")
    suspend fun getTopArticles(): ApiResult<List<Article>>

    // ---- 体系 ----

    // 体系树（含二级分类）
    @GET("tree/json")
    suspend fun getSystemTree(): ApiResult<List<Chapter>>

    // 某分类下的文章，page 从 0 开始
    @GET("article/list/{page}/json")
    suspend fun getChapterArticles(
        @Path("page") page: Int,
        @Query("cid") cid: Int,
    ): ApiResult<Page<Article>>

    // ---- 公众号 ----

    // 公众号列表
    @GET("wxarticle/chapters/json")
    suspend fun getWxAccounts(): ApiResult<List<Chapter>>

    // 某公众号的文章列表，page 从 1 开始
    @GET("wxarticle/list/{id}/{page}/json")
    suspend fun getWxArticles(
        @Path("id") id: Int,
        @Path("page") page: Int,
    ): ApiResult<Page<Article>>

    // ---- 搜索 ----

    // 热搜词
    @GET("hotkey/json")
    suspend fun getHotKeys(): ApiResult<List<HotKey>>

    // 搜索文章，page 从 0 开始
    @FormUrlEncoded
    @POST("article/query/{page}/json")
    suspend fun searchArticles(
        @Path("page") page: Int,
        @Field("k") keyword: String,
    ): ApiResult<Page<Article>>

    // ---- 登录/注册 ----

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): ApiResult<LoginData>

    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String,
    ): ApiResult<LoginData>

    // ---- 收藏 ----

    // 收藏站内文章
    @POST("lg/collect/{id}/json")
    suspend fun collectArticle(@Path("id") id: Int): ApiResult<String>

    // 取消收藏（站内文章用 originId）
    @POST("lg/uncollect_originId/{id}/json")
    suspend fun uncollectArticle(@Path("id") id: Int): ApiResult<String>

    // 收藏列表
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectList(@Path("page") page: Int): ApiResult<Page<Article>>
}
