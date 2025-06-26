package com.example.myfirstapp.Api

import com.example.myfirstapp.data.Models.Card
import com.example.myfirstapp.data.Enums.PaymentStatus
import com.example.myfirstapp.data.Models.Payment
import com.example.myfirstapp.data.Models.PaymentMethod
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PaymentApi {
    @POST("api/payments")
    suspend fun createPayment(
        @Body payment: Payment
    ): Response<Payment>

    @POST("api/cards")
    suspend fun createCard(
        @Body card: Card
    ): Response<Card>

    @POST("/api/payment-methods")
    suspend fun addPaymentMethod(@Body paymentMethod: PaymentMethod): Response<PaymentMethod>

    @GET("/api/payment-methods/{userId}")
    suspend fun getPaymentMethods(@Path("userId") userId: Long): Response<List<PaymentMethod>>

    @DELETE("api/payment-methods/{id}")
    suspend fun deletePaymentMethod(
        @Path("id") id: Long
    ): Response<Unit>
}