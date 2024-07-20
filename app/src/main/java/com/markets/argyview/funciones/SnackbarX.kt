package com.markets.argyview.funciones

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.markets.argyview.R

class SnackbarX {
    companion object{
        fun make(view:View, texto:String, fondo:Int) {
            val snackbar = Snackbar.make(view,texto, Snackbar.LENGTH_SHORT)
            with(snackbar) {
                setTextColor(view.resources.getColor(R.color.texto))
                setBackgroundTint(fondo)
                show()
            }
        }


    }
}