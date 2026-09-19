package com.example.ui.workshop

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TrainSoundManager
import com.example.data.model.LiveryVariant
import com.example.data.model.TrainModel
import com.example.data.storage.GamePreferences
import com.example.data.storage.GameState
import com.example.ui.components.drawLocomotiveDetailed
import com.example.ui.theme.GameBlueprint
import com.example.ui.theme.GameDiamond
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameSilver
import com.example.ui.theme.TrainBrightCyan
import com.example.ui.theme.TrainCharcoal
import com.example.ui.theme.TrainDarkSteel
import com.example.ui.theme.TrainLightGreen
import com.example.ui.theme.TrainSafetyRed
import com.example.ui.theme.TrainYellowPrimary

private data class PaintPaletteEntry(val name: String, val color: Color)

private val CURATED_PAINT_PALETTE = listOf(
    PaintPaletteEntry("Metra Navy", Color(0xFF10316B)),
    PaintPaletteEntry("Safety Orange", Color(0xFFF95700)),
    PaintPaletteEntry("Fluted Stainless", Color(0xFFCBD5E1)),
    PaintPaletteEntry("Glacier White", Color(0xFFF8FAFC)),
    PaintPaletteEntry("Midnight Black", Color(0xFF0F172A)),
    PaintPaletteEntry("ČD Najbrt Blue", Color(0xFF0284C7)),
    PaintPaletteEntry("ČD Speed Red", Color(0xFFDC2626)),
    PaintPaletteEntry("Union Yellow", Color(0xFFEAB308)),
    PaintPaletteEntry("British Racing Green", Color(0xFF15803D)),
    PaintPaletteEntry("CP Tuscan Maroon", Color(0xFF881337)),
    PaintPaletteEntry("Electric Cyan", Color(0xFF06B6D4)),
    PaintPaletteEntry("Deep Plum", Color(0xFF581C87)),
    PaintPaletteEntry("Dark Gunmetal", Color(0xFF334155)),
    PaintPaletteEntry("Desert Sand", Color(0xFFD97706)),
    PaintPaletteEntry("Anthracite", Color(0xFF262626)),
    PaintPaletteEntry("Apple Green", Color(0xFF4ADE80))
)

@Composable
fun PaintShopScreen(
    train: TrainModel,
    gameState: GameState,
    gamePrefs: GamePreferences,
    soundManager: TrainSoundManager,
    onBackToWorkshop: () -> Unit
) {
    val initialBodyLong = gameState.customBodyColors[train.id] ?: train.defaultBodyColor
    val initialStripeLong = gameState.customStripeColors[train.id] ?: train.defaultStripeColor
    val activeLiveryId = gameState.selectedLiveryVariants[train.id]
        ?: train.availableLiveries.firstOrNull()?.id ?: "V1_VIRGIN"

    var selectedBodyColor by remember(train.id) { mutableStateOf(Color(initialBodyLong)) }
    var selectedStripeColor by remember(train.id) { mutableStateOf(Color(initialStripeLong)) }
    var selectedLiveryId by remember(train.id) { mutableStateOf(activeLiveryId) }
    var activeTab by remember { mutableIntStateOf(0) }
    var showSavedMessage by remember { mutableStateOf(false) }

    // Live preview wheel rotation
    var wheelAngle by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                wheelAngle = (wheelAngle + 0.035f) % (2f * Math.PI.toFloat())
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("paint_shop_screen"),
        color = Color(0xFF0A0F1D)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            // TOP HEADER BAR: Back to Workshop + Title + Currencies
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .clickable {
                                soundManager.playLeverClick()
                                onBackToWorkshop()
                            }
                            .testTag("paint_shop_back_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Workshop",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "PAINT SHOP & LIVERY STUDIO",
                            color = TrainYellowPrimary,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "CUSTOMIZE FLEET COLORS & AUTHENTIC LIVERIES",
                            color = Color(0xFF94A3B8),
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Currency Badges
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Blueprints
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, GameBlueprint.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text("📐 %,d".format(gameState.blueprints), color = GameBlueprint, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                    }
                    // Silver
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF94A3B8).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text("🪙 %,d".format(gameState.silverCoins), color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                    }
                    // Diamonds
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, TrainBrightCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text("💎 %,d".format(gameState.diamonds), color = TrainBrightCyan, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                    }
                }
            }

            // PROMINENT TRAIN NAME AT THE TOP (MANDATORY REQUIREMENT)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A),
                                Color(0xFF1E293B)
                            )
                        )
                    )
                    .border(1.dp, TrainYellowPrimary.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 1.dp)
                    .testTag("paint_shop_train_name_banner")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = train.countryFlag, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Column {
                            Text(
                                text = train.name,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "${train.category.displayName.uppercase()} • ${train.type.name} • SOUND: ${train.soundProfile.name.replace("_", " ")}",
                                color = TrainBrightCyan,
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TrainYellowPrimary)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${train.category.icon} ${train.category.displayName}",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // LIVE LOCOMOTIVE INSPECTION STAGE (Ultra-compact to maximize view of liveries and paint options)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background Track Bed
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val railY = size.height * 0.78f
                    // Ballast
                    drawRect(
                        color = Color(0xFF23272F),
                        topLeft = Offset(0f, railY - 2f),
                        size = Size(size.width, 10f)
                    )
                    // Sleepers
                    val sleeperWidth = 8f
                    val sleeperGap = 16f
                    var sx = 0f
                    while (sx < size.width) {
                        drawRect(
                            color = Color(0xFF3E2723),
                            topLeft = Offset(sx, railY - 1f),
                            size = Size(sleeperWidth, 8f)
                        )
                        sx += sleeperGap
                    }
                    // Steel Rail
                    drawLine(
                        color = Color(0xFFCBD5E1),
                        start = Offset(0f, railY),
                        end = Offset(size.width, railY),
                        strokeWidth = 2.5f
                    )
                    drawLine(
                        color = Color(0xFF64748B),
                        start = Offset(0f, railY + 1.2f),
                        end = Offset(size.width, railY + 1.2f),
                        strokeWidth = 1f
                    )
                }

                // Locomotive Drawn Live
                Box(
                    modifier = Modifier
                        .size(width = 170.dp, height = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawLocomotiveDetailed(
                            train = train,
                            bodyColor = selectedBodyColor,
                            stripeColor = selectedStripeColor,
                            wheelAngleRad = wheelAngle,
                            widthPx = 160f,
                            heightPx = 38f,
                            variant = selectedLiveryId
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // TABS: Body Color, Stripe Color, Liveries
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = TrainYellowPrimary,
                        height = 2.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0; soundManager.playLeverClick() },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("BODY COLOR", fontWeight = FontWeight.Bold, fontSize = 8.5.sp)
                        }
                    }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1; soundManager.playLeverClick() },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("STRIPE", fontWeight = FontWeight.Bold, fontSize = 8.5.sp)
                        }
                    }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2; soundManager.playLeverClick() },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("LIVERIES (${train.availableLiveries.size})", fontWeight = FontWeight.Bold, fontSize = 8.5.sp)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // TAB CONTENT
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    0 -> ColorSelectionPanel(
                        palette = CURATED_PAINT_PALETTE,
                        selectedColor = selectedBodyColor,
                        onColorSelected = {
                            selectedBodyColor = it
                            soundManager.playChime()
                        },
                        tagPrefix = "body_color"
                    )
                    1 -> ColorSelectionPanel(
                        palette = CURATED_PAINT_PALETTE,
                        selectedColor = selectedStripeColor,
                        onColorSelected = {
                            selectedStripeColor = it
                            soundManager.playChime()
                        },
                        tagPrefix = "stripe_color"
                    )
                    2 -> LiverySelectionPanel(
                        liveries = train.availableLiveries,
                        activeLiveryId = selectedLiveryId,
                        onSelectLivery = {
                            selectedLiveryId = it.id
                            selectedBodyColor = Color(it.primaryColor)
                            selectedStripeColor = Color(it.accentColor)
                            soundManager.playChime()
                        }
                    )
                }
            }

            // SUCCESS NOTIFICATION TOAST
            AnimatedVisibility(visible = showSavedMessage) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TrainLightGreen)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PAINT SCHEME APPLIED TO FLEET!", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }

            // BOTTOM ACTIONS: RESET & APPLY
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Reset Factory Colors Button
                Button(
                    onClick = {
                        selectedBodyColor = Color(train.defaultBodyColor)
                        selectedStripeColor = Color(train.defaultStripeColor)
                        selectedLiveryId = train.availableLiveries.firstOrNull()?.id ?: "V1_VIRGIN"
                        gamePrefs.applyCustomPaint(train.id, train.defaultBodyColor, train.defaultStripeColor)
                        gamePrefs.setLiveryVariant(train.id, selectedLiveryId)
                        soundManager.playLeverClick()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .testTag("paint_shop_reset_button"),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("FACTORY COLORS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }

                // Apply and Save Scheme Button
                Button(
                    onClick = {
                        val bodyArgb = selectedBodyColor.toArgb().toLong() and 0xFFFFFFFFL
                        val stripeArgb = selectedStripeColor.toArgb().toLong() and 0xFFFFFFFFL
                        gamePrefs.applyCustomPaint(train.id, bodyArgb, stripeArgb)
                        if (selectedLiveryId.isNotBlank()) {
                            gamePrefs.setLiveryVariant(train.id, selectedLiveryId)
                        }
                        soundManager.playChime()
                        showSavedMessage = true
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(28.dp)
                        .testTag("paint_shop_apply_button"),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TrainYellowPrimary),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("APPLY PAINT SCHEME", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 9.5.sp)
                }
            }
        }
    }
}

@Composable
private fun ColorSelectionPanel(
    palette: List<PaintPaletteEntry>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    tagPrefix: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 1.dp)
    ) {
        Text(
            text = "SELECT COLOR FROM WORKSHOP PALETTE",
            color = Color(0xFF94A3B8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        // 4 items per row grid
        val chunked = palette.chunked(4)
        chunked.forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowItems.forEach { entry ->
                    val isSelected = entry.color == selectedColor
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(entry.color)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) TrainYellowPrimary else Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { onColorSelected(entry.color) }
                            .testTag("${tagPrefix}_${entry.name.replace(" ", "_").lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = TrainYellowPrimary,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.65f))
                                .padding(vertical = 0.5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = entry.name,
                                color = Color.White,
                                fontSize = 6.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
                // Fill remaining spaces in row if less than 4
                if (rowItems.size < 4) {
                    repeat(4 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun LiverySelectionPanel(
    liveries: List<LiveryVariant>,
    activeLiveryId: String,
    onSelectLivery: (LiveryVariant) -> Unit
) {
    if (liveries.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Standard factory paint scheme is active for this locomotive.",
                color = Color(0xFF94A3B8),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            contentPadding = PaddingValues(vertical = 1.dp)
        ) {
            items(liveries) { livery ->
                val isSelected = livery.id == activeLiveryId
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) TrainYellowPrimary else Color(0xFF334155),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectLivery(livery) }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("livery_card_${livery.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f).padding(end = 6.dp)
                        ) {
                            // Color swatches preview
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(3.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 10.dp, height = 20.dp)
                                        .background(Color(livery.primaryColor))
                                )
                                Box(
                                    modifier = Modifier
                                        .size(width = 10.dp, height = 20.dp)
                                        .background(Color(livery.accentColor))
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = livery.displayName,
                                    color = if (isSelected) TrainYellowPrimary else Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = livery.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(TrainLightGreen)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text("ACTIVE", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 7.5.sp)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF334155))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text("SELECT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 7.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
