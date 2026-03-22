package com.hampu.wherehaveibeen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hampu.wherehaveibeen.ui.theme.WhereHaveIBeenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as WhereHaveIBeenApplication).container.countryRepository
        setContent {
            WhereHaveIBeenTheme {
                WhereHaveIBeenApp(repository = repository)
            }
        }
    }
}
