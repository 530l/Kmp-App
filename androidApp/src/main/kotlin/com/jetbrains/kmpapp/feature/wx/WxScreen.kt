package com.jetbrains.kmpapp.feature.wx

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.kmpapp.presentation.WxViewModel
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
fun WxScreen(
    viewModel: WxViewModel = koinViewModel(),
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
        TopAppBar(
            title = { Text("公众号", style = MaterialTheme.typography.titleLarge) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White,
            ),
        )

        // 公众号列表作为可滑动 Tab
        if (state.accounts.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = state.accounts.indexOfFirst { it.id == state.selectedId }.coerceAtLeast(0),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 12.dp,
            ) {
                state.accounts.forEach { account ->
                    Tab(
                        selected = state.selectedId == account.id,
                        onClick = { viewModel.selectAccount(account.id) },
                        text = { Text(account.name, maxLines = 1, style = MaterialTheme.typography.labelSmall) },
                    )
                }
            }
        }

        when {
            state.loading -> LoadingState()
            state.error != null -> ErrorState(state.error!!, onRetry = { viewModel.refresh() })
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
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
