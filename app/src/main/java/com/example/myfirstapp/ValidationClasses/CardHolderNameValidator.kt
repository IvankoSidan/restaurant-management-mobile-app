package com.example.myfirstapp.ValidationClasses

import android.content.Context
import com.example.myfirstapp.Interfaces.Validator
import com.example.myfirstapp.R

class CardHolderNameValidator(private val context: Context) : Validator {
    private val namePattern = Regex("^[A-Za-zА-Яа-яЁё ]+$")

    override fun validate(input: String): Boolean {
        return namePattern.matches(input)
    }

    override fun getErrorMessage(): String {
        return context.getString(R.string.error_invalid_card_holder_name)
    }
}