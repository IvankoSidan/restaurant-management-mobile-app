package com.example.myfirstapp.ValidationClasses

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.myfirstapp.Interfaces.Validator
import com.example.myfirstapp.R
import java.util.Calendar
import kotlin.math.min

class ExpirationDateMask(private val context: Context) : TextWatcher, Validator {
    private var isUpdating = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isUpdating) return
        isUpdating = true

        val input = s.toString().replace("/", "")
        val formatted = if (input.length >= 3) {
            input.substring(0, 2) + "/" + input.substring(2, min(input.length, 4))
        } else {
            input
        }

        (s as Editable).replace(0, s.length, formatted)
        isUpdating = false
    }

    override fun validate(input: String): Boolean {
        val normalized = input.replace(" ", "")
        val regex = Regex("^(0[1-9]|1[0-2])/\\d{2}\$") // MM/YY формат
        return regex.matches(normalized) && isFutureDate(normalized)
    }

    private fun isFutureDate(date: String): Boolean {
        val currentDate = Calendar.getInstance()
        val monthYear = date.split("/")
        val month = monthYear[0].toInt()
        val year = "20${monthYear[1]}".toInt()
        val expirationDate = Calendar.getInstance().apply {
            set(year, month - 1, 1)
        }
        return expirationDate.after(currentDate)
    }

    override fun getErrorMessage(): String {
        return context.getString(R.string.error_invalid_expiration_date)
    }
}