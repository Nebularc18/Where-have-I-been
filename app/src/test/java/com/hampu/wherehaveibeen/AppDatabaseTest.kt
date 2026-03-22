package com.hampu.wherehaveibeen

import com.google.common.truth.Truth.assertThat
import com.hampu.wherehaveibeen.data.local.AppDatabase
import org.junit.Test

class AppDatabaseTest {
    @Test
    fun migrationPath_coversVersionOneInstallations() {
        assertThat(AppDatabase.VERSION).isEqualTo(2)
        assertThat(AppDatabase.ALL_MIGRATIONS.asList()).contains(AppDatabase.MIGRATION_1_2)
        assertThat(AppDatabase.MIGRATION_1_2.startVersion).isEqualTo(1)
        assertThat(AppDatabase.MIGRATION_1_2.endVersion).isEqualTo(2)
    }
}
