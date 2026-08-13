package com.jetbrains.kmpapp.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.kmpapp.presentation.SearchViewModel
import com.jetbrains.kmpapp.ui.components.ArticleCard
import com.jetbrains.kmpapp.ui.components.EndFooter
import com.jetbrains.kmpapp.ui.components.EmptyState
import com.jetbrains.kmpapp.ui.components.LoadingFooter
import com.jetbrains.kmpapp.ui.components.LoadingState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onArticleClick: (String, String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged()
            .filter { it >= state.results.size - 4 && !state.finished && !state.loading && !state.loadingMore }
            .collect { viewModel.loadMore() }
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        // 搜索框
        OutlinedTextField(
            value = state.keyword,
            onValueChange = { viewModel.setKeyword(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("搜索文章…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        )

        if (!state.searched) {
            // 初始态：展示热搜词
            if (state.hotKeys.isNotEmpty()) {
                Text(
                    "热门搜索",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.hotKeys.forEach { hotKey ->
                        AssistChip(
                            onClick = { viewModel.search(hotKey.name) },
                            label = { Text(hotKey.name) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        )
                    }
                }
            }
        } else {
            // 搜索结果
            when {
                state.loading -> LoadingState()
                state.results.isEmpty() -> EmptyState("未找到「${state.keyword}」相关文章")
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                    state = listState,
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.results, key = { it.id }) { article ->
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
