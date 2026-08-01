package com.tonial.controlefinanceiro.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tonial.controlefinanceiro.R
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // 2 segundos
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Alterado para TerraformPrimary para combinar com a barra de status e garantir contraste com ícones brancos
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.money),
            contentDescription = "Ícone do App",
            modifier = Modifier.size(128.dp),
            // Cor do ícone alterada para onPrimary (branco) para contraste sobre o fundo laranja
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    ControleFinanceiroTheme {
        SplashScreen(onTimeout = {})
    }
}
