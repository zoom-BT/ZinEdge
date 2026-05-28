package com.tchoutzine.tchoedgezine.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchoutzine.tchoedgezine.ai.GemmaInference
import com.tchoutzine.tchoedgezine.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onReady: () -> Unit) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var stage    by remember { mutableStateOf("Initialisationâ€¦") }
    var progress by remember { mutableFloatStateOf(0f) }
    var error    by remember { mutableStateOf<String?>(null) }
    var modelMissing by remember { mutableStateOf(false) }
    var copyPath by remember { mutableStateOf("") }
    var checkKey by remember { mutableIntStateOf(0) }  // incrÃ©mentÃ© pour re-vÃ©rifier

    val animProgress by animateFloatAsState(
        targetValue  = progress,
        animationSpec = tween(300, easing = LinearEasing),
        label = "loadProgress",
    )

    LaunchedEffect(checkKey) {
        modelMissing = false
        error = null
        stage = "Initialisationâ€¦"
        progress = 0f

        scope.launch {
            val ai = GemmaInference.getInstance(context)
            copyPath = ai.externalModelDir()?.absolutePath ?: ""

            val prepared = ai.prepareModel { s, pct ->
                stage = s
                if (pct >= 0) progress = pct / 100f
            }

            if (!prepared) {
                modelMissing = true
                return@launch
            }

            stage = "Chargement Gemma 3n E4Bâ€¦"
            progress = 0.6f
            ai.initialize(
                onProgress = { s, pct -> stage = s; if (pct >= 0) progress = 0.6f + pct * 0.004f },
                onReady    = { progress = 1f; stage = "PrÃªt" },
                onError    = { msg -> error = msg },
            )

            kotlinx.coroutines.delay(600)
            onReady()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Navy),
        contentAlignment = Alignment.Center,
    ) {
        if (modelMissing) {
            // â”€â”€ Ã‰cran d'installation du modÃ¨le â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
            AnimatedVisibility(visible = true, enter = fadeIn(tween(400))) {
                ModelInstallScreen(
                    copyPath  = copyPath,
                    onDemo    = onReady,
                    onRecheck = { checkKey++ },
                )
            }
        } else {
            // â”€â”€ Ã‰cran de chargement normal â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 36.dp),
            ) {
                // Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Brush.linearGradient(listOf(MidGreen, ForestGreen))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Psychology, null,
                        tint = Color.White, modifier = Modifier.size(48.dp),
                    )
                }

                Spacer(Modifier.height(32.dp))
                Text(
                    "ZinEdge", color = Color.White,
                    fontSize = 34.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                )
                Text(
                    "IA hors-ligne Â· SantÃ© & Agriculture",
                    color = LightGreen, fontSize = 13.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Spacer(Modifier.height(56.dp))

                // Barre de progression
                Column(modifier = Modifier.width(240.dp), horizontalAlignment = Alignment.Start) {
                    Text(
                        stage,
                        color = if (error != null) ErrorRed else LightGreen,
                        fontSize = 12.sp, fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { animProgress },
                            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(99.dp)),
                            color = if (error != null) ErrorRed else ForestGreen,
                            trackColor = Color.White.copy(alpha = 0.1f),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "${(animProgress * 100).toInt()}%",
                            color = Color.White.copy(0.6f), fontSize = 12.sp,
                        )
                    }
                }

                if (error != null) {
                    Spacer(Modifier.height(16.dp))
                    Text("Mode dÃ©mo activÃ©", color = Color.White.copy(0.55f), fontSize = 11.sp)
                }
            }
        }

        // Badge hors-ligne
        Row(
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(Icons.Outlined.WifiOff, null, tint = Color.White.copy(0.5f), modifier = Modifier.size(13.dp))
            Text("Sans connexion", color = Color.White.copy(0.5f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun ModelInstallScreen(
    copyPath: String,
    onDemo: () -> Unit,
    onRecheck: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // IcÃ´ne
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(0.07f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.DownloadForOffline, null, tint = LightGreen, modifier = Modifier.size(34.dp))
        }

        Text(
            "ModÃ¨le IA non installÃ©",
            color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
        )
        Text(
            "Gemma 3n E4B (4,9 Go) est requis pour le diagnostic.\nCopie le fichier via USB.",
            color = Color.White.copy(0.65f), fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        // Ã‰tapes
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InstallStep(
                num = "1",
                text = "TÃ©lÃ©charge le modÃ¨le sur PC :",
                code = "gemma-3n-E4B-it-int4.litertlm",
            )
            InstallStep(
                num = "2",
                text = "Connecte le tÃ©lÃ©phone via USB, puis copie vers :",
                code = if (copyPath.isNotEmpty()) copyPath else "Android/data/com.tchoutzine.tchoedgezine/files/models/",
            )
            InstallStep(
                num = "3",
                text = "Appuie sur Â« VÃ©rifier Â» ci-dessous.",
                code = null,
            )
        }

        // Commande de tÃ©lÃ©chargement
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(0.06f))
                .border(1.dp, Color.White.copy(0.12f), RoundedCornerShape(10.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("Commande PowerShell (PC):", color = LightGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "huggingface-cli download google/gemma-3n-E4B-it-litert-lm gemma-3n-E4B-it-int4.litertlm --local-dir .\\models",
                color = Color.White.copy(0.8f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 15.sp,
            )
        }

        // Boutons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = onDemo,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(99.dp),
                border = BorderStroke(1.dp, Color.White.copy(0.25f)),
            ) {
                Text("Mode dÃ©mo", color = Color.White.copy(0.7f), fontSize = 13.sp)
            }
            Button(
                onClick = onRecheck,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            ) {
                Icon(Icons.Outlined.Refresh, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("VÃ©rifier", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun InstallStep(num: String, text: String, code: String?) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(ForestGreen),
            contentAlignment = Alignment.Center,
        ) {
            Text(num, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text, color = Color.White.copy(0.8f), fontSize = 12.sp, lineHeight = 17.sp)
            if (code != null) {
                Text(
                    code,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(0.08f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = LightGreen,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}
