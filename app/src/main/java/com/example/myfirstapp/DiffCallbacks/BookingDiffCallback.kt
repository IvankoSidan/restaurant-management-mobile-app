package com.example.myfirstapp.DiffCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.myfirstapp.data.Models.Booking

class BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
    override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
        return oldItem.idBooking == newItem.idBooking
    }

    override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
     return oldItem == newItem
    }

}