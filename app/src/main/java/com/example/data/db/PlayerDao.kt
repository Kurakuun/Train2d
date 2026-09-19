package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM player_state WHERE id = 1")
    fun getPlayerStateFlow(): Flow<PlayerStateEntity?>

    @Query("SELECT * FROM player_state WHERE id = 1")
    suspend fun getPlayerState(): PlayerStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayerState(state: PlayerStateEntity)

    @Query("SELECT * FROM train_upgrades")
    suspend fun getAllTrainUpgrades(): List<TrainUpgradeEntity>

    @Query("SELECT * FROM train_upgrades WHERE trainId = :trainId")
    suspend fun getTrainUpgrade(trainId: String): TrainUpgradeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrainUpgrade(upgrade: TrainUpgradeEntity)

    @Query("SELECT * FROM completed_contract_records ORDER BY completedTimestamp DESC")
    suspend fun getAllCompletedContracts(): List<CompletedContractRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedContract(record: CompletedContractRecord)
}
