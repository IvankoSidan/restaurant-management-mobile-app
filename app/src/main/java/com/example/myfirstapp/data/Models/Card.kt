package com.example.myfirstapp.data.Models

import com.example.myfirstapp.data.Enums.CardType

data class Card(
    val id: Long,
    val userId: Long,
    val cardNumber: String,
    val cardHolderName: String,
    val expirationDate: String,
    val cvv: String,
    val isDefault: Boolean = false,
    val cardType: CardType
)
