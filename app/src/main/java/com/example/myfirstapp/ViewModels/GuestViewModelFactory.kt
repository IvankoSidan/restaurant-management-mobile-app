package com.example.myfirstapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.Api.HomeApi

class GuestViewModelFactory(private val homeApi: HomeApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GuestViewModel(homeApi) as T
    }
}