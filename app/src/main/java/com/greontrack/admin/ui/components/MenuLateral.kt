package com.greontrack.admin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greontrack.admin.R
import com.greontrack.admin.ui.theme.Greon

// =====================================================================
//  Diseno adaptable: celular, celular en horizontal y tablet.
//  Los cortes son los mismos que usa Material ("window size classes").
// =====================================================================

/** Que tan ancha es la pantalla. */
enum class TipoPantalla {
    /** Celular en vertical (menos de 600 dp): barra de navegacion abajo. */
    Compacta,

    /** Celular en horizontal o tablet chica (600 a 999 dp): menu lateral angosto. */
    Mediana,

    /** Tablet o pantalla grande (desde 1000 dp): menu lateral completo, como la web. */
    Expandida
}

val ANCHO_MENU_ANGOSTO = 88.dp
val ANCHO_MENU_COMPLETO = 232.dp

/** Ancho de la ventana en dp. */
@Composable
fun anchoVentana(): Dp = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }

@Composable
fun tipoPantalla(): TipoPantalla {
    val ancho = anchoVentana()
    return when {
        ancho >= 1000.dp -> TipoPantalla.Expandida
        ancho >= 600.dp -> TipoPantalla.Mediana
        else -> TipoPantalla.Compacta
    }
}

/** Ancho que le queda al contenido: la ventana menos el menu lateral. */
@Composable
fun anchoContenido(): Dp {
    val ancho = anchoVentana()
    return when (tipoPantalla()) {
        TipoPantalla.Compacta -> ancho
        TipoPantalla.Mediana -> ancho - ANCHO_MENU_ANGOSTO
        TipoPantalla.Expandida -> ancho - ANCHO_MENU_COMPLETO
    }
}

/** Cuantas tarjetas KPI caben por fila: 2 en celular, 3 en horizontal o tablet, 4 en pantallas grandes. */
fun columnasKpi(anchoContenido: Dp): Int = when {
    anchoContenido >= 900.dp -> 4
    anchoContenido >= 560.dp -> 3
    else -> 2
}

/** Una opcion del menu lateral. */
data class OpcionMenu(val titulo: String, val icono: ImageVector)

/**
 * Menu lateral con el estilo del sidebar de la web: fondo verde oscuro,
 * logo arriba y la opcion elegida resaltada en verde.
 *
 * @param completo true = con textos al lado (tablet); false = angosto, solo iconos con texto chico.
 */
@Composable
fun MenuLateral(
    opciones: List<OpcionMenu>,
    seleccionada: Int?,
    completo: Boolean,
    alElegir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxHeight()
            .width(if (completo) ANCHO_MENU_COMPLETO else ANCHO_MENU_ANGOSTO)
            .background(Brush.verticalGradient(listOf(Color(0xFF12483A), Greon.VerdeOscuro, Greon.VerdeOscuro2)))
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start))
            .verticalScroll(rememberScrollState())
            .padding(vertical = 20.dp, horizontal = if (completo) 14.dp else 8.dp),
        horizontalAlignment = if (completo) Alignment.Start else Alignment.CenterHorizontally
    ) {
        // Logo y nombre
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = if (completo) 8.dp else 0.dp, bottom = 26.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo_greon),
                contentDescription = "GreonTrack",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(3.dp)
            )
            if (completo) {
                Spacer(Modifier.width(10.dp))
                Text("GreonTrack", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }
        }

        if (completo) {
            Text(
                "ADMINISTRACIÓN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = Color.White.copy(alpha = 0.35f),
                modifier = Modifier.padding(start = 12.dp, bottom = 10.dp)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(if (completo) 4.dp else 6.dp)) {
            opciones.forEachIndexed { i, opcion ->
                if (completo) {
                    ItemMenuCompleto(opcion, i == seleccionada) { alElegir(i) }
                } else {
                    ItemMenuAngosto(opcion, i == seleccionada) { alElegir(i) }
                }
            }
        }
    }
}

/** Opcion con icono y texto a un lado (como .sidebar-link de la web). */
@Composable
private fun ItemMenuCompleto(opcion: OpcionMenu, elegida: Boolean, alTocar: () -> Unit) {
    val forma = RoundedCornerShape(10.dp)
    val color = if (elegida) Color.White else Color.White.copy(alpha = 0.62f)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(if (elegida) Greon.VerdeMedio.copy(alpha = 0.18f) else Color.Transparent)
            .then(
                if (elegida) Modifier.border(1.dp, Greon.VerdeMedio.copy(alpha = 0.35f), forma) else Modifier
            )
            .selectable(selected = elegida, role = Role.Tab, onClick = alTocar)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(opcion.icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(opcion.titulo, style = MaterialTheme.typography.labelLarge, color = color)
    }
}

/** Opcion angosta: icono en una pastilla y el texto chiquito abajo. */
@Composable
private fun ItemMenuAngosto(opcion: OpcionMenu, elegida: Boolean, alTocar: () -> Unit) {
    val color = if (elegida) Color.White else Color.White.copy(alpha = 0.62f)
    Column(
        Modifier
            .width(ANCHO_MENU_ANGOSTO - 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .selectable(selected = elegida, role = Role.Tab, onClick = alTocar)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(width = 52.dp, height = 32.dp)
                .clip(RoundedCornerShape(50))
                .background(if (elegida) Greon.VerdeMedio.copy(alpha = 0.28f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(opcion.icono, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(
            opcion.titulo,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
