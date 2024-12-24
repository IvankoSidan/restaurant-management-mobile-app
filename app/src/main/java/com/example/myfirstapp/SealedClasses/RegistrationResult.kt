package com.example.myfirstapp.SealedClasses

sealed class RegistrationResult {
    object Success : RegistrationResult()
    data class Failure(val message: String) : RegistrationResult()
}
