package com.example.myfirstapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.Interfaces.OnBookingItemClickListener
import com.example.myfirstapp.Interfaces.SortStrategy
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Models.Booking
import java.util.Locale

class BookingHistoryAdapter(
    private var bookings: List<Booking>,
    private val listener: OnBookingItemClickListener
) : RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder>(), Filterable {

    private var bookingsFull: List<Booking> = ArrayList(bookings)

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookingDate: TextView = itemView.findViewById(R.id.bookingDate)
        val bookingStatus: TextView = itemView.findViewById(R.id.bookingStatus)
        val reserveCode: TextView = itemView.findViewById(R.id.ReservationCode)
        val guestsCount: TextView = itemView.findViewById(R.id.guestsCount)
        val editButton: ImageView = itemView.findViewById(R.id.editBtn)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteBtn)

        fun bind(booking: Booking) {
            bookingDate.text = booking.bookingDate
            bookingStatus.text = booking.status.toString()
            reserveCode.text = booking.reservationCode
            guestsCount.text = booking.guestsCount.toString()

            editButton.setOnClickListener {
                listener.onEditClicked(booking)
            }
            deleteButton.setOnClickListener {
                listener.onDeleteClicked(booking)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.booking_item, parent, false)
        return BookingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int = bookings.size

    fun updateBookings(newBookings: List<Booking>) {
        bookings = newBookings
        bookingsFull = ArrayList(newBookings)
        notifyDataSetChanged()
    }

    fun sortBookings(strategy: SortStrategy) {
        bookingsFull = strategy.sort(bookingsFull)
        bookings = bookingsFull
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList: MutableList<Booking> = ArrayList()

                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(bookingsFull)
                } else {
                    val filterPattern = constraint.toString().trim().lowercase(Locale.getDefault())

                    for (booking in bookingsFull) {
                        val reservationCode = booking.reservationCode.lowercase(Locale.getDefault()) ?: ""
                        val status = booking.status.toString().lowercase(Locale.getDefault())
                        val bookingDate = booking.bookingDate.replace("/", "-").lowercase(Locale.getDefault()) ?: ""
                        val guestsCount = booking.guestsCount.toString()

                        if (reservationCode.contains(filterPattern) ||
                            status.contains(filterPattern) ||
                            bookingDate.contains(filterPattern) ||
                            guestsCount.contains(filterPattern)

                        ) {
                            filteredList.add(booking)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                bookings = results?.values as List<Booking>
                notifyDataSetChanged()
            }
        }
    }
}
