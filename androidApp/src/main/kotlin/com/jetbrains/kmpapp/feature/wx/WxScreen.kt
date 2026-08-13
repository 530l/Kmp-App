package com.jetbrains.kmpapp.feature.wx

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.kmpapp.data.model.Chapter
import com.jetbrains.kmpapp.presentation.ChapterArticles
import com.jetbrains.kmpapp.presentation.WxViewModel
import com.jetbrains.kmpapp.ui.components.ArticleCard
import com.jetbrains.kmpapp.ui.components.EndFooter
import com.jetbrains.kmpapp.ui.components.ErrorState
import com.jetbrains.kmpapp.ui.components.LoadingFooter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WxScreen(
    viewModel: WxViewModel = koinViewModel(),
    onArticleClick: (String, String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val accounts = state.accounts

    val pagerState = rememberPagerState(initialPage = state.selectedIndex, pageCount = { accounts.size })
    val tabListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Pager 滑动 → 更新 ViewModel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page -> viewModel.selectIndex(page) }
    }

    // Bug 1 修复：Pager 切页时 Tab 条跟随滚动
    LaunchedEffect(pagerState.currentPage, accounts.size) {
        if (accounts.isNotEmpty()) {
            val currentPage = pagerState.currentPage
            val firstVisible = tabListState.firstVisibleItemIndex
            val lastVisible = tabListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: firstVisible
            if (currentPage < firstVisible || currentPage > lastVisible) {
                tabListState.animateScrollToItem(currentPage)
            }
        }
    }

    val onTabClick: (Int) -> Unit = { index ->
        if (index != pagerState.currentPage) {
            scope.launch { pagerState.animateScrollToPage(index) }
        }
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("公众号", style = MaterialTheme.typography.titleLarge) },
            modifier = Modifier.statusBarsPadding(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )

        if (accounts.isNotEmpty()) {
            WxAccountTabRow(
                accounts = accounts,
                selectedIndex = pagerState.currentPage,
                onSelect = onTabClick,
                listState = tabListState,
            )
        }

        if (accounts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val id = accounts[page].id
                val chapter = state.articlesMap[id] ?: ChapterArticles()
                WxChapterContent(
                    chapter = chapter,
                    onArticleClick = onArticleClick,
                    onLoadMore = { viewModel.loadMore() },
                    onRefresh = { viewModel.refreshCurrent() },
                )
            }
        }
    }
}

@Composable
private fun WxChapterContent(
    chapter: ChapterArticles,
    onArticleClick: (String, String) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged()
            .filter { it >= chapter.articles.size - 4 && !chapter.finished && !chapter.loading && !chapter.loadingMore }
            .collect { onLoadMore() }
    }

    PullToRefreshBox(
        isRefreshing = chapter.loading && chapter.articles.isNotEmpty(),
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
        when {
            chapter.loading && chapter.articles.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            chapter.error != null && chapter.articles.isEmpty() -> {
                ErrorState(chapter.error!!, onRetry = onRefresh)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                // 底部多留空间，防止最后一个 item 被底部导航栏遮挡
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(chapter.articles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        onClick = { onArticleClick(article.link, article.title) },
                    )
                }
                if (chapter.loadingMore) item { LoadingFooter() }
                if (chapter.finished) item { EndFooter() }
            }
        }
    }
}

@Composable
private fun WxAccountTabRow(
    accounts: List<Chapter>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState,
) {
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(accounts, key = { it.id }) { account ->
            val index = accounts.indexOf(account)
            val selected = index == selectedIndex
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .clickable { onSelect(index) },
            ) {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) Color.White
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }
    }
}
