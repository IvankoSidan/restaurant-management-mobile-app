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
import com.example.myfirstapp.data.DTO.CardRequestDto
import com.example.myfirstapp.data.DTO.PaymentRequest
import com.example.myfirstapp.data.DTO.PaymentResult
import com.example.myfirstapp.data.Enums.CardType
import com.example.myfirstapp.data.Models.Card
import com.example.myfirstapp.data.Enums.PaymentStatus
import com.example.myfirstapp.data.Models.Payment
import com.example.myfirstapp.data.Models.PaymentMethod
import kotlinx.coroutines.launch
import retrofit2.Response

class PaymentViewModel(
    private val repository: PaymentRepository,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _paymentMethods = MutableLiveData<MutableList<PaymentMethod>>(mutableListOf())
    val paymentMethods: LiveData<MutableList<PaymentMethod>> = _paymentMethods

    private val _selectedMethod = MutableLiveData<PaymentMethod?>()
    val selectedMethod: LiveData<PaymentMethod?> = _selectedMethod

    private val _saveCardDetails = MutableLiveData<Boolean>(false)
    val saveCardDetails: LiveData<Boolean> = _saveCardDetails

    private val _createdCard = MutableLiveData<Response<Card>?>(null)
    val createdCard: LiveData<Response<Card>?> = _createdCard

    private val _paymentResult = MutableLiveData<PaymentResult?>()
    val paymentResult: LiveData<PaymentResult?> = _paymentResult

    private var isProcessingPayment = false
    private var isCreatingCard = false

    init {
        _paymentMethods.value?.firstOrNull { it.isSelected }?.let { _selectedMethod.value = it }
    }

    fun addPaymentMethod(newMethod: PaymentMethod) = viewModelScope.launch {
        runCatching {
            repository.addPaymentMethod(newMethod)
            getPaymentMethods(newMethod.userId)
        }.onFailure { it.printStackTrace() }
    }

    fun getPaymentMethods(userId: Long) = viewModelScope.launch {
        runCatching {
            val list = repository.getPaymentMethods(userId)
                .filter { it.id != 0L && it.cardLastFour.isNotBlank() }
            _paymentMethods.postValue(list.toMutableList())

            if (_selectedMethod.value == null && list.isNotEmpty()) {
                selectMethod(list.first())
            }
        }.onFailure {
            it.printStackTrace()
            _paymentMethods.postValue(mutableListOf())
            _selectedMethod.postValue(null)
        }
    }

    fun selectMethod(method: PaymentMethod) {
        val updatedList = _paymentMethods.value?.map { paymentMethod ->
            paymentMethod.copy(isSelected = paymentMethod.id == method.id)
        }?.toMutableList() ?: mutableListOf()
        _paymentMethods.value = updatedList
        _selectedMethod.value = method
    }

    fun setSaveCardDetails(value: Boolean) {
        _saveCardDetails.value = value
    }

    fun clearPaymentResult() {
        _paymentResult.value = null
    }

    fun resetCreatedCardState() {
        _createdCard.value = null
    }

    fun createCard(dto: CardRequestDto) = viewModelScope.launch {
        if (isCreatingCard) return@launch
        isCreatingCard = true
        runCatching {
            resetCreatedCard()
            val resp = repository.createCard(dto)
            _createdCard.postValue(resp)
        }.onFailure {
            it.printStackTrace()
            _createdCard.postValue(null)
        }.onSuccess {
            isCreatingCard = false
        }.onFailure {
            isCreatingCard = false
        }
    }

    fun deletePaymentMethod(paymentMethod: PaymentMethod) = viewModelScope.launch {
        runCatching {
            repository.deletePaymentMethod(paymentMethod.id)
            val updatedList = _paymentMethods.value?.toMutableList()?.apply {
                remove(paymentMethod)
            }
            _paymentMethods.value = updatedList ?: mutableListOf()
            if (_selectedMethod.value?.id == paymentMethod.id) {
                if (updatedList?.isNotEmpty() == true) {
                    selectMethod(updatedList.first())
                } else {
                    _selectedMethod.value = null
                }
            }
        }.onFailure { it.printStackTrace() }
    }


    fun processPayment(request: PaymentRequest) = viewModelScope.launch {
        if (isProcessingPayment) return@launch
        isProcessingPayment = true
        runCatching {
            resetPaymentResult()
            val resp = repository.processPayment(request)
            if (resp.isSuccessful && resp.body() != null) {
                _paymentResult.postValue(resp.body())
            } else {
                _paymentResult.postValue(
                    PaymentResult(
                        success = false,
                        transactionId = null,
                        message = stringProvider.getString(R.string.payment_failed)
                    )
                )
            }
        }.onFailure {
            it.printStackTrace()
            _paymentResult.postValue(
                PaymentResult(
                    success = false,
                    transactionId = null,
                    message = it.localizedMessage ?: stringProvider.getString(R.string.payment_failed)
                )
            )
        }.onSuccess {
            isProcessingPayment = false
        }.onFailure {
            isProcessingPayment = false
        }
    }

    fun resetPaymentResult() {
        _paymentResult.value = null
    }

    fun resetCreatedCard() {
        _createdCard.value = null
    }
}
