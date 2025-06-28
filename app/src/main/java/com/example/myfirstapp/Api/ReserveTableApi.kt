package com.example.myfirstapp.Api

import com.example.myfirstapp.data.Enums.TableStatus
import com.example.myfirstapp.data.Models.Booking
import com.example.myfirstapp.data.Models.RestaurantTable
import org.threeten.bp.LocalDate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ReserveTableApi {
    @GET("api/tables")
    suspend fun getAllTables(): List<RestaurantTable>

    @POST("api/bookings")
    suspend fun createBooking(
        @Body booking: Booking
    ): Booking

    @PUT("api/bookings/{id}")
    suspend fun updateBooking(@Path("id") id: Long, @Body booking: Booking): Booking

    @DELETE("api/bookings/{id}")
    suspend fun deleteBooking(@Path("id") id: Long): Response<Unit>

    @GET("api/bookings/user/{userId}")
    suspend fun getBookingsByUserId(@Path("userId") userId: Long): List<Booking>

    @PUT("api/tables/{tableId}/status/{status}")
    suspend fun updateTableStatus(
        @Path("tableId") tableId: Long,
        @Path("status") status: TableStatus
    ): RestaurantTable

    @GET("api/tables/{tableId}/availability")
    suspend fun checkTableAvailability(
        @Path("tableId") tableId: Long,
        @Query("bookingDate") bookingDate: String,
        @Query("bookingTime") bookingTime: String,
        @Query("duration") duration: Int
    ): Boolean

    @GET("api/bookings/table/{tableId}")
    suspend fun getBookingsByTableId(
        @Path("tableId") tableId: Long,
        @Query("from") fromDate: String,
        @Query("to") toDate: String
    ): List<Booking>
}
