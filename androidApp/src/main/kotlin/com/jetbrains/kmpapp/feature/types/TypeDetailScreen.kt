package com.jetbrains.kmpapp.feature.types

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.kmpapp.data.model.NamedApiResource
import com.jetbrains.kmpapp.presentation.TypeDetailViewModel
import com.jetbrains.kmpapp.ui.components.DetailHeader
import com.jetbrains.kmpapp.ui.components.SectionCard
import com.jetbrains.kmpapp.ui.components.TypeChip
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TypeDetailScreen(
    typeId: Int,
    viewModel: TypeDetailViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    LaunchedEffect(typeId) { viewModel.setId(typeId) }
    val detail by viewModel.typeDetail.collectAsStateWithLifecycle()
    val d = detail

    if (d == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            DetailHeader(
                name = d.name,
                artworkUrl = null,
                types = listOf(d.name),
                onBack = onBack,
            )
        }
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .offset(y = (-28).dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                RelationSection("攻击时双倍伤害", d.damageRelations.doubleDamageTo)
                RelationSection("攻击时伤害减半", d.damageRelations.halfDamageTo)
                RelationSection("攻击时无效", d.damageRelations.noDamageTo)
                RelationSection("受到双倍伤害", d.damageRelations.doubleDamageFrom)
                RelationSection("受到伤害减半", d.damageRelations.halfDamageFrom)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RelationSection(title: String, members: List<NamedApiResource>) {
    SectionCard(title = title) {
        if (members.isEmpty()) {
            Text("—", style = MaterialTheme.typography.bodyMedium)
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                members.forEach { res -> TypeChip(type = res.name) }
            }
        }
    }
}
