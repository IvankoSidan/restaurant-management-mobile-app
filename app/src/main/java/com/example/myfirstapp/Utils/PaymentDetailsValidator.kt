package com.example.myfirstapp.Utils

import android.content.Context
import androidx.annotation.StringRes
import com.example.myfirstapp.R
import com.example.myfirstapp.ValidationClasses.CVVValidator
import com.example.myfirstapp.ValidationClasses.CardHolderNameValidator
import com.example.myfirstapp.ValidationClasses.CardNumberMask
import com.example.myfirstapp.ValidationClasses.ExpirationDateMask

class PaymentDetailsValidator(
    private val context: Context,
    private val cardNumberMask: CardNumberMask,
    private val expirationDateMask: ExpirationDateMask,
    private val cvvValidator: CVVValidator,
    private val cardHolderNameValidator: CardHolderNameValidator,
    private val errorHandler: (String) -> Unit
) {

    fun validateAllInputs(
        cardNumber: String,
        expirationDate: String,
        cvv: String,
        cardHolderName: String
    ): Boolean {
        return validateCardNumber(cardNumber) &&
                validateExpirationDate(expirationDate) &&
                validateCvv(cvv) &&
                validateCardHolderName(cardHolderName)
    }

    fun validateCardNumber(cardNumber: String): Boolean {
        return when {
            cardNumber.isBlank() -> {
                showError(R.string.error_empty_card_number)
                false
            }
            !cardNumberMask.validate(cardNumber) -> {
                showError(cardNumberMask.getErrorMessage())
                false
            }
            else -> true
        }
    }

    fun validateExpirationDate(expirationDate: String): Boolean {
        return when {
            expirationDate.isBlank() -> {
                showError(R.string.error_empty_expiration_date)
                false
            }
            !expirationDateMask.validate(expirationDate) -> {
                showError(expirationDateMask.getErrorMessage())
                false
            }
            else -> true
        }
    }

    fun validateCvv(cvv: String): Boolean {
        return when {
            cvv.isBlank() -> {
                showError(R.string.error_empty_cvv)
                false
            }
            !cvvValidator.validate(cvv) -> {
                showError(cvvValidator.getErrorMessage())
                false
            }
            else -> true
        }
    }

    fun validateCardHolderName(name: String): Boolean {
        return when {
            name.isBlank() -> {
                showError(R.string.error_empty_card_holder_name)
                false
            }
            !cardHolderNameValidator.validate(name) -> {
                showError(cardHolderNameValidator.getErrorMessage())
                false
            }
            else -> true
        }
    }

    private fun showError(@StringRes resId: Int) {
        errorHandler(context.getString(resId))
    }

    private fun showError(message: String) {
        errorHandler(message)
    }
}
