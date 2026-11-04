package com.greontrack.admin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Un punto de una gráfica por día. `dia` viene como "2026-11-03". */
@Serializable
data class PuntoDia(
    val dia: String,
    val total: Double = 0.0
)

/** Un conteo por categoría (roles, tipos de dispositivo...). */
@Serializable
data class Conteo(
    val nombre: String? = null,
    val total: Int = 0
)

@Serializable
data class DashboardStats(
    @SerialName("usuarios_total") val usuariosTotal: Int = 0,
    @SerialName("usuarios_nuevos_7d") val usuariosNuevos7d: Int = 0,
    @SerialName("usuarios_activos_7d") val usuariosActivos7d: Int = 0,
    @SerialName("dispositivos_total") val dispositivosTotal: Int = 0,
    @SerialName("dispositivos_agente") val dispositivosAgente: Int = 0,
    @SerialName("sugeridos_pendientes") val sugeridosPendientes: Int = 0,
    @SerialName("registros_uso_total") val registrosUsoTotal: Int = 0,
    @SerialName("recomendaciones_total") val recomendacionesTotal: Int = 0,
    @SerialName("kwh_total") val kwhTotal: Double = 0.0,
    @SerialName("costo_total") val costoTotal: Double = 0.0,
    @SerialName("co2_total") val co2Total: Double = 0.0,
    @SerialName("kwh_30d") val kwh30d: Double = 0.0,
    @SerialName("consultas_greon_total") val consultasGreonTotal: Int = 0,
    @SerialName("sniffers_activos") val sniffersActivos: Int = 0,
    @SerialName("tarifa_kwh") val tarifaKwh: Double? = null,
    @SerialName("factor_co2") val factorCo2: Double? = null,
    @SerialName("registros_por_dia") val registrosPorDia: List<PuntoDia> = emptyList(),
    @SerialName("kwh_por_dia") val kwhPorDia: List<PuntoDia> = emptyList(),
    val roles: List<Conteo> = emptyList(),
    @SerialName("dispositivos_por_tipo") val dispositivosPorTipo: List<Conteo> = emptyList()
)

@Serializable
data class UsuarioResumen(
    val id: String,
    val nombre: String = "",
    val email: String? = null,
    val role: String = "general",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("ultimo_acceso") val ultimoAcceso: String? = null,
    val dispositivos: Int = 0,
    @SerialName("kwh_total") val kwhTotal: Double = 0.0,
    @SerialName("greon_consultas") val greonConsultas: Int = 0
)

@Serializable
data class DispositivoUsuario(
    val id: String,
    val nombre: String = "",
    val tipo: String = "",
    @SerialName("consumo_watts_promedio") val consumoWatts: Double = 0.0,
    val origen: String = "manual",
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class UsuarioDetalle(
    val id: String,
    val nombre: String = "",
    val email: String? = null,
    val role: String = "general",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("ultimo_acceso") val ultimoAcceso: String? = null,
    @SerialName("aviso_privacidad_aceptado_en") val avisoAceptadoEn: String? = null,
    @SerialName("greon_consultas") val greonConsultas: Int = 0,
    @SerialName("kwh_total") val kwhTotal: Double = 0.0,
    @SerialName("costo_total") val costoTotal: Double = 0.0,
    @SerialName("co2_total") val co2Total: Double = 0.0,
    @SerialName("registros_uso") val registrosUso: Int = 0,
    val recomendaciones: Int = 0,
    @SerialName("sniffer_ultimo_sondeo") val snifferUltimoSondeo: String? = null,
    val dispositivos: List<DispositivoUsuario> = emptyList()
)

@Serializable
data class DispositivoAdmin(
    val id: String,
    val nombre: String = "",
    val tipo: String = "",
    @SerialName("consumo_watts_promedio") val consumoWatts: Double = 0.0,
    val origen: String = "manual",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("usuario_id") val usuarioId: String? = null,
    @SerialName("usuario_nombre") val usuarioNombre: String? = null,
    @SerialName("kwh_total") val kwhTotal: Double = 0.0
)

@Serializable
data class Configuracion(
    val id: Int = 1,
    @SerialName("tarifa_kwh") val tarifaKwh: Double = 0.0,
    @SerialName("factor_co2") val factorCo2: Double = 0.0,
    @SerialName("updated_at") val updatedAt: String? = null
)

/** Estado genérico de una pantalla que carga datos. */
sealed interface Carga<out T> {
    data object Cargando : Carga<Nothing>
    data class Listo<T>(val datos: T) : Carga<T>
    data class Error(val mensaje: String) : Carga<Nothing>
}
