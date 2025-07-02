package com.example.myfirstapp.ValidationClasses

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.myfirstapp.Interfaces.Validator
import com.example.myfirstapp.R

class CardNumberMask(private val context: Context) : TextWatcher, Validator {

    companion object {
        private const val CARD_NUMBER_LENGTH = 16
        private const val CARD_NUMBER_GROUP_SIZE = 4
        private const val MASK_CHAR = '•'
        private const val VISIBLE_DIGITS = 4
    }

    private var isUpdating = false
    private var currentText = ""
    private var actualNumber = ""
    private var isMasked = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isUpdating || s == null) return
        val input = s.toString()
        if (input == currentText) return

        isUpdating = true

        if (input.contains(MASK_CHAR)) {
            isMasked = true
            currentText = input
        } else {
            isMasked = false
            val digits = input.replace(" ", "")
            actualNumber = digits.take(CARD_NUMBER_LENGTH)

            val formatted = StringBuilder()
            actualNumber.forEachIndexed { index, ch ->
                formatted.append(ch)
                if ((index + 1) % CARD_NUMBER_GROUP_SIZE == 0 && index + 1 != actualNumber.length) {
                    formatted.append(" ")
                }
            }
            currentText = formatted.toString()
            s.replace(0, s.length, currentText)
        }

        isUpdating = false
    }

    fun setMaskedNumber(lastFour: String) {
        isUpdating = true
        isMasked = true
        actualNumber = ""
        currentText = "${MASK_CHAR.toString().repeat(12)} $lastFour"
        isUpdating = false
    }

    override fun validate(input: String): Boolean {
        return when {
            input.contains(MASK_CHAR) -> true
            else -> {
                val clean = input.replace(" ", "")
                clean.length == CARD_NUMBER_LENGTH
            }
        }
    }

    override fun getErrorMessage(): String {
        return context.getString(R.string.error_invalid_card_number)
    }

    fun getCleanNumber(): String = actualNumber
    fun getLastFourDigits(): String =
        if (isMasked) currentText.takeLast(VISIBLE_DIGITS)
        else actualNumber.takeLast(VISIBLE_DIGITS)
}
