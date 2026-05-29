package com.example.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        // Dummy implementation of background sync with MongoDB Atlas.
        // In a real app, this would use Retrofit to send unsynced WorkRecords to a Node.js/Express backend.
        delay(2000)
        return Result.success()
    }
}
