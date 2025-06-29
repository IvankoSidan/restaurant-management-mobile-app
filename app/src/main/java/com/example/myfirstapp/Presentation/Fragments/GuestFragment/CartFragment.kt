package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Adapters.CartAdapter
import com.example.myfirstapp.Interfaces.CartManagerListener
import com.example.myfirstapp.Interfaces.ManagementCartListener
import com.example.myfirstapp.Objects.CurrencyManager
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.ViewModels.ReservationTableViewModel
import com.example.myfirstapp.data.Enums.OrderStatus
import com.example.myfirstapp.data.Models.Dish
import com.example.myfirstapp.data.Models.DishOrder
import com.example.myfirstapp.data.Models.Order
import com.example.myfirstapp.databinding.FragmentCartBinding
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartFragment : Fragment(), ManagementCartListener, CartManagerListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter

    private val guestViewModel: GuestViewModel by viewModel(ownerProducer = { requireActivity() })
    private val orderViewModel: OrderViewModel by viewModel(ownerProducer = { requireActivity() })
    private val reserveViewModel: ReservationTableViewModel by viewModel(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка RecyclerView
        cartAdapter = CartAdapter(this)
        binding.cardView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }

        // Подписка на изменения корзины
        guestViewModel.selectedDishes.observe(viewLifecycleOwner) { dishes ->
            cartAdapter.submitList(dishes.toList())
            updateTotalFee()
        }

        // Кнопки
        binding.placeOrderButton.setOnClickListener { submitOrder(navigateToPayment = false) }
        binding.openPaymentMethod.setOnClickListener { submitOrder(navigateToPayment = true) }
        binding.cancelImageBtn.setOnClickListener {
            guestViewModel.clearCart()
            orderViewModel.clearLastOrder()
            findNavController().navigate(R.id.homeFragment)
        }

        binding.backToImage.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun updateTotalFee() {
        val total = guestViewModel.getTotalFee()
        binding.textTotalPrice.text = CurrencyManager.convertPrice(total)
    }

    private fun submitOrder(navigateToPayment: Boolean) {
        val user = guestViewModel.guest.value
        val dishes = guestViewModel.selectedDishes.value.orEmpty()

        // Проверки
        if (user == null) {
            StyleableToast.makeText(requireContext(), getString(R.string.order_empty), R.style.errorToast).show()
            return
        }
        if (dishes.isEmpty()) {
            StyleableToast.makeText(requireContext(), getString(R.string.cart_empty), R.style.errorToast).show()
            return
        }

        // Получаем последний bookingId
        val userBookings = reserveViewModel.bookings.value.orEmpty()
            .filter { it.userId == user.idUser }
        val bookingId = userBookings.lastOrNull()?.idBooking

        viewLifecycleOwner.lifecycleScope.launch {
            val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val existingOrder = orderViewModel.currentOrder.value

            // Формируем объект Order
            val order = existingOrder?.copy(
                totalAmount = guestViewModel.getTotalFee(),
                orderDate = now
            ) ?: Order(
                userId = user.idUser,
                bookingId = bookingId,
                totalAmount = guestViewModel.getTotalFee(),
                status = OrderStatus.IN_PROGRESS,
                orderDate = now
            )

            // Формируем список DishOrder
            val dishOrders = dishes.map { dish ->
                DishOrder(
                    orderId = order.orderId,
                    dishId = dish.idDish,
                    quantity = dish.quantity
                )
            }

            // Создаём или обновляем заказ
            if (existingOrder == null) {
                // Новый заказ
                val created = orderViewModel.placeOrderSuspend(order, dishOrders)
                orderViewModel.setCurrentOrder(created)
            } else {
                // Обновление существующего
                orderViewModel.updateFullOrder(order, dishOrders)
            }

            // Очищаем корзину
            guestViewModel.clearCart()

            // Навигация
            if (navigateToPayment) {
                findNavController().navigate(R.id.paymentFragment)
            } else {
                findNavController().navigate(R.id.orderHistoryFragment)
            }
        }
    }

    // Реализация интерфейсов управления корзиной
    override fun plusNumberItem(position: Int) {
        guestViewModel.updateItemQuantity(position, increment = true)
    }
    override fun minusNumberItem(position: Int) {
        guestViewModel.updateItemQuantity(position, increment = false)
    }
    override fun addToCart(dish: Dish, quantity: Int) {
        guestViewModel.addToCart(dish, quantity)
    }

    override fun onResume() {
        super.onResume()
        guestViewModel.selectedDishes.value?.let { cartAdapter.submitList(it.toList()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
