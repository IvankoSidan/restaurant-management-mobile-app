package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.myfirstapp.R
import com.example.myfirstapp.ValidationClasses.CVVValidator
import com.example.myfirstapp.ValidationClasses.CardHolderNameValidator
import com.example.myfirstapp.ValidationClasses.CardNumberMask
import com.example.myfirstapp.ValidationClasses.ExpirationDateMask
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.ViewModels.PaymentViewModel
import com.example.myfirstapp.data.Enums.CardType
import com.example.myfirstapp.data.Models.PaymentMethod
import com.example.myfirstapp.databinding.FragmentAddCartBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddCardFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddCartBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by viewModel(ownerProducer  = { requireActivity() })
    private val paymentViewModel: PaymentViewModel by viewModel(ownerProducer  = { requireActivity() })

    private lateinit var cardHolderNameValidator: CardHolderNameValidator
    private lateinit var cvvValidator: CVVValidator
    private lateinit var expirationDateMask: ExpirationDateMask
    private lateinit var cardNumberMask: CardNumberMask

    private var rawCardNumber: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardHolderNameValidator = CardHolderNameValidator(requireContext())
        cvvValidator = CVVValidator(requireContext())
        expirationDateMask = ExpirationDateMask(requireContext())
        cardNumberMask = CardNumberMask(requireContext())

        binding.etCardNumber.addTextChangedListener(cardNumberMask)
        binding.etCardDate.addTextChangedListener(expirationDateMask)


        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                rawCardNumber = s.toString().filter { it.isDigit() }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.backButton.setOnClickListener {
            dismiss()
        }
        textViewChanges()

        binding.confirmPaymentButton.setOnClickListener {
            val cardNumberInput = rawCardNumber
            val cardHolder = binding.etCardHolder.text.toString()
            val expiration = binding.etCardDate.text.toString()
            val cvv = binding.etCardCVV.text.toString()

            if (!cardNumberMask.validate(cardNumberInput)) {
                showToast(cardNumberMask.getErrorMessage(), R.style.errorToast)
                return@setOnClickListener
            }
            if (!expirationDateMask.validate(expiration)) {
                showToast(expirationDateMask.getErrorMessage(), R.style.errorToast)
                return@setOnClickListener
            }
            if (!cvvValidator.validate(cvv)) {
                showToast(cvvValidator.getErrorMessage(), R.style.errorToast)
                return@setOnClickListener
            }
            if (!cardHolderNameValidator.validate(cardHolder)) {
                showToast(cardHolderNameValidator.getErrorMessage(), R.style.errorToast)
                return@setOnClickListener
            }

            val cardType = getCardType(cardNumberInput)
            orderViewModel.currentOrder.observe(viewLifecycleOwner) { order ->
                order?.let {
                    val newPaymentMethod = PaymentMethod(
                        id = 0L,
                        userId = order.userId,
                        cardNumber = formatCardNumber(cardNumberInput),
                        cardHolderName = cardHolder,
                        expirationDate = expiration,
                        isSelected = false,
                        cardType = cardType
                    )
                    paymentViewModel.addPaymentMethod(newPaymentMethod)
                    paymentViewModel.selectMethod(newPaymentMethod)
                    dismiss()
                }
            }

        }
    }

    private fun getCardType(cardNumber: String): CardType {
        return when {
            cardNumber.startsWith("4") -> CardType.VISA
            cardNumber.startsWith("5") -> CardType.MASTERCARD
            cardNumber.startsWith("3") -> CardType.AMEX
            cardNumber.startsWith("6") -> CardType.PSB
            cardNumber.startsWith("7") -> CardType.SBER
            cardNumber.startsWith("8") -> CardType.TINKOFF
            cardNumber.startsWith("2") -> CardType.UNIONPAY
            cardNumber.startsWith("1") -> CardType.DISCOVER
            cardNumber.startsWith("35") -> CardType.JCB
            else -> CardType.OTHER
        }
    }

    private fun textViewChanges() {
        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val formattedText = formatCardNumber(s.toString().filter { it.isDigit() })
                binding.cardNumber.text = formattedText
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etCardHolder.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.cardHolderNameTitle.text = s.toString().split(" ")
                    .joinToString(" ") { it.capitalize() }
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

    private fun formatCardNumber(cardNumber: String): String {
        val cleanedCardNumber = cardNumber
        val maskedPart = cleanedCardNumber.dropLast(4).map { '*' }.joinToString("")
        val lastFourDigits = cleanedCardNumber.takeLast(4)
        val maskedFormatted = maskedPart.chunked(4).joinToString(" ")
        val lastFourFormatted = lastFourDigits.chunked(4).joinToString(" ")
        return if (maskedFormatted.isNotEmpty()) "$maskedFormatted $lastFourFormatted" else lastFourFormatted
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

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.background = ColorDrawable(Color.TRANSPARENT)
                val params = it.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                it.layoutParams = params
            }
        }
        return dialog
    }
}
