package com.greontrack.admin.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greontrack.admin.R

// =====================================================================
//  Colores que cambian con el tema (claro / oscuro).
//  Son los mismos de la web: src/index.css, en :root y :root[data-theme-dark]
// =====================================================================
@Immutable
data class ColoresGreon(
    val oscuro: Boolean,
    val texto: Color,        // --texto
    val textoSuave: Color,   // --texto-suave
    val textoTitulo: Color,  // --texto-titulo
    val superficie: Color,   // --superficie (fondo de las tarjetas)
    val fondo: Color,        // --verde-muy-claro (fondo de la pantalla)
    val error: Color,        // --error
    val errorBg: Color       // --error-bg
)

val ColoresClaros = ColoresGreon(
    oscuro = false,
    texto = Color(0xFF12241C),
    textoSuave = Color(0xFF52695F),
    textoTitulo = Color(0xFF0F3D2E),
    superficie = Color(0xFFFFFFFF),
    fondo = Color(0xFFE8F5E9),
    error = Color(0xFFC0392B),
    errorBg = Color(0xFFFDECEA)
)

val ColoresOscuros = ColoresGreon(
    oscuro = true,
    texto = Color(0xFFDFEAE4),
    textoSuave = Color(0xFF93AB9F),
    textoTitulo = Color(0xFF8FD6A6),
    superficie = Color(0xFF17251F),
    fondo = Color(0xFF1C3229),
    error = Color(0xFFFF7A68),
    errorBg = Color(0xFF3A1C17)
)

val LocalColoresGreon = staticCompositionLocalOf { ColoresClaros }

/**
 * Paleta tomada de la app web de GreonTrack (src/index.css).
 *
 * Los colores de marca (verdes, acentos) son fijos. Los que dependen del
 * tema (Texto, Superficie, etc.) se leen del tema actual, por eso solo
 * se pueden usar dentro de funciones @Composable.
 */
object Greon {
    // Colores de marca: iguales en claro y oscuro
    val VerdeOscuro = Color(0xFF0F3D2E)
    val VerdeOscuro2 = Color(0xFF0A2E22)
    val Verde = Color(0xFF2E7D32)
    val VerdeMedio = Color(0xFF66BB6A)
    val VerdeClaro = Color(0xFFA7D7A0)
    val Blanco = Color(0xFFFFFFFF)

    // Acentos para tarjetas y graficas
    val Ambar = Color(0xFFE59A1F)
    val Azul = Color(0xFF2F80C2)
    val Morado = Color(0xFF7E57C2)
    val Turquesa = Color(0xFF15A39A)
    val Gris = Color(0xFF8AA197)

    // Colores que cambian con el tema
    val Texto: Color
        @Composable @ReadOnlyComposable get() = LocalColoresGreon.current.texto
    val TextoSuave: Color
        @Composable @ReadOnlyComposable get() = LocalColoresGreon.current.textoSuave
    val TextoTitulo: Color
        @Composable @ReadOnlyComposable get() = LocalColoresGreon.current.textoTitulo
    val Superficie: Color
        @Composable @ReadOnlyComposable get() = LocalColoresGreon.current.superficie
    val VerdeMuyClaro: Color
        @Composable @ReadOnlyComposable get() = LocalColoresGreon.current.fondo
    val Error: Color
        @Composable @ReadOnlyComposable get() = LocalColoresGreon.current.error
    val ErrorBg: Color
        @Composable @ReadOnlyComposable get() = LocalColoresGreon.current.errorBg
    val EsOscuro: Boolean
        @Composable @ReadOnlyComposable get() = LocalColoresGreon.current.oscuro
}

private val esquemaClaro = lightColorScheme(
    primary = Greon.Verde,
    onPrimary = Greon.Blanco,
    primaryContainer = ColoresClaros.fondo,
    onPrimaryContainer = Greon.VerdeOscuro,
    secondary = Greon.VerdeMedio,
    onSecondary = Greon.Blanco,
    secondaryContainer = Greon.VerdeClaro,
    onSecondaryContainer = Greon.VerdeOscuro,
    tertiary = Greon.Ambar,
    background = ColoresClaros.fondo,
    onBackground = ColoresClaros.texto,
    surface = ColoresClaros.superficie,
    onSurface = ColoresClaros.texto,
    surfaceVariant = Color(0xFFF1F7F2),
    onSurfaceVariant = ColoresClaros.textoSuave,
    surfaceContainer = ColoresClaros.superficie,
    surfaceContainerLow = ColoresClaros.superficie,
    surfaceContainerHigh = Color(0xFFF4F9F5),
    surfaceContainerHighest = Color(0xFFEDF5EF),
    outline = Color(0xFFBFD8C5),
    outlineVariant = Color(0xFFDCEBE0),
    error = ColoresClaros.error,
    errorContainer = ColoresClaros.errorBg
)

private val esquemaOscuro = darkColorScheme(
    // En oscuro el verde medio se lee mejor que el verde normal
    primary = Greon.VerdeMedio,
    onPrimary = Greon.VerdeOscuro2,
    primaryContainer = Color(0xFF234237),
    onPrimaryContainer = ColoresOscuros.textoTitulo,
    secondary = Greon.VerdeClaro,
    onSecondary = Greon.VerdeOscuro2,
    secondaryContainer = Color(0xFF234237),
    onSecondaryContainer = ColoresOscuros.textoTitulo,
    tertiary = Greon.Ambar,
    background = ColoresOscuros.fondo,
    onBackground = ColoresOscuros.texto,
    surface = ColoresOscuros.superficie,
    onSurface = ColoresOscuros.texto,
    surfaceVariant = Color(0xFF22382E),
    onSurfaceVariant = ColoresOscuros.textoSuave,
    surfaceContainer = ColoresOscuros.superficie,
    surfaceContainerLow = ColoresOscuros.superficie,
    surfaceContainerHigh = Color(0xFF1E2F28),
    surfaceContainerHighest = Color(0xFF25392F),
    outline = Color(0xFF3F5E50),
    outlineVariant = Color(0xFF2A4237),
    error = ColoresOscuros.error,
    errorContainer = ColoresOscuros.errorBg
)

/** Fraunces: la letra de los titulos en la web (serif con personalidad). */
val Fraunces = FontFamily(
    Font(R.font.fraunces_medium, FontWeight.Medium),
    Font(R.font.fraunces_semibold, FontWeight.SemiBold),
    Font(R.font.fraunces_bold, FontWeight.Bold)
)

/** Plus Jakarta Sans: la letra del texto general en la web. */
val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold)
)

private val titulo = Fraunces
private val texto = PlusJakartaSans

private val tipografia = Typography(
    displaySmall = TextStyle(fontFamily = titulo, fontWeight = FontWeight.SemiBold, fontSize = 34.sp, lineHeight = 40.sp),
    headlineLarge = TextStyle(fontFamily = titulo, fontWeight = FontWeight.SemiBold, fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = titulo, fontWeight = FontWeight.SemiBold, fontSize = 25.sp, lineHeight = 31.sp),
    headlineSmall = TextStyle(fontFamily = titulo, fontWeight = FontWeight.SemiBold, fontSize = 21.sp, lineHeight = 27.sp),
    titleLarge = TextStyle(fontFamily = titulo, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp),
    labelLarge = TextStyle(fontFamily = texto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = texto, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = texto, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp)
)

private val formas = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun GreonAdminTheme(oscuro: Boolean = false, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalColoresGreon provides if (oscuro) ColoresOscuros else ColoresClaros) {
        MaterialTheme(
            colorScheme = if (oscuro) esquemaOscuro else esquemaClaro,
            typography = tipografia,
            shapes = formas,
            content = content
        )
    }
}
