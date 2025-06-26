package com.example.myfirstapp.data.Models

import com.example.myfirstapp.data.Enums.UserRole

data class User(
    val idUser: Long,
    val role: UserRole,
    val name: String,
    val email: String,
    var password: String
)
