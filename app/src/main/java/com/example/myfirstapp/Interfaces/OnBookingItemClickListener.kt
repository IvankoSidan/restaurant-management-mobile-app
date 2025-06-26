package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Models.Booking

interface OnBookingItemClickListener {
    fun onEditClicked(booking: Booking)
    fun onDeleteClicked(booking: Booking)
}