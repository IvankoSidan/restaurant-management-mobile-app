package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.Dish

interface FavoriteSelectedListener {
    fun loadSelectedDish(dish: Dish)
    fun onRemoved(dish: Dish)
}
