package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.KothaKhataRepository

class KothaKhataApp : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { KothaKhataRepository(database.dao()) }
}
