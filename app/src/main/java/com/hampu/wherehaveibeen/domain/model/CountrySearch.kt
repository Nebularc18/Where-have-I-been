package com.hampu.wherehaveibeen.domain.model

fun Country.matchesQuery(query: String): Boolean {
    val normalized = query.trim()
    if (normalized.isBlank()) return true
    val needle = normalized.lowercase()
    return name.lowercase().contains(needle) ||
        isoCode.lowercase().contains(needle) ||
        capital.lowercase().contains(needle) ||
        continent.lowercase().contains(needle)
}
