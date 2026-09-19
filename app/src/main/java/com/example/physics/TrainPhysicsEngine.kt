package com.example.physics

import com.example.data.model.TrainDriveMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sign

enum class WeatherType(
    val id: String,
    val displayName: String,
    val icon: String,
    val frictionMultiplier: Float,
    val brakeEfficiency: Float,
    val conditionDescription: String,
    val slipTendencyDescription: String
) {
    CLEAR(
        id = "clear",
        displayName = "Clear & Dry",
        icon = "☀️",
        frictionMultiplier = 1.0f,
        brakeEfficiency = 1.0f,
        conditionDescription = "Dry Steel Rails",
        slipTendencyDescription = "Optimal dry traction • No sand needed"
    ),
    RAIN(
        id = "rain",
        displayName = "Wet Rain",
        icon = "🌧️",
        frictionMultiplier = 0.48f,
        brakeEfficiency = 0.70f,
        conditionDescription = "Wet / Slick Rails",
        slipTendencyDescription = "Low adhesion • Sand needed on acceleration"
    ),
    THUNDERSTORM(
        id = "thunderstorm",
        displayName = "Thunderstorm",
        icon = "⚡",
        frictionMultiplier = 0.36f,
        brakeEfficiency = 0.58f,
        conditionDescription = "Torrential Rails & Gale",
        slipTendencyDescription = "Severe wheel slip • Heavy sand required"
    ),
    SNOW(
        id = "snow",
        displayName = "Snowy Frost",
        icon = "❄️",
        frictionMultiplier = 0.58f,
        brakeEfficiency = 0.75f,
        conditionDescription = "Frosty Icy Track",
        slipTendencyDescription = "Moderate slip risk • Sand recommended"
    ),
    BLIZZARD(
        id = "blizzard",
        displayName = "Blizzard",
        icon = "🌨️",
        frictionMultiplier = 0.40f,
        brakeEfficiency = 0.62f,
        conditionDescription = "Frozen Rails & Crosswind",
        slipTendencyDescription = "High slip risk • Sand critical"
    ),
    FOG(
        id = "fog",
        displayName = "Dense Fog",
        icon = "🌫️",
        frictionMultiplier = 1.0f,
        brakeEfficiency = 1.0f,
        conditionDescription = "Low Visibility Fog • Full Dry Traction",
        slipTendencyDescription = "Dry rail adhesion • Headlight auto-engaged • No sand needed"
    )
}

enum class GearPosition(val notch: Int, val label: String) {
    REVERSE(-1, "REV"),
    NEUTRAL(0, "NEU"),
    FORWARD(1, "FWD")
}

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    var alpha: Float,
    var maxLife: Float,
    var currentLife: Float = 0f,
    val type: ParticleType
)

enum class ParticleType {
    EXHAUST_SMOKE,
    STEAM_PUFF,
    WHEEL_SPARK,
    SAND_GRAIN,
    WATER_MIST,
    DIESEL_FLAME
}

data class TrainPhysicsState(
    val distanceX: Float = 0f,
    val speedKmh: Float = 0f,
    val targetSpeedKmh: Float = 0f,
    val speedLimitKmh: Float = 160f,
    val throttle: Float = 0f, // Continuous 0.0 to 1.0 (Notches 0-8)
    val brakeLevel: Float = 0f, // Continuous Slider 0.0 to 1.0 (0%=Released, 100%=Emergency)
    val reverserSlider: Float = 1.0f, // Slider: -1.0 (REV) <-> 0.0 (NEU) <-> +1.0 (FWD)
    val gearPosition: GearPosition = GearPosition.FORWARD,
    val driveMode: TrainDriveMode = TrainDriveMode.FORWARD,
    val reverserForward: Boolean = true,
    val engineTempC: Float = 55f,
    val isOverheating: Boolean = false,
    val steamPressurePsi: Float = 85f,
    val brakePipePsi: Float = 90f, // Air Brake Pipe Pressure (90 psi normal running, drops on brake)
    val brakeCylinderPsi: Float = 0f, // Brake Cylinder Pressure (0 psi released, 70-90 psi applied)
    val dieselRpm: Float = 600f, // Diesel engine idle 600 RPM up to 1800 RPM Notch 8
    val powerKw: Float = 0f, // Active tractive power output kW
    val isWheelsSlipping: Boolean = false,
    val sandFlowPercent: Int = 0, // Notched Slider: 0, 10, 20, 40, 50, 60, 70, 80, 90, 100%
    val waterFlowPercent: Int = 0, // Notched Slider: 0, 10, 20, 40, 50, 60, 70, 80, 90, 100%
    val isSandingActive: Boolean = false,
    val isCoolingActive: Boolean = false,
    val slopeAngleRad: Float = 0f,
    val locoY: Float = 0f
)

/**
 * 2D High-Fidelity Train Physics Engine
 * Handles realistic Diesel, Steam, and Electric acceleration dynamics,
 * Davis resistance equations (rolling, mechanical flange, and aero friction),
 * true weight-based momentum & inertia, and gravity-assisted coasting in Neutral/Idle.
 */
class PhysicsEngine {

    companion object {
        const val VISUAL_WORLD_SCALE = 4.2f
        const val GRAVITY_ACCEL = 9.81f
    }

    // World position of arrival terminal station platform
    var destinationVisualX: Float = Float.MAX_VALUE

    /**
     * Realistic railroad topography profile with level station spawn grounds.
     * Starts completely flat at the departure station platform and yard (visualX <= 1500f, ~360 meters),
     * transitions through scenic mountain terrain, and smoothly eases into level flat track at the arrival terminal.
     */
    fun getElevation(visualX: Float): Float {
        // 1. Departure terminal & valley flats: 0m to ~360m completely flat ground
        if (visualX <= 1500f) return 0f

        // 2. Arrival terminal platform, green stopping area & buffer stop: completely level flat ground
        if (destinationVisualX < 1000000f && visualX >= destinationVisualX) {
            return 0f
        }

        val relX = visualX - 1500f

        // 3. Scenic Mountainous Topography:
        val passRidge = sin(relX * 0.00032f) * 62f
        val mountainUndulation = sin(relX * 0.00088f) * 38f
        val stiffGrades = sin(relX * 0.00210f) * 26f
        val cutAndFill = (1f - cos(relX * 0.00350f)) * 9f

        val rawMountainProfile = passRidge + mountainUndulation + stiffGrades + cutAndFill

        // Smooth cubic ease-in transition from flat departure ground over 1400 world units (~330m)
        val startBlendT = (relX / 1400f).coerceIn(0f, 1f)
        val startEase = startBlendT * startBlendT * (3f - 2f * startBlendT)

        // Smooth cubic ease-out transition to flat arrival station ground over final approach
        val endEase = if (destinationVisualX < 1000000f) {
            val distToDest = destinationVisualX - visualX
            if (distToDest <= 0f) 0f
            else if (distToDest < 1400f) {
                val t = (distToDest / 1400f).coerceIn(0f, 1f)
                t * t * (3f - 2f * t)
            } else 1f
        } else 1f

        return rawMountainProfile * startEase * endEase
    }

    /**
     * Calculates smooth track tangent angle for exact bogie and wheel alignment.
     */
    fun getSlopeAngle(visualX: Float): Float {
        if (visualX <= 1450f) return 0f
        val dx = 18f
        val y1 = getElevation(visualX - dx)
        val y2 = getElevation(visualX + dx)
        return atan2(y2 - y1, dx * 2f)
    }

    /**
     * Executes one high-precision physical simulation timestep.
     */
    fun stepPhysics(
        currentState: TrainPhysicsState,
        powerStat: Float,
        speedStat: Float,
        reliabilityStat: Float,
        adherenceStat: Float,
        dt: Float,
        isSteam: Boolean = false,
        isElectric: Boolean = false,
        numRailCars: Int = 3,
        cargoTonnes: Float = 60f,
        weather: WeatherType = WeatherType.CLEAR,
        health: com.example.data.storage.LocomotiveHealth? = null
    ): Pair<TrainPhysicsState, List<Particle>> {
        val spawnedParticles = mutableListOf<Particle>()

        val x = currentState.distanceX
        val currentSpeedKmh = currentState.speedKmh
        val currentSpeedMps = currentSpeedKmh * (1000f / 3600f)
        val slope = getSlopeAngle(x * VISUAL_WORLD_SCALE)

        // Health factors (0.0 to 1.0)
        val engHealthFrac = (health?.engineHealth ?: 100f) / 100f
        val gbHealthFrac = (health?.gearboxHealth ?: 100f) / 100f
        val whHealthFrac = (health?.wheelsHealth ?: 100f) / 100f
        val brkHealthFrac = (health?.brakesHealth ?: 100f) / 100f

        // 1. MASS & INERTIA CALCULATION (Metric Tonnes)
        // Locomotive Mass: Steam ~160t, Diesel ~125t, Electric/Bullet ~85t
        val locoMassTonnes = when {
            isSteam -> 165f
            isElectric -> 95f
            else -> 130f // Diesel
        }
        val railCarMassTonnes = numRailCars * 38f // ~38 tonnes per empty freight/passenger car
        val totalTrainMassTonnes = locoMassTonnes + railCarMassTonnes + cargoTonnes
        // Effective inertia with rotational inertia factor (~8% extra for wheels/axles/rotors)
        val effectiveMassKg = totalTrainMassTonnes * 1000f * 1.08f

        // 2. SPEED LIMIT CALCULATION
        val isHighSpeedBullet = speedStat >= 145f || (isElectric && speedStat >= 125f)
        val calculatedSpeedLimit = if (isHighSpeedBullet) {
            (speedStat * 1.95f).coerceIn(300f, 365f)
        } else if (speedStat >= 120f) {
            (speedStat * 1.35f).coerceIn(160f, 210f)
        } else {
            (speedStat * 1.25f).coerceIn(80f, 150f)
        }

        // 3. GEAR & REVERSER RESOLUTION
        // Reverser Slider: -1.0 to -0.15 (REV), -0.15 to +0.15 (NEU), +0.15 to +1.0 (FWD)
        // REVERSER INTERLOCK: Locomotive cannot enter REVERSE mode while accelerating or moving (speed > 0.5 km/h).
        // It can only enter REVERSE mode when the train is completely stopped (abs(speed) <= 0.5 km/h).
        val requestedGear = when {
            currentState.reverserSlider in -0.15f..0.15f -> GearPosition.NEUTRAL
            currentState.reverserSlider > 0.15f -> GearPosition.FORWARD
            currentState.reverserSlider < -0.15f -> GearPosition.REVERSE
            currentState.driveMode == TrainDriveMode.NEUTRAL -> GearPosition.NEUTRAL
            currentState.driveMode == TrainDriveMode.FORWARD -> GearPosition.FORWARD
            currentState.driveMode == TrainDriveMode.REVERSE -> GearPosition.REVERSE
            else -> GearPosition.NEUTRAL
        }

        val isTrainStopped = abs(currentSpeedKmh) <= 0.5f
        val gear = if (requestedGear == GearPosition.REVERSE && !isTrainStopped) {
            // Traction interlock: train must be stopped to engage REVERSE
            GearPosition.NEUTRAL
        } else {
            requestedGear
        }

        // Emergency braking is active when brake lever is at or above emergency notch (>= 0.95f),
        // or when explicitly commanded via EMERGENCY_BRAKE driveMode while in motion.
        // Once the train has stopped and brake handle is released (< 0.90f), emergency braking disengages.
        val isEmergencyBraking = when {
            currentState.brakeLevel >= 0.95f -> true
            currentState.driveMode == TrainDriveMode.EMERGENCY_BRAKE -> {
                if (abs(currentSpeedKmh) < 0.5f && currentState.brakeLevel < 0.90f) {
                    false
                } else {
                    true
                }
            }
            else -> false
        }

        val driveMode = when {
            isEmergencyBraking -> TrainDriveMode.EMERGENCY_BRAKE
            currentState.brakeLevel > 0.05f -> TrainDriveMode.DYNAMIC_BRAKE
            gear == GearPosition.FORWARD -> TrainDriveMode.FORWARD
            gear == GearPosition.REVERSE -> TrainDriveMode.REVERSE
            else -> TrainDriveMode.NEUTRAL
        }

        // 4. ADHESION & WHEEL SLIP
        // Adhesion coefficient mu based on rail conditions
        var baseMu = 0.34f * weather.frictionMultiplier * (0.75f + 0.25f * whHealthFrac)
        val sandFrac = if (currentState.sandFlowPercent > 0) {
            (currentState.sandFlowPercent / 100f).coerceIn(0.1f, 1.0f)
        } else if (currentState.isSandingActive) {
            1.0f
        } else {
            0.0f
        }
        val isSandingOn = sandFrac > 0f
        if (isSandingOn) {
            // Quartz sand increases friction coefficient dramatically, giving tremendous rail grip
            baseMu = max(baseMu * (1.8f + 2.2f * sandFrac), 0.44f + 0.16f * sandFrac)
        }
        val maxAdhesiveForceKn = (locoMassTonnes * GRAVITY_ACCEL * baseMu)

        // 5. ENGINE SPECIFIC TRACTIVE EFFORT (kN)
        val effectivePower = powerStat * (0.70f + 0.30f * engHealthFrac)
        val gearboxEff = 0.85f + 0.15f * gbHealthFrac
        val throttle = currentState.throttle.coerceIn(0f, 1f)

        // Diesel RPM response & lag
        val targetDieselRpm = 600f + (throttle * 1200f)
        val currentDieselRpm = currentState.dieselRpm + (targetDieselRpm - currentState.dieselRpm) * (dt * 2.5f)

        var rawTractiveForceKn = 0f
        if (gear != GearPosition.NEUTRAL && throttle > 0.01f && currentState.brakeLevel < 0.8f && !isEmergencyBraking) {
            val speedAbs = abs(currentSpeedKmh)
            // Modern locomotive starting tractive effort scaled appropriately (300-500 kN at 100%)
            val baseTractiveEffortKn = effectivePower * 9.5f * gearboxEff
            rawTractiveForceKn = when {
                isSteam -> {
                    // STEAM ENGINE: Massive low-end starting torque from cylinder expansion,
                    // tapering smoothly at high piston velocities.
                    val startingTorqueFactor = (1.60f - (speedAbs / (calculatedSpeedLimit * 1.1f)).pow(0.5f)).coerceIn(0.40f, 1.8f)
                    val boilerFactor = (currentState.steamPressurePsi / 85f).coerceIn(0.6f, 1.4f)
                    baseTractiveEffortKn * throttle * startingTorqueFactor * boilerFactor
                }
                isElectric -> {
                    // ELECTRIC / BULLET: Instantaneous linear torque, consistent high-speed power band.
                    val speedDropOff = if (speedAbs > calculatedSpeedLimit * 0.85f) 0.88f else 1.0f
                    baseTractiveEffortKn * throttle * 1.25f * speedDropOff
                }
                else -> {
                    // DIESEL-ELECTRIC: Notch rpm buildup, turbocharger boost curve.
                    val rpmFactor = (currentDieselRpm / 1800f).coerceIn(0.50f, 1.0f)
                    baseTractiveEffortKn * throttle * rpmFactor
                }
            }
        }

        // Check for Wheel Slip: In Clear & Dry and Foggy weather, rails have dry adhesion and never slip or need sand.
        // Sanding is only needed in wet, snowy, or stormy weather conditions.
        val needsSandWeather = weather != WeatherType.CLEAR && weather != WeatherType.FOG
        val gradeAdhesionFactor = (1.0f - (slope.coerceAtLeast(0f) * 10f)).coerceIn(0.45f, 1.0f)
        val effectiveMaxAdhesiveForceKn = maxAdhesiveForceKn * gradeAdhesionFactor
        val isSlipping = needsSandWeather && !isSandingOn && (sandFrac <= 0.05f) &&
                ((rawTractiveForceKn > effectiveMaxAdhesiveForceKn && throttle > 0.15f) || (slope > 0.018f && throttle > 0.28f))
        val appliedTractiveKn = if (isSlipping) {
            maxAdhesiveForceKn * 0.40f // Dynamic sliding friction drop
        } else {
            rawTractiveForceKn
        }

        // Directed Tractive Force (kN)
        val signedTractiveForceKn = when (gear) {
            GearPosition.FORWARD -> appliedTractiveKn
            GearPosition.REVERSE -> -appliedTractiveKn * 0.85f
            GearPosition.NEUTRAL -> 0f // In Neutral / Idle, engine tractive effort is strictly ZERO
        }

        // 6. DAVIS RESISTANCE EQUATION (Rolling Friction, Track Wave & Aerodynamic Drag)
        // F_resist (kN) = A + B*v + C*v^2
        // A: Journal bearing friction (proportional to total mass)
        val coeffA = totalTrainMassTonnes * 0.012f
        // B: Flange friction and track undulation
        val coeffB = totalTrainMassTonnes * 0.00030f * abs(currentSpeedKmh)
        // C: Aerodynamic drag
        val weatherAeroMult = when (weather) {
            WeatherType.BLIZZARD -> 1.45f
            WeatherType.THUNDERSTORM -> 1.25f
            else -> 1.0f
        }
        val aeroArea = if (isHighSpeedBullet) 0.00018f else 0.00030f
        val coeffC = aeroArea * currentSpeedKmh * currentSpeedKmh * weatherAeroMult

        val totalResistanceForceKn = (coeffA + coeffB + coeffC)
        // Resistance opposes current direction of travel
        val signedResistanceForceKn = if (abs(currentSpeedKmh) > 0.01f) {
            -sign(currentSpeedKmh) * totalResistanceForceKn
        } else {
            0f
        }

        // 7. BRAKE FORCE SYSTEM (High-responsiveness pneumatic trainline + dynamic braking)
        val brakeSlider = if (isEmergencyBraking) 1.0f else currentState.brakeLevel.coerceIn(0f, 1f)
        val brakeWearFactor = 0.70f + 0.30f * brkHealthFrac
        val effectiveBrakeEfficiency = (if (currentState.isSandingActive) 1.30f else weather.brakeEfficiency) * brakeWearFactor

        // Fast, authoritative brake bite: service braking is crisp and responsive; emergency braking is immediate
        val brakeMultiplier = if (isEmergencyBraking) 11.5f else (4.6f + brakeSlider * 2.8f)

        // Low-speed deceleration assist (< 35 km/h) to stop cleanly without creeping
        val lowSpeedBrakeBoost = if (brakeSlider > 0.03f && abs(currentSpeedKmh) < 35f) {
            1.0f + (1.0f - (abs(currentSpeedKmh) / 35f)) * 1.6f
        } else {
            1.0f
        }

        val maxServiceBrakeKn = totalTrainMassTonnes * brakeMultiplier * effectiveBrakeEfficiency * lowSpeedBrakeBoost
        val brakeForceKn = if (brakeSlider > 0.01f && abs(currentSpeedKmh) > 0.02f) {
            -sign(currentSpeedKmh) * (brakeSlider * maxServiceBrakeKn)
        } else {
            0f
        }

        // 8. GRAVITY ON GRADIENT (Realistic gradient resistance)
        // F_grade = - m * g * sin(slope)
        val gravityForceKn = -(totalTrainMassTonnes * GRAVITY_ACCEL * sin(slope) * 0.25f)

        // 9. TOTAL NET FORCE & ACCELERATION (Newton's 2nd Law: F_net = m * a)
        var totalNetForceKn = signedTractiveForceKn + signedResistanceForceKn + brakeForceKn + gravityForceKn

        // FORWARD GEAR GUARANTEE: When driver applies throttle in FORWARD gear, net force is positive!
        if (gear == GearPosition.FORWARD && throttle > 0.01f && brakeSlider < 0.25f) {
            val minForwardForceKn = appliedTractiveKn * 0.65f
            if (totalNetForceKn < minForwardForceKn) {
                totalNetForceKn = minForwardForceKn
            }
        }

        // Static friction & parking brake holding when stationary or nearly stationary
        if (abs(currentSpeedKmh) < 0.4f) {
            val holdingCapacityKn = (brakeSlider * maxServiceBrakeKn) + (coeffA * 2.0f)
            if (throttle <= 0.01f && abs(signedTractiveForceKn + gravityForceKn) <= holdingCapacityKn) {
                totalNetForceKn = 0f
            }
        }

        val netAccelMps2 = (totalNetForceKn * 1000f) / effectiveMassKg
        val netAccelKmhS = netAccelMps2 * 3.6f

        // New Speed integration with realistic coasting behavior
        var newSpeedKmh = currentSpeedKmh + (netAccelKmhS * dt)

        // PUNCHY FORWARD & REVERSE ACCELERATION: With throttle and brake released, train accelerates immediately
        if (gear == GearPosition.FORWARD && throttle > 0.02f && brakeSlider < 0.15f) {
            if (newSpeedKmh < 0.8f) {
                newSpeedKmh = max(newSpeedKmh, 1.5f * throttle * (0.50f + 0.50f * engHealthFrac))
            }
        } else if (gear == GearPosition.REVERSE && throttle > 0.02f && brakeSlider < 0.15f) {
            if (newSpeedKmh > -0.8f) {
                newSpeedKmh = min(newSpeedKmh, -1.5f * throttle * (0.50f + 0.50f * engHealthFrac))
            }
        }

        // NEUTRAL CRUISE BEHAVIOR:
        if (gear == GearPosition.NEUTRAL) {
            if (abs(currentSpeedKmh) < 0.25f && abs(slope) < 0.02f) {
                newSpeedKmh = 0f
            } else if (gravityForceKn <= 0.01f) {
                newSpeedKmh = min(newSpeedKmh, currentSpeedKmh)
            }
        }

        // Anti-rollback & terminal buffer stop protection:
        // 1. Train CANNOT roll in reverse unless reverser gear is explicitly in REVERSE!
        if (gear != GearPosition.REVERSE && newSpeedKmh < 0f) {
            newSpeedKmh = 0f
        }
        // 2. Complete stop when braked or when stationary with low throttle
        val isStationary = (brakeSlider > 0.04f && abs(newSpeedKmh) < 1.6f) ||
                (throttle < 0.02f && abs(newSpeedKmh) < 0.4f && (abs(slope) < 0.04f || brakeSlider > 0.02f)) ||
                (gear == GearPosition.NEUTRAL && abs(newSpeedKmh) < 0.3f && abs(slope) < 0.02f)
        if (isStationary) {
            newSpeedKmh = 0f
        }

        // Clamp speed to limits based on gear
        newSpeedKmh = if (gear == GearPosition.REVERSE) {
            newSpeedKmh.coerceIn(-35f, 0f)
        } else {
            newSpeedKmh.coerceIn(0f, calculatedSpeedLimit)
        }

        // Advance distance (SI meters: km/h * 1000/3600 * dt)
        // Monotonic non-decreasing guarantee when not in reverse
        val newDistanceX = if (gear != GearPosition.REVERSE) {
            max(x, x + (newSpeedKmh * (1000f / 3600f) * dt))
        } else {
            max(0f, x + (newSpeedKmh * (1000f / 3600f) * dt))
        }

        // 10. TEMPERATURE & THERMAL DYNAMICS (Water Lever actively cools engine)
        var temp = currentState.engineTempC
        val engineLoad = if (gear != GearPosition.NEUTRAL) throttle else 0.05f
        val heatGeneration = (engineLoad * (50f / max(10f, reliabilityStat * (0.6f + 0.4f * engHealthFrac)))) * dt * 2.0f
        val naturalDissipation = (1.4f + (abs(newSpeedKmh) * 0.02f)) * dt
        val waterFrac = if (currentState.waterFlowPercent > 0) {
            (currentState.waterFlowPercent / 100f).coerceIn(0.1f, 1.0f)
        } else if (currentState.isCoolingActive) {
            1.0f
        } else {
            0.0f
        }
        val isCoolingOn = waterFrac > 0f
        // Active water cooling rapidly drops engine temperature towards safe 60°C operating temp
        val coolantEffect = if (isCoolingOn) (48.0f * waterFrac) * dt else 0f
        temp = (temp + heatGeneration - naturalDissipation - coolantEffect).coerceIn(40f, 130f)
        val isOverheat = temp >= 100f

        // 11. STEAM / AIR PNEUMATIC PRESSURES
        var steamPressure = currentState.steamPressurePsi
        if (isSteam) {
            val steamUsage = (throttle * 8.5f) + (abs(newSpeedKmh) * 0.03f)
            val steamRegen = if (temp > 60f) (temp / 80f) * 6.5f else 3.0f
            val pressureDelta = (steamRegen - steamUsage - (if (isCoolingOn) 6f * waterFrac else 0f)) * dt
            steamPressure = (steamPressure + pressureDelta).coerceIn(30f, 140f)
        }

        val brakePipe = (90f - brakeSlider * 70f).coerceIn(20f, 90f)
        val brakeCylinder = (brakeSlider * 85f).coerceIn(0f, 85f)
        val powerKwOutput = if (throttle > 0.01f && gear != GearPosition.NEUTRAL) {
            (effectivePower * 35f * throttle * (currentDieselRpm / 1800f)).coerceAtLeast(120f)
        } else {
            0f
        }

        // 12. PARTICLE FX SPAWNING
        val wheelPositions = if (isSteam) listOf(80f, 150f, 220f, 290f) else listOf(45f, 87f, 265f, 307f)

        // Exhaust Smoke (Steam puff or Diesel exhaust)
        if (!isElectric) {
            if (throttle > 0.02f || abs(newSpeedKmh) > 2f) {
                if (isSteam) {
                    val puffs = if (throttle > 0.5f) 2 else 1
                    for (i in 0 until puffs) {
                        spawnedParticles.add(
                            Particle(
                                x = 260f + (Math.random().toFloat() * 6f - 3f),
                                y = 6f,
                                vx = -1.0f - (newSpeedKmh * 0.04f) + (Math.random().toFloat() * 0.6f - 0.3f),
                                vy = -2.5f - (throttle * 3.5f) - (Math.random().toFloat() * 1.5f),
                                size = 12f + throttle * 16f,
                                alpha = 0.8f,
                                maxLife = 1.0f + (throttle * 0.6f),
                                type = ParticleType.STEAM_PUFF
                            )
                        )
                    }
                } else {
                    val puffs = if (throttle > 0.5f) 2 else 1
                    for (i in 0 until puffs) {
                        spawnedParticles.add(
                            Particle(
                                x = 130f + (Math.random().toFloat() * 6f - 3f),
                                y = 8f,
                                vx = -1.2f - (newSpeedKmh * 0.04f) + (Math.random().toFloat() * 0.6f - 0.3f),
                                vy = -2.0f - (throttle * 2.8f) - (Math.random().toFloat() * 1.2f),
                                size = 10f + throttle * 14f,
                                alpha = 0.75f,
                                maxLife = 0.85f + (throttle * 0.5f),
                                type = ParticleType.EXHAUST_SMOKE
                            )
                        )
                    }
                }
            }

            // Diesel Overheat Flames
            if (!isSteam && isOverheat) {
                for (f in 0 until 4) {
                    spawnedParticles.add(
                        Particle(
                            x = 130f + (Math.random().toFloat() * 14f - 7f),
                            y = 8f + (Math.random().toFloat() * 6f - 3f),
                            vx = -1.8f - (newSpeedKmh * 0.07f) + (Math.random().toFloat() * 2.0f - 1.0f),
                            vy = -4.5f - (throttle * 3.5f) - (Math.random().toFloat() * 4.0f),
                            size = 15f + (Math.random().toFloat() * 14f),
                            alpha = 0.95f,
                            maxLife = 0.50f + (Math.random().toFloat() * 0.40f),
                            type = ParticleType.DIESEL_FLAME
                        )
                    )
                }
            }
        }

        // Wheel Sparks during slip or heavy brake application
        if (isSlipping || (brakeSlider > 0.75f && abs(newSpeedKmh) > 8f)) {
            for (wheelX in wheelPositions) {
                for (s in 0 until 2) {
                    spawnedParticles.add(
                        Particle(
                            x = wheelX + (Math.random().toFloat() * 8f - 4f),
                            y = 121f + (Math.random().toFloat() * 2f - 1f),
                            vx = -2.2f - (newSpeedKmh * 0.04f) + (Math.random().toFloat() * 4.5f - 2.25f),
                            vy = -(Math.random().toFloat() * 3.5f + 0.8f),
                            size = 3.5f,
                            alpha = 0.95f,
                            maxLife = 0.32f + (Math.random().toFloat() * 0.15f),
                            type = ParticleType.WHEEL_SPARK
                        )
                    )
                }
            }
        }

        // Rain wheel spray
        if ((weather == WeatherType.RAIN || weather == WeatherType.THUNDERSTORM) && abs(newSpeedKmh) > 5f) {
            for (wheelX in wheelPositions) {
                spawnedParticles.add(
                    Particle(
                        x = wheelX + (Math.random().toFloat() * 6f - 3f),
                        y = 105f,
                        vx = -1.5f - (newSpeedKmh * 0.04f),
                        vy = -(Math.random().toFloat() * 2f + 1f),
                        size = 3.5f + (Math.random().toFloat() * 3f),
                        alpha = 0.60f,
                        maxLife = 0.4f,
                        type = ParticleType.WATER_MIST
                    )
                )
            }
        }

        // Sand Particles sprayed onto rail interface
        if (isSandingOn) {
            val particlesPerWheel = if (sandFrac > 0.5f) 2 else 1
            for (wheelX in wheelPositions) {
                for (p in 0 until particlesPerWheel) {
                    spawnedParticles.add(
                        Particle(
                            x = wheelX - 6f + (Math.random().toFloat() * 4f - 2f),
                            y = 108f + (Math.random().toFloat() * 4f),
                            vx = -0.6f - (newSpeedKmh * 0.02f) + (Math.random().toFloat() * 1.0f - 0.5f),
                            vy = 3.2f + (Math.random().toFloat() * 2.0f),
                            size = 2.5f + sandFrac * 2.0f,
                            alpha = 0.85f,
                            maxLife = 0.35f,
                            type = ParticleType.SAND_GRAIN
                        )
                    )
                }
            }
        }

        // Water cooling spray
        if (isCoolingOn) {
            val mistCount = (2 + (waterFrac * 4f)).toInt()
            for (i in 0 until mistCount) {
                spawnedParticles.add(
                    Particle(
                        x = 120f + (Math.random().toFloat() * 80f),
                        y = 20f,
                        vx = -1.2f - (newSpeedKmh * 0.03f),
                        vy = -(Math.random().toFloat() * 2f + 0.5f),
                        size = 6f + waterFrac * 6f,
                        alpha = 0.65f,
                        maxLife = 0.6f,
                        type = ParticleType.WATER_MIST
                    )
                )
            }
        }

        val updatedState = currentState.copy(
            distanceX = newDistanceX,
            speedKmh = newSpeedKmh,
            speedLimitKmh = calculatedSpeedLimit,
            engineTempC = temp,
            isOverheating = isOverheat,
            steamPressurePsi = steamPressure,
            brakePipePsi = brakePipe,
            brakeCylinderPsi = brakeCylinder,
            dieselRpm = currentDieselRpm,
            powerKw = powerKwOutput,
            isWheelsSlipping = isSlipping,
            isSandingActive = isSandingOn,
            isCoolingActive = isCoolingOn,
            slopeAngleRad = slope,
            gearPosition = gear,
            driveMode = driveMode,
            reverserForward = (gear == GearPosition.FORWARD)
        )

        return Pair(updatedState, spawnedParticles)
    }
}

// Backward compatibility alias for any references
typealias TrainPhysicsEngine = PhysicsEngine
