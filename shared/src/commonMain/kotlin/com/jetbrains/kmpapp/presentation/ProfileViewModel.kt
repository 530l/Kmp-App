package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.repository.AccountRepository
import com.rickclephas.kmp.observableviewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 我的页面状态：登录/未登录
data class ProfileState(
    val loggedIn: Boolean = false,
    val username: String = "",
)

class ProfileViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    // 从 SessionManager 同步当前登录态
    fun syncLoginState() {
        _state.value = ProfileState(
            loggedIn = accountRepository.isLoggedIn(),
            username = if (accountRepository.isLoggedIn()) accountRepository.getUsername() else "",
        )
    }

    // 退出登录
    fun logout() {
        accountRepository.logout()
        _state.value = ProfileState()
    }
}
