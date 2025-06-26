package com.example.myfirstapp.Repositories

import com.example.myfirstapp.Api.OrderApi
import com.example.myfirstapp.Interfaces.OrderRepository
import com.example.myfirstapp.data.Enums.OrderStatus
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.DishOrder
import com.example.myfirstapp.data.Models.Order
import java.util.Locale

class OrderRepositoryImpl(private val orderApi: OrderApi) : OrderRepository {

    override suspend fun placeOrder(order: Order, dishes: List<DishOrder>): Order {
        val created = orderApi.createOrder(order)
            .takeIf { it.isSuccessful }
            ?.body() ?: throw Exception("Failed to place order")
        orderApi.addDishesBatch(created.orderId, dishes)
            .takeIf { it.isSuccessful } ?: throw Exception("Failed to add dishes batch")
        return created
    }

    override suspend fun loadOrders(userId: Long): List<Order> =
        orderApi.getOrdersByUserId(userId).body().orEmpty()

    override suspend fun getFormattedDishesForOrder(orderId: Long): Map<Long, String> {
        val lang = if (Locale.getDefault().language == "ru") "ru" else "en"
        val unit = if (lang == "ru") "шт." else "pcs"
        val details = orderApi
            .getDishDetailsByOrderId(orderId, lang)
            .body().orEmpty()
        if (details.isEmpty()) return emptyMap()
        val text = details.joinToString("\n") {
            "- ${it.name} (${it.quantity} $unit)"
        }
        return mapOf(orderId to text)
    }

    override suspend fun deleteOrder(orderId: Long, userId: Long) {
        orderApi.deleteOrder(orderId)
    }

    override suspend fun getDishesByOrderId(orderId: Long): List<DishOrder> =
        orderApi.getDishesByOrderId(orderId).body().orEmpty()

    override suspend fun getDishById(dishId: Long): Dish =
        orderApi.getDishesByIds(listOf(dishId))
            .body().orEmpty()
            .firstOrNull() ?: throw Exception("Dish not found")

    override suspend fun getDishesByIds(ids: List<Long>): List<Dish> =
        orderApi.getDishesByIds(ids).body().orEmpty()

    override suspend fun updateFullOrder(order: Order, dishOrders: List<DishOrder>) {
        orderApi.updateOrder(order).takeIf { it.isSuccessful }
            ?: throw Exception("Failed to update order")
        orderApi.deleteDishesByOrderId(order.orderId)
        orderApi.addDishesBatch(order.orderId, dishOrders)
            .takeIf { it.isSuccessful } ?: throw Exception("Failed to add dishes batch")
    }

    override suspend fun deleteAllDishesByOrderId(orderId: Long) {
        orderApi.deleteDishesByOrderId(orderId)
    }

    override suspend fun updateOrderStatus(orderId: Long, newStatus: OrderStatus): Order {
        val order = orderApi.getOrderById(orderId)
            .body() ?: throw Exception("Order not found")
        order.status = newStatus
        orderApi.updateOrder(order).takeIf { it.isSuccessful }
            ?: throw Exception("Failed to update status")
        return order
    }
}
