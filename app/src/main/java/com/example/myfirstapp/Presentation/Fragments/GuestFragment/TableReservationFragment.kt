package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myfirstapp.Adapters.CalendarAdapter
import com.example.myfirstapp.Presentation.Activities.MainActivity
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.ReservationTableViewModel
import com.example.myfirstapp.data.Enums.BookingStatus
import com.example.myfirstapp.data.Models.Booking
import com.example.myfirstapp.databinding.FragmentTableReservationBinding
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TableReservationFragment : Fragment(), AdapterView.OnItemClickListener {
    private lateinit var binding: FragmentTableReservationBinding
    private val calendar = Calendar.getInstance()
    private lateinit var calendarAdapter: CalendarAdapter
    private var selectedTableIds: LongArray? = null

    private val reserveViewModel: ReservationTableViewModel by viewModel(ownerProducer  = { requireActivity() })
    private val guestViewModel: GuestViewModel by viewModel(ownerProducer  = { requireActivity() })

    private var selectedHour = calendar.get(Calendar.HOUR_OF_DAY) % 12
    private var selectedMinute = calendar.get(Calendar.MINUTE)
    private var isAm = calendar.get(Calendar.HOUR_OF_DAY) < 12
    private var guestCount = 1
    private var durationTime = 60

    private val monthNames by lazy {
        resources.getStringArray(R.array.months_array)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedTableIds = arguments?.getLongArray("SELECTED_TABLE_IDS")
        selectedTableIds?.let { reserveViewModel.setSelectedTableIds(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTableReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        calendarAdapter = CalendarAdapter(layoutInflater, calendar)
        binding.calendarGridView.adapter = calendarAdapter
        updateMonthYearText()
        calendarAdapter.setSelectedDay(calendar.time)

        binding.seekBar.apply {
            progress = durationTime
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    durationTime = maxOf(15, progress)
                    binding.timeDur.text = getString(R.string.duration_text, durationTime)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        binding.apply {
            hourUpButton.setOnClickListener { changeHour(1) }
            hourDownButton.setOnClickListener { changeHour(-1) }
            minuteUpButton.setOnClickListener { changeMinute(1) }
            minuteDownButton.setOnClickListener { changeMinute(-1) }
            amPmUpButton.setOnClickListener { toggleAmPm() }
            amPmDownButton.setOnClickListener { toggleAmPm() }
            backImage.setOnClickListener { findNavController().navigate(R.id.bookingTableFragment) }
            plusGuest.setOnClickListener { changeGuestCount(1) }
            minusGuest.setOnClickListener { changeGuestCount(-1) }
            prevMonthButton.setOnClickListener { changeMonth(-1) }
            nextMonthButton.setOnClickListener { changeMonth(1) }
            selectedMonth.setOnClickListener { showMonthPickerDialog() }

            buttonCancel.setOnClickListener {
                StyleableToast.makeText(requireContext(), getString(R.string.reservation_canceled), R.style.successToast).show()
                findNavController().navigate(R.id.homeFragment)
            }

            buttonConfirm.setOnClickListener {
                if (reserveViewModel.selectedBooking.value != null) {
                    updateBooking()
                } else {
                    confirmReservation()
                }
            }
        }
    }

    private fun changeHour(delta: Int) {
        selectedHour = (selectedHour + delta).let { if (it <= 0) 12 else it % 12 }
        updateTimeText()
    }

    private fun changeMinute(delta: Int) {
        selectedMinute = (selectedMinute + delta + 60) % 60
        updateTimeText()
    }

    private fun toggleAmPm() {
        isAm = !isAm
        updateTimeText()
    }

    private fun changeGuestCount(delta: Int) {
        guestCount = maxOf(1, guestCount + delta)
        binding.countGuest.text = guestCount.toString()
    }

    private fun confirmReservation() = lifecycleScope.launch {
        val selectedDay = calendarAdapter.getSelectedDay() ?: run {
            StyleableToast.makeText(requireContext(), getString(R.string.select_booking_date), R.style.errorToast).show()
            return@launch
        }

        val bookingCalendar = Calendar.getInstance().apply {
            time = selectedDay
            val hour24 = if (isAm) selectedHour % 12 else selectedHour % 12 + 12
            set(Calendar.HOUR_OF_DAY, hour24)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (bookingCalendar.timeInMillis <= System.currentTimeMillis()) {
            StyleableToast.makeText(requireContext(), getString(R.string.chosen_time_passed), R.style.errorToast).show()
            return@launch
        }

        val bookingDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(bookingCalendar.time)
        val bookingTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(bookingCalendar.time)

        val selectedTables = reserveViewModel.selectedTables.value ?: emptyList()
        if (selectedTables.isEmpty()) {
            StyleableToast.makeText(requireContext(), getString(R.string.no_tables_selected), R.style.errorToast).show()
            return@launch
        }

        val totalCapacity = selectedTables.sumOf { it.capacity }
        if (guestCount > totalCapacity) {
            StyleableToast.makeText(requireContext(),
                getString(R.string.guest_count_exceeds_capacity, guestCount, totalCapacity),
                R.style.errorToast
            ).show()
            return@launch
        }

        val guest = guestViewModel.guest.value ?: run {
            StyleableToast.makeText(requireContext(), getString(R.string.unknown_user), R.style.errorToast).show()
            return@launch
        }

        val bookingCode = "RSV" + (1000..9999).random()

        reserveViewModel.setTotalGuestCount(guestCount)
        var remainingGuests = guestCount
        for (table in selectedTables) {
            val tableCapacity = table.capacity
            val guestsForThisTable = minOf(remainingGuests, tableCapacity)
            val actualGuestsForThisTable = minOf(guestsForThisTable, tableCapacity)

            if (!reserveViewModel.checkTableAvailability(table.idTable, bookingDate, bookingTime, durationTime)) {
                StyleableToast.makeText(requireContext(), getString(R.string.table_unavailable, table.number), R.style.errorToast).show()
                return@launch
            }

            val bookingResult = reserveViewModel.tryCreateBooking(
                Booking(
                    userId = guest.idUser,
                    tableId = table.idTable,
                    bookingDate = bookingDate,
                    bookingTime = bookingTime,
                    status = BookingStatus.PENDING,
                    guestsCount = actualGuestsForThisTable,
                    duration = durationTime,
                    reservationCode = bookingCode
                )
            )

            if (bookingResult == null) {
                StyleableToast.makeText(requireContext(), getString(R.string.failed_to_create_booking, table.number), R.style.errorToast).show()
                return@launch
            }

            remainingGuests -= actualGuestsForThisTable
            if (remainingGuests <= 0) break
        }

        StyleableToast.makeText(requireContext(), getString(R.string.reservation_confirmed), R.style.successToast).show()
        reserveViewModel.getAllBookingsByUserId(userId = guest.idUser)
        findNavController().navigate(R.id.bookSuccessFragment)
    }

    private fun updateBooking() = lifecycleScope.launch {
        val selectedDay = calendarAdapter.getSelectedDay() ?: run {
            StyleableToast.makeText(requireContext(), getString(R.string.select_booking_date), R.style.errorToast).show()
            return@launch
        }

        val bookingCalendar = Calendar.getInstance().apply {
            time = selectedDay
            val hour24 = if (isAm) selectedHour % 12 else selectedHour % 12 + 12
            set(Calendar.HOUR_OF_DAY, hour24)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (bookingCalendar.timeInMillis <= System.currentTimeMillis()) {
            StyleableToast.makeText(requireContext(), getString(R.string.booking_time_past), R.style.errorToast).show()
            return@launch
        }

        val bookingDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(bookingCalendar.time)
        val bookingTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(bookingCalendar.time)

        val selectedTable = reserveViewModel.selectedTable.value
        if (selectedTable == null) {
            StyleableToast.makeText(requireContext(), getString(R.string.no_table_selected), R.style.errorToast).show()
            return@launch
        }

        if (guestCount > selectedTable.capacity) {
            StyleableToast.makeText(requireContext(),
                getString(R.string.guest_count_exceeds_capacity, guestCount, selectedTable.capacity),
                R.style.errorToast
            ).show()
            return@launch
        }

        val booking = reserveViewModel.selectedBooking.value?.copy(
            bookingDate = bookingDate,
            bookingTime = bookingTime,
            guestsCount = guestCount,
            duration = durationTime
        ) ?: return@launch

        reserveViewModel.updateBooking(booking.idBooking, booking)
        StyleableToast.makeText(requireContext(), getString(R.string.booking_updated), R.style.successToast).show()
        findNavController().navigate(R.id.bookingHistoryFragment)
    }

    private fun updateTimeText() {
        binding.apply {
            hourPicker.text = String.format("%02d", selectedHour)
            minuteTextView.text = String.format("%02d", selectedMinute)
            amPmTextView.text = if (isAm) "AM" else "PM"
        }
    }

    private fun updateMonthYearText() {
        binding.monthYearTextView.text = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(calendar.time)
    }

    private fun showMonthPickerDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_month))
            .setItems(monthNames) { _, which ->
                calendar.set(Calendar.MONTH, which)
                calendarAdapter.updateCalendar(calendar)
                updateMonthYearText()
            }
            .show()
    }

    private fun changeMonth(delta: Int) {
        calendar.add(Calendar.MONTH, delta)
        updateMonthYearText()
        calendarAdapter.setSelectedDay(calendar.time)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        calendarAdapter.setSelectedDay(calendarAdapter.getItem(position))
    }
}
