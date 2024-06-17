package com.markets.argyview.activos

import com.google.gson.annotations.Expose

open class Activo (@Expose val ticker:String, @Expose var precio:Double, @Expose val moneda:String, @Expose var dif:Double){
    var fav = true

    var precioF = moneda + if (this.precio==0.0) " -" else String.format(" %.2f",this.precio)
    
    private var signoDif = if(this.dif>0) "+" else ""
    var difF = this.signoDif + String.format("%.2f",this.dif) + "%"

    override fun toString(): String {
        return "$ticker: $precioF ($difF)"
    }
}