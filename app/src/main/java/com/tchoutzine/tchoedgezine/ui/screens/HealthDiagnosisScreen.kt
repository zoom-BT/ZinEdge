package com.tchoutzine.tchoedgezine.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.tchoutzine.tchoedgezine.ai.GemmaInference
import com.tchoutzine.tchoedgezine.data.model.DiagnosisResult
import com.tchoutzine.tchoedgezine.ui.theme.*
import kotlinx.coroutines.launch

private val allSymptoms = listOf(
    "Fièvre", "Fatigue", "Frissons", "Toux", "Céphalée",
    "Douleurs", "Nausées", "Vomissements", "Diarrhée", "Essoufflement",
)

@Composable
fun HealthDiagnosisScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var showChatbot by remember { mutableStateOf(false) }
    var diagnosisResult by remember { mutableStateOf<DiagnosisResult?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val ai = remember { GemmaInference.getInstance(context) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) capturedBitmap = bitmap
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    capturedBitmap = BitmapFactory.decodeStream(stream)
                }
            } catch (_: Exception) {}
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    fun analyze() {
        if (selectedSymptoms.isEmpty()) return
        isAnalyzing = true
        val imageDesc = if (capturedBitmap != null) "Photo de la zone symptomatique fournie par l'utilisateur" else null
        scope.launch {
            diagnosisResult = ai.diagnoseHealth(selectedSymptoms.toList(), imageDesc)
            isAnalyzing = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Surface(color = ForestGreen) {
                    Row(
                        modifier = Modifier.fillMaxWidth().statusBarsPadding().height(56.dp).padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Retour", tint = Color.White)
                        }
                        Text("Diagnostic Santé", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        LocalIABadge()
                        Spacer(Modifier.width(12.dp))
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            floatingActionButton = {
                if (diagnosisResult != null) {
                    ExtendedFloatingActionButton(
                        onClick = {},
                        modifier = Modifier.navigationBarsPadding(),
                        containerColor = ForestGreen,
                        contentColor = Color.White,
                        icon = { Icon(Icons.Filled.BookmarkAdd, null) },
                        text = { Text("Enregistrer", fontWeight = FontWeight.Bold) },
                    )
                }
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Capture zone with optional bitmap preview
                HealthCaptureZone(bitmap = capturedBitmap, hasResult = diagnosisResult != null)

                // Camera / Gallery chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {
                            val granted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) ==
                                android.content.pm.PackageManager.PERMISSION_GRANTED
                            if (granted) cameraLauncher.launch(null)
                            else cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        },
                        label = { Text("Nouvelle photo") },
                        leadingIcon = { Icon(Icons.Outlined.CameraAlt, null, Modifier.size(18.dp)) },
                        modifier = Modifier.weight(1f),
                    )
                    AssistChip(
                        onClick = { galleryLauncher.launch("image/*") },
                        label = { Text("Galerie") },
                        leadingIcon = { Icon(Icons.Outlined.PhotoLibrary, null, Modifier.size(18.dp)) },
                    )
                }

                // Symptom selection
                SymptomsCard(
                    selected = selectedSymptoms,
                    all = allSymptoms,
                    onToggle = { s ->
                        selectedSymptoms = if (s in selectedSymptoms) selectedSymptoms - s else selectedSymptoms + s
                    },
                )

                // Analyse button
                Button(
                    onClick = { analyze() },
                    enabled = selectedSymptoms.isNotEmpty() && !isAnalyzing,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(Modifier.width(10.dp))
                        Text("Analyse en cours…", fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(Icons.Outlined.Analytics, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Analyser", fontWeight = FontWeight.SemiBold)
                    }
                }

                // Result card (only when AI has responded)
                diagnosisResult?.let { result ->
                    HealthResultCard(result = result)
                }

                // Find clinic
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(99.dp),
                    border = BorderStroke(1.5.dp, ForestGreen),
                ) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Trouver un centre de santé", color = ForestGreen, fontWeight = FontWeight.SemiBold)
                }

                // Chat
                OutlinedButton(
                    onClick = { showChatbot = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(99.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Icon(Icons.Outlined.ChatBubble, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Poser une question à l'assistant")
                }

                Spacer(Modifier.height(80.dp))
            }
        }

        if (showChatbot) {
            ChatbotBottomSheet(onDismiss = { showChatbot = false })
        }
    }
}

@Composable
private fun HealthCaptureZone(bitmap: Bitmap?, hasResult: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.radialGradient(listOf(MidGreen, ForestGreen, Color(0xFF0B1D14)))),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.35f)))
        }

        CornerBrackets()

        if (hasResult) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(ForestGreen)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Text("Analyse terminée", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        } else if (bitmap == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = Color.White.copy(0.6f), modifier = Modifier.size(40.dp))
                Text("Photographier la zone symptomatique", color = Color.White.copy(0.6f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun CornerBrackets() {
    val color = LightGreen
    val stroke = 2.dp
    val len = 20.dp
    val inset = 12.dp
    Box(modifier = Modifier.fillMaxSize().padding(inset)) {
        Box(
            modifier = Modifier.size(len).align(Alignment.TopStart).border(
                BorderStroke(stroke, color),
                object : androidx.compose.ui.graphics.Shape {
                    override fun createOutline(
                        size: androidx.compose.ui.geometry.Size,
                        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
                        density: androidx.compose.ui.unit.Density,
                    ) = androidx.compose.ui.graphics.Outline.Rectangle(
                        androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height)
                    )
                },
            ),
        )
    }
}

@Composable
private fun SymptomsCard(selected: Set<String>, all: List<String>, onToggle: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Symptômes identifiés", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(all) { symptom ->
                    FilterChip(
                        selected = symptom in selected,
                        onClick = { onToggle(symptom) },
                        label = { Text(symptom, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForestGreen,
                            selectedLabelColor = Color.White,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthResultCard(result: DiagnosisResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Résultat ZinEdge",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                if (!result.usedOnlineModel) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(99.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text("Mode démo", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.width(6.dp))
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(ForestGreen).padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text("${result.confidence}% fiabilité", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(result.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (result.detail.isNotEmpty()) {
                    Text(result.detail, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontStyle = FontStyle.Italic)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Sévérité",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        when (result.severity) {
                            "low" -> "Légère"; "moderate" -> "Modérée"; "high" -> "Grave"; else -> result.severity
                        },
                        fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Accent,
                    )
                }
                SeverityBar(level = result.severity)
            }

            if (result.recommendations.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Recommandations thérapeutiques", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    result.recommendations.forEach { rec ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(width = 3.dp, height = 16.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(ForestGreen),
                            )
                            Text(rec, fontSize = 12.sp, lineHeight = 17.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeverityBar(level: String) {
    val segments = listOf("low" to Color(0xFF3FA678), "moderate" to Accent, "high" to ErrorRed)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        segments.forEach { (seg, color) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color.copy(alpha = if (seg == level) 1f else 0.22f)),
            )
        }
    }
}

@Composable
fun LocalIABadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(Color.White.copy(0.15f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Icon(Icons.Outlined.Bolt, contentDescription = null, tint = LightGreen, modifier = Modifier.size(13.dp))
        Text("LOCAL IA", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
    }
}

@Composable
fun ChatbotBottomSheet(onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.55f)).clickable(onClick = onDismiss)) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.64f)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(enabled = false) {},
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)),
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ChatBubble, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                    Text(
                        "Assistant ZinEdge",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f).padding(start = 10.dp),
                    )
                    IconButton(onClick = onDismiss) { Icon(Icons.Outlined.Close, contentDescription = "Fermer") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(ChipBgLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text("Gemma 3n · Hors-ligne", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ForestGreen)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(99.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text("Contexte santé", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            HorizontalDivider()

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 14.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Spacer(Modifier.height(4.dp))
                UserBubble("Quels sont les symptômes du paludisme sévère ?", "14:32")
                AIBubble(
                    "Le paludisme sévère se manifeste par : fièvre > 39 °C, confusion, convulsions, ictère, urines foncées, anémie sévère ou détresse respiratoire. C'est une urgence médicale.",
                    96, "14:32",
                )
                UserBubble("Y a-t-il un traitement sans ordonnance ?", "14:33")
            }

            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(3) {
                    Box(modifier = Modifier.size(5.dp).clip(RoundedCornerShape(99.dp)).background(ForestGreen.copy(0.5f)))
                }
                Text("ZinEdge rédige…", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            HorizontalDivider()
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(onClick = {}) { Icon(Icons.Outlined.Mic, contentDescription = "Vocal", tint = ForestGreen) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text("Posez votre question…", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(99.dp)).background(ForestGreen),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Send, contentDescription = "Envoyer", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun UserBubble(text: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                    .background(ForestGreen)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(text, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
            }
            Text(time, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AIBubble(text: String, confidence: Int, time: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(99.dp)).background(ForestGreen),
            contentAlignment = Alignment.Center,
        ) {
            Text("AI", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(text, fontSize = 13.sp, lineHeight = 19.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Text(time, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

