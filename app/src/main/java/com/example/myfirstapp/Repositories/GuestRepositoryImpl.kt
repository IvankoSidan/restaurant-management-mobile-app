package com.example.myfirstapp.Repositories

import android.util.Log
import com.example.myfirstapp.Api.HomeApi
import com.example.myfirstapp.Interfaces.GuestRepository
import com.example.myfirstapp.data.Models.Category
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.FavoriteDish
import com.example.myfirstapp.data.Models.User
import retrofit2.Response
import java.util.Locale

class GuestRepositoryImpl(private val homeApi: HomeApi) : GuestRepository {
    override suspend fun getAllCategories(): List<Category> {
        return homeApi.getAllCategories(Locale.getDefault().language)
    }

    override suspend fun getAllDishes(): List<Dish> {
        return homeApi.getAllDishes(Locale.getDefault().language)
    }

    override suspend fun getAllBestDishes(): List<Dish> {
        return homeApi.getAllBestDishes(true, Locale.getDefault().language)
    }

    override suspend fun getDishesByName(name: String): List<Dish> {
        return homeApi.getDishesByName(name, Locale.getDefault().language)
    }

    override suspend fun getDishesByCategory(categoryName: String): List<Dish> {
        return homeApi.getDishesByCategory(categoryName, Locale.getDefault().language)
    }

    override suspend fun getFavoriteDishes(userId: Long): List<Dish> {
        val favorites = homeApi.getFavoriteDishes(userId, Locale.getDefault().language)
        return favorites.map { homeApi.getDishById(it.dishId, Locale.getDefault().language) }
    }

    override suspend fun existsFavoriteDish(userId: Long, dishId: Long): Boolean {
        return homeApi.existsFavoriteDish(userId, dishId)
    }

    override suspend fun addFavoriteDish(userId: Long, dishId: Long) {
        val favoriteDish = FavoriteDish(userId = userId, dishId = dishId)
        homeApi.addFavoriteDish(favoriteDish)
    }

    override suspend fun updateUser(idUser: Long, user: User): Response<User> {
        return homeApi.updateUser(idUser, user)
    }

    override suspend fun addToCart(selectedDishes: MutableList<Dish>, dish: Dish, quantity: Int): MutableList<Dish> {
        val existingDishIndex = selectedDishes.indexOfFirst { it.idDish == dish.idDish }
        if (existingDishIndex != -1) {
            selectedDishes[existingDishIndex].quantity += quantity
            if (selectedDishes[existingDishIndex].quantity <= 0) {
                selectedDishes.removeAt(existingDishIndex)
            }
        } else {
            selectedDishes.add(dish.copy(quantity = quantity))
        }
        return selectedDishes.distinctBy { it.idDish }.toMutableList()
    }

    override suspend fun updateItemQuantity(selectedDishes: MutableList<Dish>, position: Int, increment: Boolean): MutableList<Dish> {
        if (position in selectedDishes.indices) {
            if (increment) {
                selectedDishes[position].quantity += 1
            } else {
                if (selectedDishes[position].quantity > 1) {
                    selectedDishes[position].quantity -= 1
                } else {
                    selectedDishes.removeAt(position)
                }
            }
        }
        return selectedDishes
    }


    override fun getTotalFee(selectedDishes: List<Dish>): Double {
        return selectedDishes.sumOf { it.price * it.quantity }
    }

    override fun clearDishes(): List<Dish> {
        return emptyList()
    }

    override fun clearCategories(): List<Category> {
        return emptyList()
    }

    override fun clearDishesByCategory(): List<Dish> {
        return emptyList()
    }

    override fun clearSearchResults(): List<Dish> {
        return emptyList()
    }

    override fun clearFavoriteDishes(): List<Dish> {
        return emptyList()
    }

    override suspend fun removeFavoriteDish(userId: Long, dishId: Long) {
        homeApi.removeFavoriteDish(userId, dishId)
    }

    override suspend fun updateProfile(updatedUser: User): Response<User> {
        return homeApi.updateUser(updatedUser.idUser, updatedUser)
    }
}
