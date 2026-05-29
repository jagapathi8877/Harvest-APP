package com.example.data

import kotlinx.coroutines.flow.Flow

class KothaKhataRepository(private val dao: KothaKhataDao) {
    val allMachines: Flow<List<Machine>> = dao.getAllMachines()
    val allSeasons: Flow<List<Season>> = dao.getAllSeasons()
    val activeSeason: Flow<Season?> = dao.getActiveSeasonFlow()
    val allRecords: Flow<List<WorkRecord>> = dao.getAllRecords()
    val pendingRecords: Flow<List<WorkRecord>> = dao.getPendingRecords()

    fun getRecordsForDay(start: Long, end: Long) = dao.getRecordsForDay(start, end)
    
    suspend fun getRecordById(id: Int) = dao.getRecordById(id)
    suspend fun insertRecord(record: WorkRecord) = dao.insertRecord(record)
    suspend fun updateRecord(record: WorkRecord) = dao.updateRecord(record)
    suspend fun deleteRecord(id: Int) = dao.deleteRecordById(id)

    suspend fun addMachine(machine: Machine) = dao.insertMachine(machine)
    suspend fun deleteMachine(machine: Machine) = dao.deleteMachine(machine)

    suspend fun addSeason(season: Season) {
        dao.deactivateAllSeasons()
        dao.insertSeason(season.copy(isActive = true))
    }
}
