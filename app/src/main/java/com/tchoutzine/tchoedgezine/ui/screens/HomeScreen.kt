package com.tchoutzine.tchoedgezine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchoutzine.tchoedgezine.ui.theme.*

@Composable
fun HomeScreen(
    onHealthClick: () -> Unit,
    onCropClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onChatbotClick: () -> Unit,
    onTelemedClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Scaffold(
        topBar = { TchoTopBar() },
        bottomBar = { TchoBottomNav(active = "home", onHomeClick = {}, onHistoryClick = onHistoryClick, onSettingsClick = onSettingsClick) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // Hero
            HeroSection()

            // Content
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    "MODULES DISPONIBLES",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                ModuleCard(
                    iconBg = ForestGreen,
                    icon = Icons.Outlined.LocalHospital,
                    title = "Diagnostic Santé",
                    subtitle = "MedGemma 4B · AfriMed-QA · 24 000 cas",
                    onClick = onHealthClick,
                )
                ModuleCard(
                    iconBg = CropGreen,
                    icon = Icons.Outlined.Grass,
                    title = "Détection Cultures",
                    subtitle = "MobileNetV5 · 38 maladies · Pan & Scan",
                    onClick = onCropClick,
                )

                // Voice button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.5.dp, ForestGreen, RoundedCornerShape(16.dp))
                        .clickable(onClick = onVoiceClick)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Mic, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Interface Vocale", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = ForestGreen)
                        Text("Wolof · Bambara · Fulfulde · Ewondo", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Outlined.ArrowForward, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                }

                // Chatbot + Télémédecine
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickActionButton(
                        icon    = Icons.Outlined.SmartToy,
                        label   = "Chatbot IA",
                        color   = MidGreen,
                        onClick = onChatbotClick,
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionButton(
                        icon    = Icons.Outlined.VideoCall,
                        label   = "Télémédecine",
                        color   = Accent,
                        onClick = onTelemedClick,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(4.dp))
                AIStatusBar()
            }
        }
    }
}

@Composable
private fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(ForestGreen, MidGreen, MaterialTheme.colorScheme.background)))
            .padding(horizontal = 20.dp)
            .padding(top = 4.dp, bottom = 24.dp),
    ) {
        Column {
            Text("Bonjour, Balbino", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Jeudi 22 mai 2026 · 09:41", fontSize = 12.sp, color = Color.White.copy(0.72f), modifier = Modifier.padding(top = 4.dp))
            Row(modifier = Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatChip(Icons.Outlined.CheckCircle, "Gemma 3n actif")
                StatChip(Icons.Outlined.Memory, "1.9 Go")
                StatChip(Icons.Outlined.Speed, "Prêt")
            }
        }
    }
}

@Composable
private fun TchoTopBar() {
    Surface(color = ForestGreen, shadowElevation = 0.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().height(56.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("TchoEdgeZine", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            OfflineBadge()
        }
    }
}

@Composable
fun TchoBottomNav(active: String, onHomeClick: () -> Unit, onHistoryClick: () -> Unit, onSettingsClick: () -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
        listOf(
            Triple("home",     Icons.Filled.Home,    "Accueil"),
            Triple("history",  Icons.Filled.History, "Historique"),
            Triple("settings", Icons.Filled.Settings,"Paramètres"),
        ).forEach { (id, icon, label) ->
            NavigationBarItem(
                selected = active == id,
                onClick  = { when (id) { "home" -> onHomeClick(); "history" -> onHistoryClick(); else -> onSettingsClick() } },
                icon     = { Icon(icon, contentDescription = label) },
                label    = { Text(label, fontSize = 11.sp) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = ForestGreen,
                    selectedTextColor   = ForestGreen,
                    indicatorColor      = ForestGreen.copy(0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
fun OfflineBadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .border(1.dp, Color.White.copy(0.4f), RoundedCornerShape(99.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(Icons.Outlined.WifiOff, contentDescription = null, tint = LightGreen, modifier = Modifier.size(13.dp))
        Text("Hors-ligne", color = LightGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AIStatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Navy)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LightGreen))
        Text("Modèle actif", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Text("·", color = Color.White.copy(0.4f), fontSize = 11.sp)
        Text("Gemma 3n E4B · INT4 · 1.9 Go", color = Color.White.copy(0.8f), fontSize = 11.sp, modifier = Modifier.weight(1f))
        Text("LiteRT", color = LightGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatChip(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(Color.White.copy(0.13f))
            .padding(horizontal = 11.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(0.12f))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}

@Composable
private fun ModuleCard(iconBg: Color, icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
