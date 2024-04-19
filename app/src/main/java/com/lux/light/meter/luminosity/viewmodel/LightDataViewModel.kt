package com.lux.light.meter.luminosity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.lux.light.meter.luminosity.data.LightData
import com.lux.light.meter.luminosity.data.LightDataDao
import com.lux.light.meter.luminosity.data.LightDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LightDataViewModel(application: Application) : AndroidViewModel(application) {
    private val lightDataDao: LightDataDao
    val allLightData: LiveData<List<LightData>>

    init {
        val database = LightDatabase.getDatabase(application)
        lightDataDao = database.lightDataDao()
        allLightData = lightDataDao.getAllLightDataDao()
    }

    fun insert(lightData: LightData) {
        viewModelScope.launch(Dispatchers.IO) {
            lightDataDao.insert(lightData)
        }
    }

    fun delete(lightData: LightData) {
        viewModelScope.launch(Dispatchers.IO) {
            lightDataDao.delete(lightData)
        }
    }
    fun deleteAllLightData() {
        viewModelScope.launch(Dispatchers.IO) {
            lightDataDao.deleteAll()
        }
    }


    fun getAllLightDataLiveData(): LiveData<List<LightData>> {
        return allLightData
    }

}
