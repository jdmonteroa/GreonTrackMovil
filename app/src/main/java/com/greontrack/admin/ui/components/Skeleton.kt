package com.greontrack.admin.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.greontrack.admin.ui.theme.Greon

// =====================================================================
//  Skeletons: "huesos" grises con un brillo que recorre la pantalla
//  mientras cargan los datos (en lugar del circulito girando).
// =====================================================================

/** Degradado animado que se desplaza de izquierda a derecha. */
@Composable
fun rememberBrilloSkeleton(): Brush {
    // Se basa en el color del texto para que se vea en tema claro y oscuro
    val base = Greon.Texto.copy(alpha = 0.08f)
    val brillo = Greon.Texto.copy(alpha = 0.02f)
    val transicion = rememberInfiniteTransition(label = "skeleton")
    val x by transicion.animateFloat(
        initialValue = -400f,
        targetValue = 1600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "brillo"
    )
    return Brush.linearGradient(
        colors = listOf(base, brillo, base),
        start = Offset(x - 400f, 0f),
        end = Offset(x, 0f)
    )
}

/** Un bloque gris del skeleton. */
@Composable
fun Hueso(brillo: Brush, modifier: Modifier = Modifier, forma: Shape = RoundedCornerShape(6.dp)) {
    Box(
        modifier
            .clip(forma)
            .background(brillo)
    )
}

@Composable
private fun TarjetaFantasma(
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Greon.Superficie),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), content = contenido)
    }
}

// ---------------------------------------------------------------------
//  Panel
// ---------------------------------------------------------------------
@Composable
fun SkeletonDashboard() {
    val b = rememberBrilloSkeleton()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState(), enabled = false)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Banner de bienvenida
        Hueso(b, Modifier.fillMaxWidth().height(128.dp), RoundedCornerShape(20.dp))

        // Tarjetas KPI (2 por fila)
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) {
                    TarjetaFantasma(Modifier.weight(1f)) {
                        Hueso(b, Modifier.size(36.dp), RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(12.dp))
                        Hueso(b, Modifier.width(64.dp).height(22.dp))
                        Spacer(Modifier.height(8.dp))
                        Hueso(b, Modifier.fillMaxWidth(0.85f).height(11.dp))
                        Spacer(Modifier.height(8.dp))
                        Hueso(b, Modifier.fillMaxWidth(0.6f).height(9.dp))
                    }
                }
            }
        }

        // Gráfica
        TarjetaFantasma {
            Hueso(b, Modifier.width(140.dp).height(14.dp))
            Spacer(Modifier.height(6.dp))
            Hueso(b, Modifier.width(200.dp).height(10.dp))
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth().height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                listOf(0.35f, 0.6f, 0.45f, 0.8f, 0.5f, 0.7f, 0.3f, 0.9f, 0.55f, 0.65f, 0.4f, 0.75f).forEach { alto ->
                    Hueso(b, Modifier.weight(1f).fillMaxHeight(alto), RoundedCornerShape(4.dp))
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
//  Listas (usuarios / dispositivos)
// ---------------------------------------------------------------------
@Composable
fun SkeletonLista(filas: Int = 7, avatarRedondo: Boolean = true) {
    val b = rememberBrilloSkeleton()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState(), enabled = false)
    ) {
        // Buscador y chips
        Column(Modifier.padding(top = 12.dp, bottom = 8.dp)) {
            Hueso(b, Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(54.dp), RoundedCornerShape(14.dp))
            Spacer(Modifier.height(10.dp))
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(90.dp, 64.dp, 84.dp, 72.dp).forEach { ancho ->
                    Hueso(b, Modifier.width(ancho).height(32.dp), RoundedCornerShape(8.dp))
                }
            }
        }
        Column(
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Hueso(b, Modifier.width(90.dp).height(10.dp))
            repeat(filas) { i ->
                TarjetaFantasma {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Hueso(
                            b,
                            Modifier.size(44.dp),
                            if (avatarRedondo) CircleShape else RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Hueso(b, Modifier.fillMaxWidth(if (i % 2 == 0) 0.55f else 0.7f).height(13.dp))
                            Spacer(Modifier.height(7.dp))
                            Hueso(b, Modifier.fillMaxWidth(0.8f).height(10.dp))
                            Spacer(Modifier.height(7.dp))
                            Hueso(b, Modifier.fillMaxWidth(0.45f).height(9.dp))
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
//  Detalle de usuario
// ---------------------------------------------------------------------
@Composable
fun SkeletonDetalle() {
    val b = rememberBrilloSkeleton()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState(), enabled = false)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TarjetaFantasma {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Hueso(b, Modifier.size(60.dp), CircleShape)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Hueso(b, Modifier.fillMaxWidth(0.6f).height(18.dp))
                    Spacer(Modifier.height(8.dp))
                    Hueso(b, Modifier.fillMaxWidth(0.8f).height(11.dp))
                    Spacer(Modifier.height(8.dp))
                    Hueso(b, Modifier.width(60.dp).height(20.dp), RoundedCornerShape(50))
                }
            }
            Spacer(Modifier.height(18.dp))
            repeat(4) {
                Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Hueso(b, Modifier.size(18.dp), CircleShape)
                    Spacer(Modifier.width(10.dp))
                    Hueso(b, Modifier.width(100.dp).height(11.dp))
                    Spacer(Modifier.weight(1f))
                    Hueso(b, Modifier.width(80.dp).height(11.dp))
                }
            }
        }
        TarjetaFantasma {
            Hueso(b, Modifier.width(90.dp).height(14.dp))
            Spacer(Modifier.height(14.dp))
            repeat(2) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { Hueso(b, Modifier.weight(1f).height(54.dp), RoundedCornerShape(12.dp)) }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        TarjetaFantasma {
            Hueso(b, Modifier.width(130.dp).height(14.dp))
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { Hueso(b, Modifier.width(80.dp).height(32.dp), RoundedCornerShape(8.dp)) }
            }
            Spacer(Modifier.height(14.dp))
            Hueso(b, Modifier.fillMaxWidth().height(40.dp), RoundedCornerShape(12.dp))
        }
    }
}

// ---------------------------------------------------------------------
//  Formulario de ajustes
// ---------------------------------------------------------------------
@Composable
fun SkeletonFormulario() {
    val b = rememberBrilloSkeleton()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Hueso(b, Modifier.fillMaxWidth().height(56.dp), RoundedCornerShape(12.dp))
        Hueso(b, Modifier.fillMaxWidth().height(56.dp), RoundedCornerShape(12.dp))
        Hueso(b, Modifier.width(180.dp).height(10.dp))
        Spacer(Modifier.height(2.dp))
        Hueso(b, Modifier.fillMaxWidth().height(40.dp), RoundedCornerShape(12.dp))
    }
}
