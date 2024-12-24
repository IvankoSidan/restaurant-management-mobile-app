package com.example.myfirstapp.Presentation.Fragments.LoginRegisterFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.SealedClasses.RegistrationResult
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.example.myfirstapp.data.Enums.UserRole
import com.example.myfirstapp.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import io.github.muddz.styleabletoast.StyleableToast

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val name = binding.nameEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()

            if (email.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.registration(email, name, password)
            } else {
                showErrorToast("Empty fields are not allowed !")
            }
        }

        observeRegistrationResult()

        binding.textView.setOnClickListener {
            navigateToEntryFragment()
        }
    }

    private fun observeRegistrationResult() {
        loginViewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RegistrationResult.Success -> {
                    navigateToEntryFragment()
                    showSuccessToast("Registration successful !")
                }
                is RegistrationResult.Failure -> showErrorToast(result.message ?: "Registration failed!")
                null -> showErrorToast("Unexpected error occurred.")
            }
        }
    }

    private fun navigateToEntryFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, EntryFragment())
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
        loginViewModel.clearRegistrationResult()
    }
}
