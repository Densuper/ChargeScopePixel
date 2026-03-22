package com.chargescopixel.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ChargingSessionEntity::class, BatterySampleEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chargingSessionDao(): ChargingSessionDao
    abstract fun batterySampleDao(): BatterySampleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "charge_scope_pixel.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
