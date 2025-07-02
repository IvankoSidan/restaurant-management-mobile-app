package com.example.myfirstapp.SealedClasses

import com.example.myfirstapp.data.Models.User

sealed class RegistrationResult {
    object Idle : RegistrationResult()
    object Loading : RegistrationResult()
    data class Success(val user: User? = null) : RegistrationResult()
    data class Failure(val message: String?) : RegistrationResult()
}
