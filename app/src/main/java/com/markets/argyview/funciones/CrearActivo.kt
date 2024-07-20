package com.markets.argyview.funciones

import android.util.Log
import com.markets.argyview.activos.Activo
import com.markets.argyview.activos.Bono
import com.markets.argyview.activos.PagoBono
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
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

            val urlsByma = hashMapOf(
                Pair("Bonos", urlBymaBonos) ,
                Pair("Obligaciones negociables", urlBymaONs),
                Pair("Acciones", urlBymaAcciones),
                Pair("Panel General", urlBymaPGeneral),
                Pair("Cedears", urlBymaCedears)
            )
        }
    }

    companion object{

        val ARS = "$"
        val USD = "US$"

        suspend fun crear(str: String):Activo?{
            val ticker = str.uppercase()
            return when {
                ticker == "MEP" || ticker == "MEP " || ticker == "DOLAR MEP" || ticker == "DÓLAR MEP" ->
                    calcularMEP("AL30")
                ticker.contains("MEP ") -> calcularMEP(ticker)
                else -> crearBonoBolsar(ticker)
            }
        }
        suspend fun crear(vararg arr: String):List<Activo> = crear(arr.toList())

        suspend fun crear(arr: List<String>):List<Activo>{
            //val doc = Red.conectar(Urls.urlBolsarBonos)
            val docs = hashMapOf<String, Document?>()
            return arr.map {
                val ticker = it.uppercase()
                lateinit var tipo: String
                if (!ticker.contains("MEP")) {
                    tipo = BDActivos.obtenerTipo(ticker)
                    if (!docs.containsKey(tipo)) docs.put(tipo, Red.conectar(Urls.urlsBolsar[tipo]!!))
                }
                return@map when {
                    ticker == "MEP" || ticker == "MEP " || ticker == "DOLAR MEP" || ticker == "DÓLAR MEP" ->
                        calcularMEP("AL30")!!
                    ticker.contains("MEP ") -> calcularMEP(ticker)!!
                    else -> crearBonoBolsar(ticker, docs[tipo])
                }
            }
        }

        fun crear(tipo: String, doc: Document?):List<Activo>{
            val rows = doc!!.select("#lideres > tbody > tr")

            val rows24 = rows.filter { element -> element.id().endsWith("_24hs") }
            val primerasCeldas = rows24.map { it.select("td:nth-child(1)") }
            var lista = primerasCeldas.map { it.text() }

            //por error de bolsar
            if (tipo=="Bonos") lista = lista.filter {
                !BDActivos.cedears.contains(it)
                        && !it.endsWith("X")
                        && !it.endsWith("Y")
                        && !it.endsWith("Z")
            }

            Log.i("creapb",lista.joinToString("-"))
            return lista.map { crearBonoBolsar(it,doc) }
        }

        suspend fun crearPanelBolsar(tipo:String):List<Activo>{
            val doc = Red.conectar(Urls.urlsBolsar[tipo]!!)
            return crear(tipo, doc)
        }

        private suspend fun calcularMEP(str: String): Activo? {
            val tickerP = str.replace("MEP","").trim()

            val bonos = crear(tickerP, dolarizarActivo(tickerP))
            val bonoP = bonos[0]
            val bonoD = bonos[1]

            if (bonoP == null || bonoD == null) return null

            val dif = (((bonoP.dif+100.0)/(bonoD.dif+100.0))-1.0)*100
            return Activo("MEP "+tickerP,bonoP.precio/bonoD.precio, ARS,dif )
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

        private suspend fun crearBonoBonistas(str:String):Bono{
            var ticker = str
            var moneda = ARS
            if (ticker.startsWith("AL") || ticker.startsWith("AE") ||
                ticker.startsWith("GD") ){
                moneda = USD
                if (!ticker.endsWith("D")) ticker += "D"
            }

            val doc = Red.conectar(Urls.urlBonistas)
            val row = doc!!.getElementById(ticker + "_2") ?: throw Exception("No existe el activo $ticker")

            //var ticker = row!!.getElementById("ticker")!!.text()
            val precio = row.getElementById("last_price")!!.text().toDouble()
            val dif = row.getElementById("day_difference")!!.text()
                .replace("=","")
                .replace("+","")
                .replace("%","").toDouble()

            return Bono(ticker,precio,moneda,dif,obtenerFlujo(ticker))
        }


        private suspend fun crearBonoBolsar(str: String): Activo {
            val ticker = str
            val moneda = establecerMoneda(ticker)
            val tipo = BDActivos.obtenerTipo(ticker)

            val doc = Red.conectar(Urls.urlsBolsar[tipo]!!)
            val row = doc!!.getElementById(ticker + "_24hs") ?: throw Exception("No existe el activo $ticker")

            val precio = if (row.selectFirst("td:nth-child(7)")!!.text()=="-") 0.0
                        else row.selectFirst("td:nth-child(7)")!!.text()
                            .replace(".","")
                            .replace(",",".").toDouble()
            val dif = row.selectFirst("td:nth-child(8)")!!.text()
                .replace("%","")
                .replace(",",".").toDouble()

            return if (tipo == "Bonos" || tipo == "Obligaciones negociables") Bono(ticker, precio, moneda, dif, obtenerFlujo(ticker))
            else Activo(ticker,precio,moneda,dif)
        }
        private fun crearBonoBolsar(str: String, doc:Document?): Activo {
            val ticker = str
            val moneda = establecerMoneda(ticker)
            val span = doc!!.selectFirst(".mercados")!!.text()
            val tipo = if(span.contains("Bonos")) "Bonos"
                        else if (span.contains("Obligaciones")) "Obligaciones negociables"
                        else "Otro"
            Log.i("span","$ticker $span $tipo")

            val row = doc.getElementById(ticker + "_24hs") ?: throw Exception("No existe el activo $ticker")

            val precio = if (row.selectFirst("td:nth-child(7)")!!.text()=="-") 0.0
                        else row.selectFirst("td:nth-child(7)")!!.text()
                            .replace(".","")
                            .replace(",",".").toDouble()
            val dif = row.selectFirst("td:nth-child(8)")!!.text()
                .replace("%","")
                .replace(",",".").toDouble()

            return if (tipo == "Bonos" || tipo == "Obligaciones negociables") Bono(ticker, precio, moneda, dif, obtenerFlujo(ticker))
            else Activo(ticker,precio,moneda,dif)
        }

        suspend fun crearBonoBYMA(str: String): Bono? {
            val ticker = str
            var moneda = establecerMoneda(ticker)

            var json = Red.conectar(Urls.urlBymaBonos,
                "{\"T2\":false,\"T1\":true,\"T0\":false,\"Content-Type\":\"application/json\"}")
            json=json.toString()
            Log.i("BYMADATA",json)
            return null

            var precio = "settlementPrice"
            var dif = "imbalance"// * 100

            return Bono(json,0.0,"0",0.0,obtenerFlujo(ticker))
            //return Bono(ticker,precio,moneda,dif,obtenerFlujo(ticker))
        }

        private fun establecerMoneda(ticker: String): String {
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