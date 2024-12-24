package com.example.myfirstapp.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.Interfaces.CartManagerListener
import com.example.myfirstapp.Api.OrderApi
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.DishOrder
import com.example.myfirstapp.data.Models.Order
import kotlinx.coroutines.launch

class OrderViewModel(private val orderApi: OrderApi) : ViewModel() {
    private val orders = MutableLiveData<List<Order>>()
    val _orders: LiveData<List<Order>> get() = orders

    private val formattedDishesMap = MutableLiveData<Map<Long, String>>()
    val _formattedDishes: LiveData<Map<Long, String>> get() = formattedDishesMap

    private val dishOrders = MutableLiveData<List<DishOrder>>()
    val _dishOrders: LiveData<List<DishOrder>> get() = dishOrders

    private val currentOrder = MutableLiveData<Order>()
    val _currentOrder: LiveData<Order> get() = currentOrder

    private var cartManager : CartManagerListener? = null

    fun setCartListener(listener: CartManagerListener) {
        cartManager = listener
    }

    fun placeOrder(order: Order, dishes: List<DishOrder>) = viewModelScope.launch {
        val createdOrderResponse = orderApi.createOrder(order)
        createdOrderResponse.body()?.let { orderResponse ->
            dishes.forEach { dishOrder ->
                orderApi.addDishToOrder(orderResponse.orderId, dishOrder)
            }
            loadOrders(order.userId)
        }
    }

    fun loadOrders(userId: Long) = viewModelScope.launch {
        val response = orderApi.getOrdersByUserId(userId)
        orders.value = response.body()
        orders.value?.forEach { order ->
            getFormattedDishesForOrder(order.orderId)
        }
    }

    private suspend fun getFormattedDishesForOrder(orderId: Long) {
        val response = orderApi.getDishesByOrderId(orderId)
        response.body()?.let { dishOrdersList ->
            val formattedDishesString = dishOrdersList.map { dishOrder ->
                val dish = getDishById(dishOrder.dishId)
                "- ${dish.title} (${dishOrder.quantity})"
            }.joinToString("\n")

            val currentMap = formattedDishesMap.value ?: emptyMap()
            formattedDishesMap.value = currentMap + (orderId to formattedDishesString)
        }
    }

    fun deleteOrder(order: Order) = viewModelScope.launch {
        orderApi.deleteOrder(order.orderId)
        loadOrders(order.userId)
    }

    suspend fun getDishesByOrderId(orderId: Long) {
        val response = orderApi.getDishesByOrderId(orderId)
        dishOrders.value = response.body()
    }

    fun setCurrentOrder(order: Order) {
        currentOrder.value = order
    }

    fun resumeOrder() = viewModelScope.launch {
        val lastOrder = orders.value?.lastOrNull()
        lastOrder?.let { order ->
            getDishesByOrderId(order.orderId)
            _dishOrders.observeForever { dishOrders ->
                viewModelScope.launch {
                    dishOrders.forEach { dishOrder ->
                        val dish = getDishById(dishOrder.dishId).apply {
                            quantity = dishOrder.quantity
                        }
                        cartManager?.addToCart(dish, dish.quantity)
                    }
                }
            }
        }
    }

    suspend fun getDishById(dishId: Long): Dish {
        val response = orderApi.getDishById(dishId)
        return response.body() ?: throw Exception("Dish not found")
    }

    fun updateFullOrder(order: Order, dishOrders: List<DishOrder>) = viewModelScope.launch {
        orderApi.updateOrder(order)
        dishOrders.forEach { dishOrder ->
            orderApi.addDishToOrder(order.orderId, dishOrder)
        }
        loadOrders(order.userId)
    }

    fun deleteAllDishesByOrderId(orderId: Long) = viewModelScope.launch {
        orderApi.deleteDishesByOrderId(orderId)
    }
}
