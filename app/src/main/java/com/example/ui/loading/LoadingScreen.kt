package com.example.ui.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameContent
import com.example.data.storage.GameState
import com.example.ui.components.drawLocomotiveDetailed
import com.example.ui.theme.TrainBrightCyan
import com.example.ui.theme.TrainYellowPrimary
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    gameState: GameState,
    onLoadingComplete: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var tipText by remember { mutableStateOf("Preparing Locomotive Boiler & Prime Mover...") }

    val infiniteTransition = rememberInfiniteTransition(label = "loading_anim")
    val wheelAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wheel_spin"
    )

    LaunchedEffect(Unit) {
        val tips = listOf(
            "Connecting pneumatic air brake lines...",
            "Stoking firebox and checking boiler pressure...",
            "Tip: Engage Sand Lever on steep hills to stop wheel slipping!",
            "Tip: Use the Water Coolant lever if engine temp exceeds 95°C!",
            "Redeem secret codes in the menu for 4 hidden locomotives!",
            "Clear for departure on Canyon Line Track 1!"
        )

        for (i in 1..100) {
            delay(18)
            progress = i / 100f
            if (i == 20) tipText = tips[1]
            if (i == 45) tipText = tips[2]
            if (i == 70) tipText = tips[3]
            if (i == 90) tipText = tips[4]
            if (i == 98) tipText = tips[5]
        }
        delay(200)
        onLoadingComplete()
    }

    val selectedTrain = GameContent.ALL_TRAINS.find { it.id == gameState.selectedTrainId }
        ?: GameContent.ALL_TRAINS.first()
    val bodyColor = Color(gameState.customBodyColors[selectedTrain.id] ?: selectedTrain.defaultBodyColor)
    val stripeColor = Color(gameState.customStripeColors[selectedTrain.id] ?: selectedTrain.defaultStripeColor)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D141C), Color(0xFF1B232D), Color(0xFF0F172A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Moving Train Profile
            Box(
                modifier = Modifier
                    .size(width = 280.dp, height = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLocomotiveDetailed(
                        train = selectedTrain,
                        bodyColor = bodyColor,
                        stripeColor = stripeColor,
                        wheelAngleRad = wheelAngle,
                        widthPx = size.width,
                        heightPx = size.height
                    )

                    // Track Rails
                    drawLine(
                        color = Color(0xFF4B5563),
                        start = Offset(0f, size.height - 10f),
                        end = Offset(size.width, size.height - 10f),
                        strokeWidth = 6f
                    )
                    drawLine(
                        color = Color(0xFFE5E7EB),
                        start = Offset(0f, size.height - 12f),
                        end = Offset(size.width, size.height - 12f),
                        strokeWidth = 2.5f
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Loading Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color(0xFF1E293B))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(TrainYellowPrimary, TrainBrightCyan)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "LOADING...",
                    color = TrainYellowPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tipText,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
