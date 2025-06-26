package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Enums.OrderStatus
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.DishOrder
import com.example.myfirstapp.data.Models.Order

interface OrderRepository {
    suspend fun placeOrder(order: Order, dishes: List<DishOrder>): Order
    suspend fun loadOrders(userId: Long): List<Order>
    suspend fun getFormattedDishesForOrder(orderId: Long): Map<Long, String>
    suspend fun deleteOrder(orderId: Long, userId: Long)
    suspend fun getDishesByOrderId(orderId: Long): List<DishOrder>
    suspend fun getDishById(dishId: Long): Dish
    suspend fun updateFullOrder(order: Order, dishOrders: List<DishOrder>)
    suspend fun deleteAllDishesByOrderId(orderId: Long)
    suspend fun updateOrderStatus(orderId: Long, newStatus: OrderStatus): Order
    suspend fun getDishesByIds(ids: List<Long>): List<Dish>
}
