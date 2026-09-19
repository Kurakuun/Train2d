package com.example.data.db

import kotlinx.coroutines.flow.Flow

class PlayerRepository(private val playerDao: PlayerDao) {
    val playerStateFlow: Flow<PlayerStateEntity?> = playerDao.getPlayerStateFlow()

    suspend fun getPlayerState(): PlayerStateEntity? = playerDao.getPlayerState()

    suspend fun savePlayerState(state: PlayerStateEntity) = playerDao.savePlayerState(state)

    suspend fun getAllTrainUpgrades(): List<TrainUpgradeEntity> = playerDao.getAllTrainUpgrades()

    suspend fun getTrainUpgrade(trainId: String): TrainUpgradeEntity? = playerDao.getTrainUpgrade(trainId)

    suspend fun saveTrainUpgrade(upgrade: TrainUpgradeEntity) = playerDao.saveTrainUpgrade(upgrade)

    suspend fun getAllCompletedContracts(): List<CompletedContractRecord> = playerDao.getAllCompletedContracts()

    suspend fun recordCompletedContract(record: CompletedContractRecord) = playerDao.insertCompletedContract(record)
}
