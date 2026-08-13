package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.HotKey
import com.jetbrains.kmpapp.data.repository.ArticleRepository
import com.jetbrains.kmpapp.data.repository.SystemRepository
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 搜索页状态：热搜词 + 搜索结果
data class SearchState(
    val hotKeys: List<HotKey> = emptyList(),
    val keyword: String = "",
    val results: List<Article> = emptyList(),
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val finished: Boolean = false,
    val searched: Boolean = false, // 是否已发起过搜索（区分初始态）
    val error: String? = null,
)

class SearchViewModel(
    private val systemRepo: SystemRepository,
    private val articleRepo: ArticleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var page = 0

    init {
        loadHotKeys()
    }

    // 加载热搜词
    private fun loadHotKeys() {
        viewModelScope.coroutineScope.launch {
            try {
                val result = systemRepo.getHotKeys()
                if (result.isSuccess && result.data != null) {
                    _state.value = _state.value.copy(hotKeys = result.data)
                }
            } catch (e: Exception) {
                // 热搜词加载失败不阻塞页面使用
            }
        }
    }

    // 设置关键词（输入框实时同步）
    fun setKeyword(value: String) {
        _state.value = _state.value.copy(keyword = value)
    }

    // 发起搜索
    fun search(keyword: String = _state.value.keyword) {
        val q = keyword.trim()
        if (q.isBlank()) return
        page = 0
        _state.value = _state.value.copy(keyword = q, loading = true, searched = true)
        viewModelScope.coroutineScope.launch {
            try {
                val result = articleRepo.searchArticles(page, q)
                if (result.isSuccess && result.data != null) {
                    val p = result.data
                    _state.value = _state.value.copy(
                        results = p.datas,
                        loading = false,
                        finished = p.over,
                    )
                } else {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = result.errorMsg ?: "搜索失败",
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message ?: "网络异常")
            }
        }
    }

    // 下一页
    fun loadMore() {
        val current = _state.value
        if (current.loadingMore || current.finished || current.loading) return
        page++
        _state.value = current.copy(loadingMore = true)
        viewModelScope.coroutineScope.launch {
            try {
                val result = articleRepo.searchArticles(page, current.keyword)
                if (result.isSuccess && result.data != null) {
                    val p = result.data
                    _state.value = _state.value.copy(
                        results = current.results + p.datas,
                        loadingMore = false,
                        finished = p.over,
                    )
                } else {
                    page--
                    _state.value = _state.value.copy(loadingMore = false)
                }
            } catch (e: Exception) {
                page--
                _state.value = _state.value.copy(loadingMore = false)
            }
        }
    }

    // 清空搜索，回到初始态
    fun reset() {
        page = 0
        _state.value = _state.value.copy(
            keyword = "",
            results = emptyList(),
            searched = false,
            finished = false,
            error = null,
        )
    }
}
