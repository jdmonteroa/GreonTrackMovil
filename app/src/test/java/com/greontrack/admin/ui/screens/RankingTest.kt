package com.greontrack.admin.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

/** Texto del ranking de consumo en el detalle de usuario. */
class RankingTest {

    @Test
    fun primerLugar() {
        assertEquals("N.º 1 en consumo", textoRanking(lugar = 1, total = 20, kwh = 50.0))
    }

    @Test
    fun topTresYTopDiez() {
        assertEquals("Top 3 en consumo", textoRanking(lugar = 3, total = 20, kwh = 30.0))
        assertEquals("Top 10 en consumo", textoRanking(lugar = 7, total = 20, kwh = 10.0))
    }

    @Test
    fun lugaresDespuesDelDiez() {
        assertEquals("Lugar 12 de 20 en consumo", textoRanking(lugar = 12, total = 20, kwh = 1.0))
    }

    @Test
    fun sinConsumoNoHayRanking() {
        assertEquals("Sin consumo registrado", textoRanking(lugar = 1, total = 20, kwh = 0.0))
    }
}
