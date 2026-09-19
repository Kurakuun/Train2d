package com.example.ui.codes

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TrainSoundManager
import com.example.data.model.GameContent
import com.example.data.model.PromoCode
import com.example.data.model.TrainModel
import com.example.data.storage.GamePreferences
import com.example.data.storage.GameState
import com.example.ui.components.drawLocomotiveDetailed
import com.example.ui.theme.GameBlueprint
import com.example.ui.theme.GameDiamond
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameSilver
import com.example.ui.theme.TrainBrightCyan
import com.example.ui.theme.TrainGreen
import com.example.ui.theme.TrainSafetyRed
import com.example.ui.theme.TrainYellowPrimary

@Composable
fun PromoCodesDialog(
    gameState: GameState,
    gamePrefs: GamePreferences,
    soundManager: TrainSoundManager,
    onDismiss: () -> Unit
) {
    var codeInput by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var celebrationTrain by remember { mutableStateOf<TrainModel?>(null) }
    var previewTrain by remember { mutableStateOf<TrainModel?>(null) }

    fun processRedeem(codeToRedeem: String) {
        val clean = codeToRedeem.trim().replace(" ", "")
        val matchedPromo = GameContent.PROMO_CODES.find {
            it.code.trim().replace(" ", "").equals(clean, ignoreCase = true)
        }

        val err = gamePrefs.redeemPromoCode(codeToRedeem)
        if (err != null) {
            feedbackMessage = err
            isError = true
        } else {
            feedbackMessage = "Code successfully redeemed! Rewards added!"
            isError = false
            soundManager.playChime()
            codeInput = ""

            // Pop up celebratory train showcase if a secret train was unlocked!
            val unlockedId = matchedPromo?.secretTrainId ?: matchedPromo?.secretTrainIds?.firstOrNull()
            if (unlockedId != null) {
                val train = GameContent.ALL_TRAINS.find { it.id == unlockedId }
                if (train != null) {
                    celebrationTrain = train
                    soundManager.blastHorn(train.id, train.type, train.soundProfile)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.92f)
                .clickable(enabled = false) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Redeem, contentDescription = null, tint = TrainYellowPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "24 EXCLUSIVE PROMO CODES",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Text(
                    text = "Enter codes to unlock secret locomotives, bullet trains, coins, diamonds, and blueprints!",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = { codeInput = it },
                        placeholder = { Text("ENTER CODE (e.g. Crazy fast, BLOXWORKS)", color = Color(0xFF64748B), fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("promo_code_input"),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedIndicatorColor = TrainYellowPrimary,
                            unfocusedIndicatorColor = Color(0xFF475569)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { processRedeem(codeInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = TrainGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("redeem_code_button")
                    ) {
                        Text("REDEEM", fontWeight = FontWeight.Bold)
                    }
                }

                // Feedback Message
                feedbackMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = msg,
                        color = if (isError) TrainSafetyRed else TrainGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "AVAILABLE OFFICIAL CODES (${GameContent.PROMO_CODES.size} TOTAL):",
                    color = TrainBrightCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable List of 24 Promo Codes
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(GameContent.PROMO_CODES) { promo ->
                        val isRedeemed = gameState.redeemedCodes.contains(promo.code)
                        val associatedTrainId = promo.secretTrainId ?: promo.secretTrainIds.firstOrNull()
                        val associatedTrain = associatedTrainId?.let { tid -> GameContent.ALL_TRAINS.find { it.id == tid } }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isRedeemed) Color(0xFF1E293B) else Color(0xFF334155))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = promo.code,
                                        color = if (isRedeemed) Color(0xFF94A3B8) else TrainYellowPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                    if (associatedTrain != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (promo.code.contains("fast", ignoreCase = true)) Color(0xFFE6007E) else TrainBrightCyan)
                                                .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                        ) {
                                            Text(
                                                text = if (promo.code.contains("fast", ignoreCase = true)) "⚡ SECRET BULLET TRAIN" else "SECRET TRAIN",
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 8.5.sp
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = promo.rewardDescription,
                                    color = if (isRedeemed) Color(0xFF64748B) else Color.White,
                                    fontSize = 11.sp
                                )

                                // Train Preview mini chip if secret train exists
                                if (associatedTrain != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF0F172A).copy(alpha = 0.7f))
                                            .clickable {
                                                previewTrain = associatedTrain
                                                soundManager.playChime()
                                            }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Visibility, contentDescription = "Preview", tint = TrainBrightCyan, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "PREVIEW: ${associatedTrain.name} ${associatedTrain.countryFlag}",
                                            color = TrainBrightCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            if (isRedeemed) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TrainGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("CLAIMED", color = TrainGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        codeInput = promo.code
                                        processRedeem(promo.code)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TrainGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("CLAIM", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // CELEBRATION SHOWCASE DIALOG WHEN CODE UNLOCKS A SECRET TRAIN!
        // =========================================================================
        celebrationTrain?.let { train ->
            val activeVariantKey = gameState.trainLiveryVariants[train.id] ?: train.availableLiveries.firstOrNull()?.id ?: "V1_VIRGIN"
            val activeLivery = train.availableLiveries.find { it.id == activeVariantKey } ?: train.availableLiveries.firstOrNull()
            val bodyColor = Color(gameState.customBodyColors[train.id] ?: activeLivery?.primaryColor ?: train.defaultBodyColor)
            val stripeColor = Color(gameState.customStripeColors[train.id] ?: activeLivery?.accentColor ?: train.defaultStripeColor)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.88f))
                    .clickable { celebrationTrain = null },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .clickable(enabled = false) {}
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title Banner
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎉", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SECRET TRAIN UNLOCKED!",
                                color = TrainYellowPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("⚡", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${train.name} ${train.countryFlag}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large Hero Canvas of Unlocked Train
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(width = 340.dp, height = 120.dp)) {
                                drawLocomotiveDetailed(
                                    train = train,
                                    bodyColor = bodyColor,
                                    stripeColor = stripeColor,
                                    wheelAngleRad = 0f,
                                    widthPx = size.width,
                                    heightPx = size.height,
                                    variant = activeVariantKey
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Key Specs Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TOP SPEED", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("${train.baseSpeed.toInt() * 2}+ KM/H", color = TrainBrightCyan, fontSize = 13.sp, fontWeight = FontWeight.Black)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MAX UPGRADE", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("LVL ${train.maxUpgradeLevel} (MAX)", color = TrainYellowPrimary, fontSize = 13.sp, fontWeight = FontWeight.Black)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("SOUND PROFILE", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(train.soundProfile.name.replace("_", " "), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = train.description,
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    soundManager.blastHorn(train.id, train.type, train.soundProfile)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Horn", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("HORN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    gamePrefs.selectTrain(train.id)
                                    celebrationTrain = null
                                    soundManager.playChime()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TrainGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.5f)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Equip", tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("EQUIP & DRIVE", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TRAIN PREVIEW MODAL
        // =========================================================================
        previewTrain?.let { train ->
            val activeVariantKey = gameState.trainLiveryVariants[train.id] ?: train.availableLiveries.firstOrNull()?.id ?: "V1_VIRGIN"
            val activeLivery = train.availableLiveries.find { it.id == activeVariantKey } ?: train.availableLiveries.firstOrNull()
            val bodyColor = Color(gameState.customBodyColors[train.id] ?: activeLivery?.primaryColor ?: train.defaultBodyColor)
            val stripeColor = Color(gameState.customStripeColors[train.id] ?: activeLivery?.accentColor ?: train.defaultStripeColor)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { previewTrain = null },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clickable(enabled = false) {}
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${train.name} ${train.countryFlag}",
                                color = TrainBrightCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                            IconButton(onClick = { previewTrain = null }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(width = 300.dp, height = 100.dp)) {
                                drawLocomotiveDetailed(
                                    train = train,
                                    bodyColor = bodyColor,
                                    stripeColor = stripeColor,
                                    wheelAngleRad = 0f,
                                    widthPx = size.width,
                                    heightPx = size.height,
                                    variant = activeVariantKey
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = train.description,
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { previewTrain = null },
                            colors = ButtonDefaults.buttonColors(containerColor = TrainBrightCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CLOSE PREVIEW", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
