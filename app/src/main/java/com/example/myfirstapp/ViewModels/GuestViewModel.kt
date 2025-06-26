package com.example.myfirstapp.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.Api.HomeApi
import com.example.myfirstapp.Interfaces.FavoriteDishListener
import com.example.myfirstapp.Interfaces.GuestRepository
import com.example.myfirstapp.Interfaces.ManagementCartListener
import com.example.myfirstapp.Interfaces.StringProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Models.Category
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.FavoriteDish
import com.example.myfirstapp.data.Models.User
import kotlinx.coroutines.launch
import java.util.Locale

class GuestViewModel(private val repository: GuestRepository) : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _dishes = MutableLiveData<List<Dish>>()
    val dishes: LiveData<List<Dish>> = _dishes

    private val _bestDishes = MutableLiveData<List<Dish>>()
    val bestDishes: LiveData<List<Dish>> = _bestDishes

    private val _favoriteDishes = MutableLiveData<List<Dish>>()
    val favoriteDishes: LiveData<List<Dish>> = _favoriteDishes

    private val _selectedDishes = MutableLiveData<MutableList<Dish>>(mutableListOf())
    val selectedDishes: LiveData<MutableList<Dish>> = _selectedDishes

    private val _guest = MutableLiveData<User?>()
    val guest: MutableLiveData<User?> = _guest

    private val _currentDish = MutableLiveData<Dish>()
    val currentDish: LiveData<Dish> = _currentDish

    private val _dishesByCategory = MutableLiveData<List<Dish>>()
    val dishesByCategory: LiveData<List<Dish>> = _dishesByCategory

    private val _dishesByName = MutableLiveData<List<Dish>>()
    val dishesByName: LiveData<List<Dish>> = _dishesByName

    private val _foodCategory = MutableLiveData<String?>()
    val foodCategory: LiveData<String?> = _foodCategory

    private var favoriteDishListener: FavoriteDishListener? = null

    fun setFavoriteDishListener(listener: FavoriteDishListener) {
        favoriteDishListener = listener
    }

    fun clearCart() {
        _selectedDishes.value = mutableListOf()
    }

    fun clearUser() {
        _guest.value = null
    }

    fun loadCategories() = loadData(_categories) { repository.getAllCategories() }

    fun loadAllDishes() = loadData(_dishes) { repository.getAllDishes() }

    fun loadAllBestDishes() = loadData(_bestDishes) { repository.getAllBestDishes() }

    fun loadDishesByName(query: String) = loadData(_dishesByName) { repository.getDishesByName(query) }

    fun loadDishesByCategory(category: String) = loadData(_dishesByCategory) {
        _foodCategory.value = category
        repository.getDishesByCategory(category)
    }

    fun loadFavoriteDishes() = viewModelScope.launch {
        _guest.value?.let {
            _favoriteDishes.value = repository.getFavoriteDishes(it.idUser)
        }
    }

    fun addFavoriteDish(userId: Long, dishId: Long) = viewModelScope.launch {
        repository.addFavoriteDish(userId, dishId)
    }

    fun addToCart(dish: Dish, quantity: Int) = viewModelScope.launch {
        _selectedDishes.value = repository.addToCart(_selectedDishes.value ?: mutableListOf(), dish, quantity)
    }

    fun updateItemQuantity(position: Int, increment: Boolean) = viewModelScope.launch {
        _selectedDishes.value = repository.updateItemQuantity(_selectedDishes.value ?: mutableListOf(), position, increment)
    }

    fun getTotalFee(): Double = repository.getTotalFee(_selectedDishes.value ?: mutableListOf())

    fun clearDishes() {
        _dishes.value = repository.clearDishes()
        _bestDishes.value = repository.clearDishes()
    }

    fun clearCategories() {
        _categories.value = repository.clearCategories()
    }

    fun clearDishesByCategory() {
        _dishes.value = repository.clearDishesByCategory()
    }

    fun clearSearchResults() {
        _dishes.value = repository.clearSearchResults()
    }


    fun removeFavoriteDish(userId: Long, dishId: Long) = viewModelScope.launch {
        repository.removeFavoriteDish(userId, dishId)
        loadFavoriteDishes()
    }

    private fun <T> loadData(liveData: MutableLiveData<T>, action: suspend () -> T) = viewModelScope.launch {
        liveData.value = action()
    }

    fun setUser(user: User) {
        _guest.value = user
    }

    fun setCurrentDish(dish: Dish) {
        _currentDish.value = dish
    }

    fun updateProfile(updatedUser: User) = viewModelScope.launch {
        val response = repository.updateProfile(updatedUser)
        if (response.isSuccessful) {
            _guest.value = response.body()
        }
    }
}
