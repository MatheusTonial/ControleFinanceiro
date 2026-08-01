package com.tonial.controlefinanceiro.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment as GlanceAlignment
import androidx.glance.layout.Box as GlanceBox
import androidx.glance.layout.Column as GlanceColumn
import androidx.glance.layout.fillMaxSize as glanceFillMaxSize
import androidx.glance.layout.padding as glancePadding
import androidx.glance.text.FontWeight as GlanceFontWeight
import androidx.glance.text.Text as GlanceText
import androidx.glance.text.TextStyle as GlanceTextStyle
import androidx.glance.unit.ColorProvider
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.R
import com.tonial.controlefinanceiro.MainActivity
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

class TotalGastoWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dbHandler = DatabaseHandler.getInstance(context)
        val total = dbHandler.getTotalGastoMesAtual()
        val count = dbHandler.getLancamentosCountMesAtual()
        
        val gastosUnicosMes = dbHandler.getTotalGastoUnicosMesAtual()
        val totalGastoMesAnterior = dbHandler.getTotalGastoMesAnterior()
        
        val hoje = LocalDate.now()
        val mesAnterior = hoje.minusMonths(1)
        val diasNoMesAnterior = mesAnterior.lengthOfMonth()
        val diaAtual = hoje.dayOfMonth
        
        var isAumento = false
        
        if (diasNoMesAnterior > 0 && totalGastoMesAnterior > BigDecimal.ZERO) {
            val mediaDiariaMesAnterior = totalGastoMesAnterior.divide(BigDecimal(diasNoMesAnterior), 2, RoundingMode.HALF_UP)
            val gastoProporcional = mediaDiariaMesAnterior.multiply(BigDecimal(diaAtual))
            
            if (gastoProporcional > BigDecimal.ZERO) {
                val gastosAtuaisComparaveis = total.subtract(gastosUnicosMes)
                isAumento = gastosAtuaisComparaveis > gastoProporcional
            } else {
                isAumento = total > BigDecimal.ZERO
            }
        } else {
            isAumento = total > BigDecimal.ZERO
        }

        provideContent {
            GlanceTheme {
                WidgetContent(total, count, isAumento)
            }
        }
    }
}

/**
 * Conteúdo do widget com a cor Laranja (Primary) aplicada aos rótulos para destaque.
 */
@Composable
fun WidgetContent(total: BigDecimal, count: Int, isAumento: Boolean) {
    val ptBr = Locale.forLanguageTag("pt-BR")
    val currencyFormat = NumberFormat.getCurrencyInstance(ptBr)
    
    // Cor do valor: vermelho para aumento, verde para economia
    val valorColor = if (isAumento) ColorProvider(R.color.red_gasto) else ColorProvider(R.color.green_gasto)
    
    // Cor Laranja (TerraformPrimary) para os títulos do widget, combinando com o destaque do app.
    val labelColor = ColorProvider(R.color.terraform_primary)

    GlanceBox(
        modifier = GlanceModifier
            .glanceFillMaxSize()
            .clickable(actionStartActivity<MainActivity>())
            .background(ImageProvider(R.drawable.widget_background))
            .glancePadding(12.dp),
        contentAlignment = GlanceAlignment.Center
    ) {
        GlanceColumn(
            horizontalAlignment = GlanceAlignment.CenterHorizontally,
            verticalAlignment = GlanceAlignment.Vertical.CenterVertically
        ) {
            GlanceText(
                text = "TOTAL GASTO NO MÊS",
                style = GlanceTextStyle(
                    fontSize = 11.sp,
                    color = labelColor,
                    fontWeight = GlanceFontWeight.Bold
                )
            )
            GlanceText(
                text = currencyFormat.format(total),
                style = GlanceTextStyle(
                    fontSize = 20.sp,
                    fontWeight = GlanceFontWeight.Bold,
                    color = valorColor
                )
            )
        }
        
        GlanceBox(
            modifier = GlanceModifier.glanceFillMaxSize(),
            contentAlignment = GlanceAlignment.BottomEnd
        ) {
            GlanceText(
                text = "$count lanç.",
                style = GlanceTextStyle(
                    fontSize = 9.sp,
                    color = labelColor,
                    fontWeight = GlanceFontWeight.Medium
                )
            )
        }
    }
}
