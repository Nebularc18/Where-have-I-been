package com.hampu.wherehaveibeen

import com.google.common.truth.Truth.assertThat
import com.hampu.wherehaveibeen.data.local.CountrySeedParser
import java.io.ByteArrayInputStream
import org.junit.Test

class CountrySeedParserTest {
    @Test
    fun parse_returnsCountriesFromJson() {
        val json = """
            [
              {
                "isoCode": "SE",
                "name": "Sweden",
                "continent": "Europe",
                "flagEmoji": "🇸🇪",
                "capital": "Stockholm"
              }
            ]
        """.trimIndent()

        val parsed = CountrySeedParser().parse(ByteArrayInputStream(json.toByteArray()))

        assertThat(parsed).hasSize(1)
        assertThat(parsed.single().isoCode).isEqualTo("SE")
        assertThat(parsed.single().capital).isEqualTo("Stockholm")
    }

    @Test
    fun parse_throwsHelpfulMessageForMalformedJson() {
        val malformedJson = """[{ "isoCode": "SE" """

        val exception = runCatching {
            CountrySeedParser().parse(ByteArrayInputStream(malformedJson.toByteArray()))
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(IllegalStateException::class.java)
        assertThat(exception).hasMessageThat().contains("Failed to parse country seed data.")
    }
}
