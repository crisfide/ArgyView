package com.markets.argyview.activos

class ON(ticker: String, precio: Double, moneda: String, dif: Double,
         flujo: List<PagoBono>) : Bono(ticker, precio, moneda, dif, flujo)