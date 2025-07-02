package com.example.myfirstapp.Presentation.Fragments.LoginRegisterFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myfirstapp.Objects.GoogleSignInClientProvider
import com.example.myfirstapp.Objects.Validator
import com.example.myfirstapp.Presentation.Activities.MainActivity
import com.example.myfirstapp.R
import com.example.myfirstapp.SealedClasses.RegistrationResult
import com.example.myfirstapp.SealedClasses.ValidationResult
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.example.myfirstapp.databinding.FragmentRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val loginViewModel: LoginViewModel by viewModel(ownerProducer = { requireActivity() })
    private val guestViewModel: GuestViewModel by viewModel(ownerProducer = { requireActivity() })

    private val RC_GOOGLE = 1002
    private lateinit var googleClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleClient = GoogleSignInClientProvider.getClient(requireActivity())
        setupUi()
        observeRegistrationResult()
    }

    private fun setupUi() {
        binding.btnRegisterSignUp.setOnClickListener {
            val email = binding.registerEmailEt.text.toString().trim()
            val name = binding.registerNameEt.text.toString().trim()
            val password = binding.registerPasswordEt.text.toString().trim()
            val rememberMe = binding.registerRememberMeCheckBox.isChecked

            when (validateInputs(email, name, password)) {
                is ValidationResult.Success -> loginViewModel.registration(email, name, password, rememberMe)
                else -> {  }
            }
        }

        binding.btnRegisterSignUpWithGoogle.setOnClickListener {
            startActivityForResult(googleClient.signInIntent, RC_GOOGLE)
        }

        binding.registerTextView.setOnClickListener {
            findNavController().navigate(R.id.entryFragment)
        }
    }

    private fun validateInputs(email: String, name: String, password: String): ValidationResult {
        Validator.validateEmail(email).let {
            if (it is ValidationResult.Error) return it
        }
        Validator.validateName(name).let {
            if (it is ValidationResult.Error) return it
        }
        Validator.validatePassword(password).let {
            if (it is ValidationResult.Error) return it
        }
        return ValidationResult.Success
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                account.idToken?.let { loginViewModel.registerWithGoogle(it) }
            } catch (e: ApiException) {
                Log.e("RegisterFragment", "Google sign-up failed with status code: ${e.statusCode}", e)
                showErrorToast(getString(R.string.google_sign_up_failed, e.statusCode))
            }
        }
    }

    private fun observeRegistrationResult() {
        loginViewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RegistrationResult.Success -> {
                    val user = loginViewModel.getUserFromPreferences()
                    if (user != null) {
                        guestViewModel.setUser(user)
                    }
                    findNavController().navigate(R.id.homeFragment)
                    showSuccessToast(getString(R.string.registration_successful))
                }
                is RegistrationResult.Failure -> showErrorToast(result.message ?: getString(R.string.registration_failed))
                else -> { /* Idle or Loading */ }
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
