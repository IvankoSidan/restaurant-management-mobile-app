package com.example.myfirstapp.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Enums.BookingStatus
import com.example.myfirstapp.data.Models.Booking
import java.text.SimpleDateFormat
import java.util.*

class CurrentBookingsAdapter(
    private var bookings: MutableList<Booking>
) : RecyclerView.Adapter<CurrentBookingsAdapter.CurrentBookingsViewHolder>() {

    private val isoDateIn: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateOut: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val timeFmt: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun updateBookings(newBookings: List<Booking>) {
        bookings.clear()
        bookings.addAll(newBookings)
        notifyDataSetChanged()
    }

    inner class CurrentBookingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tableNumber: TextView = itemView.findViewById(R.id.tableNumber)
        private val titleDateBooking: TextView = itemView.findViewById(R.id.titleDateBooking)
        private val titleTimeDuration: TextView = itemView.findViewById(R.id.titleTimeDuration)
        private val titleCountGuest: TextView = itemView.findViewById(R.id.titleCountGuest)
        private val valueReservationCode: TextView = itemView.findViewById(R.id.valueReservationCode)
        private val valueStatus: TextView = itemView.findViewById(R.id.valueStatus)
        private val valueDuration: TextView = itemView.findViewById(R.id.valueDuration)
        private val btnShowInfo: ImageView = itemView.findViewById(R.id.btnShowInfo)
        private val detailsLayout: LinearLayout = itemView.findViewById(R.id.detailsLayout)

        fun bind(booking: Booking) {
            detailsLayout.visibility = View.GONE

            tableNumber.text = "#${booking.idBooking}"

            try {
                val d: Date? = isoDateIn.parse(booking.bookingDate)
                titleDateBooking.text = d?.let { dateOut.format(it) } ?: booking.bookingDate
            } catch (e: Exception) {
                titleDateBooking.text = booking.bookingDate
            }

            try {
                val start: Date? = timeFmt.parse(booking.bookingTime)
                start?.let {
                    val cal: Calendar = Calendar.getInstance().apply { time = it }
                    val startStr: String = timeFmt.format(cal.time)
                    cal.add(Calendar.MINUTE, booking.duration)
                    val endStr: String = timeFmt.format(cal.time)
                    titleTimeDuration.text = "$startStr–$endStr"
                } ?: run {
                    titleTimeDuration.text = booking.bookingTime
                }
            } catch (e: Exception) {
                titleTimeDuration.text = booking.bookingTime
            }

            val count: Int = booking.guestsCount
            titleCountGuest.text = itemView.context.resources
                .getQuantityString(R.plurals.guests, count, count)

            valueReservationCode.text = ""
            valueStatus.text = ""
            valueDuration.text = ""

            btnShowInfo.setOnClickListener { _: View ->
                detailsLayout.visibility = if (detailsLayout.visibility == View.VISIBLE) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                if (detailsLayout.visibility == View.VISIBLE) {
                    valueReservationCode.text = booking.reservationCode

                    val col: String = when (booking.status) {
                        BookingStatus.PENDING -> "#FFC801"
                        BookingStatus.CONFIRMED -> "#00FF00"
                        BookingStatus.CANCELLED -> "#FF0000"
                        else -> "#000000"
                    }

                    valueStatus.setTextColor(Color.parseColor(col))
                    valueStatus.text = booking.status.name

                    val hrs: Int = booking.duration / 60
                    val mins: Int = booking.duration % 60
                    val parts: MutableList<String> = mutableListOf()

                    if (hrs > 0) {
                        parts += itemView.context.resources
                            .getQuantityString(R.plurals.hours, hrs, hrs)
                    }

                    if (mins > 0) {
                        parts += itemView.context.resources
                            .getQuantityString(R.plurals.minutes, mins, mins)
                    }

                    valueDuration.text = parts.joinToString(" ")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentBookingsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_info_item, parent, false)
        return CurrentBookingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrentBookingsViewHolder, position: Int) {
        if (position in 0 until bookings.size) {
            holder.bind(bookings[position])
        }
    }

    override fun getItemCount(): Int = bookings.size
}
