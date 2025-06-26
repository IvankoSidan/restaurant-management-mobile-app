package com.example.myfirstapp.data.Models

import com.example.myfirstapp.data.Enums.BookingStatus
import com.fasterxml.jackson.annotation.JsonFormat
import org.threeten.bp.LocalDate



data class Booking(
    val idBooking: Long = 0,
    val userId: Long,
    val tableId: Long,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val bookingDate: String,
    val bookingTime: String,
    var status: BookingStatus,
    val guestsCount: Int,
    val duration: Int,
    val reservationCode: String
)