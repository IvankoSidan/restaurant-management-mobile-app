package com.example.myfirstapp.data.Models

import com.example.myfirstapp.data.Enums.OrderStatus

data class Order(
    val orderId: Long = 0,
    val userId: Long,
    val bookingId: Long?,
    val totalAmount: Double,
    var status: OrderStatus,
    val orderDate: String
)
