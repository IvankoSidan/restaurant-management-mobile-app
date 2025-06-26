package com.example.myfirstapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.myfirstapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarAdapter(
    private val inflater: LayoutInflater,
    private var calendar: Calendar
) : BaseAdapter() {
    private val daysList = mutableListOf<Date>()
    private var selectedDay: Date? = null
    private val weekDays: Array<String> = arrayOf(
        inflater.context.getString(R.string.sun),
        inflater.context.getString(R.string.mon),
        inflater.context.getString(R.string.tue),
        inflater.context.getString(R.string.wed),
        inflater.context.getString(R.string.thu),
        inflater.context.getString(R.string.fri),
        inflater.context.getString(R.string.sat)
    )

    init {
        updateCalendar(calendar)
    }

    fun updateCalendar(newCalendar: Calendar) {
        calendar = newCalendar
        daysList.clear()

        val monthCalendar = calendar.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        while (daysList.size < 42) {
            daysList.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        notifyDataSetChanged()
    }

    fun setSelectedDay(day: Date) {
        selectedDay = day
        notifyDataSetChanged()
    }

    fun getSelectedDay(): Date? = selectedDay

    override fun getCount(): Int = daysList.size + 7
    override fun getItem(position: Int): Date {
        if (position < 7) {
            val date = Calendar.getInstance()
            date.set(Calendar.DAY_OF_WEEK, position + 1)
            return date.time
        }
        return daysList[position - 7]
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.calendar_day, parent, false)
        val dayText = view.findViewById<TextView>(R.id.calendar_day_text)

        if (position < 7) {
            dayText.text = weekDays[position]
        } else {
            val date = getItem(position)
            val dayFormat = SimpleDateFormat("d", Locale.getDefault())
            dayText.text = dayFormat.format(date)

            if (selectedDay != null && isSameDay(selectedDay!!, date)) {
                view.setBackgroundResource(R.drawable.calendar_day_background)
            } else {
                view.setBackgroundResource(android.R.color.transparent)
            }
            view.setOnClickListener {
                setSelectedDay(date)
            }
        }

        return view
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}
