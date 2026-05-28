package com.tchoutzine.tchoedgezine.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchoutzine.tchoedgezine.ui.theme.*

private data class Language(val code: String, val name: String, val subtitle: String, val colors: List<Color>)

private val languages = listOf(
    Language("fr", "Français",  "Interface complète en français",    listOf(Color(0xFF0055A4), Color.White, Color(0xFFEF4135))),
    Language("en", "English",   "Full English interface",             listOf(Color(0xFF1F8A5B), Color(0xFFF5FAF6))),
    Language("ff", "Fulfulde",  "Haala e Fulfulde — Kameruun",       listOf(Color(0xFF007A5E), Color(0xFFCE1126), Color(0xFFFCD116))),
)

@Composable
fun OnboardingScreen(onContinue: () -> Unit) {
    var selected by remember { mutableStateOf("fr") }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // Header gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(MidGreen, ForestGreen, Navy)))
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 28.dp),
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Translate, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
                Spacer(Modifier.height(18.dp))
                Text("Choisissez votre langue", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
                Spacer(Modifier.height(10.dp))
                Text("Choose your language",   color = Color.White.copy(0.72f), fontSize = 13.sp)
                Text("Siddigi wolde maa",       color = Color.White.copy(0.72f), fontSize = 13.sp)
            }
        }

        // Language cards
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            languages.forEach { lang ->
                LanguageCard(lang = lang, isSelected = selected == lang.code, onClick = { selected = lang.code })
            }
        }

        // CTA
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            ) {
                Icon(Icons.Outlined.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Continuer · Continue", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            TextButton(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text("Passer / Skip", color = ForestGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun LanguageCard(lang: Language, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) ForestGreen else MaterialTheme.colorScheme.outline
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Flag chip
        Row(
            modifier = Modifier.size(40.dp).clip(CircleShape),
        ) {
            lang.colors.forEach { c -> Box(modifier = Modifier.weight(1f).fillMaxHeight().background(c)) }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(lang.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(lang.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Radio indicator
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(ForestGreen))
            }
        }
    }
}
