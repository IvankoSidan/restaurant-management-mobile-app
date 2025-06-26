package com.example.myfirstapp.DI

import android.content.Context
import com.example.myfirstapp.Api.AuthApi
import com.example.myfirstapp.Api.HomeApi
import com.example.myfirstapp.Api.OrderApi
import com.example.myfirstapp.Api.PaymentApi
import com.example.myfirstapp.Api.ReserveTableApi
import com.example.myfirstapp.Objects.AuthInterceptor
import com.example.myfirstapp.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

val networkModule = module {
    single { provideOkHttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideAuthApi(get()) }
    single { provideHomeApi(get()) }
    single { provideOrderApi(get()) }
    single { provideReserveTableApi(get()) }
    single { providePaymentApi(get()) }
}

fun provideOkHttpClient(context: Context): OkHttpClient {
    val certificateFactory = CertificateFactory.getInstance("X.509")
    val inputStream: InputStream = context.resources.openRawResource(R.raw.localhost_android)
    val certificate = certificateFactory.generateCertificate(inputStream)
    inputStream.close()

    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null, null)
        setCertificateEntry("ca", certificate)
    }

    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
        init(keyStore)
    }

    val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, trustManagerFactory.trustManagers, SecureRandom())
    }

    return OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor()) // Ваш AuthInterceptor
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .sslSocketFactory(sslContext.socketFactory, trustManagerFactory.trustManagers[0] as X509TrustManager)
        .hostnameVerifier { hostname, _ -> hostname == "10.0.2.2" }
        .build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://10.0.2.2:8443/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
fun provideHomeApi(retrofit: Retrofit): HomeApi = retrofit.create(HomeApi::class.java)
fun provideOrderApi(retrofit: Retrofit): OrderApi = retrofit.create(OrderApi::class.java)
fun provideReserveTableApi(retrofit: Retrofit): ReserveTableApi = retrofit.create(ReserveTableApi::class.java)
fun providePaymentApi(retrofit: Retrofit): PaymentApi = retrofit.create(PaymentApi::class.java)
