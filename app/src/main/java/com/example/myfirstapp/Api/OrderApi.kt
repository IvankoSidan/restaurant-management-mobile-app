package com.example.myfirstapp.Api


import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.DishOrder
import com.example.myfirstapp.data.Models.Order
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.Response

interface OrderApi {

    @POST("api/orders")
    suspend fun createOrder(@Body order: Order): Response<Order>

    @GET("api/orders/user/{userId}")
    suspend fun getOrdersByUserId(@Path("userId") userId: Long): Response<List<Order>>

    @GET("api/orders/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Long): Response<Order>

    @PUT("api/orders")
    suspend fun updateOrder(@Body order: Order): Response<Void>

    @DELETE("api/orders/{orderId}")
    suspend fun deleteOrder(@Path("orderId") orderId: Long): Response<Void>

    @POST("api/orders/{orderId}/dishes")
    suspend fun addDishToOrder(@Path("orderId") orderId: Long, @Body dishOrder: DishOrder): Response<Void>

    @GET("api/orders/{orderId}/dishes")
    suspend fun getDishesByOrderId(@Path("orderId") orderId: Long): Response<List<DishOrder>>

    @DELETE("api/orders/{orderId}/dishes")
    suspend fun deleteDishesByOrderId(@Path("orderId") orderId: Long): Response<Void>

    @GET("api/dishes/{dishId}")
    suspend fun getDishById(@Path("dishId") dishId: Long): Response<Dish>
}
