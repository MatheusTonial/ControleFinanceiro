package com.tonial.controlefinanceiro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val TerraformColorScheme = lightColorScheme(
    primary = TerraformPrimary,      // #BD4B00 (Laranja Ativo)
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = TerraformPrimary,

    secondary = TerraformSecondary,  // #3E2723 (Marrom Escuro - Base Sólida)
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7CCC8), // Marrom pálido para seleções
    onSecondaryContainer = TerraformSecondary,

    tertiary = TerraformTertiary,    // #8D6E63 (Marrom Terroso - Cards)
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF5EBE8),
    onTertiaryContainer = TerraformTertiary,

    background = TerraformSurface,   // #FBFBE2 (Creme Orgânico - Fundo Principal)
    onBackground = TerraformOnSurface,
    
    surface = TerraformSurface,
    onSurface = TerraformOnSurface,
    
    surfaceVariant = TerraformTertiary,
    onSurfaceVariant = Color.White,
    
    surfaceContainer = TerraformSurface,
    surfaceContainerLow = TerraformSurface,
    surfaceContainerLowest = TerraformSurface,
    surfaceContainerHigh = Color(0xFFF2F2D7),
    surfaceContainerHighest = Color(0xFFE7E7D0),
    
    surfaceTint = Color.Transparent,
    
    outline = TerraformTertiary,
    outlineVariant = TerraformMusgo
)

val TerraformShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp)
)

@Composable
fun ControleFinanceiroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Define a cor da barra de status como TerraformPrimary
            window.statusBarColor = TerraformPrimary.toArgb()
            
            // Configura os ícones da barra de status para serem brancos (false)
            // Já que o laranja TerraformPrimary é escuro, ícones brancos oferecem melhor contraste.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = TerraformColorScheme,
        typography = Typography,
        shapes = TerraformShapes,
        content = content
    )
}
