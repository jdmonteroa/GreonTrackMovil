package com.greontrack.admin.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greontrack.admin.R
import com.greontrack.admin.ui.theme.Greon
import kotlinx.coroutines.launch

// =====================================================================
//  Greon, la mascota de GreonTrack.
//  Igual que en la web, cambia de "animo" con un filtro de color y un
//  pequeno gesto (no hay un dibujo distinto por cada expresion).
// =====================================================================

/** Como se siente Greon (en la web: mascot-mood-feliz / mascot-mood-triste). */
enum class AnimoGreon { Feliz, Triste }

/** Relacion ancho / alto de la imagen mascota_greon.png (360 x 263). */
private const val PROPORCION_MASCOTA = 360f / 263f

/**
 * Dibuja a Greon.
 *
 * @param ancho tamano de la mascota.
 * @param animo Feliz (normal) o Triste (colores apagados, inclinado y con un puntito naranja).
 * @param flotar si sube y baja suavemente, como en la web (mascot-idle-float).
 * @param alTocar si no es null, Greon da un saltito al tocarlo y se llama esta funcion.
 */
@Composable
fun MascotaGreon(
    modifier: Modifier = Modifier,
    ancho: Dp = 120.dp,
    animo: AnimoGreon = AnimoGreon.Feliz,
    flotar: Boolean = true,
    descripcion: String? = null,
    alTocar: (() -> Unit)? = null
) {
    val triste = animo == AnimoGreon.Triste

    // Sube y baja 6 dp cada 1.7 segundos
    val transicion = rememberInfiniteTransition(label = "greon")
    val flote = transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flote"
    )

    // Saltito al tocarlo
    val escala = remember { Animatable(1f) }
    val alcance = rememberCoroutineScope()

    // Triste: colores apagados (como el filtro saturate(0.5) de la web)
    val filtro = remember(triste) {
        if (triste) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.45f) }) else null
    }

    val toque = if (alTocar != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            alcance.launch {
                escala.animateTo(1.12f, tween(durationMillis = 110))
                escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
            alTocar()
        }
    } else {
        Modifier
    }

    Box(modifier) {
        Image(
            painter = painterResource(R.drawable.mascota_greon),
            contentDescription = descripcion,
            colorFilter = filtro,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(ancho)
                .aspectRatio(PROPORCION_MASCOTA)
                .graphicsLayer {
                    val sube = if (flotar) flote.value * 6.dp.toPx() else 0f
                    translationY = -sube + if (triste) 2.dp.toPx() else 0f
                    rotationZ = if (triste) -6f else 0f
                    scaleX = escala.value
                    scaleY = escala.value
                }
                .then(toque)
        )

        if (triste) {
            // Puntito de estado en la esquina, como mascot-mood-dot de la web
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = ancho * 0.05f, end = ancho * 0.12f)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0AD4E))
                    .border(2.dp, Greon.Superficie, CircleShape)
            )
        }
    }
}
