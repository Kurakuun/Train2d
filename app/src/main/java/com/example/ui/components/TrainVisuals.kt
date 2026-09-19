package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.data.model.EnvironmentType
import com.example.data.model.RailCar
import com.example.data.model.RailCarType
import com.example.data.model.TrainModel
import com.example.data.model.TrainType
import com.example.physics.Particle
import com.example.physics.ParticleType
import com.example.physics.WeatherType
import com.example.ui.theme.DesertMesa
import com.example.ui.theme.DesertSand
import com.example.ui.theme.DesertSandDark
import com.example.ui.theme.DesertSkyBottom
import com.example.ui.theme.DesertSkyTop
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws dynamic multi-layer parallax background based on contract environment.
 */
fun DrawScope.drawEnvironmentBackground(
    environment: EnvironmentType,
    trainWorldX: Float,
    canvasW: Float,
    gameViewH: Float
) {
    when (environment) {
        EnvironmentType.DESERT_CANYON -> {
            drawDesertCanyonBackground(trainWorldX, canvasW, gameViewH)
        }
        EnvironmentType.ALPINE_PEAKS -> {
            drawAlpinePeaksBackground(trainWorldX, canvasW, gameViewH)
        }
        EnvironmentType.INDUSTRIAL_VALLEY -> {
            drawIndustrialValleyBackground(trainWorldX, canvasW, gameViewH)
        }
        EnvironmentType.REDWOOD_COAST -> {
            drawRedwoodCoastBackground(trainWorldX, canvasW, gameViewH)
        }
        EnvironmentType.ARCTIC_PASS -> {
            drawArcticPassBackground(trainWorldX, canvasW, gameViewH)
        }
    }
}

private fun wrapCoord(rawX: Float, width: Float, buffer: Float = 100f): Float {
    val span = width + buffer * 2f
    val m = ((rawX + buffer) % span + span) % span
    return m - buffer
}

private fun DrawScope.drawDesertCanyonBackground(trainWorldX: Float, canvasW: Float, gameViewH: Float) {
    // 1. Warm Desert Sky Gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF38BDF8), Color(0xFFFDE68A), Color(0xFFF97316)),
            startY = 0f,
            endY = gameViewH
        ),
        size = Size(canvasW, gameViewH)
    )

    // Glowing Desert Sun with Corona Flare
    val sunX = canvasW * 0.80f
    val sunY = gameViewH * 0.20f
    drawCircle(color = Color(0xFFFFFAEE), radius = 28f, center = Offset(sunX, sunY))
    drawCircle(color = Color(0xFFFDE047).copy(alpha = 0.35f), radius = 55f, center = Offset(sunX, sunY))
    drawCircle(color = Color(0xFFFB923C).copy(alpha = 0.18f), radius = 95f, center = Offset(sunX, sunY))

    // Distant Desert Cirrus Clouds
    for (i in 0 until 5) {
        val cloudX = wrapCoord(i * 320f - (trainWorldX * 0.03f), canvasW, 120f)
        val cloudY = 30f + (i % 3) * 20f
        drawCircle(color = Color(0x66FFFFFF), radius = 30f, center = Offset(cloudX, cloudY))
        drawCircle(color = Color(0x66FFFFFF), radius = 40f, center = Offset(cloudX + 25f, cloudY - 4f))
        drawCircle(color = Color(0x66FFFFFF), radius = 26f, center = Offset(cloudX + 52f, cloudY + 2f))
    }

    // Parallax Layer 1: Distant Grand Canyon Mesas & Flat-topped Bluffs
    val mesaPath = Path().apply {
        moveTo(0f, gameViewH)
        var mx = 0f
        while (mx <= canvasW + 40f) {
            val worldMx = mx + (trainWorldX * 0.06f)
            val my = (gameViewH * 0.40f) + sin(worldMx * 0.0018f) * 35f + cos(worldMx * 0.0042f) * 16f
            lineTo(mx, my)
            mx += 30f
        }
        lineTo(canvasW, gameViewH)
        close()
    }
    drawPath(mesaPath, color = Color(0xFFC27142).copy(alpha = 0.85f))

    // Strata rock bands on distant mesas
    for (b in 1..3) {
        val by = gameViewH * (0.44f + b * 0.04f)
        drawLine(
            color = Color(0xFF8C3E20).copy(alpha = 0.45f),
            start = Offset(0f, by),
            end = Offset(canvasW, by),
            strokeWidth = 3f
        )
    }

    // Parallax Layer 2: Midground Red Rock Canyon Formations & Natural Arches
    val midCanyonPath = Path().apply {
        moveTo(0f, gameViewH)
        var cx = 0f
        while (cx <= canvasW + 40f) {
            val worldCx = cx + (trainWorldX * 0.18f)
            val cy = (gameViewH * 0.52f) + sin(worldCx * 0.0035f) * 28f
            lineTo(cx, cy)
            cx += 25f
        }
        lineTo(canvasW, gameViewH)
        close()
    }
    drawPath(midCanyonPath, color = Color(0xFF994422))

    // Parallax Layer 3: Saguaro Cacti & Desert Scrub (Wrapping smoothly)
    for (i in 0 until 10) {
        val cactusScreenX = wrapCoord(i * 220f - (trainWorldX * 0.45f), canvasW, 60f)
        val cactusBaseY = (gameViewH * 0.63f) + sin(i * 1.5f) * 12f
        drawSaguaroCactus(cactusScreenX, cactusBaseY, 0.85f)

        // Desert Creosote Bush
        val bushX = cactusScreenX + 60f
        drawCircle(color = Color(0xFF4D7C0F), radius = 9f, center = Offset(bushX, cactusBaseY + 6f))
        drawCircle(color = Color(0xFF65A30D), radius = 6f, center = Offset(bushX - 5f, cactusBaseY + 3f))
    }
}

private fun DrawScope.drawAlpinePeaksBackground(trainWorldX: Float, canvasW: Float, gameViewH: Float) {
    // 1. Crisp Alpine Sky Gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF1E3A8A), Color(0xFF38BDF8), Color(0xFFBAE6FD)),
            startY = 0f,
            endY = gameViewH
        ),
        size = Size(canvasW, gameViewH)
    )

    // Distant Snow-capped Mountain Peaks (Jagged summits)
    val peakPath = Path().apply {
        moveTo(0f, gameViewH)
        var px = 0f
        while (px <= canvasW + 40f) {
            val worldPx = px + (trainWorldX * 0.05f)
            val py = (gameViewH * 0.30f) + sin(worldPx * 0.0024f) * 60f + cos(worldPx * 0.006f) * 30f
            lineTo(px, py)
            px += 35f
        }
        lineTo(canvasW, gameViewH)
        close()
    }
    drawPath(peakPath, color = Color(0xFFF1F5F9)) // Glacier white peaks

    // Peak shadows
    val shadowPath = Path().apply {
        moveTo(0f, gameViewH)
        var px = 0f
        while (px <= canvasW + 40f) {
            val worldPx = px + (trainWorldX * 0.05f)
            val py = (gameViewH * 0.36f) + sin(worldPx * 0.0024f) * 50f
            lineTo(px, py)
            px += 35f
        }
        lineTo(canvasW, gameViewH)
        close()
    }
    drawPath(shadowPath, color = Color(0xFF94A3B8).copy(alpha = 0.5f))

    // Midground Forest Hills (Dark evergreen ridge)
    val forestPath = Path().apply {
        moveTo(0f, gameViewH)
        var fx = 0f
        while (fx <= canvasW + 40f) {
            val worldFx = fx + (trainWorldX * 0.20f)
            val fy = (gameViewH * 0.50f) + sin(worldFx * 0.004f) * 26f
            lineTo(fx, fy)
            fx += 25f
        }
        lineTo(canvasW, gameViewH)
        close()
    }
    drawPath(forestPath, color = Color(0xFF14532D))

    // Near-ground Alpine Pine Trees & Swiss Chalets
    for (i in 0 until 12) {
        val treeX = wrapCoord(i * 180f - (trainWorldX * 0.45f), canvasW, 50f)
        val treeY = (gameViewH * 0.62f) + sin(i * 2.1f) * 10f
        drawPineTree(treeX, treeY, 0.95f)

        // Occasional Swiss Timber Chalet
        if (i % 4 == 0) {
            val chaletX = treeX + 75f
            val chaletY = treeY - 5f
            // Chalet wooden walls
            drawRect(color = Color(0xFF78350F), topLeft = Offset(chaletX, chaletY - 24f), size = Size(36f, 24f))
            // Snow-covered sloped roof
            val roof = Path().apply {
                moveTo(chaletX - 4f, chaletY - 22f)
                lineTo(chaletX + 18f, chaletY - 36f)
                lineTo(chaletX + 40f, chaletY - 22f)
                close()
            }
            drawPath(roof, color = Color.White)
            // Lit warm window
            drawRect(color = Color(0xFFFDE047), topLeft = Offset(chaletX + 8f, chaletY - 16f), size = Size(7f, 7f))
        }
    }
}

private fun DrawScope.drawIndustrialValleyBackground(trainWorldX: Float, canvasW: Float, gameViewH: Float) {
    // 1. Industrial Dusk Sky Gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF1E293B), Color(0xFF64748B), Color(0xFFEA580C)),
            startY = 0f,
            endY = gameViewH
        ),
        size = Size(canvasW, gameViewH)
    )

    // Distant Steel Factory Skyline (Warehouses, Chimneys, Smoke)
    val indPath = Path().apply {
        moveTo(0f, gameViewH)
        var ix = 0f
        while (ix <= canvasW + 60f) {
            val worldIx = ix + (trainWorldX * 0.08f)
            val iy = (gameViewH * 0.42f) + sin(worldIx * 0.003f) * 18f
            lineTo(ix, iy)
            // Smokestacks
            if ((worldIx.toInt() / 160) % 2 == 0) {
                lineTo(ix, iy - 48f)
                lineTo(ix + 18f, iy - 48f)
                lineTo(ix + 18f, iy)
            }
            ix += 45f
        }
        lineTo(canvasW, gameViewH)
        close()
    }
    drawPath(indPath, color = Color(0xFF0F172A).copy(alpha = 0.90f))

    // Billowing smoke clouds from stacks
    for (i in 0 until 6) {
        val smkX = wrapCoord(i * 260f - (trainWorldX * 0.08f), canvasW, 80f)
        val smkY = gameViewH * 0.32f
        drawCircle(color = Color(0x33CBD5E1), radius = 22f, center = Offset(smkX, smkY))
        drawCircle(color = Color(0x22CBD5E1), radius = 34f, center = Offset(smkX + 15f, smkY - 12f))
    }

    // Midground Steel Truss Bridges & High-Voltage Pylons
    for (i in 0 until 5) {
        val pylonX = wrapCoord(i * 350f - (trainWorldX * 0.35f), canvasW, 60f)
        val pylonY = gameViewH * 0.60f
        drawSteelTrussPillar(pylonX, pylonY)
    }
}

private fun DrawScope.drawRedwoodCoastBackground(trainWorldX: Float, canvasW: Float, gameViewH: Float) {
    // 1. Vibrant Ocean Coast Sky Gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0284C7), Color(0xFF7DD3FC), Color(0xFFF0FDF4)),
            startY = 0f,
            endY = gameViewH
        ),
        size = Size(canvasW, gameViewH)
    )

    // Distant Pacific Ocean Horizon & Waves
    drawRect(
        color = Color(0xFF0369A1),
        topLeft = Offset(0f, gameViewH * 0.44f),
        size = Size(canvasW, gameViewH * 0.16f)
    )
    // Surf line
    drawLine(color = Color(0x99FFFFFF), start = Offset(0f, gameViewH * 0.50f), end = Offset(canvasW, gameViewH * 0.50f), strokeWidth = 2f)

    // Distant Cargo Ship
    val shipX = wrapCoord(250f - (trainWorldX * 0.02f), canvasW, 60f)
    val shipY = gameViewH * 0.45f
    drawRect(color = Color(0xFF1E293B), topLeft = Offset(shipX, shipY), size = Size(38f, 7f))
    drawRect(color = Color(0xFFDC2626), topLeft = Offset(shipX + 10f, shipY - 6f), size = Size(14f, 6f))

    // Towering California Giant Redwoods
    for (i in 0 until 10) {
        val redX = wrapCoord(i * 200f - (trainWorldX * 0.45f), canvasW, 60f)
        val redY = (gameViewH * 0.62f) + sin(i * 1.8f) * 10f
        drawRedwoodTree(redX, redY, 1.1f)
    }
}

private fun DrawScope.drawArcticPassBackground(trainWorldX: Float, canvasW: Float, gameViewH: Float) {
    // 1. Midnight Starfield Sky
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF030712), Color(0xFF0B192C), Color(0xFF1E1B4B)),
            startY = 0f,
            endY = gameViewH
        ),
        size = Size(canvasW, gameViewH)
    )

    // Arctic Crescent Moon
    val moonX = canvasW * 0.82f
    val moonY = gameViewH * 0.18f
    drawCircle(color = Color(0xFFFEF9C3), radius = 20f, center = Offset(moonX, moonY))
    drawCircle(color = Color(0xFF030712), radius = 17f, center = Offset(moonX + 8f, moonY - 4f))

    // Glowing Aurora Borealis Curtains
    for (i in 0 until 3) {
        val auroraPath = Path().apply {
            moveTo(0f, gameViewH * 0.35f)
            var ax = 0f
            while (ax <= canvasW + 40f) {
                val ay = (gameViewH * 0.20f) + sin((ax + trainWorldX * 0.04f + i * 140f) * 0.005f) * 35f
                lineTo(ax, ay)
                ax += 25f
            }
            lineTo(canvasW, gameViewH * 0.44f)
            lineTo(0f, gameViewH * 0.44f)
            close()
        }
        val auroraColor = if (i == 1) Color(0x4406B6D4) else Color(0x4410B981)
        drawPath(auroraPath, color = auroraColor)
    }

    // Distant Glaciers & Icebergs
    val icePath = Path().apply {
        moveTo(0f, gameViewH)
        var ix = 0f
        while (ix <= canvasW + 40f) {
            val worldIx = ix + (trainWorldX * 0.06f)
            val iy = (gameViewH * 0.45f) + sin(worldIx * 0.0028f) * 35f
            lineTo(ix, iy)
            ix += 30f
        }
        lineTo(canvasW, gameViewH)
        close()
    }
    drawPath(icePath, color = Color(0xFFCBD5E1))

    // Snow-covered Pine Trees & Frost Posts
    for (i in 0 until 10) {
        val treeX = wrapCoord(i * 190f - (trainWorldX * 0.45f), canvasW, 50f)
        val treeY = (gameViewH * 0.62f)
        drawPineTree(treeX, treeY, 0.9f)
    }
}

private fun DrawScope.drawSaguaroCactus(x: Float, y: Float, scale: Float) {
    val s = scale
    // Main Trunk
    drawRoundRect(
        color = Color(0xFF2E6F40),
        topLeft = Offset(x, y - 55f * s),
        size = Size(10f * s, 60f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )
    // Left Arm
    val leftArm = Path().apply {
        moveTo(x, y - 35f * s)
        lineTo(x - 14f * s, y - 35f * s)
        lineTo(x - 14f * s, y - 50f * s)
    }
    drawPath(leftArm, color = Color(0xFF2E6F40), style = Stroke(width = 6f * s))
    // Right Arm
    val rightArm = Path().apply {
        moveTo(x + 10f * s, y - 25f * s)
        lineTo(x + 24f * s, y - 25f * s)
        lineTo(x + 24f * s, y - 42f * s)
    }
    drawPath(rightArm, color = Color(0xFF2E6F40), style = Stroke(width = 6f * s))
}

private fun DrawScope.drawPineTree(x: Float, y: Float, scale: Float) {
    val s = scale
    // Trunk
    drawRect(color = Color(0xFF3E2723), topLeft = Offset(x + 6f * s, y - 15f * s), size = Size(6f * s, 18f * s))
    // Foliage layers
    for (i in 0 until 3) {
        val py = y - (25f + i * 18f) * s
        val w = (32f - i * 8f) * s
        val treePath = Path().apply {
            moveTo(x + 9f * s, py - 18f * s)
            lineTo(x + 9f * s + w / 2, py)
            lineTo(x + 9f * s - w / 2, py)
            close()
        }
        drawPath(treePath, color = Color(0xFF1E3A2F))
    }
}

private fun DrawScope.drawRedwoodTree(x: Float, y: Float, scale: Float) {
    val s = scale
    // Massive Cinnamon Trunk
    drawRoundRect(
        color = Color(0xFF8D4925),
        topLeft = Offset(x, y - 90f * s),
        size = Size(14f * s, 95f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )
    // Towering Foliage Canopy
    for (i in 0 until 4) {
        val cy = y - (45f + i * 18f) * s
        val cw = (40f - i * 6f) * s
        drawOval(
            color = Color(0xFF14532D),
            topLeft = Offset(x + 7f * s - cw / 2, cy),
            size = Size(cw, 22f * s)
        )
    }
}

private fun DrawScope.drawSteelTrussPillar(x: Float, y: Float) {
    // Steel Gantry Frame
    drawLine(color = Color(0xFF334155), start = Offset(x, y), end = Offset(x, y - 65f), strokeWidth = 5f)
    drawLine(color = Color(0xFF334155), start = Offset(x + 35f, y), end = Offset(x + 35f, y - 65f), strokeWidth = 5f)
    drawLine(color = Color(0xFF334155), start = Offset(x, y - 65f), end = Offset(x + 35f, y - 65f), strokeWidth = 5f)
    // Cross bracing
    drawLine(color = Color(0xFF475569), start = Offset(x, y), end = Offset(x + 35f, y - 65f), strokeWidth = 2.5f)
    drawLine(color = Color(0xFF475569), start = Offset(x, y - 65f), end = Offset(x + 35f, y), strokeWidth = 2.5f)
}

/**
 * Rich multi-layered environmental terrain geological theme.
 */
data class EnvironmentTerrainTheme(
    val surfaceSoil: Color,
    val ballastMain: Color,
    val ballastDark: Color,
    val strataTop: Color,
    val strataMid: Color,
    val strataDeep: Color,
    val vegetationTuft: Color,
    val rockColor: Color
)

fun getEnvironmentTerrainTheme(environment: EnvironmentType): EnvironmentTerrainTheme {
    return when (environment) {
        EnvironmentType.DESERT_CANYON -> EnvironmentTerrainTheme(
            surfaceSoil = Color(0xFFD4A373),
            ballastMain = Color(0xFFB56A3C),
            ballastDark = Color(0xFF7C3E20),
            strataTop = Color(0xFFD97706),
            strataMid = Color(0xFFB45309),
            strataDeep = Color(0xFF78350F),
            vegetationTuft = Color(0xFF84CC16),
            rockColor = Color(0xFF9A3412)
        )
        EnvironmentType.ALPINE_PEAKS -> EnvironmentTerrainTheme(
            surfaceSoil = Color(0xFF64748B),
            ballastMain = Color(0xFF475569),
            ballastDark = Color(0xFF1E293B),
            strataTop = Color(0xFF334155),
            strataMid = Color(0xFF1E293B),
            strataDeep = Color(0xFF0F172A),
            vegetationTuft = Color(0xFF16A34A),
            rockColor = Color(0xFF94A3B8)
        )
        EnvironmentType.INDUSTRIAL_VALLEY -> EnvironmentTerrainTheme(
            surfaceSoil = Color(0xFF475569),
            ballastMain = Color(0xFF334155),
            ballastDark = Color(0xFF181E24),
            strataTop = Color(0xFF27272A),
            strataMid = Color(0xFF18181B),
            strataDeep = Color(0xFF09090B),
            vegetationTuft = Color(0xFF65A30D),
            rockColor = Color(0xFF52525B)
        )
        EnvironmentType.REDWOOD_COAST -> EnvironmentTerrainTheme(
            surfaceSoil = Color(0xFF3D5A40),
            ballastMain = Color(0xFF5D4037),
            ballastDark = Color(0xFF3E2723),
            strataTop = Color(0xFF4E342E),
            strataMid = Color(0xFF2E1B15),
            strataDeep = Color(0xFF1B0E0A),
            vegetationTuft = Color(0xFF15803D),
            rockColor = Color(0xFF78716C)
        )
        EnvironmentType.ARCTIC_PASS -> EnvironmentTerrainTheme(
            surfaceSoil = Color(0xFFF1F5F9),
            ballastMain = Color(0xFFCBD5E1),
            ballastDark = Color(0xFF64748B),
            strataTop = Color(0xFF94A3B8),
            strataMid = Color(0xFF475569),
            strataDeep = Color(0xFF1E293B),
            vegetationTuft = Color(0xFF7DD3FC),
            rockColor = Color(0xFFE2E8F0)
        )
    }
}

/**
 * Returns environment-specific ground and ballast colors.
 */
fun getEnvironmentColors(environment: EnvironmentType): Pair<Color, Color> {
    val theme = getEnvironmentTerrainTheme(environment)
    return theme.surfaceSoil to theme.ballastDark
}

/**
 * Draws Railroad Track (Gravel Ballast Bed, Wooden Sleepers/Ties, Steel Base and Shiny Polished Railhead).
 */
fun DrawScope.drawTrackSegment(
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    environment: EnvironmentType = EnvironmentType.DESERT_CANYON
) {
    val (ballastLight, ballastDark) = getEnvironmentColors(environment)

    // 1. Gravel Ballast Bed
    drawLine(
        color = ballastDark,
        start = Offset(startX, startY + 12f),
        end = Offset(endX, endY + 12f),
        strokeWidth = 22f
    )
    drawLine(
        color = ballastLight,
        start = Offset(startX, startY + 10f),
        end = Offset(endX, endY + 10f),
        strokeWidth = 14f
    )

    // 2. Wooden Sleepers / Ties (Crossbars)
    val length = kotlin.math.hypot(endX - startX, endY - startY)
    val steps = (length / 18f).toInt()
    val dx = (endX - startX) / (steps.coerceAtLeast(1))
    val dy = (endY - startY) / (steps.coerceAtLeast(1))
    val angle = kotlin.math.atan2(endY - startY, endX - startX)

    for (i in 0..steps) {
        val tx = startX + dx * i
        val ty = startY + dy * i
        val tieAngleRad = angle + (Math.PI / 2.0).toFloat()
        val tieHalfLen = 13f

        drawLine(
            color = Color(0xFF382314),
            start = Offset(tx - cos(tieAngleRad) * tieHalfLen, ty - sin(tieAngleRad) * tieHalfLen + 5f),
            end = Offset(tx + cos(tieAngleRad) * tieHalfLen, ty + sin(tieAngleRad) * tieHalfLen + 5f),
            strokeWidth = 5.0f
        )
    }

    // 3. Steel Rail Base and Polished Top Railhead (Exactly at startY..endY)
    drawLine(
        color = Color(0xFF3E444B),
        start = Offset(startX, startY + 2.5f),
        end = Offset(endX, endY + 2.5f),
        strokeWidth = 4.5f
    )
    drawLine(
        color = Color(0xFFE2E8F0),
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = 2.5f
    )
}

/**
 * Draws high-fidelity 2D locomotive side profile with mechanical details.
 * Anchor: (midBogieX, wheelBottomY) aligns precisely on the track!
 */
fun DrawScope.drawLocomotiveDetailed(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 360f,
    heightPx: Float = 140f,
    variant: String = "V1_VIRGIN"
) {
    val scale = widthPx / 360f

    // Dynamically look up active livery variant colors if available
    val activeLivery = train.liveryVariants.find { it.id == variant }
    val activeBodyColor = activeLivery?.let { Color(it.primaryColor) } ?: bodyColor
    val activeRoofColor = activeLivery?.let { Color(it.secondaryColor) } ?: Color(train.defaultRoofColor)
    val activeStripeColor = activeLivery?.let { Color(it.accentColor) } ?: stripeColor

    when {
        train.id == "ge_ac4400cw" -> {
            drawGeAc4400cwLocomotive(train, activeBodyColor, activeRoofColor, activeStripeColor, wheelAngleRad, scale, variant)
        }
        train.id == "emd_sd70ace" -> {
            drawEmdSd70aceLocomotive(train, activeBodyColor, activeRoofColor, activeStripeColor, wheelAngleRad, scale, variant)
        }
        train.id == "cd_pendolino_class_680" -> {
            drawCdPendolinoClass680(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, variant)
        }
        train.id == "cd_vectron_comfortjet" -> {
            drawAccurateSiemensVectron(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, "COMFORTJET")
        }
        train.id == "nightjet_vectron_obb_cd" -> {
            drawAccurateSiemensVectron(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, "NIGHTJET")
        }
        train.id == "cd_siemens_smartron" -> {
            drawAccurateSiemensVectron(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, "DEMO")
        }
        train.id == "cd_vectron_dual_mode_248" -> {
            drawAccurateSiemensVectron(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, "DUAL_MODE")
        }
        train.id == "cd_db_shark_link" -> {
            drawCdRegioShark844(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "cd_db_eurocity_186" -> {
            drawCdTraxxLocomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, isCargo388 = false)
        }
        train.id == "cd_alstom_traxx_388" -> {
            drawCdTraxxLocomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, isCargo388 = true)
        }
        train.id == "cd_regiopanter_640" -> {
            drawCdPanterEmu(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, isInterPanter = false)
        }
        train.id == "cd_interpanter_660" -> {
            drawCdPanterEmu(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, isInterPanter = true)
        }
        train.id == "cd_gorilla_class_350" -> {
            drawCdGorilaClass350(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "cd_pershing_class_163" -> {
            drawCdPershingAndEso(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, isEso = false)
        }
        train.id == "cd_eso_class_362" -> {
            drawCdPershingAndEso(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, isEso = true)
        }
        train.id == "cd_bardotka_class_749" -> {
            drawCdBardotkaClass749(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "cd_brejlovec_class_754" -> {
            drawCdBrejlovecClass754(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, variant)
        }
        train.id == "cd_alstom_coradia_stream" -> {
            drawCdAlstomCoradiaStream(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "cd_effishunter_742" || train.id.contains("742") -> {
            drawAccurateEffiShunter742(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "tgv_sud_est" -> {
            drawTgvSudEstLocomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "shinkansen_0_series" -> {
            drawShinkansen0SeriesLocomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "db_class_103" -> {
            drawAccurateDbClass103(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "prr_gg1" -> {
            drawAccuratePrrGg1(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "soviet_vl85" -> {
            drawSovietVl85Locomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "br_class_55_deltic" -> {
            drawBrClass55DelticLocomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, variant)
        }
        train.id == "up_big_boy_4014" -> {
            drawUpBigBoy4014Detailed(activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "nyc_dreyfuss_hudson" -> {
            drawNycDreyfussHudsonDetailed(activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "cp_royal_hudson" -> {
            drawCpRoyalHudsonDetailed(activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "pacific_462" -> {
            drawPacific462Detailed(activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "burlington_f7" -> {
            drawBurlingtonF7Locomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "alco_pa1" -> {
            drawAlcoPa1Locomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, variant)
        }
        train.id == "tow_diesel_heavy_rescue" -> {
            drawBritishClass66HeavyRescue(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "tow_electric_dual_rescue" -> {
            drawBritishClass92ElectricRescue(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "secret_hyper_steam" -> {
            drawHyperSteamLocomotive(activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "secret_virgin_pendolino_390" -> {
            drawVirginPendolinoLocomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, variant)
        }
        train.id == "tow_steam_lner_1472" -> {
            drawLnerScotsmanLocomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "secret_shinkansen_e2" -> {
            drawShinkansenE2Locomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, variant)
        }
        train.id == "emd_f40ph_via" || train.id == "emd_f40ph_metra" || train.id == "emd_f40ph" -> {
            drawAccurateF40PHLocomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, variant)
        }
        train.id.startsWith("metra_") -> {
            drawMetraDetailedLocomotive(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale, variant)
        }
        train.id == "emd_gp9_highhood" -> {
            drawAccurateGp9HighHood(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "emd_gp9_normal" || train.id == "emd_gp9" -> {
            drawAccurateGp9Normal(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "siemens_vectron_red" || train.id == "siemens_vectron_black" || train.id == "siemens_vectron_demo" || train.id == "siemens_vectron" -> {
            drawAccurateSiemensVectron(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "amtrak_acela_bullet" || train.id.contains("acela") -> {
            drawAccurateAcelaExpress(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.id == "siemens_american_flyer" || train.id.contains("american_flyer") -> {
            drawAccurateAmericanFlyer(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.type == TrainType.STEAM -> {
            drawSteamLocomotive(activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        train.type == TrainType.ELECTRIC_MAGLEV -> {
            drawElectricLocomotiveWithWheels(train, activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
        else -> {
            drawDieselLocomotive(activeBodyColor, activeStripeColor, wheelAngleRad, scale)
        }
    }
}

private fun DrawScope.drawF40PHLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String = ""
) {
    drawAccurateF40PHLocomotive(train, bodyColor, stripeColor, wheelAngleRad, s, variant)
}

private fun DrawScope.drawGp9HighHoodLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Underframe & Tank
    drawRoundRect(color = Color(0xFF1A1A1A), topLeft = Offset(10f * s, 82f * s), size = Size(340f * s, 18f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = Color(0xFF111111), topLeft = Offset(110f * s, 92f * s), size = Size(140f * s, 20f * s), cornerRadius = CornerRadius(4f * s, 4f * s))

    // Rear Long Hood
    drawRoundRect(color = bodyColor, topLeft = Offset(20f * s, 28f * s), size = Size(210f * s, 56f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Operator Cab in middle-right
    drawRoundRect(color = bodyColor, topLeft = Offset(230f * s, 16f * s), size = Size(52f * s, 68f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = Color(train.defaultRoofColor), topLeft = Offset(228f * s, 10f * s), size = Size(56f * s, 8f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // HIGH NOSE (Front Short Hood is TALL, level with cab windshield)
    drawRoundRect(color = bodyColor, topLeft = Offset(282f * s, 24f * s), size = Size(64f * s, 60f * s), cornerRadius = CornerRadius(3f * s, 3f * s))

    // Numberboards on High Hood Front "2758"
    drawRoundRect(color = Color(0xFFFFFFFF), topLeft = Offset(336f * s, 28f * s), size = Size(10f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // White NS Horsehead Emblem on Cab/Long Hood
    drawLine(color = Color(0xFFFFFFFF), start = Offset(120f * s, 48f * s), end = Offset(150f * s, 48f * s), strokeWidth = 5f * s)
    drawLine(color = Color(0xFFFFFFFF), start = Offset(135f * s, 38f * s), end = Offset(135f * s, 58f * s), strokeWidth = 4f * s)

    // Cab Windows
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(240f * s, 24f * s), size = Size(20f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Side walkway handrails (Yellow on NS)
    drawLine(color = Color(0xFFFACC15), start = Offset(10f * s, 60f * s), end = Offset(345f * s, 60f * s), strokeWidth = 2f * s)
    for (i in 0 until 7) {
        val rx = (20f + i * 50f) * s
        drawLine(color = Color(0xFFFACC15), start = Offset(rx, 60f * s), end = Offset(rx, 82f * s), strokeWidth = 1.8f * s)
    }

    // Couplers
    drawRect(color = Color(0xFF333333), topLeft = Offset(0f * s, 82f * s), size = Size(10f * s, 8f * s))
    drawRect(color = Color(0xFF333333), topLeft = Offset(348f * s, 82f * s), size = Size(12f * s, 8f * s))

    drawBogieTruck(65f * s, 100f * s, wheelAngleRad, s)
    drawBogieTruck(290f * s, 100f * s, wheelAngleRad, s)
}

private fun DrawScope.drawGp9NormalLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Underframe & Tank
    drawRoundRect(color = Color(0xFF1A1A1A), topLeft = Offset(10f * s, 82f * s), size = Size(340f * s, 18f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = Color(0xFF111111), topLeft = Offset(110f * s, 92f * s), size = Size(140f * s, 20f * s), cornerRadius = CornerRadius(4f * s, 4f * s))

    // Rear Long Hood (Green)
    drawRoundRect(color = bodyColor, topLeft = Offset(20f * s, 28f * s), size = Size(230f * s, 56f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Operator Cab (Right side)
    drawRoundRect(color = bodyColor, topLeft = Offset(250f * s, 18f * s), size = Size(55f * s, 66f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = Color(train.defaultRoofColor), topLeft = Offset(246f * s, 12f * s), size = Size(62f * s, 8f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Low Chopped Nose Hood (Rebuild GP9-RM #1751)
    drawRoundRect(color = bodyColor, topLeft = Offset(305f * s, 42f * s), size = Size(40f * s, 42f * s), cornerRadius = CornerRadius(3f * s, 3f * s))

    // Halifax Southwestern / CN White Chevron Stripe
    val chevron = Path().apply {
        moveTo(80f * s, 84f * s)
        lineTo(115f * s, 32f * s)
        lineTo(125f * s, 32f * s)
        lineTo(90f * s, 84f * s)
        close()
    }
    drawPath(chevron, color = Color(0xFFFFFFFF))

    // Cab Windows
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(262f * s, 26f * s), size = Size(22f * s, 18f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Side Handrails
    drawLine(color = Color(0xFFFFFFFF), start = Offset(10f * s, 58f * s), end = Offset(345f * s, 58f * s), strokeWidth = 2f * s)
    for (i in 0 until 8) {
        val rx = (15f + i * 46f) * s
        drawLine(color = Color(0xFFFFFFFF), start = Offset(rx, 58f * s), end = Offset(rx, 82f * s), strokeWidth = 1.8f * s)
    }

    // Couplers
    drawRect(color = Color(0xFF333333), topLeft = Offset(0f * s, 82f * s), size = Size(10f * s, 8f * s))
    drawRect(color = Color(0xFF333333), topLeft = Offset(348f * s, 82f * s), size = Size(12f * s, 8f * s))

    drawBogieTruck(65f * s, 100f * s, wheelAngleRad, s)
    drawBogieTruck(290f * s, 100f * s, wheelAngleRad, s)
}

private fun DrawScope.drawEffiShunterLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Underframe & Fuel Tank
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(10f * s, 82f * s), size = Size(340f * s, 18f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(120f * s, 92f * s), size = Size(120f * s, 18f * s), cornerRadius = CornerRadius(3f * s, 3f * s))

    // CENTER CAB LAYOUT (High central operator cab)
    drawRoundRect(color = bodyColor, topLeft = Offset(140f * s, 14f * s), size = Size(76f * s, 70f * s), cornerRadius = CornerRadius(4f * s, 4f * s))
    drawRoundRect(color = Color(train.defaultRoofColor), topLeft = Offset(136f * s, 8f * s), size = Size(84f * s, 8f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Left Low Hood (Rear)
    drawRoundRect(color = bodyColor, topLeft = Offset(25f * s, 40f * s), size = Size(115f * s, 44f * s), cornerRadius = CornerRadius(3f * s, 3f * s))

    // Right Low Hood (Front)
    drawRoundRect(color = bodyColor, topLeft = Offset(216f * s, 40f * s), size = Size(120f * s, 44f * s), cornerRadius = CornerRadius(3f * s, 3f * s))

    // ČD Blue and White body bands
    drawRect(color = Color(0xFFFFFFFF), topLeft = Offset(25f * s, 68f * s), size = Size(311f * s, 5f * s))

    // Center Cab Large Panoramic Windows
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(150f * s, 20f * s), size = Size(26f * s, 22f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(182f * s, 20f * s), size = Size(26f * s, 22f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // End walkway railings
    drawLine(color = Color(0xFFFFFFFF), start = Offset(10f * s, 58f * s), end = Offset(345f * s, 58f * s), strokeWidth = 2f * s)

    // European Buffer Beams
    drawCircle(color = Color(0xFF475569), radius = 5f * s, center = Offset(6f * s, 88f * s))
    drawCircle(color = Color(0xFF475569), radius = 5f * s, center = Offset(354f * s, 88f * s))

    drawBogieTruck(65f * s, 100f * s, wheelAngleRad, s)
    drawBogieTruck(290f * s, 100f * s, wheelAngleRad, s)
}

private fun DrawScope.drawSiemensVectronLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Underframe & Lower Transformer Pods
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(10f * s, 82f * s), size = Size(340f * s, 18f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(110f * s, 90f * s), size = Size(140f * s, 20f * s), cornerRadius = CornerRadius(4f * s, 4f * s))

    // Modern European Box Body with Curved Aerodynamic Cab Ends
    val vectronBody = Path().apply {
        moveTo(20f * s, 84f * s)
        lineTo(10f * s, 55f * s)
        cubicTo(10f * s, 35f * s, 25f * s, 20f * s, 45f * s, 20f * s)
        lineTo(315f * s, 20f * s)
        cubicTo(335f * s, 20f * s, 350f * s, 35f * s, 350f * s, 55f * s)
        lineTo(340f * s, 84f * s)
        close()
    }
    drawPath(vectronBody, color = bodyColor)

    // Roof Line & Ventilation Pods
    drawRoundRect(color = Color(train.defaultRoofColor), topLeft = Offset(40f * s, 14f * s), size = Size(280f * s, 8f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Quad/Dual Pantographs on Roof
    drawRoofPantograph(70f * s, 14f * s, s)
    drawRoofPantograph(270f * s, 14f * s, s)

    // Front & Rear Aerodynamic Windshields
    val frontWindow = Path().apply {
        moveTo(320f * s, 26f * s)
        lineTo(344f * s, 48f * s)
        lineTo(326f * s, 48f * s)
        lineTo(306f * s, 26f * s)
        close()
    }
    drawPath(frontWindow, color = Color(0xFF38BDF8))

    val rearWindow = Path().apply {
        moveTo(40f * s, 26f * s)
        lineTo(16f * s, 48f * s)
        lineTo(34f * s, 48f * s)
        lineTo(54f * s, 26f * s)
        close()
    }
    drawPath(rearWindow, color = Color(0xFF38BDF8))

    // Characteristic Angled Side Windows
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(280f * s, 28f * s), size = Size(18f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(62f * s, 28f * s), size = Size(18f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Livery Specific Emblems (DB Cargo logo / MRCE Yellow / Vectron White)
    if (train.id == "siemens_vectron_red") {
        // DB White Logo on Side
        drawRoundRect(color = Color(0xFFFFFFFF), topLeft = Offset(160f * s, 42f * s), size = Size(40f * s, 18f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
        drawRoundRect(color = Color(0xFFDC2626), topLeft = Offset(164f * s, 45f * s), size = Size(32f * s, 12f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    } else if (train.id == "siemens_vectron_black") {
        // MRCE Yellow warning front panel
        drawRoundRect(color = Color(0xFFEAB308), topLeft = Offset(330f * s, 54f * s), size = Size(16f * s, 26f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    } else {
        // Siemens Blue Stripe & "Vectron" accent
        drawRect(color = Color(0xFF0284C7), topLeft = Offset(20f * s, 68f * s), size = Size(320f * s, 5f * s))
    }

    // Modern LED 3-Point Light Cluster
    drawCircle(color = Color(0xFFFEF08A), radius = 3.5f * s, center = Offset(344f * s, 62f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 3.5f * s, center = Offset(335f * s, 72f * s))

    // European Buffers
    drawCircle(color = Color(0xFF475569), radius = 5f * s, center = Offset(6f * s, 86f * s))
    drawCircle(color = Color(0xFF475569), radius = 5f * s, center = Offset(354f * s, 86f * s))

    drawBogieTruck(65f * s, 100f * s, wheelAngleRad, s)
    drawBogieTruck(290f * s, 100f * s, wheelAngleRad, s)
}

fun DrawScope.drawRoofPantograph(cx: Float, cy: Float, s: Float) {
    // Red Isolators
    drawCircle(color = Color(0xFFEF4444), radius = 3f * s, center = Offset(cx - 15f * s, cy))
    drawCircle(color = Color(0xFFEF4444), radius = 3f * s, center = Offset(cx + 15f * s, cy))

    // Diamond / Single-arm pantograph linkage
    val panto = Path().apply {
        moveTo(cx - 15f * s, cy)
        lineTo(cx - 5f * s, cy - 14f * s)
        lineTo(cx + 15f * s, cy - 26f * s)
        lineTo(cx + 25f * s, cy - 26f * s)
    }
    drawPath(panto, color = Color(0xFF94A3B8), style = Stroke(width = 2.2f * s))
    // Copper collector bar
    drawLine(color = Color(0xFFF97316), start = Offset(cx + 5f * s, cy - 26f * s), end = Offset(cx + 35f * s, cy - 26f * s), strokeWidth = 2.5f * s)
}

private fun DrawScope.drawHighSpeedBulletLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Aerodynamic Slanted Wedge Bullet Nose (Acela / American Flyer / TGV)
    val body = Path().apply {
        moveTo(10f * s, 86f * s)
        lineTo(10f * s, 26f * s)
        lineTo(230f * s, 22f * s)
        cubicTo(290f * s, 22f * s, 345f * s, 55f * s, 355f * s, 86f * s)
        lineTo(10f * s, 86f * s)
        close()
    }
    drawPath(body, color = bodyColor)

    // Aerodynamic Roof Ribbon
    drawRoundRect(color = Color(train.defaultRoofColor), topLeft = Offset(10f * s, 16f * s), size = Size(260f * s, 10f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // High Speed Pantograph
    drawRoofPantograph(80f * s, 16f * s, s)

    // Streamlined Speed Stripe
    val speedStripe = Path().apply {
        moveTo(10f * s, 70f * s)
        lineTo(250f * s, 70f * s)
        lineTo(335f * s, 82f * s)
    }
    drawPath(speedStripe, color = stripeColor, style = Stroke(width = 4f * s))

    // Aerodynamic Streamlined Cockpit Visor
    val visor = Path().apply {
        moveTo(260f * s, 32f * s)
        lineTo(325f * s, 54f * s)
        lineTo(290f * s, 54f * s)
        lineTo(250f * s, 40f * s)
        close()
    }
    drawPath(visor, color = Color(0xFF38BDF8))

    // Passenger / Engine Skirt Covers
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(15f * s, 86f * s), size = Size(330f * s, 12f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    drawBogieTruck(65f * s, 100f * s, wheelAngleRad, s)
    drawBogieTruck(285f * s, 100f * s, wheelAngleRad, s)
}

/**
 * Draws the mysterious locked secret train silhouette with center glowing question mark.
 */
fun DrawScope.drawSecretTrainSilhouette(
    widthPx: Float = 360f,
    heightPx: Float = 140f
) {
    val s = widthPx / 360f

    // Outer mysterious dark cyan/purple energy halo
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x5500F0FF), Color(0x227C3AED), Color(0x00000000)),
            center = Offset(180f * s, 60f * s),
            radius = 160f * s
        ),
        radius = 160f * s,
        center = Offset(180f * s, 60f * s)
    )

    // Aerodynamic Bullet Silhouette Body (Pitch Black with cyan glowing rim)
    val body = Path().apply {
        moveTo(10f * s, 86f * s)
        lineTo(10f * s, 26f * s)
        lineTo(220f * s, 20f * s)
        cubicTo(270f * s, 20f * s, 340f * s, 48f * s, 355f * s, 86f * s)
        lineTo(10f * s, 86f * s)
        close()
    }
    // Glowing neon contour
    drawPath(body, color = Color(0xFF00F0FF), style = Stroke(width = 3.5f * s))
    // Deep black stealth fill
    drawPath(body, color = Color(0xFF0B0D13))

    // Mysterious Glowing Streamline Streak
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0x0000F0FF), Color(0xFF00F0FF), Color(0x0000F0FF))
        ),
        start = Offset(40f * s, 54f * s),
        end = Offset(300f * s, 54f * s),
        strokeWidth = 2.5f * s
    )

    // Sleek mystery question mark in center
    val qCenterX = 180f * s
    val qCenterY = 52f * s

    // Question Mark Glowing Disc
    drawCircle(
        color = Color(0x3300F0FF),
        radius = 24f * s,
        center = Offset(qCenterX, qCenterY)
    )
    drawCircle(
        color = Color(0xFF00F0FF),
        radius = 24f * s,
        center = Offset(qCenterX, qCenterY),
        style = Stroke(width = 2.2f * s)
    )

    // Question mark hook
    val qPath = Path().apply {
        moveTo(qCenterX - 8f * s, qCenterY - 10f * s)
        cubicTo(qCenterX - 8f * s, qCenterY - 18f * s, qCenterX + 8f * s, qCenterY - 18f * s, qCenterX + 8f * s, qCenterY - 10f * s)
        cubicTo(qCenterX + 8f * s, qCenterY - 4f * s, qCenterX, qCenterY - 2f * s, qCenterX, qCenterY + 4f * s)
    }
    drawPath(qPath, color = Color(0xFFFFEB3B), style = Stroke(width = 4f * s, cap = androidx.compose.ui.graphics.StrokeCap.Round))
    drawCircle(color = Color(0xFFFFEB3B), radius = 3f * s, center = Offset(qCenterX, qCenterY + 12f * s))

    // Black wheels / Bogies
    drawBogieTruck(65f * s, 100f * s, 0f, s)
    drawBogieTruck(285f * s, 100f * s, 0f, s)
}

/**
 * Draws the British Rail Class 390 / Super Voyager High-Speed Bullet Train with 3 accurate design variants:
 * - ( V1 ) Virgin Trains West Coast: Platinum silver body, Virgin Red roof & sweeping ribbon, yellow nose shield.
 * - ( V2 ) CrossCountry (Photos 1 & 2): Dark maroon cab, vermilion roof, white body, signature hot pink / magenta doors, crosscountry X branding.
 * - ( V3 ) Avanti West Coast (Photo 3): Deep petrol teal cab, red roof, pearl white body, red triangle Avanti West Coast emblems.
 * Underframe: Ultra-accurate high-speed chassis with chamfered equipment pod, disc brakes, and drop coupler (Photo 4).
 */
fun DrawScope.drawVirginPendolinoLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String = "V1_VIRGIN"
) {
    when (variant) {
        "V2_CROSSCOUNTRY" -> {
            drawCrossCountryPendolinoLocomotive(train, bodyColor, stripeColor, wheelAngleRad, s)
        }
        "V3_AVANTI" -> {
            drawAvantiPendolinoLocomotive(train, bodyColor, stripeColor, wheelAngleRad, s)
        }
        else -> {
            drawVirginClassicPendolinoLocomotive(train, bodyColor, stripeColor, wheelAngleRad, s)
        }
    }
}

/**
 * Variant 1: ( V1 ) Virgin Trains West Coast (Classic Silver & Virgin Red Ribbon)
 */
private fun DrawScope.drawVirginClassicPendolinoLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    val silverBody = Color(0xFFE2E8F0)
    val virginRed = Color(0xFFDC2626)
    val warningYellow = Color(0xFFFACC15)

    // 1. Aerodynamic Bullet Nose Body (Class 390 High Speed Wedge)
    val pendolinoBody = Path().apply {
        moveTo(10f * s, 86f * s)
        lineTo(10f * s, 26f * s)
        lineTo(215f * s, 22f * s)
        cubicTo(270f * s, 22f * s, 340f * s, 48f * s, 355f * s, 86f * s)
        lineTo(10f * s, 86f * s)
        close()
    }
    drawPath(pendolinoBody, color = silverBody)

    // 2. Bold Virgin Red Roof & Fairing
    val redRoof = Path().apply {
        moveTo(10f * s, 26f * s)
        lineTo(10f * s, 16f * s)
        lineTo(215f * s, 16f * s)
        cubicTo(260f * s, 16f * s, 310f * s, 32f * s, 325f * s, 46f * s)
        lineTo(290f * s, 46f * s)
        lineTo(210f * s, 26f * s)
        close()
    }
    drawPath(redRoof, color = virginRed)

    // 3. Sweeping Virgin Red Bodyside Ribbon
    val redRibbon = Path().apply {
        moveTo(10f * s, 54f * s)
        lineTo(235f * s, 54f * s)
        cubicTo(275f * s, 54f * s, 320f * s, 68f * s, 352f * s, 86f * s)
        lineTo(335f * s, 86f * s)
        cubicTo(305f * s, 74f * s, 265f * s, 64f * s, 225f * s, 64f * s)
        lineTo(10f * s, 64f * s)
        close()
    }
    drawPath(redRibbon, color = virginRed)

    // 4. British Rail Yellow Front Warning Panel
    val yellowNose = Path().apply {
        moveTo(330f * s, 86f * s)
        lineTo(355f * s, 86f * s)
        cubicTo(350f * s, 72f * s, 335f * s, 62f * s, 315f * s, 55f * s)
        lineTo(305f * s, 66f * s)
        close()
    }
    drawPath(yellowNose, color = warningYellow)

    // 5. Panoramic Tinted Cockpit Windscreen
    val cockpitWindscreen = Path().apply {
        moveTo(245f * s, 30f * s)
        lineTo(300f * s, 48f * s)
        lineTo(282f * s, 54f * s)
        lineTo(235f * s, 38f * s)
        close()
    }
    drawPath(cockpitWindscreen, color = Color(0xFF0F172A))
    drawPath(cockpitWindscreen, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

    // 6. Roof Pantograph
    drawRoofPantograph(75f * s, 16f * s, s)

    // 7. Flush Passenger Windows
    for (i in 0 until 4) {
        val wx = (30f + i * 36f) * s
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(wx, 34f * s),
            size = Size(24f * s, 16f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
        drawRoundRect(
            color = Color(0x99FEF08A),
            topLeft = Offset(wx + 2f * s, 36f * s),
            size = Size(20f * s, 8f * s),
            cornerRadius = CornerRadius(1f * s, 1f * s)
        )
    }

    // 8. Boarding Door with Status Indicator
    drawRect(color = Color(0x33000000), topLeft = Offset(180f * s, 28f * s), size = Size(16f * s, 42f * s), style = Stroke(width = 1.2f * s))
    drawCircle(color = Color(0xFFEF4444), radius = 1.8f * s, center = Offset(188f * s, 31f * s))

    // 9. Modern High-Intensity Headlights Cluster
    drawCircle(color = Color(0xFFFFFAEE), radius = 4f * s, center = Offset(348f * s, 76f * s))
    drawCircle(color = Color(0xFFFFFAEE), radius = 4f * s, center = Offset(338f * s, 82f * s))

    // 10. Accurate High-Speed Chassis & Disc Brakes (Photo 4)
    drawModernHighSpeedChassis(
        frontBogieCenterX = 285f * s,
        rearBogieCenterX = 65f * s,
        trayStartX = 100f * s,
        trayEndX = 248f * s,
        wheelAngleRad = wheelAngleRad,
        s = s,
        hasFrontCoupler = true
    )
}

/**
 * Variant 2: ( V2 ) CrossCountry (Accurate to User Reference Photos 1 & 2)
 * Features dark maroon geometric cab front, vermilion red roof line, crisp white body,
 * signature hot pink / vivid magenta passenger boarding doors, and crosscountry X livery.
 */
private fun DrawScope.drawCrossCountryPendolinoLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    val whiteBody = Color(0xFFF8FAFC)
    val maroonCab = Color(0xFF2B1422)
    val maroonGraphic = Color(0xFF4C1D3A)
    val vermilionRoof = Color(0xFFC53030)
    val windowBandCharcoal = Color(0xFF334155)
    val magentaDoor = Color(0xFFD92662)
    val warningYellow = Color(0xFFFACC15)

    // 1. Crisp White Aerodynamic Body Base
    val pendolinoBody = Path().apply {
        moveTo(10f * s, 86f * s)
        lineTo(10f * s, 26f * s)
        lineTo(215f * s, 22f * s)
        cubicTo(270f * s, 22f * s, 340f * s, 48f * s, 355f * s, 86f * s)
        lineTo(10f * s, 86f * s)
        close()
    }
    drawPath(pendolinoBody, color = whiteBody)

    // 2. Vermilion Red Roof & Fairing
    val redRoof = Path().apply {
        moveTo(10f * s, 26f * s)
        lineTo(10f * s, 16f * s)
        lineTo(215f * s, 16f * s)
        cubicTo(260f * s, 16f * s, 310f * s, 32f * s, 325f * s, 46f * s)
        lineTo(290f * s, 46f * s)
        lineTo(210f * s, 26f * s)
        close()
    }
    drawPath(redRoof, color = vermilionRoof)

    // Roof HVAC Climate Pods & Resistor Grilles
    drawRoundRect(color = Color(0xFF94A3B8), topLeft = Offset(40f * s, 12f * s), size = Size(50f * s, 6f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF94A3B8), topLeft = Offset(110f * s, 12f * s), size = Size(50f * s, 6f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // 3. Dark Charcoal Upper Window Band
    drawRect(color = windowBandCharcoal, topLeft = Offset(10f * s, 30f * s), size = Size(205f * s, 24f * s))

    // 4. Dark Maroon Aerodynamic Cab Front (Photo 1 & 2)
    val cabFront = Path().apply {
        moveTo(215f * s, 22f * s)
        lineTo(215f * s, 86f * s)
        lineTo(355f * s, 86f * s)
        cubicTo(340f * s, 48f * s, 270f * s, 22f * s, 215f * s, 22f * s)
        close()
    }
    drawPath(cabFront, color = maroonCab)

    // Geometric Maroon Chevron Graphic Trailing into White Bodyside (Photo 1)
    val chevronGraphic = Path().apply {
        moveTo(215f * s, 32f * s)
        lineTo(170f * s, 54f * s)
        lineTo(215f * s, 76f * s)
        close()
    }
    drawPath(chevronGraphic, color = maroonGraphic)

    // 5. Warning Yellow Eyebrow / Nose Visor (Photo 1)
    val yellowEyebrow = Path().apply {
        moveTo(285f * s, 44f * s)
        lineTo(345f * s, 65f * s)
        lineTo(340f * s, 72f * s)
        lineTo(275f * s, 48f * s)
        close()
    }
    drawPath(yellowEyebrow, color = warningYellow)

    // 6. Raked Driver Windscreen
    val windscreen = Path().apply {
        moveTo(250f * s, 30f * s)
        lineTo(298f * s, 46f * s)
        lineTo(285f * s, 52f * s)
        lineTo(240f * s, 38f * s)
        close()
    }
    drawPath(windscreen, color = Color(0xFF0F172A))
    drawPath(windscreen, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

    // Side Triangular Driver Cab Window
    val sideCabWindow = Path().apply {
        moveTo(235f * s, 38f * s)
        lineTo(252f * s, 44f * s)
        lineTo(235f * s, 50f * s)
        close()
    }
    drawPath(sideCabWindow, color = Color(0xFF0F172A))

    // 7. Flush Passenger Windows
    for (i in 0 until 4) {
        val wx = (30f + i * 36f) * s
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(wx, 34f * s),
            size = Size(24f * s, 16f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
        drawRoundRect(
            color = Color(0x99FEF08A),
            topLeft = Offset(wx + 2f * s, 36f * s),
            size = Size(20f * s, 8f * s),
            cornerRadius = CornerRadius(1f * s, 1f * s)
        )
    }

    // 8. SIGNATURE MAGENTA / HOT PINK BOARDING DOOR (Photo 1 & 2 Spec)
    drawRoundRect(
        color = magentaDoor,
        topLeft = Offset(178f * s, 28f * s),
        size = Size(20f * s, 44f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    drawRect(color = Color(0xFF1E2228), topLeft = Offset(182f * s, 34f * s), size = Size(12f * s, 16f * s))
    drawCircle(color = Color(0xFFFACC15), radius = 1.5f * s, center = Offset(182f * s, 56f * s)) // Yellow door handle

    // 9. CrossCountry Branding & Chevron Logo on White Bodyside
    // 'crosscountry' text bar
    drawRoundRect(
        color = Color(0xFF475569),
        topLeft = Offset(45f * s, 62f * s),
        size = Size(65f * s, 5f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )
    // Red/Magenta 'X' Chevron Logo
    drawLine(color = magentaDoor, start = Offset(118f * s, 60f * s), end = Offset(128f * s, 70f * s), strokeWidth = 2.5f * s)
    drawLine(color = magentaDoor, start = Offset(128f * s, 60f * s), end = Offset(118f * s, 70f * s), strokeWidth = 2.5f * s)

    // Unit Road Number on Skirt (221136 from Photo 2)
    drawRoundRect(color = Color(0xFFE2E8F0), topLeft = Offset(320f * s, 80f * s), size = Size(18f * s, 4f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // 10. Headlights & Marker Lights
    drawCircle(color = Color(0xFFFFFAEE), radius = 4f * s, center = Offset(348f * s, 76f * s))
    drawCircle(color = Color(0xFFFFFAEE), radius = 4f * s, center = Offset(338f * s, 82f * s))

    // 11. Accurate High-Speed Chassis & Disc Brakes (Photo 4)
    drawModernHighSpeedChassis(
        frontBogieCenterX = 285f * s,
        rearBogieCenterX = 65f * s,
        trayStartX = 100f * s,
        trayEndX = 248f * s,
        wheelAngleRad = wheelAngleRad,
        s = s,
        hasFrontCoupler = true
    )
}

/**
 * Variant 3: ( V3 ) Avanti West Coast (Accurate to User Reference Photo 3)
 * Features deep petrol slate / dark teal blue cab front, coral red triangle Avanti chevron logo,
 * vermilion red roof, dark charcoal window strip, and pearl white bodyside.
 */
private fun DrawScope.drawAvantiPendolinoLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    val pearlWhite = Color(0xFFF1F5F9)
    val petrolTealCab = Color(0xFF164E63)
    val vermilionRoof = Color(0xFFC53030)
    val windowBand = Color(0xFF272E38)
    val coralRedTriangle = Color(0xFFDC2626)
    val warningYellow = Color(0xFFFACC15)

    // 1. Pearl White Aerodynamic Body Base
    val pendolinoBody = Path().apply {
        moveTo(10f * s, 86f * s)
        lineTo(10f * s, 26f * s)
        lineTo(215f * s, 22f * s)
        cubicTo(270f * s, 22f * s, 340f * s, 48f * s, 355f * s, 86f * s)
        lineTo(10f * s, 86f * s)
        close()
    }
    drawPath(pendolinoBody, color = pearlWhite)

    // 2. Vermilion Red Roof & Fairing
    val redRoof = Path().apply {
        moveTo(10f * s, 26f * s)
        lineTo(10f * s, 16f * s)
        lineTo(215f * s, 16f * s)
        cubicTo(260f * s, 16f * s, 310f * s, 32f * s, 325f * s, 46f * s)
        lineTo(290f * s, 46f * s)
        lineTo(210f * s, 26f * s)
        close()
    }
    drawPath(redRoof, color = vermilionRoof)

    // Roof Resistors & Pods
    drawRoundRect(color = Color(0xFF94A3B8), topLeft = Offset(40f * s, 12f * s), size = Size(60f * s, 6f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // 3. Dark Charcoal Upper Window Band
    drawRect(color = windowBand, topLeft = Offset(10f * s, 30f * s), size = Size(205f * s, 24f * s))

    // 4. Deep Petrol Teal Cab Front (Photo 3)
    val cabFront = Path().apply {
        moveTo(215f * s, 22f * s)
        lineTo(215f * s, 86f * s)
        lineTo(355f * s, 86f * s)
        cubicTo(340f * s, 48f * s, 270f * s, 22f * s, 215f * s, 22f * s)
        close()
    }
    drawPath(cabFront, color = petrolTealCab)

    // 5. Warning Yellow Front Brow (Photo 3)
    val yellowEyebrow = Path().apply {
        moveTo(290f * s, 44f * s)
        lineTo(348f * s, 66f * s)
        lineTo(342f * s, 72f * s)
        lineTo(280f * s, 48f * s)
        close()
    }
    drawPath(yellowEyebrow, color = warningYellow)

    // 6. Raked Driver Windscreen
    val windscreen = Path().apply {
        moveTo(250f * s, 30f * s)
        lineTo(298f * s, 46f * s)
        lineTo(285f * s, 52f * s)
        lineTo(240f * s, 38f * s)
        close()
    }
    drawPath(windscreen, color = Color(0xFF0F172A))
    drawPath(windscreen, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

    // 7. Official Avanti West Coast Coral Red Triangle Logo on Cab Side (Photo 3)
    val triangleLogo = Path().apply {
        moveTo(230f * s, 58f * s)
        lineTo(246f * s, 74f * s)
        lineTo(214f * s, 74f * s)
        close()
    }
    drawPath(triangleLogo, color = coralRedTriangle)

    // 8. Flush Passenger Windows
    for (i in 0 until 4) {
        val wx = (30f + i * 36f) * s
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(wx, 34f * s),
            size = Size(24f * s, 16f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
        drawRoundRect(
            color = Color(0x99FEF08A),
            topLeft = Offset(wx + 2f * s, 36f * s),
            size = Size(20f * s, 8f * s),
            cornerRadius = CornerRadius(1f * s, 1f * s)
        )
    }

    // 9. Pearl White Boarding Door with Yellow Safety Border
    drawRoundRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(178f * s, 28f * s),
        size = Size(20f * s, 44f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    drawRect(color = Color(0xFF1E2228), topLeft = Offset(182f * s, 34f * s), size = Size(12f * s, 16f * s))
    drawCircle(color = Color(0xFFFACC15), radius = 1.5f * s, center = Offset(182f * s, 56f * s))

    // 10. AVANTI WEST COAST Bodyside Lettering Bar
    drawRoundRect(
        color = Color(0xFF334155),
        topLeft = Offset(45f * s, 62f * s),
        size = Size(80f * s, 5f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )

    // Unit Road Number on Skirt (221115 from Photo 3)
    drawRoundRect(color = Color(0xFFE2E8F0), topLeft = Offset(320f * s, 80f * s), size = Size(18f * s, 4f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // 11. Headlights & Marker Lights
    drawCircle(color = Color(0xFFFFFAEE), radius = 4f * s, center = Offset(348f * s, 76f * s))
    drawCircle(color = Color(0xFFFFFAEE), radius = 4f * s, center = Offset(338f * s, 82f * s))

    // 12. Accurate High-Speed Chassis & Disc Brakes (Photo 4)
    drawModernHighSpeedChassis(
        frontBogieCenterX = 285f * s,
        rearBogieCenterX = 65f * s,
        trayStartX = 100f * s,
        trayEndX = 248f * s,
        wheelAngleRad = wheelAngleRad,
        s = s,
        hasFrontCoupler = true
    )
}

/**
 * Draws the LNER 1472 Flying Scotsman 4-6-2 Pacific Heavy Tow Steam Locomotive.
 * Features Apple Green boiler casing, black smokebox, smoke deflectors, brass fittings,
 * authentic Walschaerts valve gear linkage, 3 large spoked driving wheels, and coal tender.
 */
fun DrawScope.drawLnerScotsmanLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    val appleGreen = Color(0xFF1B6E34)
    val blackIron = Color(0xFF181B1F)
    val goldLining = Color(0xFFF59E0B)
    val brassGold = Color(0xFFEAB308)

    // === 1. TENDER (Left Side, hitched to cab) ===
    drawRoundRect(
        color = appleGreen,
        topLeft = Offset(10f * s, 36f * s),
        size = Size(85f * s, 48f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Tender Gold Lining
    drawRect(color = goldLining, topLeft = Offset(14f * s, 40f * s), size = Size(77f * s, 40f * s), style = Stroke(width = 1.5f * s))
    // L N E R lettering bar
    drawRoundRect(color = Color(0xFF111315), topLeft = Offset(28f * s, 50f * s), size = Size(50f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawLine(color = goldLining, start = Offset(32f * s, 58f * s), end = Offset(74f * s, 58f * s), strokeWidth = 2f * s)
    // Coal Mound in Tender
    val coal = Path().apply {
        moveTo(12f * s, 36f * s)
        lineTo(30f * s, 22f * s)
        lineTo(65f * s, 20f * s)
        lineTo(92f * s, 36f * s)
        close()
    }
    drawPath(coal, color = Color(0xFF0F1113))

    // Authentic LNER 8-Wheel / 6-Wheel Corridor Tender Chassis (Rigid frame with external hornblocks & leaf springs)
    drawRoundRect(
        color = blackIron,
        topLeft = Offset(8f * s, 84f * s),
        size = Size(89f * s, 10f * s),
        cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
    )
    // Tender Hornblock Guides & Semi-Elliptic Leaf Springs
    for (ti in 0 until 3) {
        val twx = (24f + ti * 26f) * s
        // Inverted leaf spring
        drawRoundRect(
            color = Color(0xFF475569),
            topLeft = Offset(twx - 9f * s, 90f * s),
            size = Size(18f * s, 4.5f * s),
            cornerRadius = CornerRadius(1f * s, 1f * s)
        )
        // Mansell / spoked tender wheel (Wheel bottom at 122f)
        drawRailWheel(twx, 108f * s, wheelAngleRad, s)
    }

    // Authentic British Buffer Beam & Round Buffers on Tender Rear
    drawBritishUnderframeBufferGear(widthPx = 260f, s = s)

    // Hitch link
    drawLine(color = Color(0xFF333333), start = Offset(95f * s, 68f * s), end = Offset(105f * s, 68f * s), strokeWidth = 5f * s)

    // === 2. LOCOMOTIVE CAB ===
    drawRoundRect(
        color = appleGreen,
        topLeft = Offset(105f * s, 20f * s),
        size = Size(55f * s, 64f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    // Cab Roof (Black with curved brow)
    drawRoundRect(
        color = blackIron,
        topLeft = Offset(102f * s, 14f * s),
        size = Size(60f * s, 8f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Cab Arched Side Window
    drawRoundRect(
        color = Color(0xFF38BDF8),
        topLeft = Offset(118f * s, 28f * s),
        size = Size(18f * s, 20f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // === 3. BOILER (Apple Green Cylinder) ===
    drawRoundRect(
        color = appleGreen,
        topLeft = Offset(155f * s, 26f * s),
        size = Size(140f * s, 52f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Golden Boiler Bands
    for (i in 0 until 4) {
        val bx = (180f + i * 32f) * s
        drawLine(color = goldLining, start = Offset(bx, 26f * s), end = Offset(bx, 78f * s), strokeWidth = 2f * s)
    }

    // === 4. SMOKEBOX (Matte Black Front Cylinder) ===
    drawRoundRect(
        color = blackIron,
        topLeft = Offset(290f * s, 26f * s),
        size = Size(50f * s, 52f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )
    // Smokebox Front Door & Dart Hinge
    drawCircle(color = Color(0xFF22262B), radius = 22f * s, center = Offset(338f * s, 52f * s))
    drawCircle(color = goldLining, radius = 3.5f * s, center = Offset(338f * s, 52f * s))
    // Smokebox Number Plate "1472"
    drawRoundRect(color = Color(0xFFB91C1C), topLeft = Offset(326f * s, 48f * s), size = Size(18f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // === 5. FITTINGS: Chimney, Steam Dome, Brass Safety Valves ===
    // Chimney Stack
    drawRect(color = blackIron, topLeft = Offset(308f * s, 10f * s), size = Size(14f * s, 18f * s))
    drawOval(color = blackIron, topLeft = Offset(305f * s, 8f * s), size = Size(20f * s, 6f * s))
    // Steam Dome
    drawRoundRect(color = appleGreen, topLeft = Offset(245f * s, 14f * s), size = Size(18f * s, 14f * s), cornerRadius = CornerRadius(6f * s, 6f * s))
    // Brass Safety Valve Bonnet
    drawRoundRect(color = brassGold, topLeft = Offset(175f * s, 16f * s), size = Size(14f * s, 12f * s), cornerRadius = CornerRadius(3f * s, 3f * s))

    // === 6. LNER SMOKE DEFLECTORS (Blinkers on front) ===
    drawRoundRect(
        color = appleGreen,
        topLeft = Offset(315f * s, 18f * s),
        size = Size(24f * s, 56f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    drawRect(color = goldLining, topLeft = Offset(315f * s, 18f * s), size = Size(24f * s, 56f * s), style = Stroke(width = 1.5f * s))

    // === 7. RUNNING BOARD & CYLINDERS ===
    drawRect(color = blackIron, topLeft = Offset(155f * s, 76f * s), size = Size(190f * s, 6f * s))
    // Front Steam Cylinder
    drawRoundRect(color = blackIron, topLeft = Offset(305f * s, 72f * s), size = Size(36f * s, 22f * s), cornerRadius = CornerRadius(4f * s, 4f * s))
    drawCircle(color = brassGold, radius = 3f * s, center = Offset(338f * s, 83f * s))

    // === 8. PACIFIC WHEEL ARRANGEMENT (4-6-2) ===
    // 3 Large Driving Wheels (Radius 24f, center at y = 98f * s => wheel bottom at y = 122f * s!)
    val dRadius = 24f * s
    drawSteamDriverWheel(185f * s, 98f * s, dRadius, wheelAngleRad, s, appleGreen)
    drawSteamDriverWheel(235f * s, 98f * s, dRadius, wheelAngleRad, s, appleGreen)
    drawSteamDriverWheel(285f * s, 98f * s, dRadius, wheelAngleRad, s, appleGreen)

    // Side Connecting Rod linking the 3 driving wheel crankpins
    val crankR = dRadius * 0.45f
    val c1x = 185f * s + cos(wheelAngleRad) * crankR
    val c1y = 98f * s + sin(wheelAngleRad) * crankR
    val c3x = 285f * s + cos(wheelAngleRad) * crankR
    val c3y = 98f * s + sin(wheelAngleRad) * crankR
    drawLine(color = Color(0xFFD4D4D8), start = Offset(c1x, c1y), end = Offset(c3x, c3y), strokeWidth = 4f * s)
    drawLine(color = Color(0xFFD4D4D8), start = Offset(c3x, c3y), end = Offset(325f * s, 83f * s), strokeWidth = 3.5f * s)

    // Front 2-Axle Gresley Swing-Link Pilot Bogie (Accurate British 4-wheel pilot truck)
    drawRoundRect(
        color = blackIron,
        topLeft = Offset(310f * s, 105f * s),
        size = Size(42f * s, 5.5f * s),
        cornerRadius = CornerRadius(1.2f * s, 1.2f * s)
    )
    drawRailWheel(320f * s, 108f * s, wheelAngleRad, s)
    drawRailWheel(342f * s, 108f * s, wheelAngleRad, s)

    // Rear Cartazzi Radial Trailing Axle with Outside Frame & Leaf Spring (bottom at 122f)
    drawRoundRect(
        color = blackIron,
        topLeft = Offset(124f * s, 104f * s),
        size = Size(22f * s, 6f * s),
        cornerRadius = CornerRadius(1.2f * s, 1.2f * s)
    )
    drawRoundRect(
        color = Color(0xFF475569),
        topLeft = Offset(126f * s, 101f * s),
        size = Size(18f * s, 3f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )
    drawRailWheel(135f * s, 108f * s, wheelAngleRad, s)
}

/**
 * Draws the high-speed Japanese JR East Shinkansen E2-1000 Series Bullet Train ('Hayate').
 * Features authentic duckbill aerodynamic nose, pure pearl white upper body, vivid azalea pink waist stripe,
 * midnight indigo blue skirt, raked cockpit canopy, LED headlamps, and Tohoku rainbow apple emblem.
 */
fun DrawScope.drawShinkansenE2Locomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String = "V1_E2_HAYATE"
) {
    val isAsama = variant == "V2_E2_ASAMA"
    val isEvergreen = variant == "V3_E2_TOHOKU_EVERGREEN"

    val baseUpperBody = when {
        isAsama -> Color(0xFFF8FAFC)
        isEvergreen -> Color(0xFFF1F5F9)
        else -> bodyColor // V1 Hayate Pearl White
    }

    val skirtColor = when {
        isAsama -> Color(0xFF991B1B) // Crimson Red (Nagano 1998 Olympics)
        isEvergreen -> Color(0xFF14532D) // Alpine Forest Evergreen
        else -> Color(0xFF192A56) // Midnight Indigo Blue
    }

    val ribbonStripeColor = when {
        isAsama -> Color(0xFFEAB308) // Bright Golden Yellow
        isEvergreen -> Color(0xFF8B5CF6) // Royal Lavender / Violet
        else -> stripeColor // Azalea Pink / Magenta (0xFFE6007E)
    }

    val roofSlate = Color(0xFFE2E8F0)

    // 1. Aerodynamic Bullet Nose Body Path (E2-1000 Duckbill Profile)
    val shinkansenBody = Path().apply {
        moveTo(10f * s, 86f * s)
        lineTo(10f * s, 26f * s)
        lineTo(210f * s, 22f * s)
        cubicTo(265f * s, 22f * s, 335f * s, 50f * s, 355f * s, 86f * s)
        lineTo(10f * s, 86f * s)
        close()
    }
    drawPath(shinkansenBody, color = baseUpperBody)

    // 2. Aerodynamic Roof Contours
    drawRoundRect(
        color = roofSlate,
        topLeft = Offset(10f * s, 16f * s),
        size = Size(205f * s, 8f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // 2b. High-Speed Aerofoil Pantograph Shield & Red Arm (Rear roof at x=45f)
    drawRoundRect(
        color = skirtColor,
        topLeft = Offset(35f * s, 10f * s),
        size = Size(40f * s, 8f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    // Red Single-Arm Pantograph
    drawLine(color = Color(0xFFDC2626), start = Offset(45f * s, 10f * s), end = Offset(58f * s, 2f * s), strokeWidth = 2.2f * s)
    drawLine(color = Color(0xFFDC2626), start = Offset(58f * s, 2f * s), end = Offset(68f * s, 8f * s), strokeWidth = 2.2f * s)
    // Current Collector Contact Shoe
    drawLine(color = Color(0xFFF1F5F9), start = Offset(52f * s, 1f * s), end = Offset(64f * s, 1f * s), strokeWidth = 3f * s)

    // 3. Lower Skirt (Indigo / Crimson / Evergreen)
    val skirtPath = Path().apply {
        moveTo(10f * s, 86f * s)
        lineTo(10f * s, 68f * s)
        lineTo(230f * s, 68f * s)
        cubicTo(280f * s, 68f * s, 335f * s, 76f * s, 355f * s, 86f * s)
        lineTo(10f * s, 86f * s)
        close()
    }
    drawPath(skirtPath, color = skirtColor)

    // 4. Vivid Waist Ribbon Stripe (Azalea Pink / Gold / Violet)
    val stripePath = Path().apply {
        moveTo(10f * s, 64f * s)
        lineTo(230f * s, 64f * s)
        cubicTo(280f * s, 64f * s, 330f * s, 72f * s, 352f * s, 82f * s)
    }
    drawPath(stripePath, color = ribbonStripeColor, style = Stroke(width = 4.5f * s))

    // 5. Streamlined Cockpit Windshield (Raked dark tinted canopy)
    val windshield = Path().apply {
        moveTo(250f * s, 32f * s)
        lineTo(315f * s, 54f * s)
        lineTo(285f * s, 54f * s)
        lineTo(245f * s, 42f * s)
        close()
    }
    drawPath(windshield, color = Color(0xFF0F172A))
    // Windshield reflection
    drawLine(
        color = Color(0x9938BDF8),
        start = Offset(255f * s, 35f * s),
        end = Offset(295f * s, 50f * s),
        strokeWidth = 2.5f * s
    )

    // 6. High-Intensity Front Twin Headlamps & Nose Marker
    drawCircle(color = Color(0xFFFEF08A), radius = 3.5f * s, center = Offset(348f * s, 80f * s))
    drawCircle(color = Color(0xFFFFFFFF), radius = 2f * s, center = Offset(348f * s, 80f * s))
    drawRoundRect(
        color = Color(0xFF334155),
        topLeft = Offset(340f * s, 77f * s),
        size = Size(10f * s, 6f * s),
        cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
    )

    // 7. Flush Tinted Passenger Windows
    for (i in 0 until 4) {
        val wx = (30f + i * 40f) * s
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(wx, 34f * s),
            size = Size(25f * s, 18f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
        // Subtle warm interior curtain & seat light
        drawRoundRect(
            color = Color(0x88FEF08A),
            topLeft = Offset(wx + 2f * s, 36f * s),
            size = Size(21f * s, 10f * s),
            cornerRadius = CornerRadius(1f * s, 1f * s)
        )
    }

    // 8. Passenger Boarding Door & Handle
    drawRect(
        color = Color(0x33000000),
        topLeft = Offset(195f * s, 28f * s),
        size = Size(18f * s, 40f * s),
        style = Stroke(width = 1.2f * s)
    )
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(198f * s, 34f * s),
        size = Size(12f * s, 14f * s),
        cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
    )
    // Red door status indicator lamp
    drawCircle(color = Color(0xFFEF4444), radius = 1.8f * s, center = Offset(204f * s, 30f * s))

    // 9. Livery Decal Logo (Rainbow Apple for Hayate, Gold Emblem for Asama, Laurel for Evergreen)
    val appleX = 182f * s
    val appleY = 42f * s
    if (isAsama) {
        // Nagano 1998 Olympic Flower Petal Emblem
        drawCircle(color = Color(0xFFEAB308), radius = 5.5f * s, center = Offset(appleX, appleY))
        drawCircle(color = Color(0xFFDC2626), radius = 3.5f * s, center = Offset(appleX, appleY))
    } else if (isEvergreen) {
        // Tohoku Eco Express Mountain Leaf Crest
        drawCircle(color = Color(0xFF22C55E), radius = 5.5f * s, center = Offset(appleX, appleY))
        drawCircle(color = Color(0xFF8B5CF6), radius = 3f * s, center = Offset(appleX, appleY))
    } else {
        // Iconic Tohoku Shinkansen "Rainbow Apple" Logo
        drawCircle(color = Color(0xFFDC2626), radius = 5.5f * s, center = Offset(appleX, appleY))
        drawLine(color = Color(0xFFFACC15), start = Offset(appleX - 4f * s, appleY - 1f * s), end = Offset(appleX + 4f * s, appleY - 1f * s), strokeWidth = 1.2f * s)
        drawLine(color = Color(0xFF22C55E), start = Offset(appleX - 4f * s, appleY + 1f * s), end = Offset(appleX + 4f * s, appleY + 1f * s), strokeWidth = 1.2f * s)
        drawLine(color = Color(0xFF3B82F6), start = Offset(appleX - 3f * s, appleY + 3f * s), end = Offset(appleX + 3f * s, appleY + 3f * s), strokeWidth = 1.2f * s)
        drawCircle(color = Color(0xFF16A34A), radius = 1.5f * s, center = Offset(appleX + 2f * s, appleY - 6f * s))
    }

    // 10. Low-Noise High-Speed Bogies with side skirts (Wheel bottom at y = 122f * s)
    drawModernHighSpeedChassis(
        frontBogieCenterX = 285f * s,
        rearBogieCenterX = 65f * s,
        trayStartX = 105f * s,
        trayEndX = 245f * s,
        wheelAngleRad = wheelAngleRad,
        s = s,
        hasFrontCoupler = false
    )

    // Aerodynamic Bogie Skirt Overhangs
    drawRoundRect(
        color = skirtColor,
        topLeft = Offset(30f * s, 84f * s),
        size = Size(70f * s, 14f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    drawRoundRect(
        color = skirtColor,
        topLeft = Offset(250f * s, 84f * s),
        size = Size(70f * s, 14f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
}

private fun DrawScope.drawDieselLocomotive(
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Underframe & Fuel Tank
    drawRoundRect(
        color = Color(0xFF22262B),
        topLeft = Offset(10f * s, 82f * s),
        size = Size(340f * s, 18f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Center belly fuel tank
    drawRoundRect(
        color = Color(0xFF181B1F),
        topLeft = Offset(110f * s, 92f * s),
        size = Size(140f * s, 20f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )

    // Main Long Hood Body
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(20f * s, 28f * s),
        size = Size(240f * s, 56f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Shading on hood top edge
    drawRect(
        color = Color(0x33000000),
        topLeft = Offset(20f * s, 28f * s),
        size = Size(240f * s, 8f * s)
    )

    // Engine Compartment Access Doors
    for (i in 0 until 14) {
        val xDoor = (30f + i * 15f) * s
        drawLine(
            color = Color(0x55000000),
            start = Offset(xDoor, 38f * s),
            end = Offset(xDoor, 80f * s),
            strokeWidth = 1.5f * s
        )
        drawCircle(
            color = Color(0xFF333333),
            radius = 1.2f * s,
            center = Offset(xDoor + 4f * s, 58f * s)
        )
    }

    // Roof Radiator & Dynamic Brake Grills
    drawRoundRect(
        color = Color(0xFF202327),
        topLeft = Offset(25f * s, 14f * s),
        size = Size(230f * s, 14f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    for (i in 0 until 5) {
        drawOval(
            color = Color(0xFF111417),
            topLeft = Offset((35f + i * 42f) * s, 16f * s),
            size = Size(32f * s, 10f * s)
        )
    }
    // Exhaust stack
    drawRect(
        color = Color(0xFF15181C),
        topLeft = Offset(130f * s, 6f * s),
        size = Size(18f * s, 10f * s)
    )

    // Operator Cab (Right side)
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(250f * s, 18f * s),
        size = Size(78f * s, 66f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )
    // Cab roof overhang
    drawRoundRect(
        color = Color(0xFF25292E),
        topLeft = Offset(246f * s, 12f * s),
        size = Size(86f * s, 8f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Cab Windows
    drawRoundRect(
        color = Color(0xFF4A88A9),
        topLeft = Offset(262f * s, 24f * s),
        size = Size(26f * s, 18f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    drawRoundRect(
        color = Color(0xFF4A88A9),
        topLeft = Offset(294f * s, 24f * s),
        size = Size(26f * s, 18f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    drawLine(
        color = Color(0x88FFFFFF),
        start = Offset(264f * s, 26f * s),
        end = Offset(276f * s, 40f * s),
        strokeWidth = 2f * s
    )

    // Short front nose hood
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(324f * s, 38f * s),
        size = Size(22f * s, 46f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )

    // Stripe Line
    drawRect(
        color = stripeColor,
        topLeft = Offset(10f * s, 78f * s),
        size = Size(338f * s, 5f * s)
    )

    // Handrails
    drawLine(
        color = Color(0xFFE5C030),
        start = Offset(10f * s, 55f * s),
        end = Offset(345f * s, 55f * s),
        strokeWidth = 2.2f * s
    )
    for (i in 0 until 8) {
        val rx = (15f + i * 46f) * s
        drawLine(
            color = Color(0xFFE5C030),
            start = Offset(rx, 55f * s),
            end = Offset(rx, 82f * s),
            strokeWidth = 2f * s
        )
    }

    // Couplers
    drawRect(color = Color(0xFF333333), topLeft = Offset(0f * s, 82f * s), size = Size(10f * s, 8f * s))
    drawRect(color = Color(0xFF333333), topLeft = Offset(348f * s, 82f * s), size = Size(12f * s, 8f * s))

    // Wheel Bogies: wheel bottom at y = 122f * s
    drawBogieTruck(65f * s, 100f * s, wheelAngleRad, s)
    drawBogieTruck(290f * s, 100f * s, wheelAngleRad, s)
}

private fun DrawScope.drawSteamLocomotive(
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Steam Boiler Cylinder
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(30f * s, 26f * s),
        size = Size(220f * s, 58f * s),
        cornerRadius = CornerRadius(20f * s, 20f * s)
    )
    // Smokebox at front
    drawRoundRect(
        color = Color(0xFF1E2124),
        topLeft = Offset(240f * s, 26f * s),
        size = Size(50f * s, 58f * s),
        cornerRadius = CornerRadius(8f * s, 8f * s)
    )
    // Smokestack & Steam Dome
    drawRect(color = Color(0xFF181A1C), topLeft = Offset(260f * s, 6f * s), size = Size(16f * s, 20f * s))
    drawOval(color = stripeColor, topLeft = Offset(160f * s, 14f * s), size = Size(26f * s, 16f * s))
    drawOval(color = stripeColor, topLeft = Offset(90f * s, 14f * s), size = Size(22f * s, 15f * s))

    // Cab at rear
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(0f * s, 14f * s),
        size = Size(50f * s, 70f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )
    // Cab window
    drawRoundRect(
        color = Color(0xFFB3E5FC),
        topLeft = Offset(10f * s, 22f * s),
        size = Size(20f * s, 22f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Boiler brass bands
    for (i in 0 until 5) {
        val bx = (55f + i * 40f) * s
        drawLine(color = stripeColor, start = Offset(bx, 26f * s), end = Offset(bx, 84f * s), strokeWidth = 3f * s)
    }

    // Running Board Plate & Heavy Cast Bar Frame Chassis
    drawRect(color = Color(0xFF22262B), topLeft = Offset(10f * s, 83f * s), size = Size(295f * s, 5f * s))
    drawRoundRect(
        color = Color(0xFF1E2124),
        topLeft = Offset(40f * s, 88f * s),
        size = Size(230f * s, 10f * s),
        cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
    )

    // Realistically Scaled Wheels (Fitting cleanly under boiler, resting at rail line 122f * s)
    // 1. Rear Trailing Axle under Cab with external truck frame (4-6-2 Pacific arrangement)
    val ponyR = 11f * s
    val ponyY = 111f * s
    drawRailWheelWithRadius(28f * s, ponyY, ponyR, wheelAngleRad)
    drawRoundRect(
        color = Color(0xFF1E2124),
        topLeft = Offset(18f * s, 106f * s),
        size = Size(20f * s, 6f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )

    // 2. Three Main Spoked Drivers (Driver radius 16.5f, resting at 122f * s)
    val driverR = 16.5f * s
    val driverY = 105.5f * s
    val wheelX1 = 88f * s
    val wheelX2 = 145f * s
    val wheelX3 = 202f * s

    drawSteamDriverWheel(wheelX1, driverY, driverR, wheelAngleRad, s, stripeColor)
    drawSteamDriverWheel(wheelX2, driverY, driverR, wheelAngleRad, s, stripeColor)
    drawSteamDriverWheel(wheelX3, driverY, driverR, wheelAngleRad, s, stripeColor)

    // Steam Sand Delivery Pipe (Feeding sand directly from sandbox to front driver tire)
    val sandPipe = Path().apply {
        moveTo(165f * s, 30f * s)
        lineTo(165f * s, 83f * s)
        lineTo(wheelX2 - 12f * s, 105f * s)
        lineTo(wheelX2 - 14f * s, 119f * s)
    }
    drawPath(sandPipe, color = Color(0xFFD97706), style = Stroke(width = 1.8f * s))

    // 3. Two-Axle Front Leading Pilot Bogie with arched equalizer bar
    drawRoundRect(
        color = Color(0xFF1E2124),
        topLeft = Offset(245f * s, 107f * s),
        size = Size(60f * s, 5f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )
    drawRailWheelWithRadius(260f * s, ponyY, ponyR, wheelAngleRad)
    drawRailWheelWithRadius(290f * s, ponyY, ponyR, wheelAngleRad)

    // Walschaerts Valve Gear & Main Rod Assembly
    val pinOffset = 8f * s
    val pin1 = Offset(wheelX1 + cos(wheelAngleRad) * pinOffset, driverY + sin(wheelAngleRad) * pinOffset)
    val pin2 = Offset(wheelX2 + cos(wheelAngleRad) * pinOffset, driverY + sin(wheelAngleRad) * pinOffset)
    val pin3 = Offset(wheelX3 + cos(wheelAngleRad) * pinOffset, driverY + sin(wheelAngleRad) * pinOffset)

    // Side Coupling Rod connecting all 3 drivers
    drawLine(
        color = Color(0xFFCBD5E1),
        start = pin1,
        end = pin3,
        strokeWidth = 4f * s
    )
    drawCircle(color = Color(0xFF333333), radius = 3.5f * s, center = pin1)
    drawCircle(color = Color(0xFF333333), radius = 3.5f * s, center = pin2)
    drawCircle(color = Color(0xFF333333), radius = 3.5f * s, center = pin3)

    // Crosshead, Cylinder, and Main Piston Driving Rod
    val crossheadX = 236f * s
    val crossheadY = 104f * s
    // Main Driving Rod from center driver pin2 to crosshead
    drawLine(
        color = Color(0xFF94A3B8),
        start = pin2,
        end = Offset(crossheadX, crossheadY),
        strokeWidth = 3f * s
    )
    // Crosshead Guide Bars and Valve Expansion Link
    drawRect(color = Color(0xFF334155), topLeft = Offset(crossheadX - 6f * s, crossheadY - 3f * s), size = Size(12f * s, 6f * s))
    drawLine(color = Color(0xFF64748B), start = Offset(crossheadX - 10f * s, crossheadY - 4f * s), end = Offset(crossheadX + 16f * s, crossheadY - 4f * s), strokeWidth = 2f * s)
    drawLine(color = Color(0xFF64748B), start = Offset(crossheadX - 10f * s, crossheadY + 4f * s), end = Offset(crossheadX + 16f * s, crossheadY + 4f * s), strokeWidth = 2f * s)
    // Steam Cylinder Casting
    drawRoundRect(
        color = Color(0xFF1E2124),
        topLeft = Offset(242f * s, 94f * s),
        size = Size(28f * s, 18f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Front cowcatcher / pilot
    val cowcatcher = Path().apply {
        moveTo(290f * s, 84f * s)
        lineTo(330f * s, 120f * s)
        lineTo(290f * s, 120f * s)
        close()
    }
    drawPath(cowcatcher, color = Color(0xFF22262B))
}

private fun DrawScope.drawSteamDriverWheel(
    cx: Float,
    cy: Float,
    r: Float,
    angle: Float,
    s: Float,
    accentColor: Color
) {
    drawCircle(color = Color(0xFF4A4E54), radius = r, center = Offset(cx, cy))
    drawCircle(color = accentColor, radius = r - 2f * s, center = Offset(cx, cy))
    drawCircle(color = Color(0xFF1E2124), radius = r - 5f * s, center = Offset(cx, cy))

    drawArc(
        color = Color(0xFF6B7280),
        startAngle = Math.toDegrees((angle + PI).toDouble()).toFloat(),
        sweepAngle = 120f,
        useCenter = true,
        topLeft = Offset(cx - r + 5f * s, cy - r + 5f * s),
        size = Size((r - 5f * s) * 2, (r - 5f * s) * 2)
    )

    for (i in 0 until 8) {
        val spAngle = angle + (i * PI.toFloat() / 4f)
        drawLine(
            color = Color(0xFF8C96A0),
            start = Offset(cx, cy),
            end = Offset(cx + cos(spAngle) * (r - 5f * s), cy + sin(spAngle) * (r - 5f * s)),
            strokeWidth = 2.5f * s
        )
    }
    drawCircle(color = Color(0xFFCCCCCC), radius = 6f * s, center = Offset(cx, cy))
}

private fun DrawScope.drawElectricLocomotiveWithWheels(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // 1. Heavy Underframe & Transformers
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(10f * s, 82f * s),
        size = Size(340f * s, 18f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(110f * s, 90f * s),
        size = Size(140f * s, 20f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )

    // 2. Aerodynamic Streamlined Body
    val body = Path().apply {
        moveTo(10f * s, 84f * s)
        lineTo(10f * s, 26f * s)
        lineTo(235f * s, 22f * s)
        cubicTo(275f * s, 22f * s, 335f * s, 44f * s, 355f * s, 84f * s)
        lineTo(10f * s, 84f * s)
        close()
    }
    drawPath(body, color = bodyColor)

    // 3. Aerodynamic Roof Fairing & Pantographs
    drawRoundRect(
        color = Color(train.defaultRoofColor),
        topLeft = Offset(20f * s, 14f * s),
        size = Size(250f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    drawHighSpeedPantograph(75f * s, 14f * s, s)
    drawHighSpeedPantograph(210f * s, 14f * s, s)

    // 4. Dynamic Speed Stripe
    val speedStripe = Path().apply {
        moveTo(10f * s, 68f * s)
        lineTo(250f * s, 68f * s)
        lineTo(335f * s, 82f * s)
        lineTo(320f * s, 84f * s)
        lineTo(10f * s, 84f * s)
        close()
    }
    drawPath(speedStripe, color = stripeColor)

    // 5. Panoramic Cockpit Windscreen
    val visor = Path().apply {
        moveTo(250f * s, 28f * s)
        lineTo(315f * s, 46f * s)
        lineTo(290f * s, 54f * s)
        lineTo(240f * s, 40f * s)
        close()
    }
    drawPath(visor, color = Color(0xFF0F172A))
    drawPath(visor, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

    // 6. Passenger / Inspection Windows
    for (i in 0 until 5) {
        val wx = (25f + i * 38f) * s
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(wx, 36f * s),
            size = Size(22f * s, 14f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
        drawRoundRect(
            color = Color(0xAAFEF08A),
            topLeft = Offset(wx + 2f * s, 38f * s),
            size = Size(18f * s, 8f * s),
            cornerRadius = CornerRadius(1f * s, 1f * s)
        )
    }

    // 7. Headlight Cluster
    drawCircle(color = Color(0xFFFEF08A), radius = 3.5f * s, center = Offset(348f * s, 72f * s))

    // 8. 100% REAL STEEL FLANGED WHEELS ON BO-BO BOGIE TRUCKS
    drawEuropeanBogieTruck(65f * s, 98f * s, wheelAngleRad, s)
    drawEuropeanBogieTruck(285f * s, 98f * s, wheelAngleRad, s)
}

/**
 * Prototypical American 2-Axle Freight / Passenger Truck (AAR / Blomberg Type B standard).
 * Features:
 * - Drop-forged heavy steel side frame and central bolster with helical coil spring cluster.
 * - Accurate brake beam linkages and cast iron brake shoe heads.
 * - Flexible rubber sand hoses with brass nozzles directing sand to railhead (matching user spec).
 * - Standardized 14f * s wheels contacting rail strictly at 122f * s.
 */
fun DrawScope.drawBogieTruck(
    centerX: Float,
    centerY: Float,
    wheelAngleRad: Float,
    s: Float
) {
    val bogieFrame = Color(0xFF262C34)
    val bolsterDark = Color(0xFF181C22)
    val springSteel = Color(0xFF475569)
    val brakeShoeColor = Color(0xFF334155)
    val sandHoseRubber = Color(0xFF0F172A)
    val sandNozzleBrass = Color(0xFFD97706)

    // Standardized wheel center line: strictly at 108f * s (radius 14f * s => rail contact at 122f * s)
    val wheelY = 108f * s
    val r = 14f * s
    val wheelOffset = 22f * s

    // 1. Heavy American Drop-Equalizer / Side Frame
    val sideFramePath = Path().apply {
        moveTo(centerX - 36f * s, wheelY - 12f * s)
        lineTo(centerX + 36f * s, wheelY - 12f * s)
        lineTo(centerX + 38f * s, wheelY - 4f * s)
        lineTo(centerX + 34f * s, wheelY + 4f * s)
        lineTo(centerX + 12f * s, wheelY + 4f * s)
        lineTo(centerX + 8f * s, wheelY - 4f * s)
        lineTo(centerX - 8f * s, wheelY - 4f * s)
        lineTo(centerX - 12f * s, wheelY + 4f * s)
        lineTo(centerX - 34f * s, wheelY + 4f * s)
        lineTo(centerX - 38f * s, wheelY - 4f * s)
        close()
    }
    drawPath(sideFramePath, color = bogieFrame)

    // 2. Central Bolster & Helical Suspension Springs
    drawRoundRect(
        color = bolsterDark,
        topLeft = Offset(centerX - 10f * s, wheelY - 16f * s),
        size = Size(20f * s, 14f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    for (si in 0 until 3) {
        val sx = centerX - 6f * s + (si * 6f * s)
        drawLine(
            color = springSteel,
            start = Offset(sx, wheelY - 14f * s),
            end = Offset(sx, wheelY - 3f * s),
            strokeWidth = 2.2f * s
        )
    }

    // 3. Wheels with Journal Axle Boxes & Brake Shoes
    listOf(centerX - wheelOffset, centerX + wheelOffset).forEach { wx ->
        drawRailWheel(wx, wheelY, wheelAngleRad, s)

        // Journal box on axle center
        drawRoundRect(
            color = bolsterDark,
            topLeft = Offset(wx - 4.5f * s, wheelY - 5f * s),
            size = Size(9f * s, 10f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
        drawCircle(color = springSteel, radius = 2f * s, center = Offset(wx, wheelY))

        // Cast iron brake shoe
        val isLeft = wx < centerX
        val shoeX = if (isLeft) wx - 13f * s else wx + 11f * s
        drawRoundRect(
            color = brakeShoeColor,
            topLeft = Offset(shoeX, wheelY - 4f * s),
            size = Size(3f * s, 9f * s),
            cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
        )
    }

    // 4. American Flexible Rubber Sand Hoses & Brass Spray Nozzles (Feeding sand to railhead at 120f * s)
    // Left Front Sand Hose
    val leftHose = Path().apply {
        moveTo(centerX - 24f * s, wheelY - 8f * s)
        cubicTo(
            centerX - 33f * s, wheelY - 2f * s,
            centerX - 36f * s, wheelY + 6f * s,
            centerX - 33f * s, 120f * s
        )
    }
    drawPath(leftHose, color = sandHoseRubber, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
    drawRoundRect(
        color = sandNozzleBrass,
        topLeft = Offset(centerX - 35f * s, 118f * s),
        size = Size(3.5f * s, 3f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )

    // Right Rear Sand Hose
    val rightHose = Path().apply {
        moveTo(centerX + 24f * s, wheelY - 8f * s)
        cubicTo(
            centerX + 33f * s, wheelY - 2f * s,
            centerX + 36f * s, wheelY + 6f * s,
            centerX + 33f * s, 120f * s
        )
    }
    drawPath(rightHose, color = sandHoseRubber, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
    drawRoundRect(
        color = sandNozzleBrass,
        topLeft = Offset(centerX + 31.5f * s, 118f * s),
        size = Size(3.5f * s, 3f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )
}

/**
 * Authentic British Railway 2-Axle Bogie Truck (Gresley / BR Mark 1 Commonwealth standard).
 * Features:
 * - Heavy UK-gauge pressed-steel deep channel side frame with curved drop equalizer arches.
 * - Central elliptic leaf spring bolster plank with secondary coil suspension.
 * - Mansell/spoked steel wheels with white-painted rim tires and cast axle journal boxes.
 * - Vacuum brake cylinder linkages and double clasp brake blocks.
 * - Standardized wheel center line: strictly at 108f * s (radius 14f * s => rail contact at 122f * s).
 */
fun DrawScope.drawBritishGresleyBogieTruck(
    centerX: Float,
    centerY: Float,
    wheelAngleRad: Float,
    s: Float
) {
    val frameDark = Color(0xFF1B1F24)
    val steelFlange = Color(0xFFCBD5E1)
    val bolsterDark = Color(0xFF111418)
    val springSteel = Color(0xFF475569)
    val brakeBlock = Color(0xFF334155)

    val wheelY = 108f * s
    val r = 14f * s
    val wheelOffset = 23f * s

    // 1. UK Gresley / Commonwealth Deep Equalizer Side Frame
    val framePath = Path().apply {
        moveTo(centerX - 37f * s, wheelY - 13f * s)
        lineTo(centerX + 37f * s, wheelY - 13f * s)
        lineTo(centerX + 39f * s, wheelY - 4f * s)
        lineTo(centerX + 35f * s, wheelY + 3f * s)
        lineTo(centerX + 12f * s, wheelY + 3f * s)
        lineTo(centerX + 8f * s, wheelY - 4f * s)
        lineTo(centerX - 8f * s, wheelY - 4f * s)
        lineTo(centerX - 12f * s, wheelY + 3f * s)
        lineTo(centerX - 35f * s, wheelY + 3f * s)
        lineTo(centerX - 39f * s, wheelY - 4f * s)
        close()
    }
    drawPath(framePath, color = frameDark)

    // Top Channel Stiffener Lip
    drawLine(
        color = Color(0xFF475569),
        start = Offset(centerX - 35f * s, wheelY - 12f * s),
        end = Offset(centerX + 35f * s, wheelY - 12f * s),
        strokeWidth = 1.6f * s
    )

    // 2. Central Elliptic Leaf Spring & Bolster Plank
    drawRoundRect(
        color = bolsterDark,
        topLeft = Offset(centerX - 11f * s, wheelY - 17f * s),
        size = Size(22f * s, 12f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Inverted arched elliptic leaf spring
    val leafSpring = Path().apply {
        moveTo(centerX - 10f * s, wheelY - 14f * s)
        cubicTo(
            centerX - 5f * s, wheelY - 7f * s,
            centerX + 5f * s, wheelY - 7f * s,
            centerX + 10f * s, wheelY - 14f * s
        )
    }
    drawPath(leafSpring, color = springSteel, style = Stroke(width = 3.2f * s, cap = StrokeCap.Round))

    // 3. Two UK Standard Spoked Wheelsets (Resting on rail at 122f * s)
    listOf(centerX - wheelOffset, centerX + wheelOffset).forEach { wx ->
        // Outer rim & white tyre highlight
        drawCircle(color = Color(0xFF0F1216), radius = r + 1.8f * s, center = Offset(wx, wheelY))
        drawCircle(color = Color.White, radius = r, center = Offset(wx, wheelY), style = Stroke(width = 1.2f * s))
        drawCircle(color = Color(0xFF1E242B), radius = r - 2.5f * s, center = Offset(wx, wheelY))

        // 8 Rotating spokes
        for (i in 0 until 8) {
            val spkAngle = wheelAngleRad + (i * PI.toFloat() / 4f)
            val spkX = wx + cos(spkAngle) * (r * 0.52f)
            val spkY = wheelY + sin(spkAngle) * (r * 0.52f)
            drawCircle(color = Color(0xFF0F1216), radius = 1.6f * s, center = Offset(spkX, spkY))
        }

        // Heavy Cast Axle Journal Box with embossed lubrication cap
        drawRoundRect(
            color = bolsterDark,
            topLeft = Offset(wx - 4.8f * s, wheelY - 5.5f * s),
            size = Size(9.6f * s, 11f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
        drawCircle(color = Color(0xFFE2E8F0), radius = 2.2f * s, center = Offset(wx, wheelY))
        drawCircle(color = Color(0xFF0F1216), radius = 1.2f * s, center = Offset(wx, wheelY))

        // Clasp Brake Blocks
        val isLeft = wx < centerX
        val shoeX = if (isLeft) wx - 13f * s else wx + 10.5f * s
        drawRoundRect(
            color = brakeBlock,
            topLeft = Offset(shoeX, wheelY - 3.5f * s),
            size = Size(3f * s, 8.5f * s),
            cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
        )
    }

    // 4. British Vacuum Brake Pull Rod Linkage
    drawLine(
        color = Color(0xFF334155),
        start = Offset(centerX - 30f * s, wheelY - 2f * s),
        end = Offset(centerX + 30f * s, wheelY - 2f * s),
        strokeWidth = 1.8f * s
    )
}

/**
 * British Outline Underframe Buffer Gear & Screw Link Coupling Assembly.
 * Renders authentic UK round spring buffers, buffer beam planks, and vacuum brake pipes.
 */
fun DrawScope.drawBritishUnderframeBufferGear(
    widthPx: Float,
    s: Float
) {
    val bufferBlack = Color(0xFF181B1F)
    val bufferSteel = Color(0xFF64748B)
    val bufferShank = Color(0xFFCBD5E1)

    // Left Buffer Assembly
    drawRect(color = bufferBlack, topLeft = Offset(4f * s, 81f * s), size = Size(8f * s, 10f * s))
    drawLine(color = bufferShank, start = Offset(4f * s, 86f * s), end = Offset(-4f * s, 86f * s), strokeWidth = 2.5f * s)
    drawOval(color = bufferSteel, topLeft = Offset(-8f * s, 80f * s), size = Size(4.5f * s, 12f * s))

    // Right Buffer Assembly
    drawRect(color = bufferBlack, topLeft = Offset(248f * s, 81f * s), size = Size(8f * s, 10f * s))
    drawLine(color = bufferShank, start = Offset(256f * s, 86f * s), end = Offset(264f * s, 86f * s), strokeWidth = 2.5f * s)
    drawOval(color = bufferSteel, topLeft = Offset(263.5f * s, 80f * s), size = Size(4.5f * s, 12f * s))

    // Vacuum Brake Pipe (Flexible red/black swan neck hose in center)
    val vacPipe = Path().apply {
        moveTo(10f * s, 86f * s)
        cubicTo(7f * s, 92f * s, 2f * s, 96f * s, 0f * s, 94f * s)
    }
    drawPath(vacPipe, color = Color(0xFFDC2626), style = Stroke(width = 1.8f * s, cap = StrokeCap.Round))
}

fun DrawScope.drawRailWheel(
    cx: Float,
    cy: Float,
    angle: Float,
    s: Float
) {
    val r = 14f * s
    drawCircle(color = Color(0xFF42474E), radius = r + 2f * s, center = Offset(cx, cy))
    drawCircle(color = Color(0xFF1E2125), radius = r, center = Offset(cx, cy))
    drawCircle(color = Color(0xFF33383F), radius = r - 3.5f * s, center = Offset(cx, cy))

    for (i in 0 until 4) {
        val spokeAngle = angle + (i * PI.toFloat() / 2f)
        val hx = cx + cos(spokeAngle) * (r * 0.5f)
        val hy = cy + sin(spokeAngle) * (r * 0.5f)
        drawCircle(color = Color(0xFF14171A), radius = 2.5f * s, center = Offset(hx, hy))
    }

    drawCircle(color = Color(0xFF8E959E), radius = 3.5f * s, center = Offset(cx, cy))
}

/**
 * Draws detailed trailing railcars.
 * Scaled so wheel bottom is at y = 122f * s, perfectly matching locomotive wheel height!
 */
fun DrawScope.drawRailCarDetailed(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f,
    variant: String = "V1_VIRGIN",
    bodyColor: Color? = null,
    stripeColor: Color? = null
) {
    val s = widthPx / 260f

    // Frame (Raised to align wheels at 122f)
    drawRoundRect(
        color = Color(0xFF2C3238),
        topLeft = Offset(10f * s, 80f * s),
        size = Size(240f * s, 14f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Couplers (Left and Right)
    drawRect(color = Color(0xFF202327), topLeft = Offset(0f * s, 82f * s), size = Size(10f * s, 8f * s))
    drawRect(color = Color(0xFF202327), topLeft = Offset(250f * s, 82f * s), size = Size(10f * s, 8f * s))

    when (car.type) {
        RailCarType.LUMBER_FLATCAR -> {
            // Bulkhead ends
            drawRect(color = Color(0xFF2D5A3D), topLeft = Offset(15f * s, 20f * s), size = Size(14f * s, 62f * s))
            drawRect(color = Color(0xFF2D5A3D), topLeft = Offset(230f * s, 20f * s), size = Size(14f * s, 62f * s))
            for (i in 0 until 5) {
                drawRect(
                    color = Color(0xFF3E7A53),
                    topLeft = Offset((50f + i * 38f) * s, 25f * s),
                    size = Size(6f * s, 57f * s)
                )
            }
            // Stacked Pine Logs
            for (row in 0 until 4) {
                val countInRow = 7 - row
                val yRow = (68f - row * 12f) * s
                for (col in 0 until countInRow) {
                    val xLog = (34f + row * 8f + col * 26f) * s
                    drawCircle(color = Color(0xFFD4A373), radius = 6.5f * s, center = Offset(xLog, yRow))
                    drawCircle(color = Color(0xFFBC6C25), radius = 6.5f * s, center = Offset(xLog, yRow), style = Stroke(width = 1.2f * s))
                    drawCircle(color = Color(0xFFA65B1A), radius = 2.5f * s, center = Offset(xLog, yRow))
                }
            }
        }
        RailCarType.FUEL_TANKER -> {
            // Cylindrical Tank
            drawRoundRect(
                color = Color(0xFF22262B),
                topLeft = Offset(20f * s, 28f * s),
                size = Size(220f * s, 54f * s),
                cornerRadius = CornerRadius(24f * s, 24f * s)
            )
            // Orange Hazmat Band
            drawRect(
                color = Color(0xFFF97316),
                topLeft = Offset(105f * s, 28f * s),
                size = Size(50f * s, 54f * s)
            )
            // Top Dome
            drawRoundRect(
                color = Color(0xFF33383F),
                topLeft = Offset(118f * s, 20f * s),
                size = Size(24f * s, 10f * s),
                cornerRadius = CornerRadius(3f * s, 3f * s)
            )
            drawLine(color = Color(0xFF888888), start = Offset(25f * s, 54f * s), end = Offset(235f * s, 54f * s), strokeWidth = 2f * s)
        }
        RailCarType.CONTAINER_BOXCAR -> {
            // Corrugated Boxcar
            drawRoundRect(
                color = Color(0xFF8B263E),
                topLeft = Offset(15f * s, 22f * s),
                size = Size(230f * s, 60f * s),
                cornerRadius = CornerRadius(3f * s, 3f * s)
            )
            for (i in 0 until 16) {
                val rx = (25f + i * 13f) * s
                drawLine(color = Color(0x44000000), start = Offset(rx, 24f * s), end = Offset(rx, 78f * s), strokeWidth = 2f * s)
            }
            drawRect(color = Color(0xFF6B1D30), topLeft = Offset(105f * s, 24f * s), size = Size(50f * s, 56f * s))
        }
        RailCarType.PASSENGER_COACH -> {
            val coachBody = bodyColor ?: Color(0xFF1E3A8A)
            val coachStripe = stripeColor ?: Color(0xFFE2E8F0)
            // Streamlined Coach
            drawRoundRect(
                color = coachBody,
                topLeft = Offset(12f * s, 24f * s),
                size = Size(236f * s, 58f * s),
                cornerRadius = CornerRadius(4f * s, 4f * s)
            )
            drawRect(color = coachStripe, topLeft = Offset(12f * s, 58f * s), size = Size(236f * s, 10f * s))
            for (i in 0 until 6) {
                drawRoundRect(
                    color = Color(0xFFFEF08A),
                    topLeft = Offset((25f + i * 36f) * s, 32f * s),
                    size = Size(24f * s, 18f * s),
                    cornerRadius = CornerRadius(2f * s, 2f * s)
                )
            }

            // British outline coach buffers & vacuum equipment if running with British rolling stock
            if (car.name.contains("Gresley", ignoreCase = true) || car.name.contains("British", ignoreCase = true) || car.name.contains("Scotsman", ignoreCase = true) || car.id.contains("uk", ignoreCase = true)) {
                drawBritishUnderframeBufferGear(widthPx, s)
            }
        }
        RailCarType.HEAVY_COAL_HOPPER -> {
            // Open top hopper car
            val hopper = Path().apply {
                moveTo(20f * s, 30f * s)
                lineTo(240f * s, 30f * s)
                lineTo(225f * s, 80f * s)
                lineTo(35f * s, 80f * s)
                close()
            }
            drawPath(hopper, color = Color(0xFF374151))
            val coal = Path().apply {
                moveTo(22f * s, 30f * s)
                lineTo(55f * s, 18f * s)
                lineTo(130f * s, 14f * s)
                lineTo(200f * s, 20f * s)
                lineTo(238f * s, 30f * s)
                close()
            }
            drawPath(coal, color = Color(0xFF111315))
        }
        RailCarType.SHINKANSEN_E2_COACH -> {
            drawShinkansenE2Coach(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.PENDOLINO_COACH -> {
            drawPendolinoCoach(car, wheelAngleRad, widthPx, heightPx, variant)
        }
        RailCarType.PENDOLINO_REAR_CAB -> {
            drawPendolinoRearCab(car, wheelAngleRad, widthPx, heightPx, variant)
        }
        RailCarType.TOW_RESCUE_TENDER -> {
            drawTowRescueTender(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.METRA_GALLERY_COACH -> {
            drawMetraGalleryCoach(car, wheelAngleRad, widthPx, heightPx, variant)
        }
        RailCarType.METRA_CAB_CAR -> {
            drawMetraGalleryCoach(car, wheelAngleRad, widthPx, heightPx, variant)
        }
        RailCarType.CD_PENDOLINO_COACH -> {
            drawCdPendolinoCoach(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.CD_PENDOLINO_CAB -> {
            drawCdPendolinoRearCab(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.CD_COMFORTJET_COACH -> {
            drawCdComfortJetCoach(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.CD_REGIO_COACH -> {
            drawCdRegioCoach(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.AMTRAK_ACELA_COACH -> {
            drawAmtrakAcelaCoach(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.AMTRAK_ACELA_CAB -> {
            drawAmtrakAcelaRearPowerCar(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.STEAM_PULLMAN_COACH -> {
            drawSteamPullmanCoach(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.CSX_FREIGHT_BOXCAR -> {
            drawCsxBoxcar(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.CSX_COAL_HOPPER -> {
            drawCsxHopper(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.BNSF_HERITAGE_BOXCAR -> {
            drawBnsfBoxcar(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.BNSF_COAL_HOPPER -> {
            drawBnsfHopper(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.BN_CASCADE_GREEN_HOPPER -> {
            drawBnCascadeGreenHopper(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.BN_CASCADE_GREEN_BOXCAR -> {
            drawBnCascadeGreenBoxcar(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.BN_EXECUTIVE_BOXCAR -> {
            drawBnExecutiveBoxcar(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.SANTA_FE_WARBONNET_BOXCAR -> {
            drawSantaFeWarbonnetBoxcar(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.SANTA_FE_BLUEBONNET_TANKER -> {
            drawSantaFeBluebonnetTanker(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.UNION_PACIFIC_HOPPER -> {
            drawUnionPacificHopper(car, wheelAngleRad, widthPx, heightPx)
        }
        RailCarType.NORFOLK_SOUTHERN_HOPPER -> {
            drawNorfolkSouthernHopper(car, wheelAngleRad, widthPx, heightPx)
        }
    }

    // Standard Bogie Trucks: only for cars that do NOT have their own specialized bogies
    val hasIntegratedBogies = car.type in listOf(
        RailCarType.PENDOLINO_COACH,
        RailCarType.PENDOLINO_REAR_CAB,
        RailCarType.SHINKANSEN_E2_COACH,
        RailCarType.METRA_GALLERY_COACH,
        RailCarType.METRA_CAB_CAR,
        RailCarType.CD_PENDOLINO_COACH,
        RailCarType.CD_PENDOLINO_CAB,
        RailCarType.CD_COMFORTJET_COACH,
        RailCarType.CD_REGIO_COACH,
        RailCarType.AMTRAK_ACELA_COACH,
        RailCarType.AMTRAK_ACELA_CAB,
        RailCarType.STEAM_PULLMAN_COACH,
        RailCarType.TOW_RESCUE_TENDER
    )
    if (!hasIntegratedBogies) {
        val isBritish = car.name.contains("Gresley", ignoreCase = true) || car.name.contains("British", ignoreCase = true) || car.name.contains("Scotsman", ignoreCase = true) || car.id.contains("uk", ignoreCase = true)
        val isCzechOrEuropean = car.id.startsWith("cd_") || car.name.contains("ČD") || car.name.contains("Czech")
        when {
            isBritish -> {
                drawBritishGresleyBogieTruck(50f * s, 100f * s, wheelAngleRad, s)
                drawBritishGresleyBogieTruck(210f * s, 100f * s, wheelAngleRad, s)
            }
            isCzechOrEuropean -> {
                drawCzechSkodaBogieTruck(50f * s, 100f * s, wheelAngleRad, s)
                drawCzechSkodaBogieTruck(210f * s, 100f * s, wheelAngleRad, s)
            }
            else -> {
                // American Freight / Passenger standard AAR Blomberg truck with sand hoses
                drawBogieTruck(50f * s, 100f * s, wheelAngleRad, s)
                drawBogieTruck(210f * s, 100f * s, wheelAngleRad, s)
            }
        }
    }
}

/**
 * Draws the matching Japanese JR East Shinkansen E2-1000 series passenger coaches,
 * pantograph cars with aerodynamic wind fairings, and Green Car first class coaches.
 */
fun DrawScope.drawShinkansenE2Coach(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val pearlWhite = Color(0xFFFAFAFA)
    val azaleaPink = Color(0xFFE6007E)
    val indigoSkirt = Color(0xFF192A56)
    val roofWhite = Color(0xFFE2E8F0)

    val isPantograph = car.id.contains("c4") || car.id.contains("c6") || car.name.contains("Pantograph")
    val isGreenCar = car.id.contains("c9") || car.name.contains("Green") || car.name.contains("First Class")

    // 1. Frame & Flush End Diaphragm Gangways
    drawRect(color = Color(0xFF202327), topLeft = Offset(0f * s, 24f * s), size = Size(10f * s, 66f * s))
    drawRect(color = Color(0xFF202327), topLeft = Offset(250f * s, 24f * s), size = Size(10f * s, 66f * s))

    // Authentic Shinkansen High-Speed Bolsterless Chassis (DT205)
    drawModernHighSpeedChassis(
        frontBogieCenterX = 210f * s,
        rearBogieCenterX = 50f * s,
        trayStartX = 75f * s,
        trayEndX = 185f * s,
        wheelAngleRad = wheelAngleRad,
        s = s,
        hasFrontCoupler = false,
        hasRearCoupler = false
    )

    // 2. Streamlined Coach Body
    drawRoundRect(
        color = pearlWhite,
        topLeft = Offset(8f * s, 24f * s),
        size = Size(244f * s, 62f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // 3. Roof Fairing
    drawRoundRect(
        color = roofWhite,
        topLeft = Offset(10f * s, 18f * s),
        size = Size(240f * s, 8f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // 4. Midnight Indigo Blue Lower Skirt
    drawRect(
        color = indigoSkirt,
        topLeft = Offset(8f * s, 66f * s),
        size = Size(244f * s, 20f * s)
    )

    // 5. Vivid Azalea Pink / Magenta Stripe
    drawRect(
        color = azaleaPink,
        topLeft = Offset(8f * s, 62f * s),
        size = Size(244f * s, 4f * s)
    )

    // 6. Passenger Doors at Both Ends
    // Left Door
    drawRect(color = Color(0x33000000), topLeft = Offset(16f * s, 28f * s), size = Size(16f * s, 40f * s), style = Stroke(width = 1.2f * s))
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(19f * s, 34f * s), size = Size(10f * s, 12f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    // Right Door
    drawRect(color = Color(0x33000000), topLeft = Offset(228f * s, 28f * s), size = Size(16f * s, 40f * s), style = Stroke(width = 1.2f * s))
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(231f * s, 34f * s), size = Size(10f * s, 12f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // 7. Flush Tinted Passenger Windows (6 windows in middle)
    for (i in 0 until 6) {
        val wx = (42f + i * 31f) * s
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(wx, 34f * s),
            size = Size(22f * s, 16f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
        // Interior warm light
        drawRoundRect(
            color = if (isGreenCar) Color(0x99A7F3D0) else Color(0x99FEF08A),
            topLeft = Offset(wx + 2f * s, 36f * s),
            size = Size(18f * s, 8f * s),
            cornerRadius = CornerRadius(1f * s, 1f * s)
        )
    }

    // 8. Special Features: Pantograph or Green Car Emblem
    if (isPantograph) {
        // Aerodynamic Shroud / Wind Fairing on Roof (As shown in photo on Car 4 & 6)
        val shroud = Path().apply {
            moveTo(95f * s, 18f * s)
            lineTo(110f * s, 6f * s)
            lineTo(160f * s, 6f * s)
            lineTo(175f * s, 18f * s)
            close()
        }
        drawPath(shroud, color = Color(0xFFFAFAFA))
        drawPath(shroud, color = Color(0xFFCBD5E1), style = Stroke(width = 1.5f * s))

        // Red Single-Arm Z-Pantograph
        val panto = Path().apply {
            moveTo(125f * s, 6f * s)
            lineTo(140f * s, -10f * s)
            lineTo(130f * s, -22f * s)
            lineTo(150f * s, -22f * s)
        }
        drawPath(panto, color = Color(0xFFDC2626), style = Stroke(width = 2.5f * s))
        // Pantograph contact head
        drawLine(color = Color(0xFFF97316), start = Offset(120f * s, -22f * s), end = Offset(160f * s, -22f * s), strokeWidth = 3f * s)
    }

    if (isGreenCar) {
        // Green Car Clover Emblem (🍀) near boarding door
        val cloverX = 36f * s
        val cloverY = 42f * s
        drawCircle(color = Color(0xFF10B981), radius = 4f * s, center = Offset(cloverX, cloverY))
        drawCircle(color = Color(0xFF047857), radius = 4f * s, center = Offset(cloverX, cloverY), style = Stroke(width = 1.2f * s))
        // Tohoku Rainbow Apple Emblem on Car 9
        val appleX = 222f * s
        val appleY = 42f * s
        drawCircle(color = Color(0xFFDC2626), radius = 4f * s, center = Offset(appleX, appleY))
        drawLine(color = Color(0xFFFACC15), start = Offset(appleX - 3f * s, appleY), end = Offset(appleX + 3f * s, appleY), strokeWidth = 1.2f * s)
    }

    // 9. Aerodynamic Skirts covering upper half of wheels
    drawRoundRect(
        color = indigoSkirt,
        topLeft = Offset(20f * s, 82f * s),
        size = Size(60f * s, 14f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    drawRoundRect(
        color = indigoSkirt,
        topLeft = Offset(180f * s, 82f * s),
        size = Size(60f * s, 14f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
}

/**
 * Draws the High-Speed Pendolino / Voyager Intermediate Passenger Coach
 * with support for all 3 accurate design variants (Virgin, CrossCountry, Avanti)
 * and the improved high-speed chassis and bogies.
 */
fun DrawScope.drawPendolinoCoach(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f,
    variant: String = "V1_VIRGIN"
) {
    val s = widthPx / 260f
    val isBuffetCar = car.name.contains("Buffet") || car.id.contains("c3")

    when (variant) {
        "V2_CROSSCOUNTRY" -> {
            val whiteBody = Color(0xFFF8FAFC)
            val vermilionRoof = Color(0xFFC53030)
            val windowBand = Color(0xFF334155)
            val magentaDoor = Color(0xFFD92662)

            // Gangways
            drawRect(color = Color(0xFF202327), topLeft = Offset(0f * s, 24f * s), size = Size(10f * s, 66f * s))
            drawRect(color = Color(0xFF202327), topLeft = Offset(250f * s, 24f * s), size = Size(10f * s, 66f * s))

            // Body
            drawRoundRect(color = whiteBody, topLeft = Offset(8f * s, 24f * s), size = Size(244f * s, 62f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
            // Vermilion Red Roof
            drawRoundRect(color = vermilionRoof, topLeft = Offset(8f * s, 18f * s), size = Size(244f * s, 10f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
            // Dark Window Band
            drawRect(color = windowBand, topLeft = Offset(8f * s, 30f * s), size = Size(244f * s, 24f * s))

            // SIGNATURE HOT PINK / MAGENTA DOORS AT BOTH ENDS (Photos 1 & 2)
            drawRoundRect(color = magentaDoor, topLeft = Offset(14f * s, 28f * s), size = Size(18f * s, 44f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
            drawRect(color = Color(0xFF1E2228), topLeft = Offset(17f * s, 34f * s), size = Size(12f * s, 16f * s))
            drawCircle(color = Color(0xFFFACC15), radius = 1.4f * s, center = Offset(17f * s, 54f * s))

            drawRoundRect(color = magentaDoor, topLeft = Offset(228f * s, 28f * s), size = Size(18f * s, 44f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
            drawRect(color = Color(0xFF1E2228), topLeft = Offset(231f * s, 34f * s), size = Size(12f * s, 16f * s))
            drawCircle(color = Color(0xFFFACC15), radius = 1.4f * s, center = Offset(231f * s, 54f * s))

            // CrossCountry Branding & X Chevron
            drawRoundRect(color = Color(0xFF64748B), topLeft = Offset(40f * s, 62f * s), size = Size(50f * s, 4f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
            drawLine(color = magentaDoor, start = Offset(96f * s, 60f * s), end = Offset(104f * s, 68f * s), strokeWidth = 2f * s)
            drawLine(color = magentaDoor, start = Offset(104f * s, 60f * s), end = Offset(96f * s, 68f * s), strokeWidth = 2f * s)

            // Flush Passenger Windows
            for (i in 0 until 6) {
                val wx = (38f + i * 31f) * s
                drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wx, 34f * s), size = Size(22f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
                drawRoundRect(color = if (isBuffetCar) Color(0x99FDE047) else Color(0x99FEF08A), topLeft = Offset(wx + 2f * s, 36f * s), size = Size(18f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
            }
        }
        "V3_AVANTI" -> {
            val pearlWhite = Color(0xFFF1F5F9)
            val vermilionRoof = Color(0xFFC53030)
            val windowBand = Color(0xFF272E38)
            val coralRedTriangle = Color(0xFFDC2626)

            // Gangways
            drawRect(color = Color(0xFF202327), topLeft = Offset(0f * s, 24f * s), size = Size(10f * s, 66f * s))
            drawRect(color = Color(0xFF202327), topLeft = Offset(250f * s, 24f * s), size = Size(10f * s, 66f * s))

            // Body
            drawRoundRect(color = pearlWhite, topLeft = Offset(8f * s, 24f * s), size = Size(244f * s, 62f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
            // Roof
            drawRoundRect(color = vermilionRoof, topLeft = Offset(8f * s, 18f * s), size = Size(244f * s, 10f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
            // Window Band
            drawRect(color = windowBand, topLeft = Offset(8f * s, 30f * s), size = Size(244f * s, 24f * s))

            // White doors with yellow safety border
            drawRoundRect(color = Color(0xFFE2E8F0), topLeft = Offset(14f * s, 28f * s), size = Size(18f * s, 44f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
            drawRect(color = Color(0xFF1E2228), topLeft = Offset(17f * s, 34f * s), size = Size(12f * s, 16f * s))
            drawCircle(color = Color(0xFFFACC15), radius = 1.4f * s, center = Offset(17f * s, 54f * s))

            drawRoundRect(color = Color(0xFFE2E8F0), topLeft = Offset(228f * s, 28f * s), size = Size(18f * s, 44f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
            drawRect(color = Color(0xFF1E2228), topLeft = Offset(231f * s, 34f * s), size = Size(12f * s, 16f * s))
            drawCircle(color = Color(0xFFFACC15), radius = 1.4f * s, center = Offset(231f * s, 54f * s))

            // Avanti Red Triangle Emblem
            val tri = Path().apply {
                moveTo(125f * s, 60f * s)
                lineTo(133f * s, 68f * s)
                lineTo(117f * s, 68f * s)
                close()
            }
            drawPath(tri, color = coralRedTriangle)

            // Flush Passenger Windows
            for (i in 0 until 6) {
                val wx = (38f + i * 31f) * s
                drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wx, 34f * s), size = Size(22f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
                drawRoundRect(color = if (isBuffetCar) Color(0x99FDE047) else Color(0x99FEF08A), topLeft = Offset(wx + 2f * s, 36f * s), size = Size(18f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
            }
        }
        else -> {
            // V1 Virgin Trains West Coast
            val silverBody = Color(0xFFE2E8F0)
            val virginRed = Color(0xFFDC2626)

            // Gangways
            drawRect(color = Color(0xFF202327), topLeft = Offset(0f * s, 24f * s), size = Size(10f * s, 66f * s))
            drawRect(color = Color(0xFF202327), topLeft = Offset(250f * s, 24f * s), size = Size(10f * s, 66f * s))

            // Body
            drawRoundRect(color = silverBody, topLeft = Offset(8f * s, 24f * s), size = Size(244f * s, 62f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
            // Virgin Red Roof
            drawRoundRect(color = virginRed, topLeft = Offset(8f * s, 18f * s), size = Size(244f * s, 10f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
            // Virgin Red Bodyside Ribbon
            drawRect(color = virginRed, topLeft = Offset(8f * s, 62f * s), size = Size(244f * s, 8f * s))

            // Doors
            drawRect(color = Color(0x33000000), topLeft = Offset(16f * s, 28f * s), size = Size(16f * s, 40f * s), style = Stroke(width = 1.2f * s))
            drawCircle(color = Color(0xFFEF4444), radius = 1.6f * s, center = Offset(24f * s, 31f * s))
            drawRect(color = Color(0x33000000), topLeft = Offset(228f * s, 28f * s), size = Size(16f * s, 40f * s), style = Stroke(width = 1.2f * s))
            drawCircle(color = Color(0xFFEF4444), radius = 1.6f * s, center = Offset(236f * s, 31f * s))

            // Flush Windows
            for (i in 0 until 6) {
                val wx = (42f + i * 31f) * s
                drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wx, 34f * s), size = Size(22f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
                drawRoundRect(color = if (isBuffetCar) Color(0x99FDE047) else Color(0x99FEF08A), topLeft = Offset(wx + 2f * s, 36f * s), size = Size(18f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
            }
        }
    }

    // High-Speed Accurate Chassis (Photo 4)
    drawModernHighSpeedChassis(
        frontBogieCenterX = 210f * s,
        rearBogieCenterX = 50f * s,
        trayStartX = 78f * s,
        trayEndX = 182f * s,
        wheelAngleRad = wheelAngleRad,
        s = s,
        hasFrontCoupler = false
    )
}

/**
 * Draws the Backward-Facing Class 390 Pendolino Driving Cab Car (Trailing Rear Unit)
 * with support for all 3 accurate design variants and rear high-intensity taillights.
 */
fun DrawScope.drawPendolinoRearCab(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f,
    variant: String = "V1_VIRGIN"
) {
    val s = widthPx / 260f
    val warningYellow = Color(0xFFFACC15)

    when (variant) {
        "V2_CROSSCOUNTRY" -> {
            val whiteBody = Color(0xFFF8FAFC)
            val maroonCab = Color(0xFF2B1422)
            val maroonGraphic = Color(0xFF4C1D3A)
            val vermilionRoof = Color(0xFFC53030)
            val windowBand = Color(0xFF334155)
            val magentaDoor = Color(0xFFD92662)

            // 1. Backward-facing Body
            val rearBody = Path().apply {
                moveTo(250f * s, 86f * s)
                lineTo(250f * s, 26f * s)
                lineTo(55f * s, 22f * s)
                cubicTo(20f * s, 22f * s, 5f * s, 50f * s, 0f * s, 86f * s)
                lineTo(250f * s, 86f * s)
                close()
            }
            drawPath(rearBody, color = whiteBody)

            // 2. Roof
            val redRoof = Path().apply {
                moveTo(250f * s, 26f * s)
                lineTo(250f * s, 16f * s)
                lineTo(55f * s, 16f * s)
                cubicTo(30f * s, 16f * s, 10f * s, 32f * s, 2f * s, 46f * s)
                lineTo(30f * s, 46f * s)
                lineTo(65f * s, 26f * s)
                close()
            }
            drawPath(redRoof, color = vermilionRoof)

            // 3. Dark Window Band
            drawRect(color = windowBand, topLeft = Offset(55f * s, 30f * s), size = Size(195f * s, 24f * s))

            // 4. Maroon Cab Front (Facing backward left)
            val cabBack = Path().apply {
                moveTo(55f * s, 22f * s)
                lineTo(55f * s, 86f * s)
                lineTo(0f * s, 86f * s)
                cubicTo(5f * s, 50f * s, 20f * s, 22f * s, 55f * s, 22f * s)
                close()
            }
            drawPath(cabBack, color = maroonCab)

            // Chevron Graphic
            val chevronGraphic = Path().apply {
                moveTo(55f * s, 32f * s)
                lineTo(95f * s, 54f * s)
                lineTo(55f * s, 76f * s)
                close()
            }
            drawPath(chevronGraphic, color = maroonGraphic)

            // 5. Yellow Brow on Rear Nose
            val yellowBrow = Path().apply {
                moveTo(0f * s, 86f * s)
                lineTo(20f * s, 86f * s)
                cubicTo(15f * s, 72f * s, 25f * s, 62f * s, 40f * s, 55f * s)
                lineTo(48f * s, 66f * s)
                close()
            }
            drawPath(yellowBrow, color = warningYellow)

            // 6. Cockpit Windscreen
            val windscreen = Path().apply {
                moveTo(75f * s, 30f * s)
                lineTo(28f * s, 48f * s)
                lineTo(42f * s, 54f * s)
                lineTo(85f * s, 38f * s)
                close()
            }
            drawPath(windscreen, color = Color(0xFF0F172A))
            drawPath(windscreen, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

            // 7. Signature Magenta Door
            drawRoundRect(color = magentaDoor, topLeft = Offset(90f * s, 28f * s), size = Size(18f * s, 44f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
            drawRect(color = Color(0xFF1E2228), topLeft = Offset(93f * s, 34f * s), size = Size(12f * s, 16f * s))
            drawCircle(color = Color(0xFFFACC15), radius = 1.4f * s, center = Offset(93f * s, 54f * s))

            // 8. CrossCountry Branding & X
            drawRoundRect(color = Color(0xFF64748B), topLeft = Offset(130f * s, 62f * s), size = Size(50f * s, 4f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
            drawLine(color = magentaDoor, start = Offset(186f * s, 60f * s), end = Offset(194f * s, 68f * s), strokeWidth = 2f * s)
            drawLine(color = magentaDoor, start = Offset(194f * s, 60f * s), end = Offset(186f * s, 68f * s), strokeWidth = 2f * s)
        }
        "V3_AVANTI" -> {
            val pearlWhite = Color(0xFFF1F5F9)
            val petrolTealCab = Color(0xFF164E63)
            val vermilionRoof = Color(0xFFC53030)
            val windowBand = Color(0xFF272E38)
            val coralRedTriangle = Color(0xFFDC2626)

            // 1. Backward-facing Body
            val rearBody = Path().apply {
                moveTo(250f * s, 86f * s)
                lineTo(250f * s, 26f * s)
                lineTo(55f * s, 22f * s)
                cubicTo(20f * s, 22f * s, 5f * s, 50f * s, 0f * s, 86f * s)
                lineTo(250f * s, 86f * s)
                close()
            }
            drawPath(rearBody, color = pearlWhite)

            // 2. Roof
            val redRoof = Path().apply {
                moveTo(250f * s, 26f * s)
                lineTo(250f * s, 16f * s)
                lineTo(55f * s, 16f * s)
                cubicTo(30f * s, 16f * s, 10f * s, 32f * s, 2f * s, 46f * s)
                lineTo(30f * s, 46f * s)
                lineTo(65f * s, 26f * s)
                close()
            }
            drawPath(redRoof, color = vermilionRoof)

            // 3. Dark Window Band
            drawRect(color = windowBand, topLeft = Offset(55f * s, 30f * s), size = Size(195f * s, 24f * s))

            // 4. Petrol Teal Cab
            val cabBack = Path().apply {
                moveTo(55f * s, 22f * s)
                lineTo(55f * s, 86f * s)
                lineTo(0f * s, 86f * s)
                cubicTo(5f * s, 50f * s, 20f * s, 22f * s, 55f * s, 22f * s)
                close()
            }
            drawPath(cabBack, color = petrolTealCab)

            // 5. Yellow Brow
            val yellowBrow = Path().apply {
                moveTo(0f * s, 86f * s)
                lineTo(20f * s, 86f * s)
                cubicTo(15f * s, 72f * s, 25f * s, 62f * s, 40f * s, 55f * s)
                lineTo(48f * s, 66f * s)
                close()
            }
            drawPath(yellowBrow, color = warningYellow)

            // 6. Cockpit Windscreen
            val windscreen = Path().apply {
                moveTo(75f * s, 30f * s)
                lineTo(28f * s, 48f * s)
                lineTo(42f * s, 54f * s)
                lineTo(85f * s, 38f * s)
                close()
            }
            drawPath(windscreen, color = Color(0xFF0F172A))
            drawPath(windscreen, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

            // 7. Avanti Triangle Logo
            val tri = Path().apply {
                moveTo(40f * s, 58f * s)
                lineTo(48f * s, 66f * s)
                lineTo(32f * s, 66f * s)
                close()
            }
            drawPath(tri, color = coralRedTriangle)
        }
        else -> {
            // V1 Virgin
            val silverBody = Color(0xFFE2E8F0)
            val virginRed = Color(0xFFDC2626)

            // 1. Body
            val rearBody = Path().apply {
                moveTo(250f * s, 86f * s)
                lineTo(250f * s, 26f * s)
                lineTo(55f * s, 22f * s)
                cubicTo(20f * s, 22f * s, 5f * s, 50f * s, 0f * s, 86f * s)
                lineTo(250f * s, 86f * s)
                close()
            }
            drawPath(rearBody, color = silverBody)

            // 2. Virgin Red Roof
            val redRoof = Path().apply {
                moveTo(250f * s, 26f * s)
                lineTo(250f * s, 16f * s)
                lineTo(55f * s, 16f * s)
                cubicTo(30f * s, 16f * s, 10f * s, 32f * s, 2f * s, 46f * s)
                lineTo(30f * s, 46f * s)
                lineTo(65f * s, 26f * s)
                close()
            }
            drawPath(redRoof, color = virginRed)

            // 3. Ribbon
            val redRibbon = Path().apply {
                moveTo(250f * s, 54f * s)
                lineTo(60f * s, 54f * s)
                cubicTo(35f * s, 54f * s, 12f * s, 68f * s, 2f * s, 86f * s)
                lineTo(18f * s, 86f * s)
                cubicTo(35f * s, 74f * s, 65f * s, 64f * s, 95f * s, 64f * s)
                lineTo(250f * s, 64f * s)
                close()
            }
            drawPath(redRibbon, color = virginRed)

            // 4. Yellow Nose
            val yellowNose = Path().apply {
                moveTo(0f * s, 86f * s)
                lineTo(20f * s, 86f * s)
                cubicTo(15f * s, 72f * s, 25f * s, 62f * s, 40f * s, 55f * s)
                lineTo(48f * s, 66f * s)
                close()
            }
            drawPath(yellowNose, color = warningYellow)

            // 5. Windscreen
            val windscreen = Path().apply {
                moveTo(75f * s, 30f * s)
                lineTo(28f * s, 48f * s)
                lineTo(42f * s, 54f * s)
                lineTo(85f * s, 38f * s)
                close()
            }
            drawPath(windscreen, color = Color(0xFF0F172A))
            drawPath(windscreen, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))
        }
    }

    // Glowing Bright Red Taillights (Since it's trailing backwards!)
    drawCircle(color = Color(0xFFFF0000), radius = 4f * s, center = Offset(6f * s, 76f * s))
    drawCircle(color = Color(0xFFFF0000), radius = 4f * s, center = Offset(14f * s, 82f * s))

    // Flush Windows on rear cab body
    for (i in 0 until 3) {
        val wx = (110f + i * 36f) * s
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wx, 34f * s), size = Size(24f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
        drawRoundRect(color = Color(0x99FEF08A), topLeft = Offset(wx + 2f * s, 36f * s), size = Size(20f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    }

    // High-Speed Chassis for Rear Unit (Photo 4)
    drawModernHighSpeedChassis(
        frontBogieCenterX = 210f * s,
        rearBogieCenterX = 50f * s,
        trayStartX = 78f * s,
        trayEndX = 182f * s,
        wheelAngleRad = wheelAngleRad,
        s = s,
        hasFrontCoupler = false
    )
}

/**
 * Draws heavy recovery breakdown / rescue tender unit.
 */
fun DrawScope.drawTowRescueTender(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f

    // Heavy Breakdown Steel Body
    drawRoundRect(
        color = Color(0xFF1E3A8A),
        topLeft = Offset(12f * s, 26f * s),
        size = Size(236f * s, 58f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    // Hazard Chevrons on side
    for (i in 0 until 6) {
        val cx = (30f + i * 35f) * s
        drawRect(color = Color(0xFFFACC15), topLeft = Offset(cx, 62f * s), size = Size(16f * s, 14f * s))
        drawRect(color = Color(0xFF1E293B), topLeft = Offset(cx + 8f * s, 62f * s), size = Size(8f * s, 14f * s))
    }
    // Heavy Equipment Crane & Winch Cables
    drawRoundRect(color = Color(0xFF475569), topLeft = Offset(30f * s, 16f * s), size = Size(60f * s, 12f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawLine(color = Color(0xFFCBD5E1), start = Offset(90f * s, 22f * s), end = Offset(180f * s, 22f * s), strokeWidth = 2.5f * s)
    // Tool Locker Doors
    for (i in 0 until 4) {
        drawRect(color = Color(0x33000000), topLeft = Offset((100f + i * 32f) * s, 32f * s), size = Size(26f * s, 26f * s), style = Stroke(width = 1.2f * s))
    }
}

/**
 * Draws active particles.
 */
fun DrawScope.drawGameParticles(particles: List<Particle>) {
    for (p in particles) {
        val lifeRatio = (p.currentLife / p.maxLife).coerceIn(0f, 1f)
        val remainingAlpha = (p.alpha * (1f - lifeRatio)).coerceIn(0f, 1f)

        when (p.type) {
            ParticleType.EXHAUST_SMOKE -> {
                drawCircle(
                    color = Color(0xFF26282B).copy(alpha = remainingAlpha),
                    radius = p.size * (1f + lifeRatio * 1.6f),
                    center = Offset(p.x, p.y)
                )
            }
            ParticleType.STEAM_PUFF -> {
                drawCircle(
                    color = Color(0xFFF8FAFC).copy(alpha = remainingAlpha),
                    radius = p.size * (1f + lifeRatio * 2.2f),
                    center = Offset(p.x, p.y)
                )
            }
            ParticleType.WHEEL_SPARK -> {
                // Intense bright spark core and trailing fire streak
                drawCircle(
                    color = Color(0xFFFFFFFF).copy(alpha = remainingAlpha),
                    radius = p.size * 0.6f,
                    center = Offset(p.x, p.y)
                )
                drawCircle(
                    color = Color(0xFFFFEA00).copy(alpha = remainingAlpha),
                    radius = p.size,
                    center = Offset(p.x, p.y)
                )
                drawLine(
                    color = Color(0xFFFF6D00).copy(alpha = remainingAlpha),
                    start = Offset(p.x, p.y),
                    end = Offset(p.x - p.vx * 2.5f, p.y - p.vy * 2.5f),
                    strokeWidth = 2.4f
                )
            }
            ParticleType.SAND_GRAIN -> {
                // High-visibility golden-yellow quartz sand grains sprayed directly at wheels
                drawCircle(
                    color = Color(0xFFFBBF24).copy(alpha = remainingAlpha),
                    radius = p.size,
                    center = Offset(p.x, p.y)
                )
                drawCircle(
                    color = Color(0xFFD97706).copy(alpha = remainingAlpha * 0.8f),
                    radius = p.size * 0.6f,
                    center = Offset(p.x, p.y)
                )
            }
            ParticleType.WATER_MIST -> {
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = remainingAlpha),
                    radius = p.size * (1f + lifeRatio * 2.5f),
                    center = Offset(p.x, p.y)
                )
            }
            ParticleType.DIESEL_FLAME -> {
                // Intense 3-layer roaring flame: Red/Crimson glow -> Blazing Orange flame -> White/Yellow incandescent core
                val currentSize = p.size * (0.8f + lifeRatio * 1.6f)
                // Outer fire aura / smoke edges
                drawCircle(
                    color = Color(0xFFDC2626).copy(alpha = remainingAlpha * 0.85f),
                    radius = currentSize * 1.35f,
                    center = Offset(p.x, p.y)
                )
                // Middle vivid flame body
                drawCircle(
                    color = Color(0xFFEA580C).copy(alpha = remainingAlpha * 0.95f),
                    radius = currentSize * 0.9f,
                    center = Offset(p.x, p.y)
                )
                // Hot core
                drawCircle(
                    color = Color(0xFFFEF08A).copy(alpha = remainingAlpha),
                    radius = currentSize * 0.45f,
                    center = Offset(p.x, p.y)
                )
                // Trailing flame streak
                drawLine(
                    color = Color(0xFFFF7700).copy(alpha = remainingAlpha * 0.8f),
                    start = Offset(p.x, p.y),
                    end = Offset(p.x - p.vx * 3.5f, p.y - p.vy * 3.5f),
                    strokeWidth = currentSize * 0.7f
                )
            }
        }
    }
}

/**
 * Draws ambient sky overcast / atmosphere tint and lightning flash for the active weather condition.
 */
fun DrawScope.drawWeatherAtmosphere(
    weather: WeatherType,
    canvasW: Float,
    gameViewH: Float,
    lightningAlpha: Float
) {
    when (weather) {
        WeatherType.CLEAR -> {
            // Natural sunlight, no dark overcast
        }
        WeatherType.RAIN -> {
            // Dark rainy overcast wash
            drawRect(
                color = Color(0x380F172A),
                topLeft = Offset.Zero,
                size = Size(canvasW, gameViewH)
            )
        }
        WeatherType.THUNDERSTORM -> {
            // Deep stormy overcast
            drawRect(
                color = Color(0x5A030712),
                topLeft = Offset.Zero,
                size = Size(canvasW, gameViewH)
            )
            // Full lightning flash illumination
            if (lightningAlpha > 0.01f) {
                drawRect(
                    color = Color(0xFFE0F2FE).copy(alpha = lightningAlpha.coerceIn(0f, 0.85f)),
                    topLeft = Offset.Zero,
                    size = Size(canvasW, gameViewH)
                )
            }
        }
        WeatherType.SNOW -> {
            // Soft cold winter haze
            drawRect(
                color = Color(0x2294A3B8),
                topLeft = Offset.Zero,
                size = Size(canvasW, gameViewH)
            )
        }
        WeatherType.BLIZZARD -> {
            // Frigid howling blizzard haze
            drawRect(
                color = Color(0x45E2E8F0),
                topLeft = Offset.Zero,
                size = Size(canvasW, gameViewH)
            )
        }
        WeatherType.FOG -> {
            // Dense valley mist gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x3394A3B8), Color(0x6664748B), Color(0x44CBD5E1)),
                    startY = 0f,
                    endY = gameViewH
                ),
                size = Size(canvasW, gameViewH)
            )
        }
    }
}

/**
 * Draws active precipitation (falling rain streaks, snow flakes, fog drifts, wet track splashes).
 */
fun DrawScope.drawWeatherPrecipitation(
    weather: WeatherType,
    canvasW: Float,
    gameViewH: Float,
    speedKmh: Float,
    animTime: Float
) {
    val speedFactor = (speedKmh * 0.08f)

    when (weather) {
        WeatherType.CLEAR -> {
            // No precipitation
        }
        WeatherType.RAIN -> {
            // Moderate falling rain streaks angled by train forward speed
            val dropCount = 65
            for (i in 0 until dropCount) {
                val seed = i * 137.5f
                val fallSpeed = 520f + (i % 7) * 45f
                val y = ((animTime * fallSpeed + seed * 19f) % (gameViewH + 60f)) - 30f
                val slantX = -speedFactor * 12f - 6f
                val x = ((seed * 47f - animTime * (speedFactor * 80f + 40f)) % (canvasW + 80f) + (canvasW + 80f)) % (canvasW + 80f) - 40f

                drawLine(
                    color = Color(0x99BAE6FD),
                    start = Offset(x, y),
                    end = Offset(x + slantX, y + 16f),
                    strokeWidth = 1.4f
                )

                // Track level splash ripples
                if (y > gameViewH * 0.58f && (i % 4 == 0)) {
                    drawCircle(
                        color = Color(0x44BAE6FD),
                        radius = 2.2f,
                        center = Offset(x, y)
                    )
                }
            }
        }
        WeatherType.THUNDERSTORM -> {
            // Heavy torrential rain streaks and violent cross-streaks
            val dropCount = 110
            for (i in 0 until dropCount) {
                val seed = i * 91.3f
                val fallSpeed = 750f + (i % 9) * 60f
                val y = ((animTime * fallSpeed + seed * 23f) % (gameViewH + 70f)) - 35f
                val slantX = -speedFactor * 16f - 14f
                val x = ((seed * 53f - animTime * (speedFactor * 120f + 80f)) % (canvasW + 100f) + (canvasW + 100f)) % (canvasW + 100f) - 50f

                drawLine(
                    color = Color(0xCCBAE6FD),
                    start = Offset(x, y),
                    end = Offset(x + slantX, y + 22f),
                    strokeWidth = 1.8f
                )

                if (y > gameViewH * 0.55f && (i % 3 == 0)) {
                    drawCircle(
                        color = Color(0x66E0F2FE),
                        radius = 3.0f,
                        center = Offset(x, y)
                    )
                }
            }
        }
        WeatherType.SNOW -> {
            // Floating gentle snowflakes drifting with sine wave turbulence
            val flakeCount = 55
            for (i in 0 until flakeCount) {
                val seed = i * 113.7f
                val fallSpeed = 75f + (i % 5) * 18f
                val y = ((animTime * fallSpeed + seed * 13f) % (gameViewH + 40f)) - 20f
                val sway = sin(animTime * 2.5f + i.toFloat()) * 12f
                val x = ((seed * 39f + sway - animTime * (speedFactor * 35f + 15f)) % (canvasW + 60f) + (canvasW + 60f)) % (canvasW + 60f) - 30f
                val flakeSize = 2.0f + (i % 4) * 0.8f

                drawCircle(
                    color = Color(0xCCFFFFFF),
                    radius = flakeSize,
                    center = Offset(x, y)
                )
            }
        }
        WeatherType.BLIZZARD -> {
            // Fierce howling snow squall with fast horizontal drift
            val flakeCount = 95
            for (i in 0 until flakeCount) {
                val seed = i * 79.1f
                val fallSpeed = 160f + (i % 6) * 35f
                val y = ((animTime * fallSpeed + seed * 17f) % (gameViewH + 50f)) - 25f
                val slantX = -speedFactor * 22f - 32f
                val x = ((seed * 41f - animTime * (speedFactor * 160f + 220f)) % (canvasW + 120f) + (canvasW + 120f)) % (canvasW + 120f) - 60f

                drawLine(
                    color = Color(0xDDE2E8F0),
                    start = Offset(x, y),
                    end = Offset(x + slantX, y + 6f),
                    strokeWidth = 2.2f
                )
            }
        }
        WeatherType.FOG -> {
            // Dense Volumetric Atmospheric Fog Overlay (reduces visibility while rails & train remain clear)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x9994A3B8),
                        Color(0x66CBD5E1),
                        Color(0x33E2E8F0),
                        Color(0x5594A3B8)
                    ),
                    startY = 0f,
                    endY = gameViewH
                ),
                size = Size(canvasW, gameViewH)
            )

            // Rolling dynamic fog clouds drifting horizontally
            val bandCount = 8
            for (i in 0 until bandCount) {
                val mistY = (gameViewH * 0.15f) + (i * (gameViewH * 0.1f))
                val mistX = ((animTime * (14f + (i % 3) * 8f) + i * 160f) % (canvasW + 400f)) - 200f
                val mistW = 320f + (i % 4) * 80f
                val mistH = 50f + (i % 3) * 25f
                drawOval(
                    color = Color(0x3DFFFFFF),
                    topLeft = Offset(mistX - mistW / 2f, mistY - mistH / 2f),
                    size = Size(mistW, mistH)
                )
            }
        }
    }
}

/**
 * High-Speed Modern Train Chassis with underfloor aerodynamic equipment pods,
 * battery boxes, air tanks, transformer trays, and dual disc-braked bogies.
 */
fun DrawScope.drawModernHighSpeedChassis(
    frontBogieCenterX: Float,
    rearBogieCenterX: Float,
    trayStartX: Float,
    trayEndX: Float,
    wheelAngleRad: Float,
    s: Float,
    hasFrontCoupler: Boolean = false,
    hasRearCoupler: Boolean = false
) {
    val darkUnderframe = Color(0xFF1E293B)
    val equipMetal = Color(0xFF334155)
    val accentMetal = Color(0xFF475569)

    // Underfloor Equipment Pods & Battery Raft
    val trayW = (trayEndX - trayStartX).coerceAtLeast(20f * s)
    drawRoundRect(
        color = darkUnderframe,
        topLeft = Offset(trayStartX, 86f * s),
        size = Size(trayW, 14f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Air Reservoir & Transformer Pods
    val podCount = (trayW / (24f * s)).toInt().coerceIn(2, 6)
    for (i in 0 until podCount) {
        val px = trayStartX + (i * (trayW / podCount)) + 2f * s
        drawRoundRect(
            color = equipMetal,
            topLeft = Offset(px, 88f * s),
            size = Size((trayW / podCount) - 4f * s, 10f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
        drawLine(
            color = accentMetal,
            start = Offset(px + 2f * s, 93f * s),
            end = Offset(px + (trayW / podCount) - 6f * s, 93f * s),
            strokeWidth = 1f * s
        )
    }

    // Modern High-Speed Dual-Axle Bogies (Wheel bottom at 122f * s)
    drawModernHighSpeedBogie(rearBogieCenterX, 100f * s, wheelAngleRad, s)
    drawModernHighSpeedBogie(frontBogieCenterX, 100f * s, wheelAngleRad, s)

    // Couplers if required
    if (hasRearCoupler) {
        drawRect(color = Color(0xFF1E293B), topLeft = Offset(0f * s, 84f * s), size = Size(10f * s, 6f * s))
    }
    if (hasFrontCoupler) {
        drawRect(color = Color(0xFF1E293B), topLeft = Offset(348f * s, 84f * s), size = Size(10f * s, 6f * s))
    }
}

/**
 * Realistically accurate standard Bullet Train / High-Speed Bolsterless Bogie
 * (Modeled on Shinkansen WDT205 / Alstom TGV Y230 / Acela high-speed truck standard).
 * Features:
 * - Streamlined low-slung H-frame welded steel chassis with transom drop.
 * - Dual pneumatic secondary air spring bellows with air supply pipe.
 * - Prominent horizontal anti-hunting hydraulic yaw damper in vibrant cyan/blue with chrome rod.
 * - Longitudinal Z-link traction rods transferring high-speed traction forces.
 * - Conical rubber spring / helical primary suspension with vertical hydraulic dampers.
 * - Ventilated dual brake discs on forged alloy steel wheels with silver wear bands.
 * - Grounding brush return current connector and axle-end speed sensor wiring.
 * - Perfectly uniform scale: wheel center at 108f * s, radius 14f * s, contacting rail at 122f * s.
 */
fun DrawScope.drawStandardBulletTrainBogie(
    centerX: Float,
    centerY: Float,
    wheelAngleRad: Float,
    s: Float
) {
    val bogieFrame = Color(0xFF1E242B)
    val bolsterDark = Color(0xFF0F172A)
    val yawDamperCyan = Color(0xFF0284C7)
    val pistonChrome = Color(0xFFE2E8F0)
    val primarySpring = Color(0xFF475569)
    val brakeCaliper = Color(0xFF111827)

    // Standardized wheel center line: strictly at 108f * s (radius 14f * s => rail contact line at 122f * s!)
    val wheelY = 108f * s
    val r = 14f * s
    val wheelSpacing = 24f * s
    val leftWheelX = centerX - wheelSpacing
    val rightWheelX = centerX + wheelSpacing

    // 1. Aerodynamic Low-Slung H-Frame Chassis with Transom Drop
    val framePath = Path().apply {
        moveTo(centerX - 36f * s, wheelY - 14f * s)
        lineTo(centerX + 36f * s, wheelY - 14f * s)
        lineTo(centerX + 38f * s, wheelY - 8f * s)
        lineTo(centerX + 34f * s, wheelY - 2f * s)
        lineTo(centerX + 16f * s, wheelY - 2f * s)
        lineTo(centerX + 12f * s, wheelY - 8f * s)
        lineTo(centerX - 12f * s, wheelY - 8f * s)
        lineTo(centerX - 16f * s, wheelY - 2f * s)
        lineTo(centerX - 34f * s, wheelY - 2f * s)
        lineTo(centerX - 38f * s, wheelY - 8f * s)
        close()
    }
    drawPath(framePath, color = bogieFrame)

    // 2. Secondary Air Spring Bellows (Dual pneumatic cushions on transom bolster)
    drawRoundRect(
        color = bolsterDark,
        topLeft = Offset(centerX - 12f * s, wheelY - 20f * s),
        size = Size(10f * s, 8f * s),
        cornerRadius = CornerRadius(2.5f * s, 2.5f * s)
    )
    drawRoundRect(
        color = bolsterDark,
        topLeft = Offset(centerX + 2f * s, wheelY - 20f * s),
        size = Size(10f * s, 8f * s),
        cornerRadius = CornerRadius(2.5f * s, 2.5f * s)
    )
    // Air supply pipe connecting the bellows
    drawLine(
        color = Color(0xFF38BDF8),
        start = Offset(centerX - 10f * s, wheelY - 16f * s),
        end = Offset(centerX + 10f * s, wheelY - 16f * s),
        strokeWidth = 1.2f * s
    )

    // 3. Iconic Horizontal Anti-Hunting Hydraulic Yaw Damper
    // (Stabilizes high-speed bullet train at 300+ km/h; cyan cylinder with chrome piston)
    drawRoundRect(
        color = yawDamperCyan,
        topLeft = Offset(centerX - 24f * s, wheelY - 16f * s),
        size = Size(18f * s, 3.5f * s),
        cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
    )
    drawLine(
        color = pistonChrome,
        start = Offset(centerX - 6f * s, wheelY - 14.2f * s),
        end = Offset(centerX + 4f * s, wheelY - 14.2f * s),
        strokeWidth = 2f * s
    )
    drawCircle(color = Color(0xFF94A3B8), radius = 2f * s, center = Offset(centerX - 24f * s, wheelY - 14.2f * s))
    drawCircle(color = Color(0xFF94A3B8), radius = 2f * s, center = Offset(centerX + 4f * s, wheelY - 14.2f * s))

    // 4. Longitudinal Z-Link Traction Rods
    drawLine(
        color = Color(0xFF64748B),
        start = Offset(centerX - 14f * s, wheelY - 5f * s),
        end = Offset(centerX - 22f * s, wheelY - 2f * s),
        strokeWidth = 2.2f * s
    )
    drawLine(
        color = Color(0xFF64748B),
        start = Offset(centerX + 14f * s, wheelY - 5f * s),
        end = Offset(centerX + 22f * s, wheelY - 2f * s),
        strokeWidth = 2.2f * s
    )

    // 5. Two High-Speed Axle Sets with Ventilated Disc Brakes (Resting on rail at 122f * s)
    listOf(leftWheelX, rightWheelX).forEach { wx ->
        // Primary axle box suspension struts
        drawRoundRect(
            color = primarySpring,
            topLeft = Offset(wx - 5f * s, wheelY - 10f * s),
            size = Size(10f * s, 7f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
        // Vertical hydraulic shock absorber
        drawLine(
            color = Color(0xFF38BDF8),
            start = Offset(wx + 4f * s, wheelY - 12f * s),
            end = Offset(wx + 4f * s, wheelY - 4f * s),
            strokeWidth = 1.5f * s
        )

        // Ventilated high-speed disc wheel
        drawModernDiscWheel(wx, wheelY, wheelAngleRad, s)

        // Disk brake caliper unit clutching the brake disc
        drawRoundRect(
            color = brakeCaliper,
            topLeft = Offset(wx - 9f * s, wheelY + 1f * s),
            size = Size(5f * s, 8f * s),
            cornerRadius = CornerRadius(1f * s, 1f * s)
        )
        drawRect(
            color = Color(0xFFD97706),
            topLeft = Offset(wx - 8f * s, wheelY + 3f * s),
            size = Size(3f * s, 4f * s)
        )

        // Speed sensor cable on axle cap
        drawLine(
            color = Color(0xFF0F172A),
            start = Offset(wx, wheelY),
            end = Offset(wx - 2f * s, wheelY - 8f * s),
            strokeWidth = 1f * s
        )
    }
}

/**
 * Modern High-Speed Bogie with yaw dampers, primary coil springs,
 * air bellows, secondary air suspension, and ventilated disc wheels.
 */
fun DrawScope.drawModernHighSpeedBogie(
    centerX: Float,
    centerY: Float,
    wheelAngleRad: Float,
    s: Float
) {
    drawStandardBulletTrainBogie(centerX, centerY, wheelAngleRad, s)
}

/**
 * Modern High-Speed Ventilated Steel Wheel with Disc Brake Ring & Hub Bolts
 */
fun DrawScope.drawModernDiscWheel(
    cx: Float,
    cy: Float,
    angle: Float,
    s: Float
) {
    val r = 14f * s
    val wheelSteel = Color(0xFF334155)
    val discBrake = Color(0xFF64748B)
    val hubCenter = Color(0xFF0F172A)

    // Steel Tyre Rim
    drawCircle(color = wheelSteel, radius = r + 1.5f * s, center = Offset(cx, cy))
    drawCircle(color = Color(0xFF1E293B), radius = r, center = Offset(cx, cy))

    // Ventilated Brake Disc Ring
    drawCircle(color = discBrake, radius = r - 3.5f * s, center = Offset(cx, cy))
    drawCircle(color = Color(0xFF0F172A), radius = r - 7f * s, center = Offset(cx, cy))

    // Rotating Hub Radial Vent Holes / Brake Bolt pattern
    for (i in 0 until 4) {
        val spokeAngle = angle + (i * PI.toFloat() / 2f)
        val boltX = cx + cos(spokeAngle) * (r * 0.52f)
        val boltY = cy + sin(spokeAngle) * (r * 0.52f)
        drawCircle(color = Color(0xFF94A3B8), radius = 1.2f * s, center = Offset(boltX, boltY))
    }

    // Axle End Hub Cap
    drawCircle(color = hubCenter, radius = 3.5f * s, center = Offset(cx, cy))
    drawCircle(color = Color(0xFFCBD5E1), radius = 1.2f * s, center = Offset(cx, cy))
}

/**
 * Detailed 2D visual rendering for the Metra commuter locomotive fleet.
 */
fun DrawScope.drawMetraDetailedLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String = "V1_METRA_CLASSIC"
) {
    when (train.id) {
        "metra_f40ph_screamer" -> drawMetraF40PHLocomotive(train, bodyColor, stripeColor, wheelAngleRad, s, variant)
        "metra_mp36ph_express" -> drawMetraMP36PHLocomotive(train, bodyColor, stripeColor, wheelAngleRad, s, variant)
        "metra_f59phi_silver" -> drawMetraF59PHILocomotive(train, bodyColor, stripeColor, wheelAngleRad, s, variant)
        "metra_highliner_emu" -> drawMetraHighlinerEMULocomotive(train, bodyColor, stripeColor, wheelAngleRad, s, variant)
        "metra_charger_sc44" -> drawMetraChargerSC44Locomotive(train, bodyColor, stripeColor, wheelAngleRad, s, variant)
        "metra_e8_streamliner" -> drawMetraE8StreamlinerLocomotive(train, bodyColor, stripeColor, wheelAngleRad, s, variant)
        "metra_alstom_coradia_dual" -> drawMetraCoradiaDualLocomotive(train, bodyColor, stripeColor, wheelAngleRad, s, variant)
        else -> drawMetraF40PHLocomotive(train, bodyColor, stripeColor, wheelAngleRad, s, variant)
    }
}

/**
 * Metra F40PH "Screamer" Commuter Diesel Locomotive.
 */
private fun DrawScope.drawMetraF40PHLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String
) {
    drawAccurateF40PHLocomotive(train, bodyColor, stripeColor, wheelAngleRad, s, variant)
}

/**
 * Metra MP36PH-3S "Express" Streamline Locomotive.
 * Chicago Metra's iconic MotivePower commuter diesel with aerodynamic rounded cab,
 * fluted stainless side body, rooftop dynamic brake blister, cooling fans, and ditch lights.
 */
private fun DrawScope.drawMetraMP36PHLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String
) {
    val metraNavy = when (variant) {
        "V2_METRA_LIGHTNING_STRIPE" -> Color(0xFF0F172A)
        "V3_METRA_ILLINOIS_PRIDE" -> Color(0xFF1E3A8A)
        else -> Color(0xFF10316B) // Prototypical Chicago Metra Navy
    }
    val metraOrange = when (variant) {
        "V3_METRA_ILLINOIS_PRIDE" -> Color(0xFFFBBF24)
        else -> Color(0xFFF95700) // Metra Safety Orange
    }
    val stainlessFuselage = Color(0xFFE2E8F0)
    val darkChassis = Color(0xFF1E293B)

    // Underframe & High-Speed Dual-Axle Blomberg Bogies (Wheels uniform 14f * s at y = 108f * s)
    drawModernHighSpeedChassis(285f * s, 75f * s, 110f * s, 245f * s, wheelAngleRad, s, true, true)

    // Heavy Cast Pilot Snowplow (Matte Black)
    val plow = Path().apply {
        moveTo(330f * s, 86f * s)
        lineTo(354f * s, 86f * s)
        lineTo(352f * s, 114f * s)
        lineTo(326f * s, 114f * s)
        close()
    }
    drawPath(plow, color = Color(0xFF0F172A))

    // Main Stainless Steel Locomotive Fuselage
    val mpBody = Path().apply {
        moveTo(14f * s, 86f * s)
        lineTo(14f * s, 22f * s)
        lineTo(255f * s, 20f * s)
        cubicTo(290f * s, 20f * s, 342f * s, 42f * s, 352f * s, 86f * s)
        lineTo(14f * s, 86f * s)
        close()
    }
    drawPath(mpBody, color = stainlessFuselage)

    // Corrugated Fluted Stainless Steel Lower Body Panels
    for (i in 0 until 9) {
        val cy = (52f + i * 3.4f) * s
        drawLine(color = Color(0xFF94A3B8), start = Offset(18f * s, cy), end = Offset(250f * s, cy), strokeWidth = 1.3f * s)
        drawLine(color = Color.White.copy(alpha = 0.6f), start = Offset(18f * s, cy + 1.2f * s), end = Offset(250f * s, cy + 1.2f * s), strokeWidth = 0.8f * s)
    }

    // Aerodynamic Rounded Cab Nose (Chicago Metra Navy Cowl)
    val aeroCab = Path().apply {
        moveTo(246f * s, 20f * s)
        cubicTo(290f * s, 20f * s, 342f * s, 42f * s, 352f * s, 86f * s)
        lineTo(265f * s, 86f * s)
        lineTo(246f * s, 38f * s)
        close()
    }
    drawPath(aeroCab, color = metraNavy)

    // Metra Sweeping Chevron Speed Ribbon (Orange & White)
    val ribbonWhite = Path().apply {
        moveTo(14f * s, 67f * s)
        lineTo(252f * s, 67f * s)
        cubicTo(280f * s, 67f * s, 324f * s, 74f * s, 349f * s, 86f * s)
        lineTo(339f * s, 86f * s)
        cubicTo(312f * s, 77f * s, 276f * s, 73f * s, 248f * s, 73f * s)
        lineTo(14f * s, 73f * s)
        close()
    }
    drawPath(ribbonWhite, color = Color.White)

    val ribbonOrange = Path().apply {
        moveTo(14f * s, 70f * s)
        lineTo(250f * s, 70f * s)
        cubicTo(278f * s, 70f * s, 320f * s, 76f * s, 346f * s, 86f * s)
        lineTo(336f * s, 86f * s)
        cubicTo(308f * s, 78f * s, 274f * s, 75f * s, 246f * s, 75f * s)
        lineTo(14f * s, 75f * s)
        close()
    }
    drawPath(ribbonOrange, color = metraOrange)

    // Rooftop Dynamic Brake Blister & Radiator Q-Fans
    drawRoundRect(color = Color(0xFF334155), topLeft = Offset(60f * s, 14f * s), size = Size(170f * s, 8f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    for (f in 0 until 3) {
        val fx = (75f + f * 50f) * s
        drawCircle(color = Color(0xFF0F172A), radius = 6f * s, center = Offset(fx, 15f * s))
        drawCircle(color = Color(0xFF94A3B8), radius = 4f * s, center = Offset(fx, 15f * s), style = Stroke(width = 1f * s))
    }

    // Curved Panoramic Cockpit Glass with Center Post Divider & Wipers
    val windshield = Path().apply {
        moveTo(274f * s, 26f * s)
        lineTo(326f * s, 43f * s)
        lineTo(314f * s, 54f * s)
        lineTo(266f * s, 38f * s)
        close()
    }
    drawPath(windshield, color = Color(0xFF0F172A))
    drawPath(windshield, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))
    // Windshield center post divider
    drawLine(color = metraNavy, start = Offset(296f * s, 33f * s), end = Offset(288f * s, 46f * s), strokeWidth = 2.5f * s)

    // Illuminated Road Numberboards above windshield ("412")
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(272f * s, 20f * s), size = Size(20f * s, 6f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawRoundRect(color = Color(0xFFFEF08A), topLeft = Offset(274f * s, 21f * s), size = Size(16f * s, 4f * s), cornerRadius = CornerRadius(0.5f * s, 0.5f * s))

    // High Intensity Twin Nose Sealed-Beam Headlights
    drawCircle(color = Color(0xFFFEF08A), radius = 4f * s, center = Offset(345f * s, 66f * s))
    drawCircle(color = Color.White, radius = 2f * s, center = Offset(345f * s, 66f * s))

    // Dual Oscillating Pilot Ditch Lights (Pulsing grade-crossing lights)
    drawCircle(color = Color(0xFFFEF08A), radius = 3.2f * s, center = Offset(341f * s, 78f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 3.2f * s, center = Offset(330f * s, 81f * s))

    // Metra Cab Herald Logo
    drawCircle(color = Color.White, radius = 4f * s, center = Offset(255f * s, 54f * s))
    drawCircle(color = metraNavy, radius = 3f * s, center = Offset(255f * s, 54f * s))
    drawCircle(color = metraOrange, radius = 1.5f * s, center = Offset(255f * s, 54f * s))
}

/**
 * Metra F59PHI "Silver" Commuter Diesel Locomotive.
 * Streamlined aerodynamic cowl with composite nose, fluted side panels,
 * and high-speed commuter styling.
 */
private fun DrawScope.drawMetraF59PHILocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String
) {
    val cabNavy = when (variant) {
        "V2_METRA_PACIFIC_SURF" -> Color(0xFF0F172A)
        "V3_METRA_EXPRESS_WHITE" -> Color(0xFF0284C7)
        else -> Color(0xFF10316B)
    }
    val metraOrange = Color(0xFFF95700)
    val silverBase = if (variant == "V3_METRA_EXPRESS_WHITE") Color(0xFFF8FAFC) else Color(0xFFCBD5E1)

    // Underframe & High-Speed Dual-Axle Blomberg Bogies (Wheels uniform 14f * s at y = 108f * s)
    drawModernHighSpeedChassis(285f * s, 75f * s, 110f * s, 245f * s, wheelAngleRad, s, true, true)

    // Pilot Snowplow
    drawRect(color = Color(0xFF0F172A), topLeft = Offset(328f * s, 86f * s), size = Size(24f * s, 24f * s))

    // Sleek Composite Cowl Body
    val f59Body = Path().apply {
        moveTo(14f * s, 86f * s)
        lineTo(14f * s, 22f * s)
        lineTo(265f * s, 20f * s)
        cubicTo(305f * s, 20f * s, 344f * s, 40f * s, 352f * s, 86f * s)
        lineTo(14f * s, 86f * s)
        close()
    }
    drawPath(f59Body, color = silverBase)

    // Fluted Stainless Steel Ribbing
    for (i in 0 until 8) {
        val cy = (54f + i * 3.6f) * s
        drawLine(color = Color(0xFF94A3B8), start = Offset(18f * s, cy), end = Offset(245f * s, cy), strokeWidth = 1.3f * s)
    }

    // Aerodynamic Composite Nose Cowl
    val nosePath = Path().apply {
        moveTo(260f * s, 20f * s)
        cubicTo(305f * s, 20f * s, 344f * s, 40f * s, 352f * s, 86f * s)
        lineTo(275f * s, 86f * s)
        lineTo(255f * s, 36f * s)
        close()
    }
    drawPath(nosePath, color = cabNavy)

    // Metra Tricolor Accent Wave Ribbon
    drawRect(color = metraOrange, topLeft = Offset(14f * s, 72f * s), size = Size(270f * s, 4f * s))
    drawRect(color = Color.White, topLeft = Offset(14f * s, 76f * s), size = Size(270f * s, 2.5f * s))
    drawRect(color = cabNavy, topLeft = Offset(14f * s, 78.5f * s), size = Size(270f * s, 4f * s))

    // Rear HEP Radiator Louver Grid
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(24f * s, 26f * s), size = Size(38f * s, 24f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    for (lv in 0 until 6) {
        val ly = (28f + lv * 3.5f) * s
        drawLine(color = Color(0xFF64748B), start = Offset(26f * s, ly), end = Offset(60f * s, ly), strokeWidth = 1f * s)
    }

    // Slanted Cockpit Windshield
    val windshield = Path().apply {
        moveTo(278f * s, 26f * s)
        lineTo(328f * s, 44f * s)
        lineTo(315f * s, 53f * s)
        lineTo(270f * s, 37f * s)
        close()
    }
    drawPath(windshield, color = Color(0xFF0F172A))
    drawPath(windshield, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

    // Nose Twin Headlight & Ditch Lights
    drawCircle(color = Color(0xFFFEF08A), radius = 4f * s, center = Offset(345f * s, 66f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 3f * s, center = Offset(340f * s, 78f * s))
}

/**
 * Metra Highliner II Bi-Level Electric Multiple Unit (EMU).
 * Authentic Chicago Metra Electric District (MED) Nippon Sharyo commuter railcar
 * with stainless steel fluting, two tiers of commuter windows, rooftop Faiveley pantograph,
 * and high-visibility front safety warning chevrons!
 */
private fun DrawScope.drawMetraHighlinerEMULocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String
) {
    val cabAccent = when (variant) {
        "V2_METRA_CENTRAL_GREEN" -> Color(0xFF15803D)
        "V3_METRA_MILLENNIUM_COMMUTER" -> Color(0xFF0F172A)
        else -> Color(0xFF10316B)
    }
    val stainlessBody = Color(0xFFE2E8F0)

    // Dual-Axle Commuter Bogies (Wheels uniform 14f * s at y = 108f * s)
    drawModernHighSpeedChassis(280f * s, 80f * s, 110f * s, 250f * s, wheelAngleRad, s, true, true)

    // Tall Nippon Sharyo Bi-Level Commuter Body
    drawRoundRect(
        color = stainlessBody,
        topLeft = Offset(14f * s, 16f * s),
        size = Size(334f * s, 70f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )

    // Fluted Stainless Steel Ribs (Upper and mid body)
    for (i in 0 until 14) {
        val cy = (42f + i * 2.6f) * s
        drawLine(color = Color(0xFFCBD5E1), start = Offset(18f * s, cy), end = Offset(332f * s, cy), strokeWidth = 1.1f * s)
    }

    // Highliner Single-Arm Faiveley Catenary Pantograph
    drawRoofPantograph(70f * s, 16f * s, s)

    // Rooftop HVAC & Electric Equipment Pods
    drawRoundRect(color = Color(0xFF475569), topLeft = Offset(150f * s, 11f * s), size = Size(65f * s, 6f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    drawRoundRect(color = Color(0xFF475569), topLeft = Offset(235f * s, 11f * s), size = Size(65f * s, 6f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))

    // Upper Deck Commuter Tinted Windows with Warm Interior Passenger Glow
    for (i in 0 until 8) {
        val wx = (28f + i * 36f) * s
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wx, 21f * s), size = Size(23f * s, 13f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
        drawRoundRect(color = Color(0xCCFEF08A), topLeft = Offset(wx + 2.5f * s, 23.5f * s), size = Size(18f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    }

    // Lower Deck Commuter Tinted Windows with Warm Interior Passenger Glow
    for (i in 0 until 8) {
        val wx = (28f + i * 36f) * s
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wx, 59f * s), size = Size(23f * s, 15f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
        drawRoundRect(color = Color(0xCCFEF08A), topLeft = Offset(wx + 2.5f * s, 61.5f * s), size = Size(18f * s, 10f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    }

    // Front Engineer Cab End Face
    drawRect(color = cabAccent, topLeft = Offset(322f * s, 16f * s), size = Size(26f * s, 70f * s))

    // High-Visibility Safety Warning Chevrons on Cab Door (Orange & White transit warning)
    for (ch in 0 until 5) {
        val chy = (48f + ch * 7f) * s
        drawLine(color = Color(0xFFF95700), start = Offset(324f * s, chy), end = Offset(346f * s, chy + 5f * s), strokeWidth = 2.5f * s)
    }

    // Front LED Digital Destination Sign ("METRA ELECTRIC - MILLENNIUM")
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(326f * s, 20f * s), size = Size(18f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawRect(color = Color(0xFFFBBF24), topLeft = Offset(328f * s, 22f * s), size = Size(14f * s, 4f * s))

    // Cab End Windshield
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(328f * s, 30f * s), size = Size(16f * s, 14f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(330f * s, 32f * s), size = Size(12f * s, 10f * s), cornerRadius = CornerRadius(0.5f * s, 0.5f * s))

    // Twin LED Transit Headlights & Lower Ditch Lights
    drawCircle(color = Color(0xFFFEF08A), radius = 3.5f * s, center = Offset(344f * s, 66f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 3.5f * s, center = Offset(344f * s, 74f * s))
}

/**
 * Metra SC-44 Charger High-Speed Diesel-Electric Locomotive.
 * Modern Siemens Charger with sharp angular aerodynamic cab,
 * rooftop cooling vents, dynamic chevron slash, and LED ditch lights.
 */
private fun DrawScope.drawMetraChargerSC44Locomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String
) {
    val chargerBlue = when (variant) {
        "V2_METRA_CHARGER_ECO_GREEN" -> Color(0xFF0D9488)
        "V3_METRA_NIGHT_EXPRESS" -> Color(0xFF0F172A)
        else -> Color(0xFF10316B)
    }
    val accentStripe = when (variant) {
        "V2_METRA_CHARGER_ECO_GREEN" -> Color(0xFF22C55E)
        "V3_METRA_NIGHT_EXPRESS" -> Color(0xFF38BDF8)
        else -> Color(0xFFF95700)
    }

    // High-Speed Dual-Axle Bogies (Wheels uniform 14f * s at y = 108f * s)
    drawModernHighSpeedChassis(285f * s, 75f * s, 110f * s, 245f * s, wheelAngleRad, s, true, true)

    // Angular Charger Commuter Fuselage
    val chargerBody = Path().apply {
        moveTo(14f * s, 86f * s)
        lineTo(14f * s, 22f * s)
        lineTo(280f * s, 20f * s)
        lineTo(346f * s, 44f * s)
        lineTo(352f * s, 86f * s)
        lineTo(14f * s, 86f * s)
        close()
    }
    drawPath(chargerBody, color = Color(0xFFE2E8F0))

    // Sharp Angular Nose Facets
    val cabNose = Path().apply {
        moveTo(270f * s, 20f * s)
        lineTo(346f * s, 44f * s)
        lineTo(352f * s, 86f * s)
        lineTo(282f * s, 86f * s)
        lineTo(260f * s, 36f * s)
        close()
    }
    drawPath(cabNose, color = chargerBlue)

    // High-Speed Dynamic Lightning Slash
    val sweep = Path().apply {
        moveTo(14f * s, 71f * s)
        lineTo(270f * s, 71f * s)
        lineTo(346f * s, 77f * s)
        lineTo(346f * s, 84f * s)
        lineTo(265f * s, 78f * s)
        lineTo(14f * s, 78f * s)
        close()
    }
    drawPath(sweep, color = accentStripe)

    // Angular European-Style Cockpit Windshield
    val windshield = Path().apply {
        moveTo(284f * s, 25f * s)
        lineTo(336f * s, 43f * s)
        lineTo(322f * s, 52f * s)
        lineTo(274f * s, 36f * s)
        close()
    }
    drawPath(windshield, color = Color(0xFF0F172A))
    drawPath(windshield, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

    // Dual Rooftop Radiator Cooling Arrays (Cummins QSK95)
    drawRoundRect(color = Color(0xFF334155), topLeft = Offset(60f * s, 14f * s), size = Size(65f * s, 8f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF334155), topLeft = Offset(145f * s, 14f * s), size = Size(65f * s, 8f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Integrated LED High-Intensity Headlight & Angular Ditch Lights
    drawCircle(color = Color(0xFFFEF08A), radius = 4f * s, center = Offset(346f * s, 64f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 3.5f * s, center = Offset(338f * s, 76f * s))
}

/**
 * Helper to draw a realistic 3-axle diesel truck with uniform 14f * s wheels and American sand hoses.
 */
fun DrawScope.drawThreeAxleDieselBogie(
    centerX: Float,
    centerY: Float,
    wheelAngleRad: Float,
    s: Float
) {
    val bogieFrame = Color(0xFF1E293B)
    val springColor = Color(0xFF64748B)
    val sandHoseRubber = Color(0xFF111827)
    val sandNozzleBrass = Color(0xFFD97706)

    // Standardized wheel center line: strictly at 108f * s (radius 14f * s => rail contact line at 122f * s!)
    val wheelY = 108f * s
    val r = 14f * s

    // Side frame spanning all 3 axles
    drawRoundRect(
        color = bogieFrame,
        topLeft = Offset(centerX - 46f * s, wheelY - 14f * s),
        size = Size(92f * s, 12f * s),
        cornerRadius = CornerRadius(2.5f * s, 2.5f * s)
    )

    // Three perfectly uniform 14f * s wheels resting at rail 122f * s
    listOf(centerX - 28f * s, centerX, centerX + 28f * s).forEach { wx ->
        drawModernDiscWheel(wx, wheelY, wheelAngleRad, s)
        // Journal axle box with coil spring
        drawRoundRect(
            color = springColor,
            topLeft = Offset(wx - 4f * s, wheelY - 6f * s),
            size = Size(8f * s, 8f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
    }

    // American Truck Flexible Rubber Sand Hoses & Brass Nozzles (Feeding sand directly to railhead at 120f * s)
    // 1. Forward Sand Hose
    val frontHose = Path().apply {
        moveTo(centerX - 36f * s, wheelY - 10f * s)
        cubicTo(
            centerX - 44f * s, wheelY - 4f * s,
            centerX - 46f * s, wheelY + 6f * s,
            centerX - 43f * s, 120f * s
        )
    }
    drawPath(frontHose, color = sandHoseRubber, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round))
    drawRoundRect(
        color = sandNozzleBrass,
        topLeft = Offset(centerX - 45f * s, 118f * s),
        size = Size(4f * s, 3f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )

    // 2. Rear Sand Hose
    val rearHose = Path().apply {
        moveTo(centerX + 36f * s, wheelY - 10f * s)
        cubicTo(
            centerX + 44f * s, wheelY - 4f * s,
            centerX + 46f * s, wheelY + 6f * s,
            centerX + 43f * s, 120f * s
        )
    }
    drawPath(rearHose, color = sandHoseRubber, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round))
    drawRoundRect(
        color = sandNozzleBrass,
        topLeft = Offset(centerX + 41f * s, 118f * s),
        size = Size(4f * s, 3f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )
}

/**
 * Metra E8 "Streamliner" Vintage Bulldog Locomotive.
 * Classic EMD twin-engine streamliner in Chicago & North Western / Metra heritage blue & silver,
 * with classic circular porthole windows, dual Mars oscillating light, and 3-axle A1A trucks.
 */
private fun DrawScope.drawMetraE8StreamlinerLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String
) {
    val noseColor = when (variant) {
        "V2_METRA_MILWAUKEE_ORANGE" -> Color(0xFFEA580C)
        "V3_METRA_VINTAGE_BLUE" -> Color(0xFF0284C7)
        else -> Color(0xFF10316B)
    }
    val flankColor = when (variant) {
        "V2_METRA_MILWAUKEE_ORANGE" -> Color(0xFF1E293B)
        else -> Color(0xFFCBD5E1)
    }

    // Underframe with Two 3-Axle A1A-A1A Trucks (All 6 wheels uniform 14f * s at y = 108f * s)
    drawRect(color = Color(0xFF1E293B), topLeft = Offset(14f * s, 86f * s), size = Size(336f * s, 8f * s))
    drawThreeAxleDieselBogie(80f * s, 100f * s, wheelAngleRad, s)
    drawThreeAxleDieselBogie(250f * s, 100f * s, wheelAngleRad, s)

    // Streamlined EMD Bulldog Body
    val e8Body = Path().apply {
        moveTo(14f * s, 86f * s)
        lineTo(14f * s, 24f * s)
        lineTo(260f * s, 22f * s)
        cubicTo(300f * s, 22f * s, 345f * s, 42f * s, 350f * s, 86f * s)
        lineTo(14f * s, 86f * s)
        close()
    }
    drawPath(e8Body, color = flankColor)

    // EMD Bulldog Nose Curve
    val bulldogNose = Path().apply {
        moveTo(250f * s, 22f * s)
        cubicTo(295f * s, 22f * s, 345f * s, 42f * s, 350f * s, 86f * s)
        lineTo(260f * s, 86f * s)
        lineTo(245f * s, 35f * s)
        close()
    }
    drawPath(bulldogNose, color = noseColor)

    // Three Authentic Round Porthole Windows with Polished Chrome Bezels
    for (p in 0 until 3) {
        val px = (85f + p * 60f) * s
        drawCircle(color = Color(0xFF94A3B8), radius = 8f * s, center = Offset(px, 48f * s))
        drawCircle(color = Color(0xFF0F172A), radius = 6.5f * s, center = Offset(px, 48f * s))
        drawCircle(color = Color(0xFF38BDF8), radius = 5f * s, center = Offset(px, 48f * s))
    }

    // Classic Stainless Steel Side Fluting
    for (i in 0 until 6) {
        val cy = (64f + i * 3.2f) * s
        drawLine(color = Color(0xFF94A3B8), start = Offset(20f * s, cy), end = Offset(245f * s, cy), strokeWidth = 1.2f * s)
    }

    // Bulldog Curved Windshield with Chrome Wiper
    val windshield = Path().apply {
        moveTo(274f * s, 28f * s)
        lineTo(316f * s, 42f * s)
        lineTo(300f * s, 48f * s)
        lineTo(265f * s, 36f * s)
        close()
    }
    drawPath(windshield, color = Color(0xFF0F172A))
    drawPath(windshield, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

    // Upper Mars Oscillating Safety Signal Light + Lower Main Beam
    drawCircle(color = Color(0xFFFEF08A), radius = 5f * s, center = Offset(344f * s, 54f * s))
    drawCircle(color = Color(0x66FEF08A), radius = 10f * s, center = Offset(344f * s, 54f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 4f * s, center = Offset(344f * s, 68f * s))
}

/**
 * Metra Alstom Coradia Dual-Mode Electro-Diesel Regional Train.
 * Ultra-modern aerodynamic commuter trainset with panoramic glass cockpit
 * and high-efficiency dual-power propulsion.
 */
private fun DrawScope.drawMetraCoradiaDualLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String
) {
    val cabFrontColor = when (variant) {
        "V2_METRA_HYBRID_HYDROGEN" -> Color(0xFF0D9488)
        "V3_METRA_RAPID_TITANIUM" -> Color(0xFF334155)
        else -> Color(0xFF10316B)
    }
    val accentColor = when (variant) {
        "V2_METRA_HYBRID_HYDROGEN" -> Color(0xFF2DD4BF)
        "V3_METRA_RAPID_TITANIUM" -> Color(0xFFEA580C)
        else -> Color(0xFFF95700)
    }

    // High-Speed Dual-Axle Bogies (Wheels uniform 14f * s at y = 108f * s)
    drawModernHighSpeedChassis(285f * s, 75f * s, 110f * s, 245f * s, wheelAngleRad, s, true, true)

    // Aerodynamic Teardrop Nose Body
    val coradiaBody = Path().apply {
        moveTo(14f * s, 86f * s)
        lineTo(14f * s, 24f * s)
        lineTo(250f * s, 22f * s)
        cubicTo(290f * s, 22f * s, 342f * s, 46f * s, 354f * s, 86f * s)
        lineTo(14f * s, 86f * s)
        close()
    }
    drawPath(coradiaBody, color = Color(0xFFF1F5F9))

    // Aerodynamic Cab Wedge
    val cabWedge = Path().apply {
        moveTo(250f * s, 22f * s)
        cubicTo(290f * s, 22f * s, 342f * s, 46f * s, 354f * s, 86f * s)
        lineTo(280f * s, 86f * s)
        lineTo(245f * s, 40f * s)
        close()
    }
    drawPath(cabWedge, color = cabFrontColor)

    // Dynamic Speed Ribbon
    drawLine(color = accentColor, start = Offset(14f * s, 72f * s), end = Offset(332f * s, 72f * s), strokeWidth = 5f * s)

    // Roof Pantograph
    drawRoofPantograph(75f * s, 16f * s, s)

    // Panoramic Windshield
    val windshield = Path().apply {
        moveTo(275f * s, 28f * s)
        lineTo(326f * s, 46f * s)
        lineTo(312f * s, 54f * s)
        lineTo(266f * s, 38f * s)
        close()
    }
    drawPath(windshield, color = Color(0xFF0F172A))
    drawPath(windshield, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

    // Modern LED Headlight Array
    drawCircle(color = Color(0xFFFFFAEE), radius = 4f * s, center = Offset(348f * s, 74f * s))
    drawCircle(color = Color(0xFFFFFAEE), radius = 4f * s, center = Offset(338f * s, 80f * s))
}

/**
 * Metra Bi-Level Gallery Commuter Coach (Nippon Sharyo / Pullman-Standard & Highliner II).
 * Supports unique matching liveries for all 7 Metra locomotives and their variants.
 */
fun DrawScope.drawMetraGalleryCoach(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f,
    variant: String = "V1_METRA_CLASSIC"
) {
    val s = widthPx / 260f
    val isCabCar = car.name.contains("Cab", ignoreCase = true) || car.id.contains("cab", ignoreCase = true)

    val (bodyColor, stripe1, stripe2, stripe3) = when (variant) {
        "V2_METRA_PATRIOT", "V1_METRA_PATRIOT_120" -> QuadColor(Color(0xFFCBD5E1), Color(0xFF1E3A8A), Color(0xFFDC2626), Color(0xFFFFFFFF))
        "V3_METRA_RAVEN", "V3_METRA_RAVEN_120" -> QuadColor(Color(0xFF334155), Color(0xFF0284C7), Color(0xFF38BDF8), Color(0xFF0EA5E9))
        "V2_MP36_ROCK_ISLAND" -> QuadColor(Color(0xFFE2E8F0), Color(0xFF991B1B), Color(0xFF18181B), Color(0xFFFFFFFF))
        "V3_MP36_RTA_RETRO" -> QuadColor(Color(0xFF78350F), Color(0xFFEA580C), Color(0xFFFDE047), Color(0xFFFFFFFF))
        "V2_F59_ILLINOIS_CENTRAL" -> QuadColor(Color(0xFF451A03), Color(0xFFEA580C), Color(0xFFF59E0B), Color(0xFFFFFFFF))
        "V3_F59_MILWAUKEE_ROAD" -> QuadColor(Color(0xFFCBD5E1), Color(0xFFEA580C), Color(0xFF7F1D1D), Color(0xFFFFFFFF))
        "V1_HIGHLINER_STAINLESS", "V2_HIGHLINER_MODERN", "V3_HIGHLINER_IC_ORANGE" -> QuadColor(Color(0xFFE2E8F0), Color(0xFF1E3A8A), Color(0xFF0284C7), Color(0xFFEA580C))
        "V2_CHARGER_LINCOLN" -> QuadColor(Color(0xFFF1F5F9), Color(0xFF1E3A8A), Color(0xFFDC2626), Color(0xFF0284C7))
        "V3_CHARGER_CARBON" -> QuadColor(Color(0xFF1E293B), Color(0xFF06B6D4), Color(0xFF22D3EE), Color(0xFF0891B2))
        "V2_E8_BURLINGTON" -> QuadColor(Color(0xFFE2E8F0), Color(0xFFDC2626), Color(0xFF991B1B), Color(0xFFFFFFFF))
        "V3_E8_CNW400" -> QuadColor(Color(0xFF14532D), Color(0xFFEAB308), Color(0xFFCA8A04), Color(0xFFFFFFFF))
        "V2_CORADIA_SKYLINE" -> QuadColor(Color(0xFF0F172A), Color(0xFF0284C7), Color(0xFFEF4444), Color(0xFF38BDF8))
        "V3_CORADIA_MIDNIGHT" -> QuadColor(Color(0xFF030712), Color(0xFF0D9488), Color(0xFF2DD4BF), Color(0xFF14B8A6))
        else -> QuadColor(Color(0xFFE2E8F0), Color(0xFFEA580C), Color(0xFFFFFFFF), Color(0xFF1E3A8A))
    }

    drawModernHighSpeedChassis(210f * s, 50f * s, 80f * s, 180f * s, wheelAngleRad, s, true, true)

    // Bi-Level Gallery Body
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(15f * s, 18f * s),
        size = Size(230f * s, 68f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Stainless Corrugations
    for (i in 0 until 10) {
        val cy = (42f + i * 2.8f) * s
        drawLine(color = Color(0x33000000), start = Offset(18f * s, cy), end = Offset(242f * s, cy), strokeWidth = 1f * s)
    }

    // Livery Flank Stripes
    drawRect(color = stripe1, topLeft = Offset(15f * s, 70f * s), size = Size(230f * s, 3.5f * s))
    drawRect(color = stripe2, topLeft = Offset(15f * s, 73.5f * s), size = Size(230f * s, 1.5f * s))
    drawRect(color = stripe3, topLeft = Offset(15f * s, 75f * s), size = Size(230f * s, 3.5f * s))

    // Highliner / Electric EMU Roof Pantograph if Highliner variant
    if (variant.contains("HIGHLINER", ignoreCase = true)) {
        drawRoofPantograph(130f * s, 10f * s, s * 0.8f)
    }

    // Central Sliding Boarding Doors
    drawRoundRect(color = Color(0xFF64748B), topLeft = Offset(118f * s, 30f * s), size = Size(24f * s, 54f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRect(color = Color(0xFF0F172A), topLeft = Offset(122f * s, 36f * s), size = Size(16f * s, 24f * s))
    drawCircle(color = Color(0xFFFBBF24), radius = 1.5f * s, center = Offset(130f * s, 64f * s))

    // Upper Gallery Windows (Left & Right of center door)
    for (i in 0 until 3) {
        val wxLeft = (25f + i * 28f) * s
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wxLeft, 24f * s), size = Size(18f * s, 12f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
        drawRoundRect(color = Color(0x99FEF08A), topLeft = Offset(wxLeft + 2f * s, 26f * s), size = Size(14f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

        val wxRight = (150f + i * 28f) * s
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wxRight, 24f * s), size = Size(18f * s, 12f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
        drawRoundRect(color = Color(0x99FEF08A), topLeft = Offset(wxRight + 2f * s, 26f * s), size = Size(14f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    }

    // Lower Gallery Windows (Left & Right of center door)
    for (i in 0 until 3) {
        val wxLeft = (25f + i * 28f) * s
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wxLeft, 52f * s), size = Size(18f * s, 14f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
        drawRoundRect(color = Color(0x99FEF08A), topLeft = Offset(wxLeft + 2f * s, 54f * s), size = Size(14f * s, 10f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

        val wxRight = (150f + i * 28f) * s
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wxRight, 52f * s), size = Size(18f * s, 14f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
        drawRoundRect(color = Color(0x99FEF08A), topLeft = Offset(wxRight + 2f * s, 54f * s), size = Size(14f * s, 10f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    }

    // Cab Car Control Windshield & Taillights/Headlights if Cab Unit
    if (isCabCar) {
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(234f * s, 26f * s), size = Size(10f * s, 16f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
        drawCircle(color = Color(0xFFFF0000), radius = 3f * s, center = Offset(242f * s, 72f * s))
        drawCircle(color = Color(0xFFFF0000), radius = 3f * s, center = Offset(242f * s, 80f * s))
    }
}

private data class QuadColor(val c1: Color, val c2: Color, val c3: Color, val c4: Color)

/**
 * CSX "How Tomorrow Moves" Dark Future Boxcar.
 */
fun DrawScope.drawCsxBoxcar(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val csxBlue = Color(0xFF0C2340)
    val csxYellow = Color(0xFFFFC72C)

    // Heavy Steel Boxcar Body
    drawRoundRect(
        color = csxBlue,
        topLeft = Offset(14f * s, 22f * s),
        size = Size(232f * s, 64f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Corrugated Roof
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(12f * s, 18f * s), size = Size(236f * s, 6f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // Sliding Double Plug Doors in Center
    drawRect(color = Color(0xFF07182D), topLeft = Offset(105f * s, 26f * s), size = Size(50f * s, 56f * s))
    drawRect(color = csxYellow, topLeft = Offset(105f * s, 26f * s), size = Size(50f * s, 56f * s), style = Stroke(width = 1.5f * s))
    drawLine(color = csxYellow, start = Offset(130f * s, 26f * s), end = Offset(130f * s, 82f * s), strokeWidth = 1.5f * s)

    // CSX Big Block Logo Letters
    drawRect(color = csxYellow, topLeft = Offset(32f * s, 36f * s), size = Size(48f * s, 18f * s))
    drawRect(color = csxBlue, topLeft = Offset(42f * s, 40f * s), size = Size(10f * s, 10f * s))
    drawRect(color = csxBlue, topLeft = Offset(58f * s, 40f * s), size = Size(10f * s, 10f * s))

    // Yellow Frame Sill Reflective Striping
    for (i in 0 until 7) {
        val rx = (20f + i * 32f) * s
        drawRect(color = csxYellow, topLeft = Offset(rx, 83f * s), size = Size(16f * s, 3f * s))
    }
}

/**
 * CSX Dark Future Heavy Coal Hopper.
 */
fun DrawScope.drawCsxHopper(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val csxBlue = Color(0xFF0C2340)
    val csxYellow = Color(0xFFFFC72C)

    // Hopper Body
    val hopper = Path().apply {
        moveTo(18f * s, 26f * s)
        lineTo(242f * s, 26f * s)
        lineTo(228f * s, 82f * s)
        lineTo(32f * s, 82f * s)
        close()
    }
    drawPath(hopper, color = csxBlue)

    // Coal Mounds on Top
    val coal = Path().apply {
        moveTo(20f * s, 26f * s)
        lineTo(60f * s, 14f * s)
        lineTo(130f * s, 10f * s)
        lineTo(200f * s, 15f * s)
        lineTo(240f * s, 26f * s)
        close()
    }
    drawPath(coal, color = Color(0xFF111827))

    // Structural Ribs & CSX Herald
    for (i in 0 until 5) {
        val rx = (45f + i * 40f) * s
        drawLine(color = Color(0x44FFFFFF), start = Offset(rx, 26f * s), end = Offset(rx, 80f * s), strokeWidth = 2f * s)
    }
    drawRect(color = csxYellow, topLeft = Offset(110f * s, 42f * s), size = Size(40f * s, 14f * s))
}

/**
 * BNSF Heritage II / Swoop Wedge Boxcar.
 */
fun DrawScope.drawBnsfBoxcar(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val bnsfOrange = Color(0xFFEA580C)
    val bnsfGreen = Color(0xFF14532D)
    val bnsfYellow = Color(0xFFFACC15)

    // Body
    drawRoundRect(color = bnsfOrange, topLeft = Offset(14f * s, 22f * s), size = Size(232f * s, 64f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = bnsfGreen, topLeft = Offset(12f * s, 18f * s), size = Size(236f * s, 6f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // Green & Yellow Lower Sill
    drawRect(color = bnsfGreen, topLeft = Offset(14f * s, 76f * s), size = Size(232f * s, 10f * s))
    drawRect(color = bnsfYellow, topLeft = Offset(14f * s, 74f * s), size = Size(232f * s, 2.5f * s))

    // Center Doors
    drawRect(color = Color(0xFFC2410C), topLeft = Offset(105f * s, 24f * s), size = Size(50f * s, 58f * s))
    drawRect(color = Color.Black, topLeft = Offset(105f * s, 24f * s), size = Size(50f * s, 58f * s), style = Stroke(width = 1.5f * s))

    // BNSF Bold Wedge Logo
    drawRoundRect(color = Color.Black, topLeft = Offset(32f * s, 36f * s), size = Size(54f * s, 18f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = bnsfYellow, topLeft = Offset(34f * s, 38f * s), size = Size(50f * s, 14f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
}

/**
 * BNSF Mineral & Coal Hopper.
 */
fun DrawScope.drawBnsfHopper(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val bnsfRust = Color(0xFF7C2D12)
    val bnsfOrange = Color(0xFFEA580C)

    val hopper = Path().apply {
        moveTo(18f * s, 26f * s)
        lineTo(242f * s, 26f * s)
        lineTo(228f * s, 82f * s)
        lineTo(32f * s, 82f * s)
        close()
    }
    drawPath(hopper, color = bnsfRust)

    // Coal Heap
    val coal = Path().apply {
        moveTo(20f * s, 26f * s)
        lineTo(65f * s, 15f * s)
        lineTo(135f * s, 11f * s)
        lineTo(195f * s, 16f * s)
        lineTo(240f * s, 26f * s)
        close()
    }
    drawPath(coal, color = Color(0xFF111827))

    // BNSF Logo Panel
    drawRoundRect(color = bnsfOrange, topLeft = Offset(105f * s, 38f * s), size = Size(50f * s, 20f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRect(color = Color.Black, topLeft = Offset(110f * s, 42f * s), size = Size(40f * s, 12f * s))
}

/**
 * Burlington Northern Cascade Green Coal Hopper.
 */
fun DrawScope.drawBnCascadeGreenHopper(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val cascadeGreen = Color(0xFF15803D)

    val hopper = Path().apply {
        moveTo(18f * s, 26f * s)
        lineTo(242f * s, 26f * s)
        lineTo(228f * s, 82f * s)
        lineTo(32f * s, 82f * s)
        close()
    }
    drawPath(hopper, color = cascadeGreen)

    val coal = Path().apply {
        moveTo(20f * s, 26f * s)
        lineTo(60f * s, 13f * s)
        lineTo(130f * s, 9f * s)
        lineTo(200f * s, 14f * s)
        lineTo(240f * s, 26f * s)
        close()
    }
    drawPath(coal, color = Color(0xFF111827))

    // White BN Interlocking Logo
    drawRect(color = Color.White, topLeft = Offset(112f * s, 40f * s), size = Size(36f * s, 16f * s))
    drawRect(color = cascadeGreen, topLeft = Offset(120f * s, 44f * s), size = Size(20f * s, 8f * s))
}

/**
 * Burlington Northern Cascade Green Boxcar.
 */
fun DrawScope.drawBnCascadeGreenBoxcar(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val cascadeGreen = Color(0xFF15803D)

    drawRoundRect(color = cascadeGreen, topLeft = Offset(14f * s, 22f * s), size = Size(232f * s, 64f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF18181B), topLeft = Offset(12f * s, 18f * s), size = Size(236f * s, 6f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // White BN Logo
    drawRect(color = Color.White, topLeft = Offset(35f * s, 36f * s), size = Size(46f * s, 20f * s))
    drawRect(color = cascadeGreen, topLeft = Offset(45f * s, 40f * s), size = Size(26f * s, 12f * s))

    // Sliding Doors
    drawRect(color = Color(0xFF166534), topLeft = Offset(105f * s, 24f * s), size = Size(50f * s, 58f * s))
    drawRect(color = Color.White, topLeft = Offset(105f * s, 24f * s), size = Size(50f * s, 58f * s), style = Stroke(width = 1.2f * s))
}

/**
 * Burlington Northern Executive Grinstein Green & Cream Boxcar.
 */
fun DrawScope.drawBnExecutiveBoxcar(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val grinsteinGreen = Color(0xFF064E3B)
    val executiveCream = Color(0xFFFEF3C7)
    val redPinstripe = Color(0xFFDC2626)

    drawRoundRect(color = grinsteinGreen, topLeft = Offset(14f * s, 22f * s), size = Size(232f * s, 64f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRect(color = executiveCream, topLeft = Offset(14f * s, 44f * s), size = Size(232f * s, 20f * s))
    drawRect(color = redPinstripe, topLeft = Offset(14f * s, 42f * s), size = Size(232f * s, 2f * s))
    drawRect(color = redPinstripe, topLeft = Offset(14f * s, 64f * s), size = Size(232f * s, 2f * s))

    // Executive Gold Crest
    drawCircle(color = Color(0xFFCA8A04), radius = 8f * s, center = Offset(130f * s, 54f * s))
}

/**
 * Santa Fe Famous Warbonnet Red & Silver Freight Boxcar.
 */
fun DrawScope.drawSantaFeWarbonnetBoxcar(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val warbonnetRed = Color(0xFFDC2626)
    val warbonnetYellow = Color(0xFFFACC15)
    val stainlessSilver = Color(0xFFE2E8F0)

    drawRoundRect(color = warbonnetRed, topLeft = Offset(14f * s, 22f * s), size = Size(232f * s, 64f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = stainlessSilver, topLeft = Offset(12f * s, 18f * s), size = Size(236f * s, 6f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // Giant Santa Fe Cross Emblem in Center
    drawCircle(color = Color.White, radius = 18f * s, center = Offset(130f * s, 52f * s))
    drawCircle(color = Color(0xFF1E3A8A), radius = 15f * s, center = Offset(130f * s, 52f * s))
    drawRect(color = warbonnetYellow, topLeft = Offset(126f * s, 40f * s), size = Size(8f * s, 24f * s))
    drawRect(color = warbonnetYellow, topLeft = Offset(118f * s, 48f * s), size = Size(24f * s, 8f * s))

    // Warbonnet Yellow Speed Striping
    drawRect(color = warbonnetYellow, topLeft = Offset(14f * s, 76f * s), size = Size(232f * s, 4f * s))
    drawRect(color = Color.Black, topLeft = Offset(14f * s, 80f * s), size = Size(232f * s, 2f * s))
}

/**
 * Santa Fe Bluebonnet Liquid Tanker Car.
 */
fun DrawScope.drawSantaFeBluebonnetTanker(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val bluebonnetBlue = Color(0xFF0284C7)
    val yellowStripe = Color(0xFFFACC15)

    // Heavy Tank Cylinder
    drawRoundRect(color = bluebonnetBlue, topLeft = Offset(20f * s, 28f * s), size = Size(220f * s, 50f * s), cornerRadius = CornerRadius(20f * s, 20f * s))
    drawRect(color = yellowStripe, topLeft = Offset(20f * s, 50f * s), size = Size(220f * s, 6f * s))

    // Dome & Expansion Valve
    drawRoundRect(color = Color(0xFF0369A1), topLeft = Offset(118f * s, 20f * s), size = Size(24f * s, 10f * s), cornerRadius = CornerRadius(3f * s, 3f * s))

    // End Platforms & Safety Railings
    drawRect(color = Color(0xFF334155), topLeft = Offset(10f * s, 76f * s), size = Size(240f * s, 6f * s))
}

/**
 * Union Pacific Armor Yellow Grain & Potash Hopper.
 */
fun DrawScope.drawUnionPacificHopper(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val armorYellow = Color(0xFFFACC15)
    val harborMistGray = Color(0xFF475569)
    val upRed = Color(0xFFDC2626)

    val hopper = Path().apply {
        moveTo(18f * s, 26f * s)
        lineTo(242f * s, 26f * s)
        lineTo(228f * s, 82f * s)
        lineTo(32f * s, 82f * s)
        close()
    }
    drawPath(hopper, color = armorYellow)
    drawRoundRect(color = harborMistGray, topLeft = Offset(16f * s, 22f * s), size = Size(228f * s, 6f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // UP Shield Emblem
    drawRoundRect(color = upRed, topLeft = Offset(116f * s, 42f * s), size = Size(28f * s, 24f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRect(color = Color.White, topLeft = Offset(118f * s, 44f * s), size = Size(24f * s, 8f * s))
    drawRect(color = Color(0xFF1E3A8A), topLeft = Offset(118f * s, 52f * s), size = Size(24f * s, 12f * s))
}

/**
 * Norfolk Southern Thoroughbred Heavy Coal Hopper.
 */
fun DrawScope.drawNorfolkSouthernHopper(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f
    val nsBlack = Color(0xFF0F172A)

    val hopper = Path().apply {
        moveTo(18f * s, 26f * s)
        lineTo(242f * s, 26f * s)
        lineTo(228f * s, 82f * s)
        lineTo(32f * s, 82f * s)
        close()
    }
    drawPath(hopper, color = nsBlack)

    // Coal Heap
    val coal = Path().apply {
        moveTo(20f * s, 26f * s)
        lineTo(65f * s, 14f * s)
        lineTo(135f * s, 10f * s)
        lineTo(200f * s, 15f * s)
        lineTo(240f * s, 26f * s)
        close()
    }
    drawPath(coal, color = Color(0xFF030712))

    // White NS Stallion Logo
    drawRect(color = Color.White, topLeft = Offset(112f * s, 42f * s), size = Size(36f * s, 16f * s))
    drawRect(color = nsBlack, topLeft = Offset(120f * s, 46f * s), size = Size(20f * s, 8f * s))

    // Yellow Sill Reflectors
    for (i in 0 until 6) {
        val rx = (25f + i * 36f) * s
        drawRect(color = Color(0xFFFACC15), topLeft = Offset(rx, 80f * s), size = Size(12f * s, 2.5f * s))
    }
}

/**
 * 100% Literally Accurate GE AC4400CW Heavy Freight Diesel-Electric Locomotive.
 * Features:
 * - Steerable 3-axle HiAd roller bearing bogies (6 flanged wheels, equalizer bars, brake rigging)
 * - Massive 5,000-gal belly fuel tank with sight glass and overhead dual air brake reservoirs
 * - North American Safety Comfort Cab with teardrop windshields, sun visors, and high roof headlights
 * - Illuminated numberboards, pilot ditch lights, and heavy front snowplow with MU cables
 * - AC traction inverter cabinet bulge behind cab
 * - GE 7FDL-16 engine compartment with full side access doors and exhaust stack
 * - Signature flared rear "bathtub" wing radiators with dual circular roof cooling fan mesh
 * - Full-length safety walkways with yellow handrails and stanchions
 * - 7 Authentic Liveries: CSX YN3, BNSF Heritage II, BNSF Warbonnet Red, BN Cascade Green, NS Thoroughbred, Santa Fe Super Fleet, UP Armor Yellow
 */
fun DrawScope.drawGeAc4400cwLocomotive(
    train: TrainModel,
    bodyColor: Color,
    roofColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String = "V1_CSX_YN3"
) {
    // 1. Heavy Underframe Sill
    drawRoundRect(
        color = Color(0xFF1E242B),
        topLeft = Offset(8f * s, 82f * s),
        size = Size(344f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Reflective frame sill stripe (Yellow on CSX/BNSF/UP/BN, White on NS, Red on UP)
    val sillColor = when (variant) {
        "V5_NS_HORSEHEAD" -> Color.White
        "V7_UP_BUILDING_AMERICA" -> Color(0xFFDC2626)
        else -> Color(0xFFFACC15)
    }
    drawRect(
        color = sillColor,
        topLeft = Offset(10f * s, 88f * s),
        size = Size(340f * s, 2.5f * s)
    )

    // 2. Center 5,000 Gallon Belly Fuel Tank & Dual Air Reservoirs
    drawRoundRect(
        color = Color(0xFF0F1216),
        topLeft = Offset(115f * s, 90f * s),
        size = Size(130f * s, 22f * s),
        cornerRadius = CornerRadius(6f * s, 6f * s)
    )
    // Fuel Sight Glass & Cutoff Box
    drawRect(color = Color(0xFF38BDF8), topLeft = Offset(175f * s, 98f * s), size = Size(10f * s, 5f * s))
    drawCircle(color = Color(0xFFDC2626), radius = 2.5f * s, center = Offset(130f * s, 96f * s))
    drawCircle(color = Color(0xFFFACC15), radius = 2.5f * s, center = Offset(225f * s, 96f * s))

    // Overhead twin air brake reservoirs
    drawRoundRect(color = Color(0xFF334155), topLeft = Offset(125f * s, 84f * s), size = Size(50f * s, 6f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF334155), topLeft = Offset(185f * s, 84f * s), size = Size(50f * s, 6f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // 3. Front & Rear Heavy Pilots & Snowplows
    // Front Snowplow (Right side)
    val frontPlow = Path().apply {
        moveTo(344f * s, 82f * s)
        lineTo(354f * s, 98f * s)
        lineTo(336f * s, 98f * s)
        lineTo(334f * s, 82f * s)
        close()
    }
    drawPath(frontPlow, color = Color(0xFF1E242B))

    // Couplers
    drawRect(color = Color(0xFF111827), topLeft = Offset(0f * s, 84f * s), size = Size(10f * s, 6f * s))
    drawRect(color = Color(0xFF111827), topLeft = Offset(350f * s, 84f * s), size = Size(10f * s, 6f * s))

    // 4. North American Wide Safety Comfort Cab & Low Short Nose
    // Low Front Nose (Short Hood)
    val noseFront = Path().apply {
        moveTo(290f * s, 82f * s)
        lineTo(340f * s, 82f * s)
        lineTo(340f * s, 48f * s)
        lineTo(290f * s, 44f * s)
        close()
    }
    val noseColor = when (variant) {
        "V1_CSX_YN3" -> Color(0xFFFACC15) // CSX Boxcar Yellow Nose
        "V4_BN_CASCADE_GREEN" -> Color(0xFFFFFFFF) // BN Stark White Nose Face
        "V3_BNSF_WARBONNET_RED", "V6_SANTA_FE_SUPERFLEET" -> Color(0xFFDC2626) // Warbonnet Red
        "V5_NS_HORSEHEAD" -> Color(0xFF09090B) // NS Jet Black
        else -> bodyColor
    }
    drawPath(noseFront, color = noseColor)

    // Wide Cab Structure
    val cab = Path().apply {
        moveTo(235f * s, 82f * s)
        lineTo(295f * s, 82f * s)
        lineTo(295f * s, 22f * s)
        lineTo(240f * s, 22f * s)
        close()
    }
    drawPath(cab, color = bodyColor)

    // Cab Roof (Anti-glare dark green on BNSF, Harbor Mist gray on UP, Dark blue on CSX)
    drawRoundRect(
        color = roofColor,
        topLeft = Offset(236f * s, 16f * s),
        size = Size(62f * s, 8f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Roof HVAC Air Conditioner Box (Left of cab roof)
    drawRoundRect(
        color = Color(0xFF475569),
        topLeft = Offset(245f * s, 11f * s),
        size = Size(20f * s, 6f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )

    // 5-Chime Nathan K5LLA Air Horn
    drawLine(color = Color(0xFFFACC15), start = Offset(280f * s, 16f * s), end = Offset(272f * s, 9f * s), strokeWidth = 2.5f * s)
    drawLine(color = Color(0xFFFACC15), start = Offset(280f * s, 14f * s), end = Offset(288f * s, 10f * s), strokeWidth = 2f * s)

    // 5. Teardrop Windshields & Side Cab Windows
    // Angled Front Windshields
    val windshieldLeft = Path().apply {
        moveTo(275f * s, 26f * s)
        lineTo(292f * s, 26f * s)
        lineTo(292f * s, 42f * s)
        lineTo(275f * s, 42f * s)
        close()
    }
    drawPath(windshieldLeft, color = Color(0xFF0F172A))
    drawPath(windshieldLeft, color = Color(0xFF38BDF8), style = Stroke(width = 1.2f * s))

    // Side Sliding Windows
    drawRoundRect(
        color = Color(0xFF38BDF8),
        topLeft = Offset(245f * s, 28f * s),
        size = Size(22f * s, 14f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    drawLine(color = Color(0xFF0F172A), start = Offset(256f * s, 28f * s), end = Offset(256f * s, 42f * s), strokeWidth = 1.5f * s)

    // Illuminated Numberboards on Cab Brow
    drawRoundRect(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(288f * s, 18f * s),
        size = Size(10f * s, 5f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )

    // Recessed High-Intensity Twin Headlights
    drawCircle(color = Color(0xFFFFFBEB), radius = 2.5f * s, center = Offset(294f * s, 15f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 4f * s, center = Offset(294f * s, 15f * s), style = Stroke(width = 1f * s))

    // Front Ditch Lights on Walkway Step Deck
    drawCircle(color = Color(0xFFFFFBEB), radius = 2.2f * s, center = Offset(338f * s, 74f * s))
    drawCircle(color = Color(0xFFFFFBEB), radius = 2.2f * s, center = Offset(343f * s, 74f * s))

    // 6. AC Traction Inverter Cabinet (Large raised compartment behind cab)
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(195f * s, 24f * s),
        size = Size(42f * s, 58f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Horizontal Inverter Cooling Louvers
    for (i in 0 until 5) {
        val ly = (32f + i * 8f) * s
        drawRect(color = Color(0xFF0F172A), topLeft = Offset(202f * s, ly), size = Size(28f * s, 4f * s))
    }

    // 7. Long Hood: GE 7FDL-16 Diesel Engine Compartment & Access Doors
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(65f * s, 26f * s),
        size = Size(132f * s, 56f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Side Access Doors & Latches
    for (d in 0 until 6) {
        val dx = (70f + d * 20f) * s
        drawRect(color = Color(0x33000000), topLeft = Offset(dx, 36f * s), size = Size(16f * s, 42f * s), style = Stroke(width = 1f * s))
        drawCircle(color = Color(0xFF475569), radius = 1.2f * s, center = Offset(dx + 13f * s, 56f * s))
    }

    // Dynamic Brake Intake Grille Blister
    drawRoundRect(
        color = roofColor,
        topLeft = Offset(155f * s, 20f * s),
        size = Size(38f * s, 8f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Exhaust Stack Manifold
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(135f * s, 14f * s),
        size = Size(16f * s, 12f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // 8. SIGNATURE GE "WINGED" FLARED RADIATOR SECTION (The unmistakable AC4400CW Bathtub)
    val flaredRadiator = Path().apply {
        moveTo(14f * s, 82f * s)
        lineTo(68f * s, 82f * s)
        lineTo(68f * s, 20f * s)
        lineTo(12f * s, 20f * s)
        lineTo(10f * s, 36f * s)
        close()
    }
    drawPath(flaredRadiator, color = bodyColor)

    // Radiator Flared Overhang & Air Intake Mesh
    val radiatorWing = Path().apply {
        moveTo(10f * s, 20f * s)
        lineTo(68f * s, 20f * s)
        lineTo(64f * s, 38f * s)
        lineTo(12f * s, 38f * s)
        close()
    }
    drawPath(radiatorWing, color = Color(0xFF0F172A))
    // Diagonal Radiator Grill Mesh
    for (g in 0 until 5) {
        val gx = (16f + g * 10f) * s
        drawLine(color = Color(0xFF334155), start = Offset(gx, 22f * s), end = Offset(gx + 6f * s, 36f * s), strokeWidth = 1.5f * s)
    }

    // Top Roof Cooling Fans (Two massive circular fans)
    drawOval(color = Color(0xFF334155), topLeft = Offset(16f * s, 16f * s), size = Size(20f * s, 6f * s))
    drawOval(color = Color(0xFF334155), topLeft = Offset(42f * s, 16f * s), size = Size(20f * s, 6f * s))

    // 9. LIVERY-SPECIFIC GRAPHICS & HERALDS
    when (variant) {
        "V1_CSX_YN3" -> {
            // Bold Yellow "CSX" on dark blue hood
            drawRect(color = Color(0xFFFACC15), topLeft = Offset(105f * s, 42f * s), size = Size(46f * s, 18f * s))
            drawRect(color = bodyColor, topLeft = Offset(118f * s, 46f * s), size = Size(8f * s, 10f * s))
            drawRect(color = bodyColor, topLeft = Offset(134f * s, 46f * s), size = Size(8f * s, 10f * s))
            // CSX Boxcar Yellow Sill Stripe
            drawRect(color = Color(0xFFFACC15), topLeft = Offset(20f * s, 76f * s), size = Size(310f * s, 4f * s))
        }
        "V2_BNSF_HERITAGE2" -> {
            // Dark Green Anti-glare Cab Top & Nose
            drawRect(color = Color(0xFF14532D), topLeft = Offset(290f * s, 44f * s), size = Size(50f * s, 8f * s))
            // Yellow BNSF Pinstripes
            drawRect(color = Color(0xFFFACC15), topLeft = Offset(65f * s, 26f * s), size = Size(172f * s, 3f * s))
            drawRect(color = Color(0xFFFACC15), topLeft = Offset(65f * s, 76f * s), size = Size(172f * s, 3f * s))
            // BNSF Bold Yellow Lettering on Hood
            drawRect(color = Color(0xFFFACC15), topLeft = Offset(105f * s, 44f * s), size = Size(48f * s, 16f * s))
            drawRect(color = bodyColor, topLeft = Offset(118f * s, 48f * s), size = Size(6f * s, 8f * s))
            drawRect(color = bodyColor, topLeft = Offset(132f * s, 48f * s), size = Size(6f * s, 8f * s))
        }
        "V3_BNSF_WARBONNET_RED", "V6_SANTA_FE_SUPERFLEET" -> {
            // Sweeping Warbonnet Red Bonnet with Yellow Speed Whisker
            val warbonnet = Path().apply {
                moveTo(340f * s, 82f * s)
                lineTo(340f * s, 48f * s)
                lineTo(290f * s, 44f * s)
                lineTo(240f * s, 82f * s)
                close()
            }
            drawPath(warbonnet, color = Color(0xFFDC2626))
            drawLine(color = Color(0xFFFACC15), start = Offset(290f * s, 44f * s), end = Offset(240f * s, 82f * s), strokeWidth = 3f * s)
            // BNSF / Santa Fe Red lettering on Silver
            drawRect(color = Color(0xFFDC2626), topLeft = Offset(105f * s, 46f * s), size = Size(46f * s, 16f * s))
            drawRect(color = bodyColor, topLeft = Offset(118f * s, 50f * s), size = Size(6f * s, 8f * s))
            drawRect(color = bodyColor, topLeft = Offset(132f * s, 50f * s), size = Size(6f * s, 8f * s))
        }
        "V4_BN_CASCADE_GREEN" -> {
            // Interlocking White BN Herald
            drawRect(color = Color.White, topLeft = Offset(115f * s, 42f * s), size = Size(36f * s, 20f * s))
            drawRect(color = bodyColor, topLeft = Offset(122f * s, 47f * s), size = Size(10f * s, 10f * s))
            drawRect(color = bodyColor, topLeft = Offset(136f * s, 47f * s), size = Size(10f * s, 10f * s))
        }
        "V5_NS_HORSEHEAD" -> {
            // White Stallion Horsehead & NS Herald
            drawRect(color = Color.White, topLeft = Offset(110f * s, 44f * s), size = Size(44f * s, 16f * s))
            drawRect(color = Color(0xFF09090B), topLeft = Offset(122f * s, 48f * s), size = Size(8f * s, 8f * s))
            drawRect(color = Color(0xFF09090B), topLeft = Offset(136f * s, 48f * s), size = Size(8f * s, 8f * s))
            // White rearing stallion silhouette on cab
            drawCircle(color = Color.White, radius = 4f * s, center = Offset(265f * s, 56f * s))
        }
        "V7_UP_BUILDING_AMERICA" -> {
            // UP Red Sill & Shield
            drawRect(color = Color(0xFFDC2626), topLeft = Offset(15f * s, 76f * s), size = Size(320f * s, 3.5f * s))
            drawRoundRect(color = Color(0xFFDC2626), topLeft = Offset(118f * s, 42f * s), size = Size(26f * s, 22f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
            drawRect(color = Color.White, topLeft = Offset(120f * s, 44f * s), size = Size(22f * s, 6f * s))
            drawRect(color = Color(0xFF1E3A8A), topLeft = Offset(120f * s, 50f * s), size = Size(22f * s, 12f * s))
        }
        else -> {
            drawRect(color = stripeColor, topLeft = Offset(15f * s, 74f * s), size = Size(320f * s, 4f * s))
        }
    }

    // 10. Yellow Walkway Safety Handrails & Vertical Stanchions
    val handrailColor = when (variant) {
        "V5_NS_HORSEHEAD" -> Color.White
        else -> Color(0xFFFACC15)
    }
    drawLine(color = handrailColor, start = Offset(10f * s, 62f * s), end = Offset(346f * s, 62f * s), strokeWidth = 2.2f * s)
    for (i in 0 until 12) {
        val sx = (14f + i * 30f) * s
        drawLine(color = handrailColor, start = Offset(sx, 62f * s), end = Offset(sx, 82f * s), strokeWidth = 1.8f * s)
    }

    // 11. Steerable 3-Axle HiAd Bogies (6 flanged wheels total)
    drawThreeAxleTruck(72f * s, 100f * s, wheelAngleRad, s)
    drawThreeAxleTruck(285f * s, 100f * s, wheelAngleRad, s)
}

/**
 * High Adhesion 3-Axle Roller Bearing Bogie Truck (GE HiAd / EMD HTCR Radial Truck).
 * Features:
 * - Heavy cast steel side frame and curved equalizer drop-beams
 * - Three roller bearing axles with standardized 14f * s wheels contacting rails strictly at 122f * s
 * - Clasp brake rigging shoes on all wheelsets
 * - Dual flexible rubber sand hoses with brass nozzles feeding quartz sand to leading and trailing wheels
 */
fun DrawScope.drawThreeAxleTruck(
    centerX: Float,
    centerY: Float,
    wheelAngleRad: Float,
    s: Float
) {
    val truckFrame = Color(0xFF1E242B)
    val steelFlange = Color(0xFFCBD5E1)
    val springSteel = Color(0xFF475569)
    val sandHoseRubber = Color(0xFF0F172A)
    val sandNozzleBrass = Color(0xFFD97706)
    val brakeShoeColor = Color(0xFF334155)

    // Standardized wheel center line: strictly at 108f * s (radius 14f * s => rail contact at 122f * s)
    val wheelY = 108f * s
    val r = 14f * s

    // Heavy Cast Side Frame & Equalizer Beam
    val framePath = Path().apply {
        moveTo(centerX - 52f * s, wheelY - 14f * s)
        lineTo(centerX + 52f * s, wheelY - 14f * s)
        lineTo(centerX + 54f * s, wheelY - 4f * s)
        lineTo(centerX + 48f * s, wheelY + 3f * s)
        lineTo(centerX + 20f * s, wheelY + 3f * s)
        lineTo(centerX + 16f * s, wheelY - 4f * s)
        lineTo(centerX - 16f * s, wheelY - 4f * s)
        lineTo(centerX - 20f * s, wheelY + 3f * s)
        lineTo(centerX - 48f * s, wheelY + 3f * s)
        lineTo(centerX - 54f * s, wheelY - 4f * s)
        close()
    }
    drawPath(framePath, color = truckFrame)

    // Three Flanged Wheels with Rotating Axle Spokes (Standard 14f radius touching rail at 122f * s)
    listOf(-32f * s, 0f, 32f * s).forEach { wx ->
        val wheelX = centerX + wx

        // Outer Steel Tire & Tread
        drawCircle(color = Color(0xFF0F1216), radius = r + 1.5f * s, center = Offset(wheelX, wheelY))
        drawCircle(color = steelFlange, radius = r, center = Offset(wheelX, wheelY), style = Stroke(width = 1.5f * s))
        drawCircle(color = Color(0xFF334155), radius = r - 3f * s, center = Offset(wheelX, wheelY))

        // Center Roller Bearing Hub with Timken / Hyatt End Cap
        drawCircle(color = Color(0xFF0F1216), radius = 4f * s, center = Offset(wheelX, wheelY))
        drawCircle(color = Color(0xFF94A3B8), radius = 2.5f * s, center = Offset(wheelX, wheelY))

        // Rotating Spoke indicator
        val spkX = kotlin.math.cos(wheelAngleRad) * (5f * s)
        val spkY = kotlin.math.sin(wheelAngleRad) * (5f * s)
        drawLine(
            color = steelFlange,
            start = Offset(wheelX - spkX.toFloat(), wheelY - spkY.toFloat()),
            end = Offset(wheelX + spkX.toFloat(), wheelY + spkY.toFloat()),
            strokeWidth = 1.5f * s
        )

        // Clasp brake shoe head
        val isLeft = wx < 0f
        val shoeX = if (isLeft) wheelX - 13f * s else wheelX + 11f * s
        drawRoundRect(
            color = brakeShoeColor,
            topLeft = Offset(shoeX, wheelY - 4f * s),
            size = Size(3f * s, 9f * s),
            cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
        )
    }

    // Brake Rigging Linkage & Hydraulic Dampers
    drawRect(color = springSteel, topLeft = Offset(centerX - 46f * s, wheelY - 8f * s), size = Size(92f * s, 2.5f * s))
    drawCircle(color = Color(0xFFDC2626), radius = 2.2f * s, center = Offset(centerX - 16f * s, wheelY - 7f * s))
    drawCircle(color = Color(0xFFDC2626), radius = 2.2f * s, center = Offset(centerX + 16f * s, wheelY - 7f * s))

    // American Heavy Freight Sand Hoses & Brass Spray Nozzles (delivering sand directly to outer wheels at 120f * s)
    // Left Front Sand Hose
    val leftHose = Path().apply {
        moveTo(centerX - 36f * s, wheelY - 10f * s)
        cubicTo(
            centerX - 46f * s, wheelY - 2f * s,
            centerX - 48f * s, wheelY + 6f * s,
            centerX - 46f * s, 120f * s
        )
    }
    drawPath(leftHose, color = sandHoseRubber, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
    drawRoundRect(
        color = sandNozzleBrass,
        topLeft = Offset(centerX - 48f * s, 118f * s),
        size = Size(3.5f * s, 3f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )

    // Right Rear Sand Hose
    val rightHose = Path().apply {
        moveTo(centerX + 36f * s, wheelY - 10f * s)
        cubicTo(
            centerX + 46f * s, wheelY - 2f * s,
            centerX + 48f * s, wheelY + 6f * s,
            centerX + 46f * s, 120f * s
        )
    }
    drawPath(rightHose, color = sandHoseRubber, style = Stroke(width = 2.2f * s, cap = StrokeCap.Round))
    drawRoundRect(
        color = sandNozzleBrass,
        topLeft = Offset(centerX + 44.5f * s, 118f * s),
        size = Size(3.5f * s, 3f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )
}

/**
 * EMD SD70ACe Heavy Freight Locomotive.
 */
fun DrawScope.drawEmdSd70aceLocomotive(
    train: TrainModel,
    bodyColor: Color,
    roofColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String = "V1_BNSF_SWOOP"
) {
    // Underframe Sill & Tank
    drawRoundRect(color = Color(0xFF1E242B), topLeft = Offset(8f * s, 82f * s), size = Size(344f * s, 10f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF0F1216), topLeft = Offset(115f * s, 90f * s), size = Size(130f * s, 22f * s), cornerRadius = CornerRadius(6f * s, 6f * s))

    // Long Hood Body
    drawRoundRect(color = bodyColor, topLeft = Offset(20f * s, 26f * s), size = Size(220f * s, 56f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Isolated Cab (EMD Teardrop Whisker Cab)
    val cab = Path().apply {
        moveTo(240f * s, 82f * s)
        lineTo(296f * s, 82f * s)
        lineTo(296f * s, 20f * s)
        lineTo(240f * s, 20f * s)
        close()
    }
    drawPath(cab, color = bodyColor)
    drawRoundRect(color = roofColor, topLeft = Offset(238f * s, 14f * s), size = Size(60f * s, 8f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Short Hood Front
    drawRoundRect(color = if (variant == "V3_BN_CASCADE_GREEN") Color.White else bodyColor, topLeft = Offset(296f * s, 46f * s), size = Size(48f * s, 36f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Teardrop Windshield
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(266f * s, 24f * s), size = Size(26f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawCircle(color = Color(0xFFFFFBEB), radius = 2.5f * s, center = Offset(296f * s, 14f * s))

    // EMD Flared Rear Radiator Hood
    drawRoundRect(color = roofColor, topLeft = Offset(20f * s, 18f * s), size = Size(50f * s, 10f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Yellow Handrails
    drawLine(color = Color(0xFFFACC15), start = Offset(10f * s, 62f * s), end = Offset(346f * s, 62f * s), strokeWidth = 2f * s)

    // Couplers
    drawRect(color = Color(0xFF111827), topLeft = Offset(0f * s, 84f * s), size = Size(10f * s, 6f * s))
    drawRect(color = Color(0xFF111827), topLeft = Offset(350f * s, 84f * s), size = Size(10f * s, 6f * s))

    // 3-Axle HTCR Radial Trucks
    drawThreeAxleTruck(72f * s, 100f * s, wheelAngleRad, s)
    drawThreeAxleTruck(285f * s, 100f * s, wheelAngleRad, s)
}
