package com.lux.light.meter.luminosity.`object`

import android.content.Context
import android.content.SharedPreferences

object AutomaticRecording {
    var automatic_recording = true
    private const val PREF_AUTOMATIC_RECORDING = "pref_automatic_recording"
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("recording", Context.MODE_PRIVATE)
        automatic_recording = sharedPreferences.getBoolean(PREF_AUTOMATIC_RECORDING, true)
    }

    fun saveAutomaticRecordingState() {
        sharedPreferences.edit().putBoolean(PREF_AUTOMATIC_RECORDING, automatic_recording).apply()
    }
}
