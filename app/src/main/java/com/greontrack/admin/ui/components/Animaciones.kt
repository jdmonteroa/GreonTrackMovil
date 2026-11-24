package com.greontrack.admin.ui.components

import android.os.SystemClock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// =====================================================================
//  Animaciones de entrada: los elementos de una pantalla aparecen uno
//  tras otro con un desvanecido y un leve deslizamiento hacia arriba.
// =====================================================================

/** Tiempo entre que aparece un elemento y el siguiente. */
private const val RETRASO_ENTRE_ELEMENTOS = 70L

/** Duración del desvanecido + deslizamiento de cada elemento. */
private const val DURACION_ENTRADA = 450

/**
 * Coordina la entrada "en cascada" de una pantalla.
 *
 * Solo se anima la primera vez que se abre la pantalla: si vas a otra
 * pestaña y regresas, todo aparece directo para no marear.
 */
class EntradaEscalonada internal constructor(val animar: Boolean) {
    internal val inicio: Long = SystemClock.uptimeMillis()

    /** Elementos que ya hicieron su animación (para no repetirla al hacer scroll). */
    internal val vistos = mutableSetOf<Int>()
}

@Composable
fun rememberEntradaEscalonada(): EntradaEscalonada {
    var primeraVez by rememberSaveable { mutableStateOf(true) }
    val entrada = remember { EntradaEscalonada(animar = primeraVez) }
    LaunchedEffect(Unit) { primeraVez = false }
    return entrada
}

/**
 * Hace que el elemento aparezca con desvanecido y deslizamiento.
 * `indice` es su posición en la pantalla: 0 aparece primero, 1 después, etc.
 */
@Composable
fun Modifier.aparecer(entrada: EntradaEscalonada, indice: Int): Modifier {
    // true solo la primera vez que este elemento se dibuja
    val animarEste = remember { entrada.animar && entrada.vistos.add(indice) }
    val progreso = remember { Animatable(if (animarEste) 0f else 1f) }

    if (animarEste) {
        LaunchedEffect(Unit) {
            // Cada elemento espera su turno, contado desde que abrió la pantalla.
            // Si aparece después por hacer scroll, ya pasó su turno y se anima de inmediato.
            val espera = entrada.inicio + indice * RETRASO_ENTRE_ELEMENTOS - SystemClock.uptimeMillis()
            if (espera > 0) delay(espera)
            progreso.animateTo(1f, tween(DURACION_ENTRADA, easing = FastOutSlowInEasing))
        }
    }

    // Se lee dentro de graphicsLayer para que solo se redibuje, sin recomponer
    return this.graphicsLayer {
        alpha = progreso.value
        translationY = (1f - progreso.value) * 28.dp.toPx()
    }
}

/**
 * Avanza de 0 a 1 una sola vez. Se usa para que los números de los KPI
 * "cuenten" desde 0 hasta su valor: valor mostrado = valor real × progreso.
 */
@Composable
fun rememberProgresoContador(entrada: EntradaEscalonada): State<Float> {
    val progreso = remember { Animatable(if (entrada.animar) 0f else 1f) }
    LaunchedEffect(Unit) {
        progreso.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, delayMillis = 150, easing = FastOutSlowInEasing)
        )
    }
    return progreso.asState()
}