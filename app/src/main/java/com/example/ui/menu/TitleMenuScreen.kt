package com.example.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import kotlin.math.sin
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TrainSoundManager
import com.example.data.model.GameContent
import com.example.data.storage.GamePreferences
import com.example.data.storage.GameState
import com.example.ui.components.drawLocomotiveDetailed
import com.example.ui.inventory.InventoryDialog
import com.example.ui.settings.SettingsDialog
import androidx.compose.material.icons.filled.Settings
import com.example.ui.theme.GameDiamond
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameSilver
import com.example.ui.theme.TrainBrightCyan
import com.example.ui.theme.TrainCharcoal
import com.example.ui.theme.TrainDarkSteel
import com.example.ui.theme.TrainGreen
import com.example.ui.theme.TrainLightGreen
import com.example.ui.theme.TrainSafetyRed
import com.example.ui.theme.TrainYellowPrimary

@Composable
fun TitleMenuScreen(
    gameState: GameState,
    gamePrefs: GamePreferences,
    soundManager: TrainSoundManager,
    onStartDrive: () -> Unit,
    onOpenWorkshop: () -> Unit,
    onOpenJobs: () -> Unit,
    onOpenCodes: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenMaintenance: () -> Unit = {}
) {
    val selectedTrain = GameContent.ALL_TRAINS.find { it.id == gameState.selectedTrainId }
        ?: GameContent.ALL_TRAINS.first()
    val activeVariantKey = gameState.trainLiveryVariants[selectedTrain.id] ?: selectedTrain.availableLiveries.firstOrNull()?.id ?: "V1_VIRGIN"
    val activeLivery = selectedTrain.availableLiveries.find { it.id == activeVariantKey }
        ?: selectedTrain.availableLiveries.firstOrNull()
    val bodyColor = Color(gameState.customBodyColors[selectedTrain.id] ?: activeLivery?.primaryColor ?: selectedTrain.defaultBodyColor)
    val stripeColor = Color(gameState.customStripeColors[selectedTrain.id] ?: activeLivery?.accentColor ?: selectedTrain.defaultStripeColor)
    val trainHealth = gameState.locomotiveHealthMap[selectedTrain.id] ?: com.example.data.storage.LocomotiveHealth()

    var showMusicJukeboxModal by remember { mutableStateOf(false) }
    var showInventoryModal by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }

    // Continuous Frame Ticker for Depot Yard Activity & 30-Second Passing Train
    var depotTickerSeconds by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var lastNano = System.nanoTime()
        while (true) {
            withFrameNanos { nowNano ->
                val dt = ((nowNano - lastNano) / 1_000_000_000f).coerceIn(0.005f, 0.05f)
                lastNano = nowNano
                depotTickerSeconds += dt
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // 1. DYNAMIC REALISTIC TRAIN DEPOT YARD WITH BACKGROUND TRAINS & 30-SEC PASSING TRAIN
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // A. Atmospheric Twilight Sky & Industrial Depot Backdrop
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF090D16), Color(0xFF131A26), Color(0xFF1E293B)),
                    startY = 0f,
                    endY = h * 0.70f
                ),
                size = Size(w, h * 0.70f)
            )

            // Distant depot skyline silhouettes (Depot Roundhouse, Water Tank Tower, Crane)
            // Water Tower on left
            drawRect(color = Color(0xFF161E2E), topLeft = Offset(35f, h * 0.08f), size = Size(38f, 32f))
            drawLine(color = Color(0xFF1E293B), start = Offset(40f, h * 0.08f + 32f), end = Offset(32f, h * 0.22f), strokeWidth = 3f)
            drawLine(color = Color(0xFF1E293B), start = Offset(68f, h * 0.08f + 32f), end = Offset(76f, h * 0.22f), strokeWidth = 3f)
            drawLine(color = Color(0xFF1E293B), start = Offset(40f, h * 0.15f), end = Offset(70f, h * 0.15f), strokeWidth = 2f)

            // Gantry Crane silhouette in center distance
            drawLine(color = Color(0xFF1E293B), start = Offset(w * 0.42f, h * 0.06f), end = Offset(w * 0.58f, h * 0.06f), strokeWidth = 4f)
            drawLine(color = Color(0xFF161E2E), start = Offset(w * 0.43f, h * 0.06f), end = Offset(w * 0.41f, h * 0.22f), strokeWidth = 3.5f)
            drawLine(color = Color(0xFF161E2E), start = Offset(w * 0.57f, h * 0.06f), end = Offset(w * 0.59f, h * 0.22f), strokeWidth = 3.5f)

            // Depot Roundhouse Arch silhouette on right
            drawRoundRect(
                color = Color(0xFF151D2A),
                topLeft = Offset(w - 110f, h * 0.10f),
                size = Size(130f, h * 0.14f),
                cornerRadius = CornerRadius(20f, 20f)
            )
            // Amber windows inside roundhouse
            for (rw in 0 until 3) {
                drawRoundRect(
                    color = Color(0xFFF59E0B).copy(alpha = 0.35f),
                    topLeft = Offset(w - 95f + (rw * 32f), h * 0.14f),
                    size = Size(20f, 24f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }

            // High Depot Floodlight Towers (Two towers casting downward golden-white light cones)
            listOf(w * 0.18f, w * 0.82f).forEach { tx ->
                // Steel lattice mast
                drawLine(color = Color(0xFF334155), start = Offset(tx, h * 0.03f), end = Offset(tx, h * 0.65f), strokeWidth = 4f)
                for (step in 0..7) {
                    val sy = h * 0.05f + step * (h * 0.08f)
                    drawLine(color = Color(0xFF475569), start = Offset(tx - 6f, sy), end = Offset(tx + 6f, sy), strokeWidth = 1.5f)
                }
                // Floodlight Lamp Head Bar
                drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(tx - 18f, h * 0.03f - 6f), size = Size(36f, 10f), cornerRadius = CornerRadius(2f, 2f))
                drawCircle(color = Color(0xFFFEF08A), radius = 5f, center = Offset(tx - 10f, h * 0.03f))
                drawCircle(color = Color(0xFFFEF08A), radius = 5f, center = Offset(tx + 10f, h * 0.03f))

                // Downward Atmospheric Light Cones
                val lightCone = Path().apply {
                    moveTo(tx, h * 0.03f)
                    lineTo(tx - 140f, h * 0.68f)
                    lineTo(tx + 140f, h * 0.68f)
                    close()
                }
                drawPath(
                    lightCone,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFEF08A).copy(alpha = 0.15f), Color(0x00000000)),
                        startY = h * 0.03f,
                        endY = h * 0.68f
                    )
                )
            }

            // =========================================================================
            // TRACK 1: ELEVATED MAINLINE VIADUCT WITH 30-SECOND PASSING TRAIN
            // =========================================================================
            val viaductY = h * 0.23f
            // Stone/Steel Viaduct Structure across screen
            drawRect(color = Color(0xFF1E293B), topLeft = Offset(0f, viaductY), size = Size(w, 22f))
            drawLine(color = Color(0xFF0F172A), start = Offset(0f, viaductY + 22f), end = Offset(w, viaductY + 22f), strokeWidth = 3f)
            // Viaduct Arch Piers
            for (p in 0 until (w / 80f).toInt() + 2) {
                val px = p * 80f
                drawRoundRect(color = Color(0xFF151D2A), topLeft = Offset(px, viaductY + 4f), size = Size(32f, 26f), cornerRadius = CornerRadius(6f, 6f))
            }
            // Viaduct Track Ballast & Rails
            drawRect(color = Color(0xFF334155), topLeft = Offset(0f, viaductY - 3f), size = Size(w, 4f))
            drawLine(color = Color(0xFF64748B), start = Offset(0f, viaductY - 3f), end = Offset(w, viaductY - 3f), strokeWidth = 2.5f)
            drawLine(color = Color(0xFFE2E8F0), start = Offset(0f, viaductY - 4f), end = Offset(w, viaductY - 4f), strokeWidth = 1.2f)

            // --- 30-SECOND PASSING TRAIN SYSTEM ---
            val passCycleSec = depotTickerSeconds % 30f
            val passDurationSec = 5.2f // Train takes 5.2 seconds to pass across screen
            if (passCycleSec < passDurationSec) {
                val passProgress = (passCycleSec / passDurationSec).coerceIn(0f, 1f)
                val trainTotalWidth = 480f
                val trainPassX = -trainTotalWidth + passProgress * (w + trainTotalWidth * 2f)
                val passTrainY = viaductY - 22f
                val cycleIndex = (depotTickerSeconds / 30f).toInt()
                val isStreamliner = cycleIndex % 2 == 0

                // Forward Headlight Beam casting forward over viaduct
                val beamPath = Path().apply {
                    moveTo(trainPassX + 160f, passTrainY + 12f)
                    lineTo(trainPassX + 320f, passTrainY - 5f)
                    lineTo(trainPassX + 320f, passTrainY + 28f)
                    close()
                }
                drawPath(
                    beamPath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFEF08A).copy(alpha = 0.5f), Color(0x00000000)),
                        startX = trainPassX + 160f,
                        endX = trainPassX + 320f
                    )
                )

                if (isStreamliner) {
                    // Modern High-Speed Express / Streamliner (Locomotive + 2 Passenger Cars)
                    // Locomotive (Silver & Teal)
                    drawRoundRect(
                        color = Color(0xFF0284C7),
                        topLeft = Offset(trainPassX + 80f, passTrainY),
                        size = Size(80f, 19f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                    // Streamline nose wedge
                    val nosePath = Path().apply {
                        moveTo(trainPassX + 160f, passTrainY + 19f)
                        lineTo(trainPassX + 175f, passTrainY + 19f)
                        lineTo(trainPassX + 160f, passTrainY + 6f)
                        close()
                    }
                    drawPath(nosePath, color = Color(0xFF0284C7))
                    // Headlight
                    drawCircle(color = Color(0xFFFEF08A), radius = 3f, center = Offset(trainPassX + 173f, passTrainY + 14f))

                    // Trailing Passenger Coach 1
                    drawRoundRect(
                        color = Color(0xFF0369A1),
                        topLeft = Offset(trainPassX - 10f, passTrainY + 1f),
                        size = Size(85f, 18f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                    // Coach 1 glowing interior windows
                    for (win in 0 until 5) {
                        drawRoundRect(
                            color = Color(0xFFFEF08A).copy(alpha = 0.85f),
                            topLeft = Offset(trainPassX - 6f + (win * 16f), passTrainY + 4f),
                            size = Size(11f, 7f),
                            cornerRadius = CornerRadius(1f, 1f)
                        )
                    }

                    // Trailing Passenger Coach 2
                    drawRoundRect(
                        color = Color(0xFF0369A1),
                        topLeft = Offset(trainPassX - 100f, passTrainY + 1f),
                        size = Size(85f, 18f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                    // Coach 2 glowing interior windows
                    for (win in 0 until 5) {
                        drawRoundRect(
                            color = Color(0xFFFEF08A).copy(alpha = 0.85f),
                            topLeft = Offset(trainPassX - 96f + (win * 16f), passTrainY + 4f),
                            size = Size(11f, 7f),
                            cornerRadius = CornerRadius(1f, 1f)
                        )
                    }
                } else {
                    // Heavy Freight Diesel / Steam Train with Cargo
                    // Diesel Cab Unit (Orange & Black)
                    drawRoundRect(
                        color = Color(0xFFEA580C),
                        topLeft = Offset(trainPassX + 80f, passTrainY),
                        size = Size(85f, 20f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                    // Cab windshield
                    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(trainPassX + 148f, passTrainY + 3f), size = Size(12f, 8f), cornerRadius = CornerRadius(1f, 1f))
                    drawCircle(color = Color(0xFFFEF08A), radius = 3f, center = Offset(trainPassX + 164f, passTrainY + 13f))

                    // Exhaust heat wisps
                    val exhaustX = trainPassX + 105f - (passCycleSec * 25f) % 40f
                    drawCircle(color = Color(0xFF64748B).copy(alpha = 0.35f), radius = 5f, center = Offset(exhaustX, passTrainY - 6f))

                    // Container Freight Car 1
                    drawRoundRect(
                        color = Color(0xFF2563EB),
                        topLeft = Offset(trainPassX - 10f, passTrainY + 2f),
                        size = Size(85f, 17f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                    // Container Freight Car 2
                    drawRoundRect(
                        color = Color(0xFF16A34A),
                        topLeft = Offset(trainPassX - 100f, passTrainY + 2f),
                        size = Size(85f, 17f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                }

                // Rotating wheels under passing train
                val wheelSpin = (passCycleSec * 50f)
                listOf(
                    trainPassX - 90f, trainPassX - 30f, trainPassX + 5f,
                    trainPassX + 60f, trainPassX + 95f, trainPassX + 145f
                ).forEach { wx ->
                    drawCircle(color = Color(0xFF0F172A), radius = 4f, center = Offset(wx, passTrainY + 20f))
                    drawCircle(color = Color(0xFF94A3B8), radius = 2f, center = Offset(wx, passTrainY + 20f))
                }
            }

            // =========================================================================
            // TRACK 2: UPPER DEPOT SIDING WITH PARKED TRAIN 1 (HEAVY DIESEL LOCOMOTIVE)
            // =========================================================================
            val siding1Y = h * 0.38f
            // Ballast bed & Wooden Ties
            drawRect(color = Color(0xFF1E293B), topLeft = Offset(0f, siding1Y - 2f), size = Size(w, 16f))
            for (t in 0 until (w / 22f).toInt() + 1) {
                drawRect(color = Color(0xFF2B1D14), topLeft = Offset(t * 22f, siding1Y), size = Size(14f, 12f))
            }
            // Steel Rails
            drawLine(color = Color(0xFF475569), start = Offset(0f, siding1Y + 3f), end = Offset(w, siding1Y + 3f), strokeWidth = 4f)
            drawLine(color = Color(0xFFCBD5E1), start = Offset(0f, siding1Y + 2f), end = Offset(w, siding1Y + 2f), strokeWidth = 2f)

            // Parked Train 1: Heavy Diesel (EMD Warbonnet Style / Deltic) at x = 40f
            val dX = 40f
            val dY = siding1Y - 42f
            // Main Diesel Body (Deep Crimson with Silver Roof)
            drawRoundRect(color = Color(0xFF881337), topLeft = Offset(dX, dY + 6f), size = Size(170f, 34f), cornerRadius = CornerRadius(5f, 5f))
            drawRoundRect(color = Color(0xFFE2E8F0), topLeft = Offset(dX + 5f, dY), size = Size(160f, 10f), cornerRadius = CornerRadius(4f, 4f))
            // Streamline cab nose wedge on right
            val dNose = Path().apply {
                moveTo(dX + 170f, dY + 6f)
                lineTo(dX + 195f, dY + 22f)
                lineTo(dX + 195f, dY + 40f)
                lineTo(dX + 170f, dY + 40f)
                close()
            }
            drawPath(dNose, color = Color(0xFF881337))
            // Cab windshield & chrome nose herald
            drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(dX + 160f, dY + 8f), size = Size(18f, 11f), cornerRadius = CornerRadius(2f, 2f))
            drawCircle(color = Color(0xFFFDE047), radius = 3.5f, center = Offset(dX + 192f, dY + 26f))
            // Body side louvers & yellow speed stripe
            drawLine(color = Color(0xFFFBBF24), start = Offset(dX + 10f, dY + 22f), end = Offset(dX + 175f, dY + 22f), strokeWidth = 3f)
            for (lv in 0 until 6) {
                drawRoundRect(color = Color(0xFF4C0519), topLeft = Offset(dX + 25f + (lv * 18f), dY + 12f), size = Size(11f, 8f), cornerRadius = CornerRadius(1f, 1f))
            }
            // Diesel Exhaust port & subtle warm idling wisps
            drawRect(color = Color(0xFF0F172A), topLeft = Offset(dX + 60f, dY - 4f), size = Size(14f, 5f))
            val idleWispOffset = sin(depotTickerSeconds * 2.5f) * 3f
            drawCircle(color = Color(0xFF64748B).copy(alpha = 0.25f), radius = 5f, center = Offset(dX + 67f + idleWispOffset, dY - 12f))
            // Bogie trucks & wheels
            listOf(dX + 25f, dX + 55f, dX + 125f, dX + 155f).forEach { wx ->
                drawCircle(color = Color(0xFF0F172A), radius = 7f, center = Offset(wx, siding1Y + 2f))
                drawCircle(color = Color(0xFF64748B), radius = 4f, center = Offset(wx, siding1Y + 2f))
            }

            // Maintenance Catwalk Platform alongside Parked Diesel
            val catX = dX + 215f
            drawLine(color = Color(0xFF475569), start = Offset(catX, siding1Y - 35f), end = Offset(catX + 70f, siding1Y - 35f), strokeWidth = 3.5f)
            drawLine(color = Color(0xFFFBBF24), start = Offset(catX, siding1Y - 48f), end = Offset(catX + 70f, siding1Y - 48f), strokeWidth = 2f) // Yellow railing
            for (st in 0..4) {
                val stepY = (siding1Y - 35f) + st * 7f
                drawLine(color = Color(0xFF64748B), start = Offset(catX + 70f + (st * 5f), stepY), end = Offset(catX + 78f + (st * 5f), stepY), strokeWidth = 2f)
            }

            // =========================================================================
            // TRACK 3: LOWER DEPOT SIDING WITH PARKED TRAIN 2 (CLASSIC STEAM ENGINE)
            // =========================================================================
            val siding2Y = h * 0.54f
            // Ballast bed & Wooden Ties
            drawRect(color = Color(0xFF1E293B), topLeft = Offset(0f, siding2Y - 2f), size = Size(w, 18f))
            for (t in 0 until (w / 22f).toInt() + 1) {
                drawRect(color = Color(0xFF24170E), topLeft = Offset(t * 22f, siding2Y), size = Size(14f, 13f))
            }
            // Steel Rails
            drawLine(color = Color(0xFF475569), start = Offset(0f, siding2Y + 4f), end = Offset(w, siding2Y + 4f), strokeWidth = 4f)
            drawLine(color = Color(0xFFCBD5E1), start = Offset(0f, siding2Y + 3f), end = Offset(w, siding2Y + 3f), strokeWidth = 2f)

            // Buffer Stop on right of Track 3
            drawRect(color = Color(0xFFB91C1C), topLeft = Offset(w - 38f, siding2Y - 18f), size = Size(18f, 22f))
            drawCircle(color = Color(0xFFFEF08A), radius = 4f, center = Offset(w - 29f, siding2Y - 7f))

            // Parked Train 2: Heavy Classic Steam Locomotive at x = w - 280f
            val sX = (w - 280f).coerceAtLeast(60f)
            val sY = siding2Y - 46f
            // Tender Car with Coal
            drawRoundRect(color = Color(0xFF1E242B), topLeft = Offset(sX, sY + 12f), size = Size(70f, 30f), cornerRadius = CornerRadius(2f, 2f))
            // Coal mound on tender
            val coalMound = Path().apply {
                moveTo(sX + 4f, sY + 12f)
                lineTo(sX + 35f, sY + 4f)
                lineTo(sX + 66f, sY + 12f)
                close()
            }
            drawPath(coalMound, color = Color(0xFF0B0F14))
            // Locomotive Cab
            drawRoundRect(color = Color(0xFF1A1F26), topLeft = Offset(sX + 74f, sY + 4f), size = Size(42f, 38f), cornerRadius = CornerRadius(2f, 2f))
            // Cab window with warm firebox orange glow
            drawRoundRect(color = Color(0xFFEA580C).copy(alpha = 0.8f), topLeft = Offset(sX + 86f, sY + 9f), size = Size(14f, 12f), cornerRadius = CornerRadius(2f, 2f))
            // Boiler Cylinder
            drawRoundRect(color = Color(0xFF14181E), topLeft = Offset(sX + 116f, sY + 12f), size = Size(85f, 30f), cornerRadius = CornerRadius(3f, 3f))
            // Brass boiler bands
            for (bb in 0..3) {
                drawLine(color = Color(0xFFD97706), start = Offset(sX + 125f + (bb * 20f), sY + 12f), end = Offset(sX + 125f + (bb * 20f), sY + 42f), strokeWidth = 1.5f)
            }
            // Smokebox & Smokestack
            drawRect(color = Color(0xFF0F1216), topLeft = Offset(sX + 185f, sY + 10f), size = Size(26f, 32f))
            drawRoundRect(color = Color(0xFF0F1216), topLeft = Offset(sX + 192f, sY - 4f), size = Size(10f, 16f), cornerRadius = CornerRadius(2f, 2f))
            // Steam Dome & Brass Bell
            drawOval(color = Color(0xFFD97706), topLeft = Offset(sX + 138f, sY + 5f), size = Size(14f, 10f))
            // Front Headlamp with soft amber glow
            drawRect(color = Color(0xFF334155), topLeft = Offset(sX + 211f, sY + 18f), size = Size(8f, 12f))
            drawCircle(color = Color(0xFFFEF08A), radius = 3.5f, center = Offset(sX + 218f, sY + 24f))

            // Gentle White Steam Curls rising from boiler
            val steamCurl = sin(depotTickerSeconds * 3f) * 4f
            drawCircle(color = Color.White.copy(alpha = 0.3f), radius = 6f, center = Offset(sX + 197f + steamCurl, sY - 14f))
            drawCircle(color = Color.White.copy(alpha = 0.2f), radius = 9f, center = Offset(sX + 199f + steamCurl * 1.5f, sY - 24f))

            // Steam Driving Wheels (4 large wheels with counterweights & side connecting rod)
            listOf(sX + 115f, sX + 140f, sX + 165f, sX + 190f).forEach { wx ->
                drawCircle(color = Color(0xFF0F172A), radius = 10f, center = Offset(wx, siding2Y + 2f))
                drawCircle(color = Color(0xFF64748B), radius = 5f, center = Offset(wx, siding2Y + 2f))
                // Counterweight crescent
                drawArc(color = Color(0xFF94A3B8), startAngle = 30f, sweepAngle = 120f, useCenter = true, topLeft = Offset(wx - 8f, siding2Y - 6f), size = Size(16f, 16f))
            }
            // Steel Side Connecting Rod
            drawLine(color = Color(0xFFCBD5E1), start = Offset(sX + 115f, siding2Y + 2f), end = Offset(sX + 190f, siding2Y + 2f), strokeWidth = 3f)

            // Depot Water Column Crane (Stationed next to tender)
            val craneX = sX - 25f
            drawLine(color = Color(0xFF334155), start = Offset(craneX, siding2Y), end = Offset(craneX, siding2Y - 55f), strokeWidth = 5f) // Vertical standpipe
            drawLine(color = Color(0xFF475569), start = Offset(craneX - 4f, siding2Y - 55f), end = Offset(craneX + 38f, siding2Y - 55f), strokeWidth = 4f) // Pivoted arm
            drawLine(color = Color(0xFF1E293B), start = Offset(craneX + 35f, siding2Y - 55f), end = Offset(craneX + 35f, siding2Y - 40f), strokeWidth = 3.5f) // Discharge funnel

            // =========================================================================
            // FOREGROUND: ACTIVE DEPOT SERVICE APRON & SELECTED HERO TRAIN TRACK
            // =========================================================================
            val floorY = h * 0.67f
            // Reinforced Concrete Depot Floor
            drawRect(
                color = Color(0xFF151921),
                topLeft = Offset(0f, floorY),
                size = Size(w, h - floorY)
            )

            // Yellow/Black Industrial Hazard Caution Stripes along apron inspection pit
            var hzX = 0f
            while (hzX < w) {
                val hzPath = Path().apply {
                    moveTo(hzX, floorY)
                    lineTo(hzX + 16f, floorY)
                    lineTo(hzX + 6f, floorY + 12f)
                    lineTo(hzX - 10f, floorY + 12f)
                    close()
                }
                drawPath(hzPath, color = Color(0xFFFBBF24))
                hzX += 22f
            }

            // Depot Servicing Equipment on Apron (Lubricant drums, Mobile tool chest, Spare wheelsets)
            // Blue & Teal Coolant/Oil drums on left
            drawRoundRect(color = Color(0xFF0369A1), topLeft = Offset(18f, floorY + 16f), size = Size(20f, 32f), cornerRadius = CornerRadius(3f, 3f))
            drawRoundRect(color = Color(0xFF0D9488), topLeft = Offset(36f, floorY + 18f), size = Size(18f, 30f), cornerRadius = CornerRadius(3f, 3f))

            // Spare Wheelset resting on timber blocks on right
            drawRect(color = Color(0xFF3E2723), topLeft = Offset(w - 70f, floorY + 32f), size = Size(36f, 8f))
            drawCircle(color = Color(0xFF334155), radius = 10f, center = Offset(w - 62f, floorY + 28f))
            drawCircle(color = Color(0xFF94A3B8), radius = 4f, center = Offset(w - 62f, floorY + 28f))
            drawCircle(color = Color(0xFF334155), radius = 10f, center = Offset(w - 42f, floorY + 28f))
            drawCircle(color = Color(0xFF94A3B8), radius = 4f, center = Offset(w - 42f, floorY + 28f))
            drawLine(color = Color(0xFF64748B), start = Offset(w - 62f, floorY + 28f), end = Offset(w - 42f, floorY + 28f), strokeWidth = 3f)

            // Primary Service Track (Active Track where Hero Train rests)
            val railY = floorY + 24f
            // Wooden Ties
            for (t in 0 until (w / 28f).toInt() + 2) {
                val tx = t * 28f
                drawRoundRect(
                    color = Color(0xFF2E1C11),
                    topLeft = Offset(tx, railY - 2f),
                    size = Size(20f, 18f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }
            // Steel Top Rails
            drawLine(color = Color(0xFF334155), start = Offset(0f, railY + 6f), end = Offset(w, railY + 6f), strokeWidth = 6f)
            drawLine(color = Color(0xFFE2E8F0), start = Offset(0f, railY + 4f), end = Offset(w, railY + 4f), strokeWidth = 3f)
        }

        // 2. TOP CURRENCY & MUSIC BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Driver Badge, Inventory Button & Music Jukebox Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(TrainDarkSteel)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(TrainYellowPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${gameState.driverLevel}", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("LVL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // INVENTORY / DEPOT BUTTON
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0284C7))
                        .clickable { showInventoryModal = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("menu_inventory_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = "Inventory",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "INVENTORY",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // RETRO DRUM MUSIC JUKEBOX BUTTON (15 Tracks)
                val currentTrack = GameContent.BREAKCORE_MUSIC_TRACKS.find { it.id == gameState.selectedMusicTrackId }
                    ?: GameContent.BREAKCORE_MUSIC_TRACKS.first()

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (gameState.isMusicPlaying) Color(0xFFC2410C) else Color(0xFF374151))
                        .clickable { showMusicJukeboxModal = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("music_jukebox_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (gameState.isMusicPlaying) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "Country Music Radio",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "COUNTRY MUSIC",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // SETTINGS BUTTON
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF334155))
                        .clickable { showSettingsModal = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("menu_settings_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SETTINGS",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Top Currencies (Gold Coins and Diamonds; Silver is exclusive to Inventory & Workshop)
            Row(verticalAlignment = Alignment.CenterVertically) {
                CurrencyPill(icon = Icons.Default.MonetizationOn, value = "${gameState.goldCoins}", color = GameGold)
                Spacer(modifier = Modifier.width(5.dp))
                CurrencyPill(icon = Icons.Default.Diamond, value = "${gameState.diamonds}", color = GameDiamond)
            }
        }

        // 3. MAIN CENTER HERO SECTION (Train + Refill Gauges + Controls)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp, bottom = 4.dp, start = 10.dp, end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TRAIN SIMULATOR 2D",
                    color = TrainYellowPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Simple train game made by Bloxworks Entertainment",
                    color = TrainBrightCyan,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Hero Train Canvas resting in garage with livery rendering (Only train visible)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(width = 340.dp, height = 130.dp)) {
                    drawLocomotiveDetailed(
                        train = selectedTrain,
                        bodyColor = bodyColor,
                        stripeColor = stripeColor,
                        wheelAngleRad = 0f,
                        widthPx = size.width,
                        heightPx = size.height,
                        variant = activeVariantKey
                    )
                }
            }

            // Train Label & Active Livery Variant Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Train Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${selectedTrain.name} ${selectedTrain.countryFlag}",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• ${selectedTrain.soundProfile.name.replace("_", " ")}",
                        color = TrainBrightCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }

            // ACTION BUTTONS ROW (Drive, Workshop, Service, Contracts, Inventory, Codes, Shop)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // DRIVE / PLAY
                Button(
                    onClick = onStartDrive,
                    colors = ButtonDefaults.buttonColors(containerColor = TrainGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .padding(horizontal = 2.5.dp)
                        .testTag("title_drive_button")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Drive", tint = Color.White)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("DRIVE", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }

                // WORKSHOP / GARAGE
                Button(
                    onClick = onOpenWorkshop,
                    colors = ButtonDefaults.buttonColors(containerColor = TrainBrightCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .padding(horizontal = 2.5.dp)
                        .testTag("title_workshop_button")
                ) {
                    Icon(imageVector = Icons.Default.Build, contentDescription = "Workshop", tint = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("GARAGE", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // REPAIR & MAINTENANCE
                Button(
                    onClick = onOpenMaintenance,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (trainHealth.overallPercent < 75) TrainSafetyRed else Color(0xFF0284C7)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .padding(horizontal = 2.5.dp)
                        .testTag("title_maintenance_button")
                ) {
                    Text(if (trainHealth.overallPercent < 75) "⚠️" else "🔧", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SERVICE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                        Text("${trainHealth.overallPercent}%", color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Bold, fontSize = 8.5.sp)
                    }
                }

                // JOBS & CONTRACTS
                Button(
                    onClick = onOpenJobs,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .padding(horizontal = 2.5.dp)
                        .testTag("title_jobs_button")
                ) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = "Jobs", tint = Color.White)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("CONTRACTS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // 24 PROMO CODES
                Button(
                    onClick = onOpenCodes,
                    colors = ButtonDefaults.buttonColors(containerColor = TrainYellowPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .padding(horizontal = 2.5.dp)
                        .testTag("title_codes_button")
                ) {
                    Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = "Codes", tint = Color.Black)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("CODES", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }

                // SHOP
                Button(
                    onClick = onOpenShop,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .padding(horizontal = 2.5.dp)
                        .testTag("title_shop_button")
                ) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Shop", tint = Color.White)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("SHOP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // 4. INVENTORY MODAL OVERLAY
        if (showInventoryModal) {
            InventoryDialog(
                gameState = gameState,
                gamePrefs = gamePrefs,
                soundManager = soundManager,
                onDismiss = { showInventoryModal = false }
            )
        }

        // 5. SETTINGS MODAL OVERLAY
        if (showSettingsModal) {
            SettingsDialog(
                gameState = gameState,
                gamePrefs = gamePrefs,
                soundManager = soundManager,
                onDismiss = { showSettingsModal = false }
            )
        }

        // 5. HORROR RETRO MUSIC JUKEBOX MODAL (15 Songs, Selectable & Persisted!)
        AnimatedVisibility(
            visible = showMusicJukeboxModal,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181528)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(480.dp)
                    .height(290.dp)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📻", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RAILROAD COUNTRY JUKEBOX (15 TRACKS)",
                                color = Color(0xFFFDBA74),
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Music Mute / Unmute Toggle
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (gameState.isMusicPlaying) TrainGreen else Color(0xFF475569))
                                    .clickable {
                                        val newState = !gameState.isMusicPlaying
                                        gamePrefs.toggleMusic(newState)
                                        soundManager.setMusicTrack(gameState.selectedMusicTrackId, newState)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (gameState.isMusicPlaying) "MUSIC: ON" else "MUSIC: OFF",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = { showMusicJukeboxModal = false },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 15 Breakcore Tracks LazyList
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(bottom = 4.dp)
                    ) {
                        items(GameContent.BREAKCORE_MUSIC_TRACKS) { track ->
                            val isSelected = gameState.selectedMusicTrackId == track.id

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) Color(0xFF4C1D95) else Color(0xFF241E3A)
                                    )
                                    .clickable {
                                        gamePrefs.selectMusicTrack(track.id)
                                        soundManager.setMusicTrack(track.id, gameState.isMusicPlaying)
                                        soundManager.playChime()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isSelected) "▶" else "♪",
                                        color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = track.title,
                                            color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "${track.subtitle} • ${track.tempoBpm} BPM",
                                            color = Color(0xFFA78BFA),
                                            fontSize = 8.5.sp
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(TrainGreen)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("ACTIVE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.sp)
                                    }
                                } else {
                                    Text(
                                        text = "PLAY & SAVE",
                                        color = Color(0xFF38BDF8),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
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

@Composable
fun CurrencyPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(TrainDarkSteel)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}
