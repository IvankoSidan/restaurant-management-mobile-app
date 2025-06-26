package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myfirstapp.Presentation.Activities.MainActivity
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.example.myfirstapp.databinding.FragmentProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val guestViewModel: GuestViewModel by viewModel(ownerProducer = { requireActivity() })
    private val loginViewModel: LoginViewModel by viewModel(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProfile()
        setupUI()
    }

    private fun setupUI() {
        binding.backImage.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editProfile.setOnClickListener {
            EditProfileFragment().show(parentFragmentManager, "EditProfileDialog")
        }

        binding.logOutSystem.setOnClickListener {
            loginViewModel.clearToken()
            guestViewModel.clearUser()
            (requireActivity() as MainActivity).getNavController().navigate(R.id.entryFragment)
        }

        binding.btnOrderHistory.setOnClickListener {
            findNavController().navigate(R.id.orderHistoryFragment)
        }

        binding.btnBookingHistory.setOnClickListener {
            findNavController().navigate(R.id.bookingHistoryFragment)
        }

        binding.btnFeatures.setOnClickListener {
            findNavController().navigate(R.id.favoriteFragment)
        }

        binding.btnSelectedLanguage.setOnClickListener {
            changeLocale()
        }
    }

    private fun changeLocale() {
        val newLocale = if (Locale.getDefault().language == "ru") Locale.ENGLISH else Locale("ru")
        Locale.setDefault(newLocale)
        val config = resources.configuration
        config.setLocale(newLocale)
        resources.updateConfiguration(config, resources.displayMetrics)

        val prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_language", newLocale.language).apply()

        guestViewModel.clearCategories()
        guestViewModel.clearDishes()

        requireActivity().recreate()

        guestViewModel.loadCategories()
        guestViewModel.loadAllDishes()
        guestViewModel.loadAllBestDishes()
        guestViewModel.loadFavoriteDishes()
    }

    private fun observeProfile() {
        guestViewModel.guest.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameUsers.text = it.name
                binding.emailUsers.text = it.email
            }
        }
    }
}


