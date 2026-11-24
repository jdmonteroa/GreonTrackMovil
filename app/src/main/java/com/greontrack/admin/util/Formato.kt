package com.greontrack.admin.util

import java.text.NumberFormat
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Utilidades para mostrar numeros y fechas en español de Mexico. */
object Formato {

    private val mx: Locale = Locale.forLanguageTag("es-MX")
    private val zona: ZoneId = ZoneId.systemDefault()
    private val fFecha = DateTimeFormatter.ofPattern("d MMM yyyy", mx)
    private val fFechaHora = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", mx)
    private val fDiaCorto = DateTimeFormatter.ofPattern("d/M", mx)
    private val fDiaLargo = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", mx)
    private val fDiaMedio = DateTimeFormatter.ofPattern("EEE d 'de' MMM", mx)

    fun entero(n: Number): String = NumberFormat.getIntegerInstance(mx).format(n)

    fun decimal(d: Double, decimales: Int = 1): String =
        NumberFormat.getNumberInstance(mx).apply {
            maximumFractionDigits = decimales
            minimumFractionDigits = 0
        }.format(d)

    fun dinero(d: Double): String =
        NumberFormat.getCurrencyInstance(mx).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }.format(d)

    /** Numero corto: 1,2 mil / 3,4 M */
    fun compacto(d: Double): String = when {
        d >= 1_000_000 -> decimal(d / 1_000_000, 1) + " M"
        d >= 10_000 -> decimal(d / 1_000, 1) + " mil"
        d >= 100 -> entero(Math.round(d))
        else -> decimal(d, 1)
    }

    private fun parse(iso: String?): OffsetDateTime? =
        iso?.let { runCatching { OffsetDateTime.parse(it.replace(' ', 'T')) }.getOrNull() }

    fun fecha(iso: String?): String =
        parse(iso)?.atZoneSameInstant(zona)?.format(fFecha) ?: (iso?.take(10) ?: "—")

    fun fechaHora(iso: String?): String =
        parse(iso)?.atZoneSameInstant(zona)?.format(fFechaHora) ?: (iso?.take(16) ?: "—")

    fun diaCorto(isoDate: String): String =
        runCatching { LocalDate.parse(isoDate.take(10)).format(fDiaCorto) }.getOrDefault(isoDate)

    /** "Lun 3 de nov" (para los globitos de las gráficas). */
    fun diaMedio(isoDate: String): String =
        runCatching {
            LocalDate.parse(isoDate.take(10)).format(fDiaMedio).replace(".", "").replaceFirstChar { it.uppercase() }
        }.getOrDefault(isoDate)

    fun hoyLargo(): String =
        LocalDate.now().format(fDiaLargo).replaceFirstChar { it.uppercase() }

    /** "hace 5 min", "hace 3 h", "hace 2 días"... */
    fun relativo(iso: String?): String {
        val t = parse(iso) ?: return "Nunca"
        val d = Duration.between(t.toInstant(), java.time.Instant.now())
        return when {
            d.isNegative || d.toMinutes() < 1 -> "Justo ahora"
            d.toMinutes() < 60 -> "Hace ${d.toMinutes()} min"
            d.toHours() < 24 -> "Hace ${d.toHours()} h"
            d.toDays() < 30 -> "Hace ${d.toDays()} día" + if (d.toDays() == 1L) "" else "s"
            else -> fecha(iso)
        }
    }

    fun nombreRol(rol: String): String = when (rol) {
        "admin" -> "Admin"
        "analitico" -> "Analítico"
        else -> "General"
    }

    fun iniciales(nombre: String): String =
        nombre.trim().split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }.ifBlank { "?" }
}