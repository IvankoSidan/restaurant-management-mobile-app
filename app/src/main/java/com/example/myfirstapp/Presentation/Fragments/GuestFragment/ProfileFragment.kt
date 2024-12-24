package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myfirstapp.Presentation.Fragments.LoginRegisterFragments.EntryFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var guestViewModel: GuestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        guestViewModel = ViewModelProvider(requireActivity())[GuestViewModel::class.java]
        observeProfile()
        setupUI(view)
    }

    private fun setupUI(view: View) {
        binding.backImage.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.logOutSystem.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EntryFragment())
                .commit()
        }

        binding.btnOrderHistory.setOnClickListener {
            findNavController().navigate(R.id.orderHistoryFragment)
        }

        binding.btnPaymentDetails.setOnClickListener {
            findNavController().navigate(R.id.paymentDetailsFragment)
        }

        binding.personImage.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.editProfile.setOnClickListener {
            EditProfileFragment().show(parentFragmentManager, "EditProfileDialog")
        }

        binding.btnFeatures.setOnClickListener {
            findNavController().navigate(R.id.favoriteFragment)
        }
    }

    private fun observeProfile() {
        guestViewModel.guest.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameUsers.text = user.name
                binding.emailUsers.text = user.email
            }
        }
    }
}
