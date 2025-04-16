package com.markets.argyview.activos

class FlujoBono(val flujo : List<PagoBono>, val monedaFlujo: String, val mep : Double) : ArrayList<PagoBono>(flujo)