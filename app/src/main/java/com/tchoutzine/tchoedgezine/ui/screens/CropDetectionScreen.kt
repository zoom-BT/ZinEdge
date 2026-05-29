package com.tchoutzine.tchoedgezine.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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

private val crops = listOf("Maïs", "Tomate", "Arachide", "Sorgho")

@Composable
fun CropDetectionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedCrop by remember { mutableStateOf("Maïs") }
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
        isAnalyzing = true
        val imageDesc = if (capturedBitmap != null) "Photo de la culture fournie par l'utilisateur" else null
        scope.launch {
            diagnosisResult = ai.detectCrop(selectedCrop, imageDesc)
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
                        IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, null, tint = Color.White) }
                        Text("Cultures & Maladies", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        LocalIABadge()
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
                    .padding(14.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Crop selector
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "CULTURE ANALYSÉE",
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            crops.forEach { crop ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedCrop == crop) ForestGreen else Color.Transparent)
                                        .border(1.5.dp, if (selectedCrop == crop) ForestGreen else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                        .clickable { selectedCrop = crop; diagnosisResult = null },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        crop,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (selectedCrop == crop) Color.White else MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }
                }

                // Capture zone
                CropCaptureZone(bitmap = capturedBitmap, hasResult = diagnosisResult != null)

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

                // Analyse button
                Button(
                    onClick = { analyze() },
                    enabled = !isAnalyzing,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CropGreen),
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(Modifier.width(10.dp))
                        Text("Analyse en cours…", fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(Icons.Outlined.Biotech, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Analyser la culture", fontWeight = FontWeight.SemiBold)
                    }
                }

                // Dynamic disease card
                diagnosisResult?.let { result ->
                    CropDiseaseCard(result = result)
                    CropTreatmentCard(result = result)
                    CropYieldBanner(result = result)
                }

                // Chat
                OutlinedButton(
                    onClick = { showChatbot = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(99.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                ) {
                    Icon(Icons.Outlined.ChatBubble, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Poser une question sur cette maladie")
                }

                Spacer(Modifier.height(20.dp))
            }
        }

        if (showChatbot) ChatbotBottomSheet(onDismiss = { showChatbot = false })
    }
}

@Composable
private fun CropCaptureZone(bitmap: Bitmap?, hasResult: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.radialGradient(listOf(Color(0xFF2D5A1E), Color(0xFF1A3A14), Color(0xFF0A1809)))),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.4f)))
        }

        if (hasResult) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(ForestGreen)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Outlined.CheckCircle, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Text("Analyse terminée", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else if (bitmap == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.Grass, null, tint = Color.White.copy(0.6f), modifier = Modifier.size(40.dp))
                Text("Photographier la culture à analyser", color = Color.White.copy(0.6f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun CropDiseaseCard(result: DiagnosisResult) {
    val severityColor = when (result.severity) {
        "high" -> ErrorRed; "moderate" -> Accent; else -> ForestGreen
    }
    val severityLabel = when (result.severity) {
        "low" -> "Légère"; "moderate" -> "Modérée"; "high" -> "Grave"; else -> result.severity
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(result.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
                    if (result.detail.isNotEmpty()) {
                        Text(
                            result.detail,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(top = 3.dp),
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(severityColor.copy(0.15f))
                            .padding(horizontal = 9.dp, vertical = 4.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Outlined.Warning, null, tint = severityColor, modifier = Modifier.size(13.dp))
                            Text(severityLabel, color = severityColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (!result.usedOnlineModel) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(99.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text("Mode démo", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Fiabilité",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text("${result.confidence}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            }
            SeverityBar(level = result.severity)
        }
    }
}

@Composable
private fun CropTreatmentCard(result: DiagnosisResult) {
    if (result.recommendations.isEmpty()) return
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(20.dp).clip(RoundedCornerShape(6.dp)).background(ForestGreen),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.TaskAlt, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
                Text("Plan de traitement", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            result.recommendations.forEachIndexed { i, step ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(RoundedCornerShape(99.dp)).background(ChipBgLight),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${i + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    }
                    Text(step, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun CropYieldBanner(result: DiagnosisResult) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Navy).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "PERTE DE RENDEMENT ÉVITÉE",
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(0.6f),
            )
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("+30%", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = LightGreen, letterSpacing = (-0.5).sp)
                Text("si traitement dans 48h", fontSize = 11.sp, color = Color.White.copy(0.55f), modifier = Modifier.padding(bottom = 4.dp))
            }
        }
        OutlinedButton(
            onClick = {},
            shape = RoundedCornerShape(99.dp),
            border = BorderStroke(1.5.dp, Color.White.copy(0.6f)),
        ) {
            Icon(Icons.Filled.BookmarkAdd, null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Enregistrer", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
