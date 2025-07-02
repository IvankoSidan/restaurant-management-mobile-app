package com.example.myfirstapp.data.DTO

data class PaymentRequestDto(
    val orderId: Long,
    val cardToken: String,
    val cvv: String
)