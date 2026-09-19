package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TrainDriveMode
import com.example.physics.GearPosition
import com.example.ui.theme.GaugeFaceDark
import com.example.ui.theme.GaugeNeedleRed
import com.example.ui.theme.TrainBrightCyan
import com.example.ui.theme.TrainGreen
import com.example.ui.theme.TrainLightGreen
import com.example.ui.theme.TrainSafetyRed
import com.example.ui.theme.TrainYellowPrimary
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Modern High-Tech Locomotive Cockpit Console
 * Features:
 * 1. Dedicated Reverser / Gear Slider (REV <-> NEU <-> FWD)
 * 2. Dedicated Brake Slider (0% Release <-> 100% Emergency)
 * 3. Remodeled High-Definition Precision Speedometer with Telemetry
 * 4. Engine Temp, Steam/RPM, Coolant & Sand Reservoirs
 * 5. Dedicated Throttle Notch Slider (0 Idle <-> 8 Max Power)
 */
@Composable
fun DashboardConsole(
    speedKmh: Float,
    speedLimitKmh: Float = 160f,
    engineTempC: Float,
    isOverheating: Boolean,
    steamPressurePsi: Float,
    brakePipePsi: Float = 90f,
    brakeCylinderPsi: Float = 0f,
    dieselRpm: Float = 600f,
    powerKw: Float = 0f,
    isSandingActive: Boolean,
    isCoolingActive: Boolean,
    coolantUnits: Int,
    sandUnits: Int,
    waterFlowPercent: Int = if (isCoolingActive) 100 else 0,
    sandFlowPercent: Int = if (isSandingActive) 100 else 0,
    throttle: Float,
    brakeLevel: Float = 0f,
    reverserSlider: Float = 1.0f,
    gearPosition: GearPosition = GearPosition.FORWARD,
    driveMode: TrainDriveMode = TrainDriveMode.FORWARD,
    isWheelsSlipping: Boolean = false,
    isHeadlightOn: Boolean = false,
    onThrottleChange: (Float) -> Unit,
    onBrakeChange: (Float) -> Unit,
    onReverserChange: (Float) -> Unit,
    onToggleHeadlight: () -> Unit = {},
    onToggleWaterCooling: () -> Unit = {},
    onToggleSand: () -> Unit = {},
    onWaterFlowChange: (Int) -> Unit = {},
    onSandFlowChange: (Int) -> Unit = {},
    onHornPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isReverseAllowed = abs(speedKmh) <= 0.5f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(156.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF242A30), Color(0xFF14181C), Color(0xFF0B0D0F))
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // 1. LEFT: GEAR & REVERSER SLIDER (FWD, NEU, REV)
            ReverserGearSlider(
                reverserValue = reverserSlider,
                gearPosition = gearPosition,
                isReverseAllowed = isReverseAllowed,
                onReverserChange = onReverserChange,
                modifier = Modifier
                    .width(96.dp)
                    .fillMaxHeight()
                    .testTag("reverser_gear_slider")
            )

            // 2. CENTER-LEFT: DEDICATED BRAKE SLIDER (0% Release to 100% Emergency)
            BrakeQuadrantSlider(
                brakeLevel = brakeLevel,
                brakeCylinderPsi = brakeCylinderPsi,
                onBrakeChange = onBrakeChange,
                modifier = Modifier
                    .width(94.dp)
                    .fillMaxHeight()
                    .testTag("brake_slider")
            )

            // 3. CENTER: REMODELED HIGH-PRECISION SPEEDOMETER & TELEMETRY GAUGES
            CenterDashboardCluster(
                speedKmh = speedKmh,
                speedLimitKmh = speedLimitKmh,
                engineTempC = engineTempC,
                isOverheating = isOverheating,
                steamPressurePsi = steamPressurePsi,
                brakePipePsi = brakePipePsi,
                brakeCylinderPsi = brakeCylinderPsi,
                powerKw = powerKw,
                isSandingActive = isSandingActive,
                isCoolingActive = isCoolingActive,
                coolantUnits = coolantUnits,
                sandUnits = sandUnits,
                waterFlowPercent = waterFlowPercent,
                sandFlowPercent = sandFlowPercent,
                isWheelsSlipping = isWheelsSlipping,
                isHeadlightOn = isHeadlightOn,
                onToggleHeadlight = onToggleHeadlight,
                onToggleWaterCooling = onToggleWaterCooling,
                onToggleSand = onToggleSand,
                onWaterFlowChange = onWaterFlowChange,
                onSandFlowChange = onSandFlowChange,
                onHornPress = onHornPress,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 2.dp)
            )

            // 4. RIGHT: THROTTLE ACCELERATION SLIDER (Notches 0-8)
            ThrottleAccelerationLever(
                throttle = throttle,
                powerKw = powerKw,
                onThrottleChange = onThrottleChange,
                modifier = Modifier
                    .width(96.dp)
                    .fillMaxHeight()
                    .testTag("throttle_lever")
            )
        }
    }
}

/**
 * REVERSER / GEAR SLIDER:
 * Allows smooth continuous or notched slider control:
 * Top: FORWARD (+1.0)
 * Middle: NEUTRAL (0.0) -> Gravity & coasting enabled!
 * Bottom: REVERSE (-1.0)
 */
@Composable
fun ReverserGearSlider(
    reverserValue: Float,
    gearPosition: GearPosition,
    isReverseAllowed: Boolean = true,
    onReverserChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedReverser by animateFloatAsState(targetValue = reverserValue.coerceIn(-1f, 1f), label = "reverser_anim")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF282E35), Color(0xFF161A1E), Color(0xFF0F1215))
                )
            )
            .padding(horizontal = 3.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("REVERSER", color = TrainBrightCyan, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                val gearColor = when (gearPosition) {
                    GearPosition.FORWARD -> TrainGreen
                    GearPosition.NEUTRAL -> TrainYellowPrimary
                    GearPosition.REVERSE -> Color(0xFF38BDF8)
                }
                Text(
                    text = gearPosition.label,
                    color = gearColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp
                )
            }

            // Interactive Mechanical Slot Track
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF101316))
                    .pointerInput(isReverseAllowed) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val h = size.height
                            val fraction = (1f - (down.position.y / h)).coerceIn(0f, 1f)
                            val revVal = (fraction * 2f) - 1f
                            val snapped = when {
                                revVal > 0.30f -> 1.0f
                                revVal < -0.30f -> if (isReverseAllowed) -1.0f else 0.0f
                                else -> 0.0f
                            }
                            onReverserChange(snapped)

                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (!change.pressed) break
                                change.consume()
                                val dragFrac = (1f - (change.position.y / h)).coerceIn(0f, 1f)
                                val dragRevVal = (dragFrac * 2f) - 1f
                                val dragSnapped = when {
                                    dragRevVal > 0.30f -> 1.0f
                                    dragRevVal < -0.30f -> if (isReverseAllowed) -1.0f else 0.0f
                                    else -> 0.0f
                                }
                                onReverserChange(dragSnapped)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val slotCenterX = w * 0.35f
                    val slotTop = 8.dp.toPx()
                    val slotBottom = h - 8.dp.toPx()
                    val slotSpan = slotBottom - slotTop

                    // Deep guide slot
                    drawRoundRect(
                        color = Color(0xFF090A0C),
                        topLeft = Offset(slotCenterX - 4.dp.toPx(), slotTop),
                        size = Size(8.dp.toPx(), slotSpan),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )

                    // Three major detents: FWD (top), NEU (middle), REV (bottom)
                    val positions = listOf(
                        Triple(1.0f, TrainGreen, "FWD"),
                        Triple(0.0f, TrainYellowPrimary, "NEU"),
                        Triple(-1.0f, Color(0xFF38BDF8), "REV")
                    )

                    for ((pos, color, _) in positions) {
                        val frac = (pos + 1f) / 2f
                        val y = slotBottom - (frac * slotSpan)
                        val isSelected = when (pos) {
                            1.0f -> gearPosition == GearPosition.FORWARD
                            0.0f -> gearPosition == GearPosition.NEUTRAL
                            else -> gearPosition == GearPosition.REVERSE
                        }

                        drawLine(
                            color = if (isSelected) color else Color(0xFF475569),
                            start = Offset(slotCenterX + 6.dp.toPx(), y),
                            end = Offset(slotCenterX + 16.dp.toPx(), y),
                            strokeWidth = if (isSelected) 2.5.dp.toPx() else 1.2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        if (isSelected) {
                            drawCircle(color = color, radius = 3.dp.toPx(), center = Offset(slotCenterX + 22.dp.toPx(), y))
                        }
                    }

                    // Lever Handle
                    val leverFrac = (animatedReverser + 1f) / 2f
                    val handleY = slotBottom - (leverFrac * slotSpan)

                    // Shaft
                    drawLine(
                        color = Color(0xFFCBD5E1),
                        start = Offset(slotCenterX, slotBottom - (0.5f * slotSpan)),
                        end = Offset(slotCenterX, handleY),
                        strokeWidth = 4.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Knurled Handle Knob
                    val knobW = 34.dp.toPx()
                    val knobH = 12.dp.toPx()
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF334155), Color(0xFF94A3B8), Color(0xFF1E293B))
                        ),
                        topLeft = Offset(slotCenterX - knobW / 2f, handleY - knobH / 2f),
                        size = Size(knobW, knobH),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )

                    val activeGripColor = when (gearPosition) {
                        GearPosition.FORWARD -> TrainGreen
                        GearPosition.NEUTRAL -> TrainYellowPrimary
                        GearPosition.REVERSE -> Color(0xFF38BDF8)
                    }
                    drawCircle(color = activeGripColor, radius = 3.dp.toPx(), center = Offset(slotCenterX, handleY))
                }

                // Text labels on right
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp, top = 4.dp, bottom = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text("FWD", color = if (gearPosition == GearPosition.FORWARD) TrainGreen else Color(0xFF64748B), fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                    Text("NEU", color = if (gearPosition == GearPosition.NEUTRAL) TrainYellowPrimary else Color(0xFF64748B), fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                    Text("REV", color = if (gearPosition == GearPosition.REVERSE) Color(0xFF38BDF8) else Color(0xFF64748B), fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                }
            }

            // Quick Direct Selector Buttons [FWD] [NEU] [REV]
            Row(
                modifier = Modifier.fillMaxWidth().height(20.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Triple(1.0f, "FWD", TrainGreen),
                    Triple(0.0f, "NEU", TrainYellowPrimary),
                    Triple(-1.0f, if (!isReverseAllowed) "🔒REV" else "REV", Color(0xFF38BDF8))
                ).forEach { (pos, label, color) ->
                    val isSelected = when (pos) {
                        1.0f -> gearPosition == GearPosition.FORWARD
                        0.0f -> gearPosition == GearPosition.NEUTRAL
                        else -> gearPosition == GearPosition.REVERSE
                    }
                    val isLockedRev = (pos == -1.0f && !isReverseAllowed)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isSelected) color else if (isLockedRev) Color(0xFF14171A) else Color(0xFF1E242B))
                            .clickable {
                                if (!isLockedRev) {
                                    onReverserChange(pos)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.Black else if (isLockedRev) Color(0xFF64748B) else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Black,
                            fontSize = if (isLockedRev) 6.8.sp else 7.5.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * DEDICATED BRAKE SLIDER:
 * Full continuous vertical slider:
 * 0% = RELEASE (Green)
 * 25% = MIN REDUCTION
 * 50% = SERVICE (Amber)
 * 80% = FULL SERVICE (Orange)
 * 100% = EMERGENCY (Flashing Red)
 */
@Composable
fun BrakeQuadrantSlider(
    brakeLevel: Float,
    brakeCylinderPsi: Float,
    onBrakeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedBrake by animateFloatAsState(targetValue = brakeLevel.coerceIn(0f, 1f), label = "brake_slider_anim")
    val isEmergency = brakeLevel >= 0.95f
    val isService = brakeLevel in 0.15f..0.94f
    val isReleased = brakeLevel < 0.05f

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2E2626), Color(0xFF1A1414), Color(0xFF110D0D))
                )
            )
            .padding(horizontal = 3.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("BRAKE", color = TrainSafetyRed, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                val statusText = when {
                    isEmergency -> "EMG 100%"
                    brakeLevel >= 0.70f -> "FULL ${(brakeLevel * 100).toInt()}%"
                    brakeLevel >= 0.20f -> "SRV ${(brakeLevel * 100).toInt()}%"
                    brakeLevel > 0f -> "MIN ${(brakeLevel * 100).toInt()}%"
                    else -> "REL 0%"
                }
                val statusColor = when {
                    isEmergency -> TrainSafetyRed
                    brakeLevel >= 0.70f -> Color(0xFFEA580C)
                    brakeLevel >= 0.20f -> TrainYellowPrimary
                    brakeLevel > 0f -> Color(0xFF38BDF8)
                    else -> TrainGreen
                }
                Text(text = statusText, color = statusColor, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
            }

            // Interactive Mechanical Brake Slot
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF140D0D))
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val h = size.height
                            val fraction = (1f - (down.position.y / h)).coerceIn(0f, 1f)
                            onBrakeChange(fraction)

                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (!change.pressed) break
                                change.consume()
                                val dragFrac = (1f - (change.position.y / h)).coerceIn(0f, 1f)
                                onBrakeChange(dragFrac)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val slotCenterX = w * 0.35f
                    val slotTop = 8.dp.toPx()
                    val slotBottom = h - 8.dp.toPx()
                    val slotSpan = slotBottom - slotTop

                    // Deep guide slot
                    drawRoundRect(
                        color = Color(0xFF0A0505),
                        topLeft = Offset(slotCenterX - 4.dp.toPx(), slotTop),
                        size = Size(8.dp.toPx(), slotSpan),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )

                    // Brake Region Markers
                    val notches = listOf(
                        Pair(1.0f, TrainSafetyRed),
                        Pair(0.75f, Color(0xFFEA580C)),
                        Pair(0.50f, TrainYellowPrimary),
                        Pair(0.25f, Color(0xFF38BDF8)),
                        Pair(0.0f, TrainGreen)
                    )

                    for ((frac, color) in notches) {
                        val y = slotBottom - (frac * slotSpan)
                        drawLine(
                            color = color,
                            start = Offset(slotCenterX + 6.dp.toPx(), y),
                            end = Offset(slotCenterX + 14.dp.toPx(), y),
                            strokeWidth = 1.8.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Brake Lever Handle
                    val handleY = slotBottom - (animatedBrake * slotSpan)

                    // Metallic Shaft
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(slotCenterX, slotBottom),
                        end = Offset(slotCenterX, handleY),
                        strokeWidth = 4.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Heavy Cast-Iron Brake Handle
                    val knobW = 36.dp.toPx()
                    val knobH = 12.dp.toPx()
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF7F1D1D), Color(0xFFDC2626), Color(0xFF450A0A))
                        ),
                        topLeft = Offset(slotCenterX - knobW / 2f, handleY - knobH / 2f),
                        size = Size(knobW, knobH),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )

                    // Indicator LED
                    drawCircle(
                        color = if (isEmergency) Color.White else if (isReleased) TrainGreen else TrainYellowPrimary,
                        radius = 3.dp.toPx(),
                        center = Offset(slotCenterX, handleY)
                    )
                }

                // Text labels on right
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .padding(end = 3.dp, top = 2.dp, bottom = 2.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text("EMG", color = TrainSafetyRed, fontSize = 7.sp, fontWeight = FontWeight.Black)
                    Text("FULL", color = Color(0xFFEA580C), fontSize = 7.sp, fontWeight = FontWeight.Black)
                    Text("SRV", color = TrainYellowPrimary, fontSize = 7.sp, fontWeight = FontWeight.Black)
                    Text("REL", color = TrainGreen, fontSize = 7.sp, fontWeight = FontWeight.Black)
                }
            }

            // Direct Quick Buttons [REL] [SRV] [EMG]
            Row(
                modifier = Modifier.fillMaxWidth().height(20.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Triple(0.0f, "REL", TrainGreen),
                    Triple(0.50f, "SRV", TrainYellowPrimary),
                    Triple(1.0f, "EMG", TrainSafetyRed)
                ).forEach { (targetBrake, label, color) ->
                    val isActive = when (targetBrake) {
                        0.0f -> isReleased
                        1.0f -> isEmergency
                        else -> brakeLevel in 0.30f..0.70f
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isActive) color else Color(0xFF1E242B))
                            .clickable { onBrakeChange(targetBrake) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isActive) (if (targetBrake == 1.0f) Color.White else Color.Black) else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Black,
                            fontSize = 7.5.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * CENTER DASHBOARD:
 * Remodeled high-precision analog/digital Speedometer with G-meter,
 * Engine Temp Dial, Steam/Air pressure, Reservoir gauges, and Horn trigger.
 * Features dedicated discrete Water Flow and Sand Flow stepped sliders.
 */
val FLOW_DISCRETE_STEPS = listOf(0, 10, 20, 40, 50, 60, 70, 80, 90, 100)

@Composable
fun FlowDiscreteSlider(
    label: String,
    currentPercent: Int,
    unitsRemaining: Int,
    accentColor: Color,
    icon: ImageVector,
    isWarning: Boolean = false,
    warningText: String? = null,
    onPercentChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val currentStepIdx = FLOW_DISCRETE_STEPS.indexOf(currentPercent).let { if (it >= 0) it else 0 }
    val isActive = currentPercent > 0

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) Color(0xFF1B232C) else Color(0xFF13171B))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // Header: Icon + Title + Percent Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isWarning) Color.Yellow else if (isActive) accentColor else Color(0xFF64748B),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = label,
                    color = if (isActive) Color.White else Color(0xFF94A3B8),
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Black
                )
            }

            val badgeText = when {
                unitsRemaining <= 0 -> "EMPTY"
                isWarning && warningText != null -> warningText
                isActive -> "$currentPercent%"
                else -> "OFF"
            }
            val badgeColor = when {
                unitsRemaining <= 0 -> Color(0xFFEF4444)
                isWarning -> Color(0xFFFBBF24)
                isActive -> accentColor
                else -> Color(0xFF64748B)
            }
            Text(
                text = badgeText,
                color = badgeColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Discrete Stepped Slider Track with Step Buttons [-] [ Track ] [+]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Minus Button
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF262E38))
                    .clickable(enabled = currentStepIdx > 0 && unitsRemaining > 0) {
                        val prev = FLOW_DISCRETE_STEPS[(currentStepIdx - 1).coerceAtLeast(0)]
                        onPercentChange(prev)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("-", color = if (currentStepIdx > 0 && unitsRemaining > 0) Color.White else Color(0xFF475569), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Interactive Drag/Tap Stepped Track
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF0F1316))
                    .pointerInput(unitsRemaining) {
                        if (unitsRemaining > 0) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val frac = (offset.x / size.width).coerceIn(0f, 1f)
                                    val targetVal = (frac * 100f).roundToInt()
                                    val nearest = FLOW_DISCRETE_STEPS.minByOrNull { abs(it - targetVal) } ?: 0
                                    onPercentChange(nearest)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    val frac = (change.position.x / size.width).coerceIn(0f, 1f)
                                    val targetVal = (frac * 100f).roundToInt()
                                    val nearest = FLOW_DISCRETE_STEPS.minByOrNull { abs(it - targetVal) } ?: 0
                                    onPercentChange(nearest)
                                }
                            )
                        }
                    }
                    .testTag(testTag)
            ) {
                val fillFraction = (currentPercent / 100f).coerceIn(0f, 1f)

                // Fill Bar
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fillFraction)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(accentColor.copy(alpha = 0.4f), accentColor)
                            )
                        )
                )

                // Notch ticks for 10%, 20%, 40%, 50%, 60%, 70%, 80%, 90%, 100%
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    for (step in FLOW_DISCRETE_STEPS) {
                        if (step == 0) continue
                        val nx = (step / 100f) * w
                        drawLine(
                            color = if (step <= currentPercent) Color.White.copy(alpha = 0.8f) else Color(0xFF334155),
                            start = Offset(nx, 2.dp.toPx()),
                            end = Offset(nx, h - 2.dp.toPx()),
                            strokeWidth = 1.0.dp.toPx()
                        )
                    }

                    // Active Thumb circle
                    if (currentPercent > 0) {
                        val thumbX = (currentPercent / 100f) * w
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = Offset(thumbX.coerceIn(3.dp.toPx(), w - 3.dp.toPx()), h / 2f)
                        )
                    }
                }
            }

            // Plus Button
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF262E38))
                    .clickable(enabled = currentStepIdx < FLOW_DISCRETE_STEPS.lastIndex && unitsRemaining > 0) {
                        val next = FLOW_DISCRETE_STEPS[(currentStepIdx + 1).coerceAtMost(FLOW_DISCRETE_STEPS.lastIndex)]
                        onPercentChange(next)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = if (currentStepIdx < FLOW_DISCRETE_STEPS.lastIndex && unitsRemaining > 0) Color.White else Color(0xFF475569), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CenterDashboardCluster(
    speedKmh: Float,
    speedLimitKmh: Float = 160f,
    engineTempC: Float,
    isOverheating: Boolean,
    steamPressurePsi: Float,
    brakePipePsi: Float,
    brakeCylinderPsi: Float,
    powerKw: Float,
    isSandingActive: Boolean,
    isCoolingActive: Boolean,
    coolantUnits: Int,
    sandUnits: Int,
    waterFlowPercent: Int = if (isCoolingActive) 100 else 0,
    sandFlowPercent: Int = if (isSandingActive) 100 else 0,
    isWheelsSlipping: Boolean = false,
    isHeadlightOn: Boolean = false,
    onToggleHeadlight: () -> Unit = {},
    onToggleWaterCooling: () -> Unit = {},
    onToggleSand: () -> Unit = {},
    onWaterFlowChange: (Int) -> Unit = {},
    onSandFlowChange: (Int) -> Unit = {},
    onHornPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(Color(0xFF13171B))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // A. LEFT SUB-PANEL: COOLANT RESERVOIR + ENGINE TEMPERATURE + WATER SLIDER
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SupplyReservoirGaugeDial(
                    label = "COOLANT",
                    currentUnits = coolantUnits,
                    maxUnits = 100,
                    isActive = waterFlowPercent > 0 || isCoolingActive,
                    activeColor = Color(0xFF38BDF8),
                    icon = "💧",
                    modifier = Modifier.size(46.dp)
                )

                SubGaugeDial(
                    label = "TEMP",
                    value = engineTempC,
                    minVal = 40f,
                    maxVal = 130f,
                    isWarning = isOverheating,
                    unit = "°C",
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Stepped Water / Coolant Flow Slider (0, 10, 20, 40, 50, 60, 70, 80, 90, 100%)
            FlowDiscreteSlider(
                label = "WATER",
                currentPercent = waterFlowPercent,
                unitsRemaining = coolantUnits,
                accentColor = Color(0xFF38BDF8),
                icon = Icons.Default.Opacity,
                isWarning = isOverheating,
                warningText = "HOT!",
                onPercentChange = { newPct ->
                    onWaterFlowChange(newPct)
                },
                modifier = Modifier.width(96.dp),
                testTag = "water_flow_slider"
            )
        }

        // B. CENTER: REMODELED SPEEDOMETER & HORN + HEADLIGHT BUTTONS
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemodeledSpeedometerDial(
                speedKmh = speedKmh,
                speedLimitKmh = speedLimitKmh,
                powerKw = powerKw,
                brakeCylinderPsi = brakeCylinderPsi,
                modifier = Modifier.size(86.dp)
            )

            Spacer(modifier = Modifier.width(3.dp))

            // Stacked Cab Action Buttons: Horn (Top) & Headlight (Bottom)
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Heavy Metallic Horn Trigger Button
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFEF4444), Color(0xFFB91C1C), Color(0xFF7F1D1D))
                            )
                        )
                        .clickable { onHornPress() }
                        .testTag("train_horn_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Horn",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("HORN", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Black)
                    }
                }

                // Headlight Toggle Button
                val headlightBg = if (isHeadlightOn) {
                    listOf(Color(0xFFFBBF24), Color(0xFFD97706), Color(0xFF92400E))
                } else {
                    listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A))
                }
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Brush.verticalGradient(colors = headlightBg))
                        .clickable { onToggleHeadlight() }
                        .testTag("train_headlight_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            val rayColor = if (isHeadlightOn) Color.Black else Color(0xFF94A3B8)
                            drawArc(
                                color = rayColor,
                                startAngle = 90f,
                                sweepAngle = 180f,
                                useCenter = true,
                                topLeft = Offset(1f, 1f),
                                size = Size(size.width * 0.5f, size.height - 2f)
                            )
                            drawLine(color = rayColor, start = Offset(size.width * 0.6f, 2f), end = Offset(size.width, 1f), strokeWidth = 1.5f)
                            drawLine(color = rayColor, start = Offset(size.width * 0.62f, size.height * 0.5f), end = Offset(size.width, size.height * 0.5f), strokeWidth = 1.5f)
                            drawLine(color = rayColor, start = Offset(size.width * 0.6f, size.height - 2f), end = Offset(size.width, size.height - 1f), strokeWidth = 1.5f)
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (isHeadlightOn) "LGT ON" else "LIGHT",
                            color = if (isHeadlightOn) Color.Black else Color(0xFF94A3B8),
                            fontSize = 6.2.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // C. RIGHT SUB-PANEL: STEAM / BRAKE PRESSURE + SAND RESERVOIR + SAND SLIDER
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubGaugeDial(
                    label = "PRESSURE",
                    value = steamPressurePsi,
                    minVal = 30f,
                    maxVal = 140f,
                    isWarning = false,
                    unit = "PSI",
                    modifier = Modifier.size(46.dp)
                )

                SupplyReservoirGaugeDial(
                    label = "SAND",
                    currentUnits = sandUnits,
                    maxUnits = 100,
                    isActive = sandFlowPercent > 0 || isSandingActive,
                    activeColor = Color(0xFFF59E0B),
                    icon = "⏳",
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Stepped Sand Flow Slider (0, 10, 20, 40, 50, 60, 70, 80, 90, 100%)
            FlowDiscreteSlider(
                label = "SAND",
                currentPercent = sandFlowPercent,
                unitsRemaining = sandUnits,
                accentColor = Color(0xFFF59E0B),
                icon = if (isWheelsSlipping) Icons.Default.Warning else Icons.Default.ElectricBolt,
                isWarning = isWheelsSlipping,
                warningText = "SLIP!",
                onPercentChange = { newPct ->
                    onSandFlowChange(newPct)
                },
                modifier = Modifier.width(96.dp),
                testTag = "sand_flow_slider"
            )
        }
    }
}

/**
 * REMODELED HIGH-PRECISION SPEEDOMETER
 * Features:
 * - Dual dynamic calibration range (0-160 for classic/freight, 0-400 for high-speed bullet)
 * - Amber glowing Speed Limit radial marker
 * - Crisp luminescent tick marks and needle with glow head
 * - Oversized digital speed read-out + Limit readout
 * - Integrated mini-bar for Power kW / Brake Cylinder psi
 */
@Composable
fun RemodeledSpeedometerDial(
    speedKmh: Float,
    speedLimitKmh: Float = 160f,
    powerKw: Float = 0f,
    brakeCylinderPsi: Float = 0f,
    modifier: Modifier = Modifier
) {
    val displaySpeed = abs(speedKmh)
    val animatedSpeed by animateFloatAsState(targetValue = displaySpeed, label = "speed_needle")
    val maxDialSpeed = if (animatedSpeed > 135f || speedLimitKmh > 160f) 400f else 160f

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = size.width / 2f - 3.dp.toPx()

            // Outer Metallic Bezel
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF374151), Color(0xFF1E293B), Color(0xFF0F172A)),
                    center = Offset(cx, cy),
                    radius = r + 3.dp.toPx()
                ),
                radius = r + 3.dp.toPx(),
                center = Offset(cx, cy)
            )
            // Dial Face
            drawCircle(color = GaugeFaceDark, radius = r, center = Offset(cx, cy))

            // Gauge Background Arc
            drawArc(
                color = Color(0xFF1F2937),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(cx - r + 5.dp.toPx(), cy - r + 5.dp.toPx()),
                size = Size((r - 5.dp.toPx()) * 2, (r - 5.dp.toPx()) * 2),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Speed Limit Arc Segment (Amber marker)
            val limitFraction = (speedLimitKmh / maxDialSpeed).coerceIn(0f, 1f)
            drawArc(
                color = TrainGreen,
                startAngle = 135f,
                sweepAngle = limitFraction * 270f,
                useCenter = false,
                topLeft = Offset(cx - r + 5.dp.toPx(), cy - r + 5.dp.toPx()),
                size = Size((r - 5.dp.toPx()) * 2, (r - 5.dp.toPx()) * 2),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Speed Limit Amber Notch on dial perimeter
            val limitAngleDeg = 135f + (limitFraction * 270f)
            val limitRad = Math.toRadians(limitAngleDeg.toDouble()).toFloat()
            drawLine(
                color = Color(0xFFFBBF24),
                start = Offset(cx + cos(limitRad) * (r * 0.65f), cy + sin(limitRad) * (r * 0.65f)),
                end = Offset(cx + cos(limitRad) * (r * 0.95f), cy + sin(limitRad) * (r * 0.95f)),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Ticks around dial
            val numTicks = 8
            for (i in 0..numTicks) {
                val tickFraction = i / numTicks.toFloat()
                val tickAngle = 135f + (tickFraction * 270f)
                val tickRad = Math.toRadians(tickAngle.toDouble()).toFloat()
                val isMajor = (i % 2 == 0)
                val tStart = if (isMajor) r * 0.72f else r * 0.80f
                val tEnd = r * 0.92f

                drawLine(
                    color = if (isMajor) Color(0xFFCBD5E1) else Color(0xFF64748B),
                    start = Offset(cx + cos(tickRad) * tStart, cy + sin(tickRad) * tStart),
                    end = Offset(cx + cos(tickRad) * tEnd, cy + sin(tickRad) * tEnd),
                    strokeWidth = if (isMajor) 1.8.dp.toPx() else 1.0.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Speed Needle
            val speedFraction = (animatedSpeed / maxDialSpeed).coerceIn(0f, 1f)
            val needleAngleDeg = 135f + (speedFraction * 270f)
            val needleRad = Math.toRadians(needleAngleDeg.toDouble()).toFloat()
            val needleLen = r * 0.78f
            val nx = cx + cos(needleRad) * needleLen
            val ny = cy + sin(needleRad) * needleLen

            // Needle glow / line
            drawLine(
                color = if (animatedSpeed > speedLimitKmh) TrainSafetyRed else GaugeNeedleRed,
                start = Offset(cx, cy),
                end = Offset(nx, ny),
                strokeWidth = 2.8.dp.toPx(),
                cap = StrokeCap.Round
            )
            // Center Cap
            drawCircle(color = Color(0xFFE2E8F0), radius = 4.dp.toPx(), center = Offset(cx, cy))
            drawCircle(color = Color(0xFF0F172A), radius = 2.dp.toPx(), center = Offset(cx, cy))
        }

        // Oversized Digital Speed Display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(
                text = "${displaySpeed.roundToInt()}",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
            Text(
                text = "km/h",
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 7.5.sp
            )
            Text(
                text = "LIMIT ${speedLimitKmh.toInt()}",
                color = Color(0xFFFBBF24),
                fontWeight = FontWeight.Black,
                fontSize = 6.5.sp
            )
        }
    }
}

/**
 * THROTTLE ACCELERATION SLIDER (0 IDLE to 8 MAX POWER)
 */
@Composable
fun ThrottleAccelerationLever(
    throttle: Float,
    powerKw: Float = 0f,
    onThrottleChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val notchLevel = (throttle * 8f + 0.05f).toInt().coerceIn(0, 8)
    val animatedThrottle by animateFloatAsState(targetValue = throttle.coerceIn(0f, 1f), label = "throttle_anim")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF282E35), Color(0xFF161A1E), Color(0xFF0F1215))
                )
            )
            .padding(horizontal = 3.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("THROTTLE", color = TrainLightGreen, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
                val statusText = when (notchLevel) {
                    0 -> "0 (IDLE)"
                    1 -> "1 (SLOW)"
                    4 -> "4 (MID)"
                    8 -> "8 (MAX)"
                    else -> "NOTCH $notchLevel"
                }
                val statusColor = when (notchLevel) {
                    0 -> TrainSafetyRed
                    1 -> Color(0xFF38BDF8)
                    4 -> TrainYellowPrimary
                    8 -> Color(0xFF22C55E)
                    else -> Color.White
                }
                Text(text = statusText, color = statusColor, fontWeight = FontWeight.Black, fontSize = 8.5.sp)
            }

            // Interactive Mechanical Throttle Drag Slot
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF101316))
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val h = size.height
                            val fraction = (1f - (down.position.y / h)).coerceIn(0f, 1f)
                            val notch = (fraction * 8f).roundToInt().coerceIn(0, 8)
                            onThrottleChange(notch / 8f)

                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (!change.pressed) break
                                change.consume()
                                val dragFrac = (1f - (change.position.y / h)).coerceIn(0f, 1f)
                                val dragNotch = (dragFrac * 8f).roundToInt().coerceIn(0, 8)
                                onThrottleChange(dragNotch / 8f)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val slotCenterX = w * 0.35f
                    val slotTop = 8.dp.toPx()
                    val slotBottom = h - 8.dp.toPx()
                    val slotSpan = slotBottom - slotTop

                    // Deep recessed slot
                    drawRoundRect(
                        color = Color(0xFF090A0C),
                        topLeft = Offset(slotCenterX - 4.dp.toPx(), slotTop),
                        size = Size(8.dp.toPx(), slotSpan),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )

                    // 9 Detent Notch marks
                    for (i in 0..8) {
                        val notchY = slotBottom - (i / 8f) * slotSpan
                        val isKey = (i == 0 || i == 1 || i == 4 || i == 8)
                        val tickColor = when (i) {
                            0 -> TrainSafetyRed
                            1 -> Color(0xFF38BDF8)
                            4 -> TrainYellowPrimary
                            8 -> Color(0xFF22C55E)
                            else -> Color(0xFF64748B)
                        }

                        drawLine(
                            color = tickColor,
                            start = Offset(slotCenterX + 6.dp.toPx(), notchY),
                            end = Offset(slotCenterX + (if (isKey) 15.dp.toPx() else 8.dp.toPx()), notchY),
                            strokeWidth = if (isKey) 2.2.dp.toPx() else 1.2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Lever Handle
                    val leverHandleY = slotBottom - (animatedThrottle * slotSpan)

                    // Chrome shaft
                    drawLine(
                        color = Color(0xFFCBD5E1),
                        start = Offset(slotCenterX, slotBottom),
                        end = Offset(slotCenterX, leverHandleY),
                        strokeWidth = 4.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Knurled Handle Knob
                    val knobW = 36.dp.toPx()
                    val knobH = 12.dp.toPx()
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF374151), Color(0xFFE2E8F0), Color(0xFF1E293B))
                        ),
                        topLeft = Offset(slotCenterX - knobW / 2f, leverHandleY - knobH / 2f),
                        size = Size(knobW, knobH),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )

                    drawCircle(
                        color = when (notchLevel) {
                            0 -> TrainSafetyRed
                            1 -> Color(0xFF38BDF8)
                            4 -> TrainYellowPrimary
                            8 -> Color(0xFF22C55E)
                            else -> Color(0xFFF59E0B)
                        },
                        radius = 3.dp.toPx(),
                        center = Offset(slotCenterX, leverHandleY)
                    )
                }

                // Text labels on right
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp, top = 2.dp, bottom = 2.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text("8 MAX", color = Color(0xFF22C55E), fontSize = 7.sp, fontWeight = FontWeight.Black)
                    Text("4 MID", color = TrainYellowPrimary, fontSize = 7.sp, fontWeight = FontWeight.Black)
                    Text("1 SLOW", color = Color(0xFF38BDF8), fontSize = 7.sp, fontWeight = FontWeight.Black)
                    Text("0 IDLE", color = TrainSafetyRed, fontSize = 7.sp, fontWeight = FontWeight.Black)
                }
            }

            // Stepper and Idle Quick Controls
            Row(
                modifier = Modifier.fillMaxWidth().height(20.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step DOWN notch
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF262E38))
                        .clickable(enabled = notchLevel > 0) {
                            onThrottleChange(((notchLevel - 1) / 8f).coerceAtLeast(0f))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("-", color = if (notchLevel > 0) Color.White else Color(0xFF475569), fontSize = 12.sp, fontWeight = FontWeight.Black)
                }

                // Quick Idle Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (notchLevel == 0) TrainSafetyRed else Color(0xFF1E242B))
                        .clickable { onThrottleChange(0f) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (notchLevel == 0) "IDLE (0)" else "CUT IDLE",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 7.5.sp
                    )
                }

                // Step UP notch
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF262E38))
                        .clickable(enabled = notchLevel < 8) {
                            onThrottleChange(((notchLevel + 1) / 8f).coerceAtMost(1f))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = if (notchLevel < 8) Color.White else Color(0xFF475569), fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun SubGaugeDial(
    label: String,
    value: Float,
    minVal: Float,
    maxVal: Float,
    isWarning: Boolean,
    unit: String = "",
    modifier: Modifier = Modifier
) {
    val fraction = ((value - minVal) / (maxVal - minVal)).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(targetValue = fraction, label = "subgauge_anim")

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = size.width / 2f - 3.dp.toPx()

            drawCircle(color = Color(0xFF2C3238), radius = r + 2.dp.toPx(), center = Offset(cx, cy))
            drawCircle(
                color = if (isWarning) Color(0xFF3B1515) else GaugeFaceDark,
                radius = r,
                center = Offset(cx, cy)
            )

            val arcColor = if (isWarning) TrainSafetyRed else if (label == "TEMP") TrainLightGreen else TrainBrightCyan
            drawArc(
                color = arcColor,
                startAngle = 135f,
                sweepAngle = animatedFraction * 270f,
                useCenter = false,
                topLeft = Offset(cx - r + 3.dp.toPx(), cy - r + 3.dp.toPx()),
                size = Size((r - 3.dp.toPx()) * 2, (r - 3.dp.toPx()) * 2),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            val needleAngleDeg = 135f + (animatedFraction * 270f)
            val needleRad = Math.toRadians(needleAngleDeg.toDouble()).toFloat()
            val needleLen = r * 0.7f
            val nx = cx + cos(needleRad) * needleLen
            val ny = cy + sin(needleRad) * needleLen

            drawLine(
                color = if (isWarning) TrainSafetyRed else Color.White,
                start = Offset(cx, cy),
                end = Offset(nx, ny),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(cx, cy))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = label,
                color = if (isWarning) TrainSafetyRed else Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 7.sp
            )
            Text(
                text = "${value.toInt()}$unit",
                color = if (isWarning) TrainSafetyRed else Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun SupplyReservoirGaugeDial(
    label: String,
    currentUnits: Int,
    maxUnits: Int = 100,
    isActive: Boolean,
    activeColor: Color,
    icon: String,
    modifier: Modifier = Modifier
) {
    val fraction = (currentUnits.toFloat() / maxUnits.toFloat()).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(targetValue = fraction, label = "reservoir_anim")
    val isLow = currentUnits <= 15

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = size.width / 2f - 3.dp.toPx()

            drawCircle(color = if (isActive) activeColor.copy(alpha = 0.5f) else Color(0xFF2C3238), radius = r + 2.dp.toPx(), center = Offset(cx, cy))
            drawCircle(
                color = if (isLow) Color(0xFF2A1212) else GaugeFaceDark,
                radius = r,
                center = Offset(cx, cy)
            )

            drawArc(
                color = Color(0xFF334155),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(cx - r + 3.dp.toPx(), cy - r + 3.dp.toPx()),
                size = Size((r - 3.dp.toPx()) * 2, (r - 3.dp.toPx()) * 2),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            val arcColor = if (isLow) TrainSafetyRed else activeColor
            drawArc(
                color = arcColor,
                startAngle = 135f,
                sweepAngle = animatedFraction * 270f,
                useCenter = false,
                topLeft = Offset(cx - r + 3.dp.toPx(), cy - r + 3.dp.toPx()),
                size = Size((r - 3.dp.toPx()) * 2, (r - 3.dp.toPx()) * 2),
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            val needleAngleDeg = 135f + (animatedFraction * 270f)
            val needleRad = Math.toRadians(needleAngleDeg.toDouble()).toFloat()
            val needleLen = r * 0.7f
            val nx = cx + cos(needleRad) * needleLen
            val ny = cy + sin(needleRad) * needleLen

            drawLine(
                color = if (isLow) TrainSafetyRed else Color.White,
                start = Offset(cx, cy),
                end = Offset(nx, ny),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(cx, cy))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = "$icon $label",
                color = if (isLow) TrainSafetyRed else if (isActive) activeColor else Color(0xFF94A3B8),
                fontWeight = FontWeight.Black,
                fontSize = 6.sp
            )
            Text(
                text = "$currentUnits%",
                color = if (isLow) TrainSafetyRed else Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 8.sp
            )
        }
    }
}
