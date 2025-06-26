package com.example.myfirstapp.data.Models

import com.example.myfirstapp.R
import com.example.myfirstapp.data.Enums.CardType

data class PaymentMethod(
    val id: Long = 0,
    val userId: Long,
    val cardNumber: String,
    val cardHolderName: String,
    val expirationDate: String,
    val cardType: CardType,
    var isSelected: Boolean = false
) {
    val typeCard: String
        get() = when (cardType) {
            CardType.VISA -> "Visa"
            CardType.MASTERCARD -> "Mastercard"
            CardType.AMEX -> "American Express"
            CardType.PSB -> "PSB"
            CardType.SBER -> "Sberbank"
            CardType.TINKOFF -> "Tinkoff"
            CardType.DISCOVER -> "Discover"
            CardType.UNIONPAY -> "UnionPay"
            CardType.JCB -> "JCB"
            CardType.OTHER -> "Other"
        }


    val imageCard: Int
        get() = when (cardType) {
            CardType.VISA -> R.drawable.visa
            CardType.MASTERCARD -> R.drawable.mastercard
            CardType.AMEX -> R.drawable.amex
            CardType.PSB -> R.drawable.psb
            CardType.SBER -> R.drawable.sberbank
            CardType.TINKOFF -> R.drawable.tincoff
            CardType.DISCOVER -> R.drawable.discover
            CardType.UNIONPAY -> R.drawable.unionpay
            CardType.JCB -> R.drawable.jcb
            CardType.OTHER -> R.drawable.add_card_icon
        }
}
