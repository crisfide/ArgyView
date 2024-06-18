package com.markets.argyview.funciones

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackbarX {
    companion object{
        fun make(view:View, texto:String, fondo:Int) {
            val snackbar = Snackbar.make(view,texto, Snackbar.LENGTH_LONG)
            with(snackbar) {
                setTextColor(android.graphics.Color.WHITE)
                setBackgroundTint(fondo)
                show()
            }
        }


    }
}