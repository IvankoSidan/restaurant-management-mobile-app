package com.example.myfirstapp.data.DTO

data class PaymentRequest(
    val orderId: Long,
    val cardToken: String,
    val cvv: String
)