package com.example.myfirstapp.data.Enums

enum class OrderStatus {
    ACCEPTED,
    PAID,
    IN_PROGRESS;

    fun getDisplayName(): String {
        return "${this.name.capitalize()} ($name)"
    }
}