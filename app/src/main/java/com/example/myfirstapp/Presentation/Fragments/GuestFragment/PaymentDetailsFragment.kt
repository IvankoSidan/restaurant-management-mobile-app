package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Adapters.CardAdapter
import com.example.myfirstapp.Interfaces.CardAdapterListener
import com.example.myfirstapp.data.Models.Card
import com.example.myfirstapp.data.Models.Payment
import com.example.myfirstapp.R
import com.example.myfirstapp.ValidationClasses.CVVValidator
import com.example.myfirstapp.ValidationClasses.CardHolderNameValidator
import com.example.myfirstapp.ValidationClasses.CardNumberMask
import com.example.myfirstapp.ValidationClasses.ExpirationDateMask
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.ViewModels.PaymentViewModel
import com.example.myfirstapp.data.Enums.CardType
import com.example.myfirstapp.data.Enums.OrderStatus
import com.example.myfirstapp.data.Enums.PaymentStatus
import com.example.myfirstapp.databinding.FragmentPaymentDetailsBinding
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

    private val paymentViewModel: PaymentViewModel by viewModel(ownerProducer  = { requireActivity() })
    private val orderViewModel: OrderViewModel by viewModel(ownerProducer  = { requireActivity() })
    private val guestViewModel: GuestViewModel by viewModel(ownerProducer  = { requireActivity() })

    private lateinit var cardAdapter: CardAdapter
    private var selectedCardType: CardType? = null

    private lateinit var cardHolderNameValidator: CardHolderNameValidator
    private lateinit var cvvValidator: CVVValidator
    private lateinit var expirationDateMask: ExpirationDateMask
    private lateinit var cardNumberMask: CardNumberMask


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        binding.backButton.setOnClickListener { dismiss() }
        cardHolderNameValidator = CardHolderNameValidator(requireContext())
        cvvValidator = CVVValidator(requireContext())
        expirationDateMask = ExpirationDateMask(requireContext())
        cardNumberMask = CardNumberMask(requireContext())
        setupInputMasks()
    }

    private fun setupUI() {
        setupRecyclerView()

        binding.confirmPaymentButton.setOnClickListener { confirmPayment() }
        paymentViewModel.selectedMethod.observe(viewLifecycleOwner) { method ->
            selectedCardType = method?.cardType
            if (::cardAdapter.isInitialized) {
                selectedCardType?.let {
                    cardAdapter.updateSelectedCard(it)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        if (!::cardAdapter.isInitialized) {
            cardAdapter = CardAdapter(emptyList(), this)
            binding.paymentCardsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = cardAdapter
            }
        }

        paymentViewModel.paymentMethods.observe(viewLifecycleOwner) { methods ->
            val distinctTypes = methods.map { it.cardType }.distinct().toMutableList()
            if (!distinctTypes.contains(CardType.OTHER)) {
                distinctTypes.add(CardType.OTHER)
            }
            cardAdapter.updateData(distinctTypes)
        }
    }

    private fun confirmPayment() {
        val cardNumber = binding.cardNumberEditText.text.toString().replace(" ", "")
        val expirationDate = binding.expirationDateEditText.text.toString()
        val cvv = binding.cvvEditText.text.toString()
        val cardHolderName = binding.cardHolderNameEditText.text.toString()

        if (cardNumber.isBlank() || expirationDate.isBlank() || cvv.isBlank() || cardHolderName.isBlank()) {
            showToast(getString(R.string.fill_card_details), R.style.errorToast)
            return
        }

        if (!cardNumberMask.validate(cardNumber)) {
            showToast(cardNumberMask.getErrorMessage(), R.style.errorToast)
            return
        }
        if (!expirationDateMask.validate(expirationDate)) {
            showToast(expirationDateMask.getErrorMessage(), R.style.errorToast)
            return
        }
        if (!cvvValidator.validate(cvv)) {
            showToast(cvvValidator.getErrorMessage(), R.style.errorToast)
            return
        }
        if (!cardHolderNameValidator.validate(cardHolderName)) {
            showToast(cardHolderNameValidator.getErrorMessage(), R.style.errorToast)
            return
        }

        val currentOrder = orderViewModel.currentOrder.value ?: run {
            showToast(getString(R.string.order_data_not_found), R.style.errorToast)
            return
        }

        val card = Card(
            id = 0L,
            userId = currentOrder.userId,
            cardNumber = cardNumber,
            cardHolderName = cardHolderName,
            expirationDate = expirationDate,
            cvv = cvv,
            isDefault = false,
            cardType = selectedCardType ?: CardType.OTHER
        )

        if (paymentViewModel.saveCardDetails.value == true) {
            paymentViewModel.createCard(card)
        }

        val paymentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())


        val payment = Payment(
            idPayment = 0,
            orderId = currentOrder.orderId,
            paymentAmount = currentOrder.totalAmount,
            paymentDate = paymentDate,
            paymentStatus = PaymentStatus.SUCCESS
        )
        Log.d("PaymentDebug", "Total amount before conversion: ${payment.paymentAmount}")
        paymentViewModel.createPayment(payment)

        orderViewModel.updateOrderStatus(currentOrder.orderId, OrderStatus.PAID)
        showToast(getString(R.string.payment_confirmation_success), R.style.successToast)
        orderViewModel.clearLastOrder()
        guestViewModel.clearCart()
        showExitPopup()
    }

    private fun showExitPopup() {
        val dialog = Dialog(requireContext(), R.style.CustomDialogStyle)
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.po_up_payment, null)
        dialog.setContentView(popupView)

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            attributes.windowAnimations = R.style.DialogAnimation
        }

        popupView.findViewById<Button>(R.id.goBackButton).setOnClickListener {
            dismiss()
            dialog.dismiss()
            requireActivity().supportFragmentManager.popBackStack(
                "PaymentFragmentTag",
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            findNavController().navigate(R.id.homeFragment)
        }
        dialog.show()
    }

    private fun setupInputMasks() {
        binding.cardNumberEditText.addTextChangedListener(cardNumberMask)
        binding.expirationDateEditText.addTextChangedListener(expirationDateMask)
        binding.cvvEditText.filters = arrayOf(InputFilter.LengthFilter(4))
    }

    override fun onCardTypeSelected(cardType: CardType) {
        selectedCardType = cardType
    }

    override fun onAddCard() {
        val addCartFragment = AddCardFragment()
        addCartFragment.show(parentFragmentManager, "AddCartFragmentTag")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showToast(message: String, style: Int) {
        StyleableToast.makeText(requireContext(), message, style).show()
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
