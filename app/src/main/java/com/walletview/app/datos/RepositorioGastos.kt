package com.walletview.app.datos

object RepositorioGastos {

    val categorias = listOf(
        "Alimentos",
        "Transporte",
        "Vivienda",
        "Salud",
        "Entretenimiento",
        "Educación"
    )

    private val gastos = mutableListOf(
        Gasto("Supermercado", "Alimentos", 1800.0),
        Gasto("Restaurantes", "Alimentos", 1400.0),
        Gasto("Gasolina", "Transporte", 1200.0),
        Gasto("Renta", "Vivienda", 5500.0),
        Gasto("Consulta médica", "Salud", 800.0),
        Gasto("Cine y streaming", "Entretenimiento", 950.0),
        Gasto("Colegiatura", "Educación", 1500.0)
    )

    fun agregar(gasto: Gasto) {
        gastos.add(gasto)
    }

    fun existe(concepto: String, categoria: String): Boolean {
        return gastos.any {
            it.categoria == categoria && it.concepto.equals(concepto, ignoreCase = true)
        }
    }

    fun totalGeneral(): Double = gastos.sumOf { it.monto }

    fun totalesPorCategoria(): Map<String, Double> {
        return categorias.associateWith { categoria ->
            gastos.filter { it.categoria == categoria }.sumOf { it.monto }
        }
    }
}
