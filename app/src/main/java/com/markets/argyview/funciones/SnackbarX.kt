package com.markets.argyview.funciones

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.markets.argyview.R

class SnackbarX {
    companion object{
        private fun make(view:View, texto:String, fondo:Int) {
            val snackbar = Snackbar.make(view,texto, Snackbar.LENGTH_SHORT)
            with(snackbar) {
                setTextColor(view.resources.getColor(R.color.texto))
                setBackgroundTint(fondo)
                show()
            }
        }

        fun normal(view: View, texto: String) = make(view, texto, view.resources.getColor(R.color.fondo))

        fun err (view: View, texto: String) = make(view, texto, view.resources.getColor(R.color.error))


        fun cargando(view: View) = normal(view, view.resources.getString(R.string.cargando))

        fun noInternet(view: View) = err(view, view.resources.getString(R.string.noInternet))

    }
}