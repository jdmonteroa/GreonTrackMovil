package com.greontrack.admin

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.greontrack.admin.data.PreferenciaTema
import com.greontrack.admin.data.Tema
import com.greontrack.admin.ui.EstadoSesion
import com.greontrack.admin.ui.SesionViewModel
import com.greontrack.admin.ui.screens.AjustesScreen
import com.greontrack.admin.ui.screens.DashboardScreen
import com.greontrack.admin.ui.screens.DispositivosScreen
import com.greontrack.admin.ui.screens.LoginScreen
import com.greontrack.admin.ui.screens.SinConfigurarScreen
import com.greontrack.admin.ui.screens.UsuarioDetalleScreen
import com.greontrack.admin.ui.screens.UsuariosScreen
import com.greontrack.admin.ui.theme.Greon
import com.greontrack.admin.ui.theme.GreonAdminTheme

/** Cuanto dura la animacion de salida del splash (el logo crece y se desvanece). */
private const val DURACION_SALIDA_SPLASH = 400L

class MainActivity : ComponentActivity() {

    // El mismo SesionViewModel que usa la app: el splash lo necesita para saber cuando quitarse
    private val sesion: SesionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash oficial (API de Android 12+). Se instala antes de super.onCreate
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        // El splash se queda mientras se revisa si hay una sesion guardada
        splash.setKeepOnScreenCondition { sesion.estado == EstadoSesion.Iniciando }

        // Al salir, el logo crece un poco y se desvanece
        splash.setOnExitAnimationListener { vista ->
            runCatching {
                vista.iconView.animate()
                    .scaleX(1.35f)
                    .scaleY(1.35f)
                    .setDuration(DURACION_SALIDA_SPLASH)
                    .start()
            }
            vista.view.animate()
                .alpha(0f)
                .setDuration(DURACION_SALIDA_SPLASH)
                .withEndAction { vista.remove() }
                .start()
        }

        PreferenciaTema.iniciar(this)
        // Iconos de la barra de estado en blanco (la barra superior es verde oscuro)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT))
        setContent {
            // Claro, oscuro o lo que diga el sistema (se elige en Ajustes)
            val oscuro = when (PreferenciaTema.tema) {
                Tema.Sistema -> isSystemInDarkTheme()
                Tema.Claro -> false
                Tema.Oscuro -> true
            }

            // Barra de estado siempre con iconos blancos (la barra superior es verde oscuro).

            DisposableEffect(oscuro) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
                    navigationBarStyle = SystemBarStyle.auto(
                        AndroidColor.TRANSPARENT,
                        AndroidColor.TRANSPARENT
                    ) { oscuro }
                )
                onDispose { }
            }

            GreonAdminTheme(oscuro = oscuro) {
                GreonAdminApp(sesion)
            }
        }
    }
}

@Composable
fun GreonAdminApp(sesion: SesionViewModel = viewModel()) {
    when (val estado = sesion.estado) {
        EstadoSesion.SinConfigurar -> SinConfigurarScreen()
        EstadoSesion.Iniciando -> Splash()
        EstadoSesion.SinSesion -> LoginScreen(
            enviando = sesion.enviando,
            error = sesion.error,
            alEntrar = sesion::iniciarSesion
        )
        is EstadoSesion.Admin -> PanelAdmin(
            correo = estado.correo,
            alCerrarSesion = sesion::cerrarSesion
        )
    }
}

/** Respaldo del splash dentro de Compose. Es igual al splash del sistema. */
@Composable
private fun Splash() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Greon.VerdeOscuro),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo_greon),
            contentDescription = "GreonTrack",
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(25.dp)
        )
    }
}

private enum class Seccion(val ruta: String, val titulo: String, val icono: ImageVector) {
    Panel("panel", "Panel", Icons.Rounded.Dashboard),
    Usuarios("usuarios", "Usuarios", Icons.Rounded.Group),
    Dispositivos("dispositivos", "Dispositivos", Icons.Rounded.Devices),
    Ajustes("ajustes", "Ajustes", Icons.Rounded.Settings)
}

@Composable
private fun PanelAdmin(correo: String, alCerrarSesion: () -> Unit) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val destino = entrada?.destination
    val mostrarBarra = Seccion.entries.any { s -> destino?.hierarchy?.any { it.route == s.ruta } == true }

    Scaffold(
        containerColor = Greon.VerdeMuyClaro,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar(containerColor = Greon.Superficie, tonalElevation = 0.dp) {
                    Seccion.entries.forEach { s ->
                        val seleccionada = destino?.hierarchy?.any { it.route == s.ruta } == true
                        NavigationBarItem(
                            selected = seleccionada,
                            onClick = {
                                nav.navigate(s.ruta) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(s.icono, contentDescription = s.titulo) },
                            label = { Text(s.titulo) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Greon.TextoTitulo,
                                selectedTextColor = Greon.TextoTitulo,
                                indicatorColor = if (Greon.EsOscuro) Greon.Verde.copy(alpha = 0.35f)
                                else Greon.VerdeClaro.copy(alpha = 0.6f),
                                unselectedIconColor = Greon.TextoSuave,
                                unselectedTextColor = Greon.TextoSuave
                            )
                        )
                    }
                }
            }
        }
    ) { relleno ->
        NavHost(
            navController = nav,
            startDestination = Seccion.Panel.ruta,
            modifier = Modifier.padding(relleno)
        ) {
            composable(Seccion.Panel.ruta) { DashboardScreen() }
            composable(Seccion.Usuarios.ruta) {
                UsuariosScreen(alAbrirUsuario = { id -> nav.navigate("usuario/$id") })
            }
            composable(Seccion.Dispositivos.ruta) {
                DispositivosScreen(alAbrirUsuario = { id -> nav.navigate("usuario/$id") })
            }
            composable(Seccion.Ajustes.ruta) {
                AjustesScreen(correo = correo, alCerrarSesion = alCerrarSesion)
            }
            composable(
                route = "usuario/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStack ->
                UsuarioDetalleScreen(
                    usuarioId = backStack.arguments?.getString("id") ?: "",
                    alVolver = { nav.popBackStack() }
                )
            }
        }
    }
}
