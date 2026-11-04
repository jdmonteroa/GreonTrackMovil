package com.greontrack.admin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greontrack.admin.R

/** Paleta tomada de la app web de GreonTrack (src/index.css). */
object Greon {
    val VerdeOscuro = Color(0xFF0F3D2E)
    val VerdeOscuro2 = Color(0xFF0A2E22)
    val Verde = Color(0xFF2E7D32)
    val VerdeMedio = Color(0xFF66BB6A)
    val VerdeClaro = Color(0xFFA7D7A0)
    val VerdeMuyClaro = Color(0xFFE8F5E9)
    val Texto = Color(0xFF12241C)
    val TextoSuave = Color(0xFF52695F)
    val Blanco = Color(0xFFFFFFFF)
    val Error = Color(0xFFC0392B)
    val ErrorBg = Color(0xFFFDECEA)

    // Acentos para tarjetas y gráficas
    val Ambar = Color(0xFFE59A1F)
    val Azul = Color(0xFF2F80C2)
    val Morado = Color(0xFF7E57C2)
    val Turquesa = Color(0xFF15A39A)
    val Gris = Color(0xFF8AA197)
}

private val colores = lightColorScheme(
    primary = Greon.Verde,
    onPrimary = Greon.Blanco,
    primaryContainer = Greon.VerdeMuyClaro,
    onPrimaryContainer = Greon.VerdeOscuro,
    secondary = Greon.VerdeMedio,
    onSecondary = Greon.Blanco,
    secondaryContainer = Greon.VerdeClaro,
    onSecondaryContainer = Greon.VerdeOscuro,
    tertiary = Greon.Ambar,
    background = Greon.VerdeMuyClaro,
    onBackground = Greon.Texto,
    surface = Greon.Blanco,
    onSurface = Greon.Texto,
    surfaceVariant = Color(0xFFF1F7F2),
    onSurfaceVariant = Greon.TextoSuave,
    surfaceContainer = Greon.Blanco,
    surfaceContainerLow = Greon.Blanco,
    surfaceContainerHigh = Color(0xFFF4F9F5),
    outline = Color(0xFFBFD8C5),
    outlineVariant = Color(0xFFDCEBE0),
    error = Greon.Error,
    errorContainer = Greon.ErrorBg
)

/** Fraunces: la letra de los títulos en la web (serif con personalidad). */
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
fun GreonAdminTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colores,
        typography = tipografia,
        shapes = formas,
        content = content
    )
}
