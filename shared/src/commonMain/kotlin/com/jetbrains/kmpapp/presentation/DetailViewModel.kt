package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.model.Pokemon
import com.jetbrains.kmpapp.data.model.PokemonSpecies
import com.jetbrains.kmpapp.data.repository.PokemonRepository
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class DetailViewModel(private val repository: PokemonRepository) : ViewModel() {
    private val pokemonId = MutableStateFlow<Int?>(null)

    // 详情的本体（数值 / 属性 / 图）
    @OptIn(ExperimentalCoroutinesApi::class)
    @NativeCoroutinesState
    val pokemon: StateFlow<Pokemon?> = pokemonId
        .filterNotNull()
        .flatMapLatest { id -> flow { emit(repository.getPokemon(id)) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 种族资料（中英文名、简介），和本体分开请求
    @OptIn(ExperimentalCoroutinesApi::class)
    @NativeCoroutinesState
    val species: StateFlow<PokemonSpecies?> = pokemonId
        .filterNotNull()
        .flatMapLatest { id -> flow { emit(repository.getSpecies(id)) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setId(id: Int) {
        pokemonId.value = id
    }
}
