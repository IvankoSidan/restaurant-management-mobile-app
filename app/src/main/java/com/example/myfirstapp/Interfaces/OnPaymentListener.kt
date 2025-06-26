package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.PaymentMethod

interface OnPaymentListener {
    fun onPaymentMethodClick(paymentMethod : PaymentMethod)
    fun onPaymentMethodDelete(paymentMethod: PaymentMethod)
}
