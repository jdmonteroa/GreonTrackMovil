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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.greontrack.admin.data.UsuarioResumen
import com.greontrack.admin.ui.UsuariosViewModel
import com.greontrack.admin.ui.components.Avatar
import com.greontrack.admin.ui.components.ContenidoCarga
import com.greontrack.admin.ui.components.GreonTopBar
import com.greontrack.admin.ui.components.RolChip
import com.greontrack.admin.ui.components.Vacio
import com.greontrack.admin.ui.components.colorRol
import com.greontrack.admin.ui.theme.Greon
import com.greontrack.admin.util.Formato

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    alAbrirUsuario: (String) -> Unit,
    vm: UsuariosViewModel = viewModel()
) {
    LaunchedEffect(Unit) { vm.cargar() }

    Column(Modifier.fillMaxSize()) {
        GreonTopBar(titulo = "Usuarios", subtitulo = "Cuentas registradas en la app web", alRefrescar = { vm.cargar() })

        ContenidoCarga(vm.estado, alReintentar = { vm.cargar() }) { usuarios ->
            val texto = vm.busqueda.trim()
            val filtrados = usuarios.filter { u ->
                (vm.filtroRol == null || u.role == vm.filtroRol) &&
                    (texto.isEmpty() ||
                        u.nombre.contains(texto, ignoreCase = true) ||
                        (u.email?.contains(texto, ignoreCase = true) == true))
            }

            Column(Modifier.fillMaxSize()) {
                BuscadorYFiltros(
                    busqueda = vm.busqueda,
                    alBuscar = { vm.busqueda = it },
                    placeholder = "Buscar por nombre o correo",
                    opciones = listOf(
                        null to "Todos (${usuarios.size})",
                        "admin" to "Admin",
                        "analitico" to "Analítico",
                        "general" to "General"
                    ),
                    seleccion = vm.filtroRol,
                    alSeleccionar = { vm.filtroRol = it }
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
                            Text(
                                "${filtrados.size} resultado" + if (filtrados.size == 1) "" else "s",
                                style = MaterialTheme.typography.labelMedium,
                                color = Greon.TextoSuave
                            )
                        }
                        if (filtrados.isEmpty()) {
                            item { Vacio("No hay usuarios que coincidan con la búsqueda.") }
                        }
                        items(filtrados, key = { it.id }) { u ->
                            FilaUsuario(u) { alAbrirUsuario(u.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaUsuario(u: UsuarioResumen, alTocar: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = alTocar)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Avatar(u.nombre, colorRol(u.role))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        u.nombre.ifBlank { "Sin nombre" },
                        style = MaterialTheme.typography.titleSmall,
                        color = Greon.Texto,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(8.dp))
                    RolChip(u.role)
                }
                Text(
                    u.email ?: "—",
                    style = MaterialTheme.typography.bodySmall,
                    color = Greon.TextoSuave,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${u.dispositivos} disp. · ${Formato.compacto(u.kwhTotal)} kWh · ${Formato.relativo(u.ultimoAcceso)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Greon.Verde
                )
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Greon.Gris)
        }
    }
}

/** Buscador + fila de chips de filtro (se reutiliza en Dispositivos). */
@Composable
fun BuscadorYFiltros(
    busqueda: String,
    alBuscar: (String) -> Unit,
    placeholder: String,
    opciones: List<Pair<String?, String>>,
    seleccion: String?,
    alSeleccionar: (String?) -> Unit
) {
    Column(Modifier.padding(top = 12.dp, bottom = 8.dp)) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = alBuscar,
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { alBuscar("") }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Borrar búsqueda")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(opciones) { (valor, etiqueta) ->
                FilterChip(
                    selected = seleccion == valor,
                    onClick = { alSeleccionar(valor) },
                    label = { Text(etiqueta) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = Greon.Verde,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}
