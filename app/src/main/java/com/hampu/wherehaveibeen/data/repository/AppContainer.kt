package com.hampu.wherehaveibeen.data.repository

import android.content.Context
import androidx.room.Room
import com.hampu.wherehaveibeen.data.local.AppDatabase
import com.hampu.wherehaveibeen.data.local.CountryAssetDataSource
import kotlinx.coroutines.Dispatchers

interface AppContainer {
    val countryRepository: CountryRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "where_have_i_been.db"
        ).addMigrations(*AppDatabase.ALL_MIGRATIONS)
            .build()
    }

    override val countryRepository: CountryRepository by lazy {
        OfflineFirstCountryRepository(
            countryDao = database.countryDao(),
            assetDataSource = CountryAssetDataSource(context),
            ioDispatcher = Dispatchers.IO
        )
    }
}
