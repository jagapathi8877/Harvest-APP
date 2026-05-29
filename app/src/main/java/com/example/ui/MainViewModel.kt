package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(
    val repository: KothaKhataRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            val season = repository.activeSeason.firstOrNull()
            if (season == null) {
                val defaultSeason = Season(name = "Default Season", hourlyRate = 2600, startDate = System.currentTimeMillis())
                repository.addSeason(defaultSeason)
            }
            val defaultMachine = repository.allMachines.firstOrNull()
            if (defaultMachine.isNullOrEmpty()) {
                val machine = Machine(name = "Default Machine", isDefault = true)
                repository.addMachine(machine)
            }
        }
    }

    val allMachines = repository.allMachines.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val activeSeason = repository.activeSeason.stateIn(viewModelScope, SharingStarted.Lazily, null)
    
    val allRecords = repository.allRecords.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val pendingRecords = repository.pendingRecords.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recordsForToday = repository.getRecordsForDay(getStartOfToday(), getEndOfToday())
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getStartOfToday(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun getEndOfToday(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }

    fun saveRecord(record: WorkRecord) {
        viewModelScope.launch {
            repository.insertRecord(record)
        }
    }

    fun updateRecord(record: WorkRecord) {
        viewModelScope.launch {
            repository.updateRecord(record)
        }
    }

    fun deleteRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteRecord(id)
        }
    }

    fun addMachine(name: String) {
        viewModelScope.launch {
            val machines = repository.allMachines.firstOrNull() ?: emptyList()
            repository.addMachine(Machine(name = name, isDefault = machines.isEmpty()))
        }
    }

    fun updateMachine(machine: Machine) {
        viewModelScope.launch {
            repository.addMachine(machine)
        }
    }

    fun deleteMachine(machine: Machine) {
        viewModelScope.launch {
            repository.deleteMachine(machine)
        }
    }

    fun updateSeasonRate(newRate: Int) {
        viewModelScope.launch {
            val current = repository.activeSeason.firstOrNull()
            if (current != null) {
                repository.addSeason(current.copy(hourlyRate = newRate))
            } else {
                val newSeason = Season(name = "Default Season", hourlyRate = newRate, startDate = System.currentTimeMillis())
                repository.addSeason(newSeason)
            }
        }
    }

    class Factory(
        private val repository: KothaKhataRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
