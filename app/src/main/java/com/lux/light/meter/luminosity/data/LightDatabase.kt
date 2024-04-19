package com.lux.light.meter.luminosity.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LightData::class], version = 1, exportSchema = false)
abstract class LightDatabase : RoomDatabase() {
    abstract fun lightDataDao(): LightDataDao

    companion object {
        @Volatile
        private var INSTANCE: LightDatabase? = null

        fun getDatabase(context: Context): LightDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LightDatabase::class.java,
                    "light_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
