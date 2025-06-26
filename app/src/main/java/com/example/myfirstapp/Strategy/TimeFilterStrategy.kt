package com.example.myfirstapp.Strategy

import com.example.myfirstapp.Interfaces.FilterStrategy
import com.example.myfirstapp.data.Models.Dish


class TimeFilterStrategy(private val timeIndex: Int) : FilterStrategy {
    override fun filter(dishes: List<Dish>): List<Dish> {
        return dishes.filter { dish ->
            when (timeIndex) {
                0 -> true
                1 -> dish.timeValue in 0..10
                2 -> dish.timeValue in 10..30
                3 -> dish.timeValue > 30
                else -> true
            }
        }
    }
}
