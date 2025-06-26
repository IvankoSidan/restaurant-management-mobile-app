package com.example.myfirstapp.Strategy

import com.example.myfirstapp.Interfaces.FilterStrategy
import com.example.myfirstapp.data.Models.Dish

class PriceFilterStrategy(private val priceIndex: Int) : FilterStrategy {
    override fun filter(dishes: List<Dish>): List<Dish> {
        return dishes.filter { dish ->
            when (priceIndex) {
                0 -> true
                1 -> dish.price in 1.0..10.0
                2 -> dish.price in 10.0..30.0
                3 -> dish.price > 30.0
                else -> true
            }
        }
    }
}
