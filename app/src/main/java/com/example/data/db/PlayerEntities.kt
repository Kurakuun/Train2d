package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_state")
data class PlayerStateEntity(
    @PrimaryKey val id: Int = 1,
    val currentTrainId: String,
    val silverCoins: Int,
    val goldCoins: Int,
    val blueprints: Int,
    val diamonds: Int,
    val driverLevel: Int,
    val driverXp: Int,
    val completedContractsJson: String,
    val unlockedTrainIdsJson: String,
    val lastSavedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "train_upgrades")
data class TrainUpgradeEntity(
    @PrimaryKey val trainId: String,
    val gearboxLevel: Int,
    val generatorLevel: Int,
    val engineLevel: Int,
    val wheelsLevel: Int,
    val customBodyColor: Long,
    val customStripeColor: Long,
    val activeLiveryId: String
)

@Entity(tableName = "completed_contract_records")
data class CompletedContractRecord(
    @PrimaryKey val contractId: String,
    val title: String,
    val payoutSilver: Int,
    val payoutGold: Int,
    val completedTimestamp: Long = System.currentTimeMillis()
)
