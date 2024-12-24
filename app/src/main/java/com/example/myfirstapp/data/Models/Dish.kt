package com.example.myfirstapp.data.Models

import com.google.gson.annotations.SerializedName


data class Dish(
    val idDish: Long,
    val title: String,
    val description: String,
    val price: Double,
    val imagePath: String,
    val categoryId: Long,
    val bestFood: Boolean,
    val star: Float,
    val timeValue: Int,
    var quantity: Int
)
