package com.greontrack.admin.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greontrack.admin.R
import com.greontrack.admin.data.Carga
import com.greontrack.admin.ui.theme.Greon
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            if (titulo != null) {
                Text(titulo, style = MaterialTheme.typography.titleMedium, color = Greon.VerdeOscuro)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
fun MiniDato(valor: String, etiqueta: String, modifier: Modifier = Modifier, color: Color = Greon.VerdeOscuro) {
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
// ---------------------------------------------------------------------
@Composable
fun GraficaBarras(
    valores: List<Double>,
    etiquetas: List<String>,
    color: Color,
    modifier: Modifier = Modifier
) {
    val maximo = (valores.maxOrNull() ?: 0.0).coerceAtLeast(1.0)
    val lineaGuia = Greon.VerdeOscuro.copy(alpha = 0.08f)
    Column(modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val n = valores.size
            if (n == 0) return@Canvas
            for (i in 0..3) {
                val y = size.height * i / 3f
                drawLine(lineaGuia, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
            }
            val espacio = size.width / n
            val ancho = espacio * 0.6f
            valores.forEachIndexed { i, v ->
                val alto = ((v / maximo).toFloat() * size.height).coerceAtLeast(3.dp.toPx())
                drawRoundRect(
                    color = if (v > 0) color else color.copy(alpha = 0.18f),
                    topLeft = Offset(i * espacio + (espacio - ancho) / 2f, size.height - alto),
                    size = Size(ancho, alto),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
        EtiquetasEje(etiquetas)
    }
}

@Composable
fun GraficaLinea(
    valores: List<Double>,
    etiquetas: List<String>,
    color: Color,
    modifier: Modifier = Modifier
) {
    val maximo = (valores.maxOrNull() ?: 0.0).coerceAtLeast(0.001)
    val lineaGuia = Greon.VerdeOscuro.copy(alpha = 0.08f)
    Column(modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(top = 6.dp)
        ) {
            val n = valores.size
            if (n == 0) return@Canvas
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
            drawPath(linea, color = color, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
            puntos.forEach { p ->
                drawCircle(Color.White, radius = 4.dp.toPx(), center = p)
                drawCircle(color, radius = 2.6.dp.toPx(), center = p)
            }
        }
        EtiquetasEje(etiquetas)
    }
}

@Composable
private fun EtiquetasEje(etiquetas: List<String>) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp)) {
        etiquetas.forEachIndexed { i, e ->
            // Con 14 días mostramos una etiqueta sí y otra no para que quepan
            val mostrar = etiquetas.size <= 8 || i % 2 == (etiquetas.size - 1) % 2
            Text(
                if (mostrar) e else "",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 9.sp,
                color = Greon.TextoSuave,
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

@Composable
fun ErrorEstado(mensaje: String, alReintentar: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.CloudOff, contentDescription = null, tint = Greon.Error, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text("No se pudieron cargar los datos", style = MaterialTheme.typography.titleMedium, color = Greon.Texto)
        Spacer(Modifier.height(6.dp))
        Text(mensaje, style = MaterialTheme.typography.bodyMedium, color = Greon.TextoSuave, textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        Button(onClick = alReintentar) { Text("Reintentar") }
    }
}

@Composable
fun Vacio(texto: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.Inbox, contentDescription = null, tint = Greon.Gris, modifier = Modifier.size(40.dp))
        Spacer(Modifier.height(8.dp))
        Text(texto, color = Greon.TextoSuave, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
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

@Composable
fun LeyendaPunto(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.labelSmall, color = Greon.TextoSuave)
    }
}