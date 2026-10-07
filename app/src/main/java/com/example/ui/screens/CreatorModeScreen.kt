package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ScriptLength
import com.example.engine.ScriptTone
import com.example.engine.VoiceCatalog
import com.example.ui.components.AudioPlayerCard
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VoiceViewModel

data class GamingTemplate(
    val title: String,
    val subtitle: String,
    val script: String
)

@Composable
fun CreatorModeScreen(
    viewModel: VoiceViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val context = LocalContext.current

    val gamingTemplates = listOf(
        GamingTemplate(
            title = "BGMI Clutch",
            subtitle = "1v4 Rush Gameplay",
            script = "Arey bhai bhai bhai! Zone shrink ho raha hai, aage poori squad baithee hai! Smoke phenko aur sidha rush karo! Ek knock... do knock... Aur ye lag gaya OP 1v4 clutch! Video ko like thok do dosto!"
        ),
        GamingTemplate(
            title = "Funny Moment",
            subtitle = "GTA 5 Roleplay",
            script = "Toh bhaiyo aaj Michael gaya bank lootne, lekin police aane se pehle gaadi ka tyre puncture ho gaya! Ab ye cycle chala ke bhag raha hai! Kya hi gazab scene ho gaya yaar!"
        ),
        GamingTemplate(
            title = "Minecraft Lore",
            subtitle = "Survival Mystery",
            script = "Deep dark biome ke sabse nichle hisse me ek ajeeb sa block chamak raha tha... Jaise hi maine pair rakha, Warden ki bhayanak awaaz aayi! Kya hum aaj zinda bach payenge?"
        ),
        GamingTemplate(
            title = "Shorts Hype Hook",
            subtitle = "High retention intro",
            script = "Ye trick dekhne ke baad tumhara K/D seedha double hone wala hai! Sirf 3 settings ko on karo aur dekhna har match me chicken dinner!"
        )
    )

    val topicSuggestions = listOf(
        "BGMI Pochinki Rush",
        "GTA 5 Heist",
        "Minecraft Horror",
        "Top 5 Gaming Secrets",
        "Shorts Viral Hook"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creator Mode Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)),
                        RoundedCornerShape(22.dp)
                    ),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCardSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    brush = Brush.linearGradient(listOf(NeonCyan, NeonPurple)),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "YouTube Studio",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "YouTube Voiceover Studio",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonAmber.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "GAMING",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonAmber,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "AI Voiceover Scriptwriter & High-Energy Speech Synthesizer",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("AI Scriptwriter", "Natural Pauses", "Hindi & Hinglish", "Long Scripts").forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1B1E32),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, CyberCardBorder)
                            ) {
                                Text(
                                    text = "⚡ $tag",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // AI Scriptwriter Assistant Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Scriptwriter",
                                tint = NeonPurpleBright,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Script Generator",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonPurple.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Karan Voice AI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeonPurpleBright,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Enter a topic to generate a ready-to-speak voiceover script with natural pacing:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.scriptTopic,
                        onValueChange = { viewModel.updateScriptTopic(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("script_topic_input"),
                        placeholder = {
                            Text(
                                text = "e.g., Free Fire intense 1v4 clutch gameplay or GTA 5 car chase...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Topic suggestions
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(topicSuggestions) { topic ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1B1E32),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, CyberCardBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updateScriptTopic(topic) }
                            ) {
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tone chips
                    Text(
                        text = "Voiceover Tone:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ScriptTone.entries) { tone ->
                            val isSelected = uiState.selectedTone == tone
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color(0xFF191C2E),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan) else null,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.selectScriptTone(tone) }
                            ) {
                                Text(
                                    text = tone.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) NeonCyan else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Length chips & Generate button row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ScriptLength.entries.forEach { len ->
                                val isSelected = uiState.selectedLength == len
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) NeonPurple.copy(alpha = 0.25f) else Color(0xFF191C2E),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonPurple) else null,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.selectScriptLength(len) }
                                ) {
                                    Text(
                                        text = len.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) NeonPurpleBright else TextMuted,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.generateAiScript() },
                            enabled = !uiState.isGeneratingScript,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPurple,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("generate_ai_script_button")
                        ) {
                            if (uiState.isGeneratingScript) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Generate",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Write Script", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Quick Gaming Templates
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Ready Creator Templates",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(gamingTemplates) { tmpl ->
                        Card(
                            modifier = Modifier
                                .width(200.dp)
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.loadSampleScript(tmpl.script)
                                    viewModel.toggleCreatorMode(true)
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberCardSurface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = tmpl.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                )
                                Text(
                                    text = tmpl.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = tmpl.script,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    ),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Script Editor
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCardSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gaming Script Editor",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NeonPurple.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        val enhanced = uiState.text
                                            .replace(" भाई ", " भाई, ")
                                            .replace(" दोस्तों ", " दोस्तों, ")
                                            .replace(" !", "! ")
                                        viewModel.updateText(enhanced)
                                    }
                            ) {
                                Text(
                                    text = "+ Natural Pauses",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonPurpleBright,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.text,
                        onValueChange = { viewModel.updateText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 150.dp, max = 260.dp)
                            .testTag("creator_script_input"),
                        placeholder = {
                            Text(
                                text = "Enter gaming commentary script in Hindi/Hinglish...\n(e.g., Arey bhai! Dekho aage poori squad hai, abhi 1v4 clutch dikhata hoon!)",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = NeonCyan
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val words = if (uiState.text.isBlank()) 0 else uiState.text.trim().split(Regex("\\s+")).size
                    val estDurationSec = (words / 2.3).toInt()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Est. duration: ~$estDurationSec sec • YouTube Voiceover active",
                            style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan)
                        )
                        Text(
                            text = "${uiState.text.length} chars ($words words)",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }
            }
        }

        // Prominent Creator Generate Button
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        viewModel.toggleCreatorMode(true)
                        viewModel.generateVoice()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("creator_generate_button"),
                    enabled = !uiState.isGenerating,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = Color.Black
                    )
                ) {
                    if (uiState.isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Rendering YouTube Voiceover...",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Fast Render",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GENERATE GAMING VOICEOVER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                if (uiState.isGenerating) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { uiState.generationProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = NeonPurple,
                        trackColor = CyberCardBorder
                    )
                }
            }
        }

        // Audio Player Card
        item {
            AnimatedVisibility(visible = playerState.currentFilePath != null || uiState.lastGeneratedVoice != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Gaming Audio Master Player",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    AudioPlayerCard(
                        playerState = playerState,
                        voiceEntity = uiState.lastGeneratedVoice,
                        onPlayPause = {
                            if (playerState.isPlaying) viewModel.pauseAudio() else viewModel.resumeAudio()
                        },
                        onStop = { viewModel.stopAudio() },
                        onReplay = { viewModel.replayAudio() },
                        onSeek = { ms -> viewModel.seekAudio(ms) },
                        onExport = {
                            uiState.lastGeneratedVoice?.let { item ->
                                viewModel.exportAudio(context, item)
                            }
                        }
                    )
                }
            }
        }
    }
}
