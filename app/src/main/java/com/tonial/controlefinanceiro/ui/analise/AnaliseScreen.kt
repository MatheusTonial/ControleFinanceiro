package com.tonial.controlefinanceiro.ui.analise

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.dashed
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.Position
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.tonial.controlefinanceiro.entity.CategoriaMaisGasta
import com.tonial.controlefinanceiro.entity.Categorias
import com.tonial.controlefinanceiro.entity.GastoMensal
import com.tonial.controlefinanceiro.entity.TipoCategoria
import com.tonial.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import com.tonial.controlefinanceiro.ui.theme.TerraformMusgo
import com.tonial.controlefinanceiro.ui.theme.TerraformPrimary
import com.tonial.controlefinanceiro.ui.theme.TerraformStatusAtual
import com.tonial.controlefinanceiro.ui.theme.TerraformStatusFuturo
import com.tonial.controlefinanceiro.ui.theme.TerraformStatusVencido
import com.tonial.controlefinanceiro.ui.theme.TerraformTertiary
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val localeBR: Locale = Locale.forLanguageTag("pt-BR")

// Paleta da rosca de categorias, em tons terrosos do tema Terraform.
private val coresCategorias = listOf(
    TerraformPrimary,
    TerraformMusgo,
    TerraformStatusAtual,
    TerraformTertiary,
    Color(0xFF5D7B8C), // azul ardósia, para separar das tonalidades de marrom
    TerraformStatusFuturo,
    TerraformStatusVencido
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnaliseScreen(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: AnaliseViewModel = viewModel(),
) {
    val gastosMensais by viewModel.gastosMensais.collectAsState()
    val mesCategoria by viewModel.mesCategoria.collectAsState()
    val gastosPorCategoria by viewModel.gastosPorCategoria.collectAsState()
    val acumuladoAtual by viewModel.acumuladoAtual.collectAsState()
    val acumuladoAnterior by viewModel.acumuladoAnterior.collectAsState()
    val acumuladoRetrasado by viewModel.acumuladoRetrasado.collectAsState()
    val projecaoMes by viewModel.projecaoMes.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val categoriaFiltro by viewModel.categoriaFiltro.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Análise") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        AnaliseContent(
            modifier = Modifier.padding(paddingValues),
            gastosMensais = gastosMensais,
            mesCategoria = mesCategoria,
            gastosPorCategoria = gastosPorCategoria,
            acumuladoAtual = acumuladoAtual,
            acumuladoAnterior = acumuladoAnterior,
            acumuladoRetrasado = acumuladoRetrasado,
            projecaoMes = projecaoMes,
            onMesAnterior = viewModel::mesCategoriaAnterior,
            onMesProximo = viewModel::mesCategoriaProximo,
            categorias = categorias,
            categoriaFiltro = categoriaFiltro,
            onCategoriaFiltroChange = viewModel::selecionarCategoriaFiltro
        )
    }
}

@Composable
fun AnaliseContent(
    modifier: Modifier = Modifier,
    gastosMensais: List<GastoMensal>,
    mesCategoria: YearMonth,
    gastosPorCategoria: List<CategoriaMaisGasta>,
    acumuladoAtual: List<BigDecimal>,
    acumuladoAnterior: List<BigDecimal>,
    acumuladoRetrasado: List<BigDecimal>,
    projecaoMes: BigDecimal,
    onMesAnterior: () -> Unit,
    onMesProximo: () -> Unit,
    categorias: List<Categorias>,
    categoriaFiltro: Long?,
    onCategoriaFiltroChange: (Long?) -> Unit
) {
    val nomeFiltro = categorias.firstOrNull { it._id == categoriaFiltro }?.descricao

    ProvideVicoTheme(rememberM3VicoTheme()) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { GastoPorCategoriaCard(mesCategoria, gastosPorCategoria, onMesAnterior, onMesProximo) }
            item { FiltroCategoria(categorias, categoriaFiltro, onCategoriaFiltroChange) }
            item { EvolucaoMensalCard(gastosMensais, nomeFiltro) }
            item { GastoAcumuladoCard(acumuladoAtual, acumuladoAnterior, acumuladoRetrasado, projecaoMes, nomeFiltro) }
        }
    }
}

// Filtro de categoria aplicado aos gráficos de evolução mensal e gasto acumulado.
@Composable
private fun FiltroCategoria(
    categorias: List<Categorias>,
    selecionada: Long?,
    onSelecionar: (Long?) -> Unit
) {
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
    )
    Column {
        Text(
            text = "Filtrar gráficos abaixo por categoria",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        LazyRow(
            contentPadding = PaddingValues(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selecionada == null,
                    onClick = { onSelecionar(null) },
                    label = { Text("Todas") },
                    colors = chipColors
                )
            }
            items(categorias, key = { it._id }) { categoria ->
                FilterChip(
                    selected = selecionada == categoria._id,
                    onClick = { onSelecionar(categoria._id) },
                    label = { Text(categoria.descricao) },
                    colors = chipColors
                )
            }
        }
    }
}

@Composable
private fun GraficoCard(titulo: String, subtitulo: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SemDados() {
    Text(
        text = "Sem lançamentos neste período",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
    )
}

// ---------- Evolução mensal ----------

@Composable
fun EvolucaoMensalCard(gastosMensais: List<GastoMensal>, nomeFiltro: String? = null) {
    val numberFormat = NumberFormat.getCurrencyInstance(localeBR)
    val comGasto = gastosMensais.filter { it.total > BigDecimal.ZERO }
    // A média considera só os meses com lançamentos, para não ser puxada para baixo antes do início do uso do app.
    val media = if (comGasto.isEmpty()) BigDecimal.ZERO
        else comGasto.fold(BigDecimal.ZERO) { acc, g -> acc + g.total }.divide(BigDecimal(comGasto.size), 2, RoundingMode.HALF_UP)

    GraficoCard(
        titulo = "EVOLUÇÃO MENSAL",
        subtitulo = (nomeFiltro?.let { "$it • " } ?: "") + "Últimos ${gastosMensais.size} meses • média ${numberFormat.format(media)}"
    ) {
        if (comGasto.isEmpty()) {
            SemDados()
            return@GraficoCard
        }

        val modelProducer = remember { CartesianChartModelProducer() }
        LaunchedEffect(gastosMensais) {
            modelProducer.runTransaction {
                columnSeries { series(gastosMensais.map { it.total.toDouble() }) }
            }
        }

        val corPrimaria = MaterialTheme.colorScheme.primary
        val formato = CorneredShape.rounded(topLeftPercent = 40, topRightPercent = 40)
        val colunaNormal = rememberLineComponent(fill(corPrimaria.copy(alpha = 0.45f)), 14.dp, formato)
        val colunaMesAtual = rememberLineComponent(fill(corPrimaria), 14.dp, formato)
        val ultimoIndice = gastosMensais.lastIndex
        // Destaca a coluna do mês atual (última da série).
        val columnProvider = remember(colunaNormal, colunaMesAtual, ultimoIndice) {
            object : ColumnCartesianLayer.ColumnProvider {
                override fun getColumn(entry: ColumnCartesianLayerModel.Entry, seriesIndex: Int, extraStore: ExtraStore): LineComponent =
                    if (entry.x.toInt() == ultimoIndice) colunaMesAtual else colunaNormal

                override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore): LineComponent = colunaNormal
            }
        }

        val corMedia = MaterialTheme.colorScheme.tertiary
        val linhaMedia = rememberLineComponent(fill(corMedia), 1.5.dp)
        val rotuloMedia = rememberTextComponent(color = corMedia)
        val mediaDouble = media.toDouble()
        val decoracaoMedia = remember(linhaMedia, rotuloMedia, mediaDouble) {
            HorizontalLine(
                y = { mediaDouble },
                line = linhaMedia,
                labelComponent = rotuloMedia,
                label = { "média" },
                horizontalLabelPosition = Position.Horizontal.End
            )
        }

        val rotulosMeses = remember(gastosMensais) {
            gastosMensais.map { it.mes.month.getDisplayName(TextStyle.SHORT, localeBR).trimEnd('.') }
        }
        val formatoMes = remember(rotulosMeses) {
            CartesianValueFormatter { _, value, _ -> rotulosMeses.getOrElse(value.toInt()) { "" } }
        }

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(columnProvider = columnProvider, columnCollectionSpacing = 8.dp),
                startAxis = VerticalAxis.rememberStart(valueFormatter = formatoValorCompacto),
                bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = formatoMes, guideline = null),
                decorations = listOf(decoracaoMedia),
                marker = rememberMarcador(gastosMensais) { x ->
                    gastosMensais.getOrNull(x)?.let { g ->
                        val mes = g.mes.month.getDisplayName(TextStyle.SHORT, localeBR).trimEnd('.')
                        "$mes/${g.mes.year % 100}: ${numberFormat.format(g.total)}"
                    } ?: ""
                }
            ),
            modelProducer = modelProducer,
            scrollState = rememberVicoScrollState(scrollEnabled = false),
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
            modifier = Modifier.fillMaxWidth().height(220.dp)
        )
    }
}

// ---------- Gasto por categoria ----------

@Composable
fun GastoPorCategoriaCard(
    mes: YearMonth,
    categorias: List<CategoriaMaisGasta>,
    onMesAnterior: () -> Unit,
    onMesProximo: () -> Unit
) {
    val numberFormat = NumberFormat.getCurrencyInstance(localeBR)
    val total = categorias.fold(BigDecimal.ZERO) { acc, c -> acc + c.total }

    // As maiores categorias ganham cor própria; as demais são agrupadas em uma fatia só.
    val corDemais = MaterialTheme.colorScheme.outline
    val fatias = remember(categorias, corDemais) {
        val principais = categorias.take(coresCategorias.size).mapIndexed { i, c -> Triple(c.categoria, c.total, coresCategorias[i]) }
        val restantes = categorias.drop(coresCategorias.size)
        if (restantes.isEmpty()) principais
        else principais + Triple("Demais categorias", restantes.fold(BigDecimal.ZERO) { acc, c -> acc + c.total }, corDemais)
    }

    GraficoCard(titulo = "GASTO POR CATEGORIA", subtitulo = "Distribuição dos gastos no mês") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onMesAnterior) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mês anterior")
            }
            Text(
                text = mes.month.getDisplayName(TextStyle.FULL, localeBR).replaceFirstChar { it.uppercase() } + " de ${mes.year}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onMesProximo, enabled = mes < YearMonth.now()) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Próximo mês")
            }
        }

        if (total <= BigDecimal.ZERO) {
            SemDados()
            return@GraficoCard
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            val corFundo = MaterialTheme.colorScheme.surfaceVariant
            Canvas(modifier = Modifier.size(180.dp)) {
                val espessura = 28.dp.toPx()
                val diametro = size.minDimension - espessura
                val topLeft = Offset((size.width - diametro) / 2, (size.height - diametro) / 2)
                val arcSize = Size(diametro, diametro)
                drawArc(corFundo, 0f, 360f, false, topLeft, arcSize, style = Stroke(espessura))
                val espaco = if (fatias.size > 1) 1.5f else 0f
                var inicio = -90f
                fatias.forEach { (_, valor, cor) ->
                    val angulo = valor.toFloat() / total.toFloat() * 360f
                    drawArc(cor, inicio + espaco / 2, (angulo - espaco).coerceAtLeast(0.5f), false, topLeft, arcSize, style = Stroke(espessura))
                    inicio += angulo
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = numberFormat.format(total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            fatias.forEach { (nome, valor, cor) ->
                val percentual = valor.toDouble() / total.toDouble() * 100
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(12.dp).clip(CircleShape).background(cor))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = nome,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = String.format(localeBR, "%.1f%%", percentual),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = numberFormat.format(valor),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ---------- Gasto acumulado no mês ----------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GastoAcumuladoCard(
    acumuladoAtual: List<BigDecimal>,
    acumuladoAnterior: List<BigDecimal>,
    acumuladoRetrasado: List<BigDecimal>,
    projecaoMes: BigDecimal,
    nomeFiltro: String? = null
) {
    val numberFormat = NumberFormat.getCurrencyInstance(localeBR)
    val hoje = LocalDate.now()
    val ultimoDia = hoje.lengthOfMonth()
    val totalAtual = acumuladoAtual.lastOrNull() ?: BigDecimal.ZERO
    val totalAnteriorAteHoje = acumuladoAnterior.getOrNull(hoje.dayOfMonth - 1) ?: acumuladoAnterior.lastOrNull() ?: BigDecimal.ZERO
    val mostrarProjecao = hoje.dayOfMonth < ultimoDia && projecaoMes > BigDecimal.ZERO

    GraficoCard(
        titulo = "GASTO ACUMULADO NO MÊS",
        subtitulo = (nomeFiltro?.let { "$it • " } ?: "") + "Até hoje: ${numberFormat.format(totalAtual)} • mês anterior no mesmo dia: ${numberFormat.format(totalAnteriorAteHoje)}"
    ) {
        val acumulados = listOf(acumuladoAtual, acumuladoAnterior, acumuladoRetrasado)
        // As listas chegam uma a uma do ViewModel; o gráfico só é montado com todas carregadas.
        if (acumulados.any { it.isEmpty() } || acumulados.all { it.last() <= BigDecimal.ZERO }) {
            SemDados()
            return@GraficoCard
        }

        val modelProducer = remember { CartesianChartModelProducer() }
        LaunchedEffect(acumuladoAtual, acumuladoAnterior, acumuladoRetrasado, projecaoMes) {
            modelProducer.runTransaction {
                lineSeries {
                    series(acumuladoRetrasado.indices.map { it + 1 }, acumuladoRetrasado.map { it.toDouble() })
                    series(acumuladoAnterior.indices.map { it + 1 }, acumuladoAnterior.map { it.toDouble() })
                    series(acumuladoAtual.indices.map { it + 1 }, acumuladoAtual.map { it.toDouble() })
                    if (mostrarProjecao) {
                        series(listOf(hoje.dayOfMonth, ultimoDia), listOf(totalAtual.toDouble(), projecaoMes.toDouble()))
                    }
                }
            }
        }

        val corPrimaria = MaterialTheme.colorScheme.primary
        val corAnterior = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
        val corRetrasado = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        val linhaRetrasado = LineCartesianLayer.rememberLine(
            fill = remember(corRetrasado) { LineCartesianLayer.LineFill.single(fill(corRetrasado)) },
            stroke = LineCartesianLayer.LineStroke.continuous(thickness = 2.dp)
        )
        val linhaAnterior = LineCartesianLayer.rememberLine(
            fill = remember(corAnterior) { LineCartesianLayer.LineFill.single(fill(corAnterior)) },
            stroke = LineCartesianLayer.LineStroke.continuous(thickness = 2.dp)
        )
        val linhaAtual = LineCartesianLayer.rememberLine(
            fill = remember(corPrimaria) { LineCartesianLayer.LineFill.single(fill(corPrimaria)) },
            stroke = LineCartesianLayer.LineStroke.continuous(thickness = 3.dp)
        )
        val linhaProjecao = LineCartesianLayer.rememberLine(
            fill = remember(corPrimaria) { LineCartesianLayer.LineFill.single(fill(corPrimaria.copy(alpha = 0.7f))) },
            stroke = LineCartesianLayer.LineStroke.dashed(thickness = 2.dp)
        )
        val formatoDia = remember { CartesianValueFormatter { _, value, _ -> value.toInt().toString() } }

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = remember(linhaRetrasado, linhaAnterior, linhaAtual, linhaProjecao) {
                        LineCartesianLayer.LineProvider.series(linhaRetrasado, linhaAnterior, linhaAtual, linhaProjecao)
                    }
                ),
                startAxis = VerticalAxis.rememberStart(valueFormatter = formatoValorCompacto),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = formatoDia,
                    guideline = null,
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(spacing = { 5 }) }
                ),
                marker = rememberMarcador(acumuladoAtual, acumuladoAnterior, acumuladoRetrasado, projecaoMes, linhaGuia = true) { dia ->
                    buildList {
                        add("Dia $dia")
                        acumuladoAtual.getOrNull(dia - 1)?.let { add("Este mês: ${numberFormat.format(it)}") }
                        acumuladoAnterior.getOrNull(dia - 1)?.let { add("Mês anterior: ${numberFormat.format(it)}") }
                        acumuladoRetrasado.getOrNull(dia - 1)?.let { add("Mês retrasado: ${numberFormat.format(it)}") }
                        if (mostrarProjecao && dia == ultimoDia) add("Projeção: ${numberFormat.format(projecaoMes)}")
                    }.joinToString("\n")
                }
            ),
            modelProducer = modelProducer,
            scrollState = rememberVicoScrollState(scrollEnabled = false),
            zoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
            modifier = Modifier.fillMaxWidth().height(220.dp)
        )

        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LegendaLinha(corPrimaria, "Este mês")
            LegendaLinha(corAnterior, "Mês anterior")
            LegendaLinha(corRetrasado, "Mês retrasado")
            if (mostrarProjecao) LegendaLinha(corPrimaria.copy(alpha = 0.7f), "Projeção", tracejada = true)
        }
    }
}

@Composable
private fun LegendaLinha(cor: Color, texto: String, tracejada: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(width = 18.dp, height = 3.dp)) {
            drawLine(
                color = cor,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = size.height,
                pathEffect = if (tracejada) PathEffect.dashPathEffect(floatArrayOf(5f, 4f)) else null
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(text = texto, style = MaterialTheme.typography.bodySmall)
    }
}

// Marcador exibido ao tocar no gráfico. [texto] recebe o x tocado (índice do mês ou dia) e devolve o rótulo;
// [chaves] são os dados de que o texto depende, para recriar o marcador quando eles mudam.
@Composable
private fun rememberMarcador(
    vararg chaves: Any,
    linhaGuia: Boolean = false,
    texto: (Int) -> String
): CartesianMarker {
    val rotulo = rememberTextComponent(
        color = MaterialTheme.colorScheme.inverseOnSurface,
        lineCount = 4,
        padding = Insets(8f, 4f, 8f, 4f),
        background = rememberShapeComponent(fill(MaterialTheme.colorScheme.inverseSurface), CorneredShape.rounded(allDp = 6f))
    )
    val guia = if (linhaGuia) rememberAxisGuidelineComponent() else null
    val formatador = remember(*chaves) {
        DefaultCartesianMarker.ValueFormatter { _, targets -> texto(targets.first().x.toInt()) }
    }
    return rememberDefaultCartesianMarker(label = rotulo, valueFormatter = formatador, guideline = guia)
}

// Formata valores do eixo Y de forma compacta: 850, 1,2k, 15k.
private val formatoValorCompacto = CartesianValueFormatter { _, value, _ ->
    when {
        value >= 10_000 -> String.format(localeBR, "%.0fk", value / 1000)
        value >= 1_000 -> String.format(localeBR, "%.1fk", value / 1000).replace(",0k", "k")
        else -> String.format(localeBR, "%.0f", value)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AnaliseContentPreview() {
    ControleFinanceiroTheme {
        val mesAtual = YearMonth.now()
        val mockMensal = (11 downTo 0).map { i ->
            GastoMensal(mesAtual.minusMonths(i.toLong()), BigDecimal(2500 + (i * 137) % 900))
        }
        val mockCategorias = listOf(
            CategoriaMaisGasta("Mercado", BigDecimal("850.75"), 9),
            CategoriaMaisGasta("Condominio", BigDecimal("620.00"), 1),
            CategoriaMaisGasta("Alimentação", BigDecimal("410.30"), 12),
            CategoriaMaisGasta("Transporte", BigDecimal("250.00"), 4),
            CategoriaMaisGasta("Lazer", BigDecimal("180.00"), 3),
            CategoriaMaisGasta("Internet", BigDecimal("120.00"), 1),
            CategoriaMaisGasta("Saúde", BigDecimal("95.00"), 1),
            CategoriaMaisGasta("Outros", BigDecimal("40.00"), 2)
        )
        val diaHoje = LocalDate.now().dayOfMonth
        val mockAtual = (1..diaHoje).map { BigDecimal(it * 95) }
        val mockAnterior = (1..30).map { BigDecimal(it * 88) }
        val mockRetrasado = (1..31).map { BigDecimal(it * 76) }

        AnaliseContent(
            gastosMensais = mockMensal,
            mesCategoria = mesAtual,
            gastosPorCategoria = mockCategorias,
            acumuladoAtual = mockAtual,
            acumuladoAnterior = mockAnterior,
            acumuladoRetrasado = mockRetrasado,
            projecaoMes = BigDecimal(95 * LocalDate.now().lengthOfMonth()),
            onMesAnterior = {},
            onMesProximo = {},
            categorias = mockCategorias.mapIndexed { i, c -> Categorias(i.toLong(), c.categoria, TipoCategoria.Perda, i) },
            categoriaFiltro = null,
            onCategoriaFiltroChange = {}
        )
    }
}
