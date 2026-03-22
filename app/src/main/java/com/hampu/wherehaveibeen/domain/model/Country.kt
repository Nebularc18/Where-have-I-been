package com.hampu.wherehaveibeen.domain.model

import com.hampu.wherehaveibeen.data.local.CountryEntity

data class Country(
    val isoCode: String,
    val name: String,
    val continent: String,
    val flagEmoji: String,
    val capital: String,
    val isVisited: Boolean,
    val isWishlisted: Boolean
)

fun CountryEntity.toCountry(): Country {
    return Country(
        isoCode = isoCode,
        name = name,
        continent = continent,
        flagEmoji = flagEmoji,
        capital = capital,
        isVisited = isVisited,
        isWishlisted = isWishlisted
    )
}
