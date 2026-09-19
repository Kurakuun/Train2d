package com.example.ui.workshop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.data.model.TrainCategory
import com.example.data.model.TrainModel
import com.example.data.model.TrainType
import com.example.data.storage.GamePreferences
import com.example.data.storage.GameState
import com.example.data.storage.LocomotiveHealth
import com.example.ui.components.drawLocomotiveDetailed
import com.example.ui.components.drawSecretTrainSilhouette
import com.example.ui.inventory.InventoryDialog
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
fun WorkshopScreen(
    gameState: GameState,
    gamePrefs: GamePreferences,
    soundManager: TrainSoundManager,
    onBackToMenu: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenPaintShop: (TrainModel) -> Unit,
    onOpenMaintenance: () -> Unit = {}
) {
    var selectedIndex by remember {
        val idx = GameContent.ALL_TRAINS.indexOfFirst { it.id == gameState.selectedTrainId }
        mutableIntStateOf(if (idx >= 0) idx else 0)
    }

    val currentTrain = GameContent.ALL_TRAINS[selectedIndex]
    val isUnlocked = gameState.unlockedTrainIds.contains(currentTrain.id)

    // Current levels
    val gbLvl = gameState.gearboxLevels[currentTrain.id] ?: 1
    val genLvl = gameState.generatorLevels[currentTrain.id] ?: 1
    val engLvl = gameState.engineLevels[currentTrain.id] ?: 1
    val whLvl = gameState.wheelsLevels[currentTrain.id] ?: 1
    val overallLevel = gamePrefs.getOverallTrainLevel(currentTrain.id)

    // Dynamic stats calculated from base + upgrade levels
    val currentSpeed = currentTrain.baseSpeed + (gbLvl * 1.2f)
    val nextSpeed = currentSpeed + 1.2f

    val currentReliability = currentTrain.baseReliability + (genLvl * 0.8f)
    val nextReliability = currentReliability + 0.8f

    val currentPower = currentTrain.basePower + (engLvl * 1.5f)
    val nextPower = currentPower + 1.5f

    val currentAdherence = currentTrain.baseAdherence + (whLvl * 0.5f)
    val nextAdherence = currentAdherence + 0.5f

    val activeVariantKey = gameState.selectedLiveryVariants[currentTrain.id]
        ?: currentTrain.availableLiveries.firstOrNull()?.id ?: "V1_VIRGIN"
    val activeLivery = currentTrain.availableLiveries.find { it.id == activeVariantKey }
        ?: currentTrain.availableLiveries.firstOrNull()

    val bodyColor = Color(gameState.customBodyColors[currentTrain.id]
        ?: activeLivery?.primaryColor ?: currentTrain.defaultBodyColor)
    val stripeColor = Color(gameState.customStripeColors[currentTrain.id]
        ?: activeLivery?.accentColor ?: currentTrain.defaultStripeColor)

    val trainHealth = gameState.locomotiveHealthMap[currentTrain.id] ?: LocomotiveHealth()

    var showInventoryDialog by remember { mutableStateOf(false) }
    var showRosterDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("workshop_screen")
    ) {
        // 1. INDUSTRIAL DEPOT HANGAR CANVAS
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Workshop Wall Slate Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A))
                )
            )

            // Steel Overhead Truss Beams
            for (i in 0 until 7) {
                val bx = i * (size.width / 6f)
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(bx, 0f),
                    end = Offset(bx, size.height * 0.72f),
                    strokeWidth = 6f
                )
                drawLine(
                    color = Color(0xFF475569).copy(alpha = 0.4f),
                    start = Offset(bx - 36f, 0f),
                    end = Offset(bx + 36f, 70f),
                    strokeWidth = 3f
                )
            }

            // Arched Depot Windows with Industrial Daylight
            val wWidth = 100f
            val wHeight = 130f
            val winY = 48f
            for (i in 0 until 4) {
                val winX = 50f + i * (size.width / 3.4f)
                drawRoundRect(
                    color = Color(0xFF38BDF8).copy(alpha = 0.25f),
                    topLeft = Offset(winX, winY),
                    size = Size(wWidth, wHeight),
                    cornerRadius = CornerRadius(30f, 30f)
                )
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(winX + wWidth / 2f, winY),
                    end = Offset(winX + wWidth / 2f, winY + wHeight),
                    strokeWidth = 3f
                )
                drawLine(
                    color = Color(0xFF1E293B),
                    start = Offset(winX, winY + wHeight / 2f),
                    end = Offset(winX + wWidth, winY + wHeight / 2f),
                    strokeWidth = 3f
                )
            }

            // Overhead Warm Depot Spotlights
            val lampY = 32f
            for (i in 0 until 3) {
                val lampX = (size.width * 0.22f) + i * (size.width * 0.28f)
                val cone = Path().apply {
                    moveTo(lampX, lampY + 10f)
                    lineTo(lampX - 85f, size.height * 0.76f)
                    lineTo(lampX + 85f, size.height * 0.76f)
                    close()
                }
                drawPath(
                    path = cone,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x35FEF08A), Color(0x00FFFFFF)),
                        startY = lampY,
                        endY = size.height * 0.76f
                    )
                )
                // Industrial shade
                drawCircle(color = Color(0xFF065F46), radius = 10f, center = Offset(lampX, lampY + 6f))
                drawCircle(color = Color(0xFFFEF08A), radius = 5f, center = Offset(lampX, lampY + 10f))
            }

            // Track Platform & Pit Bed
            val trackY = size.height * 0.70f
            drawRect(
                color = Color(0xFF1F2937),
                topLeft = Offset(0f, trackY),
                size = Size(size.width, 24f)
            )
            // Wooden Sleepers
            val sleeperW = 16f
            val sleeperGap = 32f
            var sx = 0f
            while (sx < size.width) {
                drawRect(
                    color = Color(0xFF3E2723),
                    topLeft = Offset(sx, trackY + 2f),
                    size = Size(sleeperW, 18f)
                )
                sx += sleeperGap
            }
            // Steel Rails
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(0f, trackY + 4f),
                end = Offset(size.width, trackY + 4f),
                strokeWidth = 4f
            )
            drawLine(
                color = Color(0xFF64748B),
                start = Offset(0f, trackY + 8f),
                end = Offset(size.width, trackY + 8f),
                strokeWidth = 2f
            )
            // Lower inspection pit
            drawRect(
                color = Color(0xFF0F172A),
                topLeft = Offset(0f, trackY + 24f),
                size = Size(size.width, size.height - trackY - 24f)
            )
        }

        // 2. MAIN WORKSHOP UI LAYER
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP SECTION: Header bar + PROMINENT TRAIN NAME AT THE TOP + CATEGORY BAR
            Column(modifier = Modifier.fillMaxWidth()) {
                // A. TOP HEADER ROW (Title, Currencies, Depot & Roster, Menu)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Driver Profile Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Train,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LVL ${gameState.driverLevel}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }

                    // Fleet Roster & Depot Shortcuts
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TrainYellowPrimary)
                                .clickable { showRosterDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("workshop_roster_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsTransit, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ROSTER", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0284C7))
                                .clickable { showInventoryDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("workshop_inventory_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("DEPOT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                            }
                        }
                    }

                    // Currencies (Blueprints, Silver, Diamonds)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WorkshopCurrencyPill("📐", "%,d".format(gameState.blueprints), GameBlueprint)
                        WorkshopCurrencyPill("🪙", "%,d".format(gameState.silverCoins), GameSilver)
                        WorkshopCurrencyPill("💎", "%,d".format(gameState.diamonds), GameDiamond)

                        // Menu button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(TrainGreen)
                                .clickable {
                                    soundManager.playLeverClick()
                                    onBackToMenu()
                                }
                                .testTag("workshop_menu_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // B. PROMINENT TRAIN NAME AT THE TOP (MANDATORY REQUIREMENT)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A),
                                    Color(0xFF1E293B)
                                )
                            )
                        )
                        .border(1.5.dp, TrainYellowPrimary.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("workshop_train_name_top_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (currentTrain.isSecret && !isUnlocked) "❓" else currentTrain.countryFlag,
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentTrain.isSecret && !isUnlocked) "??? SECRET TRAIN ???" else currentTrain.name,
                                    color = if (currentTrain.isSecret && !isUnlocked) TrainBrightCyan else Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${currentTrain.category.displayName.uppercase()} • ${currentTrain.type.name} • SOUND: ${currentTrain.soundProfile.name.replace("_", " ")}",
                                    color = TrainBrightCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Level Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF334155))
                                    .border(1.dp, Color(0xFF64748B), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "LEVEL $overallLevel / ${currentTrain.maxUpgradeLevel}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            }

                            // Health Status Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (trainHealth.overallPercent >= 80) TrainGreen else TrainSafetyRed)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "🔧 ${trainHealth.overallPercent}%",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // C. REDESIGNED TRAIN CATEGORY UI (Sleek Horizontal Scrollable Tabs)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrainCategory.values().forEach { category ->
                        val isCatActive = currentTrain.category == category
                        val categoryTrainCount = GameContent.ALL_TRAINS.count { it.category == category }
                        val ownedInCat = GameContent.ALL_TRAINS.count { it.category == category && gameState.unlockedTrainIds.contains(it.id) }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isCatActive) TrainYellowPrimary
                                    else Color(0xFF1E293B).copy(alpha = 0.9f)
                                )
                                .border(
                                    width = if (isCatActive) 1.5.dp else 1.dp,
                                    color = if (isCatActive) Color.White else Color(0xFF334155),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    val firstInCat = GameContent.ALL_TRAINS.indexOfFirst { it.category == category }
                                    if (firstInCat >= 0) {
                                        selectedIndex = firstInCat
                                        if (gameState.unlockedTrainIds.contains(GameContent.ALL_TRAINS[firstInCat].id)) {
                                            gamePrefs.selectTrain(GameContent.ALL_TRAINS[firstInCat].id)
                                        }
                                        soundManager.playLeverClick()
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("train_category_${category.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(category.icon, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = category.displayName,
                                    color = if (isCatActive) Color.Black else Color.White,
                                    fontWeight = if (isCatActive) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isCatActive) Color.Black.copy(alpha = 0.2f) else Color(0xFF334155))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "$ownedInCat/$categoryTrainCount",
                                        color = if (isCatActive) Color.Black else Color(0xFF94A3B8),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // CENTER SECTION: Train Inspection Stage & Navigation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Left Quick Controls: PAINT SHOP, HORN, SERVICE BAY
                Column(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 1. PAINT SHOP BUTTON (Brings player to Paint Shop Screen)
                    Box(
                        modifier = Modifier
                            .size(width = 62.dp, height = 24.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(TrainYellowPrimary)
                            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(5.dp))
                            .clickable {
                                soundManager.playLeverClick()
                                onOpenPaintShop(currentTrain)
                            }
                            .testTag("workshop_paint_shop_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = "Paint Shop", tint = Color.Black, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("PAINT", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                        }
                    }

                    // 2. HORN BUTTON
                    Box(
                        modifier = Modifier
                            .size(width = 62.dp, height = 24.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(TrainSafetyRed)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
                            .clickable {
                                soundManager.blastHorn(currentTrain.type, currentTrain.soundProfile)
                            }
                            .testTag("workshop_horn_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📢", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("HORN", color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                        }
                    }

                    // 3. SERVICE BAY BUTTON (Brings player to redesigned Maintenance Bay!)
                    Box(
                        modifier = Modifier
                            .size(width = 62.dp, height = 24.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(if (trainHealth.overallPercent < 80) Color(0xFFDC2626) else Color(0xFF0284C7))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
                            .clickable {
                                soundManager.playLeverClick()
                                onOpenMaintenance()
                            }
                            .testTag("workshop_maintenance_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("SRV ${trainHealth.overallPercent}%", color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.sp)
                        }
                    }
                }

                // Left Navigation Arrow
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 70.dp)
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(TrainGreen)
                        .clickable {
                            if (selectedIndex > 0) {
                                selectedIndex--
                                if (gameState.unlockedTrainIds.contains(GameContent.ALL_TRAINS[selectedIndex].id)) {
                                    gamePrefs.selectTrain(GameContent.ALL_TRAINS[selectedIndex].id)
                                }
                                soundManager.playLeverClick()
                            }
                        }
                        .testTag("workshop_prev_train"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Prev", tint = Color.White, modifier = Modifier.size(13.dp))
                }

                // Central Locomotive Display Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (currentTrain.isSecret && !isUnlocked) {
                            drawSecretTrainSilhouette(
                                widthPx = size.width,
                                heightPx = size.height
                            )
                        } else {
                            val activeVariant = gameState.selectedLiveryVariants[currentTrain.id] ?: "V1_VIRGIN"
                            drawLocomotiveDetailed(
                                train = currentTrain,
                                bodyColor = bodyColor,
                                stripeColor = stripeColor,
                                wheelAngleRad = 0f,
                                widthPx = size.width,
                                heightPx = size.height,
                                variant = activeVariant
                            )
                        }
                    }

                    // Lock Overlay if not unlocked
                    if (!isUnlocked) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (currentTrain.isSecret) Color.Transparent else Color.Black.copy(alpha = 0.65f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (currentTrain.isSecret) {
                                    val matchingPromo = GameContent.PROMO_CODES.find {
                                        it.secretTrainId == currentTrain.id || it.secretTrainIds.contains(currentTrain.id)
                                    }
                                    val codeHint = matchingPromo?.code ?: "Crazy fast"
                                    Spacer(modifier = Modifier.height(70.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                                            .border(1.dp, TrainBrightCyan, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            "🔒 SECRET TRAIN — Redeem code '$codeHint' in Promo Codes!",
                                            color = TrainBrightCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                } else {
                                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = TrainYellowPrimary, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = {
                                            gamePrefs.unlockTrain(currentTrain.id, currentTrain.priceGold, currentTrain.priceDiamonds)
                                            soundManager.playChime()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TrainGreen),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            "BUY: 🪙 ${currentTrain.priceGold} | 💎 ${currentTrain.priceDiamonds}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Right Navigation Arrow
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(TrainGreen)
                        .clickable {
                            if (selectedIndex < GameContent.ALL_TRAINS.size - 1) {
                                selectedIndex++
                                if (gameState.unlockedTrainIds.contains(GameContent.ALL_TRAINS[selectedIndex].id)) {
                                    gamePrefs.selectTrain(GameContent.ALL_TRAINS[selectedIndex].id)
                                }
                                soundManager.playLeverClick()
                            }
                        }
                        .testTag("workshop_next_train"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(13.dp))
                }
            }

            // BOTTOM SECTION: UPGRADES ON THE BOTTOM-LEFT & STATS ON THE BOTTOM-RIGHT
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // LEFT SIDE: DOCK (MAP BUTTON) + 4 UPGRADE CARDS (Cooling System, Sand, Engine, Wheels)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // MAP BUTTON
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MAP", color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.sp)
                        Box(
                            modifier = Modifier
                                .size(width = 36.dp, height = 30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(TrainGreen)
                                .clickable {
                                    soundManager.playLeverClick()
                                    onOpenMap()
                                }
                                .testTag("workshop_map_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Public, contentDescription = "Map", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    // 1. COOLING SYSTEM (Slot 0 - replaces Gearbox)
                    UpgradeCard(
                        title = "COOLING",
                        level = gbLvl,
                        maxLevel = currentTrain.maxUpgradeLevel,
                        cost = gamePrefs.getUpgradeCost(gbLvl),
                        canAfford = gameState.silverCoins >= gamePrefs.getUpgradeCost(gbLvl) && gbLvl < currentTrain.maxUpgradeLevel,
                        iconType = "cooling",
                        statDelta = "+1.2 SPD",
                        onUpgrade = {
                            if (gamePrefs.upgradeModule(currentTrain.id, 0)) {
                                soundManager.playChime()
                            }
                        }
                    )

                    // 2. SAND SYSTEM (Slot 1 - replaces Generator/Boiler)
                    UpgradeCard(
                        title = "SAND",
                        level = genLvl,
                        maxLevel = currentTrain.maxUpgradeLevel,
                        cost = gamePrefs.getUpgradeCost(genLvl),
                        canAfford = gameState.silverCoins >= gamePrefs.getUpgradeCost(genLvl) && genLvl < currentTrain.maxUpgradeLevel,
                        iconType = "sand",
                        statDelta = "+0.8 REL",
                        onUpgrade = {
                            if (gamePrefs.upgradeModule(currentTrain.id, 1)) {
                                soundManager.playChime()
                            }
                        }
                    )

                    // 3. ENGINE (Slot 2)
                    UpgradeCard(
                        title = "ENGINE",
                        level = engLvl,
                        maxLevel = currentTrain.maxUpgradeLevel,
                        cost = gamePrefs.getUpgradeCost(engLvl),
                        canAfford = gameState.silverCoins >= gamePrefs.getUpgradeCost(engLvl) && engLvl < currentTrain.maxUpgradeLevel,
                        iconType = "engine",
                        statDelta = "+1.5 PWR",
                        onUpgrade = {
                            if (gamePrefs.upgradeModule(currentTrain.id, 2)) {
                                soundManager.playChime()
                            }
                        }
                    )

                    // 4. WHEELS (Slot 3)
                    UpgradeCard(
                        title = "WHEELS",
                        level = whLvl,
                        maxLevel = currentTrain.maxUpgradeLevel,
                        cost = gamePrefs.getUpgradeCost(whLvl),
                        canAfford = gameState.silverCoins >= gamePrefs.getUpgradeCost(whLvl) && whLvl < currentTrain.maxUpgradeLevel,
                        iconType = "wheels",
                        statDelta = "+0.5 ADH",
                        onUpgrade = {
                            if (gamePrefs.upgradeModule(currentTrain.id, 3)) {
                                soundManager.playChime()
                            }
                        }
                    )
                }

                // RIGHT SIDE: STATS GAUGE BAR + SHOP BUTTON
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // STATS GAUGE BAR
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.95f))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(
                            icon = Icons.Default.Speed,
                            label = "SPD",
                            currentVal = "%.0f".format(currentSpeed),
                            nextVal = "%.0f".format(nextSpeed),
                            progress = (currentSpeed / 80f).coerceIn(0f, 1f)
                        )
                        StatItem(
                            icon = Icons.Default.Security,
                            label = "REL",
                            currentVal = "%.1f".format(currentReliability),
                            nextVal = "%.1f".format(nextReliability),
                            progress = (currentReliability / 40f).coerceIn(0f, 1f)
                        )
                        StatItem(
                            icon = Icons.Default.ElectricBolt,
                            label = "PWR",
                            currentVal = "%.1f".format(currentPower),
                            nextVal = "%.1f".format(nextPower),
                            progress = (currentPower / 60f).coerceIn(0f, 1f)
                        )
                        StatItem(
                            icon = Icons.Default.CenterFocusStrong,
                            label = "ADH",
                            currentVal = "%.1f".format(currentAdherence),
                            nextVal = "%.1f".format(nextAdherence),
                            progress = (currentAdherence / 20f).coerceIn(0f, 1f)
                        )
                    }

                    // SHOP BUTTON
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SHOP", color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.sp)
                        Box(
                            modifier = Modifier
                                .size(width = 36.dp, height = 30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(TrainYellowDark)
                                .clickable {
                                    soundManager.playLeverClick()
                                    onOpenShop()
                                }
                                .testTag("workshop_shop_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = "Shop", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        if (showInventoryDialog) {
            InventoryDialog(
                gameState = gameState,
                gamePrefs = gamePrefs,
                soundManager = soundManager,
                onDismiss = { showInventoryDialog = false }
            )
        }

        if (showRosterDialog) {
            TrainRosterDialog(
                gameState = gameState,
                gamePrefs = gamePrefs,
                selectedTrainId = currentTrain.id,
                onSelectTrain = { newTrainId ->
                    val idx = GameContent.ALL_TRAINS.indexOfFirst { it.id == newTrainId }
                    if (idx >= 0) {
                        selectedIndex = idx
                        gamePrefs.selectTrain(newTrainId)
                    }
                },
                onDismiss = { showRosterDialog = false }
            )
        }
    }
}

@Composable
fun UpgradeCard(
    title: String,
    level: Int,
    maxLevel: Int,
    cost: Int,
    canAfford: Boolean,
    iconType: String,
    statDelta: String,
    onUpgrade: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(58.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(6.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Slot Header Pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TrainBrightCyan)
                .padding(horizontal = 3.dp, vertical = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Color.Black, fontWeight = FontWeight.Black, fontSize = 7.5.sp)
            Text("$level/$maxLevel", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 7.5.sp)
        }

        // Icon Box with Projection Delta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(20.dp)) {
                when (iconType) {
                    "cooling", "gearbox" -> {
                        // Radiator grille and cooling fan
                        drawRoundRect(
                            color = Color(0xFF0284C7),
                            topLeft = Offset(2f, 3f),
                            size = Size(16f, 14f),
                            cornerRadius = CornerRadius(2f, 2f)
                        )
                        // Grille slats
                        for (gx in 0 until 4) {
                            val sx = 4f + gx * 3.5f
                            drawLine(color = Color(0xFFE2E8F0), start = Offset(sx, 5f), end = Offset(sx, 15f), strokeWidth = 1.2f)
                        }
                    }
                    "sand", "generator" -> {
                        // Sand dome / sandbox container with nozzle
                        drawRoundRect(
                            color = Color(0xFFD97706),
                            topLeft = Offset(4f, 3f),
                            size = Size(12f, 10f),
                            cornerRadius = CornerRadius(3f, 3f)
                        )
                        // Sand nozzle line
                        drawLine(color = Color(0xFFFEF3C7), start = Offset(10f, 13f), end = Offset(13f, 17f), strokeWidth = 1.8f)
                    }
                    "engine" -> {
                        drawRoundRect(
                            color = Color(0xFF64748B),
                            topLeft = Offset(2f, 2f),
                            size = Size(16f, 16f),
                            cornerRadius = CornerRadius(2f, 2f)
                        )
                        drawLine(color = Color(0xFFCBD5E1), start = Offset(10f, 2f), end = Offset(10f, 18f), strokeWidth = 2f)
                    }
                    else -> {
                        // Wheels
                        drawCircle(color = Color(0xFF334155), radius = 6f, center = Offset(6f, 10f))
                        drawCircle(color = Color(0xFF94A3B8), radius = 3.5f, center = Offset(6f, 10f))
                        drawCircle(color = Color(0xFF334155), radius = 6f, center = Offset(14f, 10f))
                        drawCircle(color = Color(0xFF94A3B8), radius = 3.5f, center = Offset(14f, 10f))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 2.dp, vertical = 1.dp)
            ) {
                Text(statDelta, color = TrainYellowPrimary, fontWeight = FontWeight.Bold, fontSize = 6.5.sp)
            }
        }

        // Upgrade Button with Cost
        val isMaxed = level >= maxLevel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(if (isMaxed) Color(0xFF475569) else if (canAfford) TrainGreen else Color(0xFF334155))
                .clickable(enabled = canAfford && !isMaxed) { onUpgrade() },
            contentAlignment = Alignment.Center
        ) {
            if (isMaxed) {
                Text("MAX", color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.sp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⬆ ", color = Color.White, fontWeight = FontWeight.Black, fontSize = 7.5.sp)
                    Text("%,d ".format(cost), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 7.5.sp)
                    Text("🪙", fontSize = 7.sp)
                }
            }
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    currentVal: String,
    nextVal: String,
    progress: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        }
        Text(
            text = "$currentVal ➔ $nextVal",
            color = TrainYellowPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
        )
        Box(
            modifier = Modifier
                .width(62.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF0F172A))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(TrainLightGreen)
            )
        }
    }
}

@Composable
fun WorkshopCurrencyPill(
    emoji: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E293B))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(emoji, fontSize = 11.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
