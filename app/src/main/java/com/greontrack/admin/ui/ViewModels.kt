package com.greontrack.admin.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greontrack.admin.data.AdminRepository
import com.greontrack.admin.data.Carga
import com.greontrack.admin.data.Configuracion
import com.greontrack.admin.data.DashboardStats
import com.greontrack.admin.data.DispositivoAdmin
import com.greontrack.admin.data.SupabaseProvider
import com.greontrack.admin.data.UsuarioDetalle
import com.greontrack.admin.data.UsuarioResumen
import com.greontrack.admin.data.mensajeAmigable
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// =====================================================================
//  Sesión: decide si mostrar login o el panel
// =====================================================================
sealed interface EstadoSesion {
    data object SinConfigurar : EstadoSesion
    data object Iniciando : EstadoSesion
    data object SinSesion : EstadoSesion
    data class Admin(val correo: String) : EstadoSesion
}

class SesionViewModel : ViewModel() {

    var estado by mutableStateOf<EstadoSesion>(EstadoSesion.Iniciando)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var enviando by mutableStateOf(false)
        private set

    init {
        if (!SupabaseProvider.configurado) {
            estado = EstadoSesion.SinConfigurar
        } else {
            viewModelScope.launch {
                // Espera a que Supabase recupere la sesión guardada (si existe)
                val status = SupabaseProvider.client.auth.sessionStatus
                    .first {
                        it is SessionStatus.Authenticated ||
                            it is SessionStatus.NotAuthenticated ||
                            it is SessionStatus.RefreshFailure
                    }
                if (status is SessionStatus.Authenticated) verificarAdmin()
                else estado = EstadoSesion.SinSesion
            }
        }
    }

    fun iniciarSesion(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            error = "Escribe tu correo y contraseña."
            return
        }
        viewModelScope.launch {
            enviando = true
            error = null
            try {
                AdminRepository.iniciarSesion(correo.trim(), contrasena)
                verificarAdmin()
            } catch (e: Exception) {
                error = e.mensajeAmigable()
            } finally {
                enviando = false
            }
        }
    }

    private suspend fun verificarAdmin() {
        val esAdmin = try {
            AdminRepository.esAdmin()
        } catch (e: Exception) {
            error = e.mensajeAmigable()
            estado = EstadoSesion.SinSesion
            return
        }
        if (esAdmin) {
            error = null
            estado = EstadoSesion.Admin(AdminRepository.correoActual() ?: "")
        } else {
            runCatching { AdminRepository.cerrarSesion() }
            error = "Esta cuenta no tiene permisos de administrador. " +
                "Solo las cuentas con rol \"admin\" pueden entrar."
            estado = EstadoSesion.SinSesion
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            runCatching { AdminRepository.cerrarSesion() }
            error = null
            estado = EstadoSesion.SinSesion
        }
    }
}

// =====================================================================
//  Base para pantallas que cargan una sola cosa
// =====================================================================
abstract class CargaViewModel<T> : ViewModel() {

    var estado by mutableStateOf<Carga<T>>(Carga.Cargando)
        protected set
    var refrescando by mutableStateOf(false)
        private set

    protected abstract suspend fun obtener(): T

    /** Si ya hay datos, recarga "en silencio" sin borrar la pantalla. */
    fun cargar() {
        viewModelScope.launch {
            val habiaDatos = estado is Carga.Listo
            if (habiaDatos) refrescando = true else estado = Carga.Cargando
            try {
                estado = Carga.Listo(obtener())
            } catch (e: Exception) {
                if (!habiaDatos) estado = Carga.Error(e.mensajeAmigable())
            } finally {
                refrescando = false
            }
        }
    }
}

class DashboardViewModel : CargaViewModel<DashboardStats>() {
    override suspend fun obtener() = AdminRepository.dashboard()
}

class UsuariosViewModel : CargaViewModel<List<UsuarioResumen>>() {
    var busqueda by mutableStateOf("")
    var filtroRol by mutableStateOf<String?>(null)
    override suspend fun obtener() = AdminRepository.usuarios()
}

class DispositivosViewModel : CargaViewModel<List<DispositivoAdmin>>() {
    var busqueda by mutableStateOf("")
    var filtroOrigen by mutableStateOf<String?>(null)
    override suspend fun obtener() = AdminRepository.dispositivos()
}

class UsuarioDetalleViewModel : CargaViewModel<UsuarioDetalle>() {
    var usuarioId: String = ""
    var guardando by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)

    override suspend fun obtener() = AdminRepository.usuarioDetalle(usuarioId)

    fun cambiarRol(nuevoRol: String) {
        viewModelScope.launch {
            guardando = true
            try {
                AdminRepository.cambiarRol(usuarioId, nuevoRol)
                estado = Carga.Listo(AdminRepository.usuarioDetalle(usuarioId))
                mensaje = "Rol actualizado correctamente."
            } catch (e: Exception) {
                mensaje = e.mensajeAmigable()
            } finally {
                guardando = false
            }
        }
    }
}

class AjustesViewModel : CargaViewModel<Configuracion>() {
    var guardando by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)

    override suspend fun obtener() = AdminRepository.configuracion()

    fun guardar(tarifaTexto: String, factorTexto: String) {
        val tarifa = tarifaTexto.replace(",", ".").toDoubleOrNull()
        val factor = factorTexto.replace(",", ".").toDoubleOrNull()
        if (tarifa == null || factor == null || tarifa <= 0 || factor <= 0) {
            mensaje = "Escribe números válidos mayores a 0."
            return
        }
        viewModelScope.launch {
            guardando = true
            try {
                AdminRepository.actualizarConfiguracion(tarifa, factor)
                estado = Carga.Listo(AdminRepository.configuracion())
                mensaje = "Configuración guardada. Los nuevos registros de uso usarán estos valores."
            } catch (e: Exception) {
                mensaje = e.mensajeAmigable()
            } finally {
                guardando = false
            }
        }
    }
}
