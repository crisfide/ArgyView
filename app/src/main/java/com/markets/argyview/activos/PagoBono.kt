package com.markets.argyview.activos

import android.os.Build
import com.google.gson.annotations.Expose
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class PagoBono(@Expose val fecha: LocalDate, @Expose val cupon:Double, @Expose val amort:Double) {

    override fun toString(): String {

        val pattern =
            DateTimeFormatter.ofPattern("d/M/yyyy")

        return "${fecha.format(pattern)}: ${cupon} - ${amort}\n"
    }

}
