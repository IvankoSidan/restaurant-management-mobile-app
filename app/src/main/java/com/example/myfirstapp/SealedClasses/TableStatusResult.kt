package com.example.myfirstapp.SealedClasses

sealed class TableStatusResult {
    object Reserved : TableStatusResult()
    object Available : TableStatusResult()
    object Selected : TableStatusResult()
    data class Error(val message: String) : TableStatusResult()
}