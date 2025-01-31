package com.markets.argyview.activos

import android.os.Build
import android.util.Log
import com.google.gson.annotations.Expose
import org.decampo.xirr.Transaction
import org.decampo.xirr.Xirr
import java.time.LocalDate as ldJT
import kotlin.math.pow


import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.abs

open class Bono(ticker: String, precio: Double, moneda: String, dif: Double,
                @Expose val flujo : List<PagoBono>) : Activo(ticker, precio, moneda, dif) {

    fun getTIR():Double{
        if (Build.VERSION.SDK_INT < 26) {
            //todo
            return 0.0
        }

        val compra = Transaction(-this.precio, ldJT.now())
        val trans:List<Transaction> =  flujo.filter { ldJT.ofEpochDay(it.fecha.toEpochDay()) > ldJT.now() }.map {
            Transaction(it.cupon+it.amort, ldJT.ofEpochDay(it.fecha.toEpochDay()))
        } + compra

        val tasa = Xirr(trans).xirr()
        return tasa
    }
    fun getTIRf() = String.format("%.2f",this.getTIR()*100) + "%"

    fun getVN():Double = this.flujo.filter { it.fecha > LocalDate.now() }.sumOf { it.amort }

    fun getValTec():Double{
        val hoy = LocalDate.now()

        val proxCupones : List<PagoBono> = this.flujo.filter { it.fecha > hoy }
        val proxCupon : PagoBono = proxCupones.first()

        val ultCupon : PagoBono = try {
            this.flujo.filter { it.fecha < hoy }.last()
        }catch (e:NoSuchElementException){
            val difMeses = abs(ChronoUnit.MONTHS.between(proxCupon.fecha, proxCupones[1].fecha))
            PagoBono(proxCupon.fecha.minusMonths(difMeses),0.0,0.0)
        }catch (e:ArrayIndexOutOfBoundsException){
            PagoBono(LocalDate.now(),0.0,0.0)
        }

        val diasPasados = abs(ChronoUnit.DAYS.between(ultCupon.fecha, hoy))
        val diasTotales = abs(ChronoUnit.DAYS.between(ultCupon.fecha, proxCupon.fecha))
        val intCorrido = diasPasados * proxCupon.cupon / diasTotales

        return getVN() + intCorrido
    }
    fun getParidad() = this.precio / this.getValTec()
    fun getParidadF() = String.format("%.1f",this.getParidad()*100) + "%"

    fun getMD():Double{
        if (Build.VERSION.SDK_INT < 26) {
            //todo
            //return 0.0
        }

        val tir = this.getTIR()

        val flujoDesc = this.flujo
            .filter { it.fecha > LocalDate.now() }
            .mapIndexed { i, pago ->
                val aniosHastaFecha : Double = abs(ChronoUnit.DAYS.between(LocalDate.now(), pago.fecha)) / 365.0
                val pagoDesc = (pago.cupon + pago.amort) / (1 + tir).pow(aniosHastaFecha)

                Pair(aniosHastaFecha,pagoDesc)
            }

        val sumaPagosDesc = flujoDesc.sumOf { it.second }
        val dMAC = flujoDesc.sumOf { it.first * it.second } / sumaPagosDesc

        val flujoFuturo = this.flujo.filter { it.fecha > LocalDate.now() }
        val periodos = flujoFuturo.zipWithNext { a, b -> ChronoUnit.DAYS.between(a.fecha, b.fecha) }
        val frecuencia = 365 / periodos.average()


        return dMAC / (1 + tir / frecuencia)
        // fuente https://www.investopedia.com/terms/m/modifiedduration.asp
        //       https://es.wikipedia.org/wiki/Duraci%C3%B3n_de_Macaulay
        //       https://es.wikipedia.org/wiki/Duraci%C3%B3n_modificada
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