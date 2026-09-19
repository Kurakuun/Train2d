package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TrainSoundManager
import com.example.data.model.GameContent
import com.example.data.storage.GamePreferences
import com.example.data.storage.GameState
import com.example.ui.theme.TrainBrightCyan
import com.example.ui.theme.TrainGreen
import com.example.ui.theme.TrainLightGreen
import com.example.ui.theme.TrainSafetyRed
import com.example.ui.theme.TrainYellowPrimary

@Composable
fun SettingsDialog(
    gameState: GameState,
    gamePrefs: GamePreferences,
    soundManager: TrainSoundManager,
    onDismiss: () -> Unit
) {
    var soundMuted by remember { mutableStateOf(soundManager.isMuted) }
    var musicPlaying by remember { mutableStateOf(soundManager.isMusicPlaying()) }
    var currentTrackId by remember { mutableStateOf(soundManager.getCurrentTrack()?.id ?: "track_1") }
    var speedUnitMph by remember { mutableStateOf(false) }
    var hapticsEnabled by remember { mutableStateOf(true) }
    var highQualityVisuals by remember { mutableStateOf(true) }
    var sfxVolume by remember { mutableFloatStateOf(1.0f) }
    var musicVolume by remember { mutableFloatStateOf(0.85f) }

    val currentTrack = GameContent.BREAKCORE_MUSIC_TRACKS.find { it.id == currentTrackId }
        ?: GameContent.BREAKCORE_MUSIC_TRACKS.first()

    val selectedTrain = GameContent.ALL_TRAINS.find { it.id == gameState.selectedTrainId }
        ?: GameContent.ALL_TRAINS.first()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.70f))
            .clickable { onDismiss() }
            .testTag("settings_dialog_backdrop"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFA0F172A)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .border(width = 1.5.dp, color = Color(0x6638BDF8), shape = RoundedCornerShape(20.dp))
                .clickable(enabled = false) {}
                .testTag("settings_card_container")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TrainBrightCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "GAME ENGINE & AUDIO SETTINGS",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Synthesizer • Horn Profiles • Dynamic Units",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.5.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("settings_close_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // SECTION 1: MASTER AUDIO & SYNTHESIZER
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = TrainYellowPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("MASTER SOUND SYNTHESIS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.5.sp)
                                    }

                                    // Mute Toggle
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (soundMuted) TrainSafetyRed else TrainGreen)
                                            .clickable {
                                                soundMuted = soundManager.toggleMute()
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                            .testTag("settings_mute_toggle"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (soundMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (soundMuted) "MUTED" else "SOUND ON",
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Sound FX Volume Slider
                                Text("Train Engine & Mechanical SFX: ${(sfxVolume * 100).toInt()}%", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Slider(
                                    value = sfxVolume,
                                    onValueChange = { sfxVolume = it },
                                    colors = SliderDefaults.colors(thumbColor = TrainBrightCyan, activeTrackColor = TrainBrightCyan),
                                    modifier = Modifier.testTag("settings_sfx_volume_slider")
                                )

                                // Music Volume Slider
                                Text("Game Music Synth Volume: ${(musicVolume * 100).toInt()}%", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Slider(
                                    value = musicVolume,
                                    onValueChange = { musicVolume = it },
                                    colors = SliderDefaults.colors(thumbColor = TrainYellowPrimary, activeTrackColor = TrainYellowPrimary),
                                    modifier = Modifier.testTag("settings_music_volume_slider")
                                )
                            }
                        }
                    }

                    // SECTION 2: RETRO / HIP-HOP / FUNKY / HORROR MUSIC JUKEBOX
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = TrainBrightCyan, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("GAME MUSIC ENGINE (15 SOUNDTRACKS)", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.5.sp)
                                    }

                                    // Play/Stop Music
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (musicPlaying) Color(0xFF7C3AED) else Color(0xFF475569))
                                            .clickable {
                                                if (musicPlaying) {
                                                    soundManager.stopBreakcore()
                                                    musicPlaying = false
                                                } else {
                                                    soundManager.playBreakcore(currentTrack)
                                                    musicPlaying = true
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                            .testTag("settings_music_toggle_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (musicPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (musicPlaying) "STOPPING" else "PLAY TRACK",
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Genres: Country • Bluegrass • Honky-Tonk • Folk (Acoustic Guitar, Banjo & Train Shuffle)",
                                    color = TrainLightGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Track List
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(GameContent.BREAKCORE_MUSIC_TRACKS) { track ->
                                        val isSelected = track.id == currentTrackId
                                        val genreColor = when {
                                            track.subtitle.contains("Bluegrass", ignoreCase = true) -> Color(0xFFD97706) // Golden Bluegrass
                                            track.subtitle.contains("Honky-Tonk", ignoreCase = true) -> Color(0xFFB45309) // Honky-Tonk Amber
                                            track.subtitle.contains("Folk", ignoreCase = true) -> Color(0xFF059669) // Emerald Folk
                                            else -> Color(0xFFC2410C) // Warm Country Terracotta
                                        }

                                        Box(
                                            modifier = Modifier
                                                .width(180.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) genreColor else Color(0xFF0F172A))
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) Color.White else Color(0xFF334155),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clickable {
                                                    currentTrackId = track.id
                                                    soundManager.playBreakcore(track)
                                                    musicPlaying = true
                                                }
                                                .padding(10.dp)
                                                .testTag("settings_track_${track.id}")
                                        ) {
                                            Column {
                                                Text(
                                                    text = track.title,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 11.sp,
                                                    maxLines = 1
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = track.subtitle,
                                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF94A3B8),
                                                    fontSize = 9.sp,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 3: TRAIN HORN TESTER & GG1 LOW-PITCH PREVIEW
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.SurroundSound, contentDescription = null, tint = TrainYellowPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("LOCOMOTIVE HORN TESTER", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.5.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Test Selected Train Horn
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(TrainGreen)
                                            .clickable {
                                                soundManager.blastHorn(selectedTrain.id)
                                            }
                                            .padding(vertical = 10.dp, horizontal = 8.dp)
                                            .testTag("settings_test_current_horn_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("TEST ACTIVE TRAIN HORN", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                                            Text("${selectedTrain.name}", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 8.5.sp)
                                        }
                                    }

                                    // Special PRR GG1 Low Pitch Horn Test Button
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFB45309))
                                            .clickable {
                                                soundManager.blastHorn("prr_gg1")
                                            }
                                            .padding(vertical = 10.dp, horizontal = 8.dp)
                                            .testTag("settings_test_gg1_low_horn_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("PRR GG1 LOW-PITCH HORN", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                                            Text("Accurate Leslie A-200 (Single Tone)", color = Color(0xFFFEF3C7), fontWeight = FontWeight.Bold, fontSize = 8.5.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 4: DISPLAY & SIMULATION PREFERENCES
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("SIMULATION & DISPLAY CONFIGURATION", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.5.sp)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Speed Units (KM/H vs MPH)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Speedometer Units", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                        Text(if (speedUnitMph) "Imperial (Miles per Hour)" else "Metric (Kilometers per Hour)", color = Color(0xFF94A3B8), fontSize = 9.5.sp)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0F172A))
                                            .border(1.dp, TrainBrightCyan, RoundedCornerShape(8.dp))
                                            .clickable { speedUnitMph = !speedUnitMph }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                            .testTag("settings_speed_unit_toggle"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(if (speedUnitMph) "MPH" else "KM/H", color = TrainBrightCyan, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Haptic Vibration
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Track & Throttle Haptics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                        Text("Vibrate on brake friction, track joints and horn", color = Color(0xFF94A3B8), fontSize = 9.5.sp)
                                    }

                                    Switch(
                                        checked = hapticsEnabled,
                                        onCheckedChange = { hapticsEnabled = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = TrainGreen, checkedTrackColor = TrainLightGreen)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
