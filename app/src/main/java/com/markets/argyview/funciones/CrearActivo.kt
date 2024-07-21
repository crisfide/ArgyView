package com.markets.argyview.funciones

import android.util.Log
import com.google.gson.Gson
import com.markets.argyview.activos.Activo
import com.markets.argyview.activos.Bono
import com.markets.argyview.activos.PagoBono
import java.time.LocalDate

class CrearActivo {
    class Urls{
        companion object{
            val urlBonistas = "https://bonistas.com/"

            val urlBolsar = "https://bolsar.info/"
            val urlBolsarBonos = urlBolsar+"Titulos_Publicos.php"
            val urlBolsarONs = urlBolsar+"Obligaciones_Negociables.php"
            val urlBolsarAcciones = urlBolsar+"lideres.php"
            val urlBolsarPGeneral = urlBolsar+"paneles.php?panel=2&titulo=Panel%20General"
            val urlBolsarCedears = urlBolsar+"Cedears.php"

            val urlsBolsar = hashMapOf(
                Pair("Bonos", urlBolsarBonos) ,
                Pair("Obligaciones negociables", urlBolsarONs),
                Pair("Acciones", urlBolsarAcciones),
                Pair("Panel General", urlBolsarPGeneral),
                Pair("Cedears", urlBolsarCedears)
            )

            private val urlByma = "https://open.bymadata.com.ar/vanoms-be-core/rest/api/bymadata/free/"
            val urlBymaBonos = urlByma+"public-bonds"
            val urlBymaONs = urlByma+"negociable-obligations"
            val urlBymaAcciones = urlByma+"leading-equity"
            val urlBymaPGeneral = urlByma+"general-equity"
            val urlBymaCedears = urlByma+"cedears"
            val urlBymaOpciones = urlByma+"options"
            val urlBymaLetras = urlByma+"lebacs"

            val urlsByma = hashMapOf(
                Pair("Bonos", urlBymaBonos) ,
                Pair("Obligaciones negociables", urlBymaONs),
                Pair("Acciones", urlBymaAcciones),
                Pair("Panel General", urlBymaPGeneral),
                Pair("Cedears", urlBymaCedears),
                Pair("Opciones", urlBymaOpciones),
                Pair("Letras", urlBymaLetras)
            )
        }
    }

    companion object{

        private val ARS = "$"
        private val USD = "US$"

        val monedas = mapOf(
            Pair("ARS", ARS),
            Pair("USD", USD),
            Pair("EXT", USD)
        )

        private val body = "{\"excludeZeroPxAndQty\":true,\"T2\":false,\"T1\":true,\"T0\":false,\"Content-Type\":\"application/json\",\"page_size\":400}"

        suspend fun crear(str: String):Activo{
            val ticker = str.uppercase()
            return when {
                ticker == "MEP" || ticker == "MEP " || ticker == "DOLAR MEP" || ticker == "DÓLAR MEP" ->
                    calcularMEP("AL30")
                ticker.contains("MEP ") -> calcularMEP(ticker)
                else -> crearBonoBYMA(ticker)
            }
        }
        suspend fun crear(vararg arr: String):List<Activo> = crear(arr.toList())

        suspend fun crear(arr: List<String>):List<Activo>{
            val docs = hashMapOf<String, String?>()
            return arr.map {
                val ticker = it.uppercase()
                lateinit var tipo: String
                if (!ticker.contains("MEP")) {
                    tipo = BDActivos.obtenerTipo(ticker)
                    if (!docs.containsKey(tipo)) docs.put(tipo, Red.conectar(Urls.urlsByma[tipo]!!, body))
                }
                return@map when {
                    ticker == "MEP" || ticker == "MEP " || ticker == "DOLAR MEP" || ticker == "DÓLAR MEP" ->
                        calcularMEP("AL30")
                    ticker.contains("MEP ") -> calcularMEP(ticker)
                    else -> crearBonoBYMA(ticker, docs[tipo]!!, tipo)
                }
            }
        }

        private fun crear(tipo: String, jsonStr: String?):List<Activo>{
            val data = jsonToData(jsonStr)
            var lista = data.map { it["symbol"] as String }

            if (tipo=="Bonos") lista = lista.filter {
                !BDActivos.cedears.contains(it)
                        && !it.endsWith("X")
                        && !it.endsWith("Y")
                        && !it.endsWith("Z")
            }

            Log.i("creapb",lista.joinToString("-"))
            return lista.map { crearBonoBYMA(it, jsonStr, tipo) }
        }


        suspend fun crearPanelBYMA(tipo:String):List<Activo>{
            val json = Red.conectar(Urls.urlsByma[tipo]!!, body)
            return crear(tipo, json)
        }

        private suspend fun crearBonoBYMA(ticker: String): Activo {
            val tipo = BDActivos.obtenerTipo(ticker)

            val jsonStr = Red.conectar(Urls.urlsByma[tipo]!!,body)
            val data = jsonToData(jsonStr)

            val papelEncontrado = data.filter { it ["symbol"] == ticker }
            if (papelEncontrado.isEmpty()) return Activo(ticker,0.0, getMoneda(ticker),0.0)
            val papel = papelEncontrado[0]

            val precio = (if (papel["last"] == null) papel["closingPrice"] else papel["last"]) as Double
            val moneda = monedas[papel["denominationCcy"]] as String
            val dif = (papel["imbalance"] as Double) * 100

            //Log.i("byma",papel["symbol"].toString())

            return if (tipo == "Bonos" || tipo == "Obligaciones negociables") Bono(ticker, precio, moneda, dif, obtenerFlujo(ticker))
            else Activo(ticker,precio,moneda,dif)
        }

        private fun crearBonoBYMA(ticker: String, jsonStr: String?, tipo: String): Activo {
            val data = jsonToData(jsonStr)

            val papelEncontrado = data.filter { it ["symbol"] == ticker }
            if (papelEncontrado.isEmpty()) return Activo(ticker,0.0, getMoneda(ticker),0.0)
            val papel = papelEncontrado[0]

            val precio = (if (papel["last"] == null) papel["closingPrice"] else papel["last"]) as Double
            val moneda = monedas[papel["denominationCcy"]].toString()
            val dif = (papel["imbalance"] as Double) * 100

            //Log.i("byma",papel["symbol"].toString())

            return if (tipo == "Bonos" || tipo == "Obligaciones negociables") Bono(ticker, precio, moneda, dif, obtenerFlujo(ticker))
            else Activo(ticker,precio,moneda,dif)
        }


        @Suppress("UNCHECKED_CAST")
        private fun jsonToData(jsonStr: String?): List<Map<*,*>> {
            val gson = Gson()
            if (jsonStr!!.startsWith("[")){
                return gson.fromJson(jsonStr, List::class.java) as List<Map<*, *>>
            }
            return gson.fromJson(jsonStr, Map::class.java)["data"] as List<Map<*,*>>
        }

        private suspend fun calcularMEP(str: String): Activo {
            val tickerP = str.replace("MEP","").trim()

            val bonos = crear(tickerP, dolarizarActivo(tickerP))
            val bonoP = bonos[0]
            val bonoD = bonos[1]

            //if (bonoP == null || bonoD == null) return null

            val dif = (((bonoP.dif+100.0)/(bonoD.dif+100.0))-1.0)*100
            return Activo("MEP $tickerP",bonoP.precio/bonoD.precio, ARS,dif )
        }

        private fun dolarizarActivo(tickerP: String): String {
            if (BDActivos.ONs.contains(tickerP)) return tickerP.removeSuffix("O") + "D"

            return when(tickerP){
                "BA37D" -> "BA7DD"
                "BB37D" -> "BB7DD"
                "BPOA7" -> "BPA7D"
                "BPOB7" -> "BPB7D"
                "BPOC7" -> "BPC7D"
                "BPOD7" -> "BPD7D"
                else -> tickerP + "D"
            }
        }


        private fun getMoneda(ticker: String): String {
            val moneda = if (ticker.endsWith("D")) USD else ARS
            return when (ticker){
                "YPFD" -> ARS
                "BA37D" -> ARS
                "BA7DD" -> USD
                else -> moneda
            }
        }


        private fun obtenerFlujo(ticker: String): List<PagoBono> {
            //todo
            return flujoLoco()
        }

        private fun flujoLoco(): List<PagoBono> {
            return listOf<PagoBono>(
                (PagoBono(LocalDate.of(2023, 5, 1), 10.0, 0.0)),
                (PagoBono(LocalDate.of(2024, 5, 1), 10.0, 0.0)),
                (PagoBono(LocalDate.of(2025, 5, 1), 15.0, 0.0)),
                (PagoBono(LocalDate.of(2026, 5, 1), 20.0, 0.0)),
                (PagoBono(LocalDate.of(2027, 5, 1), 10.0, 0.0)),
                (PagoBono(LocalDate.of(2028, 5, 1), 15.0, 0.0)),
                (PagoBono(LocalDate.of(2029, 5, 1), 20.0, 0.0)),
                (PagoBono(LocalDate.of(2030, 5, 1), 15.0, 0.0)),
                (PagoBono(LocalDate.of(2031, 5, 1), 20.0, 0.0)),
                (PagoBono(LocalDate.of(2032, 5, 1), 10.0, 0.0)),
                (PagoBono(LocalDate.of(2033, 5, 1), 15.0, 0.0)),
                (PagoBono(LocalDate.of(2034, 5, 1), 20.0, 0.0)),
                (PagoBono(LocalDate.of(2035, 5, 1), 25.0, 100.0))
            )
        }


    }

}