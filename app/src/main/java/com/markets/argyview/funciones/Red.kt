package com.markets.argyview.funciones

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class Red {
    companion object{


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


        suspend fun conectar(url:String, body:String, optionsHeader:String="renta-fija"): String? {
            return withContext(Dispatchers.IO) {
                try {
                    val asyncResult = async {
                        val response = Jsoup.connect(url)
                            .header("Accept","application/json, text/plain, */*")
                            .header("Accept-Language","es-US,es-419;q=0.9,es;q=0.8,en;q=0.7")
                            .header("cache-control","no-cache,no-store,max-age=1,must-revaliidate")
                            .header("Connection","keep-alive")
                            .header("Content-Type","application/json")
                            .header("Origin","https://open.bymadata.com.ar")
                            .header("Referer","https://open.bymadata.com.ar/")

                            .header("expires","1")
                            .header("options",optionsHeader)

                            .header("sec-ch-ua","\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"")
                            .header("Sec-Fetch-Dest","empty")
                            .header("sec-ch-ua-mobile","?0")
                            .header("Sec-Fetch-Mode","cors")
                            .header("sec-ch-ua-platform","\"Windows\"")
                            .header("Sec-Fetch-Site","same-origin")
                            .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36")

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

        @SuppressLint("CustomX509TrustManager", "TrustAllX509TrustManager")
        private fun sslCert(): SSLContext {
            val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate>? {
                    return null
                }
                override fun checkClientTrusted(certs: Array<java.security.cert.X509Certificate>?, authType: String?) {
                    // Do nothing - accept all clients
                }
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