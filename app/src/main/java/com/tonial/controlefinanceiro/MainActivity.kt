package com.tonial.controlefinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tonial.controlefinanceiro.ui.navegacao.AppScaffold
import com.tonial.controlefinanceiro.ui.navegacao.Routes
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = if (intent?.action == "com.tonial.controlefinanceiro.QUICK_ADD") {
            intent.getStringExtra("route") ?: Routes.SPLASH
        } else {
            Routes.SPLASH
        }

        setContent {
            ControleFinanceiroTheme {
                AppScaffold(startDestination = startDestination)
            }
        }
    }
}