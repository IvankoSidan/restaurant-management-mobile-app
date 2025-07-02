package com.example.myfirstapp.data.Models

import com.example.myfirstapp.R
import com.example.myfirstapp.data.Enums.CardType

data class PaymentMethod(
    val id: Long = 0,
    val userId: Long = 0,
    val cardToken: String = "",
    val cardLastFour: String = "",
    val cardHolderName: String = "",
    val expirationDate: String = "",
    val isDefault: Boolean = false,
    val cardType: CardType = CardType.OTHER,
    val isSelected: Boolean = false,
    val imageCard: Int = 0,
    val cardNumber: String = "",
    val typeCard: String = ""
)

