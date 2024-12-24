package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.Dish

interface DishCategoryListener {
    fun loadDishesByCategory(nameCategory : String)
    fun loadSelectedDish(dish: Dish)
}
