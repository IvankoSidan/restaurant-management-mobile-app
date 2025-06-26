package com.example.myfirstapp.Repositories

import com.example.myfirstapp.Api.PaymentApi
import com.example.myfirstapp.Interfaces.PaymentRepository
import com.example.myfirstapp.data.Models.Card
import com.example.myfirstapp.data.Models.Payment
import com.example.myfirstapp.data.Models.PaymentMethod
import retrofit2.Response

class PaymentRepositoryImpl(private val paymentApi: PaymentApi) : PaymentRepository {

    override suspend fun addPaymentMethod(newMethod: PaymentMethod) {
        val response = paymentApi.addPaymentMethod(newMethod)
        if (!response.isSuccessful) {
            throw Exception("Failed to add payment method")
        }
    }

    override suspend fun getPaymentMethods(userId: Long): List<PaymentMethod> {
        val response = paymentApi.getPaymentMethods(userId)
        return response.body()?.toMutableList() ?: emptyList()
    }

    override suspend fun createPayment(payment: Payment): Response<Payment> {
        return paymentApi.createPayment(payment)
    }

    override suspend fun createCard(card: Card): Response<Card> {
        return paymentApi.createCard(card)
    }

    override suspend fun deletePaymentMethod(paymentMethodId: Long) {
        val response = paymentApi.deletePaymentMethod(paymentMethodId)
        if (!response.isSuccessful) {
            throw Exception("Failed to delete payment method")
        }
    }
}
