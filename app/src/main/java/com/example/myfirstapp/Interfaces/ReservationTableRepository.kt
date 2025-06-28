package com.example.myfirstapp.Interfaces

import com.example.myfirstapp.data.Enums.TableStatus
import com.example.myfirstapp.data.Models.Booking
import com.example.myfirstapp.data.Models.RestaurantTable

interface ReservationTableRepository {
    suspend fun getAllTables(): List<RestaurantTable>
    suspend fun createBooking(booking: Booking): List<Booking>
    suspend fun updateBooking(id: Long, booking: Booking): List<Booking>
    suspend fun deleteBooking(id: Long)
    suspend fun getAllBookingsByUserId(userId: Long): List<Booking>
    suspend fun updateTableStatus(tableId: Long, status: TableStatus): List<RestaurantTable>
    suspend fun checkTableAvailability(
        tableId: Long, bookingDate: String, bookingTime: String, duration: Int
    ): Boolean
    suspend fun onTableClicked(tableId: Long, currentTables: List<RestaurantTable>): List<RestaurantTable>
    suspend fun getSelectedTables(currentTables: List<RestaurantTable>): List<RestaurantTable>
    suspend fun setSelectedTableIds(ids: LongArray, currentTables: List<RestaurantTable>): List<RestaurantTable>
    suspend fun getBookingsByTableId(
        tableId: Long,
        fromDate: String,
        toDate: String
    ): List<Booking>
}

