package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.Chapter
import com.jetbrains.kmpapp.data.repository.ArticleRepository
import com.jetbrains.kmpapp.data.repository.SystemRepository
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 公众号页状态：公众号列表 + 每个公众号的文章（各自独立，切 Pager 不丢数据）
data class WxState(
    val accounts: List<Chapter> = emptyList(),
    val selectedIndex: Int = 0,
    // key = 公众号 id，value = 该公众号的文章状态
    val articlesMap: Map<Int, ChapterArticles> = emptyMap(),
)

class WxViewModel(
    private val systemRepo: SystemRepository,
    private val articleRepo: ArticleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(WxState())
    val state: StateFlow<WxState> = _state.asStateFlow()

    // 每个公众号的当前页码（公众号文章页码从 1 开始）
    private val pageMap = mutableMapOf<Int, Int>()

    init {
        loadAccounts()
    }

    fun refresh() = loadAccounts()

    private fun loadAccounts() {
        viewModelScope.coroutineScope.launch {
            try {
                val result = systemRepo.getWxAccounts()
                if (result.isSuccess && result.data != null) {
                    val accounts = result.data
                    _state.value = WxState(accounts = accounts, selectedIndex = 0)
                    if (accounts.isNotEmpty()) loadArticles(accounts[0].id, reset = true)
                }
            } catch (e: Exception) {
                // 静默处理
            }
        }
    }

    // Pager 切页时调用
    fun selectIndex(index: Int) {
        val accounts = _state.value.accounts
        if (index !in accounts.indices) return
        _state.value = _state.value.copy(selectedIndex = index)
        val id = accounts[index].id
        if (!_state.value.articlesMap.containsKey(id)) {
            loadArticles(id, reset = true)
        }
    }

    // 下拉刷新当前公众号
    fun refreshCurrent() {
        val accounts = _state.value.accounts
        val index = _state.value.selectedIndex
        if (index in accounts.indices) {
            loadArticles(accounts[index].id, reset = true)
        }
    }

    // 加载某公众号的文章（公众号页码从 1 开始）
    private fun loadArticles(id: Int, reset: Boolean) {
        if (reset) pageMap[id] = 1
        val page = pageMap[id] ?: 1

        val currentChapter = _state.value.articlesMap[id] ?: ChapterArticles()
        _state.value = _state.value.copy(
            articlesMap = _state.value.articlesMap + (id to currentChapter.copy(
                loading = reset,
                loadingMore = !reset,
            )),
        )

        viewModelScope.coroutineScope.launch {
            try {
                val result = articleRepo.getWxArticles(id, page)
                val updated = if (result.isSuccess && result.data != null) {
                    val p = result.data
                    val list = if (reset) p.datas else currentChapter.articles + p.datas
                    ChapterArticles(
                        articles = list,
                        loading = false,
                        loadingMore = false,
                        finished = p.over,
                    )
                } else {
                    currentChapter.copy(loading = false, loadingMore = false)
                }
                _state.value = _state.value.copy(
                    articlesMap = _state.value.articlesMap + (id to updated),
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    articlesMap = _state.value.articlesMap + (id to currentChapter.copy(
                        loading = false,
                        loadingMore = false,
                    )),
                )
            }
        }
    }

    // 下一页（当前公众号）
    fun loadMore() {
        val accounts = _state.value.accounts
        val index = _state.value.selectedIndex
        if (index !in accounts.indices) return
        val id = accounts[index].id
        val chapter = _state.value.articlesMap[id] ?: return
        if (chapter.loadingMore || chapter.finished || chapter.loading) return
        pageMap[id] = (pageMap[id] ?: 1) + 1
        loadArticles(id, reset = false)
    }
}
