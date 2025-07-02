package com.example.myfirstapp.data.DTO

import com.example.myfirstapp.data.Enums.CardType

data class CardRequestDto(
    val userId: Long,
    val cardNumber: String,
    val cvv: String,
    val expirationDate: String,
    val cardHolderName: String,
    val isDefault: Boolean,
    val cardType: CardType
)
