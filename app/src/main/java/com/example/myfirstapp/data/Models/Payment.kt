package com.example.myfirstapp.data.Models

import com.example.myfirstapp.data.Enums.PaymentStatus

data class Payment(
    val idPayment : Long = 0,
    val orderId: Long,
    val paymentAmount: Float,
    val paymentDate: String,
    val paymentStatus: PaymentStatus
)
