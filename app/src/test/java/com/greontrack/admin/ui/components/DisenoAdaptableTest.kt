package com.greontrack.admin.ui.components

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Columnas del panel segun el ancho y seleccion de barras en las graficas. */
class DisenoAdaptableTest {

    @Test
    fun columnasDelPanelSegunElAncho() {
        assertEquals(2, columnasKpi(380.dp))   // celular en vertical
        assertEquals(3, columnasKpi(700.dp))   // celular en horizontal
        assertEquals(4, columnasKpi(1000.dp))  // tablet
    }

    @Test
    fun tocarLaGraficaEligeLaBarraCorrecta() {
        // 4 barras en 100 px: cada una mide 25 px
        assertEquals(0, indiceEnX(x = 10f, ancho = 100f, n = 4))
        assertEquals(2, indiceEnX(x = 60f, ancho = 100f, n = 4))
        // Fuera de la grafica se queda en la primera o la ultima barra
        assertEquals(3, indiceEnX(x = 150f, ancho = 100f, n = 4))
        assertEquals(0, indiceEnX(x = -5f, ancho = 100f, n = 4))
    }

    @Test
    fun sinDatosNoHayBarraElegida() {
        assertNull(indiceEnX(x = 10f, ancho = 100f, n = 0))
        assertNull(indiceEnX(x = 10f, ancho = 0f, n = 4))
    }
}
