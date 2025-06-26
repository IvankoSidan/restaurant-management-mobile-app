package com.example.myfirstapp.data.DTO

data class DishOrderDetail(
    val dishId: Long,
    val defaultName: String,
    val translatedTitle: String?,
    val quantity: Int
)