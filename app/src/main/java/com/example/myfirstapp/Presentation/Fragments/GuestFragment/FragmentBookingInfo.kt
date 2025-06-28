package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Adapters.CurrentBookingsAdapter
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.ReservationTableViewModel
import com.example.myfirstapp.databinding.FragmentBookingInfoBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FragmentBookingInfo : BottomSheetDialogFragment() {

    private var _binding: FragmentBookingInfoBinding? = null
    private val binding get() = _binding!!

    private val reserveVM: ReservationTableViewModel by viewModel(ownerProducer = { requireActivity() })
    private lateinit var currentBookingsAdapter: CurrentBookingsAdapter
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupUI() {
        reserveVM.selectedTable.value?.let { table ->
            binding.apply {
                titleTableNumber.text = table.number
                floorNumber.text = table.floor.toString()
                tableName.text = table.name
                tableCapacity.text = table.capacity.toString()
                tableDescription.text = table.description
            }
        }
    }

    private fun setupRecyclerView() {
        currentBookingsAdapter = CurrentBookingsAdapter(mutableListOf())
        binding.bookingInfoView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = currentBookingsAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.btnCloseBookingInfo.setOnClickListener { dismiss() }

        binding.btnToday.setOnClickListener {
            reserveVM.selectedTable.value?.let { loadRange(it.idTable, 0) }
        }

        binding.btnWeek.setOnClickListener {
            reserveVM.selectedTable.value?.let { loadRange(it.idTable, 6) }
        }

        binding.btnCustomRange.setOnClickListener {
            showCustomDateRangePicker()
        }
    }

    private fun observeViewModel() {
        reserveVM.selectedTable.observe(viewLifecycleOwner) { table ->
            table?.let {
                setupUI()
                loadRange(it.idTable, 0)
            }
        }

        reserveVM.bookings.observe(viewLifecycleOwner) { bookings ->
            currentBookingsAdapter.updateBookings(bookings.orEmpty())
        }
    }

    private fun loadRange(tableId: Long, daysAhead: Int) {
        val cal = Calendar.getInstance()
        val from = isoDateFormat.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, daysAhead)
        val to = isoDateFormat.format(cal.time)

        reserveVM.getBookingsByTableId(tableId, from, to)
    }

    private fun showCustomDateRangePicker() {
        // Устанавливаем минимальную дату как текущую
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        // Создаем DateValidator для запрета выбора прошлых дат
        val dateValidator = DateValidatorPointForward.from(today)

        // Создаем пикер диапазона дат
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_date_range))
            .setSelection(androidx.core.util.Pair(today, today))
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setStart(today)
                    .setValidator(dateValidator)
                    .build()
            )
            .build()

        dateRangePicker.addOnPositiveButtonClickListener { dateRange ->
            val startDate = Date(dateRange.first ?: today)
            val endDate = Date(dateRange.second ?: today)

            val startStr = isoDateFormat.format(startDate)
            val endStr = isoDateFormat.format(endDate)

            reserveVM.selectedTable.value?.let {
                reserveVM.getBookingsByTableId(it.idTable, startStr, endStr)
            }
        }

        dateRangePicker.show(parentFragmentManager, "DATE_RANGE_PICKER")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dlg ->
            (dlg as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.apply {
                    background = ColorDrawable(Color.TRANSPARENT)
                    layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
                    this.layoutParams = layoutParams
                }
        }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        reserveVM.clearSelectedTable()
    }
}