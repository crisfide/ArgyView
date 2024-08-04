package com.markets.argyview.funciones

//import java.time.DayOfWeek
//import java.time.LocalDate
//import java.time.LocalTime


import org.threeten.bp.LocalDate
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime

class CheckMercado {
    companion object{
        private const val HORA_INICIO = 11
        private const val MINU_INICIO = 0

        private const val HORA_CIERRE = 17
        private const val MINU_CIERRE = 0

        private const val DELAY : Long = 21


        fun cerrado() : Boolean{
            val dia = LocalDate.now().dayOfWeek
            if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY)
                return true

            val hora = LocalTime.now()
            val postCierre = LocalTime.of(HORA_CIERRE, MINU_CIERRE).plusMinutes(DELAY)
            val preMarket = LocalTime.of(HORA_INICIO, MINU_INICIO).plusMinutes(DELAY)

            return hora.isAfter(postCierre) || hora.isBefore(preMarket)
        }
    }
}