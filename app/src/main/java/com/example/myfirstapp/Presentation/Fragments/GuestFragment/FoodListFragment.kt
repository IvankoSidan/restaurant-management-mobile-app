package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfirstapp.Adapters.FoodListAdapter
import com.example.myfirstapp.Interfaces.DishSelectedListener
import com.example.myfirstapp.R
import com.example.myfirstapp.Strategy.DishFilterManager
import com.example.myfirstapp.Strategy.PriceFilterStrategy
import com.example.myfirstapp.Strategy.RateFilterStrategy
import com.example.myfirstapp.Strategy.TimeFilterStrategy
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.databinding.FoodListBinding
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class FoodListFragment : Fragment(), DishSelectedListener {

    private lateinit var binding: FoodListBinding
    private lateinit var foodListAdapter: FoodListAdapter

    private val guestViewModel: GuestViewModel  by viewModel(ownerProducer  = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FoodListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foodListAdapter = FoodListAdapter(this)
        binding.foodListItems.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = foodListAdapter
        }

        binding.foodBackDialog.setOnClickListener {
            findNavController().popBackStack()
        }
        observeFoodList()
        setupSpinners()
    }

    private fun observeFoodList() {
        guestViewModel.dishes.observe(viewLifecycleOwner) { result ->
            if (guestViewModel.dishesByCategory.value.isNullOrEmpty()) {
                foodListAdapter.submitList(result)
            }
        }

        guestViewModel.dishesByCategory.observe(viewLifecycleOwner) { result ->
            if (result.isNullOrEmpty()) {
                guestViewModel.dishes.value?.let { dishes ->
                    foodListAdapter.submitList(dishes)
                }
            } else {
                foodListAdapter.submitList(result)
            }
        }

        guestViewModel.dishesByName.observe(viewLifecycleOwner) { result ->
            result?.let {
                foodListAdapter.submitList(it)
            }
        }

        guestViewModel.foodCategory.observe(viewLifecycleOwner) { category ->
            updateTitle(category)
        }
    }

    private fun updateTitle(category: String?) {
        binding.titleFoodCategory.text = category?.takeIf { it.isNotEmpty() } ?: getString(R.string.list_all)
    }

    override fun loadSelectedDish(dish: Dish) {
        guestViewModel.setCurrentDish(dish)
        findNavController().navigate(R.id.detailFragment)
    }

    private fun setupSpinners() {
        val rateItems = resources.getStringArray(R.array.spinner_rate_items)
        val timeItems = resources.getStringArray(R.array.spinner_time_items)
        val priceItems = resources.getStringArray(R.array.spinner_price_items)

        val rateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rateItems)
        rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStar.adapter = rateAdapter

        val timeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timeItems)
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTimeValue.adapter = timeAdapter

        val priceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priceItems)
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPrice.adapter = priceAdapter

        binding.spinnerStar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerTimeValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerPrice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun applyFilters() {
        val rateIndex = binding.spinnerStar.selectedItemPosition
        val timeIndex = binding.spinnerTimeValue.selectedItemPosition
        val priceIndex = binding.spinnerPrice.selectedItemPosition

        val strategies = listOf(
            RateFilterStrategy(rateIndex),
            TimeFilterStrategy(timeIndex),
            PriceFilterStrategy(priceIndex)
        )

        val dishes = when {
            !guestViewModel.dishesByName.value.isNullOrEmpty() -> guestViewModel.dishesByName.value
            !guestViewModel.dishesByCategory.value.isNullOrEmpty() -> guestViewModel.dishesByCategory.value
            else -> guestViewModel.dishes.value
        }

        val filteredDishes = dishes?.let { DishFilterManager(strategies).applyFilters(it) }
        foodListAdapter.submitList(filteredDishes?.toMutableList())
        foodListAdapter.notifyDataSetChanged()
    }
}

