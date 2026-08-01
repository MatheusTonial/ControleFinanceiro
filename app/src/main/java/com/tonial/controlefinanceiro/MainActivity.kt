package com.tonial.controlefinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tonial.controlefinanceiro.ui.navegacao.AppScaffold
import com.tonial.controlefinanceiro.ui.navegacao.Routes
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme

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
            ControleFinanceiroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    AppScaffold(startDestination = startDestination)
                }
            }
        }
    }
}
