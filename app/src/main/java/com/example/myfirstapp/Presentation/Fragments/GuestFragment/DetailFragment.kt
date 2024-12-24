package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myfirstapp.Interfaces.FavoriteDishListener
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.databinding.FragmentDetailBinding
import io.github.muddz.styleabletoast.StyleableToast


class DetailFragment : Fragment(), FavoriteDishListener {
    private lateinit var binding: FragmentDetailBinding
    private var quantity = 1
    private var isFavorite = false
    private val guestViewModel: GuestViewModel by lazy {
        ViewModelProvider(requireActivity())[GuestViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        guestViewModel.setFavoriteDishListener(this)

        guestViewModel.loadFavoriteDishes()
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.backImage.setOnClickListener { findNavController().navigate(R.id.homeFragment) }
        binding.addBtnToCart.setOnClickListener { addToCart() }
        binding.plusBtn.setOnClickListener { updateQuantity(1) }
        binding.minusBtn.setOnClickListener { updateQuantity(-1) }
        binding.favoriteImage.setOnClickListener { toggleFavorite() }
    }

    private fun toggleFavorite() {
        guestViewModel.selectedDish.value?.let { dish ->
            if (guestViewModel.favoriteDishes.value?.any { it.idDish == dish.idDish } == true) {
                guestViewModel.removeFavoriteDish(dish.idDish)
                binding.favoriteImage.setImageResource(R.drawable.favorite_border)
                StyleableToast.makeText(requireContext(), "${dish.title} removed from favorites!", R.style.successToast).show()
            } else {
                guestViewModel.addFavoriteDish(dish.idDish)
                binding.favoriteImage.setImageResource(R.drawable._favorite)
            }
        }
    }

    private fun setupUI(dish: Dish) {
        with(binding) {
            titleTxt.text = dish.title
            priceMeal.text = String.format("%.2f $", dish.price)
            textDescription.text = dish.description
            timeTxt.text = "${dish.timeValue} min"
            rateTxt.text = "${dish.star} Rating"
            ratingBar.rating = dish.star
            textTotalPrice.text = String.format("%.2f $", quantity * dish.price)

            Glide.with(requireContext())
                .load(dish.imagePath)
                .error(R.drawable.pepperoni)
                .into(imageMeal)

            isFavorite = guestViewModel.favoriteDishes.value?.any { it.idDish == dish.idDish } == true
            binding.favoriteImage.setImageResource(if (isFavorite) R.drawable._favorite else R.drawable.favorite_border)
        }
    }

    private fun addToCart() {
        guestViewModel.selectedDish.value?.let { dish ->
            guestViewModel.addToCart(dish, quantity)
            findNavController().navigate(R.id.cartFragment)
        }
    }

    private fun updateQuantity(change: Int) {
        quantity = (quantity + change).coerceAtLeast(1)
        binding.count.text = "$quantity"
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        guestViewModel.selectedDishes.value?.lastOrNull()?.let { lastDish ->
            binding.textTotalPrice.text = String.format("%.2f $", quantity * lastDish.price)
        } ?: run {
            guestViewModel.selectedDish.value?.let { currentDish ->
                binding.textTotalPrice.text = String.format("%.2f $", quantity * currentDish.price)
            }
        }
    }

    private fun setupObservers() {
        guestViewModel.selectedDish.observe(viewLifecycleOwner) { dish ->
            dish?.let {
                setupUI(it)
                Log.d("ImagePath", "${it.imagePath}")
            }
        }
        guestViewModel.favoriteDishes.observe(viewLifecycleOwner) { favorites ->
            val currentDish = guestViewModel.selectedDish.value
            isFavorite = currentDish?.let { favorites.any { favorite -> favorite.idDish == it.idDish } } == true
            binding.favoriteImage.setImageResource(if (isFavorite) R.drawable._favorite else R.drawable.favorite_border)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.count.text = "$quantity"
    }

    override fun onFavoriteDishAdded(dishTitle: String) {
        binding.favoriteImage.setImageResource(R.drawable._favorite)
        StyleableToast.makeText(requireContext(), dishTitle, R.style.successToast).show()
    }

    override fun onFavoriteDishAlreadyExists(dishTitle: String) {
        StyleableToast.makeText(requireContext(), dishTitle, R.style.errorToast).show()
    }
}
