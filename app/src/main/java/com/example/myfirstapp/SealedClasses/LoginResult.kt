package com.example.myfirstapp.SealedClasses

import com.example.myfirstapp.data.Models.User

sealed class LoginResult {
    object Idle    : LoginResult()
    object Loading : LoginResult()
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String?) : LoginResult()
}