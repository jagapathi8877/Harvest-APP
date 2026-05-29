package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KothaKhataDao {
    // Machine
    @Query("SELECT * FROM machines")
    fun getAllMachines(): Flow<List<Machine>>

    @Query("SELECT * FROM machines WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultMachine(): Machine?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMachine(machine: Machine): Long

    @Delete
    suspend fun deleteMachine(machine: Machine)

    // Season
    @Query("SELECT * FROM seasons")
    fun getAllSeasons(): Flow<List<Season>>

    @Query("SELECT * FROM seasons WHERE isActive = 1 LIMIT 1")
    fun getActiveSeasonFlow(): Flow<Season?>
    
    @Query("SELECT * FROM seasons WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveSeason(): Season?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: Season)
    
    @Query("UPDATE seasons SET isActive = 0")
    suspend fun deactivateAllSeasons()

    @Delete
    suspend fun deleteSeason(season: Season)

    // WorkRecord
    @Query("SELECT * FROM work_records ORDER BY dateMillis DESC")
    fun getAllRecords(): Flow<List<WorkRecord>>
    
    @Query("SELECT * FROM work_records WHERE paymentStatus != 'PAID' ORDER BY dateMillis DESC")
    fun getPendingRecords(): Flow<List<WorkRecord>>

    @Query("SELECT * FROM work_records WHERE dateMillis BETWEEN :startOfDay AND :endOfDay ORDER BY dateMillis DESC")
    fun getRecordsForDay(startOfDay: Long, endOfDay: Long): Flow<List<WorkRecord>>

    @Query("SELECT * FROM work_records WHERE id = :id")
    suspend fun getRecordById(id: Int): WorkRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: WorkRecord)

    @Update
    suspend fun updateRecord(record: WorkRecord)

    @Query("DELETE FROM work_records WHERE id = :id")
    suspend fun deleteRecordById(id: Int)
}
