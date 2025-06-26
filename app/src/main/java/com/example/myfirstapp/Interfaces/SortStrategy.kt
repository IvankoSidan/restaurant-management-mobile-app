package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.Booking

interface SortStrategy {
    fun sort(bookings: List<Booking>): List<Booking>
}
