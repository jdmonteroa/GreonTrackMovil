package com.greontrack.admin.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.greontrack.admin.R
import com.greontrack.admin.data.DashboardStats
import com.greontrack.admin.ui.DashboardViewModel
import com.greontrack.admin.ui.components.BarrasHorizontales
import com.greontrack.admin.ui.components.ContenidoCarga
import com.greontrack.admin.ui.components.GraficaBarras
import com.greontrack.admin.ui.components.GraficaLinea
import com.greontrack.admin.ui.components.GreonTopBar
import com.greontrack.admin.ui.components.KpiCard
import com.greontrack.admin.ui.components.Tarjeta
import com.greontrack.admin.ui.components.Vacio
import com.greontrack.admin.ui.theme.Greon
import com.greontrack.admin.util.Formato

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: DashboardViewModel = viewModel()) {
    LaunchedEffect(Unit) { vm.cargar() }

    Column(Modifier.fillMaxSize()) {
        GreonTopBar(titulo = "Panel", subtitulo = "Resumen general de GreonTrack", alRefrescar = { vm.cargar() })
        ContenidoCarga(vm.estado, alReintentar = { vm.cargar() }) { datos ->
            PullToRefreshBox(
                isRefreshing = vm.refrescando,
                onRefresh = { vm.cargar() },
                modifier = Modifier.fillMaxSize()
            ) {
                ContenidoDashboard(datos)
            }
        }
    }
}

@Composable
private fun ContenidoDashboard(d: DashboardStats) {
    // Tarjetas KPI: (icono, etiqueta, valor, color, detalle)
    val kpis = listOf(
        Kpi(Icons.Rounded.Group, "Usuarios registrados", Formato.entero(d.usuariosTotal), Greon.Verde,
            "+${Formato.entero(d.usuariosNuevos7d)} esta semana"),
        Kpi(Icons.Rounded.PersonAdd, "Activos (7 días)", Formato.entero(d.usuariosActivos7d), Greon.Azul,
            porcentaje(d.usuariosActivos7d, d.usuariosTotal) + " del total"),
        Kpi(Icons.Rounded.Devices, "Dispositivos", Formato.entero(d.dispositivosTotal), Greon.Turquesa,
            "${Formato.entero(d.dispositivosAgente)} detectados por agente"),
        Kpi(Icons.Rounded.History, "Registros de uso", Formato.entero(d.registrosUsoTotal), Greon.Morado, null),
        Kpi(Icons.Rounded.Bolt, "kWh registrados", Formato.compacto(d.kwhTotal), Greon.Ambar,
            "${Formato.compacto(d.kwh30d)} kWh en 30 días"),
        Kpi(Icons.Rounded.Savings, "Costo estimado", Formato.dinero(d.costoTotal), Greon.Verde, "MXN acumulado"),
        Kpi(Icons.Rounded.Eco, "CO₂ estimado", Formato.compacto(d.co2Total) + " kg", Greon.VerdeOscuro, null),
        Kpi(Icons.Rounded.Forum, "Consultas a Greon", Formato.entero(d.consultasGreonTotal), Greon.Azul, null),
        Kpi(Icons.Rounded.Sensors, "Sniffers activos", Formato.entero(d.sniffersActivos), Greon.Turquesa,
            "Últimas 24 horas"),
        Kpi(Icons.Rounded.TipsAndUpdates, "Sugerencias pendientes", Formato.entero(d.sugeridosPendientes), Greon.Ambar,
            "${Formato.entero(d.recomendacionesTotal)} recomendaciones")
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item { Bienvenida(d) }

        val filas = kpis.chunked(2)
        items(filas.size) { fila ->
            val par = filas[fila]
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                par.forEach { k ->
                    KpiCard(k.icono, k.etiqueta, k.valor, k.color, Modifier.weight(1f), k.detalle)
                }
                if (par.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        item {
            Tarjeta(titulo = "Nuevos usuarios", descripcion = "Registros por día · últimos 14 días") {
                if (d.registrosPorDia.isEmpty()) Vacio("Sin datos")
                else GraficaBarras(
                    valores = d.registrosPorDia.map { it.total },
                    etiquetas = d.registrosPorDia.map { Formato.diaCorto(it.dia) },
                    color = Greon.Verde
                )
            }
        }

        item {
            Tarjeta(titulo = "Consumo registrado", descripcion = "kWh por día · últimos 14 días") {
                if (d.kwhPorDia.isEmpty()) Vacio("Sin datos")
                else {
                    val total = d.kwhPorDia.sumOf { it.total }
                    Text(
                        "${Formato.decimal(total, 1)} kWh en el periodo",
                        style = MaterialTheme.typography.labelMedium,
                        color = Greon.Ambar
                    )
                    Spacer(Modifier.height(8.dp))
                    GraficaLinea(
                        valores = d.kwhPorDia.map { it.total },
                        etiquetas = d.kwhPorDia.map { Formato.diaCorto(it.dia) },
                        color = Greon.Ambar
                    )
                }
            }
        }

        item {
            Tarjeta(titulo = "Usuarios por rol") {
                if (d.roles.isEmpty()) Vacio("Sin usuarios")
                else BarrasHorizontales(d.roles.map { Formato.nombreRol(it.nombre ?: "general") to it.total })
            }
        }

        item {
            Tarjeta(titulo = "Dispositivos por tipo", descripcion = "Los 6 tipos más registrados") {
                if (d.dispositivosPorTipo.isEmpty()) Vacio("Aún no hay dispositivos")
                else BarrasHorizontales(
                    d.dispositivosPorTipo.map { (it.nombre ?: "Otro").replaceFirstChar { c -> c.uppercase() } to it.total }
                )
            }
        }

        item {
            Tarjeta(titulo = "Parámetros de cálculo") {
                Text(
                    "Tarifa: ${d.tarifaKwh?.let { Formato.dinero(it) } ?: "—"} por kWh",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Factor de emisión: ${d.factorCo2?.let { Formato.decimal(it, 3) } ?: "—"} kg CO₂ por kWh",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Puedes cambiarlos en Ajustes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Greon.TextoSuave
                )
            }
        }
    }
}

@Composable
private fun Bienvenida(d: DashboardStats) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Greon.Verde, Greon.VerdeOscuro)))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(Formato.hoyLargo(), style = MaterialTheme.typography.labelMedium, color = Greon.VerdeClaro)
                Spacer(Modifier.height(4.dp))
                Text("¡Hola, admin!", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Hoy GreonTrack tiene ${Formato.entero(d.usuariosTotal)} usuarios que han " +
                        "registrado ${Formato.compacto(d.kwhTotal)} kWh de consumo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Image(
                painter = painterResource(R.drawable.mascota_greon),
                contentDescription = "Greon, la mascota",
                modifier = Modifier.size(104.dp)
            )
        }
    }
}

private data class Kpi(
    val icono: androidx.compose.ui.graphics.vector.ImageVector,
    val etiqueta: String,
    val valor: String,
    val color: Color,
    val detalle: String?
)

private fun porcentaje(parte: Int, total: Int): String =
    if (total <= 0) "0%" else "${Math.round(parte * 100.0 / total)}%"
