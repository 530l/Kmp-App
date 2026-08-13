package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.model.PokemonListItem
import com.jetbrains.kmpapp.data.repository.PagedList
import com.jetbrains.kmpapp.data.repository.PokemonRepository
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PokedexViewModel(private val repository: PokemonRepository) : ViewModel() {
    // 列表分页整体状态（含 loading / finished，给 UI 显示加载条、到底提示用）
    @NativeCoroutinesState
    val pokedex: StateFlow<PagedList> =
        repository.list.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagedList())

    private val query = MutableStateFlow("")

    // 真正要展示的条目：没搜就直接是已加载列表，有关键字就现场过滤
    @NativeCoroutinesState
    val visibleItems: StateFlow<List<PokemonListItem>> =
        combine(repository.list, query) { pagedList, q ->
            if (q.isBlank()) pagedList.items
            else pagedList.items.filter { it.name.contains(q.trim(), ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(value: String) {
        query.value = value
    }

    fun loadMore() = repository.loadMore()
}
