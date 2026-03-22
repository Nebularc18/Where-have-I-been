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
}
