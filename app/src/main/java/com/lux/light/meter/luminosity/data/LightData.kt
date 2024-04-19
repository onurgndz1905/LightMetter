package com.lux.light.meter.luminosity.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable

@Entity(tableName = "light_data")
data class LightData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val minLightValue: Float,
    val maxLightValue: Float,
    @ColumnInfo(defaultValue = "0.0") // Örnek olarak, 0.0 değeri verildi
    val avgLightValue: Float,
    val timestamp: Long,
    val recordingDate: String
) : Serializable
