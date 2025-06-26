package com.example.myfirstapp.ValidationClasses

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.myfirstapp.Interfaces.Validator
import com.example.myfirstapp.R

class CardNumberMask(private val context: Context) : TextWatcher, Validator {
    private var isUpdating = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isUpdating) return
        isUpdating = true

        val digits = s.toString().replace(" ", "")
        val formatted = StringBuilder()
        for (i in digits.indices) {
            formatted.append(digits[i])
            if ((i + 1) % 4 == 0 && i + 1 != digits.length) {
                formatted.append(" ")
            }
        }

        (s as Editable).replace(0, s.length, formatted.toString())
        isUpdating = false
    }

    override fun validate(input: String): Boolean {
        val regex = Regex("^\\d{16}\$")
        return regex.matches(input.replace(" ", ""))
    }

    override fun getErrorMessage(): String {
        return context.getString(R.string.error_invalid_card_number)
    }
}