package com.example.myfirstapp.Utils

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(private val maxRetries: Int = 3) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var response: Response? = null
        var exception: IOException? = null

        while (attempt < maxRetries) {
            try {
                response = chain.proceed(chain.request())
                return response
            } catch (e: IOException) {
                exception = e
                attempt++
                Thread.sleep(2000)
            }
        }
        throw exception ?: IOException("Unknown network error")
    }
}
