package com.example.ui.inventory

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TrainSoundManager
import com.example.data.storage.GamePreferences
import com.example.data.storage.GameState
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
import com.example.ui.theme.TrainYellowPrimary

@Composable
fun InventoryDialog(
    gameState: GameState,
    gamePrefs: GamePreferences,
    soundManager: TrainSoundManager,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable { onDismiss() }
            .testTag("inventory_dialog_backdrop"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.92f)
                .clickable(enabled = false) {}
                .border(width = 1.5.dp, color = Color(0xFF334155), shape = RoundedCornerShape(18.dp))
                .testTag("inventory_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
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
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TrainBrightCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "Inventory",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "DRIVER INVENTORY & DEPOT SUPPLIES",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Manage supplies, sandbags, coolant, blueprints & currencies",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("inventory_close_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Inventory Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. COOLANT ITEM CARD
                    item {
                        InventoryItemCard(
                            iconText = "💧",
                            title = "Coolant Canister Supply",
                            subtitle = "Radiator & Engine Overheat Extinguisher",
                            description = "Injected into cylinder jacket and radiator cooling loop. Cools down hot engines during high-throttle climbing and puts out diesel engine bay fires.",
                            amountText = "${gameState.coolantUnits} / ${gameState.maxCoolantCapacity} Units",
                            fillFraction = (gameState.coolantUnits.toFloat() / gameState.maxCoolantCapacity).coerceIn(0f, 1f),
                            fillColor = Color(0xFF0284C7),
                            badgeColor = Color(0xFF0284C7),
                            badgeText = if (gameState.coolantUnits >= gameState.maxCoolantCapacity) "FULL TANK" else if (gameState.coolantUnits < 20) "LOW SUPPLY" else "READY",
                            onRefill25 = {
                                gamePrefs.refillCoolant(amount = 25)
                                soundManager.playChime()
                            },
                            onRefillMax = {
                                gamePrefs.refillCoolant(amount = gameState.maxCoolantCapacity)
                                soundManager.playChime()
                            },
                            testTagPrefix = "coolant"
                        )
                    }

                    // 2. SANDBAG ITEM CARD
                    item {
                        InventoryItemCard(
                            iconText = "⏳",
                            title = "Track Sandbag Reserves",
                            subtitle = "High-Adhesion Silica Rail Sand",
                            description = "Pressurized sand blast directly onto rails in front of driving wheels. Eliminates wheel slip instantly in rain, blizzard, ice, and heavy haul uphill traction.",
                            amountText = "${gameState.sandUnits} / ${gameState.maxSandCapacity} Units",
                            fillFraction = (gameState.sandUnits.toFloat() / gameState.maxSandCapacity).coerceIn(0f, 1f),
                            fillColor = Color(0xFFD97706),
                            badgeColor = Color(0xFFD97706),
                            badgeText = if (gameState.sandUnits >= gameState.maxSandCapacity) "FULL CAPACITY" else if (gameState.sandUnits < 20) "DEPLETED" else "READY",
                            onRefill25 = {
                                gamePrefs.refillSand(amount = 25)
                                soundManager.playChime()
                            },
                            onRefillMax = {
                                gamePrefs.refillSand(amount = gameState.maxSandCapacity)
                                soundManager.playChime()
                            },
                            testTagPrefix = "sandbag"
                        )
                    }

                    // 3. BLUEPRINT ITEM CARD
                    item {
                        InventoryResourceCard(
                            iconText = "📐",
                            title = "Technical Locomotive Blueprints",
                            subtitle = "Engineering Schematics for Upgrades",
                            description = "Specialized blueprints recovered from completed contract shipments and bonus promo codes. Required to craft and upgrade train modules.",
                            amountText = "${gameState.blueprints} Blueprints",
                            themeColor = GameBlueprint,
                            testTag = "blueprint_item_card"
                        )
                    }

                    // 4. SILVER COINS CARD
                    item {
                        InventoryResourceCard(
                            iconText = "🪙",
                            title = "Silver Coins (Railway Currency)",
                            subtitle = "Primary Operational Funds",
                            description = "Earned on every delivery route and timetable job. Used to purchase supply refills, repair component wear, overhaul engines, and buy standard trains.",
                            amountText = "${gameState.silverCoins} Silver Coins",
                            themeColor = GameSilver,
                            testTag = "silver_coins_card"
                        )
                    }

                    // 5. GOLD COINS & DIAMONDS CARD
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(width = 1.dp, color = Color(0xFF334155), shape = RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🪙", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Gold Coins", color = GameGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${gameState.goldCoins}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Text("Premium train unlocks", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                }
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(width = 1.dp, color = Color(0xFF334155), shape = RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("💎", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Diamonds", color = GameDiamond, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${gameState.diamonds}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Text("Livery variants & fast pass", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Full Refill Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DEPOT MASTER SERVICE",
                            color = TrainYellowPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Fill all Sandbags and Coolant tanks to 100% capacity",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp
                        )
                    }

                    Button(
                        onClick = {
                            gamePrefs.refillAllSupplies()
                            soundManager.playChime()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TrainGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("refill_all_supplies_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("REFILL ALL", fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryItemCard(
    iconText: String,
    title: String,
    subtitle: String,
    description: String,
    amountText: String,
    fillFraction: Float,
    fillColor: Color,
    badgeColor: Color,
    badgeText: String,
    onRefill25: () -> Unit,
    onRefillMax: () -> Unit,
    testTagPrefix: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color(0xFF334155), shape = RoundedCornerShape(12.dp))
            .testTag("${testTagPrefix}_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(iconText, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Text(text = subtitle, color = TrainBrightCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.25f))
                        .border(1.dp, badgeColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(badgeText, color = badgeColor, fontWeight = FontWeight.Black, fontSize = 9.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                color = Color(0xFF94A3B8),
                fontSize = 9.5.sp,
                lineHeight = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Gauge Fill Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Stock Gauge", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(amountText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { fillFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = fillColor,
                    trackColor = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Refill buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onRefill25,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .padding(end = 6.dp)
                        .testTag("${testTagPrefix}_refill_25_button")
                ) {
                    Text("+25 UNITS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }

                Button(
                    onClick = onRefillMax,
                    colors = ButtonDefaults.buttonColors(containerColor = fillColor),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("${testTagPrefix}_refill_max_button")
                ) {
                    Text("FILL MAX (100)", color = Color.White, fontWeight = FontWeight.Black, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
private fun InventoryResourceCard(
    iconText: String,
    title: String,
    subtitle: String,
    description: String,
    amountText: String,
    themeColor: Color,
    testTag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color(0xFF334155), shape = RoundedCornerShape(12.dp))
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(iconText, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Text(text = subtitle, color = themeColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(themeColor.copy(alpha = 0.20f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(amountText, color = themeColor, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                color = Color(0xFF94A3B8),
                fontSize = 9.5.sp,
                lineHeight = 12.sp
            )
        }
    }
}
