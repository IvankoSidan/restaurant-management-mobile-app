package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myfirstapp.Interfaces.FavoriteDishListener
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.databinding.FragmentDetailBinding
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailFragment : Fragment(), FavoriteDishListener {
    private lateinit var binding: FragmentDetailBinding
    private var quantity = 1
    private var isFavorite = false

    private val guestViewModel: GuestViewModel by viewModel(ownerProducer  = { requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.TRANSPARENT)
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
        guestViewModel.currentDish.value?.let { dish ->
            guestViewModel.guest.value?.let { user ->
                if (guestViewModel.favoriteDishes.value?.any { favorite -> favorite.idDish == dish.idDish } == true) {
                    guestViewModel.removeFavoriteDish(user.idUser, dish.idDish)
                    binding.favoriteImage.setImageResource(R.drawable.favorite_border)
                    StyleableToast.makeText(
                        requireContext(),
                        "${dish.title} ${getString(R.string.remove_favorite)}",
                        R.style.successToast
                    ).show()
                } else {
                    guestViewModel.addFavoriteDish(user.idUser, dish.idDish)
                    binding.favoriteImage.setImageResource(R.drawable._favorite)
                }
            }
        }
    }

    private fun setupUI(dish: Dish) {
        with(binding) {
            titleTxt.text = dish.title
            priceMeal.text = String.format("%.2f $", dish.price)
            textDescription.text = dish.description
            timeTxt.text = getString(R.string.dish_time, dish.timeValue, getString(R.string.min))
            rateTxt.text = getString(R.string.dish_rating, dish.star, getString(R.string.rating))
            ratingBar.rating = dish.star
            textTotalPrice.text = String.format("%.2f $", quantity * dish.price)

            Glide.with(requireContext())
                .load(dish.imagePath)
                .error(R.drawable.pepperoni)
                .into(imageMeal)

            isFavorite = guestViewModel.favoriteDishes.value?.any { favorite -> favorite.idDish == dish.idDish } == true
            binding.favoriteImage.setImageResource(if (isFavorite) R.drawable._favorite else R.drawable.favorite_border)
        }
    }

    private fun addToCart() {
        guestViewModel.currentDish.value?.let { dish ->
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
        guestViewModel.currentDish.value?.let { currentDish ->
            binding.textTotalPrice.text = String.format("%.2f $", quantity * currentDish.price)
        }
    }

    private fun setupObservers() {
        guestViewModel.currentDish.observe(viewLifecycleOwner) { dish ->
            dish?.let {
                setupUI(it)
            }
        }
        guestViewModel.favoriteDishes.observe(viewLifecycleOwner) { favorites ->
            val currentDish = guestViewModel.currentDish.value
            isFavorite = currentDish?.let { dish ->
                favorites.any { favorite -> favorite.idDish == dish.idDish }
            } == true
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
