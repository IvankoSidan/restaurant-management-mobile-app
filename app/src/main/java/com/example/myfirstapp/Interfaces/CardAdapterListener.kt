package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Enums.CardType

interface CardAdapterListener {
    fun onCardTypeSelected(cardType: CardType)
    fun onAddCard()
}