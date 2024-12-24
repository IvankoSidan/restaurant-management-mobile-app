package com.example.myfirstapp.Interfaces

interface FavoriteDishListener {
    fun onFavoriteDishAdded(dishTitle: String)
    fun onFavoriteDishAlreadyExists(dishTitle: String)
}
