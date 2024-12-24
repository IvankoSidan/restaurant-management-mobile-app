package com.example.myfirstapp.ViewModels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.Api.HomeApi
import com.example.myfirstapp.Interfaces.FavoriteDishListener
import com.example.myfirstapp.Interfaces.FavoriteSelectedListener
import com.example.myfirstapp.data.Models.Category
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.FavoriteDish
import com.example.myfirstapp.data.Models.User
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
class GuestViewModel(private val homeApi: HomeApi) : ViewModel() {


    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _dishes = MutableLiveData<List<Dish>>()
    val dishes: LiveData<List<Dish>> = _dishes

    private val _bestDishes = MutableLiveData<List<Dish>>()
    val bestDishes: LiveData<List<Dish>> = _bestDishes

    private val _dishesByName = MutableLiveData<List<Dish>>()
    val dishesByName: LiveData<List<Dish>> = _dishesByName

    private val _dishesByCategory = MutableLiveData<List<Dish>>()
    val dishesByCategory: LiveData<List<Dish>> = _dishesByCategory

    private val _foodCategory = MutableLiveData<String>()
    val foodCategory: LiveData<String> = _foodCategory

    private val _selectedDishes = MutableLiveData<MutableList<Dish>>(mutableListOf())
    val selectedDishes: LiveData<MutableList<Dish>> = _selectedDishes

    private val _guest = MutableLiveData<User>()
    val guest: LiveData<User> = _guest

    private val _favoriteDishes = MutableLiveData<List<Dish>>()
    val favoriteDishes: LiveData<List<Dish>> = _favoriteDishes

    private val _selectedDish = MutableLiveData<Dish?>()
    val selectedDish: LiveData<Dish?> = _selectedDish

    private var favoriteDishListener: FavoriteDishListener? = null


    fun setFavoriteDishListener(listener: FavoriteDishListener) {
        favoriteDishListener = listener
    }

    fun setCurrentDish(dish: Dish) {
        _selectedDish.value = dish
    }

    fun addToCart(dish: Dish, quantity: Int) {
        val currentDishes = _selectedDishes.value ?: mutableListOf()
        val existingDishIndex = currentDishes.indexOfFirst { it.idDish == dish.idDish }

        if (existingDishIndex != -1) {
            currentDishes[existingDishIndex].quantity += quantity
            if (currentDishes[existingDishIndex].quantity <= 0) {
                currentDishes.removeAt(existingDishIndex)
            }
        } else {
            currentDishes.add(dish.copy(quantity = quantity))
        }
        _selectedDishes.value = currentDishes.distinctBy { it.idDish }.toMutableList()
    }

    fun updateItemQuantity(position: Int, increment: Boolean) {
        _selectedDishes.value?.let { currentDishes ->
            if (position in currentDishes.indices) {
                if (increment) {
                    currentDishes[position].quantity += 1
                } else {
                    if (currentDishes[position].quantity > 1) {
                        currentDishes[position].quantity -= 1
                    } else {
                        currentDishes.removeAt(position)
                    }
                }
                _selectedDishes.value = currentDishes
            }
        }
    }

    fun getTotalFee(): Double {
        return _selectedDishes.value?.sumOf { it.price * it.quantity } ?: 0.0
    }

    fun clearCart() {
        _selectedDishes.value = mutableListOf()
    }

    private fun loadData(action: suspend () -> Unit, errorMessage: String) = viewModelScope.launch {
        try {
            action()
        } catch (e: Exception) {
            Log.e("GuestViewModel", errorMessage, e)
        }
    }

    fun loadCategories() = loadData({ _categories.value = homeApi.getAllCategories() }, "Categories were not found!")

    fun loadAllDishes() = loadData({ _dishes.value = homeApi.getAllDishes() }, "Dishes were not found!")

    fun loadAllBestDishes() = loadData({ _bestDishes.value = homeApi.getAllBestDishes(true) }, "Best dishes were not found!")

    fun loadDishesByName(nameFood: String) = loadData({ _dishesByName.value = homeApi.getDishesByName(nameFood) }, "This dish is not on the menu!")

    fun loadDishesByCategory(categoryName: String) = loadData({
        _foodCategory.value = categoryName
        _dishesByCategory.value = homeApi.getDishesByCategory(categoryName)
    }, "These dishes are not in the category !")

    fun updateProfile(updatedUser: User) = viewModelScope.launch {
        try {
            homeApi.updateUser(updatedUser.id, updatedUser)
            _guest.value = updatedUser
        } catch (e: Exception) {
            Log.e("GuestViewModel", "Failed to update profile: ${e.message}")
        }
    }

    fun setUser(user: User) {
        _guest.value = user
    }

    fun addFavoriteDish(dishId: Long) = viewModelScope.launch {
        _guest.value?.let { user ->
            try {
                if (homeApi.existsFavoriteDish(user.id, dishId)) {
                    favoriteDishListener?.onFavoriteDishAlreadyExists("This dish is already in favorites!")
                } else {
                    addFavorite(userId = user.id, dishId = dishId)
                    loadFavoriteDishes()
                    favoriteDishListener?.onFavoriteDishAdded("The dish has been added to favorites!")
                }
            } catch (e: Exception) {
                Log.e("GuestViewModel", "Failed to add favorite dish: ${e.message}")
            }
        } ?: Log.e("GuestViewModel", "User not logged in")
    }

    fun removeFavoriteDish(dishId: Long) = viewModelScope.launch {
        _guest.value?.let { user ->
            try {
                homeApi.removeFavoriteDish(user.id, dishId)
                loadFavoriteDishes()
            } catch (e: Exception) {
                Log.e("GuestViewModel", "Failed to remove favorite dish: ${e.message}")
            }
        } ?: Log.e("GuestViewModel", "User not logged in")
    }

    fun loadFavoriteDishes() = loadData({
        _guest.value?.let { user ->
            _favoriteDishes.value = getFavoriteDishes(user.id)
        }
    }, "Favorite dishes not found!")

    fun insertDish(dish: Dish) = viewModelScope.launch {
        homeApi.insertDish(dish)
    }

    private suspend fun addFavorite(userId: Long, dishId: Long) {
        val favoriteDish = FavoriteDish(userId = userId, dishId = dishId)
        homeApi.addFavoriteDish(favoriteDish)
    }

    private suspend fun getFavoriteDishes(userId: Long): List<Dish> {
        val favorites = homeApi.getFavoriteDishes(userId)
        return favorites.map { favoriteDish ->
            homeApi.getDishById(favoriteDish.dishId)
        }
    }
}
