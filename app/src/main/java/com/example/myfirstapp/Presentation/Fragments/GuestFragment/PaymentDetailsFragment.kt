package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Paint.Style
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Adapters.CardAdapter
import com.example.myfirstapp.Interfaces.CardAdapterListener
import com.example.myfirstapp.data.Models.Card
import com.example.myfirstapp.data.Models.Payment
import com.example.myfirstapp.R
import com.example.myfirstapp.Utils.PaymentDetailsValidator
import com.example.myfirstapp.ValidationClasses.CVVValidator
import com.example.myfirstapp.ValidationClasses.CardHolderNameValidator
import com.example.myfirstapp.ValidationClasses.CardNumberMask
import com.example.myfirstapp.ValidationClasses.ExpirationDateMask
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.ViewModels.PaymentViewModel
import com.example.myfirstapp.data.DTO.CardRequestDto
import com.example.myfirstapp.data.DTO.PaymentRequest
import com.example.myfirstapp.data.DTO.PaymentRequestDto
import com.example.myfirstapp.data.DTO.PaymentResult
import com.example.myfirstapp.data.Enums.CardType
import com.example.myfirstapp.data.Enums.OrderStatus
import com.example.myfirstapp.data.Enums.PaymentStatus
import com.example.myfirstapp.data.Models.Order
import com.example.myfirstapp.databinding.FragmentPaymentDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentDetailsFragment : BottomSheetDialogFragment(), CardAdapterListener {

    private var _binding: FragmentPaymentDetailsBinding? = null
    private val binding get() = _binding!!

    private val paymentViewModel: PaymentViewModel by viewModel(ownerProducer = { requireActivity() })
    private val orderViewModel: OrderViewModel by viewModel(ownerProducer = { requireActivity() })

    private lateinit var cardAdapter: CardAdapter
    private lateinit var paymentDetailsValidator: PaymentDetailsValidator
    private lateinit var cardNumberMask: CardNumberMask
    private lateinit var expirationDateMask: ExpirationDateMask
    private lateinit var cvvValidator: CVVValidator
    private lateinit var cardHolderNameValidator: CardHolderNameValidator

    private var currentOrder: Order? = null
    private var isProcessing = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initValidators()
        setupUI()
        setupObservers()
        checkOrderStatus()
    }

    private fun checkOrderStatus() {
        currentOrder?.let { order ->
            if (order.status == OrderStatus.PAID) {
                showSuccessPopup()
            }
        }
    }

    private fun initValidators() {
        cardNumberMask = CardNumberMask(requireContext())
        expirationDateMask = ExpirationDateMask(requireContext())
        cvvValidator = CVVValidator(requireContext())
        cardHolderNameValidator = CardHolderNameValidator(requireContext())

        paymentDetailsValidator = PaymentDetailsValidator(
            context = requireContext(),
            cardNumberMask = cardNumberMask,
            expirationDateMask = expirationDateMask,
            cvvValidator = cvvValidator,
            cardHolderNameValidator = cardHolderNameValidator,
            errorHandler = { message -> showToast(message) }
        )
    }

    private fun setupUI() {
        setupRecyclerView()
        setupInputMasks()

        binding.backButton.setOnClickListener {
            dismiss()
            paymentViewModel.clearPaymentResult()
        }
        binding.confirmPaymentButton.setOnClickListener { onConfirmClicked() }
    }

    private fun setupRecyclerView() {
        cardAdapter = CardAdapter(emptyList(), this)
        binding.paymentCardsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = cardAdapter
        }
    }

    private fun setupInputMasks() {
        binding.cardNumberEditText.addTextChangedListener(cardNumberMask)
        binding.expirationDateEditText.addTextChangedListener(expirationDateMask)
    }

    private fun setupObservers() {
        paymentViewModel.paymentResult.observe(viewLifecycleOwner, paymentResultObserver)
        orderViewModel.currentOrder.observe(viewLifecycleOwner) {
            currentOrder = it
            checkOrderStatus()
        }
        observePaymentMethods()
        observeSelectedPaymentMethod()
    }

    private val paymentResultObserver = Observer<PaymentResult?> { result ->
        result?.let {
            if (it.success) {
                handleSuccessfulPayment()
            } else {
                handleFailedPayment(it.message)
            }
            isProcessing = false
            paymentViewModel.clearPaymentResult()
        }
    }

    private fun handleSuccessfulPayment() {
        currentOrder?.let { order ->
            if (order.status != OrderStatus.PAID) {
                orderViewModel.updateOrderStatus(order.orderId, OrderStatus.PAID)
                showSuccessPopup()
            }
        } ?: run {
            showToast(getString(R.string.order_data_not_found))
            dismiss()
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun showSuccessPopup() {
        dismiss()

        val successDialog = SuccessPaymentDialogFragment().apply {
            setOnDismissListener {
                findNavController().navigate(R.id.homeFragment)
            }
        }
        successDialog.show(parentFragmentManager, "SuccessPaymentDialog")
        paymentViewModel.clearPaymentResult()
    }

    private fun handleFailedPayment(errorMessage: String?) {
        showToast(errorMessage ?: getString(R.string.payment_failed))
        paymentViewModel.clearPaymentResult()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        paymentViewModel.clearPaymentResult()
    }

    private fun onConfirmClicked() {
        if (isProcessing) return

        when {
            !validateInputs() -> return
            paymentViewModel.selectedMethod.value == null -> {
                showToast(getString(R.string.select_card))
                return
            }
            currentOrder == null || currentOrder?.status == OrderStatus.PAID -> {
                showToast(getString(R.string.order_data_not_found))
                dismiss()
                return
            }
            else -> processPayment()
        }
    }

    private fun validateInputs(): Boolean {
        return paymentDetailsValidator.validateAllInputs(
            cardNumber = binding.cardNumberEditText.text.toString(),
            expirationDate = binding.expirationDateEditText.text.toString(),
            cvv = binding.cvvEditText.text.toString(),
            cardHolderName = binding.cardHolderNameEditText.text.toString()
        )
    }

    private fun processPayment() {
        isProcessing = true
        currentOrder?.let { order ->
            val request = PaymentRequest(
                orderId = order.orderId,
                cardToken = paymentViewModel.selectedMethod.value!!.cardToken,
                cvv = binding.cvvEditText.text.toString()
            )
            paymentViewModel.processPayment(request)
        } ?: run {
            showToast(getString(R.string.order_data_not_found))
            isProcessing = false
        }
    }

    private fun observePaymentMethods() {
        paymentViewModel.paymentMethods.observe(viewLifecycleOwner) { methods ->
            methods?.let {
                val cardList = it.map { method -> method.cardType }.toMutableList()
                cardList.add(CardType.OTHER)
                cardAdapter.updateData(cardList)

                methods.firstOrNull { m -> m.isSelected }?.cardType?.let { type ->
                    cardAdapter.updateSelectedCard(type)
                }
            }
        }
    }


    private fun observeSelectedPaymentMethod() {
        paymentViewModel.selectedMethod.observe(viewLifecycleOwner) { method ->
            method?.let {
                binding.cardNumberEditText.setText("•••• •••• •••• ${it.cardLastFour}")
                binding.cardHolderNameEditText.setText(it.cardHolderName)
                binding.expirationDateEditText.setText(it.expirationDate)
                cardAdapter.updateSelectedCard(it.cardType)
            }
        }
    }

    override fun onCardTypeSelected(cardType: CardType) {
        paymentViewModel.paymentMethods.value?.firstOrNull { it.cardType == cardType }?.let {
            paymentViewModel.selectMethod(it)
        }
    }

    override fun onAddCard() {
        AddCardFragment().show(parentFragmentManager, "AddCard")
    }

    private fun showToast(message: String) {
        StyleableToast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        paymentViewModel.clearPaymentResult()
        _binding = null
    }
}