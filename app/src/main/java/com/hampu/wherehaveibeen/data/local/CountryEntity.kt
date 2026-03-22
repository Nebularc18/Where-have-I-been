package com.hampu.wherehaveibeen.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val isoCode: String,
    val name: String,
    val continent: String,
    val flagEmoji: String,
    val capital: String,
    val isVisited: Boolean,
    val isWishlisted: Boolean
)
