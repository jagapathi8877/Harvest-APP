package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "machines")
data class Machine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isDefault: Boolean = false
)

@Entity(tableName = "seasons")
data class Season(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val hourlyRate: Int,
    val startDate: Long,
    val isActive: Boolean = false
)

enum class PaymentStatus { PAID, PARTIAL, UNPAID }

@Entity(tableName = "work_records")
data class WorkRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val phoneNumber: String,
    val village: String,
    val dateMillis: Long,
    val machineId: Int,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val breakMinutes: Int,
    val runtimeMinutes: Int,
    val hourlyRate: Int,
    val totalAmount: Int,
    val paymentStatus: PaymentStatus,
    val paidAmount: Int,
    val pendingAmount: Int,
    val notes: String,
    val isSynced: Boolean = false
)
