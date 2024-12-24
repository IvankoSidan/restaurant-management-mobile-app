package com.example.myfirstapp.Presentation.Fragments.GuestFragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.Adapters.OrderAdapter
import com.example.myfirstapp.Interfaces.OrderManagementListener
import com.example.myfirstapp.Presentation.Fragments.LoginRegisterFragments.EntryFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.ViewModels.GuestViewModel
import com.example.myfirstapp.ViewModels.OrderViewModel
import com.example.myfirstapp.data.Models.DishOrder
import com.example.myfirstapp.data.Models.Order
import com.example.myfirstapp.databinding.FragmentOrderHistoryBinding
import kotlinx.coroutines.launch


class OrderHistoryFragment : Fragment(), OrderManagementListener {

    private lateinit var binding: FragmentOrderHistoryBinding
    private lateinit var orderAdapter: OrderAdapter

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
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
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
        binding.backToImage.setOnClickListener { navigateTo(R.id.profileFragment) }
    }

    private fun navigateTo(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun navigateTo(destinationId: Int) {
        findNavController().navigate(destinationId)
    }

    private fun observeOrders() {
        orderViewModel._orders.observe(viewLifecycleOwner) { orders ->
            orderAdapter.submitList(orders)
        }

        orderViewModel._formattedDishes.observe(viewLifecycleOwner) { formattedDishesMap ->
            orderAdapter.updateFormattedDishes(formattedDishesMap)
        }
    }

    private fun loadOrders() {
        guestViewModel.guest.observe(viewLifecycleOwner) { user ->
            user?.let { orderViewModel.loadOrders(it.id) }
        }
    }

    override fun onDeleteOrder(order: Order) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Order")
            .setMessage("Are you sure you want to delete this order?")
            .setPositiveButton("Yes") { _, _ -> orderViewModel.deleteOrder(order) }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onChangeOrder(order: Order) {
        handleOrderChange(order)
    }

    override fun onRepeatOrder(order: Order) {
        handleOrderChange(order)
    }

    private fun handleOrderChange(order: Order) {
        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.getDishesByOrderId(order.orderId)
            orderViewModel.setCurrentOrder(order)

            orderViewModel._dishOrders.observe(viewLifecycleOwner) { dishOrders ->
                handleDishOrders(dishOrders)
            }
        }
    }

    private fun handleDishOrders(dishOrders: List<DishOrder>) {
        viewLifecycleOwner.lifecycleScope.launch {
            val dishes = dishOrders.map { dishOrder ->
                orderViewModel.getDishById(dishOrder.dishId).apply {
                    quantity = dishOrder.quantity
                }
            }

            guestViewModel.clearCart()
            dishes.forEach { dish -> guestViewModel.addToCart(dish, dish.quantity) }
            navigateTo(R.id.cartFragment)
        }
    }

    override fun getFormattedDishesForOrder(orderId: Long, callback: (String) -> Unit) {
        orderViewModel._formattedDishes.observe(viewLifecycleOwner) { formattedDishesMap ->
            callback(formattedDishesMap[orderId] ?: "")
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterOrders(newText)
                return true
            }
        })
    }

    private fun filterOrders(query: String?) {
        val filteredOrders = orderViewModel._orders.value?.filter { order ->
            query.isNullOrEmpty() || order.matchesQuery(query)
        } ?: emptyList()

        orderAdapter.submitList(filteredOrders)
    }

    private fun Order.matchesQuery(query: String): Boolean {
        val lowerCaseQuery = query.lowercase()
        return orderId.toString().contains(lowerCaseQuery) ||
                orderDate.contains(lowerCaseQuery) ||
                status.getDisplayName().lowercase().contains(lowerCaseQuery) ||
                totalAmount.toString().contains(lowerCaseQuery) ||
                orderAdapter.getFormattedDishes(orderId)?.lowercase()?.contains(lowerCaseQuery) == true
    }
}
