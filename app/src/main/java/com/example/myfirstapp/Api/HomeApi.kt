package com.example.myfirstapp.Api

import retrofit2.http.Query
import com.example.myfirstapp.data.Models.Category
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.FavoriteDish
import com.example.myfirstapp.data.Models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HomeApi {
    @GET("api/dishes")
    suspend fun getAllDishes(@Query("language") language: String): List<Dish>

    @GET("/api/dishes/{dishId}")
    suspend fun getDishById(
        @Path("dishId") dishId: Long,
        @Query("language") language: String
    ): Dish

    @GET("api/categories")
    suspend fun getAllCategories(@Query("language") language: String): List<Category>

    @GET("api/dishes/best")
    suspend fun getAllBestDishes(
        @Query("best") best: Boolean,
        @Query("language") language: String
    ): List<Dish>

    @GET("api/dishes/search")
    suspend fun getDishesByName(
        @Query("name") name: String,
        @Query("language") language: String
    ): List<Dish>

    @GET("api/dishes/category/name/{categoryName}")
    suspend fun getDishesByCategory(
        @Path("categoryName") categoryName: String,
        @Query("language") language: String
    ): List<Dish>

    @PUT("api/users/{idUser}")
    suspend fun updateUser(@Path("idUser") idUser: Long, @Body user: User): Response<User>

    @POST("api/favorite-dishes")
    suspend fun addFavoriteDish(@Body favoriteDish: FavoriteDish): FavoriteDish

    @DELETE("api/favorite-dishes/{userId}/{dishId}")
    suspend fun removeFavoriteDish(
        @Path("userId") userId: Long,
        @Path("dishId") dishId: Long
    )

    @GET("/api/favorite-dishes/{userId}")
    suspend fun getFavoriteDishes(
        @Path("userId") userId: Long,
        @Query("language") language: String
    ): List<FavoriteDish>

    @GET("api/favorite-dishes/exists")
    suspend fun existsFavoriteDish(
        @Query("userId") userId: Long,
        @Query("dishId") dishId: Long
    ): Boolean
}
