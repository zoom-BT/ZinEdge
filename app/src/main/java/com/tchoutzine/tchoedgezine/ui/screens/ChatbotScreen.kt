package com.tchoutzine.tchoedgezine.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tchoutzine.tchoedgezine.ai.GemmaInference
import com.tchoutzine.tchoedgezine.ui.theme.*
import kotlinx.coroutines.launch

private data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun ChatbotScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    val scroll  = rememberScrollState()

    var inputText by remember { mutableStateOf("") }
    var isTyping  by remember { mutableStateOf(false) }
    val messages  = remember {
        mutableStateListOf(
            ChatMessage(
                "Bonjour ! Je suis l'assistant ZinEdge, votre aide médicale hors-ligne. Comment puis-je vous aider aujourd'hui ?",
                isUser = false,
            )
        )
    }

    val ai = remember { GemmaInference.getInstance(context) }

    fun sendMessage() {
        val text = inputText.trim()
        if (text.isEmpty()) return
        inputText = ""
        messages.add(ChatMessage(text, isUser = true))
        isTyping = true
        scope.launch {
            val response = ai.chat(text)
            messages.add(ChatMessage(response, isUser = false))
            isTyping = false
            scroll.animateScrollTo(scroll.maxValue)
        }
    }

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
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Assistant ZinEdge", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("Gemma 3n · Hors-ligne", color = Color.White.copy(0.7f), fontSize = 11.sp)
                    }
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
                .imePadding(),
        ) {
            // Contexte chips
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ContextChip("Santé générale", true)
                ContextChip("Agriculture", false)
                ContextChip("Médication", false)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.5f))

            // Messages
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scroll)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Spacer(Modifier.height(4.dp))
                messages.forEach { msg ->
                    if (msg.isUser) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                                    .background(ForestGreen)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                            ) {
                                Text(msg.text, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                            }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(CircleShape).background(ForestGreen),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("AI", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                            ) {
                                Text(
                                    msg.text,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }

                if (isTyping) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp).clip(CircleShape).background(ForestGreen),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("AI", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        TypingDots()
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Suggestions rapides
            if (messages.size <= 1) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    SuggestionChip("Symptômes paludisme") { inputText = "Quels sont les symptômes du paludisme ?" }
                    SuggestionChip("Traitement diarrhée") { inputText = "Traitement de la diarrhée chez l'enfant ?" }
                }
            }

            // Input
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.5f))
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Posez votre question…", fontSize = 13.sp) },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) ForestGreen else MaterialTheme.colorScheme.outline.copy(0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = { sendMessage() }, enabled = inputText.isNotBlank()) {
                        Icon(Icons.Outlined.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TypingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val dot1 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse, StartOffset(0)), label = "d1")
    val dot2 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse, StartOffset(200)), label = "d2")
    val dot3 by infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse, StartOffset(400)), label = "d3")
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf(dot1, dot2, dot3).forEach { alpha ->
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(ForestGreen.copy(alpha = alpha)))
            }
        }
    }
}

@Composable
private fun ContextChip(label: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(if (selected) ForestGreen.copy(0.15f) else Color.Transparent)
            .border(1.dp, if (selected) ForestGreen else MaterialTheme.colorScheme.outline, RoundedCornerShape(99.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Text(
            label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
            color = if (selected) ForestGreen else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SuggestionChip(label: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label   = { Text(label, fontSize = 11.sp) },
        leadingIcon = { Icon(Icons.Outlined.Lightbulb, null, Modifier.size(14.dp), tint = Accent) },
    )
}
