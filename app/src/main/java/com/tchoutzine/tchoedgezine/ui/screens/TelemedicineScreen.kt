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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun TelemedicineScreen(onBack: () -> Unit) {
    var isOnline by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(color = ForestGreen) {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().height(56.dp).padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, null, tint = Color.White)
                    }
                    Text(
                        "Télémédecine", color = Color.White,
                        fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    // Indicateur connexion
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color.White.copy(0.15f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(if (isOnline) LightGreen else ErrorRed))
                        Text(
                            if (isOnline) "En ligne" else "Hors-ligne",
                            color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Bannière hors-ligne
            if (!isOnline) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Accent.copy(0.12f))
                        .border(1.dp, Accent.copy(0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.WifiOff, null, tint = Accent, modifier = Modifier.size(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Connexion requise", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Accent)
                        Text(
                            "La télémédecine nécessite WiFi ou données mobiles. Les consultations enregistrées sont disponibles hors-ligne.",
                            fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp,
                        )
                    }
                }
            }

            // Consultation rapide
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(ForestGreen, MidGreen)))
                        .padding(20.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Consultation en ligne", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Connectez-vous à un professionnel de santé en moins de 15 minutes.", color = Color.White.copy(0.82f), fontSize = 12.sp, lineHeight = 17.sp)
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = {},
                            enabled = isOnline,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(99.dp),
                        ) {
                            Icon(Icons.Outlined.VideoCall, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Démarrer une consultation", color = ForestGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Services
            Text("SERVICES DISPONIBLES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            TelemedServiceCard(
                icon    = Icons.Outlined.MedicalServices,
                title   = "Avis médical",
                desc    = "Médecin généraliste · Réponse en 2h",
                status  = "En ligne",
                online  = isOnline,
            )
            TelemedServiceCard(
                icon    = Icons.Outlined.Vaccines,
                title   = "Conseil médicament",
                desc    = "Pharmacien certifié · Disponible 24h/7j",
                status  = "En ligne",
                online  = isOnline,
            )
            TelemedServiceCard(
                icon    = Icons.Outlined.Agriculture,
                title   = "Expert agricole",
                desc    = "Agronome · Diagnostic cultures avancé",
                status  = "Indisponible",
                online  = false,
            )

            // Historique consultations
            Text("DERNIÈRES CONSULTATIONS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            repeat(2) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .clickable {}
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(ForestGreen.copy(0.1f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Person, null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (i == 0) "Dr. Nguema · Médecin" else "Pharm. Bello", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(if (i == 0) "21 mai 2026 · Paludisme" else "18 mai 2026 · Prescription", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TelemedServiceCard(
    icon: ImageVector,
    title: String,
    desc: String,
    status: String,
    online: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable(enabled = online) {}
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp))
                .background(if (online) ForestGreen.copy(0.12f) else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = if (online) ForestGreen else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (online) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(99.dp))
                .background(if (online) LightGreen.copy(0.18f) else MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 9.dp, vertical = 4.dp),
        ) {
            Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (online) ForestGreen else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
