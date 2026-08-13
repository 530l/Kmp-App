package com.jetbrains.kmpapp.data.remote

import com.jetbrains.kmpapp.data.model.NamedApiResourceList
import com.jetbrains.kmpapp.data.model.Pokemon
import com.jetbrains.kmpapp.data.model.PokemonSpecies
import com.jetbrains.kmpapp.data.model.TypeDetail
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

// 用 Ktorfit 注解描述 PokeAPI，baseUrl 在 Koin 里统一配
interface PokeApi {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): NamedApiResourceList

    @GET("pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): Pokemon

    @GET("pokemon-species/{id}")
    suspend fun getSpecies(@Path("id") id: Int): PokemonSpecies

    // 属性一共也就二十来个，直接一把拉回来
    @GET("type")
    suspend fun getTypes(@Query("limit") limit: Int): NamedApiResourceList

    @GET("type/{id}")
    suspend fun getType(@Path("id") id: Int): TypeDetail
}
