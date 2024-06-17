package com.markets.argyview.funciones

import android.content.Context
import android.net.ConnectivityManager
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Red {
    companion object{
        @Throws(IOException::class)
        fun downloadData(url:String):String {
            //Envia solicitud HTTP y devuelve String
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            var inputStream: InputStream? = null
            try {
                val url = URL(url)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                inputStream = connection.inputStream
                return inputStream.bufferedReader().use{
                    it.readText()
                }
            }finally {
                if(inputStream != null){
                    inputStream.close()
                }
            }
        }
        fun downloadData(url:String, body: String):String {
            //Envia solicitud HTTP y devuelve String
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            var inputStream: InputStream? = null
            try {
                val url = URL(url)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("content-type","application/json; charset=UTF-8")

                connection.doOutput = true
                val outputStream = connection.outputStream
                outputStream.write(body.toByteArray())
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode < 300 ) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    return "Respuesta: ${response.toString()}"
                } else {
                    return "Error HTTP " + connection.responseCode
                }
            }catch (ex:Exception){
                return "Error " + ex.message
            }finally {
                if(inputStream != null){
                    inputStream.close()
                }
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        @JvmStatic
        fun conectar(url:String): Document? {
            //Envia solicitud HTTP y devuelve documentoHTML de Jsoup
            //val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            //StrictMode.setThreadPolicy(policy)

            var doc:Document? = null
            try {
                runBlocking(Dispatchers.IO) {
                    doc = Jsoup.connect(url).get()
                }

            }catch (ex:Exception){
                Log.i("Error", ex.message!!)
            }
            return doc
        }

        @JvmStatic
        fun conectar(url:String, body:String): String? {
            //Envia solicitud HTTP y devuelve documentoHTML de Jsoup

            var response:Document? = null
            try {
                runBlocking(Dispatchers.IO) {
                    response = Jsoup.connect(url)
                        .header("Connection","keep-alive")
                        .header("sec-ch-ua","\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"")
                        .header("Accept","application/json, text/plain, */*")
                        .header("Content-Type","application/json")
                        .header("sec-ch-ua-mobile","?0")
                        .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36")
                        .header("sec-ch-ua-platform","\"Windows\"")
                        .header("Origin","https://open.bymadata.com.ar")
                        .header("Sec-Fetch-Site","same-origin")
                        .header("Sec-Fetch-Mode","cors")
                        .header("Sec-Fetch-Dest","empty")
                        .header("Referer","https://open.bymadata.com.ar/")
                        .header("Accept-Language","es-US,es-419;q=0.9,es;q=0.8,en;q=0.7")
                        .requestBody(body)
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .post()
                }

                return response.toString()
            }catch (ex:Exception){
                Log.i("BYMADATA", ex.message!!)
            }
            return null
        }

        @JvmStatic
        fun isConnected(activity: AppCompatActivity):Boolean{
            val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return  networkInfo != null && networkInfo.isConnected
        }

    }
}