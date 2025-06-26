package com.example.myfirstapp.Objects

import com.example.myfirstapp.SealedClasses.ValidationResult

object Validator {
    private const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private const val MIN_PASSWORD_LENGTH = 8
    private const val MIN_NAME_LENGTH = 2

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("Email cannot be empty")
            !email.matches(EMAIL_PATTERN.toRegex()) -> ValidationResult.Error("Invalid email format")
            else -> ValidationResult.Success
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("Password cannot be empty")
            password.length < MIN_PASSWORD_LENGTH ->
                ValidationResult.Error("Password must be at least $MIN_PASSWORD_LENGTH characters")
            else -> ValidationResult.Success
        }
    }

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("Name cannot be empty")
            name.length < MIN_NAME_LENGTH ->
                ValidationResult.Error("Name must be at least $MIN_NAME_LENGTH characters")
            else -> ValidationResult.Success
        }
    }
}