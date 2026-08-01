package com.tonial.controlefinanceiro.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// O Tema Terraform é definido por uma paleta clara e orgânica (creme).
// Para manter a identidade visual consistente mesmo se o sistema estiver em modo escuro,
// definimos ambos os esquemas com a cor de fundo TerraformSurface.

private val TerraformColorScheme = lightColorScheme(
    primary = TerraformPrimary,
    secondary = TerraformSecondary,
    tertiary = TerraformTertiary,
    background = TerraformSurface,
    surface = TerraformSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TerraformOnSurface,
    onSurface = TerraformOnSurface,
    surfaceVariant = Color(0xFFE7E7D0), // Um tom levemente mais escuro que o creme para variantes
    onSurfaceVariant = TerraformOnSurface
)

val TerraformShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp)
)

@Composable
fun ControleFinanceiroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desativamos cores dinâmicas para preservar a identidade Terraform
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Ignoramos a preferência de 'darkTheme' do sistema para o fundo, 
    // pois a Identidade Terraform exige o fundo creme (#FBFBE2).
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> TerraformColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = TerraformShapes,
        content = content
    )
}
