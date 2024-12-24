package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Adapters.CartAdapter
import com.example.myfirstapp.Interfaces.CartManagerListener
import com.example.myfirstapp.Interfaces.ManagementCartListener
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.data.Enums.OrderStatus
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.DishOrder
import com.example.myfirstapp.data.Models.Order
import com.example.myfirstapp.databinding.FragmentCartBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartFragment : Fragment(), ManagementCartListener, CartManagerListener {
    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter

    private val guestViewModel: GuestViewModel by lazy {
        ViewModelProvider(requireActivity())[GuestViewModel::class.java]
    }

    private val orderViewModel: OrderViewModel by lazy {
        ViewModelProvider(requireActivity())[OrderViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartAdapter = CartAdapter(this)
        orderViewModel.setCartListener(this)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.cardView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = cartAdapter
        }
    }

    private fun observeViewModel() {
        guestViewModel.selectedDishes.observe(viewLifecycleOwner, { dishes ->
            cartAdapter.submitList(dishes)
            updateTotalFee()
        })
    }

    private fun updateTotalFee() {
        val totalFee = guestViewModel.getTotalFee()
        binding.textTotalPrice.text = String.format("%.2f $", totalFee)
    }


    private fun setupClickListeners() {
        binding.backToImage.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.placeOrderButton.setOnClickListener {
            val currentUser = guestViewModel.guest.value
            currentUser?.let { user ->
                val selectedDishes = guestViewModel.selectedDishes.value ?: emptyList()
                if (selectedDishes.isEmpty()) {
                    Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val order = Order(
                    userId = user.id,
                    bookingId = null,
                    totalAmount = guestViewModel.getTotalFee(),
                    status = OrderStatus.IN_PROGRESS,
                    orderDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                )

                val dishOrders = selectedDishes.map { dish ->
                    DishOrder(
                        orderId = 0,
                        dishId = dish.idDish,
                        quantity = dish.quantity
                    )
                }

                orderViewModel.placeOrder(order, dishOrders)
                guestViewModel.clearCart()
                findNavController().navigate(R.id.orderHistoryFragment)
            }
        }

        binding.changeOrderBtn.setOnClickListener {
            val currentOrder = orderViewModel._currentOrder.value
            currentOrder?.let { order ->
                val selectedDishes = guestViewModel.selectedDishes.value ?: emptyList()
                if (selectedDishes.isEmpty()) {
                    Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    val dishOrders = selectedDishes.map { dish ->
                        DishOrder(
                            orderId = order.orderId,
                            dishId = dish.idDish,
                            quantity = dish.quantity
                        )
                    }
                    guestViewModel.clearCart()
                    orderViewModel.deleteAllDishesByOrderId(order.orderId)
                    orderViewModel.updateFullOrder(order, dishOrders)
                    findNavController().navigate(R.id.orderHistoryFragment)
                }
            }
        }


        binding.resumeImageBtn.setOnClickListener {
            orderViewModel.resumeOrder()
        }

        binding.openPaymentMethod.setOnClickListener {
            findNavController().navigate(R.id.paymentFragment)
        }

        binding.cancelImageBtn.setOnClickListener {
            guestViewModel.clearCart()
            findNavController().navigate(R.id.homeFragment)
        }
    }

    override fun plusNumberItem(position: Int) {
        guestViewModel.updateItemQuantity(position, increment = true)
    }

    override fun minusNumberItem(position: Int) {
        guestViewModel.updateItemQuantity(position, increment = false)
    }

    override fun addToCart(dish: Dish, quantity: Int) {
        guestViewModel.addToCart(dish, quantity)
    }
}

