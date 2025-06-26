package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.ReservationTableViewModel
import com.example.myfirstapp.databinding.FragmentBookSuccessBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class BookSuccessFragment : Fragment() {

    private lateinit var binding: FragmentBookSuccessBinding

    private val reserveViewModel: ReservationTableViewModel by viewModel(ownerProducer  = { requireActivity() })
    private val guestViewModel: GuestViewModel by viewModel(ownerProducer  = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()

        binding.btnOrder.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.backImage.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun setupUI() {
        guestViewModel.guest.observe(viewLifecycleOwner) { user ->
            binding.nameClient.text = user!!.name
        }

        reserveViewModel.bookings.observe(viewLifecycleOwner) { bookings ->
            if (!bookings.isNullOrEmpty()) {
                val lastBooking = bookings.last()
                binding.dateReserve.text = lastBooking.bookingDate
                binding.timeReserve.text = lastBooking.bookingTime
                binding.guestsCount.text = getString(R.string.guests_count, reserveViewModel.totalGuestCount.value)
                binding.durationReserve.text = formatDuration(lastBooking.duration)
            } else {
                binding.dateReserve.text = getString(R.string.no_date)
                binding.timeReserve.text = getString(R.string.no_time)
                binding.guestsCount.text = getString(R.string.no_guests)
                binding.durationReserve.text = getString(R.string.no_duration)
            }
        }

        reserveViewModel.selectedTables.observe(viewLifecycleOwner) { tables ->
            val totalCapacity = tables.sumOf { it.capacity }
            if (tables.isNotEmpty()) {
                binding.floor.text = tables.map { it.floor }.distinct().joinToString(", ")
                binding.tableNumber.text = tables.map { it.number }.joinToString(", ")
                binding.guestsCount.text = getString(R.string.guests_count, totalCapacity)
            } else {
                binding.floor.text = getString(R.string.select_floor)
                binding.tableNumber.text = getString(R.string.select_table_number)
                binding.guestsCount.text = getString(R.string.guests_count, totalCapacity)
            }
        }
    }

    private fun formatDuration(durationMinutes: Int): String {
        if (durationMinutes < 15 || durationMinutes > 240) {
            return getString(R.string.invalid_duration)
        }

        val hours = durationMinutes / 60
        val minutes = durationMinutes % 60

        return when {
            hours > 0 && minutes > 0 -> {
                "${resources.getQuantityString(R.plurals.hours, hours, hours)} " +
                        resources.getQuantityString(R.plurals.minutes, minutes, minutes)
            }
            hours > 0 -> resources.getQuantityString(R.plurals.hours, hours, hours)
            else -> resources.getQuantityString(R.plurals.minutes, minutes, minutes)
        }.trim()
    }
}
