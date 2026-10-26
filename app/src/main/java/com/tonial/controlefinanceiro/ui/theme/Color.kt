package com.tonial.controlefinanceiro.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Terraform Theme - Paleta de Cores Oficial
 * Inspirada em tons terrosos e na estética de "terraformação".
 */

// ---------- Claro ----------
val TerraformPrimary = Color(0xFFBD4B00)    // Laranja Vibrante (Ativo)
val TerraformSurface = Color(0xFFFBFBE2)    // Creme suave (Fundo Principal)
val TerraformSecondary = Color(0xFF3E2723)  // Marrom Escuro (Base Sólida)
val TerraformTertiary = Color(0xFF8D6E63)   // Marrom Terroso (Cards e Detalhes)
val TerraformMusgo = Color(0xFF4E6B3F)      // Verde Musgo (Acentos e Sucesso)

val TerraformOnPrimary = Color.White
val TerraformOnSurface = Color(0xFF1C1B1F)
val TerraformOnTertiary = Color.White

val TerraformPrimaryContainer = Color(0xFFFFDBCF)   // Laranja pálido
val TerraformSecondaryContainer = Color(0xFFD7CCC8) // Marrom pálido para seleções
val TerraformTertiaryContainer = Color(0xFFF5EBE8)
val TerraformSurfaceVariant = Color(0xFFE7E7D0)     // Creme escurecido (usado em labels de input)
val TerraformOnSurfaceVariant = Color(0xFF49454F)   // Texto escuro sobre creme escurecido

// ---------- Escuro ----------
// Base vira marrom quase-preto; o laranja de TerraformPrimary se mantém (já tem contraste
// suficiente sobre fundo escuro, então não criamos um segundo "laranja escuro").
val TerraformPrimaryDark = TerraformPrimary
val TerraformOnPrimaryDark = Color.White
val TerraformPrimaryContainerDark = Color(0xFF5C2600)
val TerraformOnPrimaryContainerDark = Color(0xFFFFDBCF)

val TerraformSurfaceDark = Color(0xFF1E1512)        // marrom quase-preto (fundo principal)
val TerraformOnSurfaceDark = Color(0xFFF2EDE4)       // creme/off-white (texto principal)
val TerraformSurfaceVariantDark = Color(0xFF3E2723)  // TerraformSecondary reaproveitado como superfície elevada
val TerraformOnSurfaceVariantDark = Color(0xFFD7CCC6)

val TerraformSecondaryDark = Color(0xFFD7CCC8)       // marrom-acinzentado claro (texto/componentes secundários)
val TerraformOnSecondaryDark = Color(0xFF3E2723)
val TerraformSecondaryContainerDark = Color(0xFF53433F)
val TerraformOnSecondaryContainerDark = Color(0xFFEFE0DC)

val TerraformTertiaryDark = Color(0xFFBCAAA4)        // marrom/musgo clareado para contraste no escuro
val TerraformOnTertiaryDark = Color(0xFF2B211E)
val TerraformTertiaryContainerDark = Color(0xFF5D4037)
val TerraformOnTertiaryContainerDark = Color(0xFFF5EBE8)

val TerraformSurfaceContainerDark = Color(0xFF2A1F1B)
val TerraformSurfaceContainerLowDark = Color(0xFF241A16)
val TerraformSurfaceContainerLowestDark = Color(0xFF19120F)
val TerraformSurfaceContainerHighDark = Color(0xFF342722)
val TerraformSurfaceContainerHighestDark = Color(0xFF3F302A)

val TerraformOutlineDark = Color(0xFF9B8A83)
val TerraformOutlineVariantDark = Color(0xFF6B8C5A) // verde musgo clareado

// ---------- Indicadores de status (compartilhados entre claro/escuro) ----------
val TerraformGreenGasto = Color(0xFF3CCE43) // economia / aumento positivo (espelha res/values/colors.xml)
val TerraformRedGasto = Color(0xFFE23124)   // gasto maior que o mês anterior

val TerraformStatusFuturo = Color(0xFF7AB77E)  // conta recorrente futura
val TerraformStatusAtual = Color(0xFFD9A419)   // conta recorrente do mês atual (escurecido p/ legibilidade no modo escuro)
val TerraformStatusVencido = Color(0xFFC03A2E) // conta recorrente vencida
