package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.DTO.CardRequestDto
import com.example.myfirstapp.data.DTO.PaymentRequest
import com.example.myfirstapp.data.DTO.PaymentResult
import com.example.myfirstapp.data.Models.Card
import com.example.myfirstapp.data.Models.Payment
import com.example.myfirstapp.data.Models.PaymentMethod
import retrofit2.Response

interface PaymentRepository {
    suspend fun addPaymentMethod(newMethod: PaymentMethod)
    suspend fun getPaymentMethods(userId: Long): List<PaymentMethod>
    suspend fun createCard(card: Card): Response<Card>
    suspend fun deletePaymentMethod(paymentMethodId: Long)
    suspend fun processPayment(request: PaymentRequest): Response<PaymentResult>
    suspend fun createCard(card: CardRequestDto): Response<Card>
}
