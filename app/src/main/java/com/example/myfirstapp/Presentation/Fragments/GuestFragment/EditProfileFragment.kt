package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.databinding.EditProfileDialogBinding

class EditProfileFragment : DialogFragment() {

    private lateinit var binding: EditProfileDialogBinding
    private lateinit var guestViewModel: GuestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditProfileDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        guestViewModel = ViewModelProvider(requireActivity())[GuestViewModel::class.java]
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
        guestViewModel.guest.observe(viewLifecycleOwner, { user ->
            user?.let {
                binding.personName.setText(user.name)
                binding.personEmail.setText(user.email)
                binding.personPassword.setText(user.password)
            }
        })
    }

    private fun editProfile() {
        val newName = binding.personName.text.toString()
        val newEmail = binding.personEmail.text.toString()
        val newPassword = binding.personPassword.text.toString()

        val currentUser  = guestViewModel.guest.value ?: return
        val updatedUser  = currentUser .copy(
            name = if (newName.isNotEmpty() && newName != currentUser.name) newName else currentUser.name,
            email = if (newEmail.isNotEmpty() && newEmail != currentUser.email) newEmail else currentUser.email,
            password = if (newPassword.isNotEmpty() && newPassword != currentUser.password) newPassword else currentUser.password
        )
        if (updatedUser  != currentUser) {
            guestViewModel.updateProfile(updatedUser)
        }
    }

    override fun getTheme(): Int = R.style.Theme_AppBottomSheetDialogTheme
}