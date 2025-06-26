package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.LoginViewModel
import com.example.myfirstapp.databinding.EditProfileDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditProfileFragment : BottomSheetDialogFragment() {

    private lateinit var binding: EditProfileDialogBinding

    private val guestViewModel: GuestViewModel by viewModel(ownerProducer  = { requireActivity() })
    private val loginViewModel: LoginViewModel by viewModel(ownerProducer  = { requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditProfileDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()

        binding.btnEdit.setOnClickListener {
            editProfile()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setupUI() {

        guestViewModel.guest.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.personName.setText(it.name)
                binding.personEmail.setText(it.email)
            }
        }
    }

    private fun editProfile() {
        val newName = binding.personName.text.toString()
        val newEmail = binding.personEmail.text.toString()
        val newPassword = binding.personPassword.text.toString()

        val currentUser = guestViewModel.guest.value ?: return

        val updatedName = if (newName.isNotEmpty() && newName != currentUser.name) newName else currentUser.name
        val updatedEmail = if (newEmail.isNotEmpty() && newEmail != currentUser.email) newEmail else currentUser.email
        val updatedPassword = if (newPassword.isNotEmpty() && newPassword != currentUser.password) newPassword else ""

        val updatedUser = if (currentUser.idUser != 0L) {
            currentUser.copy(
                name = updatedName,
                email = updatedEmail,
                password = updatedPassword
            )
        } else {
            currentUser
        }

        if (updatedUser != currentUser) {
            guestViewModel.updateProfile(updatedUser)
            loginViewModel.updateUserInPreferences(updatedUser)
        }
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.background = ColorDrawable(Color.TRANSPARENT)
                val params = it.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                it.layoutParams = params
            }
        }
        return dialog
    }
}

