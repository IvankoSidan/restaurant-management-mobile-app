package com.example.myfirstapp.Presentation.Fragments.LoginRegisterFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.Presentation.Fragments.GuestFragment.ClientFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.SealedClasses.LoginResult
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.data.Enums.UserRole
import com.example.myfirstapp.data.Models.User
import com.example.myfirstapp.databinding.FragmentEntryBinding
import com.google.android.material.snackbar.Snackbar
import io.github.muddz.styleabletoast.StyleableToast

class EntryFragment : Fragment() {
    private lateinit var binding: FragmentEntryBinding

    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    private val guestViewModel: GuestViewModel by lazy {
        ViewModelProvider(requireActivity())[GuestViewModel::class.java]
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        observeLoginResult()
    }

    private fun setupUi() {

        binding.textView.setOnClickListener {
            navigateToFragment(RegisterFragment())
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            if (email.isNotBlank() && password.isNotBlank()) {
                loginViewModel.login(email, password)
            } else {
                showErrorToast("Please fill in all fields !")
            }
        }
    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    showSuccessToast("Login successful !")
                    handleRoleNavigation(result.user)
                    guestViewModel.setUser(result.user)
                }
                is LoginResult.Error -> showErrorToast(result.message ?: "Login failed !")
                else -> {}
            }
        }
    }

    private fun handleRoleNavigation(user: User) {
        val fragment = when (user.role) {
            UserRole.GUEST -> ClientFragment()
            else -> throw InvalidRoleException()
        }
        navigateToFragment(fragment)
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showErrorToast(message: String) {
        StyleableToast.makeText(requireContext(), message, Snackbar.LENGTH_SHORT, R.style.errorToast).show()
    }

    private fun showSuccessToast(message: String) {
        StyleableToast.makeText(requireContext(), message, Toast.LENGTH_SHORT, R.style.successToast).show()
    }

    override fun onStop() {
        super.onStop()
        loginViewModel.clearLoginResult()
    }

    class InvalidRoleException : Exception("Invalid role selected")
}
