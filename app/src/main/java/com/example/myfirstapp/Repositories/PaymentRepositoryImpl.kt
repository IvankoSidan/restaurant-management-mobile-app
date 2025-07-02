package com.example.myfirstapp.Repositories

import com.example.myfirstapp.Api.PaymentApi
import com.example.myfirstapp.Interfaces.PaymentRepository
import com.example.myfirstapp.data.DTO.CardRequestDto
import com.example.myfirstapp.data.DTO.PaymentRequest
import com.example.myfirstapp.data.DTO.PaymentResult
import com.example.myfirstapp.data.Models.Card
import com.example.myfirstapp.data.Models.Payment
import com.example.myfirstapp.data.Models.PaymentMethod
import retrofit2.Response

class PaymentRepositoryImpl(private val paymentApi: PaymentApi) : PaymentRepository {

    override suspend fun addPaymentMethod(newMethod: PaymentMethod) {
        val resp = paymentApi.addPaymentMethod(newMethod)
        if (!resp.isSuccessful) throw Exception("Failed to add payment method")
    }

    override suspend fun getPaymentMethods(userId: Long): List<PaymentMethod> {
        return paymentApi.getPaymentMethods(userId).body().orEmpty()
    }

    override suspend fun createCard(card: Card): Response<Card> {
        return paymentApi.createCard(card)
    }

    override suspend fun createCard(card: CardRequestDto): Response<Card> {
        return paymentApi.createCard(card)
    }

    override suspend fun deletePaymentMethod(paymentMethodId: Long) {
        val resp = paymentApi.deletePaymentMethod(paymentMethodId)
        if (!resp.isSuccessful) throw Exception("Failed to delete payment method")
    }

    override suspend fun processPayment(request: PaymentRequest): Response<PaymentResult> {
        return paymentApi.processPayment(request)
    }
}
