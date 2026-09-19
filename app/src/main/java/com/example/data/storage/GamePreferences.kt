package com.example.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.AppDatabase
import com.example.data.db.CompletedContractRecord
import com.example.data.db.PlayerDao
import com.example.data.db.PlayerStateEntity
import com.example.data.db.TrainUpgradeEntity
import com.example.data.model.GameContent
import com.example.data.model.TrainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LocomotiveHealth(
    val engineHealth: Float = 100f,
    val gearboxHealth: Float = 100f,
    val wheelsHealth: Float = 100f,
    val brakesHealth: Float = 100f
) {
    val overallPercent: Int get() = ((engineHealth + gearboxHealth + wheelsHealth + brakesHealth) / 4f).toInt().coerceIn(0, 100)
    val statusLabel: String get() = when {
        overallPercent >= 90 -> "OPTIMAL"
        overallPercent >= 75 -> "GOOD CONDITION"
        overallPercent >= 50 -> "MODERATE WEAR"
        overallPercent >= 25 -> "SERVICE DUE"
        else -> "CRITICAL WEAR"
    }
}

data class GameState(
    val silverCoins: Int = 38270,
    val goldCoins: Int = 7220,
    val blueprints: Int = 140,
    val diamonds: Int = 140,
    val coolantUnits: Int = 50,
    val sandUnits: Int = 50,
    val maxCoolantCapacity: Int = 100,
    val maxSandCapacity: Int = 100,
    val driverLevel: Int = 2,
    val driverXp: Int = 180,
    val selectedTrainId: String = "emd_gp50",
    val unlockedTrainIds: Set<String> = setOf("emd_gp50"),
    val redeemedCodes: Set<String> = emptySet(),
    val completedContractIds: Set<String> = emptySet(),
    val gearboxLevels: Map<String, Int> = mapOf("emd_gp50" to 1),
    val generatorLevels: Map<String, Int> = mapOf("emd_gp50" to 2),
    val engineLevels: Map<String, Int> = mapOf("emd_gp50" to 3),
    val wheelsLevels: Map<String, Int> = mapOf("emd_gp50" to 2),
    val locomotiveHealthMap: Map<String, LocomotiveHealth> = emptyMap(),
    val customBodyColors: Map<String, Long> = emptyMap(),
    val customStripeColors: Map<String, Long> = emptyMap(),
    val selectedLiveryVariants: Map<String, String> = mapOf("secret_virgin_pendolino_390" to "V1_VIRGIN"),
    val unlockedLiveryVariants: Set<String> = setOf("V1_VIRGIN"),
    val selectedMusicTrackId: String = "track_1",
    val isMusicPlaying: Boolean = true,
    val activeRepairTrainId: String? = null,
    val repairEndTimestamp: Long = 0L,
    val repairDurationSeconds: Int = 90
) {
    val playerDiamonds: Int get() = diamonds
    val trainLiveryVariants: Map<String, String> get() = selectedLiveryVariants
    val currentCoolant: Int get() = coolantUnits
    val currentSand: Int get() = sandUnits
    val coolantCount: Int get() = (coolantUnits / 20).coerceAtLeast(1)
    val sandbagCount: Int get() = (sandUnits / 20).coerceAtLeast(1)
}

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("train_sim_2d_prefs", Context.MODE_PRIVATE)

    private val _gameState = MutableStateFlow(loadInitialState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private fun loadInitialState(): GameState {
        val silver = prefs.getInt("silver_coins", 38270)
        val gold = prefs.getInt("gold_coins", 7220)
        val blueprints = prefs.getInt("blueprints", 32)
        val diamonds = prefs.getInt("diamonds", 140)
        val coolant = prefs.getInt("coolant_units", 50)
        val sand = prefs.getInt("sand_units", 50)
        val level = prefs.getInt("driver_level", 2)
        val xp = prefs.getInt("driver_xp", 180)
        val selectedTrain = prefs.getString("selected_train", "emd_gp50") ?: "emd_gp50"
        val unlockedTrains = prefs.getStringSet("unlocked_trains", setOf("emd_gp50")) ?: setOf("emd_gp50")
        val redeemed = prefs.getStringSet("redeemed_codes", emptySet()) ?: emptySet()
        val completed = prefs.getStringSet("completed_contracts", emptySet()) ?: emptySet()
        val musicTrack = prefs.getString("selected_music_track", "track_1") ?: "track_1"
        val musicEnabled = prefs.getBoolean("is_music_playing", true)

        val gbMap = mutableMapOf<String, Int>()
        val genMap = mutableMapOf<String, Int>()
        val engMap = mutableMapOf<String, Int>()
        val whMap = mutableMapOf<String, Int>()
        val bodyColorMap = mutableMapOf<String, Long>()
        val stripeColorMap = mutableMapOf<String, Long>()
        val healthMap = mutableMapOf<String, LocomotiveHealth>()

        val liveryMap = mutableMapOf<String, String>()
        val unlockedLiveries = prefs.getStringSet("unlocked_livery_variants", setOf("V1_VIRGIN")) ?: setOf("V1_VIRGIN")

        GameContent.ALL_TRAINS.forEach { train ->
            if (prefs.contains("livery_variant_${train.id}")) {
                liveryMap[train.id] = prefs.getString("livery_variant_${train.id}", "V1_VIRGIN") ?: "V1_VIRGIN"
            } else if (train.id == "secret_virgin_pendolino_390") {
                liveryMap[train.id] = "V1_VIRGIN"
            }
            val defaultUpg = if (train.isSecret) train.maxUpgradeLevel else (if (train.id == "emd_gp50") 2 else 1)
            gbMap[train.id] = prefs.getInt("upg_gb_${train.id}", if (train.isSecret) train.maxUpgradeLevel else (if (train.id == "emd_gp50") 1 else 1))
            genMap[train.id] = prefs.getInt("upg_gen_${train.id}", if (train.isSecret) train.maxUpgradeLevel else (if (train.id == "emd_gp50") 2 else 1))
            engMap[train.id] = prefs.getInt("upg_eng_${train.id}", if (train.isSecret) train.maxUpgradeLevel else (if (train.id == "emd_gp50") 3 else 1))
            whMap[train.id] = prefs.getInt("upg_wh_${train.id}", if (train.isSecret) train.maxUpgradeLevel else (if (train.id == "emd_gp50") 2 else 1))
            if (prefs.contains("paint_body_${train.id}")) {
                bodyColorMap[train.id] = prefs.getLong("paint_body_${train.id}", train.defaultBodyColor)
            }
            if (prefs.contains("paint_stripe_${train.id}")) {
                stripeColorMap[train.id] = prefs.getLong("paint_stripe_${train.id}", train.defaultStripeColor)
            }
            // Load wear & tear condition percentages (Default 100% health for new trains)
            val engH = prefs.getFloat("health_eng_${train.id}", 100f)
            val gbH = prefs.getFloat("health_gb_${train.id}", 100f)
            val whH = prefs.getFloat("health_wh_${train.id}", 100f)
            val brkH = prefs.getFloat("health_brk_${train.id}", 100f)
            healthMap[train.id] = LocomotiveHealth(
                engineHealth = engH.coerceIn(0f, 100f),
                gearboxHealth = gbH.coerceIn(0f, 100f),
                wheelsHealth = whH.coerceIn(0f, 100f),
                brakesHealth = brkH.coerceIn(0f, 100f)
            )
        }

        return GameState(
            silverCoins = silver,
            goldCoins = gold,
            blueprints = blueprints,
            diamonds = diamonds,
            coolantUnits = coolant,
            sandUnits = sand,
            driverLevel = level,
            driverXp = xp,
            selectedTrainId = selectedTrain,
            unlockedTrainIds = unlockedTrains,
            redeemedCodes = redeemed,
            completedContractIds = completed,
            gearboxLevels = gbMap,
            generatorLevels = genMap,
            engineLevels = engMap,
            wheelsLevels = whMap,
            locomotiveHealthMap = healthMap,
            customBodyColors = bodyColorMap,
            customStripeColors = stripeColorMap,
            selectedLiveryVariants = liveryMap,
            unlockedLiveryVariants = unlockedLiveries,
            selectedMusicTrackId = musicTrack,
            isMusicPlaying = musicEnabled,
            activeRepairTrainId = prefs.getString("active_repair_train_id", null),
            repairEndTimestamp = prefs.getLong("repair_end_timestamp", 0L),
            repairDurationSeconds = prefs.getInt("repair_duration_seconds", 90)
        )
    }

    private val database: AppDatabase = AppDatabase.getInstance(context)
    private val playerDao: PlayerDao = database.playerDao()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            try {
                val roomState = playerDao.getPlayerState()
                if (roomState != null) {
                    val unlockedIds = try {
                        roomState.unlockedTrainIdsJson.split(",").filter { it.isNotBlank() }.toSet()
                    } catch (_: Exception) { emptySet() }
                    val completedIds = try {
                        roomState.completedContractsJson.split(",").filter { it.isNotBlank() }.toSet()
                    } catch (_: Exception) { emptySet() }

                    val trainUpgrades = playerDao.getAllTrainUpgrades()
                    val cur = _gameState.value
                    val gbMap = cur.gearboxLevels.toMutableMap()
                    val genMap = cur.generatorLevels.toMutableMap()
                    val engMap = cur.engineLevels.toMutableMap()
                    val whMap = cur.wheelsLevels.toMutableMap()
                    val bodyColorMap = cur.customBodyColors.toMutableMap()
                    val stripeColorMap = cur.customStripeColors.toMutableMap()
                    val liveryMap = cur.selectedLiveryVariants.toMutableMap()

                    trainUpgrades.forEach { u ->
                        gbMap[u.trainId] = u.gearboxLevel
                        genMap[u.trainId] = u.generatorLevel
                        engMap[u.trainId] = u.engineLevel
                        whMap[u.trainId] = u.wheelsLevel
                        if (u.customBodyColor != 0L) bodyColorMap[u.trainId] = u.customBodyColor
                        if (u.customStripeColor != 0L) stripeColorMap[u.trainId] = u.customStripeColor
                        if (u.activeLiveryId.isNotBlank()) liveryMap[u.trainId] = u.activeLiveryId
                    }

                    _gameState.value = cur.copy(
                        silverCoins = roomState.silverCoins,
                        goldCoins = roomState.goldCoins,
                        blueprints = roomState.blueprints,
                        diamonds = roomState.diamonds,
                        driverLevel = roomState.driverLevel,
                        driverXp = roomState.driverXp,
                        selectedTrainId = roomState.currentTrainId,
                        unlockedTrainIds = if (unlockedIds.isNotEmpty()) unlockedIds else cur.unlockedTrainIds,
                        completedContractIds = if (completedIds.isNotEmpty()) completedIds else cur.completedContractIds,
                        gearboxLevels = gbMap,
                        generatorLevels = genMap,
                        engineLevels = engMap,
                        wheelsLevels = whMap,
                        customBodyColors = bodyColorMap,
                        customStripeColors = stripeColorMap,
                        selectedLiveryVariants = liveryMap
                    )
                } else {
                    persistCurrentStateToRoom()
                }
            } catch (_: Exception) {}
        }
    }

    fun persistCurrentStateToRoom() {
        val state = _gameState.value
        scope.launch {
            try {
                val entity = PlayerStateEntity(
                    id = 1,
                    currentTrainId = state.selectedTrainId,
                    silverCoins = state.silverCoins,
                    goldCoins = state.goldCoins,
                    blueprints = state.blueprints,
                    diamonds = state.diamonds,
                    driverLevel = state.driverLevel,
                    driverXp = state.driverXp,
                    completedContractsJson = state.completedContractIds.joinToString(","),
                    unlockedTrainIdsJson = state.unlockedTrainIds.joinToString(","),
                    lastSavedTimestamp = System.currentTimeMillis()
                )
                playerDao.savePlayerState(entity)

                state.gearboxLevels.keys.forEach { trainId ->
                    val gb = state.gearboxLevels[trainId] ?: 1
                    val gen = state.generatorLevels[trainId] ?: 1
                    val eng = state.engineLevels[trainId] ?: 1
                    val wh = state.wheelsLevels[trainId] ?: 1
                    val bodyColor = state.customBodyColors[trainId] ?: 0L
                    val stripeColor = state.customStripeColors[trainId] ?: 0L
                    val livery = state.selectedLiveryVariants[trainId] ?: "V1_VIRGIN"
                    playerDao.saveTrainUpgrade(
                        TrainUpgradeEntity(
                            trainId = trainId,
                            gearboxLevel = gb,
                            generatorLevel = gen,
                            engineLevel = eng,
                            wheelsLevel = wh,
                            customBodyColor = bodyColor,
                            customStripeColor = stripeColor,
                            activeLiveryId = livery
                        )
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun setLiveryVariant(trainId: String, variantId: String, costDiamonds: Int = 0): Boolean {
        val current = _gameState.value
        val isAlreadyUnlocked = current.unlockedLiveryVariants.contains(variantId) || costDiamonds == 0

        if (!isAlreadyUnlocked && current.diamonds < costDiamonds) {
            return false
        }

        val newDiamonds = if (!isAlreadyUnlocked) current.diamonds - costDiamonds else current.diamonds
        val newUnlocked = current.unlockedLiveryVariants + variantId
        val newSelected = current.selectedLiveryVariants + (trainId to variantId)

        val train = GameContent.ALL_TRAINS.find { it.id == trainId }
        val variant = train?.liveryVariants?.find { it.id == variantId }
        val newBodyColors = if (variant != null) {
            current.customBodyColors + (trainId to variant.primaryColor)
        } else current.customBodyColors
        val newStripeColors = if (variant != null) {
            current.customStripeColors + (trainId to variant.accentColor)
        } else current.customStripeColors

        val editor = prefs.edit()
            .putString("livery_variant_$trainId", variantId)
            .putStringSet("unlocked_livery_variants", newUnlocked)
            .putInt("diamonds", newDiamonds)

        if (variant != null) {
            editor.putLong("paint_body_$trainId", variant.primaryColor)
            editor.putLong("paint_stripe_$trainId", variant.accentColor)
        }
        editor.apply()

        _gameState.value = current.copy(
            diamonds = newDiamonds,
            unlockedLiveryVariants = newUnlocked,
            selectedLiveryVariants = newSelected,
            customBodyColors = newBodyColors,
            customStripeColors = newStripeColors
        )
        return true
    }

    fun selectMusicTrack(trackId: String) {
        prefs.edit().putString("selected_music_track", trackId).apply()
        _gameState.value = _gameState.value.copy(selectedMusicTrackId = trackId)
    }

    fun toggleMusic(enabled: Boolean) {
        prefs.edit().putBoolean("is_music_playing", enabled).apply()
        _gameState.value = _gameState.value.copy(isMusicPlaying = enabled)
    }

    fun selectTrain(trainId: String) {
        prefs.edit().putString("selected_train", trainId).apply()
        _gameState.value = _gameState.value.copy(selectedTrainId = trainId)
        persistCurrentStateToRoom()
    }

    fun unlockTrain(trainId: String, costGold: Int = 0, costDiamonds: Int = 0) {
        val current = _gameState.value
        if (current.goldCoins < costGold || current.diamonds < costDiamonds) return

        val newUnlocked = current.unlockedTrainIds + trainId
        val newGold = current.goldCoins - costGold
        val newDiamonds = current.diamonds - costDiamonds

        prefs.edit()
            .putStringSet("unlocked_trains", newUnlocked)
            .putInt("gold_coins", newGold)
            .putInt("diamonds", newDiamonds)
            .putString("selected_train", trainId)
            .apply()

        _gameState.value = current.copy(
            unlockedTrainIds = newUnlocked,
            goldCoins = newGold,
            diamonds = newDiamonds,
            selectedTrainId = trainId
        )
        persistCurrentStateToRoom()
    }

    fun upgradeModule(trainId: String, moduleIndex: Int): Boolean {
        val current = _gameState.value
        val train = GameContent.ALL_TRAINS.find { it.id == trainId } ?: GameContent.ALL_TRAINS.first()
        val maxLevel = train.maxUpgradeLevel

        val (currentLevel, cost) = when (moduleIndex) {
            0 -> (current.gearboxLevels[trainId] ?: 1) to getUpgradeCost(current.gearboxLevels[trainId] ?: 1)
            1 -> (current.generatorLevels[trainId] ?: 1) to getUpgradeCost(current.generatorLevels[trainId] ?: 1)
            2 -> (current.engineLevels[trainId] ?: 1) to getUpgradeCost(current.engineLevels[trainId] ?: 1)
            3 -> (current.wheelsLevels[trainId] ?: 1) to getUpgradeCost(current.wheelsLevels[trainId] ?: 1)
            else -> return false
        }

        if (currentLevel >= maxLevel || current.silverCoins < cost) {
            return false
        }

        val newSilver = current.silverCoins - cost
        val newGb = current.gearboxLevels.toMutableMap()
        val newGen = current.generatorLevels.toMutableMap()
        val newEng = current.engineLevels.toMutableMap()
        val newWh = current.wheelsLevels.toMutableMap()

        when (moduleIndex) {
            0 -> {
                newGb[trainId] = currentLevel + 1
                prefs.edit().putInt("upg_gb_$trainId", currentLevel + 1).apply()
            }
            1 -> {
                newGen[trainId] = currentLevel + 1
                prefs.edit().putInt("upg_gen_$trainId", currentLevel + 1).apply()
            }
            2 -> {
                newEng[trainId] = currentLevel + 1
                prefs.edit().putInt("upg_eng_$trainId", currentLevel + 1).apply()
            }
            3 -> {
                newWh[trainId] = currentLevel + 1
                prefs.edit().putInt("upg_wh_$trainId", currentLevel + 1).apply()
            }
        }

        prefs.edit().putInt("silver_coins", newSilver).apply()

        _gameState.value = current.copy(
            silverCoins = newSilver,
            gearboxLevels = newGb,
            generatorLevels = newGen,
            engineLevels = newEng,
            wheelsLevels = newWh
        )
        persistCurrentStateToRoom()
        return true
    }

    fun getUpgradeCost(level: Int): Int {
        return (50 + (level * 15) + (level * level * 2)).coerceAtLeast(55)
    }

    fun getOverallTrainLevel(trainId: String): Int {
        val train = GameContent.ALL_TRAINS.find { it.id == trainId } ?: GameContent.ALL_TRAINS.first()
        val maxLevel = train.maxUpgradeLevel
        val gb = _gameState.value.gearboxLevels[trainId] ?: 1
        val gen = _gameState.value.generatorLevels[trainId] ?: 1
        val eng = _gameState.value.engineLevels[trainId] ?: 1
        val wh = _gameState.value.wheelsLevels[trainId] ?: 1
        return ((gb + gen + eng + wh) / 4).coerceIn(1, maxLevel)
    }

    fun applyCustomPaint(trainId: String, bodyColor: Long, stripeColor: Long) {
        val current = _gameState.value
        val newBodyMap = current.customBodyColors + (trainId to bodyColor)
        val newStripeMap = current.customStripeColors + (trainId to stripeColor)

        prefs.edit()
            .putLong("paint_body_$trainId", bodyColor)
            .putLong("paint_stripe_$trainId", stripeColor)
            .apply()

        _gameState.value = current.copy(
            customBodyColors = newBodyMap,
            customStripeColors = newStripeMap
        )
        persistCurrentStateToRoom()
    }

    fun setCustomColors(trainId: String, bodyColor: Long, stripeColor: Long) {
        applyCustomPaint(trainId, bodyColor, stripeColor)
    }

    fun setLiveryVariant(trainId: String, variantId: String) {
        val current = _gameState.value
        val updated = current.selectedLiveryVariants + (trainId to variantId)
        prefs.edit().putString("livery_variant_$trainId", variantId).apply()
        _gameState.value = current.copy(selectedLiveryVariants = updated)
    }

    fun unlockLiveryVariant(trainId: String, variantId: String, costDiamonds: Int): Boolean {
        val current = _gameState.value
        val key = "${trainId}_${variantId}"
        if (current.unlockedLiveryVariants.contains(key) || current.unlockedLiveryVariants.contains(variantId)) {
            setLiveryVariant(trainId, variantId)
            return true
        }
        if (current.diamonds < costDiamonds) return false

        val newDiamonds = current.diamonds - costDiamonds
        val newUnlocked = current.unlockedLiveryVariants + key + variantId
        val updatedVariants = current.selectedLiveryVariants + (trainId to variantId)

        prefs.edit()
            .putInt("diamonds", newDiamonds)
            .putStringSet("unlocked_livery_variants", newUnlocked)
            .putString("livery_variant_$trainId", variantId)
            .apply()

        _gameState.value = current.copy(
            diamonds = newDiamonds,
            unlockedLiveryVariants = newUnlocked,
            selectedLiveryVariants = updatedVariants
        )
        return true
    }

    fun redeemPromoCode(codeUpper: String): String? {
        val current = _gameState.value
        val cleanInput = codeUpper.trim().replace(" ", "")

        val promo = GameContent.PROMO_CODES.find {
            it.code.trim().replace(" ", "").equals(cleanInput, ignoreCase = true)
        } ?: return "Invalid Promo Code! Check spelling."

        if (current.redeemedCodes.contains(promo.code) || current.redeemedCodes.contains(codeUpper.trim())) {
            return "Code has already been redeemed!"
        }

        val newRedeemed = current.redeemedCodes + promo.code + codeUpper.trim()
        val newSilver = current.silverCoins + promo.silverReward
        val newGold = current.goldCoins + promo.goldReward
        val newDiamonds = current.diamonds + promo.diamondReward
        val newBlueprints = current.blueprints + promo.blueprintReward
        val newCoolant = current.coolantUnits + promo.coolantReward
        val newSand = current.sandUnits + promo.sandReward

        var newUnlocked = current.unlockedTrainIds
        var newUnlockedVariants = current.unlockedLiveryVariants
        var selected = current.selectedTrainId
        val newGbMap = current.gearboxLevels.toMutableMap()
        val newGenMap = current.generatorLevels.toMutableMap()
        val newEngMap = current.engineLevels.toMutableMap()
        val newWhMap = current.wheelsLevels.toMutableMap()

        val editor = prefs.edit()
            .putStringSet("redeemed_codes", newRedeemed)
            .putInt("silver_coins", newSilver)
            .putInt("gold_coins", newGold)
            .putInt("diamonds", newDiamonds)
            .putInt("blueprints", newBlueprints)
            .putInt("coolant_units", newCoolant)
            .putInt("sand_units", newSand)

        val trainIdsToUnlock = mutableListOf<String>()
        promo.secretTrainId?.let { trainIdsToUnlock.add(it) }
        trainIdsToUnlock.addAll(promo.secretTrainIds)

        for (secretId in trainIdsToUnlock) {
            newUnlocked = newUnlocked + secretId
            selected = secretId
            val secretTrain = GameContent.ALL_TRAINS.find { it.id == secretId }
            val maxLvl = secretTrain?.maxUpgradeLevel ?: 50
            newGbMap[secretId] = maxLvl
            newGenMap[secretId] = maxLvl
            newEngMap[secretId] = maxLvl
            newWhMap[secretId] = maxLvl
            editor.putInt("upg_gb_$secretId", maxLvl)
            editor.putInt("upg_gen_$secretId", maxLvl)
            editor.putInt("upg_eng_$secretId", maxLvl)
            editor.putInt("upg_wh_$secretId", maxLvl)

            // Unlock all livery variants for secret/unlocked trains
            secretTrain?.liveryVariants?.forEach { variant ->
                newUnlockedVariants = newUnlockedVariants + variant.id + "${secretId}_${variant.id}"
                editor.putBoolean("livery_unlocked_${secretId}_${variant.id}", true)
            }
        }

        editor.putStringSet("unlocked_trains", newUnlocked)
            .putStringSet("unlocked_livery_variants", newUnlockedVariants)
            .putString("selected_train", selected)

        promo.skinColor?.let { colorVal ->
            editor.putLong("paint_body_${current.selectedTrainId}", colorVal)
        }
        editor.apply()

        _gameState.value = current.copy(
            silverCoins = newSilver,
            goldCoins = newGold,
            diamonds = newDiamonds,
            blueprints = newBlueprints,
            coolantUnits = newCoolant,
            sandUnits = newSand,
            redeemedCodes = newRedeemed,
            unlockedTrainIds = newUnlocked,
            unlockedLiveryVariants = newUnlockedVariants,
            selectedTrainId = selected,
            gearboxLevels = newGbMap,
            generatorLevels = newGenMap,
            engineLevels = newEngMap,
            wheelsLevels = newWhMap
        )

        return null // Success!
    }

    fun buyCoinsWithDiamonds(diamondCost: Int, silverAmount: Int = 0, goldAmount: Int = 0): Boolean {
        val current = _gameState.value
        if (current.diamonds < diamondCost) return false

        val newDiamonds = current.diamonds - diamondCost
        val newSilver = current.silverCoins + silverAmount
        val newGold = current.goldCoins + goldAmount

        prefs.edit()
            .putInt("diamonds", newDiamonds)
            .putInt("silver_coins", newSilver)
            .putInt("gold_coins", newGold)
            .apply()

        _gameState.value = current.copy(
            diamonds = newDiamonds,
            silverCoins = newSilver,
            goldCoins = newGold
        )
        persistCurrentStateToRoom()
        return true
    }

    fun addRewards(
        silver: Int = 0,
        gold: Int = 0,
        diamonds: Int = 0,
        xp: Int = 0,
        contractId: String? = null
    ) {
        val current = _gameState.value
        val newSilver = current.silverCoins + silver
        val newGold = current.goldCoins + gold
        val newDiamonds = current.diamonds + diamonds
        val totalXp = current.driverXp + xp
        // Level curve scaling smoothly to Level 20 max
        val newLevel = ((totalXp / 350) + 1).coerceIn(1, 20)
        val newContracts = if (contractId != null) current.completedContractIds + contractId else current.completedContractIds

        prefs.edit()
            .putInt("silver_coins", newSilver)
            .putInt("gold_coins", newGold)
            .putInt("diamonds", newDiamonds)
            .putInt("driver_xp", totalXp)
            .putInt("driver_level", newLevel)
            .putStringSet("completed_contracts", newContracts)
            .apply()

        _gameState.value = current.copy(
            silverCoins = newSilver,
            goldCoins = newGold,
            diamonds = newDiamonds,
            driverXp = totalXp,
            driverLevel = newLevel,
            completedContractIds = newContracts
        )
        persistCurrentStateToRoom()
    }

    fun refillSand(amount: Int = 100, costSilver: Int = 0): Boolean {
        val current = _gameState.value
        if (costSilver > 0 && current.silverCoins < costSilver) return false
        val newSilver = current.silverCoins - costSilver
        val newSand = (current.sandUnits + amount).coerceAtMost(current.maxSandCapacity)
        prefs.edit()
            .putInt("sand_units", newSand)
            .putInt("silver_coins", newSilver)
            .apply()
        _gameState.value = current.copy(
            sandUnits = newSand,
            silverCoins = newSilver
        )
        return true
    }

    fun refillCoolant(amount: Int = 100, costSilver: Int = 0): Boolean {
        val current = _gameState.value
        if (costSilver > 0 && current.silverCoins < costSilver) return false
        val newSilver = current.silverCoins - costSilver
        val newCoolant = (current.coolantUnits + amount).coerceAtMost(current.maxCoolantCapacity)
        prefs.edit()
            .putInt("coolant_units", newCoolant)
            .putInt("silver_coins", newSilver)
            .apply()
        _gameState.value = current.copy(
            coolantUnits = newCoolant,
            silverCoins = newSilver
        )
        return true
    }

    fun refillAllSupplies(costSilver: Int = 0): Boolean {
        val current = _gameState.value
        if (costSilver > 0 && current.silverCoins < costSilver) return false
        val newSilver = current.silverCoins - costSilver
        val newSand = current.maxSandCapacity
        val newCoolant = current.maxCoolantCapacity
        prefs.edit()
            .putInt("sand_units", newSand)
            .putInt("coolant_units", newCoolant)
            .putInt("silver_coins", newSilver)
            .apply()
        _gameState.value = current.copy(
            sandUnits = newSand,
            coolantUnits = newCoolant,
            silverCoins = newSilver
        )
        return true
    }

    fun replenishSupplies(sand: Int = 0, coolant: Int = 0) {
        val current = _gameState.value
        val newSand = current.sandUnits + sand
        val newCoolant = current.coolantUnits + coolant

        prefs.edit()
            .putInt("sand_units", newSand)
            .putInt("coolant_units", newCoolant)
            .apply()

        _gameState.value = current.copy(
            sandUnits = newSand,
            coolantUnits = newCoolant
        )
    }

    fun useCoolant(): Boolean {
        val current = _gameState.value
        if (current.coolantUnits > 0) {
            val newCoolant = current.coolantUnits - 1
            prefs.edit().putInt("coolant_units", newCoolant).apply()
            _gameState.value = current.copy(coolantUnits = newCoolant)
            return true
        }
        return false
    }

    fun useSand(): Boolean {
        val current = _gameState.value
        if (current.sandUnits > 0) {
            val newSand = current.sandUnits - 1
            prefs.edit().putInt("sand_units", newSand).apply()
            _gameState.value = current.copy(sandUnits = newSand)
            return true
        }
        return false
    }

    // =========================================================================
    // REPAIR & MAINTENANCE SYSTEM
    // =========================================================================

    fun getLocomotiveHealth(trainId: String): LocomotiveHealth {
        return _gameState.value.locomotiveHealthMap[trainId] ?: LocomotiveHealth()
    }

    fun getComponentRepairCost(healthValue: Float): Int {
        val wearAmount = (100f - healthValue).coerceAtLeast(0f)
        if (wearAmount < 0.5f) return 0
        return ((wearAmount * 32f) + 40f).toInt()
    }

    fun getOverhaulCost(trainId: String): Int {
        val health = getLocomotiveHealth(trainId)
        val rawSum = getComponentRepairCost(health.engineHealth) +
                getComponentRepairCost(health.gearboxHealth) +
                getComponentRepairCost(health.wheelsHealth) +
                getComponentRepairCost(health.brakesHealth)
        // 15% fleet discount for complete bundled overhaul
        return (rawSum * 0.85f).toInt()
    }

    fun repairComponent(trainId: String, componentIndex: Int): Boolean {
        val current = _gameState.value
        val health = current.locomotiveHealthMap[trainId] ?: LocomotiveHealth()
        val currentComponentHealth = when (componentIndex) {
            0 -> health.engineHealth
            1 -> health.gearboxHealth
            2 -> health.wheelsHealth
            3 -> health.brakesHealth
            else -> return false
        }

        val cost = getComponentRepairCost(currentComponentHealth)
        if (cost <= 0 || current.silverCoins < cost) {
            return false
        }

        val newHealth = when (componentIndex) {
            0 -> health.copy(engineHealth = 100f)
            1 -> health.copy(gearboxHealth = 100f)
            2 -> health.copy(wheelsHealth = 100f)
            3 -> health.copy(brakesHealth = 100f)
            else -> health
        }

        val key = when (componentIndex) {
            0 -> "health_eng_$trainId"
            1 -> "health_gb_$trainId"
            2 -> "health_wh_$trainId"
            3 -> "health_brk_$trainId"
            else -> ""
        }

        val newSilver = current.silverCoins - cost
        prefs.edit()
            .putInt("silver_coins", newSilver)
            .putFloat(key, 100f)
            .apply()

        val updatedMap = current.locomotiveHealthMap.toMutableMap()
        updatedMap[trainId] = newHealth

        _gameState.value = current.copy(
            silverCoins = newSilver,
            locomotiveHealthMap = updatedMap
        )
        return true
    }

    fun overhaulLocomotive(trainId: String): Boolean {
        val current = _gameState.value
        val cost = getOverhaulCost(trainId)
        if (cost <= 0 || current.silverCoins < cost) {
            return false
        }

        val newSilver = current.silverCoins - cost
        prefs.edit()
            .putInt("silver_coins", newSilver)
            .putFloat("health_eng_$trainId", 100f)
            .putFloat("health_gb_$trainId", 100f)
            .putFloat("health_wh_$trainId", 100f)
            .putFloat("health_brk_$trainId", 100f)
            .apply()

        val updatedMap = current.locomotiveHealthMap.toMutableMap()
        updatedMap[trainId] = LocomotiveHealth(100f, 100f, 100f, 100f)

        _gameState.value = current.copy(
            silverCoins = newSilver,
            locomotiveHealthMap = updatedMap
        )
        return true
    }

    fun overhaulWithDiamonds(trainId: String, diamondCost: Int = 8): Boolean {
        val current = _gameState.value
        if (current.diamonds < diamondCost) return false

        val newDiamonds = current.diamonds - diamondCost
        prefs.edit()
            .putInt("diamonds", newDiamonds)
            .putFloat("health_eng_$trainId", 100f)
            .putFloat("health_gb_$trainId", 100f)
            .putFloat("health_wh_$trainId", 100f)
            .putFloat("health_brk_$trainId", 100f)
            .apply()

        val updatedMap = current.locomotiveHealthMap.toMutableMap()
        updatedMap[trainId] = LocomotiveHealth(100f, 100f, 100f, 100f)

        // Also clear any running timer if this was the active repair train
        val clearsActive = current.activeRepairTrainId == trainId
        if (clearsActive) {
            prefs.edit()
                .remove("active_repair_train_id")
                .remove("repair_end_timestamp")
                .apply()
        }

        _gameState.value = current.copy(
            diamonds = newDiamonds,
            locomotiveHealthMap = updatedMap,
            activeRepairTrainId = if (clearsActive) null else current.activeRepairTrainId,
            repairEndTimestamp = if (clearsActive) 0L else current.repairEndTimestamp
        )
        return true
    }

    /**
     * Starts 1-2 minutes timed repair (costs silver coins + 70 blueprints as requested).
     * Duration defaults to 90 seconds (1.5 minutes, squarely in 1-2 min range).
     */
    fun startTimedRepair(trainId: String, durationSeconds: Int = 90, blueprintCost: Int = 70): Boolean {
        val current = _gameState.value
        val costSilver = getOverhaulCost(trainId)
        if (costSilver <= 0 || current.silverCoins < costSilver || current.blueprints < blueprintCost) {
            return false
        }

        val newSilver = current.silverCoins - costSilver
        val newBlueprints = current.blueprints - blueprintCost
        val endTimestamp = System.currentTimeMillis() + (durationSeconds * 1000L)

        prefs.edit()
            .putInt("silver_coins", newSilver)
            .putInt("blueprints", newBlueprints)
            .putString("active_repair_train_id", trainId)
            .putLong("repair_end_timestamp", endTimestamp)
            .putInt("repair_duration_seconds", durationSeconds)
            .apply()

        _gameState.value = current.copy(
            silverCoins = newSilver,
            blueprints = newBlueprints,
            activeRepairTrainId = trainId,
            repairEndTimestamp = endTimestamp,
            repairDurationSeconds = durationSeconds
        )
        return true
    }

    /**
     * Instantly finishes active repair or restores locomotive to 100% using diamonds.
     */
    fun fastRepairWithDiamonds(trainId: String, diamondCost: Int = 8): Boolean {
        return overhaulWithDiamonds(trainId, diamondCost)
    }

    /**
     * Completes timed repair if timer has elapsed.
     */
    fun checkAndCompleteTimedRepair(): Boolean {
        val current = _gameState.value
        val repairTrainId = current.activeRepairTrainId ?: return false
        val endTime = current.repairEndTimestamp
        if (System.currentTimeMillis() < endTime) {
            return false
        }

        // Timer elapsed! Restore train to 100% health
        prefs.edit()
            .putFloat("health_eng_$repairTrainId", 100f)
            .putFloat("health_gb_$repairTrainId", 100f)
            .putFloat("health_wh_$repairTrainId", 100f)
            .putFloat("health_brk_$repairTrainId", 100f)
            .remove("active_repair_train_id")
            .remove("repair_end_timestamp")
            .apply()

        val updatedMap = current.locomotiveHealthMap.toMutableMap()
        updatedMap[repairTrainId] = LocomotiveHealth(100f, 100f, 100f, 100f)

        _gameState.value = current.copy(
            locomotiveHealthMap = updatedMap,
            activeRepairTrainId = null,
            repairEndTimestamp = 0L
        )
        return true
    }

    fun applyTripWear(
        trainId: String,
        engineWear: Float,
        gearboxWear: Float,
        wheelsWear: Float,
        brakesWear: Float
    ) {
        val current = _gameState.value
        val existing = current.locomotiveHealthMap[trainId] ?: LocomotiveHealth()

        val newEng = (existing.engineHealth - engineWear).coerceIn(0f, 100f)
        val newGb = (existing.gearboxHealth - gearboxWear).coerceIn(0f, 100f)
        val newWh = (existing.wheelsHealth - wheelsWear).coerceIn(0f, 100f)
        val newBrk = (existing.brakesHealth - brakesWear).coerceIn(0f, 100f)

        val updatedHealth = LocomotiveHealth(newEng, newGb, newWh, newBrk)

        prefs.edit()
            .putFloat("health_eng_$trainId", newEng)
            .putFloat("health_gb_$trainId", newGb)
            .putFloat("health_wh_$trainId", newWh)
            .putFloat("health_brk_$trainId", newBrk)
            .apply()

        val updatedMap = current.locomotiveHealthMap.toMutableMap()
        updatedMap[trainId] = updatedHealth

        _gameState.value = current.copy(
            locomotiveHealthMap = updatedMap
        )
    }
}
