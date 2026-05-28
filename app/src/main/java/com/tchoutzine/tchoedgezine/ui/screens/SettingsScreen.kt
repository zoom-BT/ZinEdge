package com.tchoutzine.tchoedgezine.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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

@Composable
fun SettingsScreen(onBack: () -> Unit, onHomeClick: () -> Unit, onHistoryClick: () -> Unit) {
    var darkTheme by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(color = ForestGreen) {
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().height(56.dp).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Paramètres", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    OfflineBadge()
                }
            }
        },
        bottomBar = { TchoBottomNav(active = "settings", onHomeClick = onHomeClick, onHistoryClick = onHistoryClick, onSettingsClick = {}) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Profile card
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(ForestGreen).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Navy), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Person, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Utilisateur ZinEdge", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Appuie pour modifier ton profil", color = Color.White.copy(0.78f), fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                }
                Icon(Icons.Outlined.Edit, null, tint = Color.White.copy(0.85f), modifier = Modifier.size(20.dp))
            }

            SettingsGroup(label = "LANGUE") {
                SettingsRow(Icons.Outlined.Translate, "Langue de l'interface", "Français", onClick = {})
            }
            SettingsGroup(label = "MODÈLE IA") {
                SettingsRow(Icons.Outlined.Memory, "Modèle actif", "Gemma 3n E4B · 1.9 Go INT4", onClick = {})
                HorizontalDivider(modifier = Modifier.padding(start = 62.dp))
                SettingsRow(Icons.Outlined.Info, "Runtime", "LiteRT · NPU optimisé", showChevron = false, onClick = {})
            }
            SettingsGroup(label = "APPARENCE") {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(ChipBgLight), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.DarkMode, null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Thème", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(if (darkTheme) "Sombre" else "Clair", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    // TODO: wire to app-level theme state
                    Switch(checked = darkTheme, onCheckedChange = { darkTheme = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ForestGreen))
                }
            }
            SettingsGroup(label = "DONNÉES") {
                SettingsRow(Icons.Outlined.Delete, "Effacer l'historique", null, color = ErrorRed, onClick = {})
                HorizontalDivider(modifier = Modifier.padding(start = 62.dp))
                SettingsRow(Icons.Outlined.Upload, "Exporter les données (JSON)", null, onClick = {})
            }
            SettingsGroup(label = "CONFIDENTIALITÉ") {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(ChipBgLight), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Lock, null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Traitement 100% local", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Aucune donnée n'est envoyée à des serveurs externes.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp, modifier = Modifier.padding(top = 6.dp))
                    }
                    Switch(checked = true, onCheckedChange = {}, enabled = false, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ForestGreen))
                }
            }
            SettingsGroup(label = "À PROPOS") {
                SettingsRow(Icons.Outlined.Info, "ZinEdge v1.0.0", null, showChevron = false, onClick = {})
                HorizontalDivider(modifier = Modifier.padding(start = 62.dp))
                SettingsRow(Icons.Outlined.Code, "ZinEdge · v1.0.0", null, showChevron = false, onClick = {})
                HorizontalDivider(modifier = Modifier.padding(start = 62.dp))
                SettingsRow(Icons.Outlined.OpenInNew, "zoxbt.is-a.dev", null, color = ForestGreen, onClick = {})
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SettingsGroup(label: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.6.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), modifier = Modifier.fillMaxWidth()) {
            Column { content() }
        }
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, value: String?, color: Color = MaterialTheme.colorScheme.onSurface, showChevron: Boolean = true, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(ChipBgLight), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = if (color == ErrorRed) ErrorRed else ForestGreen, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = color)
            if (value != null) Text(value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (showChevron) Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}
