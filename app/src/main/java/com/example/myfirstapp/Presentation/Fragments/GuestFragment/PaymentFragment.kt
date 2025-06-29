package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Adapters.PaymentMethodAdapter
import com.example.myfirstapp.Interfaces.OnPaymentListener
import com.example.myfirstapp.Objects.CurrencyManager
import com.example.myfirstapp.data.Models.PaymentMethod
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.ViewModels.PaymentViewModel
import com.example.myfirstapp.databinding.FragmentPaymentBinding
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class PaymentFragment : Fragment(), OnPaymentListener {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val paymentViewModel: PaymentViewModel by viewModel(ownerProducer = { requireActivity() })

    private val orderViewModel: OrderViewModel by viewModel(ownerProducer = { requireActivity() })

    private lateinit var paymentMethodAdapter: PaymentMethodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
        setupRecyclerView()
        log()
    }

    private fun log() {
        val order = orderViewModel.currentOrder.value
        Log.d("ORDER_INFO","${order}")
    }

    private fun setupUI() {
        orderViewModel.currentOrder.observe(viewLifecycleOwner) { order ->
            order?.let {
                paymentViewModel.getPaymentMethods(order.userId)
                binding.apply {
                    orderDescription.text = "#${it.orderId}"
                    dateTimeOrdering.text = it.orderDate
                    totalAmount.text = CurrencyManager.convertPrice(it.totalAmount)
                    priceText.text = CurrencyManager.convertPrice(it.totalAmount)
                }
            }
        }
    }

    private fun handlePayment() {
        val paymentMethods = paymentViewModel.paymentMethods.value
        if (paymentMethods.isNullOrEmpty()) {
            AddCardFragment().show(parentFragmentManager, "AddCardFragmentTag")
            return
        }

        val selectedPaymentMethod = paymentViewModel.selectedMethod.value
        if (selectedPaymentMethod == null) {
            paymentViewModel.selectMethod(paymentMethods[0])
        }

        onPaymentMethodClick(paymentViewModel.selectedMethod.value!!)
        paymentViewModel.setSaveCardDetails(binding.saveCardDetails.isChecked)
        PaymentDetailsFragment().show(childFragmentManager, "PaymentDetailsFragmentTag")
    }

    private fun setupRecyclerView() {
        paymentMethodAdapter = PaymentMethodAdapter(mutableListOf(), this)
        binding.recPaymentMethod.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentMethodAdapter
        }
        paymentViewModel.paymentMethods.observe(viewLifecycleOwner) { methods ->
            methods?.let {
                paymentMethodAdapter.updateData(it)
            } ?: run {
                StyleableToast.makeText(
                    requireContext(),
                    getString(R.string.payment_method_null),
                    R.style.errorToast
                ).show()
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            layoutPayNow.setOnClickListener {
                handlePayment()
            }
            backToImage.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onPaymentMethodClick(paymentMethod: PaymentMethod) {
        try {
            paymentViewModel.selectMethod(paymentMethod)
        } catch (e: Exception) {
            StyleableToast.makeText(requireContext(), e.message ?: getString(R.string.error), R.style.errorToast).show()
        }
    }

    override fun onPaymentMethodDelete(paymentMethod: PaymentMethod) {
        try {
            paymentViewModel.deletePaymentMethod(paymentMethod)
        } catch (e: Exception) {
            StyleableToast.makeText(requireContext(), e.message ?: getString(R.string.error), R.style.errorToast).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
