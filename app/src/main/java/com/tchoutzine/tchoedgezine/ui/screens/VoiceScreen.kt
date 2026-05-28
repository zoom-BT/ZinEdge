package com.tchoutzine.tchoedgezine.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchoutzine.tchoedgezine.ui.theme.*

private val voiceLanguages = listOf("Français", "English", "Fulfulde", "Ewondo")

@Composable
fun VoiceScreen(onClose: () -> Unit) {
    var selectedLang by remember { mutableStateOf("Fulfulde") }
    // Correction 7 : défaut false, l'utilisateur doit appuyer pour commencer
    var isListening by remember { mutableStateOf(false) }

    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOut), RepeatMode.Reverse),
        label = "micScale",
    )

    // Correction 5 : animation alpha pour les 3 dots (décalage de 200ms chacun)
    val dotTransition = rememberInfiniteTransition(label = "dots")
    val dotAlpha0 by dotTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600, easing = EaseInOut),
            RepeatMode.Reverse,
            initialStartOffset = StartOffset(0)
        ),
        label = "dot0",
    )
    val dotAlpha1 by dotTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600, easing = EaseInOut),
            RepeatMode.Reverse,
            initialStartOffset = StartOffset(200)
        ),
        label = "dot1",
    )
    val dotAlpha2 by dotTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600, easing = EaseInOut),
            RepeatMode.Reverse,
            initialStartOffset = StartOffset(400)
        ),
        label = "dot2",
    )

    Box(
        modifier = Modifier.fillMaxSize().background(Navy),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Status bar space
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

            // Top bar
            Row(modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) { Icon(Icons.Outlined.Close, null, tint = Color.White) }
                Text("Interface Vocale", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Row(
                    modifier = Modifier.padding(end = 8.dp).clip(RoundedCornerShape(99.dp)).border(1.dp, LightGreen.copy(0.55f), RoundedCornerShape(99.dp)).padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(Icons.Outlined.Translate, null, tint = LightGreen.copy(0.8f), modifier = Modifier.size(13.dp))
                    Text(selectedLang, color = LightGreen.copy(0.8f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Visualizer
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    // Concentric rings + mic
                    Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
                        listOf(260.dp to 0.06f, 210.dp to 0.12f, 162.dp to 0.20f, 124.dp to 0.30f).forEach { (d, opacity) ->
                            Box(modifier = Modifier.size(d).clip(CircleShape).border(2.dp, LightGreen.copy(opacity), CircleShape))
                        }
                        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(ForestGreen).scale(if (isListening) scale else 1f))
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape).background(MidGreen),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.Mic, null, tint = Color.White, modifier = Modifier.size(40.dp))
                        }
                    }

                    // Status
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("En écoute", color = Color.White.copy(0.75f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        // Correction 5 : dots animés avec alpha oscillant
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(dotAlpha0, dotAlpha1, dotAlpha2).forEach { alpha ->
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(LightGreen.copy(alpha)))
                            }
                        }
                    }

                    // Language chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        voiceLanguages.forEach { lang ->
                            val active = lang == selectedLang
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(if (active) LightGreen else Color.White.copy(0.08f))
                                    .clickable { selectedLang = lang }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                if (active) Icon(Icons.Outlined.Check, null, tint = Navy, modifier = Modifier.size(14.dp))
                                Text(lang, color = if (active) Navy else Color.White.copy(0.65f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Transcription card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Accent))
                    Text("TRANSCRIPTION EN COURS", fontSize = 10.sp, letterSpacing = 0.6.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                // Correction 6 : couleur explicite onSurface pour la visibilité
                Text("Maa yiɗi anndude ko woni harallere…", fontSize = 15.sp, fontWeight = FontWeight.Medium, lineHeight = 21.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("« Je veux savoir ce qu'est le paludisme… »", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontStyle = FontStyle.Italic)

                HorizontalDivider(color = ForestGreen.copy(0.4f))

                // Correction 1 : RÉPONSE ZINEDGE (au lieu de TCHOEDGEZINE)
                Text("RÉPONSE ZINEDGE", fontSize = 10.sp, letterSpacing = 0.6.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                // Correction 6 : couleur explicite onSurface
                Text("Le paludisme est une maladie causée par un parasite transmis par les moustiques anophèles…", fontSize = 13.sp, lineHeight = 19.sp, color = MaterialTheme.colorScheme.onSurface)

                Row(
                    modifier = Modifier.clip(RoundedCornerShape(99.dp)).border(1.5.dp, ForestGreen, RoundedCornerShape(99.dp)).padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(Icons.Outlined.VolumeUp, null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                    Text("Écouter", color = ForestGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth().background(Navy).padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Correction 2 : bouton Arrêter avec clickable
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .border(1.5.dp, Color.White.copy(0.5f), RoundedCornerShape(99.dp))
                        .clickable { isListening = false }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(Icons.Outlined.Stop, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("Arrêter", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Correction 3 : grand bouton Mic avec clickable toggle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(ForestGreen)
                        .clickable { isListening = !isListening },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Mic, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }

                // Correction 4 : bouton Envoyer avec clickable (placeholder vide)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .border(1.5.dp, Color.White.copy(0.5f), RoundedCornerShape(99.dp))
                        .clickable { }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(Icons.Outlined.Send, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text("Envoyer", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
