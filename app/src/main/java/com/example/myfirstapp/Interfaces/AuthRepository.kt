package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.SealedClasses.LoginResult
import com.example.myfirstapp.SealedClasses.RegistrationResult
import com.example.myfirstapp.data.Models.User

interface AuthRepository {
    suspend fun login(email: String, password: String): LoginResult
    suspend fun register(email: String, name: String, password: String, rememberMe: Boolean): RegistrationResult
    suspend fun loginWithGoogle(idToken: String): LoginResult
    suspend fun registerWithGoogle(idToken: String): RegistrationResult
    suspend fun isTokenValid(token: String): Boolean
    fun saveUserToPreferences(user: User, token: String)
    fun getUserFromPreferences(): User?
    fun updateUserInPreferences(user: User)
    fun getTokenFromPreferences(): String?
    fun clearToken()
}
