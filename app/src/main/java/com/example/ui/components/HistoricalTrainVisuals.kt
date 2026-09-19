package com.example.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.model.TrainModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Historical & Precision Train Renderers:
 * 1. British Classics:
 *    - BR Class 55 "Deltic" (English Electric Type 5 Co-Co)
 *    - LNER Gresley Class A3 4-6-2 Flying Scotsman (Walschaerts valve gear)
 *    - Class 66 Heavy Freight / Rescue
 *    - Class 92 Tri-voltage Electric Rescue
 * 2. American Steam Legends:
 *    - UP 4-8-8-4 Big Boy #4014 (Articulated 14-axle monster)
 *    - NYC J-3a Dreyfuss Streamlined Hudson 4-6-4 (20th Century Limited)
 *    - CPR Royal Hudson 4-6-4 Semi-Streamlined #2850
 *    - PRR K4s / USRA Heavy Pacific 4-6-2
 *    - Supersonic Hyper Steam
 * 3. American Diesel & Streamline Pioneers:
 *    - EMD F7-A Bulldog Nose
 *    - Alco PA-1 "The Honor Dog"
 */

// ============================================================================
// 1. BRITISH TRAINS
// ============================================================================

/**
 * British Rail Class 55 "Deltic" (English Electric Type 5 Co-Co Diesel)
 * Features authentic triangular nose, yellow warning panel, headcode box "1A16",
 * twin Napier Deltic opposed-piston roof cooling grilles, two-tone green livery,
 * British round buffers and screw coupling.
 */
fun DrawScope.drawBrClass55DelticLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String = ""
) {
    // Underframe & Heavy Co-Co Chassis
    drawRoundRect(
        color = Color(0xFF1E242B),
        topLeft = Offset(10f * s, 84f * s),
        size = Size(340f * s, 16f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Two 3-Axle British Co-Co Bogies
    drawBritishThreeAxleBogie(68f * s, 102f * s, wheelAngleRad, s)
    drawBritishThreeAxleBogie(282f * s, 102f * s, wheelAngleRad, s)

    // Fuel & Water Tanks between bogies
    drawRoundRect(
        color = Color(0xFF0F141A),
        topLeft = Offset(135f * s, 88f * s),
        size = Size(90f * s, 20f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )

    // Lower Two-Tone Green / Grey Skirt Band
    val lowerBandColor = if (variant.contains("BLUE")) Color(0xFFE2E8F0) else Color(0xFF86EFAC)
    drawRoundRect(
        color = lowerBandColor,
        topLeft = Offset(12f * s, 74f * s),
        size = Size(336f * s, 12f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Main Locomotive Body (Iconic English Electric long hood with rounded tumblehome)
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                bodyColor.copy(alpha = 0.95f),
                bodyColor,
                bodyColor.copy(alpha = 0.85f)
            ),
            startY = 24f * s,
            endY = 76f * s
        ),
        topLeft = Offset(14f * s, 24f * s),
        size = Size(332f * s, 52f * s),
        cornerRadius = CornerRadius(8f * s, 8f * s)
    )

    // Deltic Trademark Sloped Triangular Nose (Front Cab at Right x=300..352)
    val nosePath = Path().apply {
        moveTo(290f * s, 24f * s)
        lineTo(336f * s, 38f * s)
        lineTo(352f * s, 68f * s)
        lineTo(352f * s, 84f * s)
        lineTo(290f * s, 84f * s)
        close()
    }
    drawPath(nosePath, color = bodyColor)

    // Rear Cab Nose (Double-ended British design x=8..55)
    val rearNosePath = Path().apply {
        moveTo(70f * s, 24f * s)
        lineTo(24f * s, 38f * s)
        lineTo(8f * s, 68f * s)
        lineTo(8f * s, 84f * s)
        lineTo(70f * s, 84f * s)
        close()
    }
    drawPath(rearNosePath, color = bodyColor)

    // Front Bright Yellow Warning Panel (Mandatory British Rail safety requirement)
    val frontWarningPanel = Path().apply {
        moveTo(324f * s, 42f * s)
        lineTo(350f * s, 54f * s)
        lineTo(350f * s, 82f * s)
        lineTo(324f * s, 82f * s)
        close()
    }
    drawPath(frontWarningPanel, color = Color(0xFFFACC15))

    // Rear Yellow Warning Panel
    val rearWarningPanel = Path().apply {
        moveTo(36f * s, 42f * s)
        lineTo(10f * s, 54f * s)
        lineTo(10f * s, 82f * s)
        lineTo(36f * s, 82f * s)
        close()
    }
    drawPath(rearWarningPanel, color = Color(0xFFFACC15))

    // Front Square Headcode Box ("1A16" Kings Cross - Edinburgh Express)
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(332f * s, 58f * s),
        size = Size(16f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    drawRoundRect(
        color = Color(0xFFFEF08A),
        topLeft = Offset(334f * s, 60f * s),
        size = Size(12f * s, 6f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )

    // Windscreen Windows (Angled British Cab design)
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(296f * s, 30f * s),
        size = Size(24f * s, 14f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    drawRoundRect(
        color = Color(0xFF67E8F9).copy(alpha = 0.8f),
        topLeft = Offset(298f * s, 32f * s),
        size = Size(20f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Rear Windscreen Windows
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(40f * s, 30f * s),
        size = Size(24f * s, 14f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    drawRoundRect(
        color = Color(0xFF67E8F9).copy(alpha = 0.8f),
        topLeft = Offset(42f * s, 32f * s),
        size = Size(20f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Napier Deltic Rooftop Exhausts and Circular Radiator Fans
    drawOval(color = Color(0xFF334155), topLeft = Offset(130f * s, 19f * s), size = Size(24f * s, 7f * s))
    drawOval(color = Color(0xFF334155), topLeft = Offset(206f * s, 19f * s), size = Size(24f * s, 7f * s))
    // Circular cooling fan wire meshes
    drawCircle(color = Color(0xFF1E293B), radius = 6f * s, center = Offset(142f * s, 22f * s))
    drawCircle(color = Color(0xFF1E293B), radius = 6f * s, center = Offset(218f * s, 22f * s))

    // Bodyside Circular Porthole Engine Room Windows (Deltic trademark)
    listOf(105f, 155f, 180f, 205f, 255f).forEach { px ->
        drawCircle(color = Color(0xFF0F172A), radius = 6f * s, center = Offset(px * s, 50f * s))
        drawCircle(color = Color(0xFF93C5FD).copy(alpha = 0.8f), radius = 4.5f * s, center = Offset(px * s, 50f * s))
    }

    // Cast English Electric Brass Nameplate ("D9009 ALYCIDON")
    drawRoundRect(
        color = Color(0xFFB45309),
        topLeft = Offset(164f * s, 62f * s),
        size = Size(32f * s, 6f * s),
        cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
    )

    // British Round Buffer Plates & Screw Link Coupling (Front and Rear)
    drawBritishBuffers(352f * s, 80f * s, s, isFront = true)
    drawBritishBuffers(8f * s, 80f * s, s, isFront = false)
}

/**
 * Scaled Rail Wheel helper for historical locomotives with various wheel diameters
 */
fun DrawScope.drawRailWheelWithRadius(
    cx: Float,
    cy: Float,
    r: Float,
    angle: Float
) {
    drawCircle(color = Color(0xFF42474E), radius = r + 2f, center = Offset(cx, cy))
    drawCircle(color = Color(0xFF1E2125), radius = r, center = Offset(cx, cy))
    drawCircle(color = Color(0xFF33383F), radius = (r - 3f).coerceAtLeast(1f), center = Offset(cx, cy))
    drawCircle(color = Color(0xFF8E959E), radius = (r * 0.3f).coerceAtLeast(1.5f), center = Offset(cx, cy))
}

/**
 * British Three-Axle Bogie (Co-Co Suspension with brake shoes and coil springs)
 */
fun DrawScope.drawBritishThreeAxleBogie(cx: Float, cy: Float, wheelAngle: Float, s: Float) {
    // Cast steel bogie frame
    drawRoundRect(
        color = Color(0xFF262E35),
        topLeft = Offset(cx - 42f * s, cy - 8f * s),
        size = Size(84f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Three 43-inch disc wheels
    listOf(cx - 28f * s, cx, cx + 28f * s).forEach { wx ->
        drawRailWheelWithRadius(wx, cy + 4f * s, 11f * s, wheelAngle)
        // Axle box with coil spring
        drawRoundRect(
            color = Color(0xFF475569),
            topLeft = Offset(wx - 4f * s, cy - 4f * s),
            size = Size(8f * s, 7f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
    }
}

/**
 * British Round Buffers and Drawhook
 */
fun DrawScope.drawBritishBuffers(x: Float, y: Float, s: Float, isFront: Boolean) {
    val dir = if (isFront) 1f else -1f
    // Buffer Shank
    drawRoundRect(
        color = Color(0xFF334155),
        topLeft = Offset(if (isFront) x else x - 8f * s, y - 2f * s),
        size = Size(8f * s, 4f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )
    // Round Buffer Face Plate
    drawOval(
        color = Color(0xFF0F172A),
        topLeft = Offset(if (isFront) x + 7f * s else x - 10f * s, y - 5f * s),
        size = Size(3f * s, 10f * s)
    )
    // Central Drawhook
    drawLine(
        color = Color(0xFF000000),
        start = Offset(x, y + 4f * s),
        end = Offset(x + dir * 6f * s, y + 8f * s),
        strokeWidth = 2.5f * s,
        cap = StrokeCap.Round
    )
}

/**
 * Class 66 British Heavy Freight / Rescue Locomotive
 */
fun DrawScope.drawBritishClass66HeavyRescue(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Chassis & Fuel Tank
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(10f * s, 82f * s), size = Size(340f * s, 16f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(125f * s, 86f * s), size = Size(110f * s, 22f * s), cornerRadius = CornerRadius(4f * s, 4f * s))

    // Bogies
    drawBritishThreeAxleBogie(68f * s, 102f * s, wheelAngleRad, s)
    drawBritishThreeAxleBogie(282f * s, 102f * s, wheelAngleRad, s)

    // Body with Low Cab Profile (UK Loading Gauge)
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(bodyColor, bodyColor.copy(alpha = 0.85f)),
            startY = 24f * s,
            endY = 82f * s
        ),
        topLeft = Offset(15f * s, 24f * s),
        size = Size(330f * s, 58f * s),
        cornerRadius = CornerRadius(6f * s, 6f * s)
    )

    // Full Yellow Warning Nose (British Front)
    val frontNose = Path().apply {
        moveTo(300f * s, 30f * s)
        lineTo(342f * s, 42f * s)
        lineTo(346f * s, 82f * s)
        lineTo(300f * s, 82f * s)
        close()
    }
    drawPath(frontNose, color = Color(0xFFFACC15))

    // Triple Cluster Headlights
    drawCircle(color = Color(0xFFFEF08A), radius = 3.5f * s, center = Offset(340f * s, 64f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 3.5f * s, center = Offset(340f * s, 74f * s))

    // Low Profile Windscreen
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(294f * s, 32f * s), size = Size(26f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(296f * s, 34f * s), size = Size(22f * s, 12f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))

    // Bodyside dynamic brake louvers
    for (i in 0 until 6) {
        val gx = 100f + i * 28f
        drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(gx * s, 34f * s), size = Size(18f * s, 22f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    }

    // Round Buffers
    drawBritishBuffers(346f * s, 78f * s, s, isFront = true)
    drawBritishBuffers(14f * s, 78f * s, s, isFront = false)
}

/**
 * British Rail Class 92 Dual-Voltage Electric Heavy Rescue
 */
fun DrawScope.drawBritishClass92ElectricRescue(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Chassis
    drawRoundRect(color = Color(0xFF1E242B), topLeft = Offset(10f * s, 82f * s), size = Size(340f * s, 16f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawBritishThreeAxleBogie(68f * s, 102f * s, wheelAngleRad, s)
    drawBritishThreeAxleBogie(282f * s, 102f * s, wheelAngleRad, s)

    // Body
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(14f * s, 22f * s),
        size = Size(332f * s, 60f * s),
        cornerRadius = CornerRadius(5f * s, 5f * s)
    )

    // Dual High-Speed Pantographs
    drawRoofPantograph(100f * s, 22f * s, s)
    drawRoofPantograph(260f * s, 22f * s, s)

    // Full Yellow Warning Nose
    drawRoundRect(color = Color(0xFFFACC15), topLeft = Offset(315f * s, 32f * s), size = Size(30f * s, 50f * s), cornerRadius = CornerRadius(4f * s, 4f * s))

    // Cab Windows
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(300f * s, 28f * s), size = Size(26f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF67E8F9), topLeft = Offset(302f * s, 30f * s), size = Size(22f * s, 12f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))

    // Tunnel rescue round buffers
    drawBritishBuffers(346f * s, 78f * s, s, isFront = true)
    drawBritishBuffers(14f * s, 78f * s, s, isFront = false)
}

// ============================================================================
// 2. AMERICAN STEAM TRAINS & HISTORICAL GIANTS
// ============================================================================

/**
 * Union Pacific 4-8-8-4 "Big Boy" #4014
 * The world's largest, most legendary steam locomotive!
 * Features:
 * - 4-8-8-4 Articulated Running Gear (Leading truck, Engine 1, Engine 2, Trailing truck)
 * - Animated Walschaerts Valve Gear, Crosshead, and Piston Rods
 * - Massive 400 PSI boiler with Sand Domes, Steam Turret, and Whistle
 * - Graphite Smokebox Door with Centered Headlight & Mars Oscillating Warning Light
 * - Illuminated "4014" Numberboards
 * - Giant 7-Axle Centipede Tender (14 Wheels!) with "UNION PACIFIC" block lettering
 */
fun DrawScope.drawUpBigBoy4014Detailed(
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // -------------------------------------------------------------
    // A. GIANT 7-AXLE CENTIPEDE TENDER (Left Side x = 6..120)
    // -------------------------------------------------------------
    // Tender frame & coal bunker
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF262C34), Color(0xFF13171B)),
            startY = 20f * s,
            endY = 82f * s
        ),
        topLeft = Offset(6f * s, 20f * s),
        size = Size(116f * s, 62f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Coal Bunker Heap on top of tender
    val coalHeap = Path().apply {
        moveTo(10f * s, 20f * s)
        lineTo(30f * s, 10f * s)
        lineTo(70f * s, 8f * s)
        lineTo(110f * s, 20f * s)
        close()
    }
    drawPath(coalHeap, color = Color(0xFF0A0C0E))

    // "UNION PACIFIC" Block Lettering on Tender
    drawRoundRect(
        color = Color(0xFFF59E0B),
        topLeft = Offset(24f * s, 44f * s),
        size = Size(78f * s, 9f * s),
        cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
    )
    drawRoundRect(
        color = Color(0xFF13171B),
        topLeft = Offset(26f * s, 46f * s),
        size = Size(74f * s, 5f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )

    // Tender 7-Axle Centipede Bogies (14 Wheels along underframe resting on rail at 122f * s)
    for (ti in 0 until 7) {
        val tx = 18f + ti * 14.5f
        drawRailWheelWithRadius(tx * s, 112f * s, 10f * s, wheelAngleRad)
    }

    // Tender-to-Locomotive Canvas Weather Diaphragm
    drawRoundRect(
        color = Color(0xFF1C1917),
        topLeft = Offset(120f * s, 22f * s),
        size = Size(8f * s, 58f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )

    // -------------------------------------------------------------
    // B. BIG BOY ENCLOSED CAB (x = 126..162)
    // -------------------------------------------------------------
    drawRoundRect(
        color = Color(0xFF181C22),
        topLeft = Offset(126f * s, 18f * s),
        size = Size(36f * s, 64f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    // Cab Arched Side Window
    drawRoundRect(
        color = Color(0xFF38BDF8).copy(alpha = 0.85f),
        topLeft = Offset(132f * s, 24f * s),
        size = Size(14f * s, 14f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Cab Number "4014"
    drawRoundRect(
        color = Color(0xFFFBBF24),
        topLeft = Offset(130f * s, 44f * s),
        size = Size(20f * s, 6f * s),
        cornerRadius = CornerRadius(1f * s, 1f * s)
    )

    // -------------------------------------------------------------
    // C. MASSIVE 400-PSI BOILER (x = 160..328)
    // -------------------------------------------------------------
    // Huge cylindrical boiler barrel
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF333B44), Color(0xFF1E242B), Color(0xFF12161A)),
            startY = 22f * s,
            endY = 78f * s
        ),
        topLeft = Offset(160f * s, 22f * s),
        size = Size(168f * s, 56f * s),
        cornerRadius = CornerRadius(8f * s, 8f * s)
    )

    // Polished Brass Boiler Bands
    listOf(182f, 210f, 240f, 270f, 300f).forEach { bx ->
        drawLine(
            color = Color(0xFFD97706),
            start = Offset(bx * s, 22f * s),
            end = Offset(bx * s, 78f * s),
            strokeWidth = 2f * s
        )
    }

    // Top Boiler Domes (Steam Dome, Dual Sand Domes, Whistle)
    drawRoundRect(color = Color(0xFF1E242B), topLeft = Offset(195f * s, 12f * s), size = Size(18f * s, 12f * s), cornerRadius = CornerRadius(5f * s, 5f * s))
    drawRoundRect(color = Color(0xFF1E242B), topLeft = Offset(245f * s, 12f * s), size = Size(18f * s, 12f * s), cornerRadius = CornerRadius(5f * s, 5f * s))
    drawRoundRect(color = Color(0xFF1E242B), topLeft = Offset(285f * s, 14f * s), size = Size(14f * s, 10f * s), cornerRadius = CornerRadius(4f * s, 4f * s))
    // Twin Chimneys with Smoke Hood
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(310f * s, 8f * s), size = Size(16f * s, 16f * s), cornerRadius = CornerRadius(3f * s, 3f * s))

    // -------------------------------------------------------------
    // D. GRAPHITE SMOKEBOX & HEADLIGHTS (x = 326..356)
    // -------------------------------------------------------------
    // Graphite Smokebox Door
    val smokeboxPath = Path().apply {
        moveTo(326f * s, 22f * s)
        lineTo(348f * s, 30f * s)
        lineTo(354f * s, 50f * s)
        lineTo(348f * s, 74f * s)
        lineTo(326f * s, 78f * s)
        close()
    }
    drawPath(smokeboxPath, color = Color(0xFF475569))

    // Centered High-Beam Headlight with Illuminated Lens
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(348f * s, 36f * s), size = Size(12f * s, 14f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 5f * s, center = Offset(356f * s, 43f * s))

    // Mars Warning Oscillating Light below main headlight
    drawRoundRect(color = Color(0xFF991B1B), topLeft = Offset(346f * s, 53f * s), size = Size(8f * s, 8f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawCircle(color = Color(0xFFEF4444), radius = 3f * s, center = Offset(350f * s, 57f * s))

    // Illuminated "4014" Numberboard on Smokebox
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(332f * s, 20f * s), size = Size(18f * s, 7f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    drawRoundRect(color = Color(0xFFFEF08A), topLeft = Offset(334f * s, 21.5f * s), size = Size(14f * s, 4f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // Heavy Cast Steel Cowcatcher / Pilot on Front
    val cowcatcher = Path().apply {
        moveTo(344f * s, 78f * s)
        lineTo(358f * s, 90f * s)
        lineTo(358f * s, 98f * s)
        lineTo(336f * s, 98f * s)
        lineTo(336f * s, 78f * s)
        close()
    }
    drawPath(cowcatcher, color = Color(0xFF1E293B))
    // Grate bars on cowcatcher
    for (gi in 0 until 5) {
        drawLine(
            color = Color(0xFF64748B),
            start = Offset((338f + gi * 4f) * s, 78f * s),
            end = Offset((344f + gi * 3f) * s, 96f * s),
            strokeWidth = 1.8f * s
        )
    }

    // -------------------------------------------------------------
    // E. 4-8-8-4 ARTICULATED DRIVING GEAR & CYLINDERS (Accurately aligned at 122f * s)
    // -------------------------------------------------------------
    // Front 2-Axle Pilot Truck (4 small wheels, resting on rail at 122f * s)
    drawRailWheelWithRadius(334f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(348f * s, 112f * s, 10f * s, wheelAngleRad)

    // ENGINE 1 (Front 4 Driving Wheels: realistically spaced with clean 2f * s rim clearance, resting at 122f * s)
    val frontDrivers = listOf(244f, 270f, 296f, 322f)
    for (wx in frontDrivers) {
        drawSteamDriverWheelDetailed(wx * s, 110f * s, 12f * s, wheelAngleRad, isSpoked = true, s = s)
    }
    // Front Steam Cylinder & Valve Gear
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(322f * s, 98f * s), size = Size(20f * s, 18f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawAnimatedSideRod(frontDrivers.first() * s, frontDrivers.last() * s, 110f * s, wheelAngleRad, s)

    // ENGINE 2 (Rear 4 Driving Wheels: realistically spaced with clean 2f * s rim clearance, resting at 122f * s)
    val rearDrivers = listOf(164f, 190f, 216f, 242f)
    for (wx in rearDrivers) {
        drawSteamDriverWheelDetailed(wx * s, 110f * s, 12f * s, wheelAngleRad, isSpoked = true, s = s)
    }
    // Rear Steam Cylinder
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(242f * s, 98f * s), size = Size(18f * s, 18f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawAnimatedSideRod(rearDrivers.first() * s, rearDrivers.last() * s, 110f * s, wheelAngleRad, s)

    // Rear 2-Axle Trailing Truck under firebox (resting on rail at 122f * s)
    drawRailWheelWithRadius(136f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(150f * s, 112f * s, 10f * s, wheelAngleRad)
}

/**
 * Detailed Spoked / Boxpok Driving Wheel with Counterweight
 */
fun DrawScope.drawSteamDriverWheelDetailed(
    cx: Float,
    cy: Float,
    r: Float,
    angleRad: Float,
    isSpoked: Boolean = true,
    s: Float = 1f
) {
    // Heavy steel tire rim
    drawCircle(color = Color(0xFFE2E8F0), radius = r, center = Offset(cx, cy))
    drawCircle(color = Color(0xFF1E242B), radius = r - 2.5f * s, center = Offset(cx, cy))

    if (isSpoked) {
        // Spokes
        val numSpokes = 12
        for (i in 0 until numSpokes) {
            val a = angleRad + (i * 2.0 * Math.PI / numSpokes).toFloat()
            drawLine(
                color = Color(0xFF64748B),
                start = Offset(cx, cy),
                end = Offset(cx + cos(a) * (r - 2.5f * s), cy + sin(a) * (r - 2.5f * s)),
                strokeWidth = 2.2f * s
            )
        }
    } else {
        // Boxpok disc wheel with holes
        drawCircle(color = Color(0xFF475569), radius = r - 3f * s, center = Offset(cx, cy))
        for (i in 0 until 6) {
            val a = angleRad + (i * 2.0 * Math.PI / 6).toFloat()
            drawCircle(color = Color(0xFF0F172A), radius = 3.5f * s, center = Offset(cx + cos(a) * (r * 0.55f), cy + sin(a) * (r * 0.55f)))
        }
    }

    // Heavy Counterweight wedge
    val cwPath = Path().apply {
        moveTo(cx, cy)
        val a1 = angleRad + 0.6f
        val a2 = angleRad + 2.5f
        lineTo(cx + cos(a1) * (r - 2.5f * s), cy + sin(a1) * (r - 2.5f * s))
        lineTo(cx + cos(a2) * (r - 2.5f * s), cy + sin(a2) * (r - 2.5f * s))
        close()
    }
    drawPath(cwPath, color = Color(0xFFCBD5E1).copy(alpha = 0.65f))

    // Wheel Hub & Crank Pin
    drawCircle(color = Color(0xFFCBD5E1), radius = r * 0.32f, center = Offset(cx, cy))
    val crankR = r * 0.52f
    val pinX = cx + cos(angleRad) * crankR
    val pinY = cy + sin(angleRad) * crankR
    drawCircle(color = Color(0xFFEF4444), radius = 3.5f * s, center = Offset(pinX, pinY))
}

/**
 * Animated Valve Gear Side Coupling Rod
 */
fun DrawScope.drawAnimatedSideRod(
    startX: Float,
    endX: Float,
    cy: Float,
    angleRad: Float,
    s: Float
) {
    val crankR = 8f * s
    val yOffset = sin(angleRad) * crankR
    val xOffset = cos(angleRad) * crankR

    // Heavy Steel Side Coupling Rod
    drawLine(
        color = Color(0xFFE2E8F0),
        start = Offset(startX + xOffset, cy + yOffset),
        end = Offset(endX + xOffset, cy + yOffset),
        strokeWidth = 4.5f * s,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFF64748B),
        start = Offset(startX + xOffset, cy + yOffset),
        end = Offset(endX + xOffset, cy + yOffset),
        strokeWidth = 2.0f * s,
        cap = StrokeCap.Round
    )
}

/**
 * New York Central 4-6-4 J-3a Dreyfuss Streamlined Hudson
 * Henry Dreyfuss's 1938 art-deco aerodynamic masterpiece for the 20th Century Limited!
 * Features:
 * - Bullet nose cone with distinctive vertical fin down the center
 * - Recessed sealed-beam headlight and horizontal wing stripes
 * - Streamlined scalloped running board valances
 * - Disc Boxpok driving wheels with polished rods
 * - Enclosed streamlined tender with "NEW YORK CENTRAL"
 */
fun DrawScope.drawNycDreyfussHudsonDetailed(
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // -------------------------------------------------------------
    // A. STREAMLINED TENDER (x = 8..124)
    // -------------------------------------------------------------
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF475569), Color(0xFF1E293B)),
            startY = 20f * s,
            endY = 82f * s
        ),
        topLeft = Offset(8f * s, 20f * s),
        size = Size(116f * s, 62f * s),
        cornerRadius = CornerRadius(6f * s, 6f * s)
    )

    // Brushed Aluminum Speed Stripes along tender
    drawLine(color = Color(0xFFE2E8F0), start = Offset(8f * s, 42f * s), end = Offset(124f * s, 42f * s), strokeWidth = 2.5f * s)
    drawLine(color = Color(0xFFE2E8F0), start = Offset(8f * s, 48f * s), end = Offset(124f * s, 48f * s), strokeWidth = 2.5f * s)

    // Tender 6-Wheel Bogies (Resting on rail at 122f * s)
    drawRailWheelWithRadius(24f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(42f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(60f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(82f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(100f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(118f * s, 112f * s, 10f * s, wheelAngleRad)

    // -------------------------------------------------------------
    // B. STREAMLINED CAB & BOILER CASING (x = 126..310)
    // -------------------------------------------------------------
    // Enclosed Streamlined Cab
    drawRoundRect(
        color = Color(0xFF334155),
        topLeft = Offset(126f * s, 18f * s),
        size = Size(34f * s, 64f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )
    drawRoundRect(
        color = Color(0xFF38BDF8),
        topLeft = Offset(132f * s, 24f * s),
        size = Size(14f * s, 12f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Bullet-nosed Streamlined Boiler Casing
    val casingPath = Path().apply {
        moveTo(160f * s, 20f * s)
        lineTo(306f * s, 20f * s)
        lineTo(334f * s, 32f * s)
        lineTo(354f * s, 54f * s) // Iconic Dreyfuss bullet nose cone
        lineTo(354f * s, 76f * s)
        lineTo(340f * s, 86f * s)
        lineTo(160f * s, 86f * s)
        close()
    }
    drawPath(
        casingPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF64748B), Color(0xFF334155), Color(0xFF1E293B)),
            startY = 20f * s,
            endY = 86f * s
        )
    )

    // Iconic Dreyfuss Vertical Center Nose Fin (Extends down bullet nose cone)
    drawLine(
        color = Color(0xFFE2E8F0),
        start = Offset(306f * s, 20f * s),
        end = Offset(354f * s, 54f * s),
        strokeWidth = 3.5f * s,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFFCBD5E1),
        start = Offset(354f * s, 54f * s),
        end = Offset(354f * s, 76f * s),
        strokeWidth = 3.5f * s,
        cap = StrokeCap.Round
    )

    // Recessed Sealed-Beam Bullseye Headlight
    drawCircle(color = Color(0xFF0F172A), radius = 8f * s, center = Offset(348f * s, 54f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 5.5f * s, center = Offset(348f * s, 54f * s))

    // Brushed Aluminum Wing Stripes on Nose Cone
    listOf(44f, 48f, 52f, 56f, 60f, 64f).forEach { wy ->
        drawLine(
            color = Color(0xFFE2E8F0),
            start = Offset(324f * s, wy * s),
            end = Offset(348f * s, wy * s),
            strokeWidth = 1.8f * s
        )
    }

    // Scalloped Side Running Board Skirt
    drawLine(
        color = Color(0xFFE2E8F0),
        start = Offset(160f * s, 76f * s),
        end = Offset(340f * s, 76f * s),
        strokeWidth = 2.5f * s
    )

    // -------------------------------------------------------------
    // C. 4-6-4 HUDSON RUNNING GEAR (Resting accurately at 122f * s)
    // -------------------------------------------------------------
    // Front 2-Axle Pilot (4 wheels, resting on rail at 122f * s)
    drawRailWheelWithRadius(316f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(334f * s, 112f * s, 10f * s, wheelAngleRad)

    // Three Huge 79-inch Boxpok Disc Driving Wheels (Realistically spaced: not too close, just right)
    val drivers = listOf(202f, 244f, 286f)
    for (wx in drivers) {
        drawSteamDriverWheelDetailed(wx * s, 104f * s, 18f * s, wheelAngleRad, isSpoked = false, s = s)
    }
    drawAnimatedSideRod(drivers.first() * s, drivers.last() * s, 104f * s, wheelAngleRad, s)

    // Rear 2-Axle Trailing Truck (Resting on rail at 122f * s)
    drawRailWheelWithRadius(142f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(160f * s, 112f * s, 10f * s, wheelAngleRad)
}

/**
 * Canadian Pacific Semi-Streamlined 4-6-4 Royal Hudson #2850
 * Polished stainless steel jacket, royal blue / Tuscan red band, cast Royal Crown emblem.
 */
fun DrawScope.drawCpRoyalHudsonDetailed(
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Tender with CP Tuscan Red panel
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(8f * s, 22f * s), size = Size(116f * s, 60f * s), cornerRadius = CornerRadius(4f * s, 4f * s))
    drawRoundRect(color = Color(0xFF881337), topLeft = Offset(14f * s, 36f * s), size = Size(104f * s, 26f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    // "CANADIAN PACIFIC" Gold lettering
    drawRoundRect(color = Color(0xFFFBBF24), topLeft = Offset(24f * s, 46f * s), size = Size(84f * s, 6f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // Tender wheels (Resting on rail at 122f * s)
    for (ti in 0 until 6) {
        val tx = 18f + ti * 18.5f
        drawRailWheelWithRadius(tx * s, 112f * s, 10f * s, wheelAngleRad)
    }

    // Cab with all-weather Canadian vestibule
    drawRoundRect(color = Color(0xFF881337), topLeft = Offset(126f * s, 20f * s), size = Size(36f * s, 62f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(132f * s, 26f * s), size = Size(14f * s, 12f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Polished Stainless Steel Boiler Casing
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8), Color(0xFF475569)),
            startY = 22f * s,
            endY = 76f * s
        ),
        topLeft = Offset(162f * s, 22f * s),
        size = Size(160f * s, 54f * s),
        cornerRadius = CornerRadius(6f * s, 6f * s)
    )

    // Tuscan Red Mid-Body Band with Gold Pinstripes
    drawRoundRect(color = Color(0xFF881337), topLeft = Offset(162f * s, 38f * s), size = Size(160f * s, 20f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawLine(color = Color(0xFFF59E0B), start = Offset(162f * s, 38f * s), end = Offset(322f * s, 38f * s), strokeWidth = 1.8f * s)
    drawLine(color = Color(0xFFF59E0B), start = Offset(162f * s, 58f * s), end = Offset(322f * s, 58f * s), strokeWidth = 1.8f * s)

    // Cast Royal Crown on running boards (Awarded by King George VI)
    drawRoundRect(color = Color(0xFFF59E0B), topLeft = Offset(230f * s, 62f * s), size = Size(14f * s, 10f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Front Smokebox & Recessed Headlight
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(322f * s, 24f * s), size = Size(26f * s, 52f * s), cornerRadius = CornerRadius(8f * s, 8f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 5.5f * s, center = Offset(342f * s, 46f * s))

    // Driving Wheels (Realistically spaced: not too close, just right, resting at 122f * s)
    val drivers = listOf(202f, 244f, 286f)
    for (wx in drivers) {
        drawSteamDriverWheelDetailed(wx * s, 104f * s, 18f * s, wheelAngleRad, isSpoked = true, s = s)
    }
    drawAnimatedSideRod(drivers.first() * s, drivers.last() * s, 104f * s, wheelAngleRad, s)

    // Pilot & Trailing Trucks (Resting on rail at 122f * s)
    drawRailWheelWithRadius(316f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(334f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(142f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(160f * s, 112f * s, 10f * s, wheelAngleRad)
}

/**
 * Pennsylvania Railroad K4s / USRA Heavy Pacific 4-6-2
 */
fun DrawScope.drawPacific462Detailed(
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    val britishGreen = if (bodyColor.alpha > 0f && bodyColor != Color.Unspecified) bodyColor else Color(0xFF1B6E34)
    val blackIron = Color(0xFF16191D)
    val goldLining = if (stripeColor.alpha > 0f && stripeColor != Color.Unspecified) stripeColor else Color(0xFFF59E0B)
    val brassGold = Color(0xFFEAB308)
    val bufferRed = Color(0xFFB91C1C)

    // =========================================================================
    // 1. BRITISH 8-WHEEL CORRIDOR TENDER (x = 8..116)
    // =========================================================================
    // Tender Tank Body
    drawRoundRect(
        color = britishGreen,
        topLeft = Offset(8f * s, 26f * s),
        size = Size(108f * s, 56f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    // Double Gold Lining Panelling
    drawRoundRect(
        color = goldLining,
        topLeft = Offset(14f * s, 32f * s),
        size = Size(46f * s, 44f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s),
        style = Stroke(width = 1.5f * s)
    )
    drawRoundRect(
        color = goldLining,
        topLeft = Offset(64f * s, 32f * s),
        size = Size(46f * s, 44f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s),
        style = Stroke(width = 1.5f * s)
    )
    // "L N E R" / Gold Crest Emblem in Center
    drawCircle(color = brassGold, radius = 5f * s, center = Offset(62f * s, 54f * s))
    drawCircle(color = blackIron, radius = 2.5f * s, center = Offset(62f * s, 54f * s))

    // Coal Bunker Heap on top
    val coalHeap = Path().apply {
        moveTo(10f * s, 26f * s)
        lineTo(32f * s, 12f * s)
        lineTo(75f * s, 10f * s)
        lineTo(112f * s, 26f * s)
        close()
    }
    drawPath(coalHeap, color = Color(0xFF0F1114))

    // Tender Solebar & British Rear Buffers
    drawRect(color = blackIron, topLeft = Offset(6f * s, 82f * s), size = Size(112f * s, 6f * s))
    drawBritishBuffers(4f * s, 84f * s, s, isFront = false)

    // Tender 8 Wheels (4 Axles resting on rail at 122f * s)
    listOf(20f, 44f, 76f, 100f).forEach { tx ->
        drawRailWheelWithRadius(tx * s, 112f * s, 10f * s, wheelAngleRad)
    }

    // Corridor Diaphragm Connection
    drawRoundRect(color = Color(0xFF1E2124), topLeft = Offset(116f * s, 26f * s), size = Size(8f * s, 56f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // =========================================================================
    // 2. DONCASTER ARCHED CAB (x = 124..168)
    // =========================================================================
    drawRoundRect(
        color = britishGreen,
        topLeft = Offset(124f * s, 18f * s),
        size = Size(44f * s, 66f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    // Cab Roof (Black with smooth curved overhang)
    drawRoundRect(
        color = blackIron,
        topLeft = Offset(122f * s, 12f * s),
        size = Size(48f * s, 8f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )
    // Arched Spectacle Windows with Brass Beading
    drawRoundRect(
        color = brassGold,
        topLeft = Offset(136f * s, 24f * s),
        size = Size(20f * s, 16f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s),
        style = Stroke(width = 1.2f * s)
    )
    drawRoundRect(
        color = Color(0xFF38BDF8).copy(alpha = 0.85f),
        topLeft = Offset(137f * s, 25f * s),
        size = Size(18f * s, 14f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Brass Cabside Numberplate "4472"
    drawRoundRect(color = bufferRed, topLeft = Offset(134f * s, 48f * s), size = Size(24f * s, 9f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    drawRoundRect(color = brassGold, topLeft = Offset(134f * s, 48f * s), size = Size(24f * s, 9f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s), style = Stroke(width = 1f * s))

    // =========================================================================
    // 3. BRITISH TAPERED BOILER & CASING (x = 166..300)
    // =========================================================================
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(britishGreen.copy(alpha = 0.95f), britishGreen, Color(0xFF144D24)),
            startY = 22f * s,
            endY = 76f * s
        ),
        topLeft = Offset(166f * s, 22f * s),
        size = Size(134f * s, 54f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )
    // Polished Brass Boiler Bands
    for (bb in 0..4) {
        val bx = (182f + bb * 26f) * s
        drawLine(color = goldLining, start = Offset(bx, 22f * s), end = Offset(bx, 76f * s), strokeWidth = 2f * s)
    }

    // Curved Splasher Running Plate over massive driving wheels
    val splasherPath = Path().apply {
        moveTo(164f * s, 76f * s)
        lineTo(164f * s, 70f * s)
        cubicTo(190f * s, 64f * s, 220f * s, 64f * s, 240f * s, 70f * s)
        cubicTo(260f * s, 64f * s, 290f * s, 64f * s, 305f * s, 76f * s)
        close()
    }
    drawPath(splasherPath, color = britishGreen)
    drawPath(splasherPath, color = goldLining, style = Stroke(width = 1.5f * s))

    // Curved Brass Nameplate "FLYING SCOTSMAN" on Central Splasher
    drawRoundRect(color = bufferRed, topLeft = Offset(215f * s, 64f * s), size = Size(46f * s, 6f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawRoundRect(color = brassGold, topLeft = Offset(215f * s, 64f * s), size = Size(46f * s, 6f * s), cornerRadius = CornerRadius(1f * s, 1f * s), style = Stroke(width = 0.8f * s))

    // Top Fittings: Ross Pop Safety Valve Bonnet (Brass) & Steam Dome
    drawRoundRect(color = brassGold, topLeft = Offset(188f * s, 14f * s), size = Size(14f * s, 10f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = britishGreen, topLeft = Offset(245f * s, 12f * s), size = Size(20f * s, 12f * s), cornerRadius = CornerRadius(6f * s, 6f * s))

    // =========================================================================
    // 4. SMOKEBOX & BRITISH DOUBLE CHIMNEY (x = 298..342)
    // =========================================================================
    drawRoundRect(
        color = blackIron,
        topLeft = Offset(298f * s, 22f * s),
        size = Size(44f * s, 54f * s),
        cornerRadius = CornerRadius(4f * s, 4f * s)
    )
    // Streamlined British Double Chimney (Kylchap)
    drawRoundRect(color = blackIron, topLeft = Offset(312f * s, 8f * s), size = Size(18f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawOval(color = brassGold, topLeft = Offset(310f * s, 6f * s), size = Size(22f * s, 5f * s))

    // Smokebox Front Door, Hinge Dart & Lamp
    drawCircle(color = Color(0xFF23272E), radius = 22f * s, center = Offset(340f * s, 50f * s))
    drawCircle(color = brassGold, radius = 3.5f * s, center = Offset(340f * s, 50f * s)) // Central wheel locking dart
    // British Vintage Oil Headlamp
    drawRoundRect(color = Color.White, topLeft = Offset(336f * s, 18f * s), size = Size(8f * s, 10f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 2.5f * s, center = Offset(340f * s, 23f * s))

    // Smoke Deflectors (German-style blinkers fitted to A3 Scotsman)
    val deflectorPath = Path().apply {
        moveTo(316f * s, 20f * s)
        lineTo(346f * s, 20f * s)
        lineTo(348f * s, 70f * s)
        lineTo(316f * s, 70f * s)
        close()
    }
    drawPath(deflectorPath, color = britishGreen)
    drawPath(deflectorPath, color = goldLining, style = Stroke(width = 1.2f * s))

    // Red Front Buffer Beam & British Round Buffers
    drawRoundRect(color = bufferRed, topLeft = Offset(336f * s, 76f * s), size = Size(18f * s, 12f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawBritishBuffers(348f * s, 82f * s, s, isFront = true)

    // =========================================================================
    // 5. 4-6-2 WHEEL ARRANGEMENT & WALSCHAERTS VALVE GEAR (Accurately aligned at 122f * s)
    // =========================================================================
    // Trailing Cartazzi Axle Wheel under Cab (Resting on rail at 122f * s)
    drawRailWheelWithRadius(146f * s, 112f * s, 10f * s, wheelAngleRad)

    // Three Massive 6ft 8in Driving Wheels (Realistically spaced: not too close, just right, resting at 122f * s)
    val drivers = listOf(200f, 244f, 288f)
    for (wx in drivers) {
        drawSteamDriverWheelDetailed(wx * s, 104f * s, 18f * s, wheelAngleRad, isSpoked = true, s = s)
    }
    // Polished Steel Connecting Side Rod
    drawAnimatedSideRod(drivers.first() * s, drivers.last() * s, 104f * s, wheelAngleRad, s)

    // Piston Cylinder Casing (Matte Black)
    drawRoundRect(color = blackIron, topLeft = Offset(296f * s, 92f * s), size = Size(26f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // Front 2-Axle (4-Wheel) Leading Bogie (Resting on rail at 122f * s)
    drawRailWheelWithRadius(322f * s, 112f * s, 10f * s, wheelAngleRad)
    drawRailWheelWithRadius(340f * s, 112f * s, 10f * s, wheelAngleRad)
}

/**
 * Supersonic Hyper Steam (Futuristic Art-Deco High-Speed Steam Turbine)
 */
fun DrawScope.drawHyperSteamLocomotive(
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    val hyperPath = Path().apply {
        moveTo(10f * s, 30f * s)
        lineTo(280f * s, 20f * s)
        lineTo(352f * s, 54f * s)
        lineTo(340f * s, 84f * s)
        lineTo(10f * s, 84f * s)
        close()
    }
    drawPath(
        hyperPath,
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF0F172A))
        )
    )
    // Supersonic Wing Fin
    drawRoundRect(color = Color(0xFFFACC15), topLeft = Offset(140f * s, 12f * s), size = Size(60f * s, 10f * s), cornerRadius = CornerRadius(3f * s, 3f * s))

    // Enclosed disc turbine wheels
    for (i in 0 until 5) {
        val wx = 120f + i * 40f
        drawModernDiscWheel(wx * s, 94f * s, 14f * s, wheelAngleRad)
    }
}

// ============================================================================
// 3. AMERICAN CLASSIC DIESELS
// ============================================================================

/**
 * EMD F7-A Bulldog Nose Cab Unit (Burlington Route / Santa Fe Warbonnet)
 * Features iconic curved bulldog snout, dual Mars/high-beam headlights,
 * horizontal stainless steel side grills, portholes, Blomberg B-trucks.
 */
fun DrawScope.drawBurlingtonF7Locomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float
) {
    // Underframe & Blomberg Trucks
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(12f * s, 82f * s), size = Size(336f * s, 16f * s), cornerRadius = CornerRadius(3f * s, 3f * s))
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(130f * s, 86f * s), size = Size(100f * s, 20f * s), cornerRadius = CornerRadius(4f * s, 4f * s))

    // Two Blomberg 2-Axle Trucks (American with Sand Hoses)
    drawBlombergTwoAxleBogie(72f * s, 100f * s, wheelAngleRad, s)
    drawBlombergTwoAxleBogie(272f * s, 100f * s, wheelAngleRad, s)

    // Full Streamlined Body with Curved Bulldog Nose (Front at Right x=280..352)
    val f7Body = Path().apply {
        moveTo(14f * s, 24f * s)
        lineTo(280f * s, 24f * s)
        // Bulldog curved nose curve
        cubicTo(
            310f * s, 24f * s,
            348f * s, 40f * s,
            352f * s, 68f * s
        )
        lineTo(352f * s, 82f * s)
        lineTo(14f * s, 82f * s)
        close()
    }
    drawPath(
        f7Body,
        brush = Brush.verticalGradient(
            colors = listOf(bodyColor, bodyColor.copy(alpha = 0.85f)),
            startY = 24f * s,
            endY = 82f * s
        )
    )

    // Santa Fe Warbonnet / Burlington Stripe Accent along Nose
    val warbonnetStripe = Path().apply {
        moveTo(240f * s, 82f * s)
        lineTo(290f * s, 42f * s)
        cubicTo(
            315f * s, 42f * s,
            346f * s, 54f * s,
            352f * s, 68f * s
        )
        lineTo(352f * s, 82f * s)
        close()
    }
    drawPath(warbonnetStripe, color = stripeColor)

    // Curved Wraparound Windshields
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(272f * s, 30f * s), size = Size(26f * s, 14f * s), cornerRadius = CornerRadius(4f * s, 4f * s))
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(274f * s, 32f * s), size = Size(22f * s, 10f * s), cornerRadius = CornerRadius(3f * s, 3f * s))

    // Dual Headlights: Main High-Beam + Lower Mars Oscillating Signal Light
    drawCircle(color = Color(0xFF0F172A), radius = 6f * s, center = Offset(344f * s, 54f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 4f * s, center = Offset(344f * s, 54f * s))
    drawCircle(color = Color(0xFF0F172A), radius = 6f * s, center = Offset(348f * s, 68f * s))
    drawCircle(color = Color(0xFFEF4444), radius = 4f * s, center = Offset(348f * s, 68f * s))

    // Stainless Steel Side Grilles
    drawRoundRect(
        brush = Brush.horizontalGradient(listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))),
        topLeft = Offset(40f * s, 32f * s),
        size = Size(210f * s, 8f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Bodyside Round Porthole Windows
    listOf(80f, 130f, 180f).forEach { px ->
        drawCircle(color = Color(0xFF0F172A), radius = 6f * s, center = Offset(px * s, 54f * s))
        drawCircle(color = Color(0xFF38BDF8), radius = 4.5f * s, center = Offset(px * s, 54f * s))
    }
}

/**
 * Commonwealth A1A 3-Axle Cast Steel Passenger Truck
 * Used on the ALCO PA-1 and EMD E-Units.
 * Features 3 axles (outer powered, center idler), curved drop-equalizer beams,
 * dual elliptic leaf springs, Hyatt roller bearing journal boxes, and clasp brake rigging.
 */
fun DrawScope.drawCommonwealthThreeAxleBogie(
    centerX: Float,
    centerY: Float,
    wheelAngleRad: Float,
    s: Float
) {
    val wheelY = centerY + 8f * s
    val wheelSpacing = 28f * s

    // Heavy Cast Steel Sideframe (spanning all 3 axles)
    drawRoundRect(
        color = Color(0xFF1E242B),
        topLeft = Offset(centerX - 48f * s, centerY - 8f * s),
        size = Size(96f * s, 14f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Three 40-inch Passenger Wheels (Touch rail perfectly at wheelY + 14 = 122f * s)
    val axleXPositions = listOf(centerX - wheelSpacing, centerX, centerX + wheelSpacing)
    axleXPositions.forEach { wx ->
        drawRailWheel(wx, wheelY, wheelAngleRad, s)

        // Heavy Cast Journal Box with rotating Timken/Hyatt roller bearing end cap
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(wx - 5f * s, wheelY - 5f * s),
            size = Size(10f * s, 10f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
        drawCircle(
            color = Color(0xFF94A3B8),
            radius = 2.5f * s,
            center = Offset(wx, wheelY)
        )

        // Clasp Brake Shoes flanking each wheel
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(wx - 13f * s, wheelY - 2f * s),
            size = Size(2.5f * s, 8f * s)
        )
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(wx + 10.5f * s, wheelY - 2f * s),
            size = Size(2.5f * s, 8f * s)
        )
    }

    // Curved Drop-Equalizer Bars arching between journals
    val equalizerLeft = Path().apply {
        moveTo(centerX - wheelSpacing, wheelY - 4f * s)
        cubicTo(
            centerX - wheelSpacing + 8f * s, centerY + 2f * s,
            centerX - 8f * s, centerY + 2f * s,
            centerX, wheelY - 4f * s
        )
    }
    drawPath(equalizerLeft, color = Color(0xFF475569), style = Stroke(width = 3.2f * s, cap = StrokeCap.Round))

    val equalizerRight = Path().apply {
        moveTo(centerX, wheelY - 4f * s)
        cubicTo(
            centerX + 8f * s, centerY + 2f * s,
            centerX + wheelSpacing - 8f * s, centerY + 2f * s,
            centerX + wheelSpacing, wheelY - 4f * s
        )
    }
    drawPath(equalizerRight, color = Color(0xFF475569), style = Stroke(width = 3.2f * s, cap = StrokeCap.Round))

    // Dual Elliptic Leaf Spring Packs in equalizer pockets
    listOf(centerX - 14f * s, centerX + 14f * s).forEach { sx ->
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(sx - 7f * s, centerY - 2f * s),
            size = Size(14f * s, 6f * s),
            cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
        )
        // Spring bands
        drawLine(
            color = Color(0xFF64748B),
            start = Offset(sx, centerY - 2f * s),
            end = Offset(sx, centerY + 4f * s),
            strokeWidth = 2f * s
        )
    }

    // American Sand Hoses: flexible rubber hose curving down to railhead with brass nozzle
    val sandHoseRubber = Color(0xFF111827)
    val sandNozzleBrass = Color(0xFFD97706)

    val frontHose = Path().apply {
        moveTo(centerX - 42f * s, centerY + 2f * s)
        cubicTo(
            centerX - 50f * s, centerY + 8f * s,
            centerX - 52f * s, wheelY + 8f * s,
            centerX - 48f * s, 120f * s
        )
    }
    drawPath(frontHose, color = sandHoseRubber, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round))
    drawRoundRect(
        color = sandNozzleBrass,
        topLeft = Offset(centerX - 50f * s, 118.5f * s),
        size = Size(4f * s, 2.8f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )

    val rearHose = Path().apply {
        moveTo(centerX + 42f * s, centerY + 2f * s)
        cubicTo(
            centerX + 50f * s, centerY + 8f * s,
            centerX + 52f * s, wheelY + 8f * s,
            centerX + 48f * s, 120f * s
        )
    }
    drawPath(rearHose, color = sandHoseRubber, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round))
    drawRoundRect(
        color = sandNozzleBrass,
        topLeft = Offset(centerX + 46f * s, 118.5f * s),
        size = Size(4f * s, 2.8f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )
}

/**
 * EMD Blomberg B / Blomberg M 2-Axle Passenger & Freight Truck
 * Used on the EMD F40PH, GP40-2, F7, and F59PHI.
 * Features 2 axles, iconic curved swing-hanger brackets, dual secondary coil springs,
 * hydraulic snubber shocks, roller bearing axle caps, and clasp brake rigging.
 */
fun DrawScope.drawBlombergTwoAxleBogie(
    centerX: Float,
    centerY: Float,
    wheelAngleRad: Float,
    s: Float
) {
    val wheelY = centerY + 8f * s
    val wheelSpacing = 22f * s

    // Heavy Cast Steel H-Frame Sideframe
    drawRoundRect(
        color = Color(0xFF1E242B),
        topLeft = Offset(centerX - 38f * s, centerY - 8f * s),
        size = Size(76f * s, 14f * s),
        cornerRadius = CornerRadius(3f * s, 3f * s)
    )

    // Two 40-inch Wheels (Touch rail at 122f * s)
    listOf(centerX - wheelSpacing, centerX + wheelSpacing).forEach { wx ->
        drawRailWheel(wx, wheelY, wheelAngleRad, s)

        // Roller Bearing Axle Box & Rotating End-Cap
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(wx - 5f * s, wheelY - 5f * s),
            size = Size(10f * s, 10f * s),
            cornerRadius = CornerRadius(2f * s, 2f * s)
        )
        drawCircle(
            color = Color(0xFF94A3B8),
            radius = 2.5f * s,
            center = Offset(wx, wheelY)
        )

        // Clasp Brake Shoes
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(wx - 13f * s, wheelY - 2f * s),
            size = Size(2.5f * s, 8f * s)
        )
        drawRect(
            color = Color(0xFF0F172A),
            topLeft = Offset(wx + 10.5f * s, wheelY - 2f * s),
            size = Size(2.5f * s, 8f * s)
        )
    }

    // Iconic Blomberg Curved Swing Hangers (Trademarks of Blomberg B/M trucks)
    val swingHangerLeft = Path().apply {
        moveTo(centerX - 10f * s, centerY - 8f * s)
        cubicTo(
            centerX - 16f * s, centerY - 2f * s,
            centerX - 16f * s, centerY + 4f * s,
            centerX - 8f * s, centerY + 6f * s
        )
    }
    drawPath(swingHangerLeft, color = Color(0xFF475569), style = Stroke(width = 2.8f * s, cap = StrokeCap.Round))

    val swingHangerRight = Path().apply {
        moveTo(centerX + 10f * s, centerY - 8f * s)
        cubicTo(
            centerX + 16f * s, centerY - 2f * s,
            centerX + 16f * s, centerY + 4f * s,
            centerX + 8f * s, centerY + 6f * s
        )
    }
    drawPath(swingHangerRight, color = Color(0xFF475569), style = Stroke(width = 2.8f * s, cap = StrokeCap.Round))

    // Center Bolster & Secondary Coil Springs
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(centerX - 8f * s, centerY - 6f * s),
        size = Size(16f * s, 12f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    // Coil spring coils
    for (i in 0 until 4) {
        val sy = centerY - 4f * s + (i * 2.5f * s)
        drawLine(
            color = Color(0xFF94A3B8),
            start = Offset(centerX - 6f * s, sy),
            end = Offset(centerX + 6f * s, sy),
            strokeWidth = 1.8f * s
        )
    }

    // Vertical Hydraulic Snubber Damper (M-type upgrade)
    drawLine(
        color = Color(0xFFCBD5E1),
        start = Offset(centerX + 12f * s, centerY - 6f * s),
        end = Offset(centerX + 12f * s, centerY + 4f * s),
        strokeWidth = 2.2f * s
    )

    // American Sand Hoses: flexible rubber hose curving down to railhead with brass nozzle
    val sandHoseRubber = Color(0xFF111827)
    val sandNozzleBrass = Color(0xFFD97706)

    val frontHose = Path().apply {
        moveTo(centerX - 30f * s, centerY)
        cubicTo(
            centerX - 38f * s, centerY + 6f * s,
            centerX - 40f * s, wheelY + 6f * s,
            centerX - 36f * s, 120f * s
        )
    }
    drawPath(frontHose, color = sandHoseRubber, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round))
    drawRoundRect(
        color = sandNozzleBrass,
        topLeft = Offset(centerX - 38f * s, 118.5f * s),
        size = Size(4f * s, 2.8f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )

    val rearHose = Path().apply {
        moveTo(centerX + 30f * s, centerY)
        cubicTo(
            centerX + 38f * s, centerY + 6f * s,
            centerX + 40f * s, wheelY + 6f * s,
            centerX + 36f * s, 120f * s
        )
    }
    drawPath(rearHose, color = sandHoseRubber, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round))
    drawRoundRect(
        color = sandNozzleBrass,
        topLeft = Offset(centerX + 34f * s, 118.5f * s),
        size = Size(4f * s, 2.8f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )
}

/**
 * Museum-Grade Accurate ALCO PA-1 "The Honor Dog of American Passenger Diesels"
 * Masterpiece of industrial designer Ray Patten & American Locomotive Company (1946).
 * Features authentic 15-foot dramatic chisel nose, massive horizontal chrome grille with ALCO badge,
 * twin bullet sealed-beam headlights + Mars safety signal beacon, V-canted teardrop windshields with wipers,
 * 4 circular round maritime porthole windows with warm engine room glow, upper Farr stainless steel intake band,
 * twin ALCO 244 exhaust stacks with carbon soot weathering, roof dynamic brake grid, dual rear radiator fans,
 * and 6-wheel Commonwealth A1A passenger trucks with drop-equalizer bars!
 */
fun DrawScope.drawAlcoPa1Locomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String = ""
) {
    val isDaylight = variant.contains("DAYLIGHT") || variant.contains("SP")
    val isBluebonnet = variant.contains("BLUEBONNET") || variant.contains("BLUE")
    val isWarbonnet = !isDaylight && !isBluebonnet

    val stainlessBody = Color(0xFFE2E8F0)
    val redWarbonnet = Color(0xFFC62828)
    val goldYellowStripe = Color(0xFFFACC15)
    val pinstripeBlack = Color(0xFF0F172A)
    val bluebonnetBlue = Color(0xFF0284C7)
    val spScarlet = Color(0xFFDC2626)
    val spOrange = Color(0xFFEA580C)
    val spBlack = Color(0xFF181A1C)

    val activeNoseColor = when {
        isDaylight -> spScarlet
        isBluebonnet -> bluebonnetBlue
        else -> redWarbonnet
    }

    // 1. UNDERFRAME, HEAVY FISHBELLY SILL & CENTRAL FUEL/WATER TANKS
    // Main heavy side sill plate
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B))),
        topLeft = Offset(10f * s, 80f * s),
        size = Size(344f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Underslung Central Fuel Tank (1,200 gal) & Steam Generator Water Tank (1,000 gal)
    val tankPath = Path().apply {
        moveTo(125f * s, 84f * s)
        lineTo(238f * s, 84f * s)
        lineTo(232f * s, 104f * s)
        lineTo(131f * s, 104f * s)
        close()
    }
    drawPath(tankPath, color = Color(0xFF0F172A))
    // Fuel tank weld ribs & sight glass
    drawLine(color = Color(0xFF334155), start = Offset(155f * s, 84f * s), end = Offset(153f * s, 103f * s), strokeWidth = 1.5f * s)
    drawLine(color = Color(0xFF334155), start = Offset(205f * s, 84f * s), end = Offset(203f * s, 103f * s), strokeWidth = 1.5f * s)
    // Fuel filler cap and red emergency cutoff button
    drawCircle(color = Color(0xFFCBD5E1), radius = 3f * s, center = Offset(180f * s, 94f * s))
    drawCircle(color = Color(0xFFEF4444), radius = 2f * s, center = Offset(188f * s, 94f * s))

    // Dual main air reservoirs with cooling piping
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(135f * s, 86f * s), size = Size(92f * s, 6f * s), cornerRadius = CornerRadius(2.5f * s, 2.5f * s))

    // 2. TWO COMMONWEALTH A1A 3-AXLE PASSENGER TRUCKS (6 wheels per locomotive)
    drawCommonwealthThreeAxleBogie(68f * s, 100f * s, wheelAngleRad, s)
    drawCommonwealthThreeAxleBogie(282f * s, 100f * s, wheelAngleRad, s)

    // 3. ICONIC RAY PATTEN LONG CHISEL NOSE CARBODY (The "Honor Dog" Snout)
    val alcoCarbody = Path().apply {
        moveTo(10f * s, 82f * s)
        lineTo(10f * s, 26f * s)
        quadraticTo(12f * s, 20f * s, 20f * s, 20f * s)
        lineTo(252f * s, 20f * s)
        quadraticTo(285f * s, 22f * s, 296f * s, 26f * s)
        lineTo(356f * s, 62f * s)
        lineTo(356f * s, 82f * s)
        lineTo(10f * s, 82f * s)
        close()
    }

    if (isDaylight) {
        // Southern Pacific "Daylight" Livery: Scarlet Red, Sunset Orange, and Black
        drawPath(alcoCarbody, color = spOrange)
        // Black roof crown
        val spRoof = Path().apply {
            moveTo(10f * s, 26f * s)
            quadraticTo(12f * s, 20f * s, 20f * s, 20f * s)
            lineTo(252f * s, 20f * s)
            quadraticTo(285f * s, 22f * s, 296f * s, 26f * s)
            lineTo(306f * s, 30f * s)
            lineTo(10f * s, 30f * s)
            close()
        }
        drawPath(spRoof, color = spBlack)
        // Scarlet Red upper panel
        drawRect(color = spScarlet, topLeft = Offset(10f * s, 30f * s), size = Size(296f * s, 18f * s))
        // White separation pinstripes
        drawLine(color = Color.White, start = Offset(10f * s, 30f * s), end = Offset(306f * s, 30f * s), strokeWidth = 1.5f * s)
        drawLine(color = Color.White, start = Offset(10f * s, 48f * s), end = Offset(324f * s, 48f * s), strokeWidth = 1.5f * s)
        drawLine(color = Color.White, start = Offset(10f * s, 74f * s), end = Offset(356f * s, 74f * s), strokeWidth = 1.5f * s)
        // Black lower sill skirt
        drawRect(color = spBlack, topLeft = Offset(10f * s, 74f * s), size = Size(346f * s, 8f * s))
        // SP Red Nose Wing
        val spNoseWing = Path().apply {
            moveTo(356f * s, 82f * s)
            lineTo(356f * s, 62f * s)
            lineTo(296f * s, 26f * s)
            lineTo(280f * s, 26f * s)
            cubicTo(290f * s, 48f * s, 310f * s, 68f * s, 356f * s, 74f * s)
            close()
        }
        drawPath(spNoseWing, color = spScarlet)
    } else {
        // Gleaming brushed stainless steel carbody finish
        drawPath(
            alcoCarbody,
            brush = Brush.verticalGradient(
                colors = listOf(
                    stainlessBody.copy(alpha = 0.98f),
                    stainlessBody,
                    Color(0xFFCBD5E1),
                    Color(0xFF94A3B8)
                ),
                startY = 20f * s,
                endY = 82f * s
            )
        )

        // 4. SANTA FE WARBONNET WING / BLUEBONNET ACCENTS
        val warbonnetWing = Path().apply {
            moveTo(356f * s, 82f * s)
            lineTo(356f * s, 62f * s)
            lineTo(296f * s, 26f * s)
            quadraticTo(285f * s, 22f * s, 252f * s, 20f * s)
            lineTo(240f * s, 20f * s)
            cubicTo(
                248f * s, 42f * s,
                260f * s, 64f * s,
                160f * s, 82f * s
            )
            lineTo(356f * s, 82f * s)
            close()
        }
        drawPath(
            warbonnetWing,
            brush = Brush.verticalGradient(
                colors = listOf(
                    activeNoseColor.copy(alpha = 0.95f),
                    activeNoseColor,
                    activeNoseColor.copy(alpha = 0.88f)
                ),
                startY = 20f * s,
                endY = 82f * s
            )
        )

        // Iconic Yellow and Black Warbonnet Pinstripe Edges
        val pinstripeYellow = Path().apply {
            moveTo(240f * s, 20f * s)
            cubicTo(
                248f * s, 42f * s,
                260f * s, 64f * s,
                160f * s, 82f * s
            )
        }
        // Black shadow backing pinstripe
        drawPath(pinstripeYellow, color = pinstripeBlack, style = Stroke(width = 3.2f * s, cap = StrokeCap.Round))
        // Polished Gold-Yellow pinstripe
        drawPath(pinstripeYellow, color = goldYellowStripe, style = Stroke(width = 2.0f * s, cap = StrokeCap.Round))

        // Santa Fe Cross & Circle Nose Emblem
        val emblemX = 338f * s
        val emblemY = 54f * s
        drawCircle(color = goldYellowStripe, radius = 5.5f * s, center = Offset(emblemX, emblemY), style = Stroke(width = 1.4f * s))
        drawLine(color = goldYellowStripe, start = Offset(emblemX - 6.5f * s, emblemY), end = Offset(emblemX + 6.5f * s, emblemY), strokeWidth = 1.4f * s)
        drawLine(color = goldYellowStripe, start = Offset(emblemX, emblemY - 6.5f * s), end = Offset(emblemX, emblemY + 6.5f * s), strokeWidth = 1.4f * s)
        drawCircle(color = Color(0xFF0F172A), radius = 2.5f * s, center = Offset(emblemX, emblemY))
    }

    // 5. RAY PATTEN'S MASTERPIECE: MASSIVE HORIZONTAL CHROME FRONT GRILLE
    // Lower front nose wrap-around intake grille
    val grilleBox = Path().apply {
        moveTo(326f * s, 63f * s)
        lineTo(355f * s, 63f * s)
        lineTo(355f * s, 81f * s)
        lineTo(326f * s, 81f * s)
        close()
    }
    // Deep radiator intake shadow recess
    drawPath(grilleBox, color = Color(0xFF0F172A))
    // Polished stainless steel outer border
    drawPath(grilleBox, color = Color(0xFFE2E8F0), style = Stroke(width = 1.8f * s))

    // Horizontal Chrome Grille Bars (Authentic ALCO PA signature styling)
    for (i in 0 until 6) {
        val gy = 65f * s + (i * 2.8f * s)
        // Shading
        drawLine(color = Color(0xFF1E293B), start = Offset(327f * s, gy + 0.8f * s), end = Offset(354f * s, gy + 0.8f * s), strokeWidth = 1.5f * s)
        // Chrome highlight bar
        drawLine(color = Color(0xFFF8FAFC), start = Offset(327f * s, gy), end = Offset(354f * s, gy), strokeWidth = 1.2f * s)
    }
    // Vertical chrome center divider bar
    drawLine(color = Color(0xFFF8FAFC), start = Offset(341f * s, 63f * s), end = Offset(341f * s, 81f * s), strokeWidth = 1.8f * s)

    // Polished Cast ALCO Builder Plate Badge (above grille)
    drawRoundRect(
        color = Color(0xFFB45309), // Cast brass base
        topLeft = Offset(346f * s, 57.5f * s),
        size = Size(7f * s, 3.5f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )
    drawRoundRect(
        color = Color(0xFFFDE047), // Polished brass lettering
        topLeft = Offset(347f * s, 58.2f * s),
        size = Size(5f * s, 2.0f * s),
        cornerRadius = CornerRadius(0.5f * s, 0.5f * s)
    )

    // 6. FRONT HEADLIGHTS, MARS SIGNAL BEACON & NOSE PILOT
    // Bullet-shaped chrome dual sealed-beam housing
    drawCircle(color = Color(0xFF334155), radius = 6.5f * s, center = Offset(348f * s, 48f * s))
    drawCircle(color = Color(0xFFF8FAFC), radius = 5.2f * s, center = Offset(348f * s, 48f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 3.8f * s, center = Offset(348f * s, 48f * s))
    // Headlight glowing forward cone
    drawCircle(color = Color(0x55FEF08A), radius = 10f * s, center = Offset(350f * s, 48f * s))

    // Upper Mars Signal Emergency Beacon (Oscillating Red/White signal light)
    drawCircle(color = Color(0xFF1E293B), radius = 4.5f * s, center = Offset(336f * s, 38f * s))
    drawCircle(color = Color(0xFFEF4444), radius = 3.0f * s, center = Offset(336f * s, 38f * s))
    drawCircle(color = Color(0x44EF4444), radius = 7f * s, center = Offset(336f * s, 38f * s))

    // Illuminated vintage numberboards on nose shoulders ("51L")
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(318f * s, 35f * s),
        size = Size(11f * s, 5.5f * s),
        cornerRadius = CornerRadius(1.2f * s, 1.2f * s)
    )
    drawRoundRect(
        color = Color(0xFFFEF08A),
        topLeft = Offset(319f * s, 36f * s),
        size = Size(9f * s, 3.5f * s),
        cornerRadius = CornerRadius(0.8f * s, 0.8f * s)
    )

    // Front Pilot / Cowcatcher & Coupler Pocket
    val pilotPath = Path().apply {
        moveTo(344f * s, 82f * s)
        lineTo(358f * s, 82f * s)
        lineTo(358f * s, 90f * s)
        lineTo(346f * s, 94f * s)
        close()
    }
    drawPath(pilotPath, color = Color(0xFF1E242B))
    // Knuckle coupler shank & cut lever bar
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(356f * s, 86f * s), size = Size(8f * s, 5f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawLine(color = Color(0xFF94A3B8), start = Offset(348f * s, 86f * s), end = Offset(358f * s, 86f * s), strokeWidth = 1.5f * s)
    // Air brake trainline hoses
    drawLine(color = Color(0xFF0F172A), start = Offset(354f * s, 88f * s), end = Offset(357f * s, 96f * s), strokeWidth = 2f * s, cap = StrokeCap.Round)

    // 7. V-CANTED TEARDROP CAB WINDSHIELDS & ROOF HORN
    // Raked teardrop windshield frame
    val windshieldPath = Path().apply {
        moveTo(268f * s, 25f * s)
        lineTo(294f * s, 28f * s)
        lineTo(290f * s, 42f * s)
        lineTo(265f * s, 40f * s)
        close()
    }
    drawPath(windshieldPath, color = Color(0xFF0F172A))
    drawPath(windshieldPath, color = Color(0xFF475569), style = Stroke(width = 1.4f * s))
    // Tinted sky-reflection glass
    val glassPath = Path().apply {
        moveTo(270f * s, 27f * s)
        lineTo(292f * s, 29.5f * s)
        lineTo(288f * s, 40f * s)
        lineTo(267f * s, 38.5f * s)
        close()
    }
    drawPath(glassPath, brush = Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7))))
    // Center divider pillar & stainless wiper arm
    drawLine(color = Color(0xFFCBD5E1), start = Offset(280f * s, 26f * s), end = Offset(278f * s, 41f * s), strokeWidth = 1.4f * s)
    drawLine(color = Color(0xFF1E293B), start = Offset(275f * s, 38f * s), end = Offset(282f * s, 30f * s), strokeWidth = 1.2f * s)

    // Cab side crew window & rear-view mirror
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(246f * s, 27f * s),
        size = Size(15f * s, 12f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )
    drawRoundRect(
        color = Color(0xFF38BDF8),
        topLeft = Offset(247.5f * s, 28.5f * s),
        size = Size(12f * s, 9f * s),
        cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
    )
    // Chrome side rearview mirror
    drawLine(color = Color(0xFFE2E8F0), start = Offset(263f * s, 29f * s), end = Offset(263f * s, 38f * s), strokeWidth = 1.8f * s)

    // Nathan/Leslie Triple-Chime Brass Air Horn over cab roof
    val hornX = 260f * s
    drawRoundRect(color = Color(0xFFB45309), topLeft = Offset(hornX, 15f * s), size = Size(8f * s, 3f * s), cornerRadius = CornerRadius(0.8f * s, 0.8f * s))
    drawCircle(color = Color(0xFFFDE047), radius = 2.2f * s, center = Offset(hornX + 9f * s, 16.5f * s))
    drawCircle(color = Color(0xFFFDE047), radius = 1.8f * s, center = Offset(hornX + 7f * s, 14.5f * s))

    // 8. FOUR CLASSIC ROUND MARITIME PORTHOLE WINDOWS & FARR AIR INTAKES
    // Upper full-length Farr stainless steel horizontal intake louver band
    drawRoundRect(
        brush = Brush.horizontalGradient(listOf(Color(0xFFCBD5E1), Color(0xFFF1F5F9), Color(0xFF94A3B8))),
        topLeft = Offset(35f * s, 26f * s),
        size = Size(205f * s, 8f * s),
        cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
    )
    for (i in 0 until 4) {
        val ly = 27.5f * s + (i * 1.8f * s)
        drawLine(color = Color(0xFF334155), start = Offset(37f * s, ly), end = Offset(238f * s, ly), strokeWidth = 0.8f * s)
    }

    // Four Classic Round Portholes into the 2,000 HP ALCO 244 V16 Engine Room
    val portholeXPositions = listOf(65f, 115f, 165f, 215f)
    portholeXPositions.forEach { px ->
        val pcx = px * s
        val pcy = 52f * s
        // Polished Chrome Bezel Ring
        drawCircle(color = Color(0xFF64748B), radius = 7.5f * s, center = Offset(pcx, pcy))
        drawCircle(color = Color(0xFFE2E8F0), radius = 6.5f * s, center = Offset(pcx, pcy))
        // Heavy Black Rubber Gasket
        drawCircle(color = Color(0xFF0F172A), radius = 5.2f * s, center = Offset(pcx, pcy))
        // Engine Room Glass with warm amber internal illumination showing ALCO 244 V16 block
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFEF08A), Color(0xFFF59E0B), Color(0xFFB45309)),
                center = Offset(pcx, pcy),
                radius = 4.2f * s
            ),
            radius = 4.2f * s,
            center = Offset(pcx, pcy)
        )
        // Internal engine cylinder shadow silhouette
        drawLine(color = Color(0xFF451A03), start = Offset(pcx - 2f * s, pcy + 2f * s), end = Offset(pcx + 2f * s, pcy - 2f * s), strokeWidth = 1.2f * s)
    }

    // Engine access doors, seams, and recessed latch handles
    listOf(48f, 98f, 148f, 198f, 235f).forEach { dx ->
        drawLine(color = Color(0xFF94A3B8), start = Offset(dx * s, 36f * s), end = Offset(dx * s, 80f * s), strokeWidth = 0.9f * s)
        // Door latch handle
        drawRect(color = Color(0xFF0F172A), topLeft = Offset((dx + 3f) * s, 54f * s), size = Size(2f * s, 4f * s))
    }

    // 9. ROOF EQUIPMENT: DUAL ALCO 244 TURBO EXHAUSTS, DYNAMIC BRAKE & RADIATOR FANS
    // Twin circular diesel exhaust stacks for the ALCO 244 V16 with heat soot rings
    listOf(100f, 145f).forEach { ex ->
        // Dark soot exhaust stain on roof
        drawOval(color = Color(0x661E293B), topLeft = Offset((ex - 7f) * s, 16f * s), size = Size(14f * s, 6f * s))
        // Raised exhaust manifold stack
        drawRoundRect(color = Color(0xFF1E242B), topLeft = Offset((ex - 4f) * s, 15f * s), size = Size(8f * s, 5f * s), cornerRadius = CornerRadius(1.2f * s, 1.2f * s))
        drawCircle(color = Color(0xFF0F172A), radius = 2.5f * s, center = Offset(ex * s, 17f * s))
    }

    // Recessed Dynamic Brake Grid
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(175f * s, 17f * s), size = Size(35f * s, 4f * s), cornerRadius = CornerRadius(1f * s, 1f * s))

    // Rear Radiator Section with Dual 48-inch Cooling Fan Hatches
    listOf(35f, 65f).forEach { rx ->
        drawCircle(color = Color(0xFF334155), radius = 6f * s, center = Offset(rx * s, 19f * s))
        drawCircle(color = Color(0xFF0F172A), radius = 5f * s, center = Offset(rx * s, 19f * s))
        // Fan blade spokes
        for (f in 0 until 4) {
            val fanAngle = (f * PI / 4.0).toFloat()
            val fx = rx * s + cos(fanAngle) * 4f * s
            val fy = 19f * s + sin(fanAngle) * 2f * s
            drawLine(color = Color(0xFF64748B), start = Offset(rx * s, 19f * s), end = Offset(fx, fy), strokeWidth = 1f * s)
        }
    }
}

/**
 * Museum-Grade Accurate EMD F40PH / F40PH-2 / F40PH-3 Commuter & Passenger Diesel Locomotive
 * The legendary workhorse of Amtrak, VIA Rail Canada, and Metra ("The Screamer").
 * Features authentic forward-canted / reverse-rake windshield brow, low flat nose with 45-degree chamfers,
 * anti-climber pilot with functional ditch lights, twin vertical sealed-beam headlights + upper red strobe,
 * illuminated angled numberboards, Blomberg M 2-axle trucks with curved swing-hangers & coil springs,
 * lower fluted stainless steel side panels, dynamic brake blister, turbocharged EMD 16-645E3 exhaust stack,
 * three 48-inch radiator fans in a row, and the screaming rear Head-End Power (HEP) diesel generator compartment
 * with authentic rear HEP exhaust muffler pipe!
 */
fun DrawScope.drawAccurateF40PHLocomotive(
    train: TrainModel,
    bodyColor: Color,
    stripeColor: Color,
    wheelAngleRad: Float,
    s: Float,
    variant: String = ""
) {
    val isVia = train.id == "emd_f40ph_via" || train.name.contains("VIA")
    val isMetraPatriot = train.id == "emd_f40ph_metra" || variant.contains("PATRIOT")
    val isMetraClassic = train.id == "metra_f40ph_screamer" || train.id.startsWith("metra_")
    val isAmtrak = variant.contains("AMTRAK") || variant.contains("PHASE")

    val stainlessBody = Color(0xFFE2E8F0)
    val metraNavyBlue = Color(0xFF1E3A8A)
    val metraOrange = Color(0xFFEA580C)
    val viaYellow = Color(0xFFF59E0B)
    val viaGreen = Color(0xFF164E63)
    val amtrakRed = Color(0xFFDC2626)
    val amtrakBlue = Color(0xFF1E3A8A)

    val cabColor = when {
        isVia -> viaYellow
        isMetraPatriot -> metraNavyBlue
        isMetraClassic -> metraNavyBlue
        isAmtrak -> amtrakBlue
        else -> bodyColor
    }

    // 1. CHASSIS, HEAVY I-BEAM SILL & 1,800 GALLON SLOPED FUEL TANK
    // Heavy underframe steel sill
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(10f * s, 80f * s),
        size = Size(342f * s, 10f * s),
        cornerRadius = CornerRadius(2f * s, 2f * s)
    )

    // Underslung 1,800-gal Sloped Fuel Tank
    val tank = Path().apply {
        moveTo(122f * s, 84f * s)
        lineTo(238f * s, 84f * s)
        lineTo(230f * s, 104f * s)
        lineTo(130f * s, 104f * s)
        close()
    }
    drawPath(tank, color = Color(0xFF0F172A))
    // Fuel level sight gauge & emergency fuel cutoff switch
    drawRect(color = Color(0xFFCBD5E1), topLeft = Offset(175f * s, 92f * s), size = Size(3f * s, 8f * s))
    drawCircle(color = Color(0xFFEF4444), radius = 2.5f * s, center = Offset(185f * s, 94f * s))

    // Dual air reservoir tanks nestled ahead of the fuel tank
    drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset(240f * s, 85f * s), size = Size(20f * s, 7f * s), cornerRadius = CornerRadius(2f * s, 2f * s))

    // 2. TWO BLOMBERG M 2-AXLE TRUCKS
    drawBlombergTwoAxleBogie(75f * s, 100f * s, wheelAngleRad, s)
    drawBlombergTwoAxleBogie(285f * s, 100f * s, wheelAngleRad, s)

    // 3. FULL COWL CARBODY WITH FORWARD-CANTED / REVERSE-RAKE CAB BROW
    // EMD F40PH signature carbody profile:
    // Straight flat roof from rear x=12 up to cab x=290, then rakes forward to x=342 at mid-height,
    // where the flat anti-climber nose steps forward to x=348!
    val f40Body = Path().apply {
        moveTo(12f * s, 82f * s)
        lineTo(12f * s, 22f * s)
        // Square rear corner with slight bevel
        lineTo(16f * s, 18f * s)
        // Long flat cowl roof
        lineTo(292f * s, 18f * s)
        // Reverse-rake / forward-canted cab windshield brow
        lineTo(340f * s, 48f * s)
        // Low flat front nose deck
        lineTo(348f * s, 52f * s)
        // Vertical pilot nose face
        lineTo(348f * s, 82f * s)
        lineTo(12f * s, 82f * s)
        close()
    }
    drawPath(
        f40Body,
        brush = Brush.verticalGradient(
            colors = listOf(
                stainlessBody.copy(alpha = 0.98f),
                stainlessBody,
                Color(0xFFCBD5E1),
                Color(0xFF94A3B8)
            ),
            startY = 18f * s,
            endY = 82f * s
        )
    )

    // 4. LOWER FLUTED STAINLESS STEEL SIDE PANELS
    // Authentic fluted corrugated stainless steel panels (EMD & Budd construction)
    for (i in 0 until 9) {
        val cy = 52f * s + (i * 3.2f * s)
        drawLine(
            color = Color(0xFFCBD5E1),
            start = Offset(16f * s, cy),
            end = Offset(265f * s, cy),
            strokeWidth = 1.3f * s
        )
        drawLine(
            color = Color(0xFFF8FAFC),
            start = Offset(16f * s, cy - 0.7f * s),
            end = Offset(265f * s, cy - 0.7f * s),
            strokeWidth = 0.8f * s
        )
    }

    // 5. CAB FRONT NOSE & SPECIFIC LIVERY RENDERING
    // Front cab wedge section
    val cabWedge = Path().apply {
        moveTo(348f * s, 82f * s)
        lineTo(348f * s, 52f * s)
        lineTo(340f * s, 48f * s)
        lineTo(292f * s, 18f * s)
        lineTo(262f * s, 18f * s)
        lineTo(262f * s, 82f * s)
        close()
    }

    if (isVia) {
        // VIA Rail Canada Livery (Yellow chevron nose + green anti-glare cap)
        drawPath(cabWedge, color = viaYellow)
        // Dark green anti-glare nose deck patch
        val antiGlare = Path().apply {
            moveTo(348f * s, 52f * s)
            lineTo(340f * s, 48f * s)
            lineTo(315f * s, 34f * s)
            lineTo(330f * s, 52f * s)
            close()
        }
        drawPath(antiGlare, color = viaGreen)

        // Lower VIA Teal and Yellow sill stripes
        drawRect(color = viaGreen, topLeft = Offset(12f * s, 74f * s), size = Size(336f * s, 4f * s))
        drawRect(color = viaYellow, topLeft = Offset(12f * s, 78f * s), size = Size(336f * s, 3f * s))

        // VIA Lettering & Canadian Flag Maple Leaf
        drawRoundRect(color = Color(0xFF164E63), topLeft = Offset(140f * s, 40f * s), size = Size(42f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
        drawRect(color = viaYellow, topLeft = Offset(145f * s, 43f * s), size = Size(32f * s, 10f * s))
        // Red Maple Leaf Crest
        drawCircle(color = Color(0xFFDC2626), radius = 3.5f * s, center = Offset(195f * s, 48f * s))
    } else if (isMetraPatriot) {
        // Metra Patriot #120 (Navy Blue Cab + Camo Flanks + American Stars/Stripes)
        drawPath(cabWedge, color = metraNavyBlue)
        // Digital Camouflage blocks across bodyside
        val camoColors = listOf(Color(0xFF334155), Color(0xFF475569), Color(0xFF1E293B), Color(0xFF0F172A))
        for (i in 0 until 12) {
            val bx = 30f * s + (i * 18f * s)
            val by = 30f * s + ((i % 3) * 12f * s)
            drawRect(color = camoColors[i % camoColors.size], topLeft = Offset(bx, by), size = Size(14f * s, 10f * s))
        }
        // Waving American Ribbon Stripes (Red, White, Blue)
        drawRect(color = Color(0xFFDC2626), topLeft = Offset(12f * s, 68f * s), size = Size(336f * s, 3f * s))
        drawRect(color = Color.White, topLeft = Offset(12f * s, 71f * s), size = Size(336f * s, 3f * s))
        drawRect(color = Color(0xFF1E3A8A), topLeft = Offset(12f * s, 74f * s), size = Size(336f * s, 4f * s))
    } else if (isMetraClassic) {
        // Metra Classic Screamer (Navy Blue Cab, White Brow & Orange/White/Blue Speed Stripes)
        drawPath(cabWedge, color = metraNavyBlue)
        // Metra Triple Speed Stripes
        drawRect(color = metraOrange, topLeft = Offset(12f * s, 71f * s), size = Size(336f * s, 3.5f * s))
        drawRect(color = Color.White, topLeft = Offset(12f * s, 74.5f * s), size = Size(336f * s, 2.5f * s))
        drawRect(color = metraNavyBlue, topLeft = Offset(12f * s, 77f * s), size = Size(336f * s, 4f * s))

        // Metra Logo on cab side
        drawRoundRect(color = Color(0xFF1E3A8A), topLeft = Offset(245f * s, 45f * s), size = Size(18f * s, 12f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
        drawCircle(color = metraOrange, radius = 3.5f * s, center = Offset(254f * s, 51f * s))
    } else if (isAmtrak) {
        // Amtrak Phase III (Equal stripes: Red, White, Deep Blue)
        drawPath(cabWedge, color = amtrakBlue)
        drawRect(color = amtrakRed, topLeft = Offset(12f * s, 67f * s), size = Size(336f * s, 4f * s))
        drawRect(color = Color.White, topLeft = Offset(12f * s, 71f * s), size = Size(336f * s, 4f * s))
        drawRect(color = amtrakBlue, topLeft = Offset(12f * s, 75f * s), size = Size(336f * s, 5f * s))
    } else {
        drawPath(cabWedge, color = cabColor)
        drawRect(color = stripeColor, topLeft = Offset(12f * s, 74f * s), size = Size(336f * s, 6f * s))
    }

    // 6. FORWARD-RAKED TRAPEZOIDAL WINDSHIELDS & SLIDING CREW WINDOWS
    val windshield = Path().apply {
        moveTo(320f * s, 26f * s)
        lineTo(339f * s, 46f * s)
        lineTo(312f * s, 46f * s)
        lineTo(298f * s, 26f * s)
        close()
    }
    // Heavy rubber gasket frame
    drawPath(windshield, color = Color(0xFF0F172A))
    drawPath(windshield, color = Color(0xFF334155), style = Stroke(width = 1.6f * s))
    // Sky blue tint reflection
    val glass = Path().apply {
        moveTo(321f * s, 28f * s)
        lineTo(337f * s, 44f * s)
        lineTo(314f * s, 44f * s)
        lineTo(301f * s, 28f * s)
        close()
    }
    drawPath(glass, brush = Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0284C7))))
    // Center divider pillar & heavy wiper blade
    drawLine(color = Color(0xFF0F172A), start = Offset(310f * s, 27f * s), end = Offset(326f * s, 45f * s), strokeWidth = 1.8f * s)
    drawLine(color = Color(0xFF0F172A), start = Offset(322f * s, 44f * s), end = Offset(316f * s, 33f * s), strokeWidth = 1.2f * s)

    // Cab Side Sliding Crew Windows & Rearview Mirrors
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(268f * s, 28f * s), size = Size(24f * s, 16f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawRoundRect(color = Color(0xFF38BDF8), topLeft = Offset(270f * s, 30f * s), size = Size(20f * s, 12f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    // Crew window vertical slide sash
    drawLine(color = Color(0xFF0F172A), start = Offset(280f * s, 28f * s), end = Offset(280f * s, 44f * s), strokeWidth = 1.4f * s)
    // Chrome side rearview mirror bracket
    drawLine(color = Color(0xFFCBD5E1), start = Offset(294f * s, 29f * s), end = Offset(294f * s, 42f * s), strokeWidth = 2f * s)

    // 7. FRONT NOSE LIGHTS, PILOT DITCH LIGHTS, NUMBERBOARDS & SNOWPLOW
    // Twin Vertical Sealed-Beam Headlights
    drawCircle(color = Color(0xFF0F172A), radius = 5.5f * s, center = Offset(344f * s, 54f * s))
    drawCircle(color = Color(0xFFFEF08A), radius = 4.2f * s, center = Offset(344f * s, 54f * s))
    drawCircle(color = Color(0x66FEF08A), radius = 10f * s, center = Offset(346f * s, 54f * s))

    // Upper Emergency Red Mars / Strobe Light
    drawCircle(color = Color(0xFF0F172A), radius = 4f * s, center = Offset(344f * s, 64f * s))
    drawCircle(color = Color(0xFFEF4444), radius = 2.8f * s, center = Offset(344f * s, 64f * s))
    drawCircle(color = Color(0x44EF4444), radius = 6f * s, center = Offset(344f * s, 64f * s))

    // Authentic Pilot Ditch Lights (Crucial for Amtrak, VIA, Metra safety)
    val ditchLightY = 76f * s
    listOf(344f, 338f).forEach { dlx ->
        drawRoundRect(color = Color(0xFF1E293B), topLeft = Offset((dlx - 3f) * s, ditchLightY - 3f * s), size = Size(6f * s, 6f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
        drawCircle(color = Color(0xFFFEF08A), radius = 2.5f * s, center = Offset(dlx * s, ditchLightY))
        drawCircle(color = Color(0x55FEF08A), radius = 5.5f * s, center = Offset((dlx + 2f) * s, ditchLightY))
    }

    // Illuminated Angled Numberboards ("6437" / "120" / "100")
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(324f * s, 47f * s), size = Size(14f * s, 5f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawRoundRect(color = Color(0xFFFEF08A), topLeft = Offset(325f * s, 48f * s), size = Size(12f * s, 3f * s), cornerRadius = CornerRadius(0.6f * s, 0.6f * s))

    // Heavy duty front snowplow cowcatcher, cut-bar & MU air hoses
    val plowPath = Path().apply {
        moveTo(344f * s, 82f * s)
        lineTo(354f * s, 82f * s)
        lineTo(354f * s, 92f * s)
        lineTo(342f * s, 95f * s)
        close()
    }
    drawPath(plowPath, color = Color(0xFF1E242B))
    // Knuckle coupler shank & cut lever
    drawRoundRect(color = Color(0xFF0F172A), topLeft = Offset(352f * s, 87f * s), size = Size(7f * s, 5f * s), cornerRadius = CornerRadius(1f * s, 1f * s))
    drawLine(color = Color(0xFFCBD5E1), start = Offset(344f * s, 87f * s), end = Offset(354f * s, 87f * s), strokeWidth = 1.4f * s)
    drawLine(color = Color(0xFF0F172A), start = Offset(349f * s, 89f * s), end = Offset(352f * s, 97f * s), strokeWidth = 2f * s, cap = StrokeCap.Round)

    // Center nose maintenance door with small porthole inspection window
    drawRect(color = Color(0x33000000), topLeft = Offset(336f * s, 56f * s), size = Size(8f * s, 22f * s))
    drawCircle(color = Color(0xFF38BDF8), radius = 1.8f * s, center = Offset(340f * s, 60f * s))

    // 8. ROOF ARCHITECTURE, DYNAMIC BRAKES, RADIATOR FANS & SCREAMING HEP EXHAUST
    // Dynamic Brake Blister Hatch (behind cab)
    drawRoundRect(color = Color(0xFF334155), topLeft = Offset(215f * s, 13f * s), size = Size(42f * s, 6f * s), cornerRadius = CornerRadius(2f * s, 2f * s))
    drawCircle(color = Color(0xFF0F172A), radius = 5f * s, center = Offset(236f * s, 16f * s))

    // Turbocharged EMD 16-645E3 Main Diesel Exhaust Stack with spark deflector
    drawRoundRect(color = Color(0xFF1E242B), topLeft = Offset(175f * s, 11f * s), size = Size(16f * s, 8f * s), cornerRadius = CornerRadius(1.5f * s, 1.5f * s))
    drawCircle(color = Color(0xFF0F172A), radius = 3.5f * s, center = Offset(183f * s, 13f * s))

    // Three 48-inch Engine Cooling Radiator Fans in a neat row at the rear
    listOf(65f, 95f, 125f).forEach { rx ->
        drawCircle(color = Color(0xFF334155), radius = 6.5f * s, center = Offset(rx * s, 16f * s))
        drawCircle(color = Color(0xFF0F172A), radius = 5.2f * s, center = Offset(rx * s, 16f * s))
        // Fan blades
        for (f in 0 until 4) {
            val fanAngle = (f * PI / 4.0).toFloat()
            val fx = rx * s + cos(fanAngle) * 4f * s
            val fy = 16f * s + sin(fanAngle) * 2f * s
            drawLine(color = Color(0xFF64748B), start = Offset(rx * s, 16f * s), end = Offset(fx, fy), strokeWidth = 1f * s)
        }
    }

    // THE LEGENDARY SCREAMER HEAD-END POWER (HEP) GENERATOR EXHAUST!
    // Separate rear HEP exhaust muffler pipe at the very rear (where 900 RPM screams for hotel power)
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(32f * s, 10f * s),
        size = Size(8f * s, 9f * s),
        cornerRadius = CornerRadius(1.5f * s, 1.5f * s)
    )
    drawCircle(color = Color(0xFF475569), radius = 2.2f * s, center = Offset(36f * s, 12f * s))

    // Roof safety lift hooks / access rings
    listOf(45f, 150f, 255f).forEach { hx ->
        drawCircle(color = Color(0xFF94A3B8), radius = 1.5f * s, center = Offset(hx * s, 16f * s), style = Stroke(width = 0.8f * s))
    }

    // Rear hostler backup headlight & red marker lamps
    drawCircle(color = Color(0xFFFEF08A), radius = 2.5f * s, center = Offset(12f * s, 40f * s))
    drawCircle(color = Color(0xFFEF4444), radius = 1.8f * s, center = Offset(12f * s, 48f * s))
}
