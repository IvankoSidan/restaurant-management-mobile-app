package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.myfirstapp.R
import com.example.myfirstapp.Utils.PaymentDetailsValidator
import com.example.myfirstapp.ValidationClasses.CVVValidator
import com.example.myfirstapp.ValidationClasses.CardHolderNameValidator
import com.example.myfirstapp.ValidationClasses.CardNumberMask
import com.example.myfirstapp.ValidationClasses.ExpirationDateMask
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.ViewModels.PaymentViewModel
import com.example.myfirstapp.data.DTO.CardRequestDto
import com.example.myfirstapp.data.Enums.CardType
import com.example.myfirstapp.data.Models.Card
import com.example.myfirstapp.data.Models.PaymentMethod
import com.example.myfirstapp.databinding.FragmentAddCartBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Response
import java.util.Locale

class AddCardFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentAddCartBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by viewModel(ownerProducer = { requireActivity() })
    private val paymentViewModel: PaymentViewModel by viewModel(ownerProducer = { requireActivity() })

    private lateinit var paymentDetailsValidator: PaymentDetailsValidator
    private lateinit var expirationDateMask: ExpirationDateMask
    private lateinit var cardNumberMask: CardNumberMask

    private var selectedCardType: CardType = CardType.OTHER

    private val createdCardObserver = Observer<Response<Card>?> { response ->
        if (response?.isSuccessful == true && response.body() != null) {
            val card = response.body()!!
            val pm = PaymentMethod(
                id = 0L,
                userId = card.userId,
                cardToken = card.cardToken,
                cardLastFour = cardNumberMask.getLastFourDigits(),
                cardHolderName = card.cardHolderName,
                expirationDate = card.expirationDate,
                isDefault = false,
                cardType = card.cardType
            )
            paymentViewModel.addPaymentMethod(pm)
            paymentViewModel.selectMethod(pm)
            dismiss()
        } else if (response?.isSuccessful == false) {
            showToast(getString(R.string.error_saving_card), R.style.errorToast)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)

        initValidators()
        setupInputMasks()
        setupTextWatchers()

        binding.backButton.setOnClickListener { dismiss() }
        binding.confirmPaymentButton.setOnClickListener { onConfirmClicked() }

        paymentViewModel.resetCreatedCardState()
        paymentViewModel.createdCard.observe(viewLifecycleOwner, createdCardObserver)
    }

    private fun initValidators() {
        cardNumberMask = CardNumberMask(requireContext())
        expirationDateMask = ExpirationDateMask(requireContext())

        paymentDetailsValidator = PaymentDetailsValidator(
            context = requireContext(),
            cardNumberMask = cardNumberMask,
            expirationDateMask = expirationDateMask,
            cvvValidator = CVVValidator(requireContext()),
            cardHolderNameValidator = CardHolderNameValidator(requireContext()),
            errorHandler = { message ->
                showToast(message, R.style.errorToast)
            }
        )
    }

    private fun setupInputMasks() {
        binding.etCardNumber.addTextChangedListener(cardNumberMask)
        binding.etCardDate.addTextChangedListener(expirationDateMask)
        binding.etCardCVV.filters = arrayOf(InputFilter.LengthFilter(4))
    }

    private fun setupTextWatchers() {
        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectedCardType = getCardType(cardNumberMask.getCleanNumber())

                val clean = cardNumberMask.getCleanNumber()
                binding.cardNumber.text = if (clean.isNotEmpty()) {
                    formatCardNumber(clean)
                } else {
                    ""
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etCardHolder.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.cardHolderNameTitle.text =
                    s.toString()
                        .split(" ")
                        .joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercase() } }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etCardDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.expirationDate.text = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etCardCVV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.cvv.text = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun onConfirmClicked() {
        val enteredMask = binding.etCardNumber.text.toString()
        val exp = binding.etCardDate.text.toString()
        val cvv = binding.etCardCVV.text.toString()
        val holder = binding.etCardHolder.text.toString()

        if (paymentDetailsValidator.validateAllInputs(enteredMask, exp, cvv, holder)) {
            val order = orderViewModel.currentOrder.value
            if (order != null) {
                val cleanNumber = cardNumberMask.getCleanNumber()
                val dto = CardRequestDto(
                    userId = order.userId,
                    cardNumber = cleanNumber,
                    cvv = cvv,
                    expirationDate = exp,
                    cardHolderName = holder,
                    isDefault = false,
                    cardType = selectedCardType
                )
                paymentViewModel.createCard(dto)
            } else {
                showToast(getString(R.string.order_data_not_found), R.style.errorToast)
            }
        }
    }


    private fun formatCardNumber(clean: String): String {
        val padded = clean.padStart(16, '•')
        return padded
            .chunked(4)
            .joinToString(" ")
    }

    private fun getCardType(cleanNumber: String): CardType = when {
        cleanNumber.startsWith("4")  -> CardType.VISA
        cleanNumber.startsWith("5")  -> CardType.MASTERCARD
        cleanNumber.startsWith("3")  -> CardType.AMEX
        cleanNumber.startsWith("6")  -> CardType.PSB
        cleanNumber.startsWith("7")  -> CardType.SBER
        cleanNumber.startsWith("8")  -> CardType.TINKOFF
        cleanNumber.startsWith("2")  -> CardType.UNIONPAY
        cleanNumber.startsWith("1")  -> CardType.DISCOVER
        cleanNumber.startsWith("35") -> CardType.JCB
        else                          -> CardType.OTHER
    }

    private fun showToast(message: String, style: Int) {
        StyleableToast.makeText(requireContext(), message, style).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { d ->
            val bottom = (d as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottom?.apply {
                background = ColorDrawable(Color.TRANSPARENT)
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                requestLayout()
            }
        }
        return dialog
    }
}



