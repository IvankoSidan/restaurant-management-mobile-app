package com.example.myfirstapp.data.DTO

data class PaymentResult(
    val success: Boolean,
    val transactionId: String? = null,
    val message: String? = null
)