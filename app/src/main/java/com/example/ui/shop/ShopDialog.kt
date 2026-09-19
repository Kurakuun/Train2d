package com.example.ui.shop

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TrainSoundManager
import com.example.data.storage.GamePreferences
import com.example.data.storage.GameState
import com.example.ui.theme.GameDiamond
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameSilver
import com.example.ui.theme.TrainBrightCyan
import com.example.ui.theme.TrainGreen
import com.example.ui.theme.TrainYellowPrimary

enum class ShopCategory {
    EXCHANGE_DIAMONDS,
    SUPPLIES
}

enum class CoinExchangeType {
    SILVER,
    GOLD
}

@Composable
fun ShopDialog(
    gameState: GameState,
    gamePrefs: GamePreferences,
    soundManager: TrainSoundManager,
    onDismiss: () -> Unit
) {
    var activeCategory by remember { mutableStateOf(ShopCategory.EXCHANGE_DIAMONDS) }
    var coinExchangeType by remember { mutableStateOf(CoinExchangeType.SILVER) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header & Player Balances
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = TrainYellowPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DEPOT SHOP & EXCHANGE",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Balance summary pill bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Diamond, contentDescription = null, tint = GameDiamond, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${gameState.diamonds}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Text("•", color = Color(0xFF475569), fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GameGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${gameState.goldCoins}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Text("•", color = Color(0xFF475569), fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GameSilver, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${gameState.silverCoins}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Category Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryTabButton(
                        title = "💎 BUY COINS (DIAMONDS)",
                        isSelected = activeCategory == ShopCategory.EXCHANGE_DIAMONDS,
                        modifier = Modifier.weight(1f),
                        onClick = { activeCategory = ShopCategory.EXCHANGE_DIAMONDS }
                    )
                    CategoryTabButton(
                        title = "📦 DEPOT SUPPLIES",
                        isSelected = activeCategory == ShopCategory.SUPPLIES,
                        modifier = Modifier.weight(1f),
                        onClick = { activeCategory = ShopCategory.SUPPLIES }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (activeCategory == ShopCategory.EXCHANGE_DIAMONDS) {
                    // Sub-toggle for Silver Coins vs Gold Coins
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (coinExchangeType == CoinExchangeType.SILVER) GameSilver.copy(alpha = 0.35f) else Color.Transparent)
                                .clickable { coinExchangeType = CoinExchangeType.SILVER }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🪙 SILVER COINS",
                                color = if (coinExchangeType == CoinExchangeType.SILVER) Color.White else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (coinExchangeType == CoinExchangeType.GOLD) GameGold.copy(alpha = 0.35f) else Color.Transparent)
                                .clickable { coinExchangeType = CoinExchangeType.GOLD }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🪙 GOLD COINS",
                                color = if (coinExchangeType == CoinExchangeType.GOLD) Color.White else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isSilver = (coinExchangeType == CoinExchangeType.SILVER)
                        val coinLabel = if (isSilver) "Silver Coins" else "Gold Coins"
                        val coinColor = if (isSilver) GameSilver else GameGold

                        // Package 1: 10 diamonds = 5000 coins
                        item {
                            DiamondExchangeRow(
                                diamondCost = 10,
                                coinReward = 5000,
                                coinLabel = coinLabel,
                                coinColor = coinColor,
                                packageName = "Handful Pack",
                                canAfford = gameState.diamonds >= 10,
                                onExchange = {
                                    if (gameState.diamonds >= 10) {
                                        val ok = if (isSilver) {
                                            gamePrefs.buyCoinsWithDiamonds(diamondCost = 10, silverAmount = 5000)
                                        } else {
                                            gamePrefs.buyCoinsWithDiamonds(diamondCost = 10, goldAmount = 5000)
                                        }
                                        if (ok) soundManager.playChime()
                                    }
                                }
                            )
                        }

                        // Package 2: 100 diamonds = 10,000 coins
                        item {
                            DiamondExchangeRow(
                                diamondCost = 100,
                                coinReward = 10000,
                                coinLabel = coinLabel,
                                coinColor = coinColor,
                                packageName = "Driver's Pouch",
                                canAfford = gameState.diamonds >= 100,
                                onExchange = {
                                    if (gameState.diamonds >= 100) {
                                        val ok = if (isSilver) {
                                            gamePrefs.buyCoinsWithDiamonds(diamondCost = 100, silverAmount = 10000)
                                        } else {
                                            gamePrefs.buyCoinsWithDiamonds(diamondCost = 100, goldAmount = 10000)
                                        }
                                        if (ok) soundManager.playChime()
                                    }
                                }
                            )
                        }

                        // Package 3: 500 diamonds = 500,000 coins
                        item {
                            DiamondExchangeRow(
                                diamondCost = 500,
                                coinReward = 500000,
                                coinLabel = coinLabel,
                                coinColor = coinColor,
                                packageName = "Depot Vault Crate",
                                canAfford = gameState.diamonds >= 500,
                                isBestValue = true,
                                onExchange = {
                                    if (gameState.diamonds >= 500) {
                                        val ok = if (isSilver) {
                                            gamePrefs.buyCoinsWithDiamonds(diamondCost = 500, silverAmount = 500000)
                                        } else {
                                            gamePrefs.buyCoinsWithDiamonds(diamondCost = 500, goldAmount = 500000)
                                        }
                                        if (ok) soundManager.playChime()
                                    }
                                }
                            )
                        }
                    }
                } else {
                    // Supplies Tab
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Item 1: Sand Reservoir Refill
                        item {
                            ShopItemRow(
                                icon = Icons.Default.ElectricBolt,
                                iconColor = TrainYellowPrimary,
                                title = "Sand Refill +10 Bags",
                                desc = "Crucial traction booster for steep mountain grades",
                                priceText = "50 🪙 Silver",
                                canAfford = gameState.silverCoins >= 50,
                                onBuy = {
                                    if (gameState.silverCoins >= 50) {
                                        gamePrefs.addRewards(silver = -50)
                                        gamePrefs.replenishSupplies(sand = 10, coolant = 0)
                                        soundManager.playChime()
                                    }
                                }
                            )
                        }

                        // Item 2: Radiator Coolant Drum
                        item {
                            ShopItemRow(
                                icon = Icons.Default.Opacity,
                                iconColor = TrainBrightCyan,
                                title = "Coolant Drum +10 Tanks",
                                desc = "Instantly cools overheated locomotive engines",
                                priceText = "80 🪙 Silver",
                                canAfford = gameState.silverCoins >= 80,
                                onBuy = {
                                    if (gameState.silverCoins >= 80) {
                                        gamePrefs.addRewards(silver = -80)
                                        gamePrefs.replenishSupplies(sand = 0, coolant = 10)
                                        soundManager.playChime()
                                    }
                                }
                            )
                        }

                        // Item 3: Engineering Blueprints Pack
                        item {
                            ShopItemRow(
                                icon = Icons.Default.ShoppingBag,
                                iconColor = Color(0xFF60A5FA),
                                title = "Blueprint Cache +15",
                                desc = "Used for advanced locomotive modifications",
                                priceText = "120 🪙 Gold",
                                canAfford = gameState.goldCoins >= 120,
                                onBuy = {
                                    if (gameState.goldCoins >= 120) {
                                        gamePrefs.addRewards(gold = -120)
                                        gamePrefs.replenishSupplies(sand = 0, coolant = 0)
                                        soundManager.playChime()
                                    }
                                }
                            )
                        }

                        // Item 4: Diamond Sack
                        item {
                            ShopItemRow(
                                icon = Icons.Default.Diamond,
                                iconColor = GameDiamond,
                                title = "Diamond Vault +250 💎",
                                desc = "Premium gems for custom paint designs & secret engines",
                                priceText = "FREE (Promo Bonus)",
                                canAfford = true,
                                onBuy = {
                                    gamePrefs.addRewards(diamonds = 250)
                                    soundManager.playChime()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF334155) else Color(0xFF1E293B).copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) TrainYellowPrimary else Color(0xFF94A3B8),
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
        )
    }
}

@Composable
fun DiamondExchangeRow(
    diamondCost: Int,
    coinReward: Int,
    coinLabel: String,
    coinColor: Color,
    packageName: String,
    canAfford: Boolean,
    isBestValue: Boolean = false,
    onExchange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isBestValue) Color(0xFF2E243A) else Color(0xFF334155))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = coinColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+%,d %s".format(coinReward, coinLabel),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    if (isBestValue) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFD97706))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text("BEST VALUE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 8.sp)
                        }
                    }
                }
                Text(packageName, color = Color(0xFF94A3B8), fontSize = 10.sp)
            }
        }

        Button(
            onClick = onExchange,
            enabled = canAfford,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (canAfford) TrainGreen else Color(0xFF475569)
            ),
            shape = RoundedCornerShape(6.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.testTag("buy_coins_${diamondCost}_diamonds")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$diamondCost", fontSize = 11.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.width(2.dp))
                Icon(Icons.Default.Diamond, contentDescription = null, tint = GameDiamond, modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
fun ShopItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    desc: String,
    priceText: String,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF334155))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(desc, color = Color(0xFF94A3B8), fontSize = 9.5.sp)
            }
        }

        Button(
            onClick = onBuy,
            enabled = canAfford,
            colors = ButtonDefaults.buttonColors(containerColor = TrainGreen),
            shape = RoundedCornerShape(6.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.testTag("shop_buy_button")
        ) {
            Text(priceText, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
        }
    }
}
