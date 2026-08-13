package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.Chapter
import com.jetbrains.kmpapp.domain.usecase.GetWxAccountsUseCase
import com.jetbrains.kmpapp.domain.usecase.GetWxArticlesUseCase
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 公众号页状态：公众号列表 + 当前选中公众号的文章列表
data class WxState(
    val accounts: List<Chapter> = emptyList(),
    val selectedId: Int = -1,
    val articles: List<Article> = emptyList(),
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val finished: Boolean = false,
    val error: String? = null,
)

class WxViewModel(
    private val getWxAccounts: GetWxAccountsUseCase,
    private val getWxArticles: GetWxArticlesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(WxState(loading = true))
    val state: StateFlow<WxState> = _state.asStateFlow()

    private var page = 1 // 公众号文章页码从 1 开始

    init {
        loadAccounts()
    }

    // 重新加载公众号列表
    fun refresh() = loadAccounts()

    // 加载公众号列表，默认选第一个
    private fun loadAccounts() {
        viewModelScope.coroutineScope.launch {
            try {
                val result = getWxAccounts()
                if (result.isSuccess && result.data != null) {
                    val accounts = result.data
                    val firstId = accounts.firstOrNull()?.id ?: -1
                    _state.value = WxState(accounts = accounts, selectedId = firstId)
                    if (firstId != -1) loadArticles(firstId, reset = true)
                } else {
                    _state.value = WxState(error = result.errorMsg ?: "加载失败")
                }
            } catch (e: Exception) {
                _state.value = WxState(error = e.message ?: "网络异常")
            }
        }
    }

    // 切换公众号
    fun selectAccount(id: Int) {
        if (_state.value.selectedId == id) return
        _state.value = _state.value.copy(selectedId = id)
        loadArticles(id, reset = true)
    }

    // 加载文章，reset=true 表示从头开始
    private fun loadArticles(id: Int, reset: Boolean) {
        if (reset) page = 1
        val current = _state.value
        _state.value = if (reset) current.copy(loading = true) else current.copy(loadingMore = true)
        viewModelScope.coroutineScope.launch {
            try {
                val result = getWxArticles(id, page)
                if (result.isSuccess && result.data != null) {
                    val p = result.data
                    _state.value = _state.value.copy(
                        articles = if (reset) p.datas else current.articles + p.datas,
                        loading = false,
                        loadingMore = false,
                        finished = p.over,
                    )
                } else {
                    _state.value = _state.value.copy(loading = false, loadingMore = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, loadingMore = false)
            }
        }
    }

    // 下一页
    fun loadMore() {
        val id = _state.value.selectedId
        if (id == -1) return
        val current = _state.value
        if (current.loadingMore || current.finished || current.loading) return
        page++
        loadArticles(id, reset = false)
    }
}
