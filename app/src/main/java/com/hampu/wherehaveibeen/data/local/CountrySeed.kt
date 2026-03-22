package com.hampu.wherehaveibeen.data.local

import kotlinx.serialization.Serializable

@Serializable
data class CountrySeed(
    val isoCode: String,
    val name: String,
    val continent: String,
    val flagEmoji: String,
    val capital: String
)
