package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.preferences.UserPreferencesRepository
import com.example.domain.repository.StepRepository
import com.example.sensor.StepSensorManager

class StepViewModelFactory(
    private val repository: StepRepository,
    private val sensorManager: StepSensorManager,
    private val userPreferencesRepository: UserPreferencesRepository? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepViewModel::class.java)) {
            return StepViewModel(repository, sensorManager, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}
