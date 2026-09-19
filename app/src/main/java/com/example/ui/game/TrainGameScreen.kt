package com.example.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.TrainSoundManager
import com.example.data.model.ContractJob
import com.example.data.model.EnvironmentType
import com.example.data.model.GameContent
import com.example.data.model.TrainDriveMode
import com.example.data.model.TrainType
import com.example.data.storage.GamePreferences
import com.example.data.storage.GameState
import com.example.physics.GearPosition
import com.example.physics.Particle
import com.example.physics.ParticleType
import com.example.physics.TrainPhysicsEngine
import com.example.physics.TrainPhysicsState
import com.example.physics.WeatherType
import com.example.ui.components.DashboardConsole
import com.example.ui.components.drawEnvironmentBackground
import com.example.ui.components.drawGameParticles
import com.example.ui.components.drawLocomotiveDetailed
import com.example.ui.components.drawRailCarDetailed
import com.example.ui.components.drawTrackSegment
import com.example.ui.components.drawWeatherAtmosphere
import com.example.ui.components.drawWeatherPrecipitation
import com.example.ui.components.getEnvironmentColors
import com.example.ui.components.getEnvironmentTerrainTheme
import com.example.ui.theme.GameBlueprint
import com.example.ui.theme.GameDiamond
import com.example.ui.theme.GameGold
import com.example.ui.theme.GameSilver
import com.example.ui.theme.TrainBrightCyan
import com.example.ui.theme.TrainGreen
import com.example.ui.theme.TrainLightGreen
import com.example.ui.theme.TrainSafetyRed
import com.example.ui.theme.TrainYellowPrimary
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay

enum class DeliveryStopResult {
    NONE,
    PERFECT_GREEN_ZONE,
    BUFFER_STOP_COLLISION
}

@Composable
fun TrainGameScreen(
    gameState: GameState,
    gamePrefs: GamePreferences,
    soundManager: TrainSoundManager,
    selectedContract: ContractJob? = null,
    onBackToWorkshop: () -> Unit,
    onBackToMenu: () -> Unit,
    onOpenMaintenance: () -> Unit = {}
) {
    val activeContract = selectedContract ?: GameContent.ALL_CONTRACTS.first()
    val train = GameContent.ALL_TRAINS.find { it.id == gameState.selectedTrainId }
        ?: GameContent.ALL_TRAINS.first()
    val consistCars = remember(activeContract, train) {
        GameContent.resolveConsistForTrain(activeContract, train)
    }

    val bodyColor = Color(gameState.customBodyColors[train.id] ?: train.defaultBodyColor)
    val stripeColor = Color(gameState.customStripeColors[train.id] ?: train.defaultStripeColor)

    val physicsEngine = remember { TrainPhysicsEngine() }
    LaunchedEffect(activeContract.distanceMeters) {
        physicsEngine.destinationVisualX = activeContract.distanceMeters * TrainPhysicsEngine.VISUAL_WORLD_SCALE
    }

    var physicsState by remember { mutableStateOf(TrainPhysicsState()) }
    val particles = remember { mutableStateListOf<Particle>() }

    var isPaused by remember { mutableStateOf(false) }
    var isJobCompleted by remember { mutableStateOf(false) }
    var wheelRotationAngle by remember { mutableFloatStateOf(0f) }

    // Wear & tear accumulators for this contract trip
    var tripEngineWear by remember { mutableFloatStateOf(0f) }
    var tripGearboxWear by remember { mutableFloatStateOf(0f) }
    var tripWheelsWear by remember { mutableFloatStateOf(0f) }
    var tripBrakesWear by remember { mutableFloatStateOf(0f) }

    // Boost timer
    var boostActiveUntil by remember { mutableStateOf(0L) }

    // Dynamic Weather system
    val defaultWeatherForEnv = remember(activeContract.environment) {
        when (activeContract.environment) {
            EnvironmentType.ARCTIC_PASS -> WeatherType.SNOW
            EnvironmentType.REDWOOD_COAST -> WeatherType.RAIN
            EnvironmentType.ALPINE_PEAKS -> WeatherType.SNOW
            EnvironmentType.INDUSTRIAL_VALLEY -> WeatherType.FOG
            EnvironmentType.DESERT_CANYON -> WeatherType.CLEAR
        }
    }
    var currentWeather by remember { mutableStateOf(defaultWeatherForEnv) }
    var isHeadlightOn by remember { mutableStateOf(defaultWeatherForEnv == WeatherType.FOG) }
    var deliveryStopResult by remember { mutableStateOf(DeliveryStopResult.NONE) }
    var hasImpactedBuffer by remember { mutableStateOf(false) }
    var cameraShakeAmount by remember { mutableFloatStateOf(0f) }
    var reverserLockoutWarning by remember { mutableStateOf<String?>(null) }
    var lightningAlpha by remember { mutableFloatStateOf(0f) }
    var gameElapsedTime by remember { mutableFloatStateOf(0f) }

    // Automatic Headlights in Fog: in fog weather, turn train lights on automatically
    LaunchedEffect(currentWeather) {
        if (currentWeather == WeatherType.FOG) {
            isHeadlightOn = true
        }
    }

    // Dismiss reverser lockout warning after brief delay
    LaunchedEffect(reverserLockoutWarning) {
        if (reverserLockoutWarning != null) {
            delay(2800)
            reverserLockoutWarning = null
        }
    }

    // Upgraded stats
    val gbLvl = gameState.gearboxLevels[train.id] ?: 1
    val genLvl = gameState.generatorLevels[train.id] ?: 1
    val engLvl = gameState.engineLevels[train.id] ?: 1
    val whLvl = gameState.wheelsLevels[train.id] ?: 1

    val speedStat = train.baseSpeed + (gbLvl * 1.2f)
    val reliabilityStat = train.baseReliability + (genLvl * 0.8f)
    val powerStat = train.basePower + (engLvl * 1.5f) + (if (System.currentTimeMillis() < boostActiveUntil) 25f else 0f)
    val adherenceStat = train.baseAdherence + (whLvl * 0.5f)

    // Supply consumption accumulators
    var coolantTimer by remember { mutableFloatStateOf(0f) }
    var sandTimer by remember { mutableFloatStateOf(0f) }

    // Music and Ambience Synchronization
    LaunchedEffect(gameState.selectedMusicTrackId, gameState.isMusicPlaying) {
        soundManager.setMusicTrack(gameState.selectedMusicTrackId, gameState.isMusicPlaying)
    }
    LaunchedEffect(activeContract.environment, currentWeather, isPaused, isJobCompleted) {
        soundManager.setDrivingAmbience(activeContract.environment, isDriving = !isPaused && !isJobCompleted)
        soundManager.setWeather(currentWeather)
    }

    val handleBackToWorkshop = {
        gamePrefs.applyTripWear(train.id, tripEngineWear, tripGearboxWear, tripWheelsWear, tripBrakesWear)
        soundManager.updateEngineState(train.soundProfile, speedKmh = 0f, throttle = 0f, reverserForward = true, isBraking = false)
        soundManager.setDrivingAmbience(null, isDriving = false)
        onBackToWorkshop()
    }
    val handleBackToMenu = {
        gamePrefs.applyTripWear(train.id, tripEngineWear, tripGearboxWear, tripWheelsWear, tripBrakesWear)
        soundManager.updateEngineState(train.soundProfile, speedKmh = 0f, throttle = 0f, reverserForward = true, isBraking = false)
        soundManager.setDrivingAmbience(null, isDriving = false)
        onBackToMenu()
    }
    val handleOpenMaintenance = {
        gamePrefs.applyTripWear(train.id, tripEngineWear, tripGearboxWear, tripWheelsWear, tripBrakesWear)
        soundManager.updateEngineState(train.soundProfile, speedKmh = 0f, throttle = 0f, reverserForward = true, isBraking = false)
        soundManager.setDrivingAmbience(null, isDriving = false)
        onOpenMaintenance()
    }

    // Game Physics Simulation Loop (60 FPS)
    LaunchedEffect(isPaused, isJobCompleted, currentWeather) {
        var lastTime = System.nanoTime()
        while (!isPaused && !isJobCompleted) {
            withFrameNanos { now ->
                // Smooth frame-time delta with 33ms ceiling (guarantees silky smooth 60-120fps motion)
                val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.005f, 0.033f)
                lastTime = now
                gameElapsedTime += dt

                // Weather lightning effect for thunderstorms
                if (currentWeather == WeatherType.THUNDERSTORM) {
                    if (Math.random() < 0.006) {
                        lightningAlpha = 0.85f
                        soundManager.playThunder()
                    }
                }
                if (lightningAlpha > 0f) {
                    lightningAlpha = (lightningAlpha - dt * 3.5f).coerceAtLeast(0f)
                }

                // Supply consumption when cooling/sand are active (scales with slider percentage)
                if (physicsState.isCoolingActive || physicsState.waterFlowPercent > 0) {
                    val flowMult = (physicsState.waterFlowPercent.coerceAtLeast(10) / 100f)
                    coolantTimer += dt * (0.5f + flowMult * 1.5f)
                    if (coolantTimer >= 2.0f) {
                        coolantTimer = 0f
                        if (!gamePrefs.useCoolant()) {
                            physicsState = physicsState.copy(isCoolingActive = false, waterFlowPercent = 0)
                        }
                    }
                }
                if (physicsState.isSandingActive || physicsState.sandFlowPercent > 0) {
                    val flowMult = (physicsState.sandFlowPercent.coerceAtLeast(10) / 100f)
                    sandTimer += dt * (0.5f + flowMult * 1.5f)
                    if (sandTimer >= 2.0f) {
                        sandTimer = 0f
                        if (!gamePrefs.useSand()) {
                            physicsState = physicsState.copy(isSandingActive = false, sandFlowPercent = 0)
                        }
                    }
                }

                // Step Physics with Dynamic Weather & Engine Health
                val isElectricTrain = (train.type == TrainType.ELECTRIC_MAGLEV)
                val isSteamTrain = (train.type == TrainType.STEAM)
                val currentHealth = gameState.locomotiveHealthMap[train.id]
                val totalCargoTonnes = consistCars.sumOf { it.weightKg.toDouble() }.toFloat() / 1000f
                val (newState, newParticles) = physicsEngine.stepPhysics(
                    currentState = physicsState,
                    powerStat = powerStat,
                    speedStat = speedStat,
                    reliabilityStat = reliabilityStat,
                    adherenceStat = adherenceStat,
                    dt = dt,
                    isSteam = isSteamTrain,
                    isElectric = isElectricTrain,
                    numRailCars = consistCars.size,
                    cargoTonnes = totalCargoTonnes,
                    weather = currentWeather,
                    health = currentHealth
                )
                physicsState = newState

                val speedRatio = (newState.speedKmh / 55f).coerceAtLeast(0f)

                // Accumulate component wear from high-speed & high-load driving
                if (kotlin.math.abs(newState.speedKmh) > 5f) {
                    // High speed and high throttle engine wear
                    if (newState.speedKmh > 65f || newState.throttle > 0.7f) {
                        val tempFactor = if (newState.engineTempC > 85f) 2.2f else 1.0f
                        tripEngineWear += (speedRatio * 1.5f + newState.throttle * 1.0f) * tempFactor * dt * 0.045f
                    }
                    // Gearbox torque wear
                    if (newState.throttle > 0.5f || newState.speedKmh > 60f) {
                        tripGearboxWear += (newState.throttle * 1.2f + speedRatio * 0.8f) * dt * 0.04f
                    }
                    // Wheel flange wear from high speed mileage + wheel slips
                    val slipWear = if (newState.isWheelsSlipping) 4.0f else 0.0f
                    tripWheelsWear += (speedRatio * 1.2f + slipWear) * dt * 0.045f
                }
                // Brake wear during active braking
                if (newState.brakeLevel > 0f) {
                    val brkIntensity = if (newState.brakeLevel > 0.85f) 2.5f else 1.0f
                    tripBrakesWear += (newState.brakeLevel * (newState.speedKmh / 40f).coerceAtLeast(0.5f) * brkIntensity) * dt * 0.08f
                }

                // Update real-time dynamic engine audio
                soundManager.updateEngineState(
                    soundProfile = train.soundProfile,
                    speedKmh = newState.speedKmh,
                    throttle = newState.throttle,
                    reverserForward = newState.reverserForward,
                    isBraking = newState.brakeLevel > 0.05f
                )

                // Ultra-smooth frame-rate independent wheel rotation locked directly to physical ground translation
                val isWheelStationary = kotlin.math.abs(newState.speedKmh) < 0.15f || (newState.brakeLevel > 0.04f && kotlin.math.abs(newState.speedKmh) < 0.8f)
                if (!isWheelStationary && kotlin.math.abs(newState.speedKmh) > 0.08f) {
                    wheelRotationAngle += (newState.speedKmh * (1000f / 3600f) * dt * 0.38f)
                }

                // Smoothly decay camera impact shake
                if (cameraShakeAmount > 0f) {
                    cameraShakeAmount = (cameraShakeAmount - dt * 24f).coerceAtLeast(0f)
                }

                // Update particles with strict capacity cap for rock-solid 60+ FPS
                particles.addAll(newParticles)
                val iterator = particles.iterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    p.currentLife += dt
                    p.x += p.vx * (dt * 60f)
                    p.y += p.vy * (dt * 60f)
                    if (p.currentLife >= p.maxLife) {
                        iterator.remove()
                    }
                }
                if (particles.size > 90) {
                    val excess = particles.size - 90
                    for (k in 0 until excess) {
                        if (particles.isNotEmpty()) particles.removeAt(0)
                    }
                }

                // Check contract delivery point stopping and buffer stop collision (Expanded Green Area)
                val greenStartMeters = activeContract.distanceMeters - 32f
                val greenEndMeters = activeContract.distanceMeters + 64f
                val bufferWorldX = (activeContract.distanceMeters * TrainPhysicsEngine.VISUAL_WORLD_SCALE) + 440f
                val locoFrontWorldX = (newState.distanceX * TrainPhysicsEngine.VISUAL_WORLD_SCALE) + 175f

                if (!isJobCompleted) {
                    if (locoFrontWorldX >= bufferWorldX) {
                        // Train failed to stop in the green area and collided with the buffer stop!
                        hasImpactedBuffer = true
                        deliveryStopResult = DeliveryStopResult.BUFFER_STOP_COLLISION
                        cameraShakeAmount = 18f
                        isJobCompleted = true

                        // Arrest train motion at the buffer post
                        val clampedDistance = (bufferWorldX - 175f) / TrainPhysicsEngine.VISUAL_WORLD_SCALE
                        physicsState = physicsState.copy(
                            distanceX = clampedDistance,
                            speedKmh = 0f,
                            throttle = 0f,
                            brakeLevel = 1.0f,
                            driveMode = TrainDriveMode.EMERGENCY_BRAKE
                        )
                        soundManager.playBufferStopCrashSound()

                        // Spawn violent collision sparks at buffer impact point
                        for (i in 0 until 28) {
                            particles.add(
                                Particle(
                                    x = 356f,
                                    y = 100f + (Math.random().toFloat() * 24f - 12f),
                                    vx = (Math.random().toFloat() * -14f - 4f),
                                    vy = (Math.random().toFloat() * 16f - 8f),
                                    size = 4f + Math.random().toFloat() * 4f,
                                    alpha = 1f,
                                    maxLife = 0.45f + Math.random().toFloat() * 0.4f,
                                    type = ParticleType.WHEEL_SPARK
                                )
                            )
                        }

                        gamePrefs.applyTripWear(train.id, tripEngineWear + 8f, tripGearboxWear + 8f, tripWheelsWear + 12f, tripBrakesWear + 12f)
                        soundManager.updateEngineState(
                            soundProfile = train.soundProfile,
                            speedKmh = 0f,
                            throttle = 0f,
                            reverserForward = true,
                            isBraking = true
                        )
                        gamePrefs.addRewards(
                            silver = activeContract.rewardSilver,
                            gold = activeContract.rewardGold,
                            diamonds = activeContract.rewardDiamonds,
                            xp = activeContract.xpReward,
                            contractId = activeContract.id
                        )
                    } else if (newState.distanceX in greenStartMeters..greenEndMeters && abs(newState.speedKmh) < 0.8f) {
                        // Train safely halted inside the designated Green Stopping Area!
                        deliveryStopResult = DeliveryStopResult.PERFECT_GREEN_ZONE
                        isJobCompleted = true
                        physicsState = physicsState.copy(throttle = 0f, speedKmh = 0f)
                        gamePrefs.applyTripWear(train.id, tripEngineWear, tripGearboxWear, tripWheelsWear, tripBrakesWear)
                        soundManager.updateEngineState(
                            soundProfile = train.soundProfile,
                            speedKmh = 0f,
                            throttle = 0f,
                            reverserForward = true,
                            isBraking = true
                        )
                        // Precision bonus rewards!
                        gamePrefs.addRewards(
                            silver = (activeContract.rewardSilver * 1.25f).toInt(),
                            gold = (activeContract.rewardGold * 1.25f).toInt(),
                            diamonds = activeContract.rewardDiamonds + 1,
                            xp = (activeContract.xpReward * 1.25f).toInt(),
                            contractId = activeContract.id
                        )
                        soundManager.playChime()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // 1. MAIN 2D GAME WORLD CANVAS
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val gameViewH = canvasH - 145.dp.toPx() // Above dashboard console

            // CAMERA SHAKE ON IMPACT
            val shakeOffsetX = if (cameraShakeAmount > 0f) sin(gameElapsedTime * 75f) * cameraShakeAmount else 0f
            val shakeOffsetY = if (cameraShakeAmount > 0f) cos(gameElapsedTime * 85f) * cameraShakeAmount * 0.7f else 0f

            // CAMERA ANCHOR: Center train horizontally
            val screenTrainX = canvasW * 0.54f + shakeOffsetX
            val groundBaseY = gameViewH * 0.64f + shakeOffsetY

            // 100% physically locked visual position (eliminates jitter, jumping, or reversing near station)
            val trainWorldX = physicsState.distanceX * TrainPhysicsEngine.VISUAL_WORLD_SCALE

            // A. DYNAMIC PARALLAX ENVIRONMENT BACKGROUND (Matching contract location!)
            drawEnvironmentBackground(
                environment = activeContract.environment,
                trainWorldX = trainWorldX,
                canvasW = canvasW,
                gameViewH = gameViewH
            )

            // Dynamic Weather Atmosphere Tint & Storm Lighting
            drawWeatherAtmosphere(
                weather = currentWeather,
                canvasW = canvasW,
                gameViewH = gameViewH,
                lightningAlpha = lightningAlpha
            )

            // B. REMADE REALISTIC GEOLOGICAL TERRAIN & TRACK EMBANKMENT
            val terrainTheme = getEnvironmentTerrainTheme(activeContract.environment)

            // 1. Deep Subterranean Bedrock Strata (Lowest Layer)
            val deepBedrockPath = Path().apply {
                moveTo(-30f, gameViewH)
                var tx = -30f
                while (tx <= canvasW + 50f) {
                    val worldX = (tx - screenTrainX) + trainWorldX
                    val elev = physicsEngine.getElevation(worldX)
                    val ty = groundBaseY - elev + 48f + sin(worldX * 0.008f) * 6f
                    lineTo(tx, ty)
                    tx += 20f
                }
                lineTo(canvasW + 30f, gameViewH)
                close()
            }
            drawPath(deepBedrockPath, color = terrainTheme.strataDeep)

            // 2. Mid Sedimentary Rock Strata Layer
            val midStrataPath = Path().apply {
                moveTo(-30f, gameViewH)
                var tx = -30f
                while (tx <= canvasW + 50f) {
                    val worldX = (tx - screenTrainX) + trainWorldX
                    val elev = physicsEngine.getElevation(worldX)
                    val ty = groundBaseY - elev + 26f + cos(worldX * 0.006f) * 5f
                    lineTo(tx, ty)
                    tx += 18f
                }
                lineTo(canvasW + 30f, gameViewH)
                close()
            }
            drawPath(midStrataPath, color = terrainTheme.strataMid)

            // 3. Upper Sub-Ballast & Topsoil Layer
            val upperStrataPath = Path().apply {
                moveTo(-30f, gameViewH)
                var tx = -30f
                while (tx <= canvasW + 50f) {
                    val worldX = (tx - screenTrainX) + trainWorldX
                    val elev = physicsEngine.getElevation(worldX)
                    val ty = groundBaseY - elev + 10f
                    lineTo(tx, ty)
                    tx += 15f
                }
                lineTo(canvasW + 30f, gameViewH)
                close()
            }
            drawPath(upperStrataPath, color = terrainTheme.strataTop)

            // Subtle geological sediment horizontal striation lines
            for (st in 1..3) {
                val striationPath = Path()
                var sx = -20f
                val worldX0 = (sx - screenTrainX) + trainWorldX
                striationPath.moveTo(sx, groundBaseY - physicsEngine.getElevation(worldX0) + 18f * st)
                while (sx <= canvasW + 40f) {
                    val wx = (sx - screenTrainX) + trainWorldX
                    val sy = groundBaseY - physicsEngine.getElevation(wx) + (18f * st) + sin(wx * 0.02f) * 2f
                    striationPath.lineTo(sx, sy)
                    sx += 30f
                }
                drawPath(
                    striationPath,
                    color = Color.Black.copy(alpha = 0.15f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                )
            }

            // 4. Engineered Ballast Berm / Shoulder Slope
            val ballastPrismPath = Path().apply {
                moveTo(-30f, gameViewH)
                var tx = -30f
                while (tx <= canvasW + 50f) {
                    val worldX = (tx - screenTrainX) + trainWorldX
                    val elev = physicsEngine.getElevation(worldX)
                    val ty = groundBaseY - elev + 4f
                    lineTo(tx, ty)
                    tx += 15f
                }
                lineTo(canvasW + 30f, gameViewH)
                close()
            }
            drawPath(ballastPrismPath, color = terrainTheme.surfaceSoil)

            // C. DYNAMIC MOVING TRACK SLEEPERS & CONTINUOUS SMOOTH RAILS
            val railPath = Path()
            var prevScreenX = -30f
            var prevWorldX = (prevScreenX - screenTrainX) + trainWorldX
            var prevY = groundBaseY - physicsEngine.getElevation(prevWorldX)
            railPath.moveTo(prevScreenX, prevY)

            var stepX = -10f
            while (stepX <= canvasW + 50f) {
                val curWorldX = (stepX - screenTrainX) + trainWorldX
                val curY = groundBaseY - physicsEngine.getElevation(curWorldX)
                railPath.lineTo(stepX, curY)
                stepX += 20f
            }

            // Ballast bed below rails (Multi-layer crushed rock)
            drawPath(railPath, color = terrainTheme.ballastDark, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 24f))
            drawPath(railPath, color = terrainTheme.ballastMain, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16f))

            // Ballast stone texture speckles
            val ballastGrainSpacing = 16f
            val startGrain = ((trainWorldX - screenTrainX - 40f) / ballastGrainSpacing).toInt()
            val endGrain = ((trainWorldX - screenTrainX + canvasW + 40f) / ballastGrainSpacing).toInt()
            for (gi in startGrain..endGrain) {
                val gx = gi * ballastGrainSpacing - trainWorldX + screenTrainX
                val gWorldX = gi * ballastGrainSpacing
                val gElev = physicsEngine.getElevation(gWorldX)
                val gy = groundBaseY - gElev + 6f + ((gi * 7) % 11) - 5f
                drawCircle(
                    color = if (gi % 2 == 0) terrainTheme.ballastDark else Color.White.copy(alpha = 0.25f),
                    radius = 1.4f,
                    center = Offset(gx, gy)
                )
            }

            // Dynamic 3D Wooden Ties / Sleepers with Cast Iron Tie Plates & Spikes
            val tieSpacing = 24f
            val startTieWorldIndex = ((trainWorldX - screenTrainX - 60f) / tieSpacing).toInt()
            val endTieWorldIndex = ((trainWorldX - screenTrainX + canvasW + 60f) / tieSpacing).toInt()
            for (ti in startTieWorldIndex..endTieWorldIndex) {
                val tieWorldX = ti * tieSpacing
                val tieScreenX = tieWorldX - trainWorldX + screenTrainX
                val tieElev = physicsEngine.getElevation(tieWorldX)
                val tieSlope = physicsEngine.getSlopeAngle(tieWorldX)
                val tieY = groundBaseY - tieElev
                val tieAngleRad = tieSlope + (Math.PI / 2.0).toFloat()
                val tieHalfLen = 15f

                // Main Creosote Wooden Cross-Tie
                drawLine(
                    color = Color(0xFF2B1B10),
                    start = Offset(tieScreenX - cos(tieAngleRad) * tieHalfLen, tieY - sin(tieAngleRad) * tieHalfLen + 5f),
                    end = Offset(tieScreenX + cos(tieAngleRad) * tieHalfLen, tieY + sin(tieAngleRad) * tieHalfLen + 5f),
                    strokeWidth = 6.0f
                )
                // Tie Highlight
                drawLine(
                    color = Color(0xFF4A3324),
                    start = Offset(tieScreenX - cos(tieAngleRad) * (tieHalfLen - 2f), tieY - sin(tieAngleRad) * (tieHalfLen - 2f) + 4f),
                    end = Offset(tieScreenX + cos(tieAngleRad) * (tieHalfLen - 2f), tieY + sin(tieAngleRad) * (tieHalfLen - 2f) + 4f),
                    strokeWidth = 2.0f
                )
                // Cast Iron Tie Plate Base & Spikes
                drawCircle(color = Color(0xFF1E293B), radius = 2.2f, center = Offset(tieScreenX - 4f, tieY + 4f))
                drawCircle(color = Color(0xFF94A3B8), radius = 1.0f, center = Offset(tieScreenX - 4f, tieY + 3.5f))
                drawCircle(color = Color(0xFF1E293B), radius = 2.2f, center = Offset(tieScreenX + 4f, tieY + 4f))
                drawCircle(color = Color(0xFF94A3B8), radius = 1.0f, center = Offset(tieScreenX + 4f, tieY + 3.5f))
            }

            // Double Steel Rail base (web shadow) and mirror polished railhead
            drawPath(railPath, color = Color(0xFF1E242B), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.5f))
            drawPath(railPath, color = Color(0xFF64748B), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f))
            drawPath(railPath, color = Color(0xFFF8FAFC), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.8f))

            // Trackside Environmental Vegetation & Rock Details
            val vegSpacing = 90f
            val startVeg = ((trainWorldX - screenTrainX - 60f) / vegSpacing).toInt()
            val endVeg = ((trainWorldX - screenTrainX + canvasW + 60f) / vegSpacing).toInt()
            for (vi in startVeg..endVeg) {
                val vWorldX = vi * vegSpacing + ((vi * 37) % 40)
                val vScreenX = vWorldX - trainWorldX + screenTrainX
                val vElev = physicsEngine.getElevation(vWorldX)
                val vBaseY = groundBaseY - vElev + 14f

                if (vi % 3 == 0) {
                    // Shrub / Grass Tuft
                    drawCircle(color = terrainTheme.vegetationTuft.copy(alpha = 0.85f), radius = 4f, center = Offset(vScreenX, vBaseY))
                    drawCircle(color = terrainTheme.vegetationTuft.copy(alpha = 0.95f), radius = 3f, center = Offset(vScreenX - 3f, vBaseY + 1f))
                    drawCircle(color = terrainTheme.vegetationTuft.copy(alpha = 0.95f), radius = 3.5f, center = Offset(vScreenX + 3f, vBaseY + 1f))
                } else if (vi % 5 == 0) {
                    // Embankment Boulder / Rock
                    drawRoundRect(
                        color = terrainTheme.rockColor,
                        topLeft = Offset(vScreenX - 4f, vBaseY - 2f),
                        size = Size(9f, 6f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                    )
                }
            }

            // Trackside Automatic 3-Aspect Block Signals (Every 650m)
            val signalSpacing = 650f * TrainPhysicsEngine.VISUAL_WORLD_SCALE
            val startSignalIdx = ((trainWorldX - screenTrainX - 100f) / signalSpacing).toInt()
            val endSignalIdx = ((trainWorldX - screenTrainX + canvasW + 100f) / signalSpacing).toInt()
            for (si in startSignalIdx..endSignalIdx) {
                val sigWorldX = si * signalSpacing
                val sigScreenX = sigWorldX - trainWorldX + screenTrainX
                val sigElev = physicsEngine.getElevation(sigWorldX)
                val sigGroundY = groundBaseY - sigElev

                // Signal Mast Post (Steel tubular mast with base relay cabinet)
                drawLine(color = Color(0xFF64748B), start = Offset(sigScreenX, sigGroundY + 8f), end = Offset(sigScreenX, sigGroundY - 76f), strokeWidth = 3.5f)
                // Ladder Rungs
                for (r in 0 until 5) {
                    val ry = sigGroundY - 20f - (r * 10f)
                    drawLine(color = Color(0xFF94A3B8), start = Offset(sigScreenX - 4f, ry), end = Offset(sigScreenX + 4f, ry), strokeWidth = 1.2f)
                }
                // Base Instrument Relay Cabinet
                drawRoundRect(
                    color = Color(0xFF94A3B8),
                    topLeft = Offset(sigScreenX - 6f, sigGroundY - 14f),
                    size = Size(12f, 22f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                )

                // 3-Aspect Circular Target Head
                drawRoundRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(sigScreenX - 9f, sigGroundY - 82f),
                    size = Size(18f, 32f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )

                // Aspect Logic: Green if train is far behind, Yellow if approaching, Red if train has passed
                val distFromTrain = sigWorldX - trainWorldX
                val isOccupied = distFromTrain in -350f..20f
                val isApproaching = distFromTrain in 20f..400f

                val (topLamp, midLamp, botLamp) = when {
                    isOccupied -> Triple(Color(0xFFEF4444), Color(0xFF334155), Color(0xFF334155)) // RED
                    isApproaching -> Triple(Color(0xFF334155), Color(0xFFFBBF24), Color(0xFF334155)) // YELLOW
                    else -> Triple(Color(0xFF334155), Color(0xFF334155), Color(0xFF22C55E)) // GREEN
                }

                // Draw Red, Yellow, Green lenses
                drawCircle(color = topLamp, radius = 3.2f, center = Offset(sigScreenX, sigGroundY - 75f))
                if (topLamp != Color(0xFF334155)) drawCircle(color = topLamp.copy(alpha = 0.4f), radius = 7f, center = Offset(sigScreenX, sigGroundY - 75f))

                drawCircle(color = midLamp, radius = 3.2f, center = Offset(sigScreenX, sigGroundY - 66f))
                if (midLamp != Color(0xFF334155)) drawCircle(color = midLamp.copy(alpha = 0.4f), radius = 7f, center = Offset(sigScreenX, sigGroundY - 66f))

                drawCircle(color = botLamp, radius = 3.2f, center = Offset(sigScreenX, sigGroundY - 57f))
                if (botLamp != Color(0xFF334155)) drawCircle(color = botLamp.copy(alpha = 0.4f), radius = 7f, center = Offset(sigScreenX, sigGroundY - 57f))
            }

            // Trackside Concrete Milepost Markers (Every 250m)
            val mpSpacing = 250f * TrainPhysicsEngine.VISUAL_WORLD_SCALE
            val startMp = ((trainWorldX - screenTrainX - 60f) / mpSpacing).toInt()
            val endMp = ((trainWorldX - screenTrainX + canvasW + 60f) / mpSpacing).toInt()
            for (mi in startMp..endMp) {
                val mpWorldX = mi * mpSpacing
                val mpScreenX = mpWorldX - trainWorldX + screenTrainX
                val mpElev = physicsEngine.getElevation(mpWorldX)
                val mpGroundY = groundBaseY - mpElev

                // Concrete post with beveled pyramid top
                drawRoundRect(
                    color = Color(0xFFE2E8F0),
                    topLeft = Offset(mpScreenX + 16f, mpGroundY - 14f),
                    size = Size(7f, 20f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f, 1f)
                )
                // Black MP marking line
                drawLine(
                    color = Color(0xFF0F172A),
                    start = Offset(mpScreenX + 18f, mpGroundY - 8f),
                    end = Offset(mpScreenX + 21f, mpGroundY - 8f),
                    strokeWidth = 1.2f
                )
            }

            // Trackside Telegraph Poles & Swaying Communication Lines
            val poleSpacing = 360f
            val startPoleIdx = ((trainWorldX - screenTrainX - 100f) / poleSpacing).toInt()
            val endPoleIdx = ((trainWorldX - screenTrainX + canvasW + 100f) / poleSpacing).toInt()
            for (pi in startPoleIdx..endPoleIdx) {
                val poleWorldX = pi * poleSpacing
                val poleScreenX = poleWorldX - trainWorldX + screenTrainX
                val poleElev = physicsEngine.getElevation(poleWorldX)
                val poleGroundY = groundBaseY - poleElev

                // Wooden Telegraph Pole with crossarm
                drawLine(
                    color = Color(0xFF4A3728),
                    start = Offset(poleScreenX, poleGroundY + 8f),
                    end = Offset(poleScreenX, poleGroundY - 74f),
                    strokeWidth = 4.5f
                )
                // Crossarm
                drawLine(
                    color = Color(0xFF3E2C1C),
                    start = Offset(poleScreenX - 18f, poleGroundY - 64f),
                    end = Offset(poleScreenX + 18f, poleGroundY - 64f),
                    strokeWidth = 3.5f
                )
                // Ceramic Insulators
                drawCircle(color = Color(0xFF93C5FD), radius = 2.5f, center = Offset(poleScreenX - 14f, poleGroundY - 66f))
                drawCircle(color = Color(0xFF93C5FD), radius = 2.5f, center = Offset(poleScreenX + 14f, poleGroundY - 66f))

                // Telegraph sagging line to next pole
                val nextPoleScreenX = poleScreenX + poleSpacing
                val nextPoleElev = physicsEngine.getElevation((pi + 1) * poleSpacing)
                val nextPoleGroundY = groundBaseY - nextPoleElev
                val wirePath = Path().apply {
                    moveTo(poleScreenX + 14f, poleGroundY - 66f)
                    val midWireX = (poleScreenX + 14f + nextPoleScreenX - 14f) / 2f
                    val midWireY = ((poleGroundY - 66f + nextPoleGroundY - 66f) / 2f) + 12f
                    quadraticBezierTo(midWireX, midWireY, nextPoleScreenX - 14f, nextPoleGroundY - 66f)
                }
                drawPath(wirePath, color = Color(0xFF334155).copy(alpha = 0.7f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2f))
            }

            // Destination Grand Terminal Station Platform (Removing tunnel prompt / replacing with scenic passenger terminal)
            val destWorldX = activeContract.distanceMeters * TrainPhysicsEngine.VISUAL_WORLD_SCALE
            val stationScreenX = destWorldX - trainWorldX + screenTrainX
            if (stationScreenX in -600f..(canvasW + 800f)) {
                val stationElev = physicsEngine.getElevation(destWorldX)
                val stationGroundY = groundBaseY - stationElev

                // 1. Station Building & Grand Glass Canopy Background
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
                        startY = stationGroundY - 240f,
                        endY = stationGroundY + 10f
                    ),
                    topLeft = Offset(stationScreenX - 40f, stationGroundY - 220f),
                    size = Size(480f, 230f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
                )

                // Arched Glass Canopy Roof
                for (canopyOffset in listOf(0f, 120f, 240f, 360f)) {
                    drawRoundRect(
                        color = Color(0xFF38BDF8).copy(alpha = 0.25f),
                        topLeft = Offset(stationScreenX - 30f + canopyOffset, stationGroundY - 210f),
                        size = Size(105f, 180f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f)
                    )
                }

                // Station Clock & Arrival Sign
                drawRoundRect(
                    color = Color(0xFF0284C7),
                    topLeft = Offset(stationScreenX + 120f, stationGroundY - 205f),
                    size = Size(160f, 24f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
                // Station Clock
                drawCircle(color = Color.White, radius = 10f, center = Offset(stationScreenX + 200f, stationGroundY - 170f))
                drawCircle(color = Color(0xFF0F172A), radius = 10f, center = Offset(stationScreenX + 200f, stationGroundY - 170f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                drawLine(color = Color(0xFF0F172A), start = Offset(stationScreenX + 200f, stationGroundY - 170f), end = Offset(stationScreenX + 200f, stationGroundY - 176f), strokeWidth = 2f)
                drawLine(color = Color(0xFF0F172A), start = Offset(stationScreenX + 200f, stationGroundY - 170f), end = Offset(stationScreenX + 205f, stationGroundY - 170f), strokeWidth = 1.5f)

                // Platform Signals & Green Lanterns
                drawCircle(color = Color(0xFF22C55E), radius = 6f, center = Offset(stationScreenX - 15f, stationGroundY - 130f))
                drawCircle(color = Color(0x6622C55E), radius = 14f, center = Offset(stationScreenX - 15f, stationGroundY - 130f))

                // Passenger Station Platform
                drawRoundRect(
                    color = Color(0xFF475569),
                    topLeft = Offset(stationScreenX - 260f, stationGroundY - 10f),
                    size = Size(720f, 18f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
                )
                // Yellow Safety Edge Line
                drawLine(
                    color = Color(0xFFFBBF24),
                    start = Offset(stationScreenX - 260f, stationGroundY - 10f),
                    end = Offset(stationScreenX + 460f, stationGroundY - 10f),
                    strokeWidth = 3.5f
                )

                // 2. DESIGNATED GREEN STOPPING AREA (Contract Delivery Stopping Point - Enlarged)
                val greenStartScreenX = stationScreenX - 160f
                val greenEndScreenX = stationScreenX + 320f
                val greenWidth = greenEndScreenX - greenStartScreenX

                // Glowing Green Ballast Under Track
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0x7722C55E), Color(0x3310B981), Color(0x05047857)),
                        startY = stationGroundY - 8f,
                        endY = stationGroundY + 18f
                    ),
                    topLeft = Offset(greenStartScreenX, stationGroundY - 8f),
                    size = Size(greenWidth, 26f)
                )

                // High-Intensity Green Safety Platform Edge with Neon Halo
                drawLine(
                    color = Color(0x5522C55E),
                    start = Offset(greenStartScreenX, stationGroundY - 10f),
                    end = Offset(greenEndScreenX, stationGroundY - 10f),
                    strokeWidth = 12f
                )
                drawLine(
                    color = Color(0xFF22C55E),
                    start = Offset(greenStartScreenX, stationGroundY - 10f),
                    end = Offset(greenEndScreenX, stationGroundY - 10f),
                    strokeWidth = 5f
                )
                // Striped white-green diagonal safety marks on platform edge
                for (cx in (greenStartScreenX.toInt()..greenEndScreenX.toInt() step 24)) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.85f),
                        start = Offset(cx.toFloat(), stationGroundY - 7f),
                        end = Offset(cx.toFloat() + 8f, stationGroundY - 12f),
                        strokeWidth = 2.5f
                    )
                }

                // Overhead Illuminated Canopy "DELIVERY STOP ZONE" Sign
                val midGreenX = (greenStartScreenX + greenEndScreenX) / 2f
                drawRoundRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(midGreenX - 95f, stationGroundY - 160f),
                    size = Size(190f, 26f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
                drawRoundRect(
                    color = Color(0xFF22C55E),
                    topLeft = Offset(midGreenX - 95f, stationGroundY - 160f),
                    size = Size(190f, 26f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )
                // Pulsing Green Beacon indicators on canopy sign
                val signPulse = (sin(gameElapsedTime * 4f) * 0.3f + 0.7f).coerceIn(0.4f, 1f)
                drawCircle(color = Color(0xFF22C55E).copy(alpha = signPulse), radius = 5f, center = Offset(midGreenX - 80f, stationGroundY - 147f))
                drawCircle(color = Color(0xFF22C55E).copy(alpha = signPulse), radius = 5f, center = Offset(midGreenX + 80f, stationGroundY - 147f))

                // Green Stopping Area Boundary Pylons (Entrance and Exit Beacons)
                for (pylonX in listOf(greenStartScreenX, greenEndScreenX)) {
                    // Mast
                    drawLine(color = Color(0xFF64748B), start = Offset(pylonX, stationGroundY - 10f), end = Offset(pylonX, stationGroundY - 85f), strokeWidth = 3f)
                    // Crossbar
                    drawLine(color = Color(0xFF64748B), start = Offset(pylonX - 8f, stationGroundY - 75f), end = Offset(pylonX + 8f, stationGroundY - 75f), strokeWidth = 2.5f)
                    // Bright Green Beacon
                    drawCircle(color = Color(0x6622C55E), radius = 12f, center = Offset(pylonX, stationGroundY - 85f))
                    drawCircle(color = Color(0xFF22C55E), radius = 6f, center = Offset(pylonX, stationGroundY - 85f))
                    drawCircle(color = Color.White, radius = 2.5f, center = Offset(pylonX, stationGroundY - 85f))
                }

                // Station Lamp Posts
                for (lampX in listOf(stationScreenX - 160f, stationScreenX + 260f)) {
                    drawLine(color = Color(0xFF94A3B8), start = Offset(lampX, stationGroundY - 10f), end = Offset(lampX, stationGroundY - 95f), strokeWidth = 3.5f)
                    drawCircle(color = Color(0xFFFEF08A), radius = 6f, center = Offset(lampX, stationGroundY - 95f))
                    drawCircle(color = Color(0x33FEF08A), radius = 18f, center = Offset(lampX, stationGroundY - 95f))
                }
            }

            // D. TRAILING RAILCARS (Authentic matched consist resolved for active locomotive)
            var trailingOffset = 310f
            for (car in consistCars) {
                val carWorldX = trainWorldX - trailingOffset
                val carFrontBogieX = carWorldX + 80f
                val carRearBogieX = carWorldX - 80f

                val carFrontRailY = groundBaseY - physicsEngine.getElevation(carFrontBogieX)
                val carRearRailY = groundBaseY - physicsEngine.getElevation(carRearBogieX)
                val carMidRailY = (carFrontRailY + carRearRailY) / 2f

                // Exact pitch angle of railcar from its front and rear wheel trucks
                val carPitchRad = atan2(carFrontRailY - carRearRailY, carFrontBogieX - carRearBogieX)
                val carPitchDeg = Math.toDegrees(carPitchRad.toDouble()).toFloat()

                val carScreenX = screenTrainX - trailingOffset
                val carScreenY = carMidRailY

                val activeVariant = gameState.trainLiveryVariants[train.id] ?: "V1_VIRGIN"
                // drawRailCarDetailed has center mid-bogie at (130f, 122f) where 122f is the wheel contact line!
                withTransform({
                    translate(carScreenX - 130f, carScreenY - 122f)
                    rotate(degrees = carPitchDeg, pivot = Offset(130f, 122f))
                }) {
                    drawRailCarDetailed(
                        car = car,
                        wheelAngleRad = wheelRotationAngle,
                        widthPx = 260f,
                        heightPx = 140f,
                        variant = activeVariant,
                        bodyColor = bodyColor,
                        stripeColor = stripeColor
                    )
                }
                trailingOffset += 270f
            }

            // E. MAIN LOCOMOTIVE (Mathematically anchored on wheel line so wheels touch rails!)
            val locoFrontBogieX = trainWorldX + 112f
            val locoRearBogieX = trainWorldX - 112f
            val locoFrontRailY = groundBaseY - physicsEngine.getElevation(locoFrontBogieX)
            val locoRearRailY = groundBaseY - physicsEngine.getElevation(locoRearBogieX)
            val locoMidRailY = (locoFrontRailY + locoRearRailY) / 2f

            val locoPitchRad = atan2(locoFrontRailY - locoRearRailY, locoFrontBogieX - locoRearBogieX)
            val locoPitchDeg = Math.toDegrees(locoPitchRad.toDouble()).toFloat()

            val locoScreenX = screenTrainX
            val locoScreenY = locoMidRailY

            val activeVariant = gameState.trainLiveryVariants[train.id] ?: "V1_VIRGIN"
            // drawLocomotiveDetailed has mid-bogie at (177.5f, 122f) where 122f is the wheel contact line!
            withTransform({
                translate(locoScreenX - 177.5f, locoScreenY - 122f)
                rotate(degrees = locoPitchDeg, pivot = Offset(177.5f, 122f))
            }) {
                drawLocomotiveDetailed(
                    train = train,
                    bodyColor = bodyColor,
                    stripeColor = stripeColor,
                    wheelAngleRad = wheelRotationAngle,
                    widthPx = 360f,
                    heightPx = 140f,
                    variant = activeVariant
                )

                // Overheated Diesel Fire / Flames: Blazing flames licking from exhaust & engine bay until cooled down
                if (physicsState.isOverheating && (train.type == TrainType.DIESEL)) {
                    val s = 360f / 360f
                    val flicker1 = sin(gameElapsedTime * 32f) * 4f
                    val flicker2 = cos(gameElapsedTime * 38f) * 3f
                    // Exhaust stack fireball plume
                    drawCircle(
                        color = Color(0xFFFF2200).copy(alpha = 0.85f),
                        radius = 24f + flicker1,
                        center = Offset(130f * s, 6f * s + flicker2)
                    )
                    drawCircle(
                        color = Color(0xFFFF9900).copy(alpha = 0.95f),
                        radius = 16f + flicker1 * 0.7f,
                        center = Offset(130f * s, 4f * s + flicker2)
                    )
                    drawCircle(
                        color = Color(0xFFFFFF88).copy(alpha = 0.90f),
                        radius = 8f + flicker1 * 0.4f,
                        center = Offset(130f * s, 2f * s)
                    )
                    // Blazing engine compartment grilles
                    drawRoundRect(
                        color = Color(0xCCFF3700),
                        topLeft = Offset(110f * s, 22f * s + flicker2),
                        size = Size(100f * s, 32f * s + flicker1),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * s, 6f * s)
                    )
                    drawRoundRect(
                        color = Color(0xEEFFAA00),
                        topLeft = Offset(120f * s, 24f * s),
                        size = Size(80f * s, 20f * s),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * s, 4f * s)
                    )
                }

                // 1. ALL TRAINS: Front Headlight Fixture & Volumetric Forward Light Beam
                val headlightX = 356f
                val headlightY = 74f
                val ditchLightLeft = Offset(352f, 102f)
                val ditchLightRight = Offset(358f, 102f)

                if (isHeadlightOn) {
                    // Volumetric conical forward beam casting across the track
                    val beamPath = Path().apply {
                        moveTo(headlightX, headlightY - 3f)
                        lineTo(headlightX + 540f, -40f)
                        lineTo(headlightX + 560f, 140f)
                        lineTo(headlightX, headlightY + 5f)
                        close()
                    }
                    val beamBrush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xEEFFFBEB),
                            Color(0x99FEF08A),
                            Color(0x44FDE047),
                            Color(0x15FBBF24),
                            Color.Transparent
                        ),
                        startX = headlightX,
                        endX = headlightX + 560f
                    )
                    drawPath(beamPath, brush = beamBrush)

                    // Dense Fog Atmospheric Light Scattering: in fog, the headlights cut brilliantly through mist
                    if (currentWeather == WeatherType.FOG) {
                        val fogScatterPath = Path().apply {
                            moveTo(headlightX - 10f, headlightY - 15f)
                            lineTo(headlightX + 480f, -60f)
                            lineTo(headlightX + 500f, 160f)
                            lineTo(headlightX - 10f, headlightY + 20f)
                            close()
                        }
                        drawPath(
                            fogScatterPath,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0x66E0F2FE), Color(0x33BAE6FD), Color.Transparent),
                                startX = headlightX - 10f,
                                endX = headlightX + 500f
                            )
                        )
                    }

                    // Illuminated Rail Gleam
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xCCFEF08A), Color(0x55FBBF24), Color.Transparent),
                            startX = headlightX,
                            endX = headlightX + 420f
                        ),
                        start = Offset(headlightX, 122f),
                        end = Offset(headlightX + 420f, 122f),
                        strokeWidth = 3f
                    )

                    // Brilliant Headlight Corona & Lens Flares
                    drawCircle(color = Color(0x66FEF08A), radius = 20f, center = Offset(headlightX, headlightY))
                    drawCircle(color = Color(0xCCFDE047), radius = 10f, center = Offset(headlightX, headlightY))
                    drawCircle(color = Color.White, radius = 5.5f, center = Offset(headlightX, headlightY))

                    // Ditch Lights (Twin front pilot warning lamps)
                    for (ditchPos in listOf(ditchLightLeft, ditchLightRight)) {
                        drawCircle(color = Color(0x44FEF08A), radius = 10f, center = ditchPos)
                        drawCircle(color = Color(0xEEFDE047), radius = 4.5f, center = ditchPos)
                        drawCircle(color = Color.White, radius = 2.5f, center = ditchPos)
                    }
                } else {
                    // Headlights Off: Clear optical lenses with chrome bezels
                    drawCircle(color = Color(0xFF334155), radius = 5.5f, center = Offset(headlightX, headlightY))
                    drawCircle(color = Color(0xFF94A3B8), radius = 5.5f, center = Offset(headlightX, headlightY), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f))
                    for (ditchPos in listOf(ditchLightLeft, ditchLightRight)) {
                        drawCircle(color = Color(0xFF334155), radius = 3.5f, center = ditchPos)
                        drawCircle(color = Color(0xFF94A3B8), radius = 3.5f, center = ditchPos, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2f))
                    }
                }

                // Active game particles (exhaust smoke from stack, wheel sparks on wheel-rail contact, sand spray on wheels, diesel overheat fire)
                drawGameParticles(particles)
            }

            // Foreground Station Terminal End Buffer Stop (Heavy industrial track bumper post)
            if (stationScreenX in -600f..(canvasW + 800f)) {
                val stationElev = physicsEngine.getElevation(destWorldX)
                val stationGroundY = groundBaseY - stationElev
                val bufferX = stationScreenX + 440f

                // Base Anchor Foundation
                drawRect(color = Color(0xFF1E293B), topLeft = Offset(bufferX - 18f, stationGroundY - 6f), size = Size(38f, 14f))

                // Heavy Reinforced A-Frame Steel Girders
                drawLine(color = Color(0xFF7F1D1D), start = Offset(bufferX - 16f, stationGroundY - 4f), end = Offset(bufferX + 4f, stationGroundY - 48f), strokeWidth = 9f)
                drawLine(color = Color(0xFF991B1B), start = Offset(bufferX + 16f, stationGroundY - 4f), end = Offset(bufferX + 4f, stationGroundY - 48f), strokeWidth = 9f)

                // Impact Buffer Head with Red/White Hazard Chevrons
                drawRoundRect(
                    color = Color(0xFFDC2626),
                    topLeft = Offset(bufferX - 14f, stationGroundY - 46f),
                    size = Size(26f, 40f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
                // Diagonal safety stripes
                for (stripY in listOf(stationGroundY - 42f, stationGroundY - 32f, stationGroundY - 22f, stationGroundY - 12f)) {
                    drawLine(color = Color.White, start = Offset(bufferX - 12f, stripY + 6f), end = Offset(bufferX + 10f, stripY - 6f), strokeWidth = 3f)
                }

                // Dual Hydraulic Compression Bumper Cylinders (Contact plates)
                drawCircle(color = Color(0xFF475569), radius = 9f, center = Offset(bufferX - 12f, stationGroundY - 26f))
                drawCircle(color = Color(0xFFFBBF24), radius = 6.5f, center = Offset(bufferX - 12f, stationGroundY - 26f))
                drawCircle(color = Color(0xFF0F172A), radius = 2.5f, center = Offset(bufferX - 12f, stationGroundY - 26f))

                // End-of-Track Blinking Red Warning Signal Beacon on top
                val beaconBlink = (gameElapsedTime * 3.5f).toInt() % 2 == 0
                val beaconColor = if (beaconBlink) Color(0xFFFF2222) else Color(0xFF7F1D1D)
                drawCircle(color = beaconColor, radius = 6f, center = Offset(bufferX + 4f, stationGroundY - 56f))
                if (beaconBlink) {
                    drawCircle(color = Color(0x66FF2222), radius = 14f, center = Offset(bufferX + 4f, stationGroundY - 56f))
                }
            }

            // Dynamic Falling Weather Precipitation (Rain drops, Blizzard squalls, Snow flakes, Fog)
            drawWeatherPrecipitation(
                weather = currentWeather,
                canvasW = canvasW,
                gameViewH = gameViewH,
                speedKmh = physicsState.speedKmh,
                animTime = gameElapsedTime
            )
        }

        // 2. TOP HUD BAR (Matching Screenshot 1)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Quick Status & Boost Button (Top Left)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Conductor Avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2563EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Train, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Supercharge Boost Button
                val isBoosted = System.currentTimeMillis() < boostActiveUntil
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isBoosted) TrainYellowPrimary else Color(0xFFD97706))
                        .clickable {
                            boostActiveUntil = System.currentTimeMillis() + 6000L
                            soundManager.playChime()
                        }
                        .testTag("supercharge_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = "Boost", tint = Color.White, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Dynamic Weather Badge & Quick Weather Switcher
                val weatherIcon = when (currentWeather) {
                    WeatherType.CLEAR -> "☀️"
                    WeatherType.RAIN -> "🌧️"
                    WeatherType.THUNDERSTORM -> "⛈️"
                    WeatherType.SNOW -> "❄️"
                    WeatherType.BLIZZARD -> "🌨️"
                    WeatherType.FOG -> "🌫️"
                }
                val weatherGripPercent = (currentWeather.frictionMultiplier * 100).toInt()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.9f))
                        .clickable {
                            val allWeathers = WeatherType.values()
                            val nextIdx = (currentWeather.ordinal + 1) % allWeathers.size
                            currentWeather = allWeathers[nextIdx]
                            soundManager.playLeverClick()
                        }
                        .padding(horizontal = 7.dp, vertical = 6.dp)
                        .testTag("weather_selector_chip")
                ) {
                    Text(weatherIcon, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = currentWeather.displayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "${weatherGripPercent}% Grip",
                            color = if (weatherGripPercent < 80) TrainYellowPrimary else Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 7.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Speed Limit Indicator Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.9f))
                        .padding(horizontal = 7.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Speed Limit",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "SPEED LIMIT",
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.sp
                        )
                        Text(
                            text = "${physicsState.speedLimitKmh.toInt()} KM/H",
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Central Route Progress Bar with km display (Matching Screenshot 1)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                val progressFraction = (physicsState.distanceX / activeContract.distanceMeters).coerceIn(0f, 1f)
                val remainingKm = ((activeContract.distanceMeters - physicsState.distanceX).coerceAtLeast(0f) / 1000f)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Train Name at the top
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 3.dp)
                    ) {
                        Text(train.countryFlag, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = train.name.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Track Line with Station Nodes
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.85f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .fillMaxSize()
                                .background(TrainLightGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Distance Remaining display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${(physicsState.distanceX / 1000f).let { "%.2f".format(it) }} km",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = TrainYellowPrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${"%.2f".format(remainingKm)} km to ${activeContract.destinationStation}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Top Right Currency Stats & Pause Button (Matching Screenshot 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Driver Level Star
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.85f))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text("⭐", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("LVL ${gameState.driverLevel}", color = TrainYellowPrimary, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Gold Coins
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.85f))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text("🪙", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${gameState.goldCoins}", color = GameGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Diamonds
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.85f))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text("💎", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${gameState.diamonds}", color = GameDiamond, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Pause Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2563EB))
                        .clickable { isPaused = true }
                        .testTag("game_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }

        // REVERSER INTERLOCK LOCKOUT WARNING
        if (reverserLockoutWarning != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0369A1).copy(alpha = 0.95f))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔒", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = reverserLockoutWarning ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        // DELIVERY POINT & GREEN ZONE GUIDANCE BANNER
        val distRemaining = activeContract.distanceMeters - physicsState.distanceX
        if (!isJobCompleted && reverserLockoutWarning == null) {
            when {
                distRemaining in 25f..220f -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0284C7).copy(alpha = 0.92f))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏁", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "APPROACHING DESTINATION (${distRemaining.toInt()}m): SLOW DOWN TO STOP IN GREEN AREA!",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                distRemaining in -12f..25f -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(TrainGreen.copy(alpha = 0.95f))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🟩", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "INSIDE GREEN ZONE! APPLY BRAKES TO BRING TRAIN TO A COMPLETE STOP!",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
                distRemaining < -12f && !hasImpactedBuffer -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDC2626).copy(alpha = 0.95f))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🚨", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OVERSHOOT WARNING! EMERGENCY BRAKE NOW — BUFFER STOP AHEAD!",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            }
        }

        // OVERHEAT WARNING BANNER
        if (physicsState.isOverheating) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 88.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TrainSafetyRed.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OVERHEATING! TOGGLE WATER COOLING [ON]!",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // WHEEL SLIP WARNING BANNER
        if (physicsState.isWheelsSlipping) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 124.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFD97706).copy(alpha = 0.9f))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WHEEL SLIP DETECTED (${currentWeather.displayName})! TOGGLE SAND [ON]!",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 3. BOTTOM DASHBOARD CONSOLE (Remodeled cockpit with sliders)
        DashboardConsole(
            speedKmh = physicsState.speedKmh,
            speedLimitKmh = physicsState.speedLimitKmh,
            engineTempC = physicsState.engineTempC,
            isOverheating = physicsState.isOverheating,
            steamPressurePsi = physicsState.steamPressurePsi,
            brakePipePsi = physicsState.brakePipePsi,
            brakeCylinderPsi = physicsState.brakeCylinderPsi,
            dieselRpm = physicsState.dieselRpm,
            powerKw = physicsState.powerKw,
            isSandingActive = physicsState.isSandingActive,
            isCoolingActive = physicsState.isCoolingActive,
            coolantUnits = gameState.coolantUnits,
            sandUnits = gameState.sandUnits,
            waterFlowPercent = physicsState.waterFlowPercent,
            sandFlowPercent = physicsState.sandFlowPercent,
            throttle = physicsState.throttle,
            brakeLevel = physicsState.brakeLevel,
            reverserSlider = physicsState.reverserSlider,
            gearPosition = physicsState.gearPosition,
            driveMode = physicsState.driveMode,
            isWheelsSlipping = physicsState.isWheelsSlipping,
            isHeadlightOn = isHeadlightOn,
            onToggleHeadlight = {
                isHeadlightOn = !isHeadlightOn
                soundManager.playDriveModeClick()
            },
            onThrottleChange = { newT ->
                val newDriveMode = if (physicsState.driveMode == TrainDriveMode.EMERGENCY_BRAKE && physicsState.brakeLevel < 0.90f) {
                    when {
                        physicsState.gearPosition == GearPosition.FORWARD -> TrainDriveMode.FORWARD
                        physicsState.gearPosition == GearPosition.REVERSE -> TrainDriveMode.REVERSE
                        else -> TrainDriveMode.NEUTRAL
                    }
                } else physicsState.driveMode
                physicsState = physicsState.copy(throttle = newT, driveMode = newDriveMode)
                soundManager.playLeverClick()
            },
            onBrakeChange = { newBrake ->
                val prevBrake = physicsState.brakeLevel
                val newDriveMode = when {
                    newBrake >= 0.95f -> TrainDriveMode.EMERGENCY_BRAKE
                    newBrake > 0.05f -> TrainDriveMode.DYNAMIC_BRAKE
                    physicsState.gearPosition == GearPosition.FORWARD -> TrainDriveMode.FORWARD
                    physicsState.gearPosition == GearPosition.REVERSE -> TrainDriveMode.REVERSE
                    else -> TrainDriveMode.NEUTRAL
                }
                physicsState = physicsState.copy(brakeLevel = newBrake, driveMode = newDriveMode)
                if (newBrake > 0.80f && prevBrake <= 0.80f) {
                    soundManager.playHissSound(700)
                } else if (newBrake == 0f && prevBrake > 0.1f) {
                    soundManager.playHissSound(400)
                }
            },
            onReverserChange = { newRev ->
                val isStopped = abs(physicsState.speedKmh) <= 0.5f
                val requestedRev = newRev < -0.15f

                if (requestedRev && !isStopped) {
                    // Traction Interlock: train is accelerating/in motion; prevent reverse engagement!
                    reverserLockoutWarning = "REVERSER INTERLOCK: Train must be completely stopped before shifting to REVERSE!"
                    soundManager.playSwitchTrack()
                    // Clamp to Neutral
                    val safeRev = if (physicsState.reverserSlider < 0f) 0f else physicsState.reverserSlider
                    physicsState = physicsState.copy(reverserSlider = safeRev)
                } else {
                    val newGear = when {
                        newRev in -0.15f..0.15f -> GearPosition.NEUTRAL
                        newRev > 0.15f -> GearPosition.FORWARD
                        else -> GearPosition.REVERSE
                    }
                    val newDriveMode = if (physicsState.brakeLevel >= 0.95f) {
                        TrainDriveMode.EMERGENCY_BRAKE
                    } else if (physicsState.brakeLevel > 0.05f) {
                        TrainDriveMode.DYNAMIC_BRAKE
                    } else {
                        when (newGear) {
                            GearPosition.FORWARD -> TrainDriveMode.FORWARD
                            GearPosition.REVERSE -> TrainDriveMode.REVERSE
                            GearPosition.NEUTRAL -> TrainDriveMode.NEUTRAL
                        }
                    }
                    physicsState = physicsState.copy(reverserSlider = newRev, gearPosition = newGear, driveMode = newDriveMode)
                    soundManager.playDriveModeClick()
                }
            },
            onToggleWaterCooling = {
                val nextState = !physicsState.isCoolingActive
                if (nextState) {
                    if (gamePrefs.useCoolant()) {
                        physicsState = physicsState.copy(isCoolingActive = true, waterFlowPercent = 100)
                        soundManager.playHissSound(800)
                    }
                } else {
                    physicsState = physicsState.copy(isCoolingActive = false, waterFlowPercent = 0)
                }
            },
            onToggleSand = {
                val nextState = !physicsState.isSandingActive
                if (nextState) {
                    if (gamePrefs.useSand()) {
                        physicsState = physicsState.copy(isSandingActive = true, sandFlowPercent = 100)
                        soundManager.playSandSound()
                    }
                } else {
                    physicsState = physicsState.copy(isSandingActive = false, sandFlowPercent = 0)
                }
            },
            onWaterFlowChange = { newPct ->
                if (newPct > 0) {
                    if (gameState.coolantUnits > 0) {
                        physicsState = physicsState.copy(waterFlowPercent = newPct, isCoolingActive = true)
                        soundManager.playHissSound(600)
                    } else {
                        physicsState = physicsState.copy(waterFlowPercent = 0, isCoolingActive = false)
                    }
                } else {
                    physicsState = physicsState.copy(waterFlowPercent = 0, isCoolingActive = false)
                }
            },
            onSandFlowChange = { newPct ->
                if (newPct > 0) {
                    if (gameState.sandUnits > 0) {
                        physicsState = physicsState.copy(sandFlowPercent = newPct, isSandingActive = true)
                        soundManager.playSandSound()
                    } else {
                        physicsState = physicsState.copy(sandFlowPercent = 0, isSandingActive = false)
                    }
                } else {
                    physicsState = physicsState.copy(sandFlowPercent = 0, isSandingActive = false)
                }
            },
            onHornPress = {
                soundManager.blastHorn(train.id, train.type, train.soundProfile)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 4. DELIVERY COMPLETE / VICTORY MODAL
        AnimatedVisibility(
            visible = isJobCompleted,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(360.dp)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val outcomeTitle = when (deliveryStopResult) {
                        DeliveryStopResult.PERFECT_GREEN_ZONE -> "⭐ PERFECT GREEN ZONE STOP!"
                        DeliveryStopResult.BUFFER_STOP_COLLISION -> "💥 HALTED BY BUFFER STOP!"
                        else -> "ROUTE COMPLETED!"
                    }
                    val outcomeColor = when (deliveryStopResult) {
                        DeliveryStopResult.PERFECT_GREEN_ZONE -> TrainLightGreen
                        DeliveryStopResult.BUFFER_STOP_COLLISION -> Color(0xFFF87171)
                        else -> TrainYellowPrimary
                    }
                    Text(outcomeTitle, color = outcomeColor, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (deliveryStopResult == DeliveryStopResult.PERFECT_GREEN_ZONE) {
                            "Precision stopping in green area achieved! (+25% reward bonus awarded)"
                        } else if (deliveryStopResult == DeliveryStopResult.BUFFER_STOP_COLLISION) {
                            "The train failed to stop in the green area and was stopped by the buffer stop."
                        } else {
                            activeContract.title
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Reward list
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🪙", fontSize = 18.sp)
                            Text("+${activeContract.rewardSilver}", color = GameSilver, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🪙", fontSize = 18.sp)
                            Text("+${activeContract.rewardGold}", color = GameGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💎", fontSize = 18.sp)
                            Text("+${activeContract.rewardDiamonds}", color = GameDiamond, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⭐", fontSize = 18.sp)
                            Text("+${activeContract.xpReward} XP", color = TrainLightGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = handleOpenMaintenance,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🔧 SERVICE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = handleBackToWorkshop,
                            colors = ButtonDefaults.buttonColors(containerColor = TrainBrightCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("GARAGE", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = handleBackToMenu,
                            colors = ButtonDefaults.buttonColors(containerColor = TrainGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("MENU", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 5. PAUSE MENU MODAL
        AnimatedVisibility(
            visible = isPaused,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(320.dp)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("GAME PAUSED", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { isPaused = false },
                        colors = ButtonDefaults.buttonColors(containerColor = TrainGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESUME", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            physicsState = TrainPhysicsState()
                            isPaused = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESTART ROUTE", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = handleOpenMaintenance,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔧", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SERVICE / REPAIR BAY", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = handleBackToWorkshop,
                        colors = ButtonDefaults.buttonColors(containerColor = TrainBrightCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("GO TO WORKSHOP", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
