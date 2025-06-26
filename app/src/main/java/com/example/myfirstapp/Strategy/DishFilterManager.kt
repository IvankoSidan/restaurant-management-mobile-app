package com.example.myfirstapp.Strategy

import com.example.myfirstapp.Interfaces.FilterStrategy
import com.example.myfirstapp.data.Models.Dish

class DishFilterManager(private val strategies: List<FilterStrategy>) {
    fun applyFilters(dishes: List<Dish>): List<Dish> {
        var filteredDishes = dishes
        for (strategy in strategies) {
            filteredDishes = strategy.filter(filteredDishes)
        }
        return filteredDishes
    }
}