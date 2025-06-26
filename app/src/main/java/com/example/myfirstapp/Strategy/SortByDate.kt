package com.example.myfirstapp.Strategy

import com.example.myfirstapp.Interfaces.SortStrategy
import com.example.myfirstapp.data.Models.Booking

class SortByDate : SortStrategy {
    override fun sort(bookings: List<Booking>): List<Booking> {
        return bookings.sortedBy { it.bookingDate }
    }
}