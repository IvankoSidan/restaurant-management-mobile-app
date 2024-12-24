package com.example.myfirstapp.Presentation.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.myfirstapp.Objects.RetrofitClient
import com.example.myfirstapp.Presentation.Fragments.LoginRegisterFragments.EntryFragment
import com.example.myfirstapp.Presentation.Fragments.LoginRegisterFragments.RegisterFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.GuestViewModelFactory
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.example.myfirstapp.ViewModels.LoginViewModelFactory
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.ViewModels.OrderViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var guestViewModel: GuestViewModel
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authApi = RetrofitClient.authApi
        val homeApi = RetrofitClient.homeApi
        val orderApi = RetrofitClient.orderApi

        val factoryLogin = LoginViewModelFactory(authApi)
        val factoryGuest = GuestViewModelFactory(homeApi)
        val factoryOrder = OrderViewModelFactory(orderApi)

        loginViewModel = ViewModelProvider(this, factoryLogin)[LoginViewModel::class.java]
        guestViewModel = ViewModelProvider(this, factoryGuest)[GuestViewModel::class.java]
        orderViewModel = ViewModelProvider(this, factoryOrder)[OrderViewModel::class.java]

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EntryFragment())
                .commit()
        }
    }
}

