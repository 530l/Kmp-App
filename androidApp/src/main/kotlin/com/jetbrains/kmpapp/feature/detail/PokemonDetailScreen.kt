package com.jetbrains.kmpapp.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.kmpapp.data.model.Pokemon
import com.jetbrains.kmpapp.data.model.PokemonSpecies
import com.jetbrains.kmpapp.presentation.DetailViewModel
import com.jetbrains.kmpapp.ui.components.DetailHeader
import com.jetbrains.kmpapp.ui.components.SectionCard
import com.jetbrains.kmpapp.ui.components.StatBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    viewModel: DetailViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    LaunchedEffect(pokemonId) { viewModel.setId(pokemonId) }
    val pokemon by viewModel.pokemon.collectAsStateWithLifecycle()
    val species by viewModel.species.collectAsStateWithLifecycle()
    val p = pokemon

    if (p == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            DetailHeader(
                name = p.name,
                artworkUrl = artworkUrl(p.id),
                types = p.types.map { it.type.name },
                onBack = onBack,
                sharedId = p.id,
            )
        }
        // 内容区：圆角从 header 下沿盖上来，像底部 sheet 上拉
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .offset(y = (-28).dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 20.dp),
            ) {
                StatsBlock(p)
                species?.let { InfoBlock(species = it, fallbackName = p.name) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatsBlock(p: Pokemon) {
    SectionCard(title = "基础能力", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        p.stats.forEach { st ->
            StatBar(label = st.stat.name, value = st.baseStat)
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "身高 ${p.height / 10f} m · 体重 ${p.weight / 10f} kg",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun InfoBlock(species: PokemonSpecies, fallbackName: String) {
    // 优先中文名和简介，没匹配到就回退英文
    val name = species.names.firstOrNull { it.language.name == "zh-Hans" }?.name
        ?: species.names.firstOrNull { it.language.name == "en" }?.name
        ?: fallbackName
    val genus = species.genera.firstOrNull { it.language.name == "zh-Hans" }?.genus
        ?: species.genera.firstOrNull { it.language.name == "en" }?.genus
    val flavor = species.flavorTextEntries.firstOrNull { it.language.name == "zh-Hans" }?.flavorText
        ?: species.flavorTextEntries.firstOrNull { it.language.name == "en" }?.flavorText

    SectionCard(title = "图鉴资料", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        genus?.let { Text("分类：$it", style = MaterialTheme.typography.bodyMedium) }
        flavor?.let {
            Text(it.replace('\n', ' '), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// 官方美术图地址有规律，列表和详情都能直接拼
private fun artworkUrl(id: Int) =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
