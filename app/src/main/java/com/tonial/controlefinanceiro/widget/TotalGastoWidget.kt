package com.tonial.controlefinanceiro.widget

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
        
        // Lógica de cores baseada no DashboardViewModel para definir se houve aumento de gastos.
        val gastosUnicosMes = dbHandler.getTotalGastoUnicosMesAtual()
        val totalGastoMesAnterior = dbHandler.getTotalGastoMesAnterior()
        
        val hoje = LocalDate.now()
        val mesAnterior = hoje.minusMonths(1)
        val diasNoMesAnterior = mesAnterior.lengthOfMonth()
        val diaAtual = hoje.dayOfMonth
        
        var isAumento = false
        
        // Compara os gastos atuais (descontando os únicos) com o gasto proporcional do mês passado.
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
 * Conteúdo do widget com a lógica de cores aplicada ao valor total.
 */
@Composable
fun WidgetContent(total: BigDecimal, count: Int, isAumento: Boolean) {
    val ptBr = Locale.forLanguageTag("pt-BR")
    val currencyFormat = NumberFormat.getCurrencyInstance(ptBr)
    
    // Define a cor: vermelho para aumento de gastos, verde para economia.
    // Usando ColorProvider com resId para evitar erro de visibilidade do Glance
    val valorColor = if (isAumento) ColorProvider(R.color.red_gasto) else ColorProvider(R.color.green_gasto)
    
    GlanceBox(
        modifier = GlanceModifier
            .glanceFillMaxSize()
            .clickable(actionStartActivity<MainActivity>())
            // Mantém o drawable de fundo para garantir bordas arredondadas no Android 10.
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
                    color = GlanceTheme.colors.onSurfaceVariant
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
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        }
    }
}

@Preview(widthDp = 200, heightDp = 100)
@Composable
fun WidgetContentPreview() {
    val total = BigDecimal("123.45")
    val count = 5
    val ptBr = Locale.forLanguageTag("pt-BR")
    val currencyFormat = NumberFormat.getCurrencyInstance(ptBr)
    val isAumento = true

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TOTAL GASTO NO MÊS",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currencyFormat.format(total),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAumento) MaterialTheme.colorScheme.error else Color(0xFF388E3C)
                )
            }
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = "$count lanç.",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
