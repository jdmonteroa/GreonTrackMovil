package com.greontrack.admin.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.greontrack.admin.data.DispositivoAdmin
import com.greontrack.admin.ui.DispositivosViewModel
import com.greontrack.admin.ui.components.ContenidoCarga
import com.greontrack.admin.ui.components.SkeletonLista
import com.greontrack.admin.ui.components.Etiqueta
import com.greontrack.admin.ui.components.GreonTopBar
import com.greontrack.admin.ui.components.MiniDato
import com.greontrack.admin.ui.components.Vacio
import com.greontrack.admin.ui.theme.Greon
import com.greontrack.admin.util.Formato

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispositivosScreen(
    alAbrirUsuario: (String) -> Unit,
    vm: DispositivosViewModel = viewModel()
) {
    LaunchedEffect(Unit) { vm.cargar() }

    Column(Modifier.fillMaxSize()) {
        GreonTopBar(titulo = "Dispositivos", subtitulo = "Aparatos registrados por todos los usuarios", alRefrescar = { vm.cargar() })

        ContenidoCarga(vm.estado, alReintentar = { vm.cargar() }, cargando = { SkeletonLista(avatarRedondo = false) }) { lista ->
            val texto = vm.busqueda.trim()
            val filtrados = lista.filter { d ->
                (vm.filtroOrigen == null || d.origen == vm.filtroOrigen) &&
                    (texto.isEmpty() ||
                        d.nombre.contains(texto, true) ||
                        d.tipo.contains(texto, true) ||
                        (d.usuarioNombre?.contains(texto, true) == true))
            }

            Column(Modifier.fillMaxSize()) {
                BuscadorYFiltros(
                    busqueda = vm.busqueda,
                    alBuscar = { vm.busqueda = it },
                    placeholder = "Buscar aparato, tipo o dueño",
                    opciones = listOf(
                        null to "Todos (${lista.size})",
                        "manual" to "Manual",
                        "agente" to "Agente / sniffer"
                    ),
                    seleccion = vm.filtroOrigen,
                    alSeleccionar = { vm.filtroOrigen = it }
                )

                PullToRefreshBox(
                    isRefreshing = vm.refrescando,
                    onRefresh = { vm.cargar() },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                MiniDato(Formato.entero(filtrados.size), "Mostrados", Modifier.weight(1f))
                                MiniDato(
                                    Formato.compacto(filtrados.sumOf { it.consumoWatts } / 1000.0) + " kW",
                                    "Potencia total",
                                    Modifier.weight(1f),
                                    Greon.Ambar
                                )
                                MiniDato(
                                    Formato.compacto(filtrados.sumOf { it.kwhTotal }),
                                    "kWh registrados",
                                    Modifier.weight(1f),
                                    Greon.Verde
                                )
                            }
                        }
                        if (filtrados.isEmpty()) {
                            item { Vacio("No hay dispositivos que coincidan.") }
                        }
                        items(filtrados, key = { it.id }) { d ->
                            FilaDispositivo(d) { d.usuarioId?.let(alAbrirUsuario) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaDispositivo(d: DispositivoAdmin, alTocarDueno: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoDispositivo(d.tipo)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        d.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        color = Greon.Texto,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${d.tipo} · ${Formato.entero(Math.round(d.consumoWatts))} W",
                        style = MaterialTheme.typography.bodySmall,
                        color = Greon.TextoSuave,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Etiqueta(
                    if (d.origen == "agente") "Agente" else "Manual",
                    if (d.origen == "agente") Greon.Turquesa else Greon.Gris
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier
                        .weight(1f)
                        .clickable(onClick = alTocarDueno),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Person, contentDescription = null, tint = Greon.VerdeMedio, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        d.usuarioNombre ?: "Usuario desconocido",
                        style = MaterialTheme.typography.labelMedium,
                        color = Greon.Verde,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    "${Formato.decimal(d.kwhTotal, 1)} kWh · ${Formato.fecha(d.createdAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Greon.TextoSuave
                )
            }
        }
    }
}
