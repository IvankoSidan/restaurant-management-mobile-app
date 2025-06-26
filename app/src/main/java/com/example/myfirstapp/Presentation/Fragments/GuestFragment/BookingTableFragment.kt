package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myfirstapp.Adapters.FloorPagerAdapter
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.ReservationTableViewModel
import com.example.myfirstapp.databinding.FragmentBookingTableBinding
import com.google.android.material.tabs.TabLayoutMediator
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookingTableFragment : Fragment() {

    private lateinit var binding: FragmentBookingTableBinding

    private lateinit var floorPagerAdapter: FloorPagerAdapter

    private val viewModel: ReservationTableViewModel by viewModel(ownerProducer  = { requireActivity() })

    private val tabTitles by lazy {
        resources.getStringArray(R.array.floor_titles)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookingTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()

        binding.backImage.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.btnBookATable.setOnClickListener {
            val selectedTables = viewModel.selectedTables.value
            if (selectedTables.isNullOrEmpty()) {
                StyleableToast.makeText(
                    requireContext(),
                    getString(R.string.error_select_table),
                    R.style.errorToast
                ).show()
            } else {
                val selectedTableIds = selectedTables.map { it.idTable }.toLongArray()
                val bundle = Bundle().apply {
                    putLongArray("SELECTED_TABLE_IDS", selectedTableIds)
                }
                findNavController().navigate(R.id.tableReservationFragment, bundle)
            }
        }

        viewModel.tables.observe(viewLifecycleOwner) { tables ->
            binding.btnBookATable.isEnabled = tables.isNotEmpty()
        }
    }

    private fun setupViewPager() {
        floorPagerAdapter = FloorPagerAdapter(requireActivity())
        binding.tablePager.adapter = floorPagerAdapter
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.tablePager) { tab, position ->
            tab.text = tabTitles.getOrNull(position) ?: "${getString(R.string.floor_info)} ${position + 1}"
        }.attach()
    }
}
