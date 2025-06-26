package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.Dish

interface FilterStrategy {
    fun filter(dishes: List<Dish>): List<Dish>
}