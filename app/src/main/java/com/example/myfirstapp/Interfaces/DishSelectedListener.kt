package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.Dish

interface DishSelectedListener {
    fun loadSelectedDish(dish: Dish)
}
