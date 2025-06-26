package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.Category
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.User
import retrofit2.Response

interface GuestRepository {
    suspend fun getAllCategories(): List<Category>
    suspend fun getAllDishes(): List<Dish>
    suspend fun getAllBestDishes(): List<Dish>
    suspend fun getDishesByName(name: String): List<Dish>
    suspend fun getDishesByCategory(categoryName: String): List<Dish>
    suspend fun getFavoriteDishes(userId: Long): List<Dish>
    suspend fun existsFavoriteDish(userId: Long, dishId: Long): Boolean
    suspend fun addFavoriteDish(userId: Long, dishId: Long)
    suspend fun updateUser(idUser: Long, user: User): Response<User>
    suspend fun addToCart(selectedDishes: MutableList<Dish>, dish: Dish, quantity: Int): MutableList<Dish>
    suspend fun updateItemQuantity(selectedDishes: MutableList<Dish>, position: Int, increment: Boolean): MutableList<Dish>
    suspend fun updateProfile(updatedUser: User): Response<User>
    fun getTotalFee(selectedDishes: List<Dish>): Double
    fun clearDishes(): List<Dish>
    fun clearCategories(): List<Category>
    fun clearDishesByCategory(): List<Dish>
    fun clearSearchResults(): List<Dish>
    fun clearFavoriteDishes(): List<Dish>
    suspend fun removeFavoriteDish(userId: Long, dishId: Long)
}

