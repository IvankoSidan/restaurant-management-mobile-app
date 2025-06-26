package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.SealedClasses.LoginResult
import com.example.myfirstapp.SealedClasses.RegistrationResult
import com.example.myfirstapp.data.Models.User

interface AuthenticationRepository {
    suspend fun login(email: String, password: String): LoginResult
    suspend fun register(email: String, name: String, password: String, rememberMe: Boolean): RegistrationResult
    suspend fun isTokenValid(token: String): Boolean
    fun getUserFromPreferences(): User?
    fun getTokenFromPreferences(): String?
    fun clearUserData()
}