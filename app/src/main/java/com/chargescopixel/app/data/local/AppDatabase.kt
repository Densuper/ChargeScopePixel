package com.chargescopixel.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ChargingSessionEntity::class, BatterySampleEntity::class],
    version = 4,
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
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                ensureBatterySampleColumns(database)
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                ensureBatterySampleColumns(database)
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                ensureBatterySampleColumns(database)
            }
        }

        private fun ensureBatterySampleColumns(database: SupportSQLiteDatabase) {
            if (!hasColumn(database, "battery_samples", "health")) {
                database.execSQL("ALTER TABLE battery_samples ADD COLUMN health TEXT NOT NULL DEFAULT 'Unknown'")
            }
            if (!hasColumn(database, "battery_samples", "technology")) {
                database.execSQL("ALTER TABLE battery_samples ADD COLUMN technology TEXT NOT NULL DEFAULT 'Unknown'")
            }
            if (!hasColumn(database, "battery_samples", "cycleCount")) {
                database.execSQL("ALTER TABLE battery_samples ADD COLUMN cycleCount INTEGER")
            }
        }

        private fun hasColumn(
            database: SupportSQLiteDatabase,
            tableName: String,
            columnName: String
        ): Boolean {
            database.query("PRAGMA table_info($tableName)").use { cursor ->
                val nameIndex = cursor.getColumnIndex("name")
                while (cursor.moveToNext()) {
                    if (nameIndex >= 0 && cursor.getString(nameIndex) == columnName) {
                        return true
                    }
                }
            }
            return false
        }
    }
}
