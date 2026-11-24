package com.greontrack.admin.data

import org.junit.Assert.assertEquals
import org.junit.Test

/** El tema guardado en Ajustes se lee bien, aunque el valor guardado sea raro. */
class TemaTest {

    @Test
    fun leeLosTemasGuardados() {
        assertEquals(Tema.Claro, Tema.desde("claro"))
        assertEquals(Tema.Oscuro, Tema.desde("oscuro"))
        assertEquals(Tema.Sistema, Tema.desde("sistema"))
    }

    @Test
    fun siNoHayValorUsaElDelSistema() {
        assertEquals(Tema.Sistema, Tema.desde(null))
        assertEquals(Tema.Sistema, Tema.desde("morado"))
    }
}
