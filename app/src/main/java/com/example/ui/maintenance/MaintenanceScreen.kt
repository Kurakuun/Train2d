package com.example.ui.maintenance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.audio.TrainSoundManager
import com.example.data.model.GameContent
import com.example.data.model.TrainModel
import com.example.data.model.TrainType
import com.example.data.storage.GamePreferences
import com.example.data.storage.GameState
import com.example.data.storage.LocomotiveHealth
import com.example.ui.components.drawLocomotiveDetailed
import com.example.ui.theme.GameBlueprint
import com.example.ui.theme.GameDiamond
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameSilver
import com.example.ui.theme.TrainBrightCyan
import com.example.ui.theme.TrainCharcoal
import com.example.ui.theme.TrainDarkSteel
import com.example.ui.theme.TrainGreen
import com.example.ui.theme.TrainLightGreen
import com.example.ui.theme.TrainSafetyRed
import com.example.ui.theme.TrainYellowDark
import com.example.ui.theme.TrainYellowPrimary

@Composable
fun MaintenanceScreen(
    gameState: GameState,
    gamePrefs: GamePreferences,
    soundManager: TrainSoundManager,
    onBackToMenu: () -> Unit = {},
    onBackToWorkshop: () -> Unit = {},
    onStartDrive: () -> Unit = {},
    onBack: () -> Unit = onBackToMenu
) {
    // Unlocked trains player owns
    val unlockedTrains = remember(gameState.unlockedTrainIds) {
        GameContent.ALL_TRAINS.filter { gameState.unlockedTrainIds.contains(it.id) }
            .ifEmpty { listOf(GameContent.ALL_TRAINS.first()) }
    }

    var selectedIndex by remember(gameState.selectedTrainId) {
        val idx = unlockedTrains.indexOfFirst { it.id == gameState.selectedTrainId }
        mutableIntStateOf(if (idx >= 0) idx else 0)
    }

    val currentTrain = unlockedTrains.getOrElse(selectedIndex) { unlockedTrains.first() }
    val currentHealth = gameState.locomotiveHealthMap[currentTrain.id] ?: LocomotiveHealth()

    val bodyColor = Color(gameState.customBodyColors[currentTrain.id] ?: currentTrain.defaultBodyColor)
    val stripeColor = Color(gameState.customStripeColors[currentTrain.id] ?: currentTrain.defaultStripeColor)

    var repairToastMessage by remember { mutableStateOf<String?>(null) }
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(gameState.activeRepairTrainId, gameState.repairEndTimestamp) {
        while (gameState.activeRepairTrainId != null) {
            currentTimeMs = System.currentTimeMillis()
            if (currentTimeMs >= gameState.repairEndTimestamp) {
                if (gamePrefs.checkAndCompleteTimedRepair()) {
                    soundManager.playRepairSound()
                    repairToastMessage = "Timed Overhaul Finished! Locomotive 100% Restored!"
                }
                break
            }
            delay(500L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F141C))
    ) {
        // DEPOT ROUNDHOUSE SERVICE BACKGROUND CANVAS
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Industrial concrete service floor gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF191F2B), Color(0xFF111620), Color(0xFF0B0E14)),
                    startY = 0f,
                    endY = h
                ),
                size = Size(w, h)
            )

            // Hydraulic service bay floor grids
            val gridStep = 45f
            var gx = 0f
            while (gx < w) {
                drawLine(
                    color = Color(0xFF242C3D).copy(alpha = 0.25f),
                    start = Offset(gx, 0f),
                    end = Offset(gx, h),
                    strokeWidth = 1f
                )
                gx += gridStep
            }
            var gy = 0f
            while (gy < h) {
                drawLine(
                    color = Color(0xFF242C3D).copy(alpha = 0.25f),
                    start = Offset(0f, gy),
                    end = Offset(w, gy),
                    strokeWidth = 1f
                )
                gy += gridStep
            }

            // Hazard warning yellow/black diagonal stripes along top header border
            val stripeW = 28f
            var sx = -stripeW
            while (sx < w + stripeW) {
                val path = Path().apply {
                    moveTo(sx, 0f)
                    lineTo(sx + 14f, 0f)
                    lineTo(sx - 8f, 10f)
                    lineTo(sx - 22f, 10f)
                    close()
                }
                drawPath(path, color = Color(0xFFEAB308).copy(alpha = 0.65f))
                sx += stripeW
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // 1. TOP HEADER BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { onBack() }
                            .testTag("maintenance_back_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Handyman,
                                contentDescription = null,
                                tint = TrainYellowPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "REPAIR & MAINTENANCE BAY",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                letterSpacing = 0.4.sp
                            )
                        }
                        Text(
                            text = "Service engine wear & tear caused by high-speed operations",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.5.sp
                        )
                    }
                }

                // Currency Balances (Blueprints, Silver, Diamonds)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Blueprints (Required for 1-2 min timed repairs)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📐", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "%,d".format(gameState.blueprints),
                                color = GameBlueprint,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Silver
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🪙", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "%,d".format(gameState.silverCoins),
                                color = GameSilver,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Diamonds
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💎", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${gameState.diamonds}",
                                color = GameDiamond,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // 2. LOCOMOTIVE INSPECTION STAGE (Interactive Carousel + Condition Readout)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E).copy(alpha = 0.92f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .border(1.dp, Color(0xFF2A364F), RoundedCornerShape(10.dp))
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Train Model Name + Health Badge Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(currentTrain.countryFlag, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentTrain.name,
                                color = TrainYellowPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when (currentTrain.type) {
                                            TrainType.DIESEL -> Color(0xFFD97706)
                                            TrainType.STEAM -> Color(0xFFB45309)
                                            TrainType.ELECTRIC_MAGLEV -> Color(0xFF0284C7)
                                        }
                                    )
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = currentTrain.type.name,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        // Overall condition status badge
                        val overallHealth = currentHealth.overallPercent
                        val badgeColor = when {
                            overallHealth >= 85 -> TrainGreen
                            overallHealth >= 65 -> TrainBrightCyan
                            overallHealth >= 45 -> Color(0xFFEAB308)
                            overallHealth >= 25 -> Color(0xFFF97316)
                            else -> TrainSafetyRed
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeColor.copy(alpha = 0.2f))
                                .border(1.dp, badgeColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(badgeColor)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$overallHealth% • ${currentHealth.statusLabel}",
                                    color = badgeColor,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Center Stage: Train 2D Drawing with Prev/Next Navigation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Previous Train Arrow
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E293B))
                                .clickable {
                                    if (selectedIndex > 0) {
                                        selectedIndex--
                                        gamePrefs.selectTrain(unlockedTrains[selectedIndex].id)
                                    } else if (unlockedTrains.isNotEmpty()) {
                                        selectedIndex = unlockedTrains.size - 1
                                        gamePrefs.selectTrain(unlockedTrains[selectedIndex].id)
                                    }
                                }
                                .testTag("maintenance_prev_train"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Prev",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }

                        // Train Rendering Canvas in Diagnostic Depot Cradle
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .padding(horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val cw = size.width
                                val ch = size.height

                                // Draw Depot Service Rail Track & Diagnostic Laser Line
                                val railY = ch * 0.72f
                                drawLine(
                                    color = Color(0xFF64748B),
                                    start = Offset(10f, railY),
                                    end = Offset(cw - 10f, railY),
                                    strokeWidth = 3f
                                )
                                drawLine(
                                    color = Color(0xFF334155),
                                    start = Offset(10f, railY + 5f),
                                    end = Offset(cw - 10f, railY + 5f),
                                    strokeWidth = 2f
                                )

                                // Service Ties
                                var tieX = 15f
                                while (tieX < cw - 10f) {
                                    drawRect(
                                        color = Color(0xFF475569),
                                        topLeft = Offset(tieX, railY + 1f),
                                        size = Size(8f, 8f)
                                    )
                                    tieX += 22f
                                }

                                // Hydraulic lift pedestals
                                drawRect(
                                    color = Color(0xFFEAB308).copy(alpha = 0.7f),
                                    topLeft = Offset(cw * 0.28f, railY + 8f),
                                    size = Size(18f, 16f)
                                )
                                drawRect(
                                    color = Color(0xFFEAB308).copy(alpha = 0.7f),
                                    topLeft = Offset(cw * 0.70f, railY + 8f),
                                    size = Size(18f, 16f)
                                )

                                // Draw the Locomotive
                                drawLocomotiveDetailed(
                                    train = currentTrain,
                                    bodyColor = bodyColor,
                                    stripeColor = stripeColor,
                                    wheelAngleRad = 0f,
                                    widthPx = cw,
                                    heightPx = ch * 0.95f
                                )
                            }
                        }

                        // Next Train Arrow
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E293B))
                                .clickable {
                                    if (selectedIndex < unlockedTrains.size - 1) {
                                        selectedIndex++
                                        gamePrefs.selectTrain(unlockedTrains[selectedIndex].id)
                                    } else if (unlockedTrains.isNotEmpty()) {
                                        selectedIndex = 0
                                        gamePrefs.selectTrain(unlockedTrains[selectedIndex].id)
                                    }
                                }
                                .testTag("maintenance_next_train"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Next",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // Fleet quick switch thumbnails
                    if (unlockedTrains.size > 1) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(unlockedTrains) { trainItem ->
                                val isSelected = trainItem.id == currentTrain.id
                                val trainH = gameState.locomotiveHealthMap[trainItem.id]?.overallPercent ?: 100
                                val hColor = when {
                                    trainH >= 85 -> TrainGreen
                                    trainH >= 65 -> TrainBrightCyan
                                    trainH >= 45 -> Color(0xFFEAB308)
                                    else -> TrainSafetyRed
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B))
                                        .border(
                                            1.dp,
                                            if (isSelected) TrainBrightCyan else Color(0xFF334155),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            val idx = unlockedTrains.indexOfFirst { it.id == trainItem.id }
                                            if (idx >= 0) {
                                                selectedIndex = idx
                                                gamePrefs.selectTrain(trainItem.id)
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(trainItem.countryFlag, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = trainItem.name.take(12),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$trainH%",
                                            color = hColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. COMPONENT SERVICE BAYS (4 Modules: Engine, Gearbox, Wheels, Brakes)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // MODULE 0: ENGINE CORE & CYLINDERS
                item {
                    ComponentServiceCard(
                        index = 0,
                        title = if (currentTrain.type == TrainType.STEAM) "STEAM BOILER & FIREBOX" else "ENGINE CORE & CYLINDERS",
                        icon = "🔥",
                        wearCauseDescription = "Thermal stress & piston wear from high-speed throttle & high engine temperature.",
                        healthValue = currentHealth.engineHealth,
                        costSilver = gamePrefs.getComponentRepairCost(currentHealth.engineHealth),
                        canAfford = gameState.silverCoins >= gamePrefs.getComponentRepairCost(currentHealth.engineHealth),
                        onRepair = {
                            if (gamePrefs.repairComponent(currentTrain.id, 0)) {
                                soundManager.playRepairSound()
                                repairToastMessage = "Engine Serviced & Restored to 100%!"
                            }
                        }
                    )
                }

                // MODULE 1: TRANSMISSION & GEAR TRAIN
                item {
                    ComponentServiceCard(
                        index = 1,
                        title = "TRANSMISSION & DRIVE PINIONS",
                        icon = "⚙️",
                        wearCauseDescription = "Torque friction from hard acceleration, grade climbs & sudden throttle changes.",
                        healthValue = currentHealth.gearboxHealth,
                        costSilver = gamePrefs.getComponentRepairCost(currentHealth.gearboxHealth),
                        canAfford = gameState.silverCoins >= gamePrefs.getComponentRepairCost(currentHealth.gearboxHealth),
                        onRepair = {
                            if (gamePrefs.repairComponent(currentTrain.id, 1)) {
                                soundManager.playRepairSound()
                                repairToastMessage = "Transmission & Pinions Restored to 100%!"
                            }
                        }
                    )
                }

                // MODULE 2: WHEELSETS & ROLLER BEARINGS
                item {
                    ComponentServiceCard(
                        index = 2,
                        title = "WHEELSETS & AXLE BEARINGS",
                        icon = "🛞",
                        wearCauseDescription = "Wheel flange degradation & wheel-rail friction from high velocity mileage and wheel slips.",
                        healthValue = currentHealth.wheelsHealth,
                        costSilver = gamePrefs.getComponentRepairCost(currentHealth.wheelsHealth),
                        canAfford = gameState.silverCoins >= gamePrefs.getComponentRepairCost(currentHealth.wheelsHealth),
                        onRepair = {
                            if (gamePrefs.repairComponent(currentTrain.id, 2)) {
                                soundManager.playRepairSound()
                                repairToastMessage = "Wheelsets Trued & Bearings Replaced!"
                            }
                        }
                    )
                }

                // MODULE 3: BRAKE PNEUMATICS & SHOES
                item {
                    ComponentServiceCard(
                        index = 3,
                        title = "BRAKE PNEUMATICS & FRICTION SHOES",
                        icon = "🛑",
                        wearCauseDescription = "Thermal brake pad wear from dynamic deceleration and high-speed emergency stops.",
                        healthValue = currentHealth.brakesHealth,
                        costSilver = gamePrefs.getComponentRepairCost(currentHealth.brakesHealth),
                        canAfford = gameState.silverCoins >= gamePrefs.getComponentRepairCost(currentHealth.brakesHealth),
                        onRepair = {
                            if (gamePrefs.repairComponent(currentTrain.id, 3)) {
                                soundManager.playRepairSound()
                                repairToastMessage = "Brake Pads & Pneumatics Replaced!"
                            }
                        }
                    )
                }
            }

            // Toast Message Notification
            repairToastMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(TrainGreen.copy(alpha = 0.9f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✅ $msg",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // 4. BOTTOM ACTION DOCK (Timed Repair vs Fast Diamond Repair)
            val overhaulCost = gamePrefs.getOverhaulCost(currentTrain.id)
            val isFullyRepaired = currentHealth.overallPercent >= 100
            val isTrainInTimedRepair = gameState.activeRepairTrainId == currentTrain.id
            val isAnyTrainInTimedRepair = gameState.activeRepairTrainId != null && gameState.activeRepairTrainId != currentTrain.id

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isTrainInTimedRepair) Color(0xFF1E293B) else Color(0xFF161E2E)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .border(
                        1.dp,
                        if (isTrainInTimedRepair) TrainBrightCyan else Color(0xFF2A364F),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                if (isTrainInTimedRepair) {
                    val remainingMs = (gameState.repairEndTimestamp - currentTimeMs).coerceAtLeast(0L)
                    val remainingSec = remainingMs / 1000L
                    val formattedTime = "%02d:%02d".format(remainingSec / 60, remainingSec % 60)
                    val totalSec = gameState.repairDurationSeconds.coerceAtLeast(1)
                    val progress = (1f - (remainingSec.toFloat() / totalSec.toFloat())).coerceIn(0f, 1f)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⏱️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "TIMED OVERHAUL IN PROGRESS",
                                        color = TrainBrightCyan,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Mechanics servicing engine, transmission, wheels & brakes ($formattedTime remaining)",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                // Wait button (shows countdown, disabled)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF0F172A))
                                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("⏳", fontSize = 10.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("WAIT ($formattedTime)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                                    }
                                }

                                // Fast Repair with Diamonds
                                Button(
                                    onClick = {
                                        if (gamePrefs.fastRepairWithDiamonds(currentTrain.id, 8)) {
                                            soundManager.playRepairSound()
                                            repairToastMessage = "Instant Factory Fast Repair Complete!"
                                        }
                                    },
                                    enabled = gameState.diamonds >= 8,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0284C7),
                                        disabledContainerColor = Color(0xFF1E293B)
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("maintenance_fast_repair_diamonds")
                                ) {
                                    Text("⚡ FAST REPAIR (💎 8)", color = Color.White, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TrainBrightCyan,
                            trackColor = Color(0xFF0F172A)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "FLEET MASTER OVERHAUL",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                            Text(
                                text = if (isFullyRepaired) {
                                    "All modules at 100% peak efficiency"
                                } else if (isAnyTrainInTimedRepair) {
                                    "Another locomotive is currently in the timed service bay"
                                } else {
                                    "Wait 1-2 mins (Silver + 70 Blueprints) or Fast Repair with Diamonds"
                                },
                                color = if (isFullyRepaired) TrainGreen else Color(0xFF94A3B8),
                                fontSize = 9.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // 1-2 Mins Timed Repair Button (Silver + 70 Blueprints)
                            val canStartTimed = !isFullyRepaired && !isAnyTrainInTimedRepair &&
                                    gameState.silverCoins >= overhaulCost && gameState.blueprints >= 70 && overhaulCost > 0

                            Button(
                                onClick = {
                                    if (gamePrefs.startTimedRepair(currentTrain.id, durationSeconds = 90, blueprintCost = 70)) {
                                        soundManager.playRepairSound()
                                        repairToastMessage = "Timed Overhaul Started (1.5 min)! Mechanics at work."
                                    }
                                },
                                enabled = canStartTimed,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TrainGreen,
                                    disabledContainerColor = Color(0xFF1E293B)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("maintenance_timed_repair_button")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("⏱️", fontSize = 8.5.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (isFullyRepaired) "ALL REPAIRED" else "1.5 MIN OVERHAUL",
                                            color = if (isFullyRepaired) Color(0xFF94A3B8) else Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 8.5.sp
                                        )
                                    }
                                    if (!isFullyRepaired) {
                                        Text(
                                            text = "🪙 %,d  +  📐 70".format(overhaulCost),
                                            color = if (canStartTimed) TrainYellowPrimary else Color(0xFF94A3B8),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 7.5.sp
                                        )
                                    }
                                }
                            }

                            // Fast Repair with Diamonds (Instant)
                            Button(
                                onClick = {
                                    if (gamePrefs.fastRepairWithDiamonds(currentTrain.id, 8)) {
                                        soundManager.playRepairSound()
                                        repairToastMessage = "Instant Fast Repair Complete!"
                                    }
                                },
                                enabled = !isFullyRepaired && gameState.diamonds >= 8,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0284C7),
                                    disabledContainerColor = Color(0xFF1E293B)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("maintenance_fast_repair_diamonds")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("⚡", fontSize = 8.5.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("FAST REPAIR", color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                                    }
                                    Text("💎 8", color = GameDiamond, fontWeight = FontWeight.Bold, fontSize = 7.5.sp)
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
private fun ComponentServiceCard(
    index: Int,
    title: String,
    icon: String,
    wearCauseDescription: String,
    healthValue: Float,
    costSilver: Int,
    canAfford: Boolean,
    onRepair: () -> Unit
) {
    val healthInt = healthValue.toInt().coerceIn(0, 100)
    val isPristine = healthInt >= 100

    val barColor = when {
        healthInt >= 80 -> TrainGreen
        healthInt >= 60 -> TrainBrightCyan
        healthInt >= 40 -> Color(0xFFEAB308)
        healthInt >= 20 -> Color(0xFFF97316)
        else -> TrainSafetyRed
    }

    val animatedProgress by animateFloatAsState(
        targetValue = healthValue / 100f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "health_bar"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF182232)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2C394F), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Component details & Condition Progress Bar
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$healthInt%",
                        color = barColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = barColor,
                    trackColor = Color(0xFF0F172A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = wearCauseDescription,
                    color = Color(0xFF94A3B8),
                    fontSize = 8.5.sp,
                    lineHeight = 10.5.sp
                )
            }

            // Right: Repair Action Button
            Button(
                onClick = onRepair,
                enabled = !isPristine && canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrainBrightCyan,
                    disabledContainerColor = Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                modifier = Modifier.testTag("repair_component_$index")
            ) {
                if (isPristine) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TrainGreen,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "100% OK",
                            color = TrainGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = if (canAfford) Color(0xFF0F172A) else Color(0xFF64748B),
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "REPAIR",
                                color = if (canAfford) Color(0xFF0F172A) else Color(0xFF64748B),
                                fontWeight = FontWeight.Black,
                                fontSize = 8.5.sp
                            )
                        }
                        Text(
                            text = "🪙 %,d".format(costSilver),
                            color = if (canAfford) Color(0xFF1E293B) else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.5.sp
                        )
                    }
                }
            }
        }
    }
}
