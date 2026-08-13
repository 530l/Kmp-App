package com.jetbrains.kmpapp.feature.system

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.jetbrains.kmpapp.data.model.Chapter
import com.jetbrains.kmpapp.presentation.SystemViewModel
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
fun SystemScreen(
    viewModel: SystemViewModel = koinViewModel(),
    onArticleClick: (String, String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // 所有二级分类（叶子节点）扁平化，供 Tab 展示
    val leaves: List<Chapter> = state.tree.flatMap { it.children }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged()
            .filter { it >= state.articles.size - 4 && !state.finished && !state.loading && !state.loadingMore }
            .collect { viewModel.loadMore() }
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("体系", style = MaterialTheme.typography.titleLarge) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White,
            ),
        )

        // 二级分类 Tab 横滑
        if (leaves.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = leaves.indexOfFirst { it.id == state.selectedChapter?.id }.coerceAtLeast(0),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 12.dp,
            ) {
                leaves.forEachIndexed { index, chapter ->
                    Tab(
                        selected = state.selectedChapter?.id == chapter.id,
                        onClick = { viewModel.selectChapter(chapter) },
                        text = { Text(chapter.name, maxLines = 1, style = MaterialTheme.typography.labelSmall) },
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
