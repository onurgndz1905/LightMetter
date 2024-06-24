package com.lux.light.meter.luminosity.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

@Database(entities = [LightData::class], version = 2, exportSchema = false)
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
                ).addMigrations(MIGRATION_1_2) // Migration'ı buraya ekle
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Geçiş nesnesi oluştur
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Burada 1'den 2'ye geçiş için gerekli SQL sorgularını yazmalısınız
                // Örneğin, yeni bir tablo eklemek, varolan bir tabloya sütun eklemek vb.
                database.execSQL("ALTER TABLE light_data ADD COLUMN new_column_name FLOAT DEFAULT 0.0 NOT NULL")

            }
        }
    }
}
