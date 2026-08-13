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

// 单个分类的文章状态
data class ChapterArticles(
    val articles: List<Article> = emptyList(),
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val finished: Boolean = false,
    val error: String? = null,
)

// 体系页状态：分类树 + 每个分类的文章列表（各自独立，切 Pager 不丢数据）
data class SystemState(
    val tree: List<Chapter> = emptyList(),
    val selectedIndex: Int = 0,
    // key = 分类 cid，value = 该分类的文章状态
    val articlesMap: Map<Int, ChapterArticles> = emptyMap(),
)

class SystemViewModel(
    private val systemRepo: SystemRepository,
    private val articleRepo: ArticleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SystemState())
    val state: StateFlow<SystemState> = _state.asStateFlow()

    // 每个分类的当前页码
    private val pageMap = mutableMapOf<Int, Int>()

    init {
        loadTree()
    }

    fun refresh() = loadTree()

    // 加载体系树，默认选中第一个叶子节点
    private fun loadTree() {
        viewModelScope.coroutineScope.launch {
            try {
                val result = systemRepo.getSystemTree()
                if (result.isSuccess && result.data != null) {
                    val tree = result.data
                    val leaves = tree.flatMap { it.children }
                    _state.value = SystemState(tree = tree, selectedIndex = 0)
                    if (leaves.isNotEmpty()) loadArticles(leaves[0].id, reset = true)
                }
            } catch (e: Exception) {
                // 静默处理，tree 为空时 UI 显示空态
            }
        }
    }

    // Pager 切页时调用
    fun selectIndex(index: Int) {
        val leaves = _state.value.tree.flatMap { it.children }
        if (index !in leaves.indices) return
        _state.value = _state.value.copy(selectedIndex = index)
        val cid = leaves[index].id
        // 没加载过才加载
        if (!_state.value.articlesMap.containsKey(cid)) {
            loadArticles(cid, reset = true)
        }
    }

    // 下拉刷新当前分类
    fun refreshCurrent() {
        val leaves = _state.value.tree.flatMap { it.children }
        val index = _state.value.selectedIndex
        if (index in leaves.indices) {
            loadArticles(leaves[index].id, reset = true)
        }
    }

    // 加载某分类的文章
    private fun loadArticles(cid: Int, reset: Boolean) {
        if (reset) pageMap[cid] = 0
        val page = pageMap[cid] ?: 0

        // 标记 loading
        val currentChapter = _state.value.articlesMap[cid] ?: ChapterArticles()
        _state.value = _state.value.copy(
            articlesMap = _state.value.articlesMap + (cid to currentChapter.copy(
                loading = reset,
                loadingMore = !reset,
            )),
        )

        viewModelScope.coroutineScope.launch {
            try {
                val result = articleRepo.getChapterArticles(page, cid)
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
                    articlesMap = _state.value.articlesMap + (cid to updated),
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    articlesMap = _state.value.articlesMap + (cid to currentChapter.copy(
                        loading = false,
                        loadingMore = false,
                    )),
                )
            }
        }
    }

    // 下一页（当前分类）
    fun loadMore() {
        val leaves = _state.value.tree.flatMap { it.children }
        val index = _state.value.selectedIndex
        if (index !in leaves.indices) return
        val cid = leaves[index].id
        val chapter = _state.value.articlesMap[cid] ?: return
        if (chapter.loadingMore || chapter.finished || chapter.loading) return
        pageMap[cid] = (pageMap[cid] ?: 0) + 1
        loadArticles(cid, reset = false)
    }
}
