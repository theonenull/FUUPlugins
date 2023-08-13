package com.example.fuuplugins.network

import com.example.fuuplugins.config.BuildConfig
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

object OkHttpUtil {
    val trustAllCerts = arrayOf<X509TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
        }

        override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {}

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    })

    val getSSLSocketFactory: SSLSocketFactory by lazy {

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(
            null, trustAllCerts,
            SecureRandom()
        )
        sslContext.socketFactory
    }

    private val defaultOkHttpClient: OkHttpClient by lazy {
        val logInterceptor = HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
//        val logInterceptor = HttpLoggingInterceptor().apply {
//            level =
//                 HttpLoggingInterceptor.Level.NONE
//        }
//        val certificatePinnerBuilder = CertificatePinner.Builder()
//        certificates.forEach {
//            certificatePinnerBuilder.add(it.key, it.value)
//        }
        OkHttpClient.Builder()
//            .addNetworkInterceptor(DoraemonWeakNetworkInterceptor())
//            .addInterceptor(DoraemonInterceptor())
            .addInterceptor(logInterceptor)
//            .apply {
//                if (DataManager.cloudSettings.trustAllCerts) {
//                    hostnameVerifier { _, _ ->
//                        true
//                    }.sslSocketFactory(getSSLSocketFactory, trustAllCerts[0])
//                } else {
//                    certificatePinner(certificatePinnerBuilder.build())
//                }
//            }
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    fun getDefaultClient(): OkHttpClient = defaultOkHttpClient
}