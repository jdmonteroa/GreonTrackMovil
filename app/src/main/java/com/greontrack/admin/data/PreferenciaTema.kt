package com.greontrack.admin.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

/** Opciones de tema, igual que en la web: seguir al sistema, claro u oscuro. */
enum class Tema(val clave: String, val nombre: String) {
    Sistema("sistema", "Sistema"),
    Claro("claro", "Claro"),
    Oscuro("oscuro", "Oscuro");

    companion object {
        fun desde(clave: String?): Tema = entries.firstOrNull { it.clave == clave } ?: Sistema
    }
}

/**
 * Guarda el tema que eligio el usuario en Ajustes (SharedPreferences),
 * para que se mantenga aunque cierre la app.
 */
object PreferenciaTema {

    private const val ARCHIVO = "greon_ajustes"
    private const val CLAVE_TEMA = "tema"

    private var prefs: SharedPreferences? = null

    /** Tema actual. Al cambiarlo, toda la app se vuelve a dibujar con los nuevos colores. */
    var tema by mutableStateOf(Tema.Sistema)
        private set

    /** Se llama una vez al abrir la app (en MainActivity). */
    fun iniciar(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        prefs = p
        tema = Tema.desde(p.getString(CLAVE_TEMA, null))
    }

    fun cambiar(nuevo: Tema) {
        tema = nuevo
        prefs?.edit { putString(CLAVE_TEMA, nuevo.clave) }
    }
}
