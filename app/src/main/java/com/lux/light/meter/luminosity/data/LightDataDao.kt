package com.lux.light.meter.luminosity.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LightDataDao {
    @Insert
    suspend fun insert(lightData: LightData)

    @Delete
    suspend fun delete(lightData: LightData)

    @Query("SELECT * FROM light_data")
    fun getAllLightDataDao(): LiveData<List<LightData>>

    @Query("DELETE FROM light_data")
    suspend fun deleteAll()
}
