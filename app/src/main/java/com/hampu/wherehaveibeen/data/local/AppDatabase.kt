package com.hampu.wherehaveibeen.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CountryEntity::class],
    version = AppDatabase.VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao

    companion object {
        const val VERSION = 2

        // Version 2 keeps the existing schema intact and formalizes the upgrade path for persisted data.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) = Unit
        }

        val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)
    }
}
