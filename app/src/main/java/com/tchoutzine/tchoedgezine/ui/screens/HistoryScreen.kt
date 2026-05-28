package com.tchoutzine.tchoedgezine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchoutzine.tchoedgezine.ui.theme.*

private data class HistoryEntry(val type: String, val title: String, val date: String, val confidence: Int)

private val sampleHistory = listOf(
    HistoryEntry("health", "Paludisme P. falciparum", "Hier, 14:32",   94),
    HistoryEntry("agri",   "Brûlure du maïs",         "20 mai 2026",   91),
    HistoryEntry("health", "Anémie ferriprive",        "18 mai 2026",   87),
    HistoryEntry("agri",   "Mildiou de la tomate",     "15 mai 2026",   89),
    HistoryEntry("health", "Infection respiratoire",   "12 mai 2026",   82),
)

@Composable
fun HistoryScreen(onBack: () -> Unit, onHomeClick: () -> Unit, onSettingsClick: () -> Unit) {
    var filter by remember { mutableStateOf("Tout") }
    val filters = listOf("Tout", "Santé", "Agriculture", "Cette semaine")

    Scaffold(
        topBar = {
            Surface(color = ForestGreen) {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().height(56.dp).padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Historique", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    OfflineBadge()
                }
            }
        },
        bottomBar = {
            TchoBottomNav(active = "history", onHomeClick = onHomeClick, onHistoryClick = {}, onSettingsClick = onSettingsClick)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {},
                containerColor = ForestGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("Nouvelle consultation", fontWeight = FontWeight.Bold) },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(Icons.Outlined.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    Text("Rechercher un diagnostic…", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.Tune, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filters) { f ->
                        FilterChip(
                            selected = filter == f,
                            onClick = { filter = f },
                            label = { Text(f) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                selectedLabelColor = Color.White,
                            ),
                        )
                    }
                }
            }

            val displayed = sampleHistory.filter { entry ->
                when (filter) {
                    "Santé"       -> entry.type == "health"
                    "Agriculture" -> entry.type == "agri"
                    else          -> true
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
            ) {
                items(displayed) { entry -> HistoryRow(entry) }
            }
        }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry) {
    val isHealth = entry.type == "health"
    val confColor = when {
        entry.confidence >= 90 -> ForestGreen
        entry.confidence >= 85 -> Accent
        else                   -> ErrorRed
    }
    val confBg = when {
        entry.confidence >= 90 -> ChipBgLight
        entry.confidence >= 85 -> AccentDark.copy(0.15f)
        else                   -> ErrorRed.copy(0.1f)
    }

    Row(
        modifier = Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(if (isHealth) ForestGreen else CropGreen),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (isHealth) Icons.Outlined.LocalHospital else Icons.Outlined.Grass,
                null, tint = Color.White, modifier = Modifier.size(22.dp),
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(entry.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(entry.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(
            modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(confBg).padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
            Text("${entry.confidence}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = confColor)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
}
