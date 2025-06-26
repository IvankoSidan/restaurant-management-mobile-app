package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Adapters.OrderAdapter
import com.example.myfirstapp.Interfaces.OrderManagementListener
import com.example.myfirstapp.Presentation.Fragments.LoginRegisterFragments.EntryFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.data.Enums.OrderStatus
import com.example.myfirstapp.data.Models.DishOrder
import com.example.myfirstapp.data.Models.Order
import com.example.myfirstapp.databinding.FragmentOrderHistoryBinding
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.launch


class OrderHistoryFragment : Fragment(), OrderManagementListener {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter

    private val guestViewModel: GuestViewModel by viewModel(ownerProducer = { requireActivity() })
    private val orderViewModel: OrderViewModel by viewModel(ownerProducer = { requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        loadOrders()
        observeOrders()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(this).apply {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = this
        }
    }

    private fun setupClickListeners() {
        binding.btnLogOut.setOnClickListener { navigateTo(EntryFragment()) }
        binding.backToImage.setOnClickListener { findNavController().popBackStack() }
    }

    private fun navigateTo(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_nav_host_fragment, fragment)
            .commit()
    }

    private fun navigateTo(destinationId: Int) {
        findNavController().navigate(destinationId)
    }

    private fun loadOrders() {
        guestViewModel.guest.observe(viewLifecycleOwner) { user ->
            user?.let { orderViewModel.loadOrders(it.idUser) }
        }
    }

    private fun observeOrders() {
        orderViewModel.orders.observe(viewLifecycleOwner) { orders ->
            orderAdapter.submitList(orders.sortedBy { it.orderId })
            orders.forEach { order ->
                orderViewModel.loadFormattedDishes(order.orderId)
            }
        }
        orderViewModel.formattedDishes.observe(viewLifecycleOwner) { map ->
            orderAdapter.updateFormattedDishes(map)
        }
    }

    override fun onDeleteOrder(order: Order) {
        if (order.status == OrderStatus.PAID || order.status == OrderStatus.ACCEPTED) {
            StyleableToast.makeText(requireContext(),
                getString(R.string.order_delete_error, order.orderId),
                R.style.errorToast).show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.delete_order_confirmation))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    orderViewModel.deleteOrder(order)
                }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        }
    }

    override fun onChangeOrder(order: Order) {
        if (order.status == OrderStatus.PAID || order.status == OrderStatus.ACCEPTED) {
            StyleableToast.makeText(requireContext(),
                getString(R.string.order_paid_cannot_change, order.orderId),
                R.style.errorToast).show()
        } else {
            handleOrderChange(order)
        }
    }

    override fun onRepeatOrder(order: Order) {
        if (order.status != OrderStatus.IN_PROGRESS) {
            StyleableToast.makeText(requireContext(),
                getString(R.string.order_paid_repeat, order.orderId),
                R.style.errorToast).show()
        } else {
            handleRepeatOrder(order)
        }
    }

    private fun handleOrderChange(order: Order) {
        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.clearLastOrder()
            val dishOrders = orderViewModel.fetchDishOrders(order.orderId)
            orderViewModel.setCurrentOrder(order)
            updateGuestCart(dishOrders)
            orderViewModel.loadOrders(order.userId)
        }
    }

    private suspend fun updateGuestCart(dishOrders: List<DishOrder>) {
        guestViewModel.clearCart()
        val ids = dishOrders.map { it.dishId }
        val dishes = orderViewModel.getDishesByIds(ids)
        dishes.forEach { dish ->
            val qty = dishOrders.first { it.dishId == dish.idDish }.quantity
            dish.quantity = qty
            guestViewModel.addToCart(dish, qty)
        }
        navigateTo(R.id.cartFragment)
    }

    private fun handleRepeatOrder(order: Order) {
        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.setCurrentOrder(order)
            navigateTo(R.id.paymentFragment)
        }
    }

    override fun getFormattedDishesForOrder(orderId: Long, callback: (String) -> Unit) {
        orderViewModel.formattedDishes.observe(viewLifecycleOwner) { map ->
            callback(map[orderId] ?: "")
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterOrders(newText)
                return true
            }
        })
    }

    private fun filterOrders(query: String?) {
        val filtered = orderViewModel.orders.value?.filter { order ->
            query.isNullOrEmpty() || order.matchesQuery(query)
        } ?: emptyList()
        orderAdapter.submitList(filtered)
    }

    private fun Order.matchesQuery(q: String): Boolean {
        val lq = q.lowercase()
        return orderId.toString().contains(lq)
                || orderDate.contains(lq)
                || status.getDisplayName().lowercase().contains(lq)
                || totalAmount.toString().contains(lq)
                || orderAdapter.getFormattedDishes(orderId)?.lowercase()?.contains(lq) == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
