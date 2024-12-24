package com.example.myfirstapp.data.Models

data class DishOrder(
    val dishOrderId: Long = 0,
    val orderId: Long,
    val dishId: Long,
    val quantity: Int
)
