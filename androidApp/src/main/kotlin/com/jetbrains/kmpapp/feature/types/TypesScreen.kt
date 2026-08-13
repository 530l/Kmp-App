package com.jetbrains.kmpapp.feature.types

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.kmpapp.data.model.NamedApiResource
import com.jetbrains.kmpapp.data.model.id
import com.jetbrains.kmpapp.presentation.TypesViewModel
import com.jetbrains.kmpapp.ui.theme.colorForType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TypesScreen(
    viewModel: TypesViewModel = koinViewModel(),
    onTypeClick: (Int) -> Unit,
) {
    val types by viewModel.types.collectAsStateWithLifecycle()
    LazyColumn(Modifier.statusBarsPadding()) {
        items(types, key = { it.name }) { res ->
            TypeRow(res = res, onClick = { onTypeClick(res.id) })
            HorizontalDivider()
        }
    }
}

@Composable
private fun TypeRow(res: NamedApiResource, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(colorForType(res.name)),
        )
        Text(
            res.name.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}
