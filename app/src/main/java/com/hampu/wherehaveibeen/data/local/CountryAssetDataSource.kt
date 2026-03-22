package com.hampu.wherehaveibeen.data.local

import android.content.Context

open class CountryAssetDataSource(
    private val context: Context,
    private val parser: CountrySeedParser = CountrySeedParser()
) {
    open fun loadCountries(): List<CountrySeed> {
        context.assets.open(COUNTRIES_ASSET).use { input ->
            return parser.parse(input)
        }
    }

    companion object {
        private const val COUNTRIES_ASSET = "countries.json"
    }
}
