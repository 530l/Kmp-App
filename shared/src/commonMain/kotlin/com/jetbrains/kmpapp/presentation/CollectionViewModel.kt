package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.repository.ArticleRepository
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 收藏列表页状态
data class CollectionState(
    val articles: List<Article> = emptyList(),
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val finished: Boolean = false,
    val error: String? = null,
)

class CollectionViewModel(
    private val repo: ArticleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionState(loading = true))
    val state: StateFlow<CollectionState> = _state.asStateFlow()

    private var page = 0

    init {
        refresh()
    }

    fun refresh() {
        page = 0
        _state.value = CollectionState(loading = true)
        viewModelScope.coroutineScope.launch {
            try {
                val result = repo.getCollectList(page)
                if (result.isSuccess && result.data != null) {
                    val p = result.data
                    _state.value = CollectionState(
                        articles = p.datas,
                        finished = p.over,
                    )
                } else {
                    _state.value = CollectionState(error = result.errorMsg ?: "加载失败")
                }
            } catch (e: Exception) {
                _state.value = CollectionState(error = e.message ?: "网络异常")
            }
        }
    }

    fun loadMore() {
        val current = _state.value
        if (current.loadingMore || current.finished || current.loading) return
        page++
        _state.value = current.copy(loadingMore = true)
        viewModelScope.coroutineScope.launch {
            try {
                val result = repo.getCollectList(page)
                if (result.isSuccess && result.data != null) {
                    val p = result.data
                    _state.value = _state.value.copy(
                        articles = current.articles + p.datas,
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
}
