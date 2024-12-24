package com.example.myfirstapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.Api.OrderApi

class OrderViewModelFactory(private val orderApi: OrderApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrderViewModel(orderApi) as T
    }
}