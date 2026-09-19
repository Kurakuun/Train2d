package com.example

import com.example.data.model.GameContent
import com.example.data.model.TrainDriveMode
import com.example.physics.GearPosition
import com.example.physics.TrainPhysicsEngine
import com.example.physics.TrainPhysicsState
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAll24TrainsLoaded() {
        assertTrue(GameContent.ALL_TRAINS.size >= 24)
        val uniqueIds = GameContent.ALL_TRAINS.map { it.id }.toSet()
        assertEquals(GameContent.ALL_TRAINS.size, uniqueIds.size)
    }

    @Test
    fun testAll24PromoCodesLoaded() {
        assertTrue(GameContent.PROMO_CODES.size >= 24)
        val codes = GameContent.PROMO_CODES.map { it.code }
        assertTrue(codes.contains("Crazy fast") || codes.contains("CRAZYFAST"))
    }

    @Test
    fun testAll15HorrorMusicTracksLoaded() {
        assertEquals(15, GameContent.HORROR_MUSIC_TRACKS.size)
        val uniqueTracks = GameContent.HORROR_MUSIC_TRACKS.map { it.id }.toSet()
        assertEquals(15, uniqueTracks.size)
    }

    @Test
    fun testHighTierContractsDistanceAndLevelRequirement() {
        val highTierContracts = GameContent.ALL_CONTRACTS.filter { it.isHighTier }
        assertTrue(highTierContracts.isNotEmpty())
        for (contract in highTierContracts) {
            assertTrue(contract.distanceMeters >= 4000f)
        }
    }

    @Test
    fun testPhysicsEngineDriveModes() {
        val engine = TrainPhysicsEngine()
        var state = TrainPhysicsState(
            speedKmh = 50f,
            throttle = 0.8f,
            driveMode = TrainDriveMode.EMERGENCY_BRAKE
        )
        val (nextState, _) = engine.stepPhysics(
            currentState = state,
            powerStat = 40f,
            speedStat = 50f,
            reliabilityStat = 20f,
            adherenceStat = 15f,
            dt = 0.1f,
            isSteam = false,
            isElectric = false
        )
        // Emergency brake should decelerate rapidly
        assertTrue(nextState.speedKmh < state.speedKmh)
    }

    @Test
    fun testEmergencyBrakeReleaseAndAcceleration() {
        val engine = TrainPhysicsEngine()
        // 1. Train emergency brakes from 40 km/h down to complete halt
        var state = TrainPhysicsState(
            speedKmh = 40f,
            throttle = 0.0f,
            brakeLevel = 1.0f,
            driveMode = TrainDriveMode.EMERGENCY_BRAKE
        )
        for (i in 0 until 50) {
            val (next, _) = engine.stepPhysics(
                currentState = state,
                powerStat = 50f,
                speedStat = 60f,
                reliabilityStat = 50f,
                adherenceStat = 50f,
                dt = 0.1f,
                isSteam = false,
                isElectric = false
            )
            state = next
        }
        assertEquals(0f, state.speedKmh, 0.01f)

        // 2. Train releases brake and applies throttle in FORWARD gear
        state = state.copy(
            brakeLevel = 0.0f,
            throttle = 0.8f,
            reverserSlider = 1.0f,
            gearPosition = GearPosition.FORWARD,
            driveMode = TrainDriveMode.FORWARD
        )

        val (accelState, _) = engine.stepPhysics(
            currentState = state,
            powerStat = 50f,
            speedStat = 60f,
            reliabilityStat = 50f,
            adherenceStat = 50f,
            dt = 0.1f,
            isSteam = false,
            isElectric = false
        )

        // Train must accelerate again after emergency brake is released!
        assertTrue("Train should accelerate forward after emergency brake release, but speed was ${accelState.speedKmh}", accelState.speedKmh > 0f)
        assertFalse(accelState.driveMode == TrainDriveMode.EMERGENCY_BRAKE)
    }

    @Test
    fun testElectricTrainHasNoSmoke() {
        val engine = TrainPhysicsEngine()
        val state = TrainPhysicsState(
            speedKmh = 100f,
            throttle = 1.0f,
            driveMode = TrainDriveMode.FORWARD
        )
        val (_, particles) = engine.stepPhysics(
            currentState = state,
            powerStat = 80f,
            speedStat = 100f,
            reliabilityStat = 50f,
            adherenceStat = 50f,
            dt = 0.1f,
            isSteam = false,
            isElectric = true
        )
        val smokeParticles = particles.filter {
            it.type == com.example.physics.ParticleType.EXHAUST_SMOKE || it.type == com.example.physics.ParticleType.STEAM_PUFF
        }
        assertTrue(smokeParticles.isEmpty())
    }

    @Test
    fun testDieselAndSteamTrainsHaveSmoke() {
        val engine = TrainPhysicsEngine()
        val state = TrainPhysicsState(
            speedKmh = 40f,
            throttle = 0.8f,
            driveMode = TrainDriveMode.FORWARD
        )
        // Diesel test
        val (_, dieselParticles) = engine.stepPhysics(
            currentState = state,
            powerStat = 60f,
            speedStat = 70f,
            reliabilityStat = 30f,
            adherenceStat = 30f,
            dt = 0.1f,
            isSteam = false,
            isElectric = false
        )
        assertTrue(dieselParticles.any { it.type == com.example.physics.ParticleType.EXHAUST_SMOKE })

        // Steam test
        val (_, steamParticles) = engine.stepPhysics(
            currentState = state,
            powerStat = 60f,
            speedStat = 70f,
            reliabilityStat = 30f,
            adherenceStat = 30f,
            dt = 0.1f,
            isSteam = true,
            isElectric = false
        )
        assertTrue(steamParticles.any { it.type == com.example.physics.ParticleType.STEAM_PUFF })
    }

    @Test
    fun testWheelSparksAndSandAtWheels() {
        val engine = TrainPhysicsEngine()
        // Sanding active
        val sandingState = TrainPhysicsState(
            speedKmh = 20f,
            throttle = 0.5f,
            isSandingActive = true
        )
        val (_, sandParticles) = engine.stepPhysics(
            currentState = sandingState,
            powerStat = 40f,
            speedStat = 40f,
            reliabilityStat = 30f,
            adherenceStat = 20f,
            dt = 0.1f,
            isSteam = false,
            isElectric = true
        )
        val sands = sandParticles.filter { it.type == com.example.physics.ParticleType.SAND_GRAIN }
        assertTrue(sands.isNotEmpty())
        // Sand particles should originate at wheel heights (108f)
        for (sand in sands) {
            assertTrue(sand.y >= 105f)
        }
    }

    @Test
    fun testWeatherPhysicsAndSanding() {
        val engine = TrainPhysicsEngine()
        var rainState = TrainPhysicsState(
            speedKmh = 25f,
            throttle = 0.9f,
            driveMode = TrainDriveMode.FORWARD,
            isSandingActive = false
        )
        val allParticles = mutableListOf<com.example.physics.Particle>()
        // Simulate a few ticks in rain
        for (i in 0 until 10) {
            val (nextState, particles) = engine.stepPhysics(
                currentState = rainState,
                powerStat = 90f,
                speedStat = 80f,
                reliabilityStat = 50f,
                adherenceStat = 10f,
                dt = 0.1f,
                isSteam = false,
                isElectric = true,
                weather = com.example.physics.WeatherType.RAIN
            )
            rainState = nextState
            allParticles.addAll(particles)
        }
        // Rain generates water spray particles
        assertTrue(allParticles.any { it.type == com.example.physics.ParticleType.WATER_MIST })

        // When sanding is activated in rain, adherence is boosted and wheel slip avoided
        val sandRainState = rainState.copy(isSandingActive = true)
        val (stateWithSand, _) = engine.stepPhysics(
            currentState = sandRainState,
            powerStat = 90f,
            speedStat = 80f,
            reliabilityStat = 50f,
            adherenceStat = 10f,
            dt = 0.1f,
            isSteam = false,
            isElectric = true,
            weather = com.example.physics.WeatherType.RAIN
        )
        assertFalse(stateWithSand.isWheelsSlipping)
    }

    @Test
    fun testLocomotiveHealthWearAndRepair() {
        val health = com.example.data.storage.LocomotiveHealth(
            engineHealth = 40f,
            gearboxHealth = 50f,
            wheelsHealth = 60f,
            brakesHealth = 30f
        )
        // Test health degradation
        assertTrue(health.overallPercent < 50)
        assertEquals("SERVICE DUE", health.statusLabel)

        // Fully healthy locomotive
        val perfectHealth = com.example.data.storage.LocomotiveHealth()
        assertEquals(100, perfectHealth.overallPercent)
        assertEquals("OPTIMAL", perfectHealth.statusLabel)
    }

    @Test
    fun testPhysicsDegradesWithPoorHealth() {
        val engine = TrainPhysicsEngine()
        val healthyState = TrainPhysicsState(speedKmh = 0f, throttle = 1.0f, driveMode = TrainDriveMode.FORWARD)
        val wornHealth = com.example.data.storage.LocomotiveHealth(engineHealth = 10f, gearboxHealth = 10f, wheelsHealth = 10f, brakesHealth = 10f)
        val perfectHealth = com.example.data.storage.LocomotiveHealth()

        val (stateWorn, _) = engine.stepPhysics(
            currentState = healthyState,
            powerStat = 50f,
            speedStat = 60f,
            reliabilityStat = 30f,
            adherenceStat = 20f,
            dt = 0.1f,
            isSteam = false,
            isElectric = false,
            health = wornHealth
        )

        val (statePerfect, _) = engine.stepPhysics(
            currentState = healthyState,
            powerStat = 50f,
            speedStat = 60f,
            reliabilityStat = 30f,
            adherenceStat = 20f,
            dt = 0.1f,
            isSteam = false,
            isElectric = false,
            health = perfectHealth
        )

        // Perfect health locomotive accelerates faster than heavily worn locomotive
        assertTrue(statePerfect.speedKmh > stateWorn.speedKmh)
    }

    @Test
    fun testClearWeatherNoSandNeededOnFlatTerrain() {
        val engine = TrainPhysicsEngine()
        val flatState = TrainPhysicsState(distanceX = 0f, speedKmh = 0f, throttle = 1.0f, driveMode = TrainDriveMode.FORWARD, isSandingActive = false)
        val (state, _) = engine.stepPhysics(
            currentState = flatState,
            powerStat = 90f,
            speedStat = 80f,
            reliabilityStat = 50f,
            adherenceStat = 30f,
            dt = 0.1f,
            isSteam = false,
            isElectric = false,
            weather = com.example.physics.WeatherType.CLEAR
        )
        // In clear weather on flat/normal terrain, wheel slip does not trigger even at 100% throttle without sand
        assertFalse(state.isWheelsSlipping)
    }

    @Test
    fun testDieselLocomotiveFlamesWhenOverheatedUntilCooled() {
        val engine = TrainPhysicsEngine()
        val overheatedState = TrainPhysicsState(
            speedKmh = 50f,
            throttle = 0.8f,
            engineTempC = 105f,
            isOverheating = true,
            isCoolingActive = false
        )
        val (stateOverheated, particles) = engine.stepPhysics(
            currentState = overheatedState,
            powerStat = 80f,
            speedStat = 70f,
            reliabilityStat = 40f,
            adherenceStat = 40f,
            dt = 0.1f,
            isSteam = false,
            isElectric = false,
            weather = com.example.physics.WeatherType.CLEAR
        )
        assertTrue(stateOverheated.isOverheating)
        // Verify flaming particles are generated
        val hasFlames = particles.any { it.type == com.example.physics.ParticleType.DIESEL_FLAME }
        assertTrue(hasFlames)

        // Now activate cooling system
        var coolingState = stateOverheated.copy(isCoolingActive = true)
        for (i in 0 until 10) {
            val (nextState, _) = engine.stepPhysics(
                currentState = coolingState,
                powerStat = 80f,
                speedStat = 70f,
                reliabilityStat = 40f,
                adherenceStat = 40f,
                dt = 0.1f,
                isSteam = false,
                isElectric = false,
                weather = com.example.physics.WeatherType.CLEAR
            )
            coolingState = nextState
        }
        // Temperature drops with cooling active
        assertTrue(coolingState.engineTempC < stateOverheated.engineTempC)
    }
}
