package com.lux.light.meter.luminosity.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PaywallViewModel2 : ViewModel() {
    val booleanLiveData = MutableLiveData<Boolean>()

    fun setBooleanValue(newValue: Boolean) {
        booleanLiveData.value = newValue
    }
}