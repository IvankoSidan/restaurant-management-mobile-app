package com.example.myfirstapp.SealedClasses

import com.example.myfirstapp.data.Models.User

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    object Failure : LoginResult()
    data class Error(val message: String) : LoginResult()
}