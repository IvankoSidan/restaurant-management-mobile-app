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
import com.example.myfirstapp.SealedClasses.LoginResult
import com.example.myfirstapp.SealedClasses.ValidationResult
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.example.myfirstapp.databinding.FragmentEntryBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class EntryFragment : Fragment() {
    private lateinit var binding: FragmentEntryBinding
    private val loginViewModel: LoginViewModel by viewModel(ownerProducer = { requireActivity() })
    private val guestViewModel: GuestViewModel by viewModel(ownerProducer = { requireActivity() })

    private val RC_GOOGLE = 1001
    private lateinit var googleClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем GoogleSignInClient
        googleClient = GoogleSignInClientProvider.getClient(requireActivity())

        setupUi()
        observeLoginResult()
    }

    private fun setupUi() {
        binding.entryTextView.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        binding.btnEntrySignIn.setOnClickListener {
            val email = binding.entryEmailEt.text.toString().trim()
            val password = binding.entryPasswordEt.text.toString().trim()
            when (validateInputs(email, password)) {
                is ValidationResult.Success -> loginViewModel.login(email, password)
                else -> { /* Ошибка показывает сама валидация */ }
            }
        }

        binding.btnEntrySignInWithGoogle.setOnClickListener {
            startActivityForResult(googleClient.signInIntent, RC_GOOGLE)
        }
    }

    private fun validateInputs(email: String, password: String): ValidationResult {
        Validator.validateEmail(email).let {
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
                account.idToken?.let { loginViewModel.loginWithGoogle(it) }
            } catch (e: ApiException) {
                Log.e("EntryFragment", "Google sign-in failed with status code: ${e.statusCode}", e)
                showErrorToast(getString(R.string.google_sign_in_failed, e.statusCode))
            }
        }
    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    guestViewModel.setUser(result.user)
                    findNavController().navigate(R.id.homeFragment)
                }
                is LoginResult.Error -> showErrorToast(result.message ?: getString(R.string.login_failed))
                else -> {  }
            }
        }
    }

    private fun showErrorToast(message: String) {
        StyleableToast.makeText(requireContext(), message, Toast.LENGTH_SHORT, R.style.errorToast).show()
    }

    override fun onStop() {
        super.onStop()
        loginViewModel.clearLoginResult()
    }
}
