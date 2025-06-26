package com.example.myfirstapp.Strategy

import com.example.myfirstapp.Interfaces.FilterStrategy
import com.example.myfirstapp.data.Models.Dish

class RateFilterStrategy(private val rateIndex: Int) : FilterStrategy {
    override fun filter(dishes: List<Dish>): List<Dish> {
        return dishes.filter { dish ->
            when (rateIndex) {
                0 -> true
                1 -> dish.star in 3.0..4.0
                2 -> dish.star in 4.0..4.5
                3 -> dish.star in 4.5..5.0
                else -> true
            }
        }
    }
}