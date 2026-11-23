package com.walletview.app.logica

import com.walletview.app.datos.RepositorioGastos

object ValidadorGasto {

    private const val LONGITUD_MAXIMA_CONCEPTO = 40
    private const val MONTO_MAXIMO = 1_000_000.0
    private val formatoMonto = Regex("^\\d+(\\.\\d{1,2})?$")

    fun validar(concepto: String, categoria: String?, montoTexto: String): String? {
        val conceptoLimpio = concepto.trim()
        val montoLimpio = montoTexto.trim()

        if (conceptoLimpio.isEmpty()) {
            return "Escriba el concepto del gasto."
        }
        if (conceptoLimpio.length > LONGITUD_MAXIMA_CONCEPTO) {
            return "El concepto no puede tener más de 40 caracteres."
        }
        if (categoria == null) {
            return "Seleccione una categoría."
        }
        if (montoLimpio.isEmpty()) {
            return "Escriba el monto del gasto."
        }
        if (!formatoMonto.matches(montoLimpio)) {
            return "El monto debe ser un número positivo con máximo dos decimales."
        }
        val monto = montoLimpio.toDouble()
        if (monto <= 0.0) {
            return "El monto debe ser mayor que cero."
        }
        if (monto > MONTO_MAXIMO) {
            return "El monto no puede ser mayor a 1,000,000 de pesos."
        }
        if (RepositorioGastos.existe(conceptoLimpio, categoria)) {
            return "Ya existe un gasto con ese concepto en la categoría seleccionada."
        }
        return null
    }
}
