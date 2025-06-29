package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Enums.Currency

interface CurrencyListener {
    fun onCurrencyChanged(currency: Currency)
}