package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.ReservationTableViewModel
import com.example.myfirstapp.data.Enums.TableStatus
import com.example.myfirstapp.data.Models.RestaurantTable
import com.example.myfirstapp.databinding.FragmentFloorBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class FloorFragment : Fragment() {

    private var floorNumber: Int = 0
    private var _binding: FragmentFloorBinding? = null
    private val binding get() = _binding!!

    private val reserveViewModel: ReservationTableViewModel  by viewModel(ownerProducer  = { requireActivity() })

    companion object {
        private const val FLOOR_NUMBER_KEY = "FLOOR_NUMBER"
        fun newInstance(floor: Int): FloorFragment = FloorFragment().apply {
            arguments = Bundle().apply { putInt(FLOOR_NUMBER_KEY, floor) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        floorNumber = arguments?.getInt(FLOOR_NUMBER_KEY) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFloorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayTablesForFloor(floorNumber)
        reserveViewModel.tables.observe(viewLifecycleOwner) { tables ->
            val filteredTables = tables.filter { it.floor == floorNumber }
            displayTables(filteredTables)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayTablesForFloor(floor: Int) {
        reserveViewModel.tables.observe(viewLifecycleOwner) { tables ->
            val filteredTables = tables.filter { it.floor == floor }
            displayTables(filteredTables)
        }
        reserveViewModel.getAllTables()
    }

    private fun displayTables(tables: List<RestaurantTable>) = with(binding) {
        overlayContainer.removeAllViews()
        tables.forEach { table ->
            val tableView = LayoutInflater.from(context)
                .inflate(R.layout.table_item, overlayContainer, false)

            val tableNumberTextView = tableView.findViewById<TextView>(R.id.tableNumberTextView)
            val tableIconImageView = tableView.findViewById<ImageView>(R.id.tableIconImageView)

            tableNumberTextView.text = table.number
            setupTableViewAppearance(table, tableIconImageView, tableNumberTextView)

            val resId = requireContext().resources.getIdentifier(
                table.imagePath, "drawable", requireContext().packageName
            )
            if (resId != 0) {
                tableIconImageView.setImageResource(resId)
            }

            tableView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = convertDesignPxToDevicePx(table.x).toInt()
                topMargin = convertDesignPxToDevicePx(table.y).toInt()
            }

            tableView.setOnClickListener {
                reserveViewModel.onTableClicked(table.idTable)
            }
            overlayContainer.addView(tableView)
        }
    }

    private fun convertDesignPxToDevicePx(designPx: Float): Float {
        val density = resources.displayMetrics.density
        return designPx * density
    }

    private fun setupTableViewAppearance(
        table: RestaurantTable,
        tableImage: ImageView,
        tableNumber: TextView
    ) {
        val colorRes = when (table.status) {
            TableStatus.AVAILABLE -> R.color.teal_700
            TableStatus.SELECTED -> R.color.teal_200
            TableStatus.RESERVED -> R.color.colorAccent
        }
        val color = ContextCompat.getColor(requireContext(), colorRes)
        tableImage.clearColorFilter()
        tableNumber.setTextColor(color)
    }
}
