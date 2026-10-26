package com.tonial.controlefinanceiro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val TerraformLightColorScheme = lightColorScheme(
    primary = TerraformPrimary,      // #BD4B00 (Laranja Ativo)
    onPrimary = TerraformOnPrimary,
    primaryContainer = TerraformPrimaryContainer,
    onPrimaryContainer = TerraformPrimary,

    secondary = TerraformSecondary,  // #3E2723 (Marrom Escuro - Base Sólida)
    onSecondary = Color.White,
    secondaryContainer = TerraformSecondaryContainer,
    onSecondaryContainer = TerraformSecondary,

    tertiary = TerraformTertiary,    // #8D6E63 (Marrom Terroso - Cards)
    onTertiary = TerraformOnTertiary,
    tertiaryContainer = TerraformTertiaryContainer,
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

private val TerraformDarkColorScheme = darkColorScheme(
    primary = TerraformPrimaryDark,
    onPrimary = TerraformOnPrimaryDark,
    primaryContainer = TerraformPrimaryContainerDark,
    onPrimaryContainer = TerraformOnPrimaryContainerDark,

    secondary = TerraformSecondaryDark,
    onSecondary = TerraformOnSecondaryDark,
    secondaryContainer = TerraformSecondaryContainerDark,
    onSecondaryContainer = TerraformOnSecondaryContainerDark,

    tertiary = TerraformTertiaryDark,
    onTertiary = TerraformOnTertiaryDark,
    tertiaryContainer = TerraformTertiaryContainerDark,
    onTertiaryContainer = TerraformOnTertiaryContainerDark,

    background = TerraformSurfaceDark,
    onBackground = TerraformOnSurfaceDark,

    surface = TerraformSurfaceDark,
    onSurface = TerraformOnSurfaceDark,

    surfaceVariant = TerraformSurfaceVariantDark,
    onSurfaceVariant = TerraformOnSurfaceVariantDark,

    surfaceContainer = TerraformSurfaceContainerDark,
    surfaceContainerLow = TerraformSurfaceContainerLowDark,
    surfaceContainerLowest = TerraformSurfaceContainerLowestDark,
    surfaceContainerHigh = TerraformSurfaceContainerHighDark,
    surfaceContainerHighest = TerraformSurfaceContainerHighestDark,

    surfaceTint = Color.Transparent,

    outline = TerraformOutlineDark,
    outlineVariant = TerraformOutlineVariantDark
)

// "Rounded Eight": 8dp em todos os níveis, inclusive extraSmall/extraLarge
// (FloatingActionButton usa o token extraLarge — sem isso ele cairia no círculo padrão do M3).
val TerraformShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
    extraLarge = RoundedCornerShape(8.dp)
)

@Composable
fun ControleFinanceiroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) TerraformDarkColorScheme else TerraformLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Barra de status segue o esquema ativo em vez de ficar hardcoded em laranja.
            window.statusBarColor = colorScheme.primary.toArgb()

            // Ícones claros sobre a cor primary, tanto no claro quanto no escuro
            // (o laranja TerraformPrimary é escuro o suficiente nos dois casos).
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = TerraformShapes,
        content = content
    )
}
