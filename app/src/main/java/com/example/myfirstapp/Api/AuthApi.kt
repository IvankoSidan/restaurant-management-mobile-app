package com.example.myfirstapp.Api

import com.example.myfirstapp.data.DTO.LoginDTO
import com.example.myfirstapp.data.DTO.TokenDTO
import com.example.myfirstapp.data.DTO.UserDTO
import com.example.myfirstapp.data.Models.User
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthApi {
    @POST("api/auth/register")
    suspend fun registerUser(@Body userDTO: UserDTO): Response<User>

    @POST("api/auth/login")
    suspend fun loginUser(@Body loginDTO: LoginDTO): Response<Map<String, Any>>

    @POST("/api/auth/validateToken")
    suspend fun validateToken(@Body tokenDTO: TokenDTO): Response<Map<String, Boolean>>
}
