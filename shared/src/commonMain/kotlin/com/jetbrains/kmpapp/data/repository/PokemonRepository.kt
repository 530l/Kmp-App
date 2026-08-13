package com.jetbrains.kmpapp.data.repository

import com.jetbrains.kmpapp.data.model.NamedApiResource
import com.jetbrains.kmpapp.data.model.Pokemon
import com.jetbrains.kmpapp.data.model.PokemonListItem
import com.jetbrains.kmpapp.data.model.PokemonSpecies
import com.jetbrains.kmpapp.data.model.TypeDetail
import com.jetbrains.kmpapp.data.remote.PokeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class PokemonRepository(private val pokeApi: PokeApi) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val pageSize = 24

    // 预取主属性时限流：一页 24 个，最多 8 并发，免得打爆 PokeAPI 被封 IP
    private val prefetchPermits = Semaphore(8)

    // 列表的分页状态
    private val _list = MutableStateFlow(PagedList())
    val list: StateFlow<PagedList> = _list.asStateFlow()

    // PokeAPI 要求本地缓存已请求过的资源，否则可能被封 IP。详情按 id 缓存
    private val pokemonCache = mutableMapOf<Int, Pokemon>()
    private val speciesCache = mutableMapOf<Int, PokemonSpecies>()
    private val typeCache = mutableMapOf<Int, TypeDetail>()

    // 属性列表（就二十来个，单独放一个 StateFlow）
    private val _types = MutableStateFlow<List<NamedApiResource>>(emptyList())
    val types: StateFlow<List<NamedApiResource>> = _types.asStateFlow()

    init {
        loadMore() // 进来先拉第一页
        scope.launch {
            try {
                _types.value = pokeApi.getTypes(100).results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** 往后加载一页，正在加载或到底了就不重复拉 */
    fun loadMore() {
        val current = _list.value
        if (current.loading || current.finished) return
        _list.update { it.copy(loading = true) }
        scope.launch {
            try {
                val page = pokeApi.getPokemonList(limit = pageSize, offset = current.items.size)
                val newItems = page.results.map { it.toListItem() }
                _list.update {
                    it.copy(
                        items = it.items + newItems,
                        loading = false,
                        finished = page.next == null,
                    )
                }
                // 卡片要按主属性着色，但列表接口不返回属性，只能为每个新项预取一次详情
                // 复用 pokemonCache，进详情页时不会重复请求
                newItems.forEach { item -> prefetchPrimaryType(item.id) }
            } catch (e: Exception) {
                _list.update { it.copy(loading = false) }
                e.printStackTrace()
            }
        }
    }

    private fun prefetchPrimaryType(id: Int) {
        scope.launch {
            prefetchPermits.withPermit {
                try {
                    val primary = getPokemon(id).types.firstOrNull()?.type?.name
                    if (primary != null) {
                        _list.update { list ->
                            list.copy(items = list.items.map {
                                if (it.id == id) it.copy(primaryType = primary) else it
                            })
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun getPokemon(id: Int): Pokemon = pokemonCache.getOrPut(id) { pokeApi.getPokemon(id) }

    suspend fun getSpecies(id: Int): PokemonSpecies =
        speciesCache.getOrPut(id) { pokeApi.getSpecies(id) }

    suspend fun getType(id: Int): TypeDetail = typeCache.getOrPut(id) { pokeApi.getType(id) }

    // url 形如 ".../pokemon/1/"，尾巴那个数字就是 id
    private fun NamedApiResource.toListItem(): PokemonListItem {
        val id = url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
        return PokemonListItem(
            id = id,
            name = name,
            // 官方美术图地址是规律的，直接拼，列表项不必再单独请求
            artworkUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
        )
    }
}

/** 列表分页用的小状态 */
data class PagedList(
    val items: List<PokemonListItem> = emptyList(),
    val loading: Boolean = false,
    val finished: Boolean = false,
)
