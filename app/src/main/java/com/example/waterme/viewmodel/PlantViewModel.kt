package com.example.waterme.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.waterme.data.DataSource
import com.example.waterme.worker.WaterReminderWorker
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class PlantViewModel(application: Application): ViewModel() {

    val plants = DataSource.plants

    val workManager = WorkManager.getInstance(application)

    internal fun scheduleReminder(
        duration: Long,
        unit: TimeUnit,
        plantName: String
    ) {
        val data = Data.Builder()
            .putString(WaterReminderWorker.nameKey, plantName)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<WaterReminderWorker>()
            .setInitialDelay(duration, unit)
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            plantName,
            androidx.work.ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}

class PlantViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            PlantViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
