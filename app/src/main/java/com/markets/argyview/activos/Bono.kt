package com.markets.argyview.activos

//import org.threeten.bp.LocalDate
//import org.threeten.bp.temporal.ChronoUnit
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.annotations.Expose
import org.decampo.xirr.Transaction
import org.decampo.xirr.Xirr
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.pow

open class Bono(ticker: String, precio: Double, moneda: String, dif: Double,
                @Expose val flujo : List<PagoBono>) : Activo(ticker, precio, moneda, dif) {

    fun getTIR():Double{
        var compra = Transaction(-this.precio, LocalDate.now())
        var trans:List<Transaction> =  flujo.filter { it.fecha > LocalDate.now() }.map {
            Transaction(it.cupon+it.amort, it.fecha)
        } + compra

        var tasa = Xirr(trans).xirr()
        return tasa
    }
    fun getTIRf() = String.format("%.2f",this.getTIR()*100) + "%"

    fun getVN():Double = this.flujo.filter { it.fecha > LocalDate.now() }.sumOf { it.amort }

    fun getValTec():Double{
        var hoy = LocalDate.now()
        var ultCupon: PagoBono = this.flujo.filter { it.fecha < hoy }.last()
        var proxCupon: PagoBono = this.flujo.filter { it.fecha > hoy }.first()

        var diasPasados = ChronoUnit.DAYS.between(ultCupon.fecha, hoy)
        var diasTotales = ChronoUnit.DAYS.between(ultCupon.fecha, proxCupon.fecha)
        var intCorrido = diasPasados * proxCupon.cupon /diasTotales

        return getVN() + intCorrido
    }
    fun getParidad() = this.precio / this.getValTec()
    fun getParidadF() = String.format("%.1f",this.getParidad()*100) + "%"

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMD():Double{
        var flujoDesc = this.flujo.filter { it.fecha > LocalDate.now() }
            .mapIndexed { i, pago ->
                (pago.cupon + pago.amort) / (1 + this.getTIR()).pow(i+1)
            }
        var precioDesc = flujoDesc.sum()
        var dMAC = flujoDesc.reduceIndexed { i, a, v ->
            flujoDesc[i] * (i+1) / precioDesc
        }

        var flujoFuturo = this.flujo.filter { it.fecha > LocalDate.now() }
        var periodos = mutableListOf<Long>()
        for (i in 1..< flujoFuturo.size){
            periodos.add(ChronoUnit.DAYS.between(flujoFuturo[i-1].fecha, flujoFuturo[i].fecha))
        }
        var frecuencia = 1 / (periodos.average() / 365)
        Log.i("pagobono",frecuencia.toString())

        return dMAC / (1+this.getTIR()) / frecuencia
        // fuente https://www.investopedia.com/terms/m/modifiedduration.asp
    }
    fun getMDf() = String.format("%.2f",this.getMD())

    fun getVencimiento() = this.flujo.last().fecha
    fun getVencimientoF():String{
        val pattern = DateTimeFormatter.ofPattern("d/M/yyyy")
        return this.getVencimiento().format(pattern)
    }

    fun esBullet():Boolean = this.flujo.last().amort == 100.0

    fun getFlujof() = this.flujo.joinToString("")

    override fun toString(): String {
        return super.toString() + "\n" +
                "Vencimiento: " + getVencimientoF() + "\n" +
                "TIR: " + getTIRf() + "\n" +
                "Paridad: " + getParidadF() + "\n" +
                "MD: " + getMDf() + " años \n" +
                "Flujo de fondos: \n" + getFlujof()
    }
}