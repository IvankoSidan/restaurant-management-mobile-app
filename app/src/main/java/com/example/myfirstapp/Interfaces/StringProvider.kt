package com.example.myfirstapp.Interfaces

interface StringProvider {
    fun getStringResource(resId: Int): String
    fun getString(resId: Int): String
}
