package com.example.myfirstapp.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.Api.PaymentApi
import com.example.myfirstapp.Interfaces.PaymentRepository
import com.example.myfirstapp.Interfaces.StringProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Enums.CardType
import com.example.myfirstapp.data.Models.Card
import com.example.myfirstapp.data.Enums.PaymentStatus
import com.example.myfirstapp.data.Models.Payment
import com.example.myfirstapp.data.Models.PaymentMethod
import kotlinx.coroutines.launch
import retrofit2.Response

class PaymentViewModel(private val repository: PaymentRepository, private val stringProvider: StringProvider) : ViewModel() {

    private val _createdPayment = MutableLiveData<Response<Payment>>()
    val createdPayment: LiveData<Response<Payment>> get() = _createdPayment

    private val _createdCard = MutableLiveData<Response<Card>>()
    val createdCard: LiveData<Response<Card>> get() = _createdCard

    private val _selectedMethod = MutableLiveData<PaymentMethod>()
    val selectedMethod: LiveData<PaymentMethod> get() = _selectedMethod

    private val _saveCardDetails = MutableLiveData<Boolean>()
    val saveCardDetails: LiveData<Boolean> get() = _saveCardDetails

    private val _paymentMethods = MutableLiveData<MutableList<PaymentMethod>>().apply {
        value = mutableListOf()
    }
    val paymentMethods: LiveData<MutableList<PaymentMethod>> get() = _paymentMethods

    init {
        _paymentMethods.value?.firstOrNull { it.isSelected }?.let {
            _selectedMethod.value = it
        }
    }

    fun addPaymentMethod(newMethod: PaymentMethod) = viewModelScope.launch {
        try {
            repository.addPaymentMethod(newMethod)
            getPaymentMethods(newMethod.userId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPaymentMethods(userId: Long) = viewModelScope.launch {
        try {
            val methods = repository.getPaymentMethods(userId)
            _paymentMethods.postValue(methods.toMutableList())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setSaveCardDetails(value: Boolean) {
        _saveCardDetails.value = value
    }

    fun selectMethod(method: PaymentMethod) {
        _paymentMethods.value?.forEach { it.isSelected = false }
        method.isSelected = true
        _selectedMethod.value = method
        _paymentMethods.value = _paymentMethods.value
    }

    fun createPayment(payment: Payment) = viewModelScope.launch {
        try {
            val response = repository.createPayment(payment)
            _createdPayment.postValue(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createCard(card: Card) = viewModelScope.launch {
        try {
            val response = repository.createCard(card)
            _createdCard.postValue(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deletePaymentMethod(paymentMethod: PaymentMethod) = viewModelScope.launch {
        try {
            repository.deletePaymentMethod(paymentMethod.id)
            _paymentMethods.value?.remove(paymentMethod)
            _paymentMethods.value = _paymentMethods.value
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

