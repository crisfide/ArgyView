package com.markets.argyview.funciones

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class CheckMercado {
    companion object{
        private const val HORA_CIERRE = 17
        private const val MINU_CIERRE = 0
        private const val DELAY : Long = 21


        fun cerrado() : Boolean{
            val dia = LocalDate.now().dayOfWeek
            if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY)
                return true

            val hora = LocalTime.now()
            val postCierre = LocalTime.of(HORA_CIERRE, MINU_CIERRE).plusMinutes(DELAY)

            return hora.isAfter(postCierre)
        }
    }
}