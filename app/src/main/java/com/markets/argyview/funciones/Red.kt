package com.markets.argyview.funciones

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class Red {
    companion object{

        /*@Throws(IOException::class)
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
        }*/
        /*fun downloadData(url:String, body: String):String {
            //Envia solicitud HTTP y devuelve String
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val inputStream: InputStream? = null
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
        }*/

        @OptIn(DelicateCoroutinesApi::class)
        @JvmStatic
        fun conectar2(url:String): Document? {
            //Envia solicitud HTTP y devuelve documentoHTML de Jsoup
            //val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            //StrictMode.setThreadPolicy(policy)

            var doc:Document? = null
            runBlocking(Dispatchers.IO) {
                doc = Jsoup.connect(url).get()
            }

            return doc
        }

        suspend fun conectar(url: String): Document? {
            return withContext(Dispatchers.IO) {
                try {
                    val asyncResult = async {
                        Jsoup.connect(url).get()
                    }
                    asyncResult.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }


        suspend fun conectar(url:String, body:String): String? {
            return withContext(Dispatchers.IO) {
                try {
                    val asyncResult = async {
                        val response = Jsoup.connect(url)
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
                            .sslSocketFactory(sslCert().socketFactory)
                            .post()
                            .body()
                            .text()

                        response
                    }
                    asyncResult.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }


        }

        private fun sslCert(): SSLContext {
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate>? {
                    return null
                }
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(certs: Array<java.security.cert.X509Certificate>?, authType: String?) {
                    // Do nothing - accept all clients
                }
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(certs: Array<java.security.cert.X509Certificate>?, authType: String?) {
                    // Do nothing - accept all servers
                }
            })
            val sc: SSLContext = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, java.security.SecureRandom())
            return sc
        }



        @JvmStatic
        fun isConnected(activity: AppCompatActivity):Boolean{
            val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return  networkInfo != null && networkInfo.isConnected
        }

    }
}