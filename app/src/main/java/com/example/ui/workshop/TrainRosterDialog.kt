package com.example.ui.workshop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.GameContent
import com.example.data.model.TrainCategory
import com.example.data.model.TrainModel
import com.example.data.storage.GamePreferences
import com.example.data.storage.GameState
import com.example.ui.components.drawLocomotiveDetailed
import com.example.ui.theme.*

/**
 * Full-featured Fleet Catalog & Train Roster Dialog.
 * Allows browsing all locomotives categorized with live visual photos/previews,
 * authentic technical specs, and instant train selection.
 */
@Composable
fun TrainRosterDialog(
    gameState: GameState,
    gamePrefs: GamePreferences,
    selectedTrainId: String,
    onSelectTrain: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<TrainCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredTrains = remember(selectedCategory, searchQuery) {
        GameContent.ALL_TRAINS.filter { train ->
            val matchesCategory = selectedCategory == null || train.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                train.name.contains(searchQuery, ignoreCase = true) ||
                train.type.name.contains(searchQuery, ignoreCase = true) ||
                train.id.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B111A).copy(alpha = 0.96f))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF131B26))
                    .border(1.5.dp, Color(0xFF2D3A4B), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                // HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TrainYellowPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsTransit, contentDescription = null, tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "GLOBAL FLEET ROSTER",
                                color = TrainYellowPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${GameContent.ALL_TRAINS.size} Authentically Modeled Trains • ${gameState.unlockedTrainIds.size} In Service",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // CATEGORY CHIPS SCROLL ROW
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "ALL" Chip
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = {
                            Text(
                                "🌐 ALL (${GameContent.ALL_TRAINS.size})",
                                fontWeight = if (selectedCategory == null) FontWeight.Black else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TrainYellowPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF1E293B),
                            labelColor = Color(0xFFE2E8F0)
                        )
                    )

                    // Each category chip
                    TrainCategory.values().forEach { category ->
                        val count = GameContent.ALL_TRAINS.count { it.category == category }
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    "${category.icon} ${category.displayName} ($count)",
                                    fontWeight = if (selectedCategory == category) FontWeight.Black else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TrainYellowPrimary,
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFFE2E8F0)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // TRAIN GRID WITH LIVE VISUAL PREVIEWS
                if (filteredTrains.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No trains found in this category.",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 270.dp),
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(filteredTrains, key = { it.id }) { train ->
                            val isSelected = train.id == selectedTrainId
                            val isUnlocked = gameState.unlockedTrainIds.contains(train.id)

                            TrainRosterCard(
                                train = train,
                                isSelected = isSelected,
                                isUnlocked = isUnlocked,
                                activeVariant = gameState.trainLiveryVariants[train.id] ?: "V1_VIRGIN",
                                customBodyColor = gameState.customBodyColors[train.id],
                                customStripeColor = gameState.customStripeColors[train.id],
                                onSelect = {
                                    onSelectTrain(train.id)
                                    onDismiss()
                                },
                                onUnlock = {
                                    gamePrefs.unlockTrain(train.id, train.priceGold, train.priceDiamonds)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Train Card with live photo/visual canvas preview
 */
@Composable
fun TrainRosterCard(
    train: TrainModel,
    isSelected: Boolean,
    isUnlocked: Boolean,
    activeVariant: String,
    customBodyColor: Long?,
    customStripeColor: Long?,
    onSelect: () -> Unit,
    onUnlock: () -> Unit
) {
    val borderColor = when {
        isSelected -> TrainYellowPrimary
        isUnlocked -> Color(0xFF334155)
        else -> Color(0xFF1E293B)
    }

    val cardBg = if (isSelected) Color(0xFF1A2634) else Color(0xFF182230)

    val activeLivery = train.availableLiveries.find { it.id == activeVariant }
        ?: train.availableLiveries.firstOrNull()
    val bodyColor = Color(customBodyColor ?: activeLivery?.primaryColor ?: train.defaultBodyColor)
    val stripeColor = Color(customStripeColor ?: activeLivery?.accentColor ?: train.defaultStripeColor)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable {
                if (isUnlocked) {
                    onSelect()
                }
            }
            .padding(10.dp)
            .testTag("train_card_${train.id}")
    ) {
        // TOP ROW: Category Chip & Flag
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${train.category.icon} ${train.category.displayName.uppercase()}",
                    color = Color(0xFF94A3B8),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = train.countryFlag, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = train.type.name,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // TRAIN PHOTO / LIVE CANVAS PREVIEW
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLocomotiveDetailed(
                    train = train,
                    bodyColor = bodyColor,
                    stripeColor = stripeColor,
                    wheelAngleRad = 0f,
                    widthPx = size.width,
                    heightPx = size.height,
                    variant = activeVariant
                )
            }

            // Lock Overlay if not yet unlocked
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = TrainYellowPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // TRAIN NAME
        Text(
            text = train.name,
            color = if (isSelected) TrainYellowPrimary else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // STATS ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatBadge(label = "SPEED", value = "${train.baseSpeed.toInt()} km/h")
            StatBadge(label = "POWER", value = "${train.basePower.toInt()} HP")
            StatBadge(label = "RELIABILITY", value = "${train.baseReliability.toInt()}%")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ACTION BUTTON / STATUS
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(TrainYellowPrimary)
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ACTIVE TRAIN",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                )
            }
        } else if (isUnlocked) {
            Button(
                onClick = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "SELECT TRAIN",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        } else {
            Button(
                onClick = onUnlock,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TrainGreen),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "🪙 ${train.priceGold}  💎 ${train.priceDiamonds}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color(0xFF64748B), fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = Color(0xFFCBD5E1), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
