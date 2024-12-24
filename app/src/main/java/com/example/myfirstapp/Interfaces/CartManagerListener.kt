package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.Dish

interface CartManagerListener {
    fun addToCart(dish: Dish, quantity: Int)
}