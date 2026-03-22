package com.hampu.wherehaveibeen.data.local

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream

class CountrySeedParser(
    private val json: Json = Json {
        ignoreUnknownKeys = true
    }
) {
    fun parse(stream: InputStream): List<CountrySeed> {
        return try {
            json.decodeFromString(stream.readBytes().decodeToString())
        } catch (exception: SerializationException) {
            throw IllegalStateException("Failed to parse country seed data.", exception)
        } catch (exception: IllegalArgumentException) {
            throw IllegalStateException("Failed to decode country seed data.", exception)
        }
    }
}
