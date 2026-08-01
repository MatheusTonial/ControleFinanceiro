package com.tonial.controlefinanceiro.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Terraform Theme - Paleta de Cores Oficial
 * Inspirada em tons terrosos e na estética de "terraformação".
 */

// Cores Principais
val TerraformPrimary = Color(0xFFBD4B00)    // Laranja Vibrante (Ativo)
val TerraformSurface = Color(0xFFFBFBE2)    // Creme suave (Fundo Principal)
val TerraformSecondary = Color(0xFF3E2723)  // Marrom Escuro (Base Sólida)
val TerraformTertiary = Color(0xFF8D6E63)   // Marrom Terroso (Cards e Detalhes)
val TerraformMusgo = Color(0xFF4E6B3F)      // Verde Musgo (Acentos e Sucesso)

// Cores de Contraste
val TerraformOnPrimary = Color.White
val TerraformOnSurface = Color(0xFF1C1B1F)
val TerraformOnTertiary = Color.White

// Variações para Containers e Estados (Essenciais para remover o violeta padrão do M3)
val TerraformPrimaryContainer = Color(0xFFFFDBCF)   // Laranja pálido
val TerraformSecondaryContainer = Color(0xFFE7EDE0) // Verde musgo pálido (Seleção)
val TerraformTertiaryContainer = Color(0xFF8D6E63)  // Usado para cards
val TerraformSurfaceVariant = Color(0xFFE7E7D0)    // Creme escurecido (usado em labels de input)
val TerraformOnSurfaceVariant = Color(0xFF49454F)  // Texto escuro sobre creme escurecido

// Antigos tokens (mantidos para compatibilidade, mas não devem ser usados no novo tema)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
