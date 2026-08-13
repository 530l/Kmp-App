package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.model.TypeDetail
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

class TypeDetailViewModel(private val repository: PokemonRepository) : ViewModel() {
    private val typeId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    @NativeCoroutinesState
    val typeDetail: StateFlow<TypeDetail?> = typeId
        .filterNotNull()
        .flatMapLatest { id -> flow { emit(repository.getType(id)) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setId(id: Int) {
        typeId.value = id
    }
}
