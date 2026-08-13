package com.jetbrains.kmpapp.presentation

import com.jetbrains.kmpapp.data.model.Article
import com.jetbrains.kmpapp.data.model.Chapter
import com.jetbrains.kmpapp.domain.usecase.GetChapterArticlesUseCase
import com.jetbrains.kmpapp.domain.usecase.GetSystemTreeUseCase
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 体系页状态：分类树 + 当前选中分类的文章列表
data class SystemState(
    val tree: List<Chapter> = emptyList(),
    val selectedChapter: Chapter? = null,
    val articles: List<Article> = emptyList(),
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val finished: Boolean = false,
    val error: String? = null,
)

class SystemViewModel(
    private val getSystemTree: GetSystemTreeUseCase,
    private val getChapterArticles: GetChapterArticlesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SystemState(loading = true))
    val state: StateFlow<SystemState> = _state.asStateFlow()

    private var page = 0

    init {
        loadTree()
    }

    // 重新加载体系树
    fun refresh() = loadTree()

    // 加载体系树，默认选中第一个叶子节点（二级分类）
    private fun loadTree() {
        viewModelScope.coroutineScope.launch {
            try {
                val result = getSystemTree()
                if (result.isSuccess && result.data != null) {
                    val tree = result.data
                    // 找第一个有 id 的二级分类作为默认选中
                    val firstLeaf = tree.firstNotNullOfOrNull { parent ->
                        parent.children.firstOrNull()
                    }
                    _state.value = SystemState(tree = tree, selectedChapter = firstLeaf)
                    if (firstLeaf != null) loadArticles(firstLeaf.id, reset = true)
                } else {
                    _state.value = SystemState(error = result.errorMsg ?: "加载失败")
                }
            } catch (e: Exception) {
                _state.value = SystemState(error = e.message ?: "网络异常")
            }
        }
    }

    // 切换分类
    fun selectChapter(chapter: Chapter) {
        if (_state.value.selectedChapter?.id == chapter.id) return
        _state.value = _state.value.copy(selectedChapter = chapter)
        loadArticles(chapter.id, reset = true)
    }

    // 加载某分类的文章，reset=true 表示从头开始（切换分类时）
    private fun loadArticles(cid: Int, reset: Boolean) {
        if (reset) page = 0
        val current = _state.value
        _state.value = if (reset) current.copy(loading = true) else current.copy(loadingMore = true)
        viewModelScope.coroutineScope.launch {
            try {
                val result = getChapterArticles(page, cid)
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
        val cid = _state.value.selectedChapter?.id ?: return
        val current = _state.value
        if (current.loadingMore || current.finished || current.loading) return
        page++
        loadArticles(cid, reset = false)
    }
}
