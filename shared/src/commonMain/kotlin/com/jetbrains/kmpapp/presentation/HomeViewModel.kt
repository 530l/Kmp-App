package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.repository.ArticleRepository
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 首页状态：置顶 + 文章分页
data class HomeState(
    val topArticles: List<Article> = emptyList(),
    val articles: List<Article> = emptyList(),
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val finished: Boolean = false,
    val error: String? = null,
)

class HomeViewModel(
    private val repo: ArticleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState(loading = true))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var page = 0

    init {
        refresh()
    }

    // 首次加载或下拉刷新
    fun refresh() {
        page = 0
        _state.value = HomeState(loading = true)
        viewModelScope.coroutineScope.launch {
            try {
                val tops = repo.getTopArticles().data ?: emptyList()
                val result = repo.getHomeArticles(page)
                if (result.isSuccess) {
                    val p = result.data!!
                    _state.value = HomeState(
                        topArticles = tops,
                        articles = p.datas,
                        finished = p.over,
                    )
                } else {
                    _state.value = HomeState(error = result.errorMsg ?: "加载失败")
                }
            } catch (e: Exception) {
                _state.value = HomeState(error = e.message ?: "网络异常")
            }
        }
    }

    // 加载下一页
    fun loadMore() {
        val current = _state.value
        if (current.loadingMore || current.finished || current.loading) return
        _state.value = current.copy(loadingMore = true)
        viewModelScope.coroutineScope.launch {
            try {
                page++
                val result = repo.getHomeArticles(page)
                if (result.isSuccess && result.data != null) {
                    val p = result.data
                    _state.value = current.copy(
                        articles = current.articles + p.datas,
                        loadingMore = false,
                        finished = p.over,
                    )
                } else {
                    page--
                    _state.value = current.copy(loadingMore = false)
                }
            } catch (e: Exception) {
                page--
                _state.value = current.copy(loadingMore = false)
            }
        }
    }
}
