package com.hampu.wherehaveibeen.data.local

import kotlinx.serialization.json.Json
import java.io.InputStream

class CountrySeedParser(
    private val json: Json = Json {
        ignoreUnknownKeys = true
    }
) {
    fun parse(stream: InputStream): List<CountrySeed> {
        return json.decodeFromString(stream.readBytes().decodeToString())
    }
}
