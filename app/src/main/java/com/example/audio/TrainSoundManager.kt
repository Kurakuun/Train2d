package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.model.EnvironmentType
import com.example.data.model.GameContent
import com.example.data.model.SoundProfileType
import com.example.data.model.TrainType
import com.example.physics.WeatherType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class TrainSoundManager {

    private val scope = CoroutineScope(Dispatchers.Default)

    private var hornJob: Job? = null
    private var sfxJob: Job? = null
    private var musicJob: Job? = null
    private var engineLoopJob: Job? = null
    private var ambienceJob: Job? = null

    @Volatile
    private var currentMusicTrackId: String = "track_1"
    @Volatile
    private var isMusicEnabled: Boolean = true

    // Ambience parameters
    @Volatile
    private var isDrivingAmbienceActive: Boolean = false
    @Volatile
    private var currentEnvironment: EnvironmentType = EnvironmentType.REDWOOD_COAST
    @Volatile
    private var currentWeather: WeatherType = WeatherType.CLEAR

    // Engine loop parameters
    @Volatile
    private var currentSoundProfile: SoundProfileType = SoundProfileType.EMD_2STROKE_DIESEL
    @Volatile
    private var currentSpeed: Float = 0f
    @Volatile
    private var currentThrottle: Float = 0f

    init {
        startMusicLoop()
        startEngineAudioLoop()
        startAmbienceAudioLoop()
    }

    // =========================================================================
    // 1. RETRO DRUM & CHIPTUNE SYNTHESIZER (PUNCHY 80S DRUMS, RETRO BASS & LEADS)
    // =========================================================================

    var isMuted: Boolean = false
        private set

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun isMusicPlaying(): Boolean = isMusicEnabled && !isMuted

    fun getCurrentTrack(): com.example.data.model.BreakcoreMusicTrack? {
        return GameContent.BREAKCORE_MUSIC_TRACKS.find { it.id == currentMusicTrackId }
    }

    fun playBreakcore(track: com.example.data.model.BreakcoreMusicTrack) {
        currentMusicTrackId = track.id
        isMusicEnabled = true
    }

    fun stopBreakcore() {
        isMusicEnabled = false
    }

    fun setMusicTrack(trackId: String, enabled: Boolean) {
        currentMusicTrackId = trackId
        isMusicEnabled = enabled
    }

    fun setDrivingAmbience(environment: EnvironmentType?, isDriving: Boolean) {
        isDrivingAmbienceActive = isDriving
        if (environment != null) {
            currentEnvironment = environment
        }
    }

    fun setWeather(weather: WeatherType) {
        currentWeather = weather
    }

    fun playThunder() {
        scope.launch {
            try {
                val sampleRate = 22050
                val durationSec = 2.2
                val numSamples = (sampleRate * durationSec).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val crack = if (t < 0.12) {
                        val crackEnv = (1.0 - t / 0.12)
                        (Math.random() * 2.0 - 1.0) * crackEnv * 0.95
                    } else 0.0

                    val rumbleEnv = if (t < 0.25) (t / 0.25) else (1.0 - (t - 0.25) / 1.95).coerceIn(0.0, 1.0)
                    val f1 = 46.0 + sin(t * 10.0) * 14.0
                    val f2 = 68.0 + cos(t * 7.0) * 18.0
                    val rumbleSine = sin(2.0 * PI * f1 * t) * 0.55 + sin(2.0 * PI * f2 * t) * 0.35
                    val rumbleNoise = (Math.random() * 2.0 - 1.0) * 0.38
                    val rumble = (rumbleSine + rumbleNoise) * rumbleEnv * 0.85

                    val sample = (crack + rumble).coerceIn(-1.0, 1.0)
                    buffer[i] = (sample * Short.MAX_VALUE * 0.70).toInt().toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    private fun startMusicLoop() {
        musicJob?.cancel()
        musicJob = scope.launch {
            val sampleRate = 22050
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            var audioTrack: AudioTrack? = null
            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack.play()

                var step = 0
                val shortBuffer = ShortArray(1024)

                while (isActive) {
                    if (!isMusicEnabled || isMuted) {
                        shortBuffer.fill(0)
                        audioTrack.write(shortBuffer, 0, shortBuffer.size)
                        delay(50)
                        continue
                    }

                    val track = GameContent.BREAKCORE_MUSIC_TRACKS.find { it.id == currentMusicTrackId }
                        ?: GameContent.BREAKCORE_MUSIC_TRACKS.first()

                    val bpm = track.tempoBpm
                    val rootFreq = track.scaleRootFreq
                    val samplesPerBeat = (sampleRate * 60f / bpm).toInt()
                    val samplesPer16th = (samplesPerBeat / 4).coerceAtLeast(1)

                    // Synthesize Authentic Country & Bluegrass Music Chunk
                    for (i in shortBuffer.indices) {
                        val totalSampleIndex = step * shortBuffer.size + i
                        val stepIndex16th = (totalSampleIndex / samplesPer16th) % 32
                        val stepProgress16th = (totalSampleIndex % samplesPer16th).toFloat() / samplesPer16th
                        val t = totalSampleIndex.toDouble() / sampleRate
                        val barStep = stepIndex16th % 16

                        // 1. COUNTRY TRAIN SHUFFLE DRUMS (Kick on 1 & 3, Snare Backbeat on 2 & 4, Swung Train Chug Brushes)
                        val drumSample = getCountryDrumSample(stepIndex16th, stepProgress16th, totalSampleIndex, samplesPer16th, track.id)

                        // 2. ACOUSTIC UPRIGHT BASS ("Boom-Chick" Alternating Root-Fifth Bass)
                        val chordRootSemitone = getCountryChordSemitone(track.id, stepIndex16th)
                        val isBassStep = (barStep in 0..3) || (barStep in 8..11) || (barStep == 14)
                        val bassDegreeOffset = if (barStep in 8..11) 7.0 else if (barStep == 14) 2.0 else 0.0
                        val freqBass = (rootFreq * 0.5) * Math.pow(2.0, (chordRootSemitone + bassDegreeOffset) / 12.0)
                        val bassSine = sin(2.0 * PI * freqBass * t)
                        val bassHarmonic = sin(4.0 * PI * freqBass * t) * 0.25
                        val bassPluckEnv = if (isBassStep) Math.exp(-stepProgress16th.toDouble() * 5.2) else 0.0
                        val woodyClick = if (isBassStep && stepProgress16th < 0.04f) (Math.random() * 2.0 - 1.0) * 0.20 else 0.0
                        val countryBass = if (isBassStep) (bassSine + bassHarmonic + woodyClick) * bassPluckEnv else 0.0

                        // 3. ACOUSTIC RHYTHM GUITAR CHOP / STRUM (On Backbeats 2 & 4: steps 4..7 and 12..15)
                        val isGuitarChop = (barStep in 4..7) || (barStep in 12..15)
                        val guitarProgress = if (barStep in 4..7) {
                            (barStep - 4 + stepProgress16th) / 4.0
                        } else {
                            (barStep - 12 + stepProgress16th) / 4.0
                        }
                        val guitarEnv = Math.exp(-guitarProgress * 9.5)
                        val gRoot = rootFreq * Math.pow(2.0, chordRootSemitone / 12.0)
                        val g3rd = gRoot * Math.pow(2.0, (chordRootSemitone + 4.0) / 12.0) // Warm country major 3rd
                        val g5th = gRoot * Math.pow(2.0, (chordRootSemitone + 7.0) / 12.0)
                        val gOct = gRoot * 2.0
                        val guitarStrum = if (isGuitarChop) {
                            (
                                sin(2.0 * PI * gRoot * t) * 0.35 +
                                sin(2.0 * PI * g3rd * t) * 0.35 +
                                sin(2.0 * PI * g5th * t) * 0.30 +
                                sin(2.0 * PI * gOct * t) * 0.20 +
                                (Math.random() * 2.0 - 1.0) * 0.12
                            ) * guitarEnv
                        } else 0.0

                        // 4. BANJO ROLL & ACOUSTIC FLATPICKING LEAD (Scruggs 3-finger banjo picking roll)
                        val semitoneLead = getCountryLeadSemitone(track.id, stepIndex16th)
                        val freqLead = rootFreq * Math.pow(2.0, (semitoneLead + 12.0) / 12.0)
                        val leadPhase = (t * freqLead) % 1.0
                        val leadTriangle = (Math.abs(leadPhase * 2.0 - 1.0) * 2.0 - 1.0)
                        val leadSine = sin(2.0 * PI * freqLead * t)
                        val lead2ndHarmonic = sin(4.0 * PI * freqLead * t) * 0.32
                        val leadTransient = if (stepProgress16th < 0.03f) (Math.random() * 2.0 - 1.0) * 0.22 else 0.0
                        val banjoPluckEnv = Math.exp(-stepProgress16th.toDouble() * 6.8)
                        val countryBanjo = (leadSine * 0.65 + leadTriangle * 0.25 + lead2ndHarmonic + leadTransient) * banjoPluckEnv

                        // 5. SWEET COUNTRY PEDAL STEEL / SLIDE HARMONY
                        val steelRoot = rootFreq * Math.pow(2.0, (chordRootSemitone + 16.0) / 12.0)
                        val steel3rd = rootFreq * Math.pow(2.0, (chordRootSemitone + 20.0) / 12.0)
                        val steelSine = (
                            sin(2.0 * PI * steelRoot * t) * 0.50 +
                            sin(2.0 * PI * steel3rd * t) * 0.50
                        ) * 0.10

                        val mixed = (
                            drumSample * 0.40 +
                            countryBass * 0.32 +
                            guitarStrum * 0.22 +
                            countryBanjo * 0.30 +
                            steelSine * 0.12
                        ).coerceIn(-1.0, 1.0)

                        shortBuffer[i] = (mixed * Short.MAX_VALUE * 0.72).toInt().toShort()
                    }

                    audioTrack.write(shortBuffer, 0, shortBuffer.size)
                    step = (step + 1) % 100000
                }
            } catch (_: Exception) {
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (_: Exception) {}
            }
        }
    }

    private fun getCountryDrumSample(stepIndex16th: Int, progress: Float, totalSample: Int, samplesPer16th: Int, trackId: String = "track_1"): Double {
        val barStep = stepIndex16th % 16

        // 1. Thumping country kick on beats 1 & 3
        val isKick = (barStep == 0 || barStep == 8)
        // 2. Crisp acoustic snare backbeat on beats 2 & 4
        val isSnare = (barStep == 4 || barStep == 12)
        // 3. Train shuffle brushed snare & high-hat chug (swung 16th train pattern)
        val isTrainShuffle = !isKick && !isSnare
        val isBrushAccent = (barStep % 2 == 1)

        var drum = 0.0

        // Acoustic Kick (Warm, rounded resonant thump)
        if (isKick && progress < 0.40f) {
            val kickT = progress.toDouble() * 0.04
            val kickPitch = 48.0 + (125.0 - 48.0) * Math.exp(-progress * 22.0)
            val kickSine = sin(2.0 * PI * kickPitch * kickT)
            val kickEnv = Math.exp(-progress.toDouble() * 14.0)
            val kickWood = if (progress < 0.03f) (Math.random() * 2.0 - 1.0) * 0.20 else 0.0
            drum += (kickSine * 0.85 + kickWood) * kickEnv * 1.0
        }

        // Acoustic Snare Backbeat (Crisp wood snare with wire brush decay)
        if (isSnare && progress < 0.34f) {
            val snareT = progress.toDouble() * 0.04
            val toneFreq = 195.0
            val toneBody = sin(2.0 * PI * toneFreq * snareT) * 0.35
            val wireBrushNoise = (Math.random() * 2.0 - 1.0) * 0.85
            val snareEnv = Math.exp(-progress.toDouble() * 11.0)
            drum += (toneBody + wireBrushNoise) * snareEnv * 0.88
        }

        // Train Shuffle Brushed Snare & High-Hat Chug
        if (isTrainShuffle && progress < 0.16f) {
            val shuffleNoise = (Math.random() * 2.0 - 1.0)
            val shuffleEnv = Math.exp(-progress.toDouble() * 18.0)
            val shuffleAmp = if (isBrushAccent) 0.22 else 0.13
            drum += shuffleNoise * shuffleEnv * shuffleAmp
        }

        return drum
    }

    private fun getCountryChordSemitone(trackId: String, stepIndex: Int): Double {
        val bar = (stepIndex / 8) % 4
        return when (trackId) {
            "track_1" -> listOf(0.0, 5.0, 7.0, 0.0)[bar] // Wabash Cannonball (I - IV - V - I)
            "track_2" -> listOf(0.0, 0.0, 5.0, 7.0)[bar] // Freight Train Blues (I - I - IV - V)
            "track_3" -> listOf(0.0, 7.0, 0.0, 5.0)[bar] // Blue Ridge Mountain Haul (I - V - I - IV)
            "track_4" -> listOf(0.0, 9.0, 5.0, 7.0)[bar] // Midnight Prairie Special (I - vi - IV - V)
            "track_5" -> listOf(0.0, 5.0, 0.0, 7.0)[bar] // Nashville Rail Yard (I - IV - I - V)
            "track_6" -> listOf(0.0, 0.0, 5.0, 7.0)[bar] // Coal Miner's Highball (I - I - IV - V)
            "track_7" -> listOf(0.0, 9.0, 2.0, 7.0)[bar] // Texas Panhandle Flyer (Western Swing)
            "track_8" -> listOf(0.0, 5.0, 0.0, 7.0)[bar] // Smoky Mountain Rambler
            "track_9" -> listOf(0.0, 4.0, 5.0, 7.0)[bar] // Golden Spike Jubilee
            "track_10" -> listOf(0.0, 5.0, 7.0, 0.0)[bar] // Shenandoah Valley Line
            "track_11" -> listOf(0.0, 0.0, 5.0, 7.0)[bar] // Highballing Through Dixie
            "track_12" -> listOf(0.0, 5.0, 9.0, 7.0)[bar] // Cotton Belt Sunset
            "track_13" -> listOf(0.0, 5.0, 7.0, 0.0)[bar] // Grand Ole Rail Opry
            "track_14" -> listOf(0.0, 10.0, 5.0, 0.0)[bar] // Wild West Iron Horse
            else -> listOf(0.0, 5.0, 7.0, 0.0)[bar] // Appalachian Express Finale
        }
    }

    private fun getCountryLeadSemitone(trackId: String, stepIndex: Int): Double {
        val patternIndex = stepIndex % 16
        return when (trackId) {
            "track_1" -> listOf(0.0, 4.0, 7.0, 12.0, 7.0, 4.0, 2.0, 0.0, 5.0, 9.0, 12.0, 16.0, 12.0, 9.0, 7.0, 4.0)[patternIndex] // Banjo Scruggs Roll
            "track_2" -> listOf(0.0, 2.0, 4.0, 7.0, 9.0, 7.0, 4.0, 0.0, 5.0, 7.0, 9.0, 12.0, 9.0, 7.0, 4.0, 2.0)[patternIndex] // Country Blues Pick
            "track_3" -> listOf(12.0, 7.0, 4.0, 0.0, 4.0, 7.0, 12.0, 14.0, 14.0, 11.0, 7.0, 2.0, 7.0, 11.0, 14.0, 12.0)[patternIndex] // High Mountain Breakdown
            "track_4" -> listOf(0.0, 4.0, 7.0, 11.0, 9.0, 7.0, 4.0, 2.0, 0.0, 4.0, 5.0, 7.0, 9.0, 7.0, 4.0, 0.0)[patternIndex] // Prairie Slide Melody
            "track_5" -> listOf(0.0, 4.0, 7.0, 9.0, 12.0, 9.0, 7.0, 4.0, 5.0, 9.0, 12.0, 14.0, 12.0, 9.0, 7.0, 4.0)[patternIndex] // Two-Step Lead
            "track_6" -> listOf(0.0, 7.0, 12.0, 7.0, 4.0, 7.0, 12.0, 16.0, 12.0, 7.0, 4.0, 2.0, 0.0, 4.0, 7.0, 12.0)[patternIndex] // Fast Bluegrass Run
            "track_7" -> listOf(0.0, 4.0, 7.0, 9.0, 11.0, 12.0, 14.0, 12.0, 9.0, 7.0, 4.0, 2.0, 0.0, 2.0, 4.0, 7.0)[patternIndex] // Western Swing
            "track_8" -> listOf(0.0, 2.0, 4.0, 7.0, 9.0, 12.0, 9.0, 7.0, 4.0, 2.0, 0.0, 2.0, 4.0, 7.0, 4.0, 0.0)[patternIndex] // Folk Fingerpicking
            "track_9" -> listOf(12.0, 9.0, 7.0, 4.0, 0.0, 4.0, 7.0, 12.0, 14.0, 12.0, 9.0, 7.0, 4.0, 2.0, 0.0, 4.0)[patternIndex] // Jubilee Breakdown
            "track_10" -> listOf(0.0, 4.0, 7.0, 12.0, 9.0, 7.0, 4.0, 2.0, 5.0, 9.0, 12.0, 9.0, 7.0, 5.0, 4.0, 2.0)[patternIndex] // Shenandoah Strum
            "track_11" -> listOf(0.0, 4.0, 7.0, 12.0, 7.0, 4.0, 0.0, 4.0, 5.0, 9.0, 12.0, 9.0, 7.0, 4.0, 2.0, 0.0)[patternIndex] // Honky-Tonk Lead
            "track_12" -> listOf(0.0, 4.0, 7.0, 11.0, 12.0, 11.0, 7.0, 4.0, 5.0, 9.0, 12.0, 9.0, 7.0, 4.0, 2.0, 0.0)[patternIndex] // Sunset Reverie
            "track_13" -> listOf(12.0, 7.0, 4.0, 7.0, 12.0, 16.0, 12.0, 7.0, 5.0, 9.0, 12.0, 9.0, 7.0, 4.0, 2.0, 0.0)[patternIndex] // Barn Stomp
            "track_14" -> listOf(0.0, 3.0, 4.0, 7.0, 10.0, 12.0, 10.0, 7.0, 0.0, 3.0, 4.0, 7.0, 10.0, 7.0, 4.0, 0.0)[patternIndex] // Outlaw Western
            else -> listOf(12.0, 14.0, 16.0, 12.0, 7.0, 4.0, 0.0, 4.0, 7.0, 12.0, 16.0, 19.0, 16.0, 12.0, 7.0, 0.0)[patternIndex] // Virtuoso Finale
        }
    }


    // =========================================================================
    // 2. DRIVING AMBIENT SOUNDSCAPES (FOREST, BIRD CALLS, NATURE BREEZE)
    // =========================================================================

    private fun startAmbienceAudioLoop() {
        ambienceJob?.cancel()
        ambienceJob = scope.launch {
            val sampleRate = 22050
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            var track: AudioTrack? = null
            try {
                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()
                val chunk = ShortArray(1024)
                var sampleCounter = 0L

                while (isActive) {
                    if (!isDrivingAmbienceActive) {
                        chunk.fill(0)
                        track.write(chunk, 0, chunk.size)
                        delay(60)
                        continue
                    }

                    val env = currentEnvironment
                    val wtr = currentWeather

                    for (i in chunk.indices) {
                        sampleCounter++
                        val t = sampleCounter.toDouble() / sampleRate

                        val envValue: Double = when (env) {
                            // FOREST / REDWOOD COAST: Soothing pine breeze + gentle natural bird chirping calls
                            EnvironmentType.REDWOOD_COAST -> {
                                // 1. Gentle rustling pine breeze (filtered smooth noise)
                                val windMod = 0.5 + 0.5 * sin(2.0 * PI * 0.15 * t)
                                val windNoise = (Math.random() * 2.0 - 1.0) * 0.12 * windMod

                                // 2. Periodic natural bird chirp calls (melodic FM sine chirps every 3.5 seconds)
                                val birdPeriod = 3.5
                                val birdLocalTime = (t % birdPeriod)
                                val birdSample = if (birdLocalTime < 0.35) {
                                    val birdT = birdLocalTime / 0.35
                                    // Ascending warble chirp from 2400 Hz to 3600 Hz
                                    val birdFreq = 2600.0 + sin(birdT * PI * 6.0) * 800.0
                                    val birdSine = sin(2.0 * PI * birdFreq * t)
                                    val birdEnv = sin(birdT * PI)
                                    birdSine * birdEnv * 0.22
                                } else if (birdLocalTime in 0.45..0.75) {
                                    val birdT = (birdLocalTime - 0.45) / 0.30
                                    val birdFreq = 3200.0 - (birdT * 600.0)
                                    val birdSine = sin(2.0 * PI * birdFreq * t)
                                    val birdEnv = sin(birdT * PI)
                                    birdSine * birdEnv * 0.18
                                } else {
                                    0.0
                                }

                                (windNoise + birdSample) * 0.65
                            }

                            // ALPINE PEAKS: Mountain breeze & whistling gusts
                            EnvironmentType.ALPINE_PEAKS -> {
                                val gustMod = 0.4 + 0.6 * sin(2.0 * PI * 0.18 * t)
                                val windHiss = (Math.random() * 2.0 - 1.0) * 0.15 * gustMod
                                val whistleTone = sin(2.0 * PI * (440.0 + sin(t * 0.8) * 60.0) * t) * 0.05 * gustMod
                                (windHiss + whistleTone) * 0.6
                            }

                            // DESERT CANYON: Warm canyon wind sweeps
                            EnvironmentType.DESERT_CANYON -> {
                                val canyonSweep = 0.5 + 0.5 * sin(2.0 * PI * 0.12 * t)
                                val sandBreeze = (Math.random() * 2.0 - 1.0) * 0.14 * canyonSweep
                                sandBreeze * 0.55
                            }

                            // INDUSTRIAL VALLEY: Deep generator hum & resonant metallic clank
                            EnvironmentType.INDUSTRIAL_VALLEY -> {
                                val plantHum = sin(2.0 * PI * 60.0 * t) * 0.12 + sin(2.0 * PI * 120.0 * t) * 0.06
                                val steamVent = if ((t % 4.0) < 0.6) (Math.random() * 2.0 - 1.0) * 0.08 else 0.0
                                (plantHum + steamVent) * 0.65
                            }

                            // ARCTIC PASS: Blizzard chill gusts
                            EnvironmentType.ARCTIC_PASS -> {
                                val blizzard = (Math.random() * 2.0 - 1.0) * (0.16 + 0.10 * sin(2.0 * PI * 0.25 * t))
                                val iceWhistle = sin(2.0 * PI * (620.0 + sin(t * 1.2) * 90.0) * t) * 0.06
                                (blizzard + iceWhistle) * 0.65
                            }
                        }

                        // Weather Ambient Sound Layer
                        val weatherValue: Double = when (wtr) {
                            WeatherType.CLEAR -> 0.0
                            WeatherType.RAIN -> {
                                // Continuous soothing rain hiss and gentle patter
                                val rainHiss = (Math.random() * 2.0 - 1.0) * (0.18 + 0.04 * sin(2.0 * PI * 0.3 * t))
                                rainHiss * 0.75
                            }
                            WeatherType.THUNDERSTORM -> {
                                // Heavy downpour rain noise + gusty storm wind
                                val stormRain = (Math.random() * 2.0 - 1.0) * (0.24 + 0.08 * sin(2.0 * PI * 0.45 * t))
                                val stormWind = sin(2.0 * PI * (120.0 + sin(t * 0.5) * 30.0) * t) * 0.06
                                (stormRain + stormWind) * 0.85
                            }
                            WeatherType.SNOW -> {
                                // Gentle winter hush noise
                                val snowNoise = (Math.random() * 2.0 - 1.0) * 0.07
                                snowNoise * 0.5
                            }
                            WeatherType.BLIZZARD -> {
                                // Freezing blizzard wind howl and turbulent squall
                                val galeNoise = (Math.random() * 2.0 - 1.0) * (0.22 + 0.08 * sin(2.0 * PI * 0.2 * t))
                                val howlTone = sin(2.0 * PI * (540.0 + sin(t * 1.5) * 110.0) * t) * 0.08
                                (galeNoise + howlTone) * 0.8
                            }
                            WeatherType.FOG -> {
                                // Damp calm breeze
                                val fogNoise = (Math.random() * 2.0 - 1.0) * 0.05
                                fogNoise * 0.4
                            }
                        }

                        val combinedSample = (envValue * 0.65 + weatherValue * 0.75)
                        chunk[i] = (combinedSample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.55).toInt().toShort()
                    }

                    track.write(chunk, 0, chunk.size)
                }
            } catch (_: Exception) {
            } finally {
                try {
                    track?.stop()
                    track?.release()
                } catch (_: Exception) {}
            }
        }
    }

    // =========================================================================
    // 3. AUTHENTIC TRAIN HORNS & STEAM WHISTLES (SAMPLED ACOUSTIC SYNTHESIS)
    // - Electric Horns: 2-Tone warning chimes (Tone 1 -> Tone 2)
    // - Diesel Horns: Raw single locomotive horn (no multiple horns sounding)
    // - Steam Whistles: Authentic shifting single steam chime
    // - Every train has exactly ONE unique, dedicated horn (no randomizing)
    // =========================================================================

    enum class HornCategory {
        DIESEL,
        ELECTRIC,
        STEAM
    }

    data class TrainHornSample(
        val id: String,
        val displayName: String,
        val category: HornCategory,
        val baseFreq: Double,
        val secondToneFreq: Double? = null,
        val isTwoTone: Boolean = false,
        val isSteam: Boolean = false,
        val steamHissNoise: Double = 0.0,
        val attackMs: Double = 14.0,
        val releaseMs: Double = 95.0,
        val rawGrit: Double = 0.0
    )

    companion object {
        // Default Horns
        val DEFAULT_ELECTRIC_HORN = TrainHornSample(
            id = "default_electric_twotone",
            displayName = "Electric High-Speed 2-Tone Warning Horn",
            category = HornCategory.ELECTRIC,
            baseFreq = 659.25, // E5 (High Tone)
            secondToneFreq = 523.25, // C5 (Low Tone)
            isTwoTone = true,
            attackMs = 10.0,
            releaseMs = 85.0
        )

        val DEFAULT_DIESEL_HORN = TrainHornSample(
            id = "default_diesel_raw",
            displayName = "Diesel Raw Single Locomotive Horn",
            category = HornCategory.DIESEL,
            baseFreq = 311.13, // D#4 raw fundamental
            rawGrit = 0.55,
            attackMs = 12.0,
            releaseMs = 95.0
        )

        val DEFAULT_STEAM_WHISTLE = TrainHornSample(
            id = "default_steam_whistle",
            displayName = "Classic Steamboat Steam Whistle",
            category = HornCategory.STEAM,
            baseFreq = 293.66, // D4
            isSteam = true,
            steamHissNoise = 0.09,
            attackMs = 100.0,
            releaseMs = 180.0
        )

        // 1. DEDICATED TRAIN-SPECIFIC HORNS (Every single train has ONLY 1 unique horn)
        val TRAIN_SPECIFIC_HORNS: Map<String, TrainHornSample> = mapOf(
            // --- ELECTRIC TRAINS (2-TONE HORNS) ---
            "secret_virgin_pendolino_390" to TrainHornSample(
                id = "pendolino_390_twotone",
                displayName = "British Rail Class 390 Pendolino 2-Tone High-Speed Horn",
                category = HornCategory.ELECTRIC,
                baseFreq = 659.25, // E5 High Tone
                secondToneFreq = 523.25, // C5 Low Tone
                isTwoTone = true,
                attackMs = 10.0,
                releaseMs = 80.0
            ),
            "metra_highliner_emu" to TrainHornSample(
                id = "metra_highliner_twotone",
                displayName = "Metra Highliner EMU District 2-Tone Electric Horn",
                category = HornCategory.ELECTRIC,
                baseFreq = 587.33, // D5 High Tone
                secondToneFreq = 440.00, // A4 Low Tone
                isTwoTone = true,
                attackMs = 11.0,
                releaseMs = 85.0
            ),
            "metra_alstom_coradia_dual" to TrainHornSample(
                id = "metra_coradia_twotone",
                displayName = "Metra Dual-Power Fastliner 2-Tone Warning Horn",
                category = HornCategory.ELECTRIC,
                baseFreq = 622.25, // D#5 High Tone
                secondToneFreq = 466.16, // A#4 Low Tone
                isTwoTone = true,
                attackMs = 10.0,
                releaseMs = 85.0
            ),
            "siemens_vectron_red" to TrainHornSample(
                id = "vectron_db_twotone",
                displayName = "Siemens Vectron DB Traffic Red 2-Tone Chime",
                category = HornCategory.ELECTRIC,
                baseFreq = 523.25, // C5 High Tone
                secondToneFreq = 415.30, // G#4 Low Tone
                isTwoTone = true,
                attackMs = 10.0,
                releaseMs = 85.0
            ),
            "siemens_vectron_black" to TrainHornSample(
                id = "vectron_mrce_twotone",
                displayName = "Siemens Vectron MRCE Black 2-Tone Precision Horn",
                category = HornCategory.ELECTRIC,
                baseFreq = 587.33, // D5 High Tone
                secondToneFreq = 440.00, // A4 Low Tone
                isTwoTone = true,
                attackMs = 10.0,
                releaseMs = 85.0
            ),
            "siemens_vectron_demo" to TrainHornSample(
                id = "vectron_demo_twotone",
                displayName = "Siemens Vectron Demo 2-Tone Precision Chime",
                category = HornCategory.ELECTRIC,
                baseFreq = 554.37, // C#5 High Tone
                secondToneFreq = 440.00, // A4 Low Tone
                isTwoTone = true,
                attackMs = 10.0,
                releaseMs = 85.0
            ),
            "amtrak_acela_bullet" to TrainHornSample(
                id = "acela_bullet_twotone",
                displayName = "Amtrak Acela Express High-Speed 2-Tone Horn",
                category = HornCategory.ELECTRIC,
                baseFreq = 698.46, // F5 High Tone
                secondToneFreq = 554.37, // C#5 Low Tone
                isTwoTone = true,
                attackMs = 10.0,
                releaseMs = 80.0
            ),
            "siemens_american_flyer" to TrainHornSample(
                id = "american_flyer_twotone",
                displayName = "Siemens American Flyer 2-Tone High-Speed Chime",
                category = HornCategory.ELECTRIC,
                baseFreq = 659.25, // E5 High Tone
                secondToneFreq = 493.88, // B4 Low Tone
                isTwoTone = true,
                attackMs = 11.0,
                releaseMs = 85.0
            ),
            "db_class_103" to TrainHornSample(
                id = "db_103_twotone",
                displayName = "DB Class 103 German TEE 2-Tone Makrofon",
                category = HornCategory.ELECTRIC,
                baseFreq = 587.33, // D5 High Tone
                secondToneFreq = 493.88, // B4 Low Tone
                isTwoTone = true,
                attackMs = 10.0,
                releaseMs = 85.0
            ),
            "tgv_sud_est" to TrainHornSample(
                id = "tgv_sud_est_twotone",
                displayName = "SNCF TGV Sud-Est 2-Tone High-Speed Avertisseur",
                category = HornCategory.ELECTRIC,
                baseFreq = 783.99, // G5 High Tone
                secondToneFreq = 659.25, // E5 Low Tone
                isTwoTone = true,
                attackMs = 9.0,
                releaseMs = 80.0
            ),
            "shinkansen_0_series" to TrainHornSample(
                id = "shinkansen_0_twotone",
                displayName = "Japanese Shinkansen 0 Series 2-Tone Bullet Horn",
                category = HornCategory.ELECTRIC,
                baseFreq = 440.00, // A4 High Tone
                secondToneFreq = 369.99, // F#4 Low Tone
                isTwoTone = true,
                attackMs = 10.0,
                releaseMs = 85.0
            ),
            "secret_shinkansen_e2" to TrainHornSample(
                id = "shinkansen_e2_twotone",
                displayName = "Shinkansen E2-1000 Supersonic 2-Tone Bullet Horn",
                category = HornCategory.ELECTRIC,
                baseFreq = 493.88, // B4 High Tone
                secondToneFreq = 392.00, // G4 Low Tone
                isTwoTone = true,
                attackMs = 9.0,
                releaseMs = 80.0
            ),
            "prr_gg1" to TrainHornSample(
                id = "prr_gg1_low_pitch_leslie",
                displayName = "PRR GG1 Leslie A-200 Accurate Low-Pitch Horn",
                category = HornCategory.DIESEL, // Raw single low-pitch pneumatic horn
                baseFreq = 311.13, // Accurate D#4 / E-flat single low-pitch fundamental
                isTwoTone = false, // Strictly single low tone, no other horn
                secondToneFreq = null,
                rawGrit = 0.45,
                attackMs = 14.0,
                releaseMs = 110.0
            ),
            "soviet_vl85" to TrainHornSample(
                id = "soviet_vl85_twotone",
                displayName = "Soviet VL85 Siberian Heavy 2-Tone Typhon",
                category = HornCategory.ELECTRIC,
                baseFreq = 329.63, // E4 High Tone
                secondToneFreq = 261.63, // C4 Low Tone
                isTwoTone = true,
                attackMs = 12.0,
                releaseMs = 95.0
            ),
            "secret_cyber_maglev" to TrainHornSample(
                id = "cyber_maglev_twotone",
                displayName = "CyberRail X-Maglev Plasma Dual-Tone Resonance",
                category = HornCategory.ELECTRIC,
                baseFreq = 783.99, // G5 High Tone
                secondToneFreq = 523.25, // C5 Low Tone
                isTwoTone = true,
                attackMs = 8.0,
                releaseMs = 75.0
            ),
            "tow_electric_dual_rescue" to TrainHornSample(
                id = "aerotow_92_twotone",
                displayName = "AeroTow Class 92 Electric 2-Tone Rescue Chime",
                category = HornCategory.ELECTRIC,
                baseFreq = 440.00, // A4 High Tone
                secondToneFreq = 349.23, // F4 Low Tone
                isTwoTone = true,
                attackMs = 10.0,
                releaseMs = 85.0
            ),

            // --- DIESEL TRAINS (RAW SINGLE HORN - NO MULTIPLE HORNS SOUNDING) ---
            "metra_f40ph_screamer" to TrainHornSample(
                id = "metra_f40ph_nathan_k5la",
                displayName = "Metra F40PH Nathan K5LA Raw Locomotive Horn",
                category = HornCategory.DIESEL,
                baseFreq = 311.13, // D#4 raw fundamental
                rawGrit = 0.55,
                attackMs = 12.0,
                releaseMs = 95.0
            ),
            "emd_f40ph_metra" to TrainHornSample(
                id = "metra_f40ph_120_raw",
                displayName = "Metra #120 Patriot Raw Locomotive Horn",
                category = HornCategory.DIESEL,
                baseFreq = 311.13,
                rawGrit = 0.55,
                attackMs = 12.0,
                releaseMs = 95.0
            ),
            "metra_mp36ph_express" to TrainHornSample(
                id = "metra_mp36ph_leslie_s3l",
                displayName = "Metra MP36PH Leslie S-3L Throaty Raw Horn",
                category = HornCategory.DIESEL,
                baseFreq = 246.94, // B3 raw throat
                rawGrit = 0.60,
                attackMs = 14.0,
                releaseMs = 100.0
            ),
            "metra_f59phi_silver" to TrainHornSample(
                id = "metra_f59phi_k3la",
                displayName = "Metra F59PHI AirChime K3LA Raw Diesel Horn",
                category = HornCategory.DIESEL,
                baseFreq = 293.66, // D4 raw mid
                rawGrit = 0.50,
                attackMs = 12.0,
                releaseMs = 90.0
            ),
            "metra_charger_sc44" to TrainHornSample(
                id = "metra_charger_k5hl",
                displayName = "Metra Siemens SC-44 Charger Raw Heavy Horn",
                category = HornCategory.DIESEL,
                baseFreq = 261.63, // C4 raw power
                rawGrit = 0.52,
                attackMs = 12.0,
                releaseMs = 95.0
            ),
            "metra_e8_streamliner" to TrainHornSample(
                id = "metra_e8_classic_dual",
                displayName = "Metra EMD E8 Classic Twin-Bulldog Raw Horn",
                category = HornCategory.DIESEL,
                baseFreq = 277.18, // C#4 raw vintage
                rawGrit = 0.48,
                attackMs = 15.0,
                releaseMs = 105.0
            ),
            "emd_f40ph_via" to TrainHornSample(
                id = "via_f40ph_raw",
                displayName = "VIA Rail Canada F40PH-2D Raw Passenger Horn",
                category = HornCategory.DIESEL,
                baseFreq = 329.63, // E4 raw fundamental
                rawGrit = 0.50,
                attackMs = 12.0,
                releaseMs = 90.0
            ),
            "emd_gp9_highhood" to TrainHornSample(
                id = "ns_gp9_leslie_s3l",
                displayName = "Norfolk Southern GP9 High Hood Raw Leslie Horn",
                category = HornCategory.DIESEL,
                baseFreq = 261.63, // C4 raw punch
                rawGrit = 0.58,
                attackMs = 14.0,
                releaseMs = 100.0
            ),
            "emd_gp9_normal" to TrainHornSample(
                id = "cn_gp9_raw",
                displayName = "Canadian National GP9-RM Raw Single Diesel Horn",
                category = HornCategory.DIESEL,
                baseFreq = 277.18, // C#4 raw tone
                rawGrit = 0.52,
                attackMs = 13.0,
                releaseMs = 95.0
            ),
            "cd_effishunter_742" to TrainHornSample(
                id = "cd_effishunter_raw",
                displayName = "ČD 742 EffiShunter Raw European Diesel Horn",
                category = HornCategory.DIESEL,
                baseFreq = 349.23, // F4 raw punch
                rawGrit = 0.50,
                attackMs = 12.0,
                releaseMs = 90.0
            ),
            "burlington_f7" to TrainHornSample(
                id = "burlington_f7_raw",
                displayName = "Burlington Route F7 Raw Streamliner Diesel Horn",
                category = HornCategory.DIESEL,
                baseFreq = 293.66, // D4 raw vintage
                rawGrit = 0.48,
                attackMs = 14.0,
                releaseMs = 95.0
            ),
            "br_class_55_deltic" to TrainHornSample(
                id = "br_deltic_raw",
                displayName = "British Rail Class 55 Deltic Raw Napier Horn",
                category = HornCategory.DIESEL,
                baseFreq = 392.00, // G4 raw racing diesel
                rawGrit = 0.54,
                attackMs = 12.0,
                releaseMs = 90.0
            ),
            "ge_ac4400cw" to TrainHornSample(
                id = "ge_ac4400cw_k5hl",
                displayName = "GE AC4400CW Raw Nathan K5HL Mountain Titan Horn",
                category = HornCategory.DIESEL,
                baseFreq = 261.63, // C4 raw power
                rawGrit = 0.58,
                attackMs = 14.0,
                releaseMs = 110.0
            ),
            "emd_sd70ace" to TrainHornSample(
                id = "emd_sd70ace_k5la",
                displayName = "EMD SD70ACe Raw Heavy Freight Locomotive Horn",
                category = HornCategory.DIESEL,
                baseFreq = 311.13, // D#4 raw growl
                rawGrit = 0.56,
                attackMs = 13.0,
                releaseMs = 105.0
            ),
            "alco_pa1" to TrainHornSample(
                id = "alco_pa1_raw",
                displayName = "ALCO PA-1 Raw Deep-Throat Honk",
                category = HornCategory.DIESEL,
                baseFreq = 246.94, // B3 raw throat
                rawGrit = 0.62,
                attackMs = 15.0,
                releaseMs = 110.0
            ),
            "secret_golden_emd" to TrainHornSample(
                id = "golden_emd_raw",
                displayName = "EMD Golden Eagle 24K Sovereign Raw Diesel Horn",
                category = HornCategory.DIESEL,
                baseFreq = 329.63, // E4 rich gold
                rawGrit = 0.52,
                attackMs = 12.0,
                releaseMs = 90.0
            ),
            "tow_diesel_heavy_rescue" to TrainHornSample(
                id = "titan_rescue_raw",
                displayName = "Titan HD-5000 Rescue Raw Locomotive Horn",
                category = HornCategory.DIESEL,
                baseFreq = 233.08, // Bb3 raw heavy
                rawGrit = 0.60,
                attackMs = 13.0,
                releaseMs = 100.0
            ),

            // --- STEAM TRAINS (AUTHENTIC SINGLE STEAM WHISTLES) ---
            "up_big_boy_4014" to TrainHornSample(
                id = "big_boy_4014_whistle",
                displayName = "Union Pacific Big Boy 4014 Deep 5-Chime Whistle",
                category = HornCategory.STEAM,
                baseFreq = 233.08, // Bb3 deep steam
                isSteam = true,
                steamHissNoise = 0.12,
                attackMs = 120.0,
                releaseMs = 210.0
            ),
            "pacific_462" to TrainHornSample(
                id = "pacific_462_whistle",
                displayName = "Pacific 4-6-2 Iron Titan Steamboat Whistle",
                category = HornCategory.STEAM,
                baseFreq = 293.66, // D4
                isSteam = true,
                steamHissNoise = 0.09,
                attackMs = 100.0,
                releaseMs = 180.0
            ),
            "nyc_dreyfuss_hudson" to TrainHornSample(
                id = "nyc_dreyfuss_whistle",
                displayName = "NYC J3a Dreyfuss Hudson Art Deco Streamlined Whistle",
                category = HornCategory.STEAM,
                baseFreq = 349.23, // F4
                isSteam = true,
                steamHissNoise = 0.08,
                attackMs = 90.0,
                releaseMs = 170.0
            ),
            "cp_royal_hudson" to TrainHornSample(
                id = "cp_royal_whistle",
                displayName = "Canadian Pacific Royal Hudson 2850 Classic Whistle",
                category = HornCategory.STEAM,
                baseFreq = 261.63, // C4
                isSteam = true,
                steamHissNoise = 0.08,
                attackMs = 100.0,
                releaseMs = 180.0
            ),
            "tow_steam_lner_1472" to TrainHornSample(
                id = "lner_1472_scotsman_whistle",
                displayName = "LNER 1472 Flying Scotsman High-to-Low Shifting Chime Whistle",
                category = HornCategory.STEAM,
                baseFreq = 440.00, // A4 shifting down
                isSteam = true,
                steamHissNoise = 0.10,
                attackMs = 90.0,
                releaseMs = 170.0
            ),
            "secret_ghost_train" to TrainHornSample(
                id = "ghost_spectral_whistle",
                displayName = "Phantom Express 9000 Spectral Ghost Steam Shriek",
                category = HornCategory.STEAM,
                baseFreq = 587.33, // D5 eerie screech
                isSteam = true,
                steamHissNoise = 0.16,
                attackMs = 80.0,
                releaseMs = 160.0
            ),
            "secret_hyper_steam" to TrainHornSample(
                id = "hyper_colossus_whistle",
                displayName = "Hyper Steam Colossus 4-8-4 Supercharged Steam Whistle",
                category = HornCategory.STEAM,
                baseFreq = 220.00, // A3 deep thunder
                isSteam = true,
                steamHissNoise = 0.14,
                attackMs = 110.0,
                releaseMs = 200.0
            )
        )

        // SoundProfile fallback map
        val PROFILE_SPECIFIC_HORNS: Map<SoundProfileType, TrainHornSample> = mapOf(
            SoundProfileType.CLASS_390_PENDOLINO_ELECTRIC to (TRAIN_SPECIFIC_HORNS["secret_virgin_pendolino_390"] ?: DEFAULT_ELECTRIC_HORN),
            SoundProfileType.SIEMENS_VECTRON_ELECTRIC to (TRAIN_SPECIFIC_HORNS["siemens_vectron_red"] ?: DEFAULT_ELECTRIC_HORN),
            SoundProfileType.TGV_HIGH_SPEED_ELECTRIC to (TRAIN_SPECIFIC_HORNS["tgv_sud_est"] ?: DEFAULT_ELECTRIC_HORN),
            SoundProfileType.SHINKANSEN_BULLET_ELECTRIC to (TRAIN_SPECIFIC_HORNS["shinkansen_0_series"] ?: DEFAULT_ELECTRIC_HORN),
            SoundProfileType.EURO_ELECTRIC_103 to (TRAIN_SPECIFIC_HORNS["db_class_103"] ?: DEFAULT_ELECTRIC_HORN),
            SoundProfileType.PRR_GG1_ARTDECO_ELECTRIC to (TRAIN_SPECIFIC_HORNS["prr_gg1"] ?: DEFAULT_ELECTRIC_HORN),
            SoundProfileType.SOVIET_VL85_ELECTRIC to (TRAIN_SPECIFIC_HORNS["soviet_vl85"] ?: DEFAULT_ELECTRIC_HORN),
            SoundProfileType.CYBER_MAGLEV_PLASMA to (TRAIN_SPECIFIC_HORNS["secret_cyber_maglev"] ?: DEFAULT_ELECTRIC_HORN),
            SoundProfileType.F40PH_SCREAMER_DIESEL to (TRAIN_SPECIFIC_HORNS["metra_f40ph_screamer"] ?: DEFAULT_DIESEL_HORN),
            SoundProfileType.EMD_2STROKE_DIESEL to (TRAIN_SPECIFIC_HORNS["emd_sd70ace"] ?: DEFAULT_DIESEL_HORN),
            SoundProfileType.GE_4STROKE_TURBODIESEL to (TRAIN_SPECIFIC_HORNS["ge_ac4400cw"] ?: DEFAULT_DIESEL_HORN),
            SoundProfileType.ALCO_V16_DIESEL to (TRAIN_SPECIFIC_HORNS["alco_pa1"] ?: DEFAULT_DIESEL_HORN),
            SoundProfileType.DELTIC_NAPIER_DIESEL to (TRAIN_SPECIFIC_HORNS["br_class_55_deltic"] ?: DEFAULT_DIESEL_HORN),
            SoundProfileType.BIG_BOY_ARTICULATED_STEAM to (TRAIN_SPECIFIC_HORNS["up_big_boy_4014"] ?: DEFAULT_STEAM_WHISTLE),
            SoundProfileType.PACIFIC_CLASSIC_STEAM to (TRAIN_SPECIFIC_HORNS["pacific_462"] ?: DEFAULT_STEAM_WHISTLE),
            SoundProfileType.DREYFUSS_STREAMLINE_STEAM to (TRAIN_SPECIFIC_HORNS["nyc_dreyfuss_hudson"] ?: DEFAULT_STEAM_WHISTLE),
            SoundProfileType.ROYAL_HUDSON_STEAM to (TRAIN_SPECIFIC_HORNS["cp_royal_hudson"] ?: DEFAULT_STEAM_WHISTLE),
            SoundProfileType.LNER_A3_STEAM_WHISTLE to (TRAIN_SPECIFIC_HORNS["tow_steam_lner_1472"] ?: DEFAULT_STEAM_WHISTLE),
            SoundProfileType.GHOST_SPECTRAL_STEAM to (TRAIN_SPECIFIC_HORNS["secret_ghost_train"] ?: DEFAULT_STEAM_WHISTLE),
            SoundProfileType.HYPER_COLOSSUS_STEAM to (TRAIN_SPECIFIC_HORNS["secret_hyper_steam"] ?: DEFAULT_STEAM_WHISTLE)
        )
    }

    private var lastPlayedHornSampleName: String = ""

    fun getLastPlayedHornName(): String = lastPlayedHornSampleName

    /**
     * Resolves the deterministic single horn for a train.
     */
    fun getHornForTrain(trainId: String?, trainType: TrainType?, soundProfile: SoundProfileType?): TrainHornSample {
        if (trainId != null && TRAIN_SPECIFIC_HORNS.containsKey(trainId)) {
            return TRAIN_SPECIFIC_HORNS[trainId]!!
        }
        if (soundProfile != null && PROFILE_SPECIFIC_HORNS.containsKey(soundProfile)) {
            return PROFILE_SPECIFIC_HORNS[soundProfile]!!
        }
        return when (trainType) {
            TrainType.ELECTRIC_MAGLEV -> DEFAULT_ELECTRIC_HORN
            TrainType.STEAM -> DEFAULT_STEAM_WHISTLE
            TrainType.DIESEL, null -> DEFAULT_DIESEL_HORN
        }
    }

    /**
     * Blasts the train's unique horn deterministically.
     * - Electric trains: 2-Tone horn
     * - Diesel trains: Raw single horn (only 1 horn, no multiple horns sounding)
     * - Steam trains: Dedicated steam whistle
     */
    fun blastHorn(
        trainId: String,
        trainType: TrainType,
        soundProfile: SoundProfileType? = null,
        durationMs: Long = 1050
    ) {
        val sample = getHornForTrain(trainId, trainType, soundProfile)
        playHornSample(sample, durationMs)
    }

    fun blastHorn(
        trainType: TrainType,
        soundProfile: SoundProfileType? = null,
        durationMs: Long = 1050
    ) {
        val sample = getHornForTrain(null, trainType, soundProfile)
        playHornSample(sample, durationMs)
    }

    fun blastHorn(soundProfile: SoundProfileType, durationMs: Long = 1050) {
        val sample = getHornForTrain(null, null, soundProfile)
        playHornSample(sample, durationMs)
    }

    fun blastHorn(trainId: String, durationMs: Long = 1050) {
        val sample = getHornForTrain(trainId, null, null)
        playHornSample(sample, durationMs)
    }

    private var activeHornTrack: AudioTrack? = null
    private val hornTrackMutex = Any()

    /**
     * Synthesizes and plays authentic physical multi-chime locomotive air horns.
     * Accurately reproduces the harmonic chords, pneumatic air inrush chiff,
     * natural bell micro-detune beating, warm diaphragm saturation, and valve bleed decay.
     * Guarantees that only ONE horn sounds at any time (immediately stops any previous horn).
     */
    fun playHornSample(sample: TrainHornSample, durationMs: Long = 1050) {
        lastPlayedHornSampleName = sample.displayName
        hornJob?.cancel()

        // Immediately stop active horn track so only 1 horn is sounding
        synchronized(hornTrackMutex) {
            try {
                activeHornTrack?.pause()
                activeHornTrack?.flush()
                activeHornTrack?.stop()
                activeHornTrack?.release()
            } catch (_: Exception) {}
            activeHornTrack = null
        }

        hornJob = scope.launch {
            try {
                val sampleRate = 44100
                val totalDurationMs = durationMs.coerceAtLeast(800L)
                val numSamples = (sampleRate * (totalDurationMs / 1000f)).toInt()
                val buffer = ShortArray(numSamples)

                val attackSamples = (0.045 * sampleRate).toInt() // 45ms pneumatic attack
                val releaseSamples = (0.160 * sampleRate).toInt() // 160ms valve bleed release
                val twoToneSplit = (numSamples * 0.46).toInt()

                // Multi-chime frequency intervals and bell weights for authentic acoustic chords
                val chordIntervals: DoubleArray
                val bellWeights: DoubleArray

                when {
                    sample.isTwoTone -> {
                        chordIntervals = doubleArrayOf(1.0, 1.498) // Dual trumpet resonance
                        bellWeights = doubleArrayOf(0.65, 0.45)
                    }
                    sample.isSteam -> {
                        // Classic 5-chime steam whistle (D, F#, A, B, D)
                        chordIntervals = doubleArrayOf(1.0, 1.2599, 1.4983, 1.6818, 2.0)
                        bellWeights = doubleArrayOf(0.40, 0.28, 0.22, 0.16, 0.12)
                    }
                    sample.id.contains("leslie_s3l") || sample.id.contains("gp9") || sample.id.contains("e8") -> {
                        // Leslie S-3L 3-Chime Chord (B3, D#4, F#4)
                        chordIntervals = doubleArrayOf(1.0, 1.2599, 1.4983)
                        bellWeights = doubleArrayOf(0.48, 0.38, 0.32)
                    }
                    sample.id.contains("gg1") -> {
                        // Leslie A-200 / Wabco Dual Low-Pitch Chime
                        chordIntervals = doubleArrayOf(1.0, 1.4983)
                        bellWeights = doubleArrayOf(0.65, 0.42)
                    }
                    else -> {
                        // Authentic Nathan K5LA 5-Chime Chord (D#4, F#4, G#4, B4, D#5)
                        chordIntervals = doubleArrayOf(1.0, 1.1892, 1.3348, 1.5874, 2.0)
                        bellWeights = doubleArrayOf(0.38, 0.28, 0.24, 0.20, 0.16)
                    }
                }

                val numBells = chordIntervals.size
                val bellPhases = DoubleArray(numBells)
                val bellDetunePhases = DoubleArray(numBells)

                // Single-pole low-pass filter state for warm brass acoustics
                var filteredSample = 0.0
                val lpfAlpha = 0.32 // Low pass smoothing (~3200Hz cutoff)

                for (i in 0 until numSamples) {
                    val progressTotal = i.toDouble() / numSamples

                    // Smooth pneumatic valve pressure envelope
                    val env = when {
                        i < attackSamples -> {
                            val p = i.toDouble() / attackSamples
                            Math.sin(p * Math.PI * 0.5) // Smooth valve inrush
                        }
                        i > numSamples - releaseSamples -> {
                            val relProgress = (numSamples - i).toDouble() / releaseSamples
                            // Natural exponential pressure bleed-down
                            relProgress * relProgress
                        }
                        else -> 1.0
                    }

                    // Slight pitch droop on release as air reservoir pressure bleeds down
                    val releaseDroop = if (i > numSamples - releaseSamples) {
                        val relP = (i - (numSamples - releaseSamples)).toDouble() / releaseSamples
                        1.0 - (0.04 * relP)
                    } else {
                        1.0
                    }

                    var rawWave = 0.0

                    if (sample.isTwoTone && sample.secondToneFreq != null) {
                        // --- ELECTRIC 2-TONE HIGH SPEED HORN (European Makrofon / Bullet Train) ---
                        val isTone2 = i >= twoToneSplit
                        val activeBaseFreq = (if (isTone2) sample.secondToneFreq else sample.baseFreq) * releaseDroop

                        for (b in 0 until numBells) {
                            val bellFreq = activeBaseFreq * chordIntervals[b]
                            bellPhases[b] += (2.0 * Math.PI * bellFreq) / sampleRate
                            if (bellPhases[b] > 2.0 * Math.PI) bellPhases[b] -= 2.0 * Math.PI

                            bellDetunePhases[b] += (2.0 * Math.PI * (bellFreq * 1.002)) / sampleRate
                            if (bellDetunePhases[b] > 2.0 * Math.PI) bellDetunePhases[b] -= 2.0 * Math.PI

                            // Dual-mode bell vibration with natural acoustic beating
                            val bellFund = 0.70 * Math.sin(bellPhases[b]) + 0.30 * Math.sin(bellDetunePhases[b])
                            val bellH2 = 0.28 * Math.sin(bellPhases[b] * 2.0)
                            val bellH3 = 0.12 * Math.sin(bellPhases[b] * 3.0)

                            rawWave += (bellFund + bellH2 + bellH3) * bellWeights[b]
                        }

                        // Crossfade at 2-tone boundary
                        if (Math.abs(i - twoToneSplit) < 300) {
                            val cross = (i - (twoToneSplit - 300)).toDouble() / 600.0
                            val transEnv = 0.88 + 0.12 * Math.sin(cross * Math.PI)
                            rawWave *= transEnv
                        }
                    } else if (sample.category == HornCategory.DIESEL) {
                        // --- AUTHENTIC MULTI-CHIME DIESEL AIR HORN (Nathan K5LA / Leslie S3L / K3LA) ---
                        val currentBaseFreq = sample.baseFreq * releaseDroop

                        for (b in 0 until numBells) {
                            val bellFreq = currentBaseFreq * chordIntervals[b]

                            bellPhases[b] += (2.0 * Math.PI * bellFreq) / sampleRate
                            if (bellPhases[b] > 2.0 * Math.PI) bellPhases[b] -= 2.0 * Math.PI

                            // Micro-detuned paired oscillation (creates rich living acoustic beating)
                            val detuneRatio = 1.0 + (if (b % 2 == 0) 0.0032 else -0.0028)
                            bellDetunePhases[b] += (2.0 * Math.PI * (bellFreq * detuneRatio)) / sampleRate
                            if (bellDetunePhases[b] > 2.0 * Math.PI) bellDetunePhases[b] -= 2.0 * Math.PI

                            val bellOsc = 0.65 * Math.sin(bellPhases[b]) + 0.35 * Math.sin(bellDetunePhases[b])
                            val bellH2 = 0.32 * Math.sin(bellPhases[b] * 2.0)
                            val bellH3 = 0.15 * Math.sin(bellPhases[b] * 3.0)
                            val bellH4 = 0.06 * Math.sin(bellPhases[b] * 4.0)

                            rawWave += (bellOsc + bellH2 + bellH3 + bellH4) * bellWeights[b]
                        }

                        // Pneumatic air inrush chiff on attack (turbulent compressed air burst)
                        if (i < attackSamples * 2) {
                            val airProgress = 1.0 - (i.toDouble() / (attackSamples * 2))
                            val airBurst = (Math.random() * 2.0 - 1.0) * 0.08 * airProgress
                            rawWave += airBurst
                        }
                    } else {
                        // --- STEAM LOCOMOTIVE CHIME WHISTLE ---
                        val pitchShift = (1.04 - (0.07 * progressTotal)) * releaseDroop
                        val currentBaseFreq = sample.baseFreq * pitchShift

                        for (b in 0 until numBells) {
                            val bellFreq = currentBaseFreq * chordIntervals[b]

                            bellPhases[b] += (2.0 * Math.PI * bellFreq) / sampleRate
                            if (bellPhases[b] > 2.0 * Math.PI) bellPhases[b] -= 2.0 * Math.PI

                            bellDetunePhases[b] += (2.0 * Math.PI * (bellFreq * 1.0025)) / sampleRate
                            if (bellDetunePhases[b] > 2.0 * Math.PI) bellDetunePhases[b] -= 2.0 * Math.PI

                            val chimeOsc = 0.75 * Math.sin(bellPhases[b]) + 0.25 * Math.sin(bellDetunePhases[b])
                            val chimeH2 = 0.26 * Math.sin(bellPhases[b] * 2.0)
                            val chimeH3 = 0.12 * Math.sin(bellPhases[b] * 3.0)

                            rawWave += (chimeOsc + chimeH2 + chimeH3) * bellWeights[b]
                        }

                        // Organic steam jet hiss & breath
                        val hiss = (Math.random() * 2.0 - 1.0) * (sample.steamHissNoise.coerceAtLeast(0.06))
                        rawWave = rawWave * 0.90 + hiss * 0.10
                    }

                    // Warm pneumatic non-linear diaphragm compression (tanh saturation removes all arcade buzzer harshness)
                    val saturated = Math.tanh(rawWave * 1.35)

                    // Acoustic bell low-pass filtering to simulate body resonance & distance
                    filteredSample += lpfAlpha * (saturated - filteredSample)

                    val finalWave = filteredSample * env
                    buffer[i] = (finalWave * Short.MAX_VALUE * 0.78).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playHornBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    private fun playHornBuffer(buffer: ShortArray, sampleRate: Int) {
        try {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val format = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack(
                attributes,
                format,
                buffer.size * 2,
                AudioTrack.MODE_STATIC,
                0
            )

            track.write(buffer, 0, buffer.size)
            track.play()

            synchronized(hornTrackMutex) {
                activeHornTrack = track
            }

            scope.launch {
                delay((buffer.size.toDouble() / sampleRate * 1000).toLong() + 100)
                synchronized(hornTrackMutex) {
                    if (activeHornTrack == track) {
                        try {
                            track.stop()
                            track.release()
                        } catch (_: Exception) {}
                        activeHornTrack = null
                    }
                }
            }
        } catch (_: Exception) {
            synchronized(hornTrackMutex) {
                try {
                    activeHornTrack?.release()
                } catch (_: Exception) {}
                activeHornTrack = null
            }
        }
    }

    /**
     * Tactile mechanical metallic click sound for throttle quadrant notch movements.
     */
    fun playLeverClickSound() {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * 0.045).toInt()
                val buffer = ShortArray(numSamples)
                for (i in buffer.indices) {
                    val t = i.toDouble() / sampleRate
                    val env = Math.exp(-t * 110.0)
                    val click = sin(2.0 * PI * 1850.0 * t) * 0.6 + (Math.random() * 2.0 - 1.0) * 0.4
                    buffer[i] = (click * env * Short.MAX_VALUE * 0.65).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    // =========================================================================
    // 4. DISTINCT ELECTRIC VS. DIESEL ENGINE AUDIO (IDLE & ACCELERATION)
    // =========================================================================

    fun updateEngineState(
        soundProfile: SoundProfileType,
        speedKmh: Float,
        throttle: Float,
        reverserForward: Boolean,
        isBraking: Boolean
    ) {
        currentSoundProfile = soundProfile
        currentSpeed = speedKmh
        currentThrottle = throttle
    }

    private fun startEngineAudioLoop() {
        engineLoopJob?.cancel()
        engineLoopJob = scope.launch {
            val sampleRate = 22050
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            var track: AudioTrack? = null
            try {
                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()
                val chunk = ShortArray(1024)
                var phase = 0.0
                var turboPhase = 0.0
                var fanPhase = 0.0
                var sampleCounter = 0L

                while (isActive) {
                    val sp = currentSoundProfile
                    val speed = abs(currentSpeed)
                    val thr = currentThrottle.coerceIn(0f, 1f)

                    for (i in chunk.indices) {
                        sampleCounter++
                        val t = sampleCounter.toDouble() / sampleRate

                        val sampleValue: Double = when (sp) {
                            // STEAM LOCOMOTIVES: Dynamic percussive steam chuffs, firebox draft, siderod clatter & boiler sizzle
                            SoundProfileType.BIG_BOY_ARTICULATED_STEAM,
                            SoundProfileType.PACIFIC_CLASSIC_STEAM,
                            SoundProfileType.DREYFUSS_STREAMLINE_STEAM,
                            SoundProfileType.ROYAL_HUDSON_STEAM,
                            SoundProfileType.GHOST_SPECTRAL_STEAM,
                            SoundProfileType.HYPER_COLOSSUS_STEAM -> {
                                if (speed < 0.5f && thr < 0.02f) {
                                    // Idle: Gentle boiler simmer, safety valve sizzle, coal fire draft
                                    val simmer = (Math.random() * 2.0 - 1.0) * 0.12
                                    val boilerHum = sin(2.0 * PI * 52.0 * t) * 0.09
                                    val hiss = (Math.random() * 2.0 - 1.0) * 0.06
                                    (simmer + boilerHum + hiss) * 0.55
                                } else {
                                    // STEAM ACCELERATION: Heavy rhythmic acoustic chuffs barking under throttle load!
                                    // Chuff cadence synchronized with driver wheel rotation
                                    val chuffRate = (1.2 + (speed * 0.18)).coerceIn(1.0, 16.0)
                                    val chuffCycle = (t * chuffRate) % 1.0
                                    val chuffBark = if (chuffCycle < 0.32) {
                                        val env = sin(chuffCycle / 0.32 * PI)
                                        val noise = (Math.random() * 2.0 - 1.0)
                                        // Low frequency steam chest resonance + white noise blast
                                        val steamPop = sin(2.0 * PI * 75.0 * t) * 0.5 + sin(2.0 * PI * 150.0 * t) * 0.3
                                        (noise * 0.55 + steamPop * 0.45) * env * (0.35 + thr * 0.85)
                                    } else {
                                        (Math.random() * 2.0 - 1.0) * (0.05 + thr * 0.10)
                                    }
                                    // Siderod metallic clank
                                    val rodClank = if (chuffCycle in 0.48..0.54) {
                                        sin(2.0 * PI * 880.0 * t) * 0.15 * (speed / 80f).coerceIn(0.1f, 1f)
                                    } else 0.0

                                    // Firebox induction draft roar
                                    val draftRoar = (Math.random() * 2.0 - 1.0) * (0.04 + thr * 0.14)

                                    (chuffBark * 0.70 + rodClank + draftRoar) * 0.75
                                }
                            }

                            // ELECTRIC & HIGH-SPEED BULLET: Prototypical IGBT / GTO musical inverter stepped frequencies + traction motor whine!
                            SoundProfileType.EURO_ELECTRIC_103,
                            SoundProfileType.SIEMENS_VECTRON_ELECTRIC,
                            SoundProfileType.TGV_HIGH_SPEED_ELECTRIC,
                            SoundProfileType.SHINKANSEN_BULLET_ELECTRIC,
                            SoundProfileType.PRR_GG1_ARTDECO_ELECTRIC,
                            SoundProfileType.SOVIET_VL85_ELECTRIC,
                            SoundProfileType.CLASS_390_PENDOLINO_ELECTRIC,
                            SoundProfileType.CYBER_MAGLEV_PLASMA -> {
                                // 1. Constant cooling blower air stream
                                val blowerNoise = (Math.random() * 2.0 - 1.0) * 0.07
                                fanPhase += (2.0 * PI * 100.0) / sampleRate
                                // 2. High-voltage transformer 50Hz/60Hz/100Hz magnetostriction hum (swells under current load)
                                val transformerHum = (sin(fanPhase * 0.5) * 0.22 + sin(fanPhase) * 0.15) * (0.4 + thr * 0.6)

                                if (speed < 0.5f && thr < 0.02f) {
                                    // ELECTRIC IDLE: Deep electrical hum + blower ventilation airflow
                                    (transformerHum * 0.7 + blowerNoise * 0.6) * 0.55
                                } else {
                                    // ELECTRIC ACCELERATION:
                                    // A. Musical Stepped IGBT Inverter Switching Scale on start & acceleration
                                    val inverterStageFreq: Double = when {
                                        speed < 5.0f -> 220.0  // Musical Note A3
                                        speed < 10.0f -> 246.9 // Musical Note B3
                                        speed < 16.0f -> 277.2 // Musical Note C#4
                                        speed < 22.0f -> 293.7 // Musical Note D4
                                        speed < 30.0f -> 329.6 // Musical Note E4
                                        speed < 40.0f -> 370.0 // Musical Note F#4
                                        speed < 50.0f -> 415.3 // Musical Note G#4
                                        speed < 65.0f -> 440.0 // Musical Note A4
                                        else -> (500.0 + (speed - 65.0) * 14.0).coerceIn(500.0, 3200.0)
                                    }
                                    phase += (2.0 * PI * inverterStageFreq) / sampleRate
                                    // Inverter PWM switching tone
                                    val inverterPwm = sin(phase) * 0.40 + sin(phase * 2.0) * 0.20 + sin(phase * 3.0) * 0.10

                                    // B. Traction Motor Electromagnetic Whine (pitch rises continuously with wheel RPM)
                                    turboPhase += (2.0 * PI * (120.0 + speed * 15.5)) / sampleRate
                                    val motorWhine = sin(turboPhase) * 0.35 + sin(turboPhase * 2.0) * 0.18

                                    // C. Aerodynamic High-Speed Wind Rush
                                    val windRush = if (speed > 50f) {
                                        val windIntensity = ((speed - 50f) / 200f).coerceIn(0f, 1f)
                                        (Math.random() * 2.0 - 1.0) * windIntensity * 0.18
                                    } else 0.0

                                    val electricDrive = (
                                        inverterPwm * (0.35 + thr * 0.65) +
                                        motorWhine * (0.30 + thr * 0.50) +
                                        transformerHum * 0.35 +
                                        blowerNoise * 0.50 +
                                        windRush
                                    )
                                    electricDrive * 0.65
                                }
                            }

                            // DIESEL LOCOMOTIVES: 8-Notch Physical Revving, Throbbing Cylinder Detonations & Spooling Turbocharger
                            else -> {
                                if (speed < 0.5f && thr < 0.02f) {
                                    // DIESEL IDLE: Deep rhythmic 28-36 Hz pulsing cylinder detonations + compressor purr
                                    phase += (2.0 * PI * 32.0) / sampleRate
                                    val idleThrum = sin(phase) * 0.55 + sin(phase * 0.5) * 0.30 + sin(phase * 2.0) * 0.20
                                    val idleAirPurr = (Math.random() * 2.0 - 1.0) * 0.05
                                    (idleThrum + idleAirPurr) * 0.60
                                } else {
                                    // DIESEL ACCELERATION:
                                    // 1. 8-Notch RPM Stepping
                                    val notch = (thr * 8f).toInt().coerceIn(0, 8)
                                    val baseNotchRpm = 32.0 + (notch * 8.8) + (thr * 6.0) + (speed * 0.12)
                                    phase += (2.0 * PI * baseNotchRpm) / sampleRate

                                    // Multi-harmonic cylinder firing pressure pulses (warm saturated roar)
                                    val cyl1 = sin(phase) * 0.50
                                    val cyl2 = sin(phase * 2.0) * 0.32
                                    val cyl3 = sin(phase * 3.0) * 0.18
                                    val cyl4 = sin(phase * 4.0) * 0.10
                                    val cylinderRoar = (cyl1 + cyl2 + cyl3 + cyl4)

                                    // 2. High-Speed Dynamic Turbocharger Spooling (1.2 kHz -> 3.6 kHz whine surging with boost)
                                    val turboSpoolFreq = (1100.0 + (thr * 2400.0) + (speed * 6.0)).coerceIn(1100.0, 3800.0)
                                    turboPhase += (2.0 * PI * turboSpoolFreq) / sampleRate
                                    val turboImpellerWhine = sin(turboPhase) * (0.03 + thr * 0.26)

                                    // 3. Exhaust Manifold Air Pressure Roar & Radiator Cooling Fan
                                    val exhaustPressure = (Math.random() * 2.0 - 1.0) * (0.05 + thr * 0.20)
                                    fanPhase += (2.0 * PI * 68.0) / sampleRate
                                    val radiatorFan = sin(fanPhase) * 0.10

                                    val dieselDrive = (
                                        cylinderRoar * (0.40 + thr * 0.60) +
                                        turboImpellerWhine +
                                        exhaustPressure +
                                        radiatorFan
                                    )
                                    dieselDrive * 0.68
                                }
                            }
                        }

                        // Add Universal Steel-on-Rail Rolling Physics & Track Joint Click-Clack
                        val railRollingRumble = if (speed > 1f) {
                            val rollingNoise = (Math.random() * 2.0 - 1.0) * (speed / 180f).coerceIn(0.02f, 0.18f)
                            rollingNoise
                        } else 0.0

                        val finalAudioSample = (sampleValue * 0.85 + railRollingRumble)
                        chunk[i] = (finalAudioSample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.50).toInt().toShort()
                    }

                    track.write(chunk, 0, chunk.size)
                }
            } catch (_: Exception) {
            } finally {
                try {
                    track?.stop()
                    track?.release()
                } catch (_: Exception) {}
            }
        }
    }

    // =========================================================================
    // 5. CONTROL SOUND EFFECTS (LEVER CLICKS, HISS, SAND, CHIME)
    // =========================================================================

    fun playLeverClick() {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * 0.04f).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = (1.0 - (i.toDouble() / numSamples))
                    val wave = (sin(2.0 * PI * 1800.0 * t) * 0.5 + (Math.random() * 2.0 - 1.0) * 0.5) * env
                    buffer[i] = (wave * Short.MAX_VALUE * 0.4).toInt().toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    fun playDriveModeClick() {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * 0.07f).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = (1.0 - (i.toDouble() / numSamples))
                    val wave = (sin(2.0 * PI * 420.0 * t) * 0.7 + (Math.random() * 2.0 - 1.0) * 0.3) * env
                    buffer[i] = (wave * Short.MAX_VALUE * 0.5).toInt().toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    fun playHissSound(durationMs: Long = 400) {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val env = (1.0 - progress) * (1.0 - progress)
                    val noise = (Math.random() * 2.0 - 1.0) * env
                    buffer[i] = (noise * Short.MAX_VALUE * 0.35).toInt().toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    fun playSandSound() {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * 0.35f).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = (1.0 - (i.toDouble() / numSamples))
                    val grit = ((Math.random() * 2.0 - 1.0) * 0.6 + sin(2.0 * PI * 1800.0 * t) * 0.4) * env
                    buffer[i] = (grit * Short.MAX_VALUE * 0.3).toInt().toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    fun playRepairSound() {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * 0.45f).toInt()
                val buffer = ShortArray(numSamples)
                // Pneumatic impact wrench ratchet + metallic wrench clink + high-frequency restore chime
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val env = (1.0 - progress)

                    // 1. Ratchet pulses (rapid pneumatic clicks)
                    val ratchetPulse = (sin(2.0 * PI * 45.0 * t) > 0.0)
                    val ratchetNoise = if (ratchetPulse && t < 0.25) (Math.random() * 2.0 - 1.0) * 0.4 else 0.0

                    // 2. Metallic wrench ping
                    val pingEnv = if (t < 0.05) (t / 0.05) else (1.0 - (t - 0.05) / 0.4).coerceIn(0.0, 1.0)
                    val ping = sin(2.0 * PI * 1760.0 * t) * 0.35 * pingEnv + sin(2.0 * PI * 2637.0 * t) * 0.25 * pingEnv

                    // 3. Power drill whir
                    val whir = sin(2.0 * PI * (350.0 + t * 400.0) * t) * 0.2 * env

                    val wave = (ratchetNoise + ping + whir).coerceIn(-1.0, 1.0)
                    buffer[i] = (wave * Short.MAX_VALUE * 0.55).toInt().toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    fun playChime() {
        scope.launch {
            try {
                val sampleRate = 44100
                val numSamples = (sampleRate * 0.35f).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = (1.0 - (i.toDouble() / numSamples))
                    val wave = (sin(2.0 * PI * 1046.5 * t) * 0.6 + sin(2.0 * PI * 1318.5 * t) * 0.4) * env
                    buffer[i] = (wave * Short.MAX_VALUE * 0.4).toInt().toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    fun playSwitchTrack() {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * 0.18f).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = (1.0 - (i.toDouble() / numSamples))
                    val tone = (sin(2.0 * PI * 240.0 * t) * 0.6 + sin(2.0 * PI * 480.0 * t) * 0.4) * env
                    val click = if (t < 0.01) (Math.random() * 2.0 - 1.0) * 0.4 else 0.0
                    buffer[i] = ((tone + click).coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.6).toInt().toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    /**
     * Heavy industrial collision impact sound when train impacts the end buffer stop:
     * Deep low-end steel shudder + metallic clang + compressed pneumatic buffer hiss.
     */
    fun playBufferStopCrashSound() {
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * 0.85f).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples

                    // 1. Heavy low-frequency thump (65 Hz decaying rapidly)
                    val thumpEnv = (1.0 - progress).pow(3.0)
                    val thump = sin(2.0 * PI * 65.0 * t * (1.0 - t * 0.3)) * 0.65 * thumpEnv

                    // 2. High-energy metallic clash & crunch noise in first 0.3s
                    val crunchEnv = if (t < 0.02) (t / 0.02) else (1.0 - (t - 0.02) / 0.35).coerceIn(0.0, 1.0)
                    val noise = (Math.random() * 2.0 - 1.0) * 0.5 * crunchEnv
                    val metalPing = (sin(2.0 * PI * 420.0 * t) * 0.4 + sin(2.0 * PI * 880.0 * t) * 0.25) * crunchEnv

                    // 3. Hydraulic buffer cylinder pressure hiss
                    val hissEnv = if (t > 0.05) (1.0 - (t - 0.05) / 0.75).coerceIn(0.0, 1.0) * 0.35 else 0.0
                    val hiss = (Math.random() * 2.0 - 1.0) * hissEnv

                    val wave = (thump + noise + metalPing + hiss).coerceIn(-1.0, 1.0)
                    buffer[i] = (wave * Short.MAX_VALUE * 0.75).toInt().toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    private fun playPcmBuffer(buffer: ShortArray, sampleRate: Int) {
        var audioTrack: AudioTrack? = null
        try {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val format = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            audioTrack = AudioTrack(
                attributes,
                format,
                buffer.size * 2,
                AudioTrack.MODE_STATIC,
                0
            )

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            scope.launch {
                delay((buffer.size.toDouble() / sampleRate * 1000).toLong() + 100)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            try {
                audioTrack?.release()
            } catch (_: Exception) {}
        }
    }

    fun release() {
        musicJob?.cancel()
        engineLoopJob?.cancel()
        ambienceJob?.cancel()
        hornJob?.cancel()
        sfxJob?.cancel()
        synchronized(hornTrackMutex) {
            try {
                activeHornTrack?.stop()
                activeHornTrack?.release()
            } catch (_: Exception) {}
            activeHornTrack = null
        }
    }
}
