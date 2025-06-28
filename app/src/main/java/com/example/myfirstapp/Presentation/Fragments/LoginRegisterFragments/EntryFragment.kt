package com.example.myfirstapp.Presentation.Fragments.LoginRegisterFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myfirstapp.Objects.Validator
import com.example.myfirstapp.Presentation.Activities.MainActivity
import com.example.myfirstapp.R
import com.example.myfirstapp.SealedClasses.LoginResult
import com.example.myfirstapp.SealedClasses.ValidationResult
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.example.myfirstapp.databinding.FragmentEntryBinding
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class EntryFragment : Fragment() {
    private lateinit var binding: FragmentEntryBinding

    private val loginViewModel: LoginViewModel by viewModel(ownerProducer = { requireActivity() })
    private val guestViewModel: GuestViewModel by viewModel(ownerProducer = { requireActivity() })

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
            findNavController().navigate(R.id.registerFragment)
        }

        binding.button.setOnClickListener {
            val email = "ivankosidenko@gmail.com"
            val password = "ThreeS9379992aca"

            when (validateInputs(email, password)) {
                is ValidationResult.Success -> loginViewModel.login(email, password)
                else -> {}
            }
        }
    }

    private fun validateInputs(email: String, password: String): ValidationResult {
        Validator.validateEmail(email).let { result ->
            if (result is ValidationResult.Error) {
                showErrorToast(getString(R.string.email_validation_error, result.message))
                return result
            }
        }

        Validator.validatePassword(password).let { result ->
            if (result is ValidationResult.Error) {
                showErrorToast(getString(R.string.password_validation_error, result.message))
                return result
            }
        }

        return ValidationResult.Success
    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    showSuccessToast(getString(R.string.login_successful))
                    guestViewModel.setUser(result.user)
                    (requireActivity() as MainActivity).getNavController().navigate(R.id.homeFragment)
                }
                is LoginResult.Error -> {
                    val errorMessage = result.message ?: getString(R.string.login_failed)
                    showErrorToast(errorMessage)
                }
                is LoginResult.Idle, is LoginResult.Loading -> {
                    // Ничего не делаем
                }
                else -> showErrorToast(getString(R.string.unexpected_error))
            }
        }
    }

    private fun showErrorToast(message: String) {
        StyleableToast.makeText(requireContext(), message, Toast.LENGTH_SHORT, R.style.errorToast).show()
    }

    private fun showSuccessToast(message: String) {
        StyleableToast.makeText(requireContext(), message, Toast.LENGTH_SHORT, R.style.successToast).show()
    }

    override fun onStop() {
        super.onStop()
        loginViewModel.clearLoginResult()
    }
}
