package com.greontrack.admin.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.greontrack.admin.BuildConfig
import com.greontrack.admin.data.Carga
import com.greontrack.admin.data.Configuracion
import com.greontrack.admin.data.PreferenciaTema
import com.greontrack.admin.data.SupabaseProvider
import com.greontrack.admin.data.Tema
import com.greontrack.admin.ui.AjustesViewModel
import com.greontrack.admin.ui.components.Avatar
import com.greontrack.admin.ui.components.ContenidoCarga
import com.greontrack.admin.ui.components.SkeletonFormulario
import com.greontrack.admin.ui.components.Etiqueta
import com.greontrack.admin.ui.components.GreonTopBar
import com.greontrack.admin.ui.components.Tarjeta
import com.greontrack.admin.ui.theme.Greon
import com.greontrack.admin.util.Formato

@Composable
fun AjustesScreen(
    correo: String,
    alCerrarSesion: () -> Unit,
    vm: AjustesViewModel = viewModel()
) {
    LaunchedEffect(Unit) { vm.cargar() }
    var confirmarSalida by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        GreonTopBar(titulo = "Ajustes", subtitulo = "Cuenta y parámetros de cálculo")

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cuenta
            Tarjeta(titulo = "Tu cuenta") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(correo.substringBefore("@"), Greon.Morado, 48.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(correo, style = MaterialTheme.typography.titleSmall, color = Greon.Texto)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.AdminPanelSettings,
                                contentDescription = null,
                                tint = Greon.Morado,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Etiqueta("Administrador", Greon.Morado)
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                OutlinedButton(
                    onClick = { confirmarSalida = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Greon.Error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar sesión")
                }
            }

            // Apariencia: claro, oscuro o igual que el sistema
            Tarjeta(
                titulo = "Apariencia",
                descripcion = "Elige si la app se ve clara, oscura o igual que tu celular."
            ) {
                SelectorTema()
            }

            // Configuración global
            Tarjeta(
                titulo = "Parámetros de cálculo",
                descripcion = "Se usan para calcular costo y CO₂ cada vez que un usuario registra uso (tabla configuracion)."
            ) {
                ContenidoCargaCompacto(vm)
            }

            // Acerca de
            Tarjeta(titulo = "Acerca de") {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = Greon.VerdeMedio)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("GreonTrack Admin v${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "App móvil de administración de GreonTrack. Comparte la base de datos " +
                                "de Supabase con la app web.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Greon.TextoSuave
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            SupabaseProvider.url.removePrefix("https://"),
                            style = MaterialTheme.typography.labelSmall,
                            color = Greon.Gris
                        )
                    }
                }
            }
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tendrás que volver a escribir tu correo y contraseña para entrar.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    alCerrarSesion()
                }) { Text("Cerrar sesión", color = Greon.Error) }
            },
            dismissButton = { TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") } }
        )
    }

    vm.mensaje?.let { texto ->
        AlertDialog(
            onDismissRequest = { vm.mensaje = null },
            confirmButton = { TextButton(onClick = { vm.mensaje = null }) { Text("Aceptar") } },
            title = { Text("Ajustes") },
            text = { Text(texto) }
        )
    }
}

@Composable
private fun SelectorTema() {
    Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Tema.entries.forEach { opcion ->
            FilterChip(
                selected = PreferenciaTema.tema == opcion,
                onClick = { PreferenciaTema.cambiar(opcion) },
                label = { Text(opcion.nombre) },
                leadingIcon = {
                    Icon(iconoTema(opcion), contentDescription = null, modifier = Modifier.size(18.dp))
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Greon.Verde,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}

private fun iconoTema(tema: Tema): ImageVector = when (tema) {
    Tema.Sistema -> Icons.Rounded.BrightnessAuto
    Tema.Claro -> Icons.Rounded.LightMode
    Tema.Oscuro -> Icons.Rounded.DarkMode
}

@Composable
private fun ContenidoCargaCompacto(vm: AjustesViewModel) {
    Column(Modifier.fillMaxWidth()) {
        when (val e = vm.estado) {
            is Carga.Cargando -> SkeletonFormulario()
            is Carga.Error -> {
                Text(e.mensaje, color = Greon.Error, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = { vm.cargar() }) { Text("Reintentar") }
            }
            is Carga.Listo -> FormularioConfig(e.datos, vm)
        }
    }
}

@Composable
private fun FormularioConfig(config: Configuracion, vm: AjustesViewModel) {
    var tarifa by remember(config) { mutableStateOf(config.tarifaKwh.toString()) }
    var factor by remember(config) { mutableStateOf(config.factorCo2.toString()) }

    OutlinedTextField(
        value = tarifa,
        onValueChange = { tarifa = it },
        label = { Text("Tarifa por kWh (MXN)") },
        leadingIcon = { Icon(Icons.Rounded.Bolt, contentDescription = null, tint = Greon.Ambar) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(
        value = factor,
        onValueChange = { factor = it },
        label = { Text("Factor de emisión (kg CO₂ por kWh)") },
        leadingIcon = { Icon(Icons.Rounded.Eco, contentDescription = null, tint = Greon.Verde) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(6.dp))
    Text(
        "Última actualización: ${Formato.fechaHora(config.updatedAt)}",
        style = MaterialTheme.typography.bodySmall,
        color = Greon.TextoSuave
    )
    Spacer(Modifier.height(12.dp))
    Button(
        onClick = { vm.guardar(tarifa, factor) },
        enabled = !vm.guardando,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Greon.Verde),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (vm.guardando) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        } else {
            Text("Guardar cambios")
        }
    }
}
