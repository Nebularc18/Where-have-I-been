package com.hampu.wherehaveibeen.data.local

import android.content.Context
import java.io.IOException

open class CountryAssetDataSource(
    private val context: Context,
    private val parser: CountrySeedParser = CountrySeedParser()
) {
    open fun loadCountries(): List<CountrySeed> {
        try {
            context.assets.open(COUNTRIES_ASSET).use { input ->
                return parser.parse(input)
            }
        } catch (exception: IOException) {
            throw IllegalStateException("Failed to load countries asset '$COUNTRIES_ASSET'.", exception)
        }
    }

    companion object {
        private const val COUNTRIES_ASSET = "countries.json"
    }
}
