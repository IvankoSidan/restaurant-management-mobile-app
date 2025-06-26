package com.example.myfirstapp.ValidationClasses

import android.content.Context
import com.example.myfirstapp.Interfaces.Validator
import com.example.myfirstapp.R


class CVVValidator(private val context: Context) : Validator {
    override fun validate(input: String): Boolean {
        val regex = Regex("^\\d{3,4}\$")
        return regex.matches(input)
    }

    override fun getErrorMessage(): String {
        return context.getString(R.string.error_invalid_cvv)
    }
}