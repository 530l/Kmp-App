package com.jetbrains.kmpapp.presentation

// 通用 UI 状态：Loading / Success / Error，ViewModel 统一用这个包装
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
