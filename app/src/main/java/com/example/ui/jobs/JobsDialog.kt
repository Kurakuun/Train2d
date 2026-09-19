package com.example.ui.jobs

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContractJob
import com.example.data.model.GameContent
import com.example.data.storage.GameState
import com.example.ui.theme.GameDiamond
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameSilver
import com.example.ui.theme.TrainBrightCyan
import com.example.ui.theme.TrainGreen
import com.example.ui.theme.TrainLightGreen
import com.example.ui.theme.TrainSafetyRed
import com.example.ui.theme.TrainYellowPrimary

@Composable
fun JobsDialog(
    gameState: GameState,
    onSelectJob: (ContractJob) -> Unit,
    onDismiss: () -> Unit
) {
    val activeTrain = remember(gameState.selectedTrainId) {
        GameContent.ALL_TRAINS.find { it.id == gameState.selectedTrainId } ?: GameContent.ALL_TRAINS.first()
    }

    var selectedFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "STANDARD", "HIGH TIER", "SHORT HAUL", "LONG HAUL")

    val filteredJobs = GameContent.ALL_CONTRACTS.filter { job ->
        when (selectedFilter) {
            "STANDARD" -> !job.isHighTier
            "HIGH TIER" -> job.isHighTier
            "SHORT HAUL" -> job.distanceMeters < 5000f
            "LONG HAUL" -> job.distanceMeters >= 5000f
            else -> true
        }
    }

    var selectedPreviewJob by remember { mutableStateOf(filteredJobs.firstOrNull() ?: GameContent.ALL_CONTRACTS.first()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.70f))
            .clickable { onDismiss() }
            .testTag("jobs_dialog_backdrop"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFA0B1120)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .border(width = 1.5.dp, color = Color(0x6638BDF8), shape = RoundedCornerShape(20.dp))
                .clickable(enabled = false) {}
                .testTag("jobs_card_container")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // 1. HEADER BAR
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
                            Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = Color(0xFF0B1120), modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GLOBAL ROUTE & DISPATCH RADAR",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "⭐ Driver Rank: LVL ${gameState.driverLevel} • Available Dispatch Routes: ${filteredJobs.size}",
                                color = TrainLightGreen,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("jobs_close_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. INTERACTIVE ROUTE MAP & ELEVATION PROFILE RADAR
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131E35)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .border(1.dp, Color(0x4438BDF8), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Elevation Radar Canvas
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                // Grid Lines
                                for (i in 0..4) {
                                    val y = (h / 4f) * i
                                    drawLine(Color(0x2238BDF8), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                                }
                                for (i in 0..6) {
                                    val x = (w / 6f) * i
                                    drawLine(Color(0x2238BDF8), Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                                }

                                // Mountain Elevation Curve
                                val path = Path()
                                path.moveTo(0f, h * 0.75f)
                                path.cubicTo(w * 0.25f, h * 0.25f, w * 0.5f, h * 0.85f, w * 0.75f, h * 0.35f)
                                path.lineTo(w, h * 0.60f)

                                // Draw glow path
                                drawPath(path, color = TrainBrightCyan, style = Stroke(width = 3.5f))

                                // Waypoints
                                drawCircle(Color.White, radius = 5f, center = Offset(0f, h * 0.75f))
                                drawCircle(TrainYellowPrimary, radius = 5f, center = Offset(w * 0.5f, h * 0.85f))
                                drawCircle(TrainGreen, radius = 5f, center = Offset(w, h * 0.60f))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Right: Selected Route Briefing
                        Column(
                            modifier = Modifier.width(170.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("SELECTED:", color = Color(0xFF94A3B8), fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(selectedPreviewJob.title, color = TrainYellowPrimary, fontSize = 9.5.sp, fontWeight = FontWeight.Black, maxLines = 1)
                            }
                            Text(
                                text = "${selectedPreviewJob.originStation} ➔ ${selectedPreviewJob.destinationStation}",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "Dist: ${(selectedPreviewJob.distanceMeters / 1000f).let { "%.1f".format(it) }} km • Gradient: ${selectedPreviewJob.hillSeverity}",
                                color = TrainBrightCyan,
                                fontSize = 8.5.sp
                            )
                            Text(
                                text = "Environment: ${selectedPreviewJob.environment.name.replace("_", " ")} • Consist: ${GameContent.resolveConsistForTrain(selectedPreviewJob, activeTrain).size} Matched Cars",
                                color = Color(0xFFCBD5E1),
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. FILTER CATEGORY CHIPS
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) TrainBrightCyan else Color(0xFF1E293B))
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                                .testTag("jobs_filter_$filter")
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 4. CONTRACT DISPATCH LIST
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredJobs) { job ->
                        val isUnlocked = gameState.driverLevel >= job.requiredDriverLevel
                        val isPreviewed = job.id == selectedPreviewJob.id

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (!isUnlocked) Color(0x661E2430)
                                else if (isPreviewed) Color(0xFF1E3A5F)
                                else Color(0xFF162032)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isPreviewed) 1.5.dp else 1.dp,
                                    color = if (isPreviewed) TrainBrightCyan else if (job.isHighTier && isUnlocked) Color(0xFFE11D48) else Color(0x33475569),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedPreviewJob = job
                                }
                                .testTag("job_item_${job.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    // Title + Tier Tag
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = job.title,
                                            color = if (isUnlocked) (if (job.isHighTier) TrainYellowPrimary else Color.White) else Color(0xFF64748B),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.5.sp
                                        )
                                        if (job.isHighTier) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFFDC2626))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text("HIGH TIER", color = Color.White, fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    // Origin ➔ Destination Route
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Route, contentDescription = null, tint = TrainBrightCyan, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = job.originStation,
                                            color = if (isUnlocked) Color(0xFF93C5FD) else Color(0xFF64748B),
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = job.destinationStation,
                                            color = if (isUnlocked) Color(0xFF67E8F9) else Color(0xFF64748B),
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• ${job.environment.name.replace("_", " ")}",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 9.5.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Metrics & Payout Row
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // Distance
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF0F172A))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text("${(job.distanceMeters / 1000f).let { "%.1f".format(it) }} km", color = Color(0xFFE2E8F0), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Time
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF0F172A))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = TrainBrightCyan, modifier = Modifier.size(9.dp))
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(job.estimatedMinutes, color = TrainBrightCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Cargo Cars
                                        val consistCount = GameContent.resolveConsistForTrain(job, activeTrain).size
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF0F172A))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text("📦 $consistCount Cars", color = Color(0xFFCBD5E1), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Rewards
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🪙 ${job.rewardGold}", color = GameGold, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("💎 ${job.rewardDiamonds}", color = GameDiamond, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Dispatch or Locked State
                                if (isUnlocked) {
                                    Button(
                                        onClick = {
                                            onSelectJob(job)
                                            onDismiss()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (job.isHighTier) Color(0xFFE11D48) else TrainGreen
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("dispatch_job_${job.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("DISPATCH", fontWeight = FontWeight.Black, fontSize = 10.5.sp)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF1E293B))
                                            .padding(horizontal = 8.dp, vertical = 5.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = Color(0xFF94A3B8), modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "LVL ${job.requiredDriverLevel}",
                                                color = Color(0xFFCBD5E1),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp
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
    }
}

