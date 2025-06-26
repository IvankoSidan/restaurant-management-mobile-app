package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfirstapp.Adapters.FavoriteAdapter
import com.example.myfirstapp.Interfaces.FavoriteSelectedListener
import com.example.myfirstapp.R
import com.example.myfirstapp.Strategy.DishFilterManager
import com.example.myfirstapp.Strategy.PriceFilterStrategy
import com.example.myfirstapp.Strategy.RateFilterStrategy
import com.example.myfirstapp.Strategy.TimeFilterStrategy
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.databinding.FragmentFavoriteBinding
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment(), FavoriteSelectedListener {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val guestViewModel: GuestViewModel by viewModel(ownerProducer  = { requireActivity() })

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
            findNavController().popBackStack()
        }
        setupSpinners()
        observeFavorite()
    }

    private fun observeFavorite() {
        guestViewModel.favoriteDishes.observe(viewLifecycleOwner) { favoriteDishes ->
            favoriteDishes?.let {
                favoriteAdapter.submitList(it)
            }
        }
    }


    override fun loadSelectedDish(dish: Dish) {
        guestViewModel.setCurrentDish(dish)
        findNavController().navigate(R.id.detailFragment)
    }

    override fun onRemoved(dish: Dish) {
        guestViewModel.guest.value?.idUser?.let { userId ->
            guestViewModel.removeFavoriteDish(userId, dish.idDish)
            StyleableToast.makeText(requireContext(), "${dish.title} ${getString(R.string.delete_favorite)}", R.style.successToast).show()
        }
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

        val filterManager = DishFilterManager(strategies)

        val dishes = guestViewModel.favoriteDishes.value ?: emptyList()

        val filteredDishes = filterManager.applyFilters(dishes)
        favoriteAdapter.submitList(filteredDishes)
    }

    override fun onResume() {
        super.onResume()
        guestViewModel.loadFavoriteDishes()
    }
}
