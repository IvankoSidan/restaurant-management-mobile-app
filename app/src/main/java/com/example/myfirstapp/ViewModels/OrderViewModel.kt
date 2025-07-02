package com.example.myfirstapp.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.Interfaces.CartManagerListener
import com.example.myfirstapp.Api.OrderApi
import com.example.myfirstapp.Interfaces.ManagementCartListener
import com.example.myfirstapp.Interfaces.OrderRepository
import com.example.myfirstapp.Interfaces.StringProvider
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.DishOrder
import com.example.myfirstapp.data.Models.Order
import com.example.myfirstapp.data.Enums.OrderStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    private val _formattedDishes = MutableLiveData<Map<Long, String>>()
    val formattedDishes: LiveData<Map<Long, String>> = _formattedDishes

    private val _dishOrders = MutableLiveData<List<DishOrder>>()
    val dishOrders: LiveData<List<DishOrder>> = _dishOrders

    private val _currentOrder = MutableLiveData<Order?>()
    val currentOrder: LiveData<Order?> = _currentOrder

    private var cartListener: ManagementCartListener? = null

    suspend fun placeOrderSuspend(order: Order, dishes: List<DishOrder>): Order = withContext(Dispatchers.IO) { repository.placeOrder(order, dishes) }

    fun loadOrders(userId: Long) = viewModelScope.launch {
        _orders.value = repository.loadOrders(userId)
        val newFormattedDishes = mutableMapOf<Long, String>()
        _orders.value?.forEach { order ->
            newFormattedDishes.putAll(repository.getFormattedDishesForOrder(order.orderId))
        }
        _formattedDishes.value = newFormattedDishes
    }

    fun deleteOrder(order: Order) = viewModelScope.launch {
        repository.deleteOrder(order.orderId, order.userId)
        loadOrders(order.userId)
    }

    fun getDishesByOrderId(orderId: Long) = viewModelScope.launch {
        _dishOrders.value = repository.getDishesByOrderId(orderId)
    }

    fun setCurrentOrder(order: Order) {
        _currentOrder.value = order.copy()
    }

    fun updateFullOrder(order: Order, dishOrders: List<DishOrder>) = viewModelScope.launch {
        repository.updateFullOrder(order, dishOrders)
        loadOrders(order.userId)
    }

    fun deleteAllDishesByOrderId(orderId: Long) = viewModelScope.launch {
        repository.deleteAllDishesByOrderId(orderId)
    }

    fun clearLastOrder() {
        _currentOrder.value = null
    }

    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) = viewModelScope.launch {
        val updatedOrder = repository.updateOrderStatus(orderId, newStatus)
        loadOrders(updatedOrder.userId)
    }

    fun setCartListener(listener: ManagementCartListener) {
        cartListener = listener
    }

    suspend fun fetchDishOrders(orderId: Long): List<DishOrder> {
        return repository.getDishesByOrderId(orderId)
    }

    fun loadFormattedDishes(orderId: Long) = viewModelScope.launch {
        _formattedDishes.value = repository.getFormattedDishesForOrder(orderId)
    }



    suspend fun getDishesByIds(ids: List<Long>): List<Dish> = repository.getDishesByIds(ids)
}
