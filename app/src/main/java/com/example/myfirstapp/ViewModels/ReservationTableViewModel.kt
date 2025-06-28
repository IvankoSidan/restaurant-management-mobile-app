package com.example.myfirstapp.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.Api.ReserveTableApi
import com.example.myfirstapp.Interfaces.ReservationTableRepository
import com.example.myfirstapp.SealedClasses.TableStatusResult
import com.example.myfirstapp.data.Enums.TableStatus
import com.example.myfirstapp.data.Models.Booking
import com.example.myfirstapp.data.Models.RestaurantTable
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class ReservationTableViewModel(private val repository: ReservationTableRepository) : ViewModel() {

    private val _tables = MutableLiveData<List<RestaurantTable>>(emptyList())
    val tables: LiveData<List<RestaurantTable>> = _tables

    private val _bookings = MutableLiveData<List<Booking>>(emptyList())
    val bookings: LiveData<List<Booking>> = _bookings

    private val _selectedTables = MutableLiveData<List<RestaurantTable>>(emptyList())
    val selectedTables: LiveData<List<RestaurantTable>> = _selectedTables

    private val _selectedBooking = MutableLiveData<Booking?>()
    val selectedBooking: LiveData<Booking?> = _selectedBooking

    private val _selectedTable = MutableLiveData<RestaurantTable?>()
    val selectedTable: LiveData<RestaurantTable?> = _selectedTable

    fun getAllTables() = viewModelScope.launch {
        _tables.value = repository.getAllTables()
    }


    private val _totalGuestCount = MutableLiveData<Int>()
    val totalGuestCount: LiveData<Int> get() = _totalGuestCount

    fun setTotalGuestCount(count: Int) {
        _totalGuestCount.value = count
    }

    suspend fun tryCreateBooking(booking: Booking): Booking? {
        return try {
            repository.createBooking(booking).lastOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateBooking(id: Long, booking: Booking) = viewModelScope.launch {
        _bookings.value = repository.updateBooking(id, booking)
    }

    fun deleteBooking(id: Long) = viewModelScope.launch {
        try {
            repository.deleteBooking(id)
            _bookings.value = _bookings.value?.filter { it.idBooking != id } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllBookingsByUserId(userId: Long) = viewModelScope.launch {
        _bookings.value = repository.getAllBookingsByUserId(userId)
    }

    suspend fun checkTableAvailability(
        tableId: Long,
        bookingDate: String,
        bookingTime: String,
        duration: Int
    ): Boolean {
        return repository.checkTableAvailability(tableId, bookingDate, bookingTime, duration)
    }

    fun setSelectedTable(table: RestaurantTable) {
        _selectedTable.value = table
    }

    fun onTableClicked(tableId: Long) = viewModelScope.launch {
        _tables.value = repository.onTableClicked(tableId, _tables.value ?: emptyList())
        _selectedTables.value = repository.getSelectedTables(_tables.value ?: emptyList())
    }

    fun selectBooking(booking: Booking?) {
        _selectedBooking.value = booking
    }

    fun setSelectedTableIds(ids: LongArray) = viewModelScope.launch {
        val currentTables = _tables.value ?: return@launch
        _tables.value = repository.setSelectedTableIds(ids, currentTables)
        updateSelectedTables()
    }

    private fun updateSelectedTables() {
        val updatedList = _tables.value?.filter { it.status == TableStatus.SELECTED } ?: emptyList()
        _selectedTables.value = updatedList
    }

    fun clearSelectedTable() {
        _selectedTable.value = null
    }

    fun getBookingsByTableId(tableId: Long, fromDate: String, toDate: String) = viewModelScope.launch {
        try {
            _bookings.value = repository.getBookingsByTableId(tableId, fromDate, toDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

