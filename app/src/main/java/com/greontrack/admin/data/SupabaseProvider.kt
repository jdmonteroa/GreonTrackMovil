package com.greontrack.admin.data

import com.greontrack.admin.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

/**
 * Cliente único de Supabase para toda la app.
 *
 * Las credenciales NO se escriben aquí: se leen del archivo
 * `supabase.properties` (en la raíz del proyecto) al compilar.
 */
object SupabaseProvider {

    val url: String = BuildConfig.SUPABASE_URL
    private val anonKey: String = BuildConfig.SUPABASE_ANON_KEY

    /** true si ya se llenó supabase.properties con datos reales. */
    val configurado: Boolean =
        url.startsWith("https://") &&
            !url.contains("TU-PROYECTO") &&
            anonKey.isNotBlank() &&
            !anonKey.contains("PEGA_AQUI")

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = anonKey
        ) {
            defaultSerializer = KotlinXSerializer(
                Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                    isLenient = true
                }
            )
            install(Auth)
            install(Postgrest)
        }
    }
}
