package com.example.myfirstapp.Objects

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.example.myfirstapp.Interfaces.CurrencyListener
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Enums.Currency
import io.github.muddz.styleabletoast.StyleableToast

object CurrencyManager {
    private var currentCurrency: Currency = Currency.USD
    private val currencyListeners = mutableListOf<CurrencyListener>()
    private const val PREF_CURRENCY = "pref_currency"

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        currentCurrency = Currency.valueOf(prefs.getString(PREF_CURRENCY, Currency.USD.name) ?: Currency.USD.name)
    }

    fun getCurrentCurrency(): Currency = currentCurrency

    fun switchCurrency(context: Context) {
        currentCurrency = if (currentCurrency == Currency.USD) Currency.RUB else Currency.USD
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_CURRENCY, currentCurrency.name).apply()

        // Сообщение для тоста
        val message = when (currentCurrency) {
            Currency.USD -> context.getString(R.string.currency_changed_to_usd)
            Currency.RUB -> context.getString(R.string.currency_changed_to_rub)
        }

        // Используем стиль successToast
        StyleableToast.makeText(context, message, R.style.successToast).show()

        notifyCurrencyChanged()
    }


    fun addCurrencyListener(listener: CurrencyListener) {
        currencyListeners.add(listener)
    }

    fun removeCurrencyListener(listener: CurrencyListener) {
        currencyListeners.remove(listener)
    }

    private fun notifyCurrencyChanged() {
        currencyListeners.forEach { it.onCurrencyChanged(currentCurrency) }
    }

    fun convertPrice(price: Double): String {
        return when (currentCurrency) {
            Currency.USD -> "$${"%.2f".format(price)}"
            Currency.RUB -> "${"%.2f".format(price * 75.0)} ₽" // Примерный курс
        }
    }
}