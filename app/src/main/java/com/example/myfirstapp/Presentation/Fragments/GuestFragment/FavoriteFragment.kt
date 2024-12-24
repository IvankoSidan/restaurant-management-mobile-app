package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfirstapp.Adapters.FavoriteAdapter
import com.example.myfirstapp.Interfaces.FavoriteSelectedListener
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.databinding.FragmentFavoriteBinding
import io.github.muddz.styleabletoast.StyleableToast

class FavoriteFragment : Fragment(), FavoriteSelectedListener {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val guestViewModel: GuestViewModel by lazy {
        ViewModelProvider(requireActivity())[GuestViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAdapter = FavoriteAdapter(this)
        binding.forWardListItems.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = favoriteAdapter
        }

        binding.foodBackDialog.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
        setupSpinners()
        observeFavorite()
    }

    private fun observeFavorite() {
        guestViewModel.favoriteDishes.observe(viewLifecycleOwner) { favoriteDishes ->
            favoriteDishes?.let {
                favoriteAdapter.submitList(it.toMutableList())
                favoriteAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun loadSelectedDish(dish: Dish) {
        guestViewModel.setCurrentDish(dish)
        findNavController().navigate(R.id.detailFragment)
    }

    override fun onRemoved(dish: Dish) {
        guestViewModel.removeFavoriteDish(dish.idDish)
        StyleableToast.makeText(requireContext(), "${dish.title} deleted from favorites !", R.style.successToast).show()
        favoriteAdapter.removeDish(dish)
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
        val rate = binding.spinnerStar.selectedItem.toString()
        val time = binding.spinnerTimeValue.selectedItem.toString()
        val price = binding.spinnerPrice.selectedItem.toString()

        val filteredDishes = when {
            guestViewModel.dishesByCategory.value != null -> {
                guestViewModel.dishesByCategory.value?.filter { dish ->
                    isRateValid(dish.star, rate) && isTimeValid(dish.timeValue, time) && isPriceValid(dish.price, price)
                }
            }
            guestViewModel.dishesByName.value != null -> {
                guestViewModel.dishesByName.value?.filter { dish ->
                    isRateValid(dish.star, rate) && isTimeValid(dish.timeValue, time) && isPriceValid(dish.price, price)
                }
            }
            guestViewModel.favoriteDishes.value != null -> {
                guestViewModel.favoriteDishes.value?.filter { dish ->
                    isRateValid(dish.star, rate) && isTimeValid(dish.timeValue, time) && isPriceValid(dish.price, price)
                }
            }
            else -> {
                guestViewModel.dishes.value?.filter { dish ->
                    isRateValid(dish.star, rate) && isTimeValid(dish.timeValue, time) && isPriceValid(dish.price, price)
                }
            }
        }

        favoriteAdapter.submitList(filteredDishes?.toMutableList())
        favoriteAdapter.notifyDataSetChanged()
    }

    private fun isRateValid(rating: Float, rate: String): Boolean {
        return when (rate) {
            "All" -> true
            "3.0 - 4.0" -> rating in 3.0..4.0
            "4.0 - 4.5" -> rating in 4.0..4.5
            "4.5 - 5.0" -> rating in 4.5..5.0
            else -> true
        }
    }

    private fun isTimeValid(cookingTime: Int, time: String): Boolean {
        return when (time) {
            "All" -> true
            "0 - 10 min" -> cookingTime in 0..10
            "10 - 30 min" -> cookingTime in 10..30
            "more than 30 min" -> cookingTime > 30
            else -> true
        }
    }

    private fun isPriceValid(price: Double, priceRange: String): Boolean {
        return when (priceRange) {
            "All" -> true
            "1$ - 10$" -> price in 1.0..10.0
            "10$ - 30$" -> price in 10.0..30.0
            "more than 30$" -> price > 30.0
            else -> true
        }
    }
}
