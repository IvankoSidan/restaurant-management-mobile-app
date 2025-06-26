package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Adapters.BookingHistoryAdapter
import com.example.myfirstapp.Interfaces.OnBookingItemClickListener
import com.example.myfirstapp.R
import com.example.myfirstapp.Strategy.SortByDate
import com.example.myfirstapp.Strategy.SortByGuest
import com.example.myfirstapp.Strategy.SortByStatus
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.ReservationTableViewModel
import com.example.myfirstapp.data.Models.Booking
import com.example.myfirstapp.databinding.FragmentBookingHistoryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookingHistoryFragment : Fragment(), OnBookingItemClickListener {

    private var _binding: FragmentBookingHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: BookingHistoryAdapter

    private val viewModel: ReservationTableViewModel by viewModel(ownerProducer  = { requireActivity() })
    private val guestViewModel: GuestViewModel by viewModel(ownerProducer  = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupFilterButton()
        setupSearchView()
        observeBookings()
        setupAddBookingButton()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun setupUI() {
        historyAdapter = BookingHistoryAdapter(emptyList(), this)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    private fun setupFilterButton() {
        binding.filterBtn.setOnClickListener {
            val sortOptions = resources.getStringArray(R.array.sort_options)
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.select_sort_title))
                .setItems(sortOptions) { _, which ->
                    when (which) {
                        0 -> historyAdapter.sortBookings(SortByDate())
                        1 -> historyAdapter.sortBookings(SortByGuest())
                        2 -> historyAdapter.sortBookings(SortByStatus())
                    }
                }
                .show()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                historyAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                historyAdapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun observeBookings() {
        guestViewModel.guest.observe(viewLifecycleOwner) { guest ->
            guest?.let {
                viewModel.getAllBookingsByUserId(it.idUser)
            }
        }

        viewModel.bookings.observe(viewLifecycleOwner) { bookings ->
            historyAdapter.updateBookings(bookings)
        }
    }

    override fun onEditClicked(booking: Booking) {
        val selectedTable = viewModel.tables.value?.firstOrNull { it.idTable == booking.tableId }
        selectedTable?.let {
            viewModel.setSelectedTable(it)
            viewModel.selectBooking(booking)
            findNavController().navigate(R.id.tableReservationFragment)
        }
    }

    override fun onDeleteClicked(booking: Booking) {
        viewModel.deleteBooking(booking.idBooking)
    }

    private fun setupAddBookingButton() {
        binding.floatingActionButton.setOnClickListener {
            viewModel.selectBooking(null)
            findNavController().navigate(R.id.bookingTableFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAllTables()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
