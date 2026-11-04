package com.greontrack.admin.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Todo el acceso a Supabase pasa por aquí.
 * Los datos de administración se piden a funciones RPC (ver supabase/admin_setup.sql)
 * que validan en el servidor que el usuario tenga role = 'admin'.
 */
object AdminRepository {

    private val client get() = SupabaseProvider.client

    // ---------------- Sesión ----------------

    suspend fun iniciarSesion(correo: String, contrasena: String) {
        client.auth.signInWith(Email) {
            email = correo
            password = contrasena
        }
    }

    suspend fun cerrarSesion() {
        client.auth.signOut()
    }

    fun correoActual(): String? = client.auth.currentUserOrNull()?.email

    fun idActual(): String? = client.auth.currentUserOrNull()?.id

    suspend fun esAdmin(): Boolean =
        client.postgrest.rpc("is_admin", buildJsonObject { }).decodeAs<Boolean>()

    // ---------------- Datos de admin ----------------

    suspend fun dashboard(): DashboardStats =
        client.postgrest.rpc("admin_dashboard_stats", buildJsonObject { })
            .decodeAs<DashboardStats>()

    suspend fun usuarios(): List<UsuarioResumen> =
        client.postgrest.rpc("admin_usuarios", buildJsonObject { })
            .decodeList<UsuarioResumen>()

    suspend fun usuarioDetalle(id: String): UsuarioDetalle =
        client.postgrest.rpc("admin_usuario_detalle", buildJsonObject { put("p_usuario", id) })
            .decodeAs<UsuarioDetalle>()

    suspend fun dispositivos(): List<DispositivoAdmin> =
        client.postgrest.rpc("admin_dispositivos", buildJsonObject { })
            .decodeList<DispositivoAdmin>()

    suspend fun cambiarRol(usuarioId: String, rol: String) {
        client.postgrest.rpc(
            "admin_cambiar_rol",
            buildJsonObject {
                put("p_usuario", usuarioId)
                put("p_role", rol)
            }
        )
    }

    suspend fun configuracion(): Configuracion =
        client.from("configuracion")
            .select { filter { eq("id", 1) } }
            .decodeSingle<Configuracion>()

    suspend fun actualizarConfiguracion(tarifa: Double, factor: Double) {
        client.postgrest.rpc(
            "admin_actualizar_config",
            buildJsonObject {
                put("p_tarifa", tarifa)
                put("p_factor", factor)
            }
        )
    }
}

/** Convierte cualquier error de red/Supabase en un mensaje entendible. */
fun Throwable.mensajeAmigable(): String {
    val texto = (message ?: "") + " " + (cause?.message ?: "")
    return when {
        texto.contains("Invalid login credentials", true) ->
            "Correo o contraseña incorrectos."
        texto.contains("Email not confirmed", true) ->
            "Esta cuenta aún no confirma su correo."
        texto.contains("42501") || texto.contains("administradores", true) ->
            "Tu cuenta no tiene permisos de administrador."
        texto.contains("PGRST202") || texto.contains("Could not find the function", true) ->
            "Falta ejecutar supabase/admin_setup.sql en el SQL Editor de Supabase."
        texto.contains("Unable to resolve host", true) ||
            texto.contains("UnknownHost", true) ||
            texto.contains("timeout", true) ||
            texto.contains("Failed to connect", true) ->
            "No hay conexión con Supabase. Revisa tu internet."
        texto.contains("JWT", true) ->
            "Tu sesión expiró. Vuelve a iniciar sesión."
        else -> message?.lineSequence()?.firstOrNull()?.take(180) ?: "Ocurrió un error inesperado."
    }
}
