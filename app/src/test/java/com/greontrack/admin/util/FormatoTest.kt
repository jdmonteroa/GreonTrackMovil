package com.greontrack.admin.util

import org.junit.Assert.assertEquals
import org.junit.Test

/** Pruebas de los numeros y fechas que se muestran en la app (español de Mexico). */
class FormatoTest {

    @Test
    fun enteroUsaComasDeMiles() {
        assertEquals("1,234,567", Formato.entero(1234567))
    }

    @Test
    fun decimalRespetaLosDecimalesPedidos() {
        assertEquals("3.14", Formato.decimal(3.14159, 2))
        assertEquals("2", Formato.decimal(2.0, 1))
    }

    @Test
    fun compactoAbreviaNumerosGrandes() {
        assertEquals("42.3", Formato.compacto(42.3))
        assertEquals("950", Formato.compacto(950.4))
        assertEquals("12.3 mil", Formato.compacto(12_345.0))
        assertEquals("2.5 M", Formato.compacto(2_500_000.0))
    }

    @Test
    fun nombreRolTraduceLosRoles() {
        assertEquals("Admin", Formato.nombreRol("admin"))
        assertEquals("Analítico", Formato.nombreRol("analitico"))
        assertEquals("General", Formato.nombreRol("cualquier_otro"))
    }

    @Test
    fun inicialesTomaLasPrimerasDosPalabras() {
        assertEquals("AM", Formato.iniciales("ana maria lopez"))
        assertEquals("?", Formato.iniciales("   "))
    }

    @Test
    fun diaCortoYDiaMedio() {
        assertEquals("3/11", Formato.diaCorto("2026-11-03"))
        assertEquals("Mar 3 de nov", Formato.diaMedio("2026-11-03"))
    }

    @Test
    fun fechasInvalidasNoTruenan() {
        assertEquals("no-es-fecha", Formato.diaCorto("no-es-fecha"))
        assertEquals("Nunca", Formato.relativo(null))
    }
}
