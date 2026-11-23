package com.walletview.app.pantallas

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.walletview.app.R
import com.walletview.app.datos.Gasto
import com.walletview.app.datos.RepositorioGastos
import com.walletview.app.logica.ValidadorGasto
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var campoConcepto: EditText
    private lateinit var campoMonto: EditText
    private lateinit var listaCategorias: Spinner
    private lateinit var textoMensaje: TextView
    private lateinit var textoTotal: TextView
    private lateinit var graficaPastel: PieChart
    private lateinit var graficaBarras: BarChart
    private lateinit var botonPastel: Button
    private lateinit var botonBarras: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        campoConcepto = findViewById(R.id.campoConcepto)
        campoMonto = findViewById(R.id.campoMonto)
        listaCategorias = findViewById(R.id.listaCategorias)
        textoMensaje = findViewById(R.id.textoMensaje)
        textoTotal = findViewById(R.id.textoTotal)
        graficaPastel = findViewById(R.id.graficaPastel)
        graficaBarras = findViewById(R.id.graficaBarras)
        botonPastel = findViewById(R.id.botonPastel)
        botonBarras = findViewById(R.id.botonBarras)

        val opciones = listOf(getString(R.string.seleccione_categoria)) + RepositorioGastos.categorias
        listaCategorias.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)

        findViewById<Button>(R.id.botonAgregar).setOnClickListener { agregarGasto() }
        botonPastel.setOnClickListener { mostrarGrafica(pastel = true) }
        botonBarras.setOnClickListener { mostrarGrafica(pastel = false) }

        actualizarPantalla()
        mostrarGrafica(pastel = true)
    }

    private fun agregarGasto() {
        val concepto = campoConcepto.text.toString()
        val monto = campoMonto.text.toString()
        val categoria = if (listaCategorias.selectedItemPosition == 0) {
            null
        } else {
            listaCategorias.selectedItem.toString()
        }

        val error = ValidadorGasto.validar(concepto, categoria, monto)
        if (error != null) {
            mostrarMensaje(error, R.color.error)
            return
        }

        RepositorioGastos.agregar(Gasto(concepto.trim(), categoria!!, monto.trim().toDouble()))
        campoConcepto.text.clear()
        campoMonto.text.clear()
        listaCategorias.setSelection(0)
        mostrarMensaje("Gasto agregado correctamente.", R.color.exito)
        actualizarPantalla()
    }

    private fun mostrarMensaje(texto: String, colorId: Int) {
        textoMensaje.text = texto
        textoMensaje.setTextColor(ContextCompat.getColor(this, colorId))
        textoMensaje.visibility = View.VISIBLE
    }

    private fun mostrarGrafica(pastel: Boolean) {
        graficaPastel.visibility = if (pastel) View.VISIBLE else View.GONE
        graficaBarras.visibility = if (pastel) View.GONE else View.VISIBLE
        botonPastel.alpha = if (pastel) 1f else 0.6f
        botonBarras.alpha = if (pastel) 0.6f else 1f
        if (pastel) graficaPastel.animateY(800) else graficaBarras.animateY(800)
    }

    private fun actualizarPantalla() {
        val totales = RepositorioGastos.totalesPorCategoria().filterValues { it > 0 }
        textoTotal.text = String.format(Locale.US, "Total del mes: $%,.2f", RepositorioGastos.totalGeneral())
        dibujarPastel(totales)
        dibujarBarras(totales)
    }

    private fun dibujarPastel(totales: Map<String, Double>) {
        val entradas = totales.map { PieEntry(it.value.toFloat(), it.key) }
        val conjunto = PieDataSet(entradas, "").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList() + ColorTemplate.VORDIPLOM_COLORS.toList()
            valueTextSize = 12f
            valueTextColor = Color.BLACK
        }
        graficaPastel.apply {
            data = PieData(conjunto).apply { setValueFormatter(PercentFormatter(graficaPastel)) }
            setUsePercentValues(true)
            description.isEnabled = false
            setDrawEntryLabels(false)
            legend.isWordWrapEnabled = true
            centerText = "Gastos"
            animateY(800)
            invalidate()
        }
    }

    private fun dibujarBarras(totales: Map<String, Double>) {
        val entradas = totales.values.mapIndexed { indice, valor -> BarEntry(indice.toFloat(), valor.toFloat()) }
        val conjunto = BarDataSet(entradas, "Gasto en pesos").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 11f
        }
        graficaBarras.apply {
            data = BarData(conjunto)
            description.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(totales.keys.toList())
                granularity = 1f
                labelRotationAngle = -30f
                setDrawGridLines(false)
            }
            animateY(800)
            invalidate()
        }
    }
}
