package com.jetbrains.kmpapp.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.kmpapp.presentation.HomeViewModel
import com.jetbrains.kmpapp.ui.components.ArticleCard
import com.jetbrains.kmpapp.ui.components.EndFooter
import com.jetbrains.kmpapp.ui.components.ErrorState
import com.jetbrains.kmpapp.ui.components.LoadingFooter
import com.jetbrains.kmpapp.ui.components.LoadingState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onArticleClick: (String, String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged()
            .filter { it >= state.articles.size - 4 && !state.finished && !state.loading && !state.loadingMore }
            .collect { viewModel.loadMore() }
    }

    Column(Modifier.fillMaxSize()) {
        // 白色标题栏，statusBarsPadding 避开状态栏
        TopAppBar(
            title = { Text("玩 Android", style = MaterialTheme.typography.titleLarge) },
            modifier = Modifier.statusBarsPadding(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )

        when {
            state.loading && state.articles.isEmpty() -> LoadingState()
            state.error != null && state.articles.isEmpty() -> ErrorState(state.error!!, onRetry = { viewModel.refresh() })
            else -> PullToRefreshBox(
                isRefreshing = state.loading && state.articles.isNotEmpty(),
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                if (state.topArticles.isNotEmpty()) {
                    items(state.topArticles, key = { "top-${it.id}" }) { article ->
                        ArticleCard(
                            article = article,
                            onClick = { onArticleClick(article.link, article.title) },
                        )
                    }
                }
                items(state.articles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        onClick = { onArticleClick(article.link, article.title) },
                    )
                }
                if (state.loadingMore) item { LoadingFooter() }
                if (state.finished) item { EndFooter() }
                }
            }
        }
    }
}
