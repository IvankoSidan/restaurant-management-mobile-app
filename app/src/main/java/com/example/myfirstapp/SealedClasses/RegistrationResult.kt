package com.example.myfirstapp.SealedClasses

sealed class RegistrationResult {
    object Idle : RegistrationResult()
    object Loading : RegistrationResult()
    object Success : RegistrationResult()
    data class Failure(val message: String?) : RegistrationResult()
}
