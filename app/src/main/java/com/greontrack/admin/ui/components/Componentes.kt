package com.greontrack.admin.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greontrack.admin.R
import com.greontrack.admin.data.Carga
import com.greontrack.admin.ui.theme.Greon
import com.greontrack.admin.ui.theme.PlusJakartaSans
import com.greontrack.admin.util.Formato

// ---------------------------------------------------------------------
//  Barra superior verde oscuro, como el sidebar de la web
// ---------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreonTopBar(
    titulo: String,
    subtitulo: String? = null,
    navigationIcon: @Composable () -> Unit = {
        Image(
            painter = painterResource(R.drawable.logo_greon),
            contentDescription = "GreonTrack",
            modifier = Modifier
                .padding(start = 12.dp, end = 4.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(3.dp)
        )
    },
    alRefrescar: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(titulo, style = MaterialTheme.typography.titleLarge, color = Color.White, maxLines = 1)
                if (subtitulo != null) {
                    Text(
                        subtitulo,
                        style = MaterialTheme.typography.bodySmall,
                        color = Greon.VerdeClaro,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = navigationIcon,
        actions = {
            if (alRefrescar != null) {
                IconButton(onClick = alRefrescar) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Actualizar", tint = Color.White)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Greon.VerdeOscuro,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

// ---------------------------------------------------------------------
//  Tarjetas
// ---------------------------------------------------------------------
@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    titulo: String? = null,
    descripcion: String? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Greon.Superficie),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            if (titulo != null) {
                Text(titulo, style = MaterialTheme.typography.titleMedium, color = Greon.TextoTitulo)
                if (descripcion != null) {
                    Text(descripcion, style = MaterialTheme.typography.bodySmall, color = Greon.TextoSuave)
                }
                Spacer(Modifier.height(14.dp))
            }
            contenido()
        }
    }
}

/**
 * Tarjeta de indicador. El número "cuenta" desde 0 hasta [numero]
 * conforme [progreso] avanza de 0 a 1 (ver rememberProgresoContador).
 */
@Composable
fun KpiCard(
    icono: ImageVector,
    etiqueta: String,
    numero: Double,
    formato: (Double) -> String,
    acento: Color,
    modifier: Modifier = Modifier,
    detalle: String? = null,
    progreso: () -> Float = { 1f }
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Greon.Superficie),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(acento.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = acento, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                formato(numero * progreso()),
                style = MaterialTheme.typography.headlineSmall,
                color = Greon.Texto,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = Greon.TextoSuave, maxLines = 1)
            if (detalle != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    detalle,
                    style = MaterialTheme.typography.labelSmall,
                    color = acento,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Dato pequeño: valor arriba, etiqueta abajo. */
@Composable
fun MiniDato(valor: String, etiqueta: String, modifier: Modifier = Modifier, color: Color = Greon.TextoTitulo) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Greon.VerdeMuyClaro)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(valor, style = MaterialTheme.typography.titleMedium, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = Greon.TextoSuave, maxLines = 1)
    }
}

// ---------------------------------------------------------------------
//  Chips y avatar
// ---------------------------------------------------------------------
fun colorRol(rol: String): Color = when (rol) {
    "admin" -> Greon.Morado
    "analitico" -> Greon.Azul
    else -> Greon.Verde
}

@Composable
fun RolChip(rol: String) {
    Etiqueta(Formato.nombreRol(rol), colorRol(rol))
}

@Composable
fun Etiqueta(texto: String, color: Color) {
    Text(
        texto,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun Avatar(nombre: String, color: Color = Greon.Verde, tamano: Dp = 44.dp) {
    Box(
        Modifier
            .size(tamano)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            Formato.iniciales(nombre),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = (tamano.value * 0.36f).sp
        )
    }
}

// ---------------------------------------------------------------------
//  Gráficas (dibujadas a mano con Canvas, sin librerías extra)
//
//  Al tocar una barra o un punto (o deslizar el dedo encima) se resalta
//  y aparece un globito con el día y el valor exacto. Tocar otra vez
//  el mismo elemento oculta el globito.
// ---------------------------------------------------------------------
@Composable
fun GraficaBarras(
    valores: List<Double>,
    etiquetas: List<String>,
    color: Color,
    modifier: Modifier = Modifier,
    etiquetasGlobo: List<String> = etiquetas,
    formatoValor: (Double) -> String = { Formato.decimal(it, 1) },
    altura: Dp = 150.dp
) {
    val maximo = (valores.maxOrNull() ?: 0.0).coerceAtLeast(1.0)
    val lineaGuia = Greon.Texto.copy(alpha = 0.08f)
    val fondoGlobo = if (Greon.EsOscuro) Greon.Verde else Greon.VerdeOscuro
    val seleccion = remember(valores) { mutableStateOf<Int?>(null) }
    val medidor = rememberTextMeasurer()
    val haptico = LocalHapticFeedback.current

    Column(modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(altura)
                .tocarParaSeleccionar(seleccion, valores.size) {
                    haptico.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
        ) {
            val n = valores.size
            if (n == 0) return@Canvas
            val elegido = seleccion.value
            for (i in 0..3) {
                val y = size.height * i / 3f
                drawLine(lineaGuia, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
            }
            val espacio = size.width / n
            val ancho = espacio * 0.6f
            valores.forEachIndexed { i, v ->
                val alto = ((v / maximo).toFloat() * size.height).coerceAtLeast(3.dp.toPx())
                val base = if (v > 0) color else color.copy(alpha = 0.18f)
                // Si hay una barra elegida, las demás se ven más tenues
                val apagada = elegido != null && elegido != i
                drawRoundRect(
                    color = if (apagada) base.copy(alpha = base.alpha * 0.35f) else base,
                    topLeft = Offset(i * espacio + (espacio - ancho) / 2f, size.height - alto),
                    size = Size(ancho, alto),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
            if (elegido != null && elegido < n) {
                val alto = ((valores[elegido] / maximo).toFloat() * size.height).coerceAtLeast(3.dp.toPx())
                dibujarGlobo(
                    medidor = medidor,
                    fondo = fondoGlobo,
                    titulo = etiquetasGlobo.getOrElse(elegido) { "" },
                    valor = formatoValor(valores[elegido]),
                    ancla = Offset(elegido * espacio + espacio / 2f, size.height - alto)
                )
            }
        }
        EtiquetasEje(etiquetas, seleccion.value)
    }
}

@Composable
fun GraficaLinea(
    valores: List<Double>,
    etiquetas: List<String>,
    color: Color,
    modifier: Modifier = Modifier,
    etiquetasGlobo: List<String> = etiquetas,
    formatoValor: (Double) -> String = { Formato.decimal(it, 1) },
    altura: Dp = 150.dp
) {
    val maximo = (valores.maxOrNull() ?: 0.0).coerceAtLeast(0.001)
    val lineaGuia = Greon.Texto.copy(alpha = 0.08f)
    val fondoPunto = Greon.Superficie
    val fondoGlobo = if (Greon.EsOscuro) Greon.Verde else Greon.VerdeOscuro
    val seleccion = remember(valores) { mutableStateOf<Int?>(null) }
    val medidor = rememberTextMeasurer()
    val haptico = LocalHapticFeedback.current

    Column(modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(altura)
                .padding(top = 6.dp)
                .tocarParaSeleccionar(seleccion, valores.size) {
                    haptico.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
        ) {
            val n = valores.size
            if (n == 0) return@Canvas
            val elegido = seleccion.value
            for (i in 0..3) {
                val y = size.height * i / 3f
                drawLine(lineaGuia, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
            }
            // Centramos cada punto en su "columna" para alinear con las etiquetas
            val espacio = size.width / n
            val puntos = valores.mapIndexed { i, v ->
                Offset(
                    x = i * espacio + espacio / 2f,
                    y = size.height - (v / maximo).toFloat() * size.height * 0.92f
                )
            }
            val linea = Path().apply {
                puntos.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
            }
            val area = Path().apply {
                addPath(linea)
                lineTo(puntos.last().x, size.height)
                lineTo(puntos.first().x, size.height)
                close()
            }
            drawPath(
                area,
                brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.30f), color.copy(alpha = 0.02f)))
            )

            // Línea punteada vertical en el día elegido
            if (elegido != null && elegido < n) {
                drawLine(
                    color = color.copy(alpha = 0.55f),
                    start = Offset(puntos[elegido].x, 0f),
                    end = Offset(puntos[elegido].x, size.height),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))
                )
            }

            drawPath(linea, color = color, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
            puntos.forEachIndexed { i, p ->
                if (i == elegido) {
                    // Punto elegido: más grande y con un halo
                    drawCircle(color.copy(alpha = 0.18f), radius = 10.dp.toPx(), center = p)
                    drawCircle(fondoPunto, radius = 5.5.dp.toPx(), center = p)
                    drawCircle(color, radius = 4.dp.toPx(), center = p)
                } else {
                    drawCircle(fondoPunto, radius = 4.dp.toPx(), center = p)
                    drawCircle(color, radius = 2.6.dp.toPx(), center = p)
                }
            }

            if (elegido != null && elegido < n) {
                dibujarGlobo(
                    medidor = medidor,
                    fondo = fondoGlobo,
                    titulo = etiquetasGlobo.getOrElse(elegido) { "" },
                    valor = formatoValor(valores[elegido]),
                    ancla = puntos[elegido]
                )
            }
        }
        EtiquetasEje(etiquetas, seleccion.value)
    }
}

/**
 * Detecta toques y deslizamientos horizontales sobre la gráfica y guarda
 * en [seleccion] el índice de la barra/punto que quedó bajo el dedo.
 */
private fun Modifier.tocarParaSeleccionar(
    seleccion: MutableState<Int?>,
    n: Int,
    alCambiarConDedo: () -> Unit
): Modifier = this
    .pointerInput(seleccion, n) {
        detectTapGestures { posicion ->
            val i = indiceEnX(posicion.x, size.width.toFloat(), n) ?: return@detectTapGestures
            // Tocar el mismo elemento otra vez el globito
            seleccion.value = if (seleccion.value == i) null else i
        }
    }
    .pointerInput(seleccion, n) {
        detectHorizontalDragGestures(
            onDragStart = { posicion ->
                seleccion.value = indiceEnX(posicion.x, size.width.toFloat(), n)
            },
            onHorizontalDrag = { cambio, _ ->
                cambio.consume()
                val i = indiceEnX(cambio.position.x, size.width.toFloat(), n)
                if (i != null && i != seleccion.value) {
                    seleccion.value = i
                    alCambiarConDedo()
                }
            }
        )
    }

/** Convierte la posición X del dedo en el índice de la barra/punto (o null si no hay datos). */
private fun indiceEnX(x: Float, ancho: Float, n: Int): Int? {
    if (n <= 0 || ancho <= 0f) return null
    return (x / (ancho / n)).toInt().coerceIn(0, n - 1)
}

/**
 * Dibuja el globito con el día y el valor. Va arriba del punto [ancla];
 * si no cabe arriba (barra muy alta), se pone abajo.
 */
private fun DrawScope.dibujarGlobo(
    medidor: TextMeasurer,
    fondo: Color,
    titulo: String,
    valor: String,
    ancla: Offset
) {
    val texto = buildAnnotatedString {
        withStyle(SpanStyle(color = Greon.VerdeClaro, fontSize = 10.sp, fontWeight = FontWeight.Medium)) {
            append(titulo)
        }
        append("\n")
        withStyle(SpanStyle(color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)) {
            append(valor)
        }
    }
    val medida = medidor.measure(
        text = texto,
        style = TextStyle(fontFamily = PlusJakartaSans, textAlign = TextAlign.Center)
    )

    val rellenoH = 10.dp.toPx()
    val rellenoV = 6.dp.toPx()
    val pico = 5.dp.toPx()
    val separacion = 6.dp.toPx()
    val esquina = 8.dp.toPx()

    val ancho = medida.size.width + rellenoH * 2
    val alto = medida.size.height + rellenoV * 2
    val x = (ancla.x - ancho / 2f).coerceIn(0f, (size.width - ancho).coerceAtLeast(0f))
    val cabeArriba = ancla.y - separacion - pico - alto >= 0f
    val y = if (cabeArriba) ancla.y - separacion - pico - alto else ancla.y + separacion + pico

    drawRoundRect(
        color = fondo,
        topLeft = Offset(x, y),
        size = Size(ancho, alto),
        cornerRadius = CornerRadius(esquina, esquina)
    )

    // Piquito que apunta a la barra/punto
    val picoX = ancla.x.coerceIn(x + esquina, maxOf(x + esquina, x + ancho - esquina))
    val triangulo = Path().apply {
        if (cabeArriba) {
            moveTo(picoX - pico, y + alto)
            lineTo(picoX + pico, y + alto)
            lineTo(picoX, y + alto + pico)
        } else {
            moveTo(picoX - pico, y)
            lineTo(picoX + pico, y)
            lineTo(picoX, y - pico)
        }
        close()
    }
    drawPath(triangulo, color = fondo)

    drawText(medida, topLeft = Offset(x + rellenoH, y + rellenoV))
}

@Composable
private fun EtiquetasEje(etiquetas: List<String>, elegido: Int? = null) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp)) {
        etiquetas.forEachIndexed { i, e ->
            // Con 14 días mostramos una etiqueta sí y otra no para que quepan,
            // pero la del día elegido siempre se ve (y resaltada)
            val esElegida = i == elegido
            val mostrar = esElegida || etiquetas.size <= 8 || i % 2 == (etiquetas.size - 1) % 2
            Text(
                if (mostrar) e else "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 9.sp,
                fontWeight = if (esElegida) FontWeight.Bold else FontWeight.Normal,
                color = if (esElegida) Greon.TextoTitulo else Greon.TextoSuave,
                maxLines = 1
            )
        }
    }
}

/** Barras horizontales con etiqueta y número (para roles y tipos). */
@Composable
fun BarrasHorizontales(
    datos: List<Pair<String, Int>>,
    colores: List<Color> = listOf(Greon.Verde, Greon.Azul, Greon.Ambar, Greon.Morado, Greon.Turquesa, Greon.Gris)
) {
    val maximo = (datos.maxOfOrNull { it.second } ?: 0).coerceAtLeast(1)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        datos.forEachIndexed { i, (etiqueta, valor) ->
            val color = colores[i % colores.size]
            Column {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        etiqueta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Greon.Texto,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(Formato.entero(valor), style = MaterialTheme.typography.labelLarge, color = color)
                }
                Spacer(Modifier.height(5.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Greon.VerdeMuyClaro)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(valor.toFloat() / maximo)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(color)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
//  Estados de carga / error / vacío
// ---------------------------------------------------------------------
@Composable
fun Cargando(texto: String = "Cargando…") {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = Greon.Verde)
        Spacer(Modifier.height(12.dp))
        Text(texto, color = Greon.TextoSuave, style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * Pantalla de error con Greon triste, como en la web.
 * Si el problema es de conexion, el titulo lo dice claro.
 */
@Composable
fun ErrorEstado(mensaje: String, alReintentar: () -> Unit) {
    val sinConexion = mensaje.contains("conexión", ignoreCase = true) ||
        mensaje.contains("internet", ignoreCase = true)
    // Centrado en la pantalla, pero con scroll si no cabe (por ejemplo, en horizontal):
    // el Box centra la columna y la columna solo hace scroll si no cabe.
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ContenidoError(sinConexion, mensaje, alReintentar)
        }
    }
}

@Composable
private fun ContenidoError(sinConexion: Boolean, mensaje: String, alReintentar: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        MascotaGreon(ancho = 150.dp, animo = AnimoGreon.Triste)
        Spacer(Modifier.height(18.dp))
        Text(
            if (sinConexion) "Greon no pudo conectarse" else "No se pudieron cargar los datos",
            style = MaterialTheme.typography.titleLarge,
            color = Greon.TextoTitulo,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(mensaje, style = MaterialTheme.typography.bodyMedium, color = Greon.TextoSuave, textAlign = TextAlign.Center)
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = alReintentar,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Greon.Verde, contentColor = Color.White)
        ) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}

/**
 * Estado vacio con Greon a un lado, como "device-empty" en la web.
 *
 * @param titulo texto en negritas (opcional).
 * @param compacto version mas chica para dentro de tarjetas (por ejemplo, las graficas).
 */
@Composable
fun Vacio(
    texto: String,
    modifier: Modifier = Modifier,
    titulo: String? = null,
    compacto: Boolean = false
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = if (compacto) 6.dp else 24.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MascotaGreon(ancho = if (compacto) 72.dp else 108.dp, flotar = !compacto)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            if (titulo != null) {
                Text(titulo, style = MaterialTheme.typography.titleSmall, color = Greon.TextoTitulo)
                Spacer(Modifier.height(3.dp))
            }
            Text(texto, style = MaterialTheme.typography.bodyMedium, color = Greon.TextoSuave)
        }
    }
}

/**
 * Muestra skeleton / error / contenido según el estado,
 * con un desvanecido suave al cambiar de uno a otro.
 */
@Composable
fun <T> ContenidoCarga(
    estado: Carga<T>,
    alReintentar: () -> Unit,
    cargando: @Composable () -> Unit = { Cargando() },
    contenido: @Composable (T) -> Unit
) {
    val fase = when (estado) {
        is Carga.Cargando -> 0
        is Carga.Error -> 1
        is Carga.Listo -> 2
    }
    Crossfade(targetState = fase, animationSpec = tween(350), label = "carga") { f ->
        when (f) {
            0 -> cargando()
            1 -> (estado as? Carga.Error)?.let { ErrorEstado(it.mensaje, alReintentar) }
            else -> (estado as? Carga.Listo)?.let { contenido(it.datos) }
        }
    }
}
