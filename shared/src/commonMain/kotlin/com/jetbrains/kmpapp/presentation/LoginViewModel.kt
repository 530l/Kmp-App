package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.repository.AccountRepository
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 登录/注册页状态
data class LoginState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val mode: LoginMode = LoginMode.LOGIN,
)

enum class LoginMode { LOGIN, REGISTER }

class LoginViewModel(
    private val repo: AccountRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    // 切换 登录/注册 模式
    fun switchMode(mode: LoginMode) {
        _state.value = _state.value.copy(mode = mode, error = null)
    }

    // 提交（根据当前 mode 自动调登录或注册）
    fun submit(username: String, password: String, repassword: String = password) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(error = "用户名和密码不能为空")
            return
        }
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.coroutineScope.launch {
            try {
                val result = if (_state.value.mode == LoginMode.LOGIN) {
                    repo.login(username, password)
                } else {
                    if (password != repassword) {
                        _state.value = _state.value.copy(loading = false, error = "两次密码不一致")
                        return@launch
                    }
                    repo.register(username, password, repassword)
                }
                if (result.isSuccess) {
                    _state.value = _state.value.copy(loading = false, success = true)
                } else {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = result.errorMsg ?: "操作失败",
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message ?: "网络异常")
            }
        }
    }
}
