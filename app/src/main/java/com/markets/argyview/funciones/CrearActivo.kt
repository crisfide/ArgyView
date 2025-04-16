package com.markets.argyview.funciones

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.util.Log
import com.google.gson.Gson
import com.markets.argyview.activos.Activo
import com.markets.argyview.activos.Bono
import com.markets.argyview.activos.FlujoBono
import com.markets.argyview.activos.PagoBono
import org.threeten.bp.Instant

import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

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

            fun optionsHeader (tipo:String): String{
                return when (tipo){
                    "Bonos", "Obligaciones negociables", "Letras" -> "renta-fija"
                    "Acciones", "Panel General", "Cedears" -> "renta-variable"
                    "Opciones" -> "derivados"
                    else -> ""
                }
            }
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

        val bodyByma = "{\"excludeZeroPxAndQty\":true,\"T2\":false,\"T1\":true,\"T0\":false,\"Content-Type\":\"application/json\",\"page_size\":400}"

        private lateinit var preferences: SharedPreferences
        private lateinit var assets: AssetManager

        fun initPrefs(context : Context) {
            preferences = context.getSharedPreferences("db", 0)
            assets = context.assets
        }

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
                    if (!docs.containsKey(tipo)) docs[tipo] = obtenerJson(tipo)
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

            //Log.i("creapb",lista.joinToString("-"))
            return lista.map { crearBonoBYMA(it, data, tipo) }
        }


        suspend fun crearPanelBYMA(tipo:String):List<Activo> = crear(tipo, obtenerJson(tipo))

        fun crearPanelBYMA(tipo:String, json:String?):List<Activo> = crear(tipo, json)


        private suspend fun crearBonoBYMA(ticker: String): Activo {
            val tipo = BDActivos.obtenerTipo(ticker)
            val jsonStr = obtenerJson(tipo)

            return crearBonoBYMA(ticker,jsonStr,tipo)
        }

        private fun crearBonoBYMA(ticker: String, jsonStr: String?, tipo: String): Activo {
            val data = jsonToData(jsonStr)
            return crearBonoBYMA(ticker,data,tipo)
        }
        private fun crearBonoBYMA(ticker: String, data: List<Map<*, *>>, tipo: String): Activo {

            val papelEncontrado = data.filter { it ["symbol"] == ticker }
            if (papelEncontrado.isEmpty()) return Activo(ticker,0.0, getMoneda(ticker),0.0)
            val papel = papelEncontrado[0]

            val precio = (if (papel.keys.contains("trade")) papel["trade"]
                        else if (papel.keys.contains("settlementPrice")) papel["settlementPrice"]
                        else if (papel.keys.contains("last")) papel["last"]
                        else if (papel.keys.contains("closingPrice")) papel["closingPrice"]
                        else 0.0) as Double
            val moneda = monedas[papel["denominationCcy"]].toString()
            val dif = (papel["imbalance"] as Double) * 100

            //Log.i("byma",papel["symbol"].toString())

            return if (tipo == "Bonos" || tipo == "Obligaciones negociables") Bono(ticker, precio, moneda, dif, obtenerFlujo(ticker))
                else Activo(ticker,precio,moneda,dif)
        }


        suspend fun obtenerJson(tipo: String): String? {
            if (CheckMercado.cerrado()){
                val json = obtenerJsonPref(tipo)
                if (json != null) return json
            }
            return obtenerJsonByma(tipo)
        }

        suspend fun obtenerJsonByma(tipo: String) = Red.conectar(Urls.urlsByma[tipo]!!, bodyByma, Urls.optionsHeader(tipo))

        fun obtenerJsonPref(tipo: String): String? = preferences.getString("json-$tipo", null)


        @Suppress("UNCHECKED_CAST")
        fun jsonToData(jsonStr: String?): List<Map<*,*>> {
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

            if (bonoP.precio==0.0 || bonoD.precio==0.0) return Activo("MEP $tickerP",0.0, ARS,0.0)

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
                "BPJ25" -> "BPJ5D"
                "BPY26" -> "BPY6D"
                else -> tickerP + "D"
            }
        }

        private fun pesificarActivo(tickerD: String): String {
            if (tickerD.endsWith("D") && BDActivos.ONs.contains(tickerD.removeSuffix("D") + "O")) {
                return tickerD.removeSuffix("D") + "O"
            }
            if (tickerD.endsWith("C") && BDActivos.ONs.contains(tickerD.removeSuffix("C") + "O")) {
                return tickerD.removeSuffix("C") + "O"
            }
            if (tickerD.endsWith("C")){
                return tickerD.removeSuffix("C")
            }

            return when (tickerD) {
                "BA7DD" -> "BA37D"
                "BB7DD" -> "BB37D"
                "BPA7D" -> "BPOA7"
                "BPB7D" -> "BPOB7"
                "BPC7D" -> "BPOC7"
                "BPD7D" -> "BPOD7"
                "BPJ5D" -> "BPJ25"
                "BPY6D" -> "BPY26"
                else -> tickerD.removeSuffix("D")
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


        private fun jsonDesdeAssets(nombreArchivo: String): String? {
            return try {
                assets.open(nombreArchivo).bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                null
            }
        }

        private fun obtenerFlujo(ticker: String): FlujoBono {
            val tickerP = pesificarActivo(ticker)
            //Log.i("bono.1",tickerP.toString())

            val jsonStr = jsonDesdeAssets("datosBonos/${tickerP}.json")
                ?: return flujoLoco() as FlujoBono

            //Log.i("bono.json",jsonStr.toString())

            val gson = Gson()
            val json = gson.fromJson(jsonStr, Map::class.java)
            //Log.i("bono.flujo",flujoArr[0].toString())

            val monedaFlujo = monedas[json["moneda"]]!!
            val flujoArr = json["flujo"] as List<Map<String, Double>>

            val flujo = flujoArr.map {
                PagoBono(
                    Instant.ofEpochMilli(it["fecha"]!!.toLong()) // Convertir a Instant
                        .atZone(ZoneId.systemDefault())    // Aplicar zona horaria
                        .toLocalDate(),
                    it["renta"]!!,
                    it["amortizacion"]!!)
            }

            //Log.i("bono.ff",flujo.toString())
            val mep = preferences.getFloat("MEP",1.0f).toDouble()

            return FlujoBono(flujo, monedaFlujo, mep)
        }

        private fun flujoLoco(): List<PagoBono> {
            return FlujoBono(listOf<PagoBono>(
                (PagoBono(LocalDate.of(0, 1, 1), 0.0, 0.0)),
                (PagoBono(LocalDate.of(2099, 1, 1), 0.0, 100.0))
            ), monedas["USD"]!!, 1.0)
        }


    }

}