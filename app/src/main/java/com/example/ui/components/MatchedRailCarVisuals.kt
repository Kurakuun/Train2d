package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.model.RailCar
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-fidelity matched railcars designed specifically for:
 * - Chicago Metra Bi-Level Gallery Commuter Trains (Gallery Coaches & Cab Cars)
 * - České Dráhy (ČD) SuperCity Pendolino (Class 680 Intermediate & Tail Cab Trailer)
 * - České Dráhy (ČD) ComfortJet & EuroCity Coaches
 * - České Dráhy (ČD) Regio Regional Low-Floor Coaches
 * - Amtrak Acela Express High-Speed Coaches & Trailing Power Units
 * - Historic Heavyweight Pullman Parlor & Observation Coaches
 */

/**
 * České Dráhy (ČD) Class 680 Pendolino Tilting Intermediate Coach
 */
fun DrawScope.drawCdPendolinoCoach(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f

    // High speed chassis with streamlined side skirts
    drawModernHighSpeedChassis(210f * s, 50f * s, 82f * s, 180f * s, wheelAngleRad, s, hasFrontCoupler = true, hasRearCoupler = true)

    // Aerodynamic Tilting Body Shell in ČD Najbrt Livery
    val cdNavy = Color(0xFF1E3A8A)
    val cdSky = Color(0xFF0284C7)
    val cdWhite = Color(0xFFF8FAFC)
    val cdGray = Color(0xFF64748B)

    // Main Sky Blue Body
    drawRoundRect(
        color = cdSky,
        topLeft = Offset(14f * s, 26f * s),
        size = Size(232f * s, 60f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )

    // Deep Sapphire Blue Roof Fairing
    drawRoundRect(
        color = cdNavy,
        topLeft = Offset(12f * s, 20f * s),
        size = Size(236f * s, 12f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )

    // Crisp White Najbrt Separation Stripe
    drawRect(color = cdWhite, topLeft = Offset(14f * s, 32f * s), size = Size(232f * s, 3.5f * s))

    // Lower Dark Charcoal Skirt
    drawRoundRect(
        color = cdGray,
        topLeft = Offset(14f * s, 76f * s),
        size = Size(232f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // EuroCity Tinted Panoramic Continuous Window Ribbon
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(24f * s, 38f * s),
        size = Size(212f * s, 22f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Individual Window Panes with Warm Interior Ambiance
    val isBistro = car.name.contains("Bistro", ignoreCase = true) || car.id.contains("p2")
    for (i in 0 until 8) {
        val wx = (28f + i * 25.5f) * s
        drawRoundRect(
            color = if (isBistro && (i == 3 || i == 4)) Color(0xFFE2E8F0) else Color(0x99FEF08A),
            topLeft = Offset(wx, 41f * s),
            size = Size(21f * s, 16f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
    }

    // ČD Logo Badge
    drawRoundRect(color = cdNavy, topLeft = Offset(18f * s, 66f * s), size = Size(14f * s, 7f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    drawRoundRect(color = cdWhite, topLeft = Offset(19f * s, 67.5f * s), size = Size(12f * s, 4f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // First Class / Bistro Markings
    if (isBistro) {
        drawRoundRect(color = Color(0xFFDC2626), topLeft = Offset(118f * s, 65f * s), size = Size(24f * s, 8f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    } else {
        drawRoundRect(color = Color(0xFFFBBF24), topLeft = Offset(36f * s, 28f * s), size = Size(16f * s, 3.5f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    }
}

/**
 * České Dráhy (ČD) Class 680 Pendolino Trailing Aerodynamic Cab Unit
 */
fun DrawScope.drawCdPendolinoRearCab(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f

    val cdNavy = Color(0xFF1E3A8A)
    val cdSky = Color(0xFF0284C7)
    val cdWhite = Color(0xFFF8FAFC)
    val cdCharcoal = Color(0xFF334155)

    // High speed chassis (gangway only on rear-right side)
    drawModernHighSpeedChassis(210f * s, 50f * s, 82f * s, 180f * s, wheelAngleRad, s, hasFrontCoupler = true, hasRearCoupler = false)

    // Aerodynamic Wedge Bullet Cab pointing Left (Reverse driving trailer)
    val cabPath = Path().apply {
        moveTo(246f * s, 20f * s)
        lineTo(70f * s, 20f * s)
        cubicTo(45f * s, 20f * s, 20f * s, 42f * s, 8f * s, 72f * s)
        lineTo(8f * s, 86f * s)
        lineTo(246f * s, 86f * s)
        close()
    }
    drawPath(cabPath, color = cdSky)

    // Aerodynamic Dark Blue Roof Band
    val roofPath = Path().apply {
        moveTo(246f * s, 20f * s)
        lineTo(70f * s, 20f * s)
        cubicTo(50f * s, 20f * s, 35f * s, 28f * s, 22f * s, 40f * s)
        lineTo(246f * s, 32f * s)
        close()
    }
    drawPath(roofPath, color = cdNavy)

    // White Pinstripe across cab flank
    drawLine(color = cdWhite, start = Offset(16f * s, 68f * s), end = Offset(246f * s, 68f * s), strokeWidth = 3f * s)

    // Aerodynamic Streamlined Cockpit Windshield
    val windshield = Path().apply {
        moveTo(58f * s, 26f * s)
        lineTo(28f * s, 48f * s)
        lineTo(32f * s, 60f * s)
        lineTo(64f * s, 40f * s)
        close()
    }
    drawPath(windshield, color = Color(0xFF0F172A))
    drawPath(windshield, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

    // Passenger Tinted Window Ribbon on cab body
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(78f * s, 38f * s),
        size = Size(160f * s, 22f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    for (i in 0 until 6) {
        val wx = (84f + i * 25.5f) * s
        drawRoundRect(
            color = Color(0x99FEF08A),
            topLeft = Offset(wx, 41f * s),
            size = Size(20f * s, 16f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
    }

    // Rear Red High-Speed Taillights
    drawCircle(color = Color(0xFFDC2626), radius = 4f * s, center = Offset(14f * s, 76f * s))
    drawCircle(color = Color(0xFFEF4444), radius = 2.5f * s, center = Offset(14f * s, 76f * s))
    drawCircle(color = Color(0xFFDC2626), radius = 4f * s, center = Offset(22f * s, 78f * s))

    // Lower Front Air Scoop
    drawRoundRect(color = cdCharcoal, topLeft = Offset(6f * s, 84f * s), size = Size(36f * s, 6f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
}

/**
 * České Dráhy (ČD) ComfortJet & EuroCity First/Second Class Coach
 */
fun DrawScope.drawCdComfortJetCoach(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f

    drawModernHighSpeedChassis(210f * s, 50f * s, 82f * s, 180f * s, wheelAngleRad, s, hasFrontCoupler = true, hasRearCoupler = true)

    val cdNavy = Color(0xFF1E3A8A)
    val cdSky = Color(0xFF0284C7)
    val cdCyan = Color(0xFF06B6D4)
    val cdSilver = Color(0xFFF1F5F9)
    val isFirstClass = car.name.contains("First", ignoreCase = true)

    // Main Body: Modern EuroCity Light Silver-Blue
    drawRoundRect(
        color = cdSilver,
        topLeft = Offset(14f * s, 22f * s),
        size = Size(232f * s, 64f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Deep Sapphire Blue Lower Flank Band
    drawRoundRect(
        color = cdSky,
        topLeft = Offset(14f * s, 58f * s),
        size = Size(232f * s, 24f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Dark Navy Aerodynamic Roof Fairing
    drawRoundRect(
        color = cdNavy,
        topLeft = Offset(12f * s, 18f * s),
        size = Size(236f * s, 12f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Cyan High-Speed Pinstripe
    drawRect(color = cdCyan, topLeft = Offset(14f * s, 56f * s), size = Size(232f * s, 2.5f * s))

    // First Class Yellow Roof Stripe if 1st Class
    if (isFirstClass) {
        drawRect(color = Color(0xFFFBBF24), topLeft = Offset(14f * s, 22f * s), size = Size(232f * s, 3.5f * s))
    }

    // Flush EuroCity Window Ribbon
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(24f * s, 28f * s),
        size = Size(212f * s, 26f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    for (i in 0 until 8) {
        val wx = (28f + i * 25.5f) * s
        drawRoundRect(
            color = Color(0x99FEF08A),
            topLeft = Offset(wx, 32f * s),
            size = Size(21f * s, 18f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
    }

    // Pressurized Automatic Passenger Sliding Doors at Car Ends
    drawRoundRect(color = cdNavy, topLeft = Offset(16f * s, 28f * s), size = Size(7f * s, 52f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawRoundRect(color = cdNavy, topLeft = Offset(237f * s, 28f * s), size = Size(7f * s, 52f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // Class Number Marking (1 or 2)
    val classBadgeColor = if (isFirstClass) Color(0xFFFBBF24) else Color(0xFF38BDF8)
    drawRoundRect(color = classBadgeColor, topLeft = Offset(26f * s, 62f * s), size = Size(9f * s, 12f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    drawRoundRect(color = classBadgeColor, topLeft = Offset(225f * s, 62f * s), size = Size(9f * s, 12f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
}

/**
 * České Dráhy (ČD) Regio Low-Floor Coach (RegioShark / RegioPanter)
 */
fun DrawScope.drawCdRegioCoach(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f

    drawModernHighSpeedChassis(210f * s, 50f * s, 82f * s, 180f * s, wheelAngleRad, s, hasFrontCoupler = true, hasRearCoupler = true)

    val cdNavy = Color(0xFF1E3A8A)
    val cdSky = Color(0xFF0284C7)
    val cdWhite = Color(0xFFFFFFFF)

    // Body Shell
    drawRoundRect(
        color = cdSky,
        topLeft = Offset(14f * s, 24f * s),
        size = Size(232f * s, 62f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Navy Lower Band & Roof
    drawRoundRect(color = cdNavy, topLeft = Offset(12f * s, 18f * s), size = Size(236f * s, 10f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRect(color = cdNavy, topLeft = Offset(14f * s, 74f * s), size = Size(232f * s, 12f * s))
    drawRect(color = cdWhite, topLeft = Offset(14f * s, 72f * s), size = Size(232f * s, 2.5f * s))

    // Wide Center Low-Floor Double Boarding Doors
    drawRoundRect(color = Color(0xFFE2E8F0), topLeft = Offset(114f * s, 32f * s), size = Size(32f * s, 52f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(118f * s, 38f * s), size = Size(10f * s, 26f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(132f * s, 38f * s), size = Size(10f * s, 26f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))

    // Left & Right Large Low-Floor Windows
    for (i in 0 until 3) {
        val wLeft = (22f + i * 28f) * s
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wLeft, 32f * s), size = Size(22f * s, 26f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
        drawRoundRect(color = Color(0x99FEF08A), topLeft = Offset(wLeft + 2f * s, 35f * s), size = Size(18f * s, 20f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))

        val wRight = (152f + i * 28f) * s
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wRight, 32f * s), size = Size(22f * s, 26f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
        drawRoundRect(color = Color(0x99FEF08A), topLeft = Offset(wRight + 2f * s, 35f * s), size = Size(18f * s, 20f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    }
}

/**
 * Amtrak Acela Express High-Speed Passenger Coach (First / Business / Cafe Club)
 */
fun DrawScope.drawAmtrakAcelaCoach(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f

    drawModernHighSpeedChassis(210f * s, 50f * s, 82f * s, 180f * s, wheelAngleRad, s, hasFrontCoupler = true, hasRearCoupler = true)

    val stainless = Color(0xFFCBD5E1)
    val acelaNavy = Color(0xFF1E3A8A)
    val acelaRed = Color(0xFFDC2626)
    val acelaSky = Color(0xFF0284C7)

    // Brushed Stainless Steel Body
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFE2E8F0), stainless, Color(0xFF94A3B8)),
            startY = 20f * s,
            endY = 82f * s
        ),
        topLeft = Offset(14f * s, 20f * s),
        size = Size(232f * s, 64f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Aerodynamic Curved Navy Roof Fairing
    drawRoundRect(
        color = acelaNavy,
        topLeft = Offset(12f * s, 16f * s),
        size = Size(236f * s, 12f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )

    // Continuous Dark Tinted High-Speed Window Band
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(24f * s, 32f * s),
        size = Size(212f * s, 24f * s),
        cornerRadius = CornerRadius(2.5f * s, 2.5f * s)
    )

    for (i in 0 until 8) {
        val wx = (28f + i * 25.5f) * s
        drawRoundRect(
            color = Color(0x99FEF08A),
            topLeft = Offset(wx, 36f * s),
            size = Size(20f * s, 16f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
    }

    // Amtrak Red Sill Pinstripe & Sky Blue Accent
    drawRect(color = acelaRed, topLeft = Offset(14f * s, 64f * s), size = Size(232f * s, 3f * s))
    drawRect(color = acelaSky, topLeft = Offset(14f * s, 67f * s), size = Size(232f * s, 2f * s))

    // Car End Stainless Boarding Vestibules
    drawRoundRect(color = Color(0xFF64748B), topLeft = Offset(16f * s, 26f * s), size = Size(6f * s, 54f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawRoundRect(color = Color(0xFF64748B), topLeft = Offset(238f * s, 26f * s), size = Size(6f * s, 54f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // "Acela" Emblem Accent
    drawCircle(color = acelaSky, radius = 4f * s, center = Offset(130f * s, 74f * s))
    drawCircle(color = Color(0xFFFFFFFF), radius = 2f * s, center = Offset(130f * s, 74f * s))
}

/**
 * Amtrak Acela Express Trailing Power Car Unit
 */
fun DrawScope.drawAmtrakAcelaRearPowerCar(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f

    val stainless = Color(0xFFCBD5E1)
    val acelaNavy = Color(0xFF1E3A8A)
    val acelaRed = Color(0xFFDC2626)

    drawModernHighSpeedChassis(210f * s, 50f * s, 82f * s, 180f * s, wheelAngleRad, s, hasFrontCoupler = true, hasRearCoupler = false)

    // Aerodynamic Sloping Bullet Nose facing left
    val nose = Path().apply {
        moveTo(246f * s, 18f * s)
        lineTo(70f * s, 18f * s)
        cubicTo(45f * s, 18f * s, 18f * s, 38f * s, 8f * s, 68f * s)
        lineTo(8f * s, 84f * s)
        lineTo(246f * s, 84f * s)
        close()
    }
    drawPath(
        nose,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFE2E8F0), stainless, Color(0xFF64748B)),
            startY = 18f * s,
            endY = 84f * s
        )
    )

    // Curved Navy Nose Top
    val navyRoof = Path().apply {
        moveTo(246f * s, 18f * s)
        lineTo(70f * s, 18f * s)
        cubicTo(48f * s, 18f * s, 32f * s, 26f * s, 18f * s, 38f * s)
        lineTo(246f * s, 30f * s)
        close()
    }
    drawPath(navyRoof, color = acelaNavy)

    // Sloping Cockpit Windscreen
    val glass = Path().apply {
        moveTo(58f * s, 26f * s)
        lineTo(28f * s, 46f * s)
        lineTo(32f * s, 58f * s)
        lineTo(64f * s, 38f * s)
        close()
    }
    drawPath(glass, color = Color(0xFF0F172A))
    drawPath(glass, color = Color(0xFF38BDF8), style = Stroke(width = 1.5f * s))

    // Red Flank Sill Stripe
    drawLine(color = acelaRed, start = Offset(12f * s, 66f * s), end = Offset(246f * s, 66f * s), strokeWidth = 3f * s)

    // Dynamic Brake Louvers along power bay
    for (i in 0 until 5) {
        val gx = (90f + i * 28f) * s
        drawRoundRect(color = Color(0xFF334155), topLeft = Offset(gx, 36f * s), size = Size(18f * s, 22f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    }

    // High-Speed Aerofoil Pantograph on Roof Well
    drawRoofPantograph(180f * s, 8f * s, s * 0.8f)

    // Red High-Intensity Trailing Marker Lights
    drawCircle(color = Color(0xFFDC2626), radius = 4.5f * s, center = Offset(14f * s, 74f * s))
    drawCircle(color = Color(0xFFEF4444), radius = 2.5f * s, center = Offset(14f * s, 74f * s))
}

/**
 * Historic Pullman Heavyweight Parlor, Dining & Observation Car
 * Features authentic arched clerestory monitor roof, Pullman green/Tuscan livery,
 * gold leaf lettering, and heavy 6-wheel (3-axle) passenger bogies.
 */
fun DrawScope.drawSteamPullmanCoach(
    car: RailCar,
    wheelAngleRad: Float = 0f,
    widthPx: Float = 260f,
    heightPx: Float = 140f
) {
    val s = widthPx / 260f

    val pullmanGreen = Color(0xFF1E3A2B)
    val goldLeaf = Color(0xFFFBBF24)
    val roofBlack = Color(0xFF181C1E)
    val isObservation = car.name.contains("Observation", ignoreCase = true) || car.id.contains("pull_3")

    // Heavy Underframe Center Sill (Steel Girder)
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(10f * s, 80f * s),
        size = Size(240f * s, 8f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Battery Boxes & Steam Heating Regulators under floor
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(90f * s, 88f * s), size = Size(40f * s, 10f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(140f * s, 88f * s), size = Size(35f * s, 10f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))

    // Heavyweight Body Shell
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(pullmanGreen, Color(0xFF14291E)),
            startY = 24f * s,
            endY = 80f * s
        ),
        topLeft = Offset(12f * s, 24f * s),
        size = Size(236f * s, 56f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Historic Arched Clerestory Monitor Roof
    drawRoundRect(
        color = roofBlack,
        topLeft = Offset(10f * s, 18f * s),
        size = Size(240f * s, 10f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    drawRoundRect(
        color = Color(0xFF262C30),
        topLeft = Offset(24f * s, 14f * s),
        size = Size(212f * s, 6f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Clerestory Deck Ventilator Windows
    for (vi in 0 until 14) {
        val vx = (28f + vi * 14.5f) * s
        drawRect(color = Color(0xFF64748B), topLeft = Offset(vx, 15.5f * s), size = Size(8f * s, 3f * s))
    }

    // Gold Leaf Letterboard "P U L L M A N"
    drawRect(color = Color(0xFF0F2016), topLeft = Offset(14f * s, 28f * s), size = Size(232f * s, 8f * s))
    drawRoundRect(color = goldLeaf, topLeft = Offset(95f * s, 30f * s), size = Size(70f * s, 4f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // Gold Pinstriping along Belt Rail
    drawLine(color = goldLeaf, start = Offset(14f * s, 62f * s), end = Offset(246f * s, 62f * s), strokeWidth = 1.5f * s)
    drawLine(color = goldLeaf, start = Offset(14f * s, 74f * s), end = Offset(246f * s, 74f * s), strokeWidth = 1.2f * s)

    // Arched Heavyweight Passenger Windows with Gas/Incandescent Glow
    for (wi in 0 until 9) {
        val wx = (20f + wi * 24.5f) * s
        // Window Frame
        drawRoundRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(wx, 38f * s),
            size = Size(18f * s, 22f * s),
            cornerRadius = CornerRadius(2.5f * s, 2.5f * s)
        )
        // Glass with Interior Glow
        drawRoundRect(
            color = Color(0xFFFDE68A),
            topLeft = Offset(wx + 1.5f * s, 40f * s),
            size = Size(15f * s, 18f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
        // Window Transom Divider
        drawLine(color = Color(0xFF334155), start = Offset(wx + 1.5f * s, 45f * s), end = Offset(wx + 16.5f * s, 45f * s), strokeWidth = 1.2f * s)
    }

    // Observation Rear Brass Platform if Observation Car
    if (isObservation) {
        // Cutaway Open Platform at Left End
        drawRect(color = Color(0xFF0F172A), topLeft = Offset(10f * s, 36f * s), size = Size(18f * s, 44f * s))
        // Polished Brass Railings & Drumhead Emblem
        drawLine(color = goldLeaf, start = Offset(8f * s, 54f * s), end = Offset(28f * s, 54f * s), strokeWidth = 2.5f * s)
        for (ri in 0 until 5) {
            val rx = (8f + ri * 4.5f) * s
            drawLine(color = goldLeaf, start = Offset(rx, 54f * s), end = Offset(rx, 80f * s), strokeWidth = 1.5f * s)
        }
        // Illuminated Brass Drumhead "CENTURY LIMITED"
        drawCircle(color = goldLeaf, radius = 5.5f * s, center = Offset(16f * s, 66f * s))
        drawCircle(color = Color(0xFFDC2626), radius = 4f * s, center = Offset(16f * s, 66f * s))
        drawCircle(color = Color(0xFFFFFFFF), radius = 2f * s, center = Offset(16f * s, 66f * s))
    }

    // Two Heavyweight 6-Wheel (3-Axle) Commonwealth Passenger Bogies
    drawPullmanThreeAxleBogie(58f * s, 100f * s, wheelAngleRad, s)
    drawPullmanThreeAxleBogie(202f * s, 100f * s, wheelAngleRad, s)
}

/**
 * Heavyweight 6-Wheel (3-Axle) Cast Steel Passenger Bogie Truck
 */
fun DrawScope.drawPullmanThreeAxleBogie(
    centerX: Float,
    centerY: Float,
    wheelAngleRad: Float,
    s: Float
) {
    // Cast steel side frame
    drawRoundRect(
        color = Color(0xFF1E242B),
        topLeft = Offset(centerX - 42f * s, centerY - 6f * s),
        size = Size(84f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Equalizer bars & dual leaf springs
    drawRoundRect(color = Color(0xFF334155), topLeft = Offset(centerX - 30f * s, centerY - 2f * s), size = Size(20f * s, 5f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawRoundRect(color = Color(0xFF334155), topLeft = Offset(centerX + 10f * s, centerY - 2f * s), size = Size(20f * s, 5f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // 3 Flanged Steel Wheels (Wheel radius 14f resting exactly at 122f * s)
    val r = 14f * s
    val wy = centerY + 6.5f * s
    for (offset in listOf(-26f * s, 0f, 26f * s)) {
        val wx = centerX + offset
        drawCircle(color = Color(0xFF0F172A), radius = r + 1.5f * s, center = Offset(wx, wy))
        drawCircle(color = Color(0xFF475569), radius = r, center = Offset(wx, wy))
        drawCircle(color = Color(0xFF1E293B), radius = r - 2.5f * s, center = Offset(wx, wy))

        // Spokes
        val cosA = cos(wheelAngleRad)
        val sinA = sin(wheelAngleRad)
        drawLine(
            color = Color(0xFF94A3B8),
            start = Offset(wx - cosA * 7f * s, wy - sinA * 7f * s),
            end = Offset(wx + cosA * 7f * s, wy + sinA * 7f * s),
            strokeWidth = 1.8f * s
        )
        // Journal box
        drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(wx - 4f * s, wy - 4f * s), size = Size(8f * s, 8f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
        drawCircle(color = Color(0xFFFBBF24), radius = 1.8f * s, center = Offset(wx, wy))
    }
}
