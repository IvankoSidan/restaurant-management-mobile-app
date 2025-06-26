package com.example.myfirstapp.Objects

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesProvider(private val context: Context) {
    val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
}