package com.tonial.controlefinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tonial.controlefinanceiro.ui.navegacao.AppScaffold
import com.tonial.controlefinanceiro.ui.navegacao.Routes
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import com.tonial.controlefinanceiro.ui.theme.ThemeMode
import com.tonial.controlefinanceiro.ui.theme.ThemePreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ativa o modo Edge-to-Edge padrão.
        // A cor da barra de status será controlada pelo SideEffect no Theme.kt
        enableEdgeToEdge()

        val startDestination = if (intent?.action == "com.tonial.controlefinanceiro.QUICK_ADD") {
            intent.getStringExtra("route") ?: Routes.SPLASH
        } else {
            Routes.SPLASH
        }

        setContent {
            var themeMode by remember { mutableStateOf(ThemePreference.get(this)) }
            val isSystemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemDark
            }

            ControleFinanceiroTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScaffold(
                        startDestination = startDestination,
                        themeMode = themeMode,
                        onThemeModeChange = { newMode ->
                            themeMode = newMode
                            ThemePreference.set(this, newMode)
                        }
                    )
                }
            }
        }
    }
}
