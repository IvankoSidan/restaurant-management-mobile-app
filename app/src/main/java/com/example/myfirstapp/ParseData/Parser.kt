package com.example.myfirstapp.ParseData

import com.example.myfirstapp.data.Models.Dish
import com.google.gson.Gson
import java.io.InputStreamReader

object Parser {
    fun parseJsonToDishes(inputStreamReader: InputStreamReader): List<Dish> {
        val json = inputStreamReader.readText()
        val foodArray = convertJsonToFoods(json)
        return foodArray.Foods.map { dishJson ->
            Dish(
                idDish = dishJson.Id.toLong(),
                title = dishJson.Title,
                description = dishJson.Description,
                price = dishJson.Price,
                imagePath = dishJson.ImagePath,
                categoryId = dishJson.CategoryId.toLong(),
                bestFood = dishJson.BestFood,
                star = dishJson.Star.toFloat(),
                timeValue = dishJson.TimeValue,
                quantity = 0
            )
        }
    }

    private fun convertJsonToFoods(json: String): Foods {
        val gson = Gson()
        return gson.fromJson(json, Foods::class.java)
    }
}