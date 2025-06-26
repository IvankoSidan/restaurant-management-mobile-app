package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Adapters.BestFoodAdapter
import com.example.myfirstapp.Adapters.CategoryAdapter
import com.example.myfirstapp.Interfaces.DishCategoryListener
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.databinding.FragmentHomeBinding
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), DishCategoryListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var bestFoodAdapter: BestFoodAdapter

    private val guestViewModel: GuestViewModel by viewModel(ownerProducer  = { requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()

        guestViewModel.loadCategories()
        guestViewModel.loadAllBestDishes()
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter(this)
        bestFoodAdapter = BestFoodAdapter(this)
    }

    private fun setupRecyclerViews() {
        binding.categoryRec.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = categoryAdapter
        }

        binding.bestFoodRec.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = bestFoodAdapter
        }
    }

    private fun setupClickListeners() {
        binding.viewAllMeal.setOnClickListener {
            guestViewModel.clearDishesByCategory()
            guestViewModel.clearSearchResults()
            guestViewModel.loadAllDishes()
            findNavController().navigate(R.id.foodListFragment)
        }

        binding.avatar.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        binding.editSearchFood.setOnClickListener {
            val query = binding.editSearchFood.text.toString().trim()
            if (query.isNotEmpty()) {
                guestViewModel.loadDishesByName(query)
                findNavController().navigate(R.id.foodListFragment)
                binding.editSearchFood.text.clear()
            } else {
                StyleableToast.makeText(requireContext(), getString(R.string.field_empty), R.style.errorToast).show()
            }
        }
    }

    private fun observeViewModel() {
        guestViewModel.guest.observe(viewLifecycleOwner) { user ->
            binding.textName.text = getString(R.string.hi_user, user!!.name)
        }

        guestViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                categoryAdapter.submitList(it)
            }
        }

        guestViewModel.bestDishes.observe(viewLifecycleOwner) { bestDishes ->
            bestDishes?.let {
                bestFoodAdapter.submitList(it)
            }
        }
    }

    override fun loadDishesByCategory(nameCategory: String) {
        guestViewModel.clearSearchResults()
        guestViewModel.loadDishesByCategory(nameCategory)
        findNavController().navigate(R.id.foodListFragment)
    }

    override fun loadSelectedDish(dish: Dish) {
        guestViewModel.setCurrentDish(dish)
        findNavController().navigate(R.id.detailFragment)
    }
}
