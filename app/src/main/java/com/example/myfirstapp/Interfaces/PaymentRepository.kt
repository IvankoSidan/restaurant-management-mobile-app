package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.Card
import com.example.myfirstapp.data.Models.Payment
import com.example.myfirstapp.data.Models.PaymentMethod
import retrofit2.Response

interface PaymentRepository {
    suspend fun addPaymentMethod(newMethod: PaymentMethod)
    suspend fun getPaymentMethods(userId: Long): List<PaymentMethod>
    suspend fun createPayment(payment: Payment): Response<Payment>
    suspend fun createCard(card: Card): Response<Card>
    suspend fun deletePaymentMethod(paymentMethodId: Long)
}
