package com.example.myfirstapp.Repositories

import com.example.myfirstapp.Api.ReserveTableApi
import com.example.myfirstapp.Interfaces.ReservationTableRepository
import com.example.myfirstapp.data.Enums.TableStatus
import com.example.myfirstapp.data.Models.Booking
import com.example.myfirstapp.data.Models.RestaurantTable

class ReservationTableRepositoryImpl(private val reserveTableApi: ReserveTableApi) : ReservationTableRepository {

    override suspend fun getAllTables(): List<RestaurantTable> {
        return try {
            reserveTableApi.getAllTables().map { table ->
                table.copy(status = TableStatus.AVAILABLE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun createBooking(booking: Booking): List<Booking> {
        return try {
            val createdBooking = reserveTableApi.createBooking(booking)
            val currentBookings = getAllBookingsByUserId(booking.userId).toMutableList()
            currentBookings.add(createdBooking)
            currentBookings
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun updateBooking(id: Long, booking: Booking): List<Booking> {
        return try {
            val updatedBooking = reserveTableApi.updateBooking(id, booking)
            val bookingsList = getAllBookingsByUserId(booking.userId).toMutableList()
            val index = bookingsList.indexOfFirst { it.idBooking == updatedBooking.idBooking }
            if (index != -1) bookingsList[index] = updatedBooking
            bookingsList
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun deleteBooking(id: Long) {
        try {
            reserveTableApi.deleteBooking(id)
        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun getAllBookingsByUserId(userId: Long): List<Booking> {
        return try {
            reserveTableApi.getBookingsByUserId(userId)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun updateTableStatus(tableId: Long, status: TableStatus): List<RestaurantTable> {
        return try {
            val updatedTable = reserveTableApi.updateTableStatus(tableId, status)
            val tablesList = getAllTables().toMutableList()
            val index = tablesList.indexOfFirst { it.idTable == updatedTable.idTable }
            if (index != -1) tablesList[index] = updatedTable
            tablesList
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun checkTableAvailability(tableId: Long, bookingDate: String, bookingTime: String, duration: Int): Boolean {
        return try {
            reserveTableApi.checkTableAvailability(tableId, bookingDate, bookingTime, duration)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun onTableClicked(tableId: Long, currentTables: List<RestaurantTable>): List<RestaurantTable> {
        return currentTables.map { table ->
            if (table.idTable == tableId) {
                when (table.status) {
                    TableStatus.SELECTED -> table.copy(status = TableStatus.AVAILABLE)
                    else -> table.copy(status = TableStatus.SELECTED)
                }
            } else table
        }
    }

    override suspend fun getSelectedTables(currentTables: List<RestaurantTable>): List<RestaurantTable> {
        return currentTables.filter { it.status == TableStatus.SELECTED }
    }

    override suspend fun setSelectedTableIds(
        ids: LongArray,
        currentTables: List<RestaurantTable>
    ): List<RestaurantTable> {
        return currentTables.map { table ->
            if (ids.contains(table.idTable)) {
                table.copy(status = TableStatus.SELECTED)
            } else {
                table
            }
        }
    }
}
