package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.model.NamedApiResource
import com.jetbrains.kmpapp.data.repository.PokemonRepository
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TypesViewModel(repository: PokemonRepository) : ViewModel() {
    @NativeCoroutinesState
    val types: StateFlow<List<NamedApiResource>> =
        repository.types.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
