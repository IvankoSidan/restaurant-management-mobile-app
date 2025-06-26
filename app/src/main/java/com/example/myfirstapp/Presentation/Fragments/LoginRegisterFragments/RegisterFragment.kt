package com.example.myfirstapp.Presentation.Fragments.LoginRegisterFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myfirstapp.Objects.Validator
import com.example.myfirstapp.Presentation.Activities.MainActivity
import com.example.myfirstapp.R
import com.example.myfirstapp.SealedClasses.RegistrationResult
import com.example.myfirstapp.SealedClasses.ValidationResult
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.example.myfirstapp.databinding.FragmentRegisterBinding
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    private val loginViewModel: LoginViewModel by viewModel(ownerProducer  = { requireActivity() })

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
            val rememberMe = binding.rememberMeCheckBox.isChecked

            when (validateInputs(email, name, password)) {
                is ValidationResult.Success -> {
                    loginViewModel.registration(email, name, password, rememberMe)
                }
                else -> {}
            }
        }

        observeRegistrationResult()

        binding.textView.setOnClickListener {
            findNavController().navigate(R.id.entryFragment)
        }
    }

    private fun validateInputs(email: String, name: String, password: String): ValidationResult {
        Validator.validateEmail(email).let { result ->
            if (result is ValidationResult.Error) {
                showErrorToast(getString(R.string.invalid_email, result.message))
                return result
            }
        }

        Validator.validateName(name).let { result ->
            if (result is ValidationResult.Error) {
                showErrorToast(getString(R.string.invalid_name, result.message))
                return result
            }
        }

        Validator.validatePassword(password).let { result ->
            if (result is ValidationResult.Error) {
                showErrorToast(getString(R.string.invalid_password, result.message))
                return result
            }
        }

        return ValidationResult.Success
    }

    private fun observeRegistrationResult() {
        loginViewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RegistrationResult.Success -> {
                    (requireActivity() as MainActivity).getNavController().navigate(R.id.homeFragment)
                    showSuccessToast(getString(R.string.registration_successful))
                }
                is RegistrationResult.Failure -> {
                    val errorMessage = result.message ?: getString(R.string.registration_failed)
                    showErrorToast(errorMessage)
                }
                is RegistrationResult.Idle, is RegistrationResult.Loading -> {
                    // Не показываем ошибок
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
        loginViewModel.clearRegistrationResult()
    }
}
