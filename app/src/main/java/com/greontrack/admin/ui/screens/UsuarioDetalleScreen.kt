package com.greontrack.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Kitchen
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.LocalLaundryService
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Microwave
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Router
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.greontrack.admin.data.AdminRepository
import com.greontrack.admin.data.DispositivoUsuario
import com.greontrack.admin.data.UsuarioDetalle
import com.greontrack.admin.ui.UsuarioDetalleViewModel
import com.greontrack.admin.ui.components.Avatar
import com.greontrack.admin.ui.components.ContenidoCarga
import com.greontrack.admin.ui.components.Etiqueta
import com.greontrack.admin.ui.components.GraficaLinea
import com.greontrack.admin.ui.components.GreonTopBar
import com.greontrack.admin.ui.components.MiniDato
import com.greontrack.admin.ui.components.SkeletonDetalle
import com.greontrack.admin.ui.components.Tarjeta
import com.greontrack.admin.ui.components.Vacio
import com.greontrack.admin.ui.components.aparecer
import com.greontrack.admin.ui.components.colorRol
import com.greontrack.admin.ui.components.rememberEntradaEscalonada
import com.greontrack.admin.ui.theme.Greon
import com.greontrack.admin.util.Formato
import kotlin.math.roundToLong

@Composable
fun UsuarioDetalleScreen(
    usuarioId: String,
    alVolver: () -> Unit,
    vm: UsuarioDetalleViewModel = viewModel()
) {
    LaunchedEffect(usuarioId) {
        vm.usuarioId = usuarioId
        vm.cargar()
    }

    Column(Modifier.fillMaxSize()) {
        GreonTopBar(
            titulo = "Detalle de usuario",
            navigationIcon = {
                IconButton(onClick = alVolver) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
            },
            alRefrescar = { vm.cargar() }
        )
        ContenidoCarga(vm.estado, alReintentar = { vm.cargar() }, cargando = { SkeletonDetalle() }) { u ->
            ContenidoDetalle(u, vm)
        }
    }

    vm.mensaje?.let { texto ->
        AlertDialog(
            onDismissRequest = { vm.mensaje = null },
            confirmButton = { TextButton(onClick = { vm.mensaje = null }) { Text("Aceptar") } },
            title = { Text("Usuarios") },
            text = { Text(texto) }
        )
    }
}

@Composable
private fun ContenidoDetalle(u: UsuarioDetalle, vm: UsuarioDetalleViewModel) {
    val abajo = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // Las tarjetas aparecen una tras otra, igual que en el Panel
    val entrada = rememberEntradaEscalonada()

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp + abajo),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Encabezado con degradado verde, rol y ranking
        item { EncabezadoUsuario(u, Modifier.aparecer(entrada, 0)) }

        // Mini grafica de consumo de los ultimos 14 dias
        item { TarjetaConsumo(u, Modifier.aparecer(entrada, 1)) }

        // Numeros del usuario
        item {
            Tarjeta(modifier = Modifier.aparecer(entrada, 2), titulo = "Actividad") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniDato(Formato.entero(u.dispositivos.size), "Dispositivos", Modifier.weight(1f))
                    MiniDato(Formato.entero(u.registrosUso), "Registros", Modifier.weight(1f))
                    MiniDato(Formato.entero(u.greonConsultas), "Consultas", Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniDato(Formato.compacto(u.kwhTotal), "kWh", Modifier.weight(1f), Greon.Ambar)
                    MiniDato(Formato.dinero(u.costoTotal), "Costo", Modifier.weight(1f), Greon.Verde)
                    MiniDato(Formato.compacto(u.co2Total) + " kg", "CO₂", Modifier.weight(1f))
                }
            }
        }

        // Datos de la cuenta
        item {
            Tarjeta(modifier = Modifier.aparecer(entrada, 3), titulo = "Información") {
                FilaInfo(Icons.Rounded.Schedule, "Registrado", Formato.fecha(u.createdAt))
                FilaInfo(Icons.Rounded.TouchApp, "Último acceso", Formato.relativo(u.ultimoAcceso))
                FilaInfo(
                    Icons.Rounded.CheckCircle,
                    "Aviso de privacidad",
                    if (u.avisoAceptadoEn != null) "Aceptado el ${Formato.fecha(u.avisoAceptadoEn)}" else "Pendiente"
                )
                FilaInfo(
                    Icons.Rounded.Sensors,
                    "Sniffer",
                    if (u.snifferUltimoSondeo != null) "Último sondeo: ${Formato.relativo(u.snifferUltimoSondeo)}"
                    else "No instalado"
                )
            }
        }

        // Cambiar rol
        item { CambiarRol(u, vm, Modifier.aparecer(entrada, 4)) }

        // Dispositivos
        item {
            Text(
                "Dispositivos (${u.dispositivos.size})",
                style = MaterialTheme.typography.titleMedium,
                color = Greon.TextoTitulo,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (u.dispositivos.isEmpty()) {
            item { Vacio(titulo = "Aún no hay dispositivos", texto = "Este usuario todavía no registra aparatos.") }
        }
        items(u.dispositivos.size) { i -> FilaDispositivoUsuario(u.dispositivos[i]) }
    }
}

/** Encabezado verde con el avatar, el correo, el rol y el lugar en consumo. */
@Composable
private fun EncabezadoUsuario(u: UsuarioDetalle, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Greon.Verde, Greon.VerdeOscuro)))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(u.nombre, Color.White, tamano = 64.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    u.nombre.ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    u.email ?: "—",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Greon.VerdeClaro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PildoraEncabezado(iconoRol(u.role), Formato.nombreRol(u.role))
            val lugar = u.rankingConsumo
            if (lugar != null) {
                PildoraEncabezado(
                    icono = if (lugar <= 3 && u.kwhTotal > 0) Icons.Rounded.EmojiEvents else Icons.Rounded.Leaderboard,
                    texto = textoRanking(lugar, u.usuariosTotal, u.kwhTotal),
                    destacada = lugar <= 3 && u.kwhTotal > 0
                )
            }
        }
    }
}

/** "N.º 1 en consumo", "Top 3 en consumo", "Lugar 12 de 40 en consumo"... */
internal fun textoRanking(lugar: Int, total: Int, kwh: Double): String = when {
    kwh <= 0.0 -> "Sin consumo registrado"
    lugar == 1 -> "N.º 1 en consumo"
    lugar <= 3 -> "Top 3 en consumo"
    lugar <= 10 -> "Top 10 en consumo"
    else -> "Lugar $lugar de $total en consumo"
}

private fun iconoRol(rol: String): ImageVector = when (rol) {
    "admin" -> Icons.Rounded.AdminPanelSettings
    "analitico" -> Icons.Rounded.Insights
    else -> Icons.Rounded.Person
}

/** Etiqueta redondeada para el encabezado verde. La destacada va en ambar. */
@Composable
private fun PildoraEncabezado(icono: ImageVector, texto: String, destacada: Boolean = false) {
    val fondo = if (destacada) Greon.Ambar else Color.White.copy(alpha = 0.16f)
    val color = if (destacada) Greon.VerdeOscuro2 else Color.White
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = color)
    }
}

/** Mini grafica con los kWh por dia de este usuario. */
@Composable
private fun TarjetaConsumo(u: UsuarioDetalle, modifier: Modifier = Modifier) {
    Tarjeta(
        modifier = modifier,
        titulo = "Consumo reciente",
        descripcion = "kWh por día · últimos 14 días · toca un punto"
    ) {
        val dias = u.kwhPorDia
        if (dias == null) {
            // La funcion de Supabase todavia es la version anterior
            Vacio(
                titulo = "Falta actualizar Supabase",
                texto = "Vuelve a ejecutar supabase/admin_setup.sql en el SQL Editor para ver esta gráfica.",
                compacto = true
            )
        } else if (dias.all { it.total <= 0.0 }) {
            Vacio("Este usuario no registró consumo en estos 14 días.", compacto = true)
        } else {
            Text(
                "${Formato.decimal(dias.sumOf { it.total }, 1)} kWh en el periodo",
                style = MaterialTheme.typography.labelMedium,
                color = Greon.Ambar
            )
            Spacer(Modifier.height(8.dp))
            GraficaLinea(
                valores = dias.map { it.total },
                etiquetas = dias.map { Formato.diaCorto(it.dia) },
                etiquetasGlobo = dias.map { Formato.diaMedio(it.dia) },
                color = Greon.Ambar,
                formatoValor = { "${Formato.decimal(it, 2)} kWh" },
                altura = 120.dp
            )
        }
    }
}

@Composable
private fun CambiarRol(u: UsuarioDetalle, vm: UsuarioDetalleViewModel, modifier: Modifier = Modifier) {
    var elegido by remember(u.role) { mutableStateOf(u.role) }
    var confirmar by remember { mutableStateOf(false) }
    val esYoMismo = AdminRepository.idActual() == u.id

    Tarjeta(
        modifier = modifier,
        titulo = "Rol del usuario",
        descripcion = "Admin: entra a esta app. Analítico: acceso a reportes. General: usuario normal."
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("general", "analitico", "admin").forEach { rol ->
                FilterChip(
                    selected = elegido == rol,
                    onClick = { elegido = rol },
                    enabled = !esYoMismo,
                    label = { Text(Formato.nombreRol(rol)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colorRol(rol),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        if (esYoMismo) {
            Spacer(Modifier.height(6.dp))
            Text(
                "Esta es tu cuenta: no puedes cambiar tu propio rol.",
                style = MaterialTheme.typography.bodySmall,
                color = Greon.TextoSuave
            )
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { confirmar = true },
            enabled = elegido != u.role && !vm.guardando,
            colors = ButtonDefaults.buttonColors(containerColor = Greon.Verde),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (vm.guardando) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            } else {
                Text("Guardar rol")
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("¿Cambiar rol?") },
            text = {
                Text(
                    "${u.nombre} pasará de ${Formato.nombreRol(u.role)} a ${Formato.nombreRol(elegido)}." +
                        if (elegido == "admin") "\n\nPodrá entrar a esta app y ver los datos de todos los usuarios." else ""
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    vm.cambiarRol(elegido)
                }) { Text("Sí, cambiar") }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun FilaInfo(icono: ImageVector, etiqueta: String, valor: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = Greon.VerdeMedio, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = Greon.TextoSuave, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.bodyMedium, color = Greon.Texto)
    }
}

@Composable
private fun FilaDispositivoUsuario(d: DispositivoUsuario) {
    Tarjeta {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconoDispositivo(d.tipo)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(d.nombre, style = MaterialTheme.typography.titleSmall, color = Greon.Texto, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${d.tipo.replaceFirstChar { it.uppercase() }} · ${Formato.entero(d.consumoWatts.roundToLong())} W",
                    style = MaterialTheme.typography.bodySmall,
                    color = Greon.TextoSuave
                )
            }
            Etiqueta(
                if (d.origen == "agente") "Agente" else "Manual",
                if (d.origen == "agente") Greon.Turquesa else Greon.Gris
            )
        }
    }
}

/** Ícono dentro de un cuadrito de color, según el tipo de aparato. */
@Composable
fun IconoDispositivo(tipo: String) {
    val (icono, color) = iconoPorTipo(tipo)
    Box(
        Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
    }
}

fun iconoPorTipo(tipo: String): Pair<ImageVector, Color> {
    val t = tipo.lowercase()
    fun tiene(vararg palabras: String) = palabras.any { it in t }
    return when {
        tiene("refri", "congelador", "nevera") -> Icons.Rounded.Kitchen to Greon.Azul
        tiene("micro", "horno", "estufa", "tostador", "licuadora", "hervidor", "arrocera", "cafetera", "vajilla") ->
            Icons.Rounded.Microwave to Greon.Ambar
        tiene("lavadora", "secadora", "plancha", "aspiradora") -> Icons.Rounded.LocalLaundryService to Greon.Turquesa
        tiene("aire", "ventilador", "calefactor", "deshumid", "calentador", "bomba", "clima") -> Icons.Rounded.AcUnit to Greon.Azul
        tiene("tele", "tv", "proyector") -> Icons.Rounded.Tv to Greon.Morado
        tiene("consola", "videojuego") -> Icons.Rounded.SportsEsports to Greon.Morado
        tiene("bocina", "estéreo", "estereo") -> Icons.Rounded.Speaker to Greon.Morado
        tiene("laptop", "compu", "impresora", "monitor") -> Icons.Rounded.Computer to Greon.Azul
        tiene("router", "módem", "modem", "wifi") -> Icons.Rounded.Router to Greon.Turquesa
        tiene("foco", "luz", "lámpara", "lampara", "ilumin") -> Icons.Rounded.Lightbulb to Greon.Ambar
        tiene("celular", "teléfono", "telefono", "tablet", "móvil", "movil", "cargador") ->
            Icons.Rounded.Smartphone to Greon.Verde
        else -> Icons.Rounded.Memory to Greon.Verde
    }
}
