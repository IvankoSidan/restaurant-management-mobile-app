package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.Order

interface OrderManagementListener {
    fun onDeleteOrder(order: Order)
    fun onChangeOrder(order: Order)
    fun onRepeatOrder(order: Order)
    fun getFormattedDishesForOrder(orderId: Long, callback: (String) -> Unit)
}
