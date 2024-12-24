package com.example.myfirstapp.Api

import com.example.myfirstapp.data.Models.User
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface AuthApi {
    @POST("api/auth/register")
    suspend fun registerUser (@Body user: User): Response<User>

    @POST("api/auth/login")
    suspend fun authenticateUser (@Body credentials: Map<String, String>): Response<Map<String, Any>>
}
