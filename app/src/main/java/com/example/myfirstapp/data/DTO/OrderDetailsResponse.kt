package com.example.myfirstapp.data.DTO

data class OrderDetailsResponse(
    val orderId: Long,
    val dishes: List<DishOrderDetail>
)
