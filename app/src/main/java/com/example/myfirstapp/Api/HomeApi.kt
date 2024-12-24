package com.example.myfirstapp.Api

import retrofit2.http.Query
import com.example.myfirstapp.data.Models.Category
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.FavoriteDish
import com.example.myfirstapp.data.Models.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HomeApi {
    @GET("api/dishes")
    suspend fun getAllDishes(): List<Dish>

    @GET("api/dishes/{id}")
    suspend fun getDishById(@Path("id") id: Long): Dish

    @POST("api/dishes")
    suspend fun createDish(@Body dish: Dish): Dish

    @PUT("api/dishes/{id}")
    suspend fun updateDish(@Path("id") id: Long, @Body dish: Dish): Dish

    @DELETE("api/dishes/{id}")
    suspend fun deleteDish(@Path("id") id: Long)

    @GET("api/categories")
    suspend fun getAllCategories(): List<Category>

    @GET("api/categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Long): Category

    @POST("api/categories")
    suspend fun createCategory(@Body category: Category): Category

    @PUT("api/categories/{id}")
    suspend fun updateCategory(@Path("id") id: Long, @Body category: Category): Category

    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long): Unit

    @GET("api/dishes/best")
    suspend fun getAllBestDishes(@Query("best") best: Boolean): List<Dish>

    @GET("api/dishes/search")
    suspend fun getDishesByName(@Query("name") name: String): List<Dish>

    @GET("api/dishes/category/name/{categoryName}")
    suspend fun getDishesByCategory(@Path("categoryName") categoryName: String): List<Dish>

    @GET("api/users")
    suspend fun getUser(@Query("email") email: String): User

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: User): User

    @POST("api/favorite-dishes")
    suspend fun addFavoriteDish(@Body favoriteDish: FavoriteDish): FavoriteDish

    @DELETE("api/favorite-dishes/{userId}/{dishId}")
    suspend fun removeFavoriteDish(@Path("userId") userId: Long, @Path("dishId") dishId: Long)

    @GET("api/favorite-dishes/{userId}")
    suspend fun getFavoriteDishes(@Path("userId") userId: Long): List<FavoriteDish>

    @POST("dishes/insert")
    suspend fun insertDish(@Body dish: Dish): Dish

    @GET("api/favorite-dishes/exists")
    suspend fun existsFavoriteDish(@Query("userId") userId: Long, @Query("dishId") dishId: Long): Boolean
}
