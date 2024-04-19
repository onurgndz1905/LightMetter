package com.lux.light.meter.luminosity.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SensorDataViewModel : ViewModel() {
    private val _sensorData = MutableLiveData<Float>()
    val sensorData: LiveData<Float>
        get() = _sensorData

    fun updateSensorData(data: Float) {
        _sensorData.value = data
    }

}
