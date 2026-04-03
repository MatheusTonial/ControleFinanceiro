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
import com.tonial.controlefinanceiro.database.DatabaseHandler
import com.tonial.controlefinanceiro.R
import com.tonial.controlefinanceiro.MainActivity
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class TotalGastoWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dbHandler = DatabaseHandler.getInstance(context)
        val total = dbHandler.getTotalGastoMesAtual()
        val count = dbHandler.getLancamentosCountMesAtual()

        provideContent {
            GlanceTheme {
                WidgetContent(total, count)
            }
        }
    }
}

@Composable
fun WidgetContent(total: BigDecimal, count: Int) {
    val ptBr = Locale.forLanguageTag("pt-BR")
    val currencyFormat = NumberFormat.getCurrencyInstance(ptBr)
    
    GlanceBox(
        modifier = GlanceModifier
            .glanceFillMaxSize()
            // Adicionada a ação para abrir o aplicativo ao clicar no widget
            .clickable(actionStartActivity<MainActivity>())
            // Usando o Drawable XML para garantir bordas arredondadas no Android 10
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
                    color = GlanceTheme.colors.onSurface
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
                    color = MaterialTheme.colorScheme.onSurface
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
