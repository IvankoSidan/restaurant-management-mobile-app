package com.example.myfirstapp.Interfaces

interface Validator {
    fun validate(input: String): Boolean
    fun getErrorMessage(): String
}
