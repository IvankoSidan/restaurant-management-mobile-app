package com.example.myfirstapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.DiffCallbacks.OrderDiffCallback
import com.example.myfirstapp.Interfaces.OrderManagementListener
import com.example.myfirstapp.Objects.CurrencyManager
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Models.Order

class OrderAdapter(private val orderManagementListener: OrderManagementListener)
    : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    private val formattedDishesMap = mutableMapOf<Long, String>()

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textOrderNumber: TextView = itemView.findViewById(R.id.textOrderNumber)
        private val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        private val statusOrder: TextView = itemView.findViewById(R.id.statusOrder)
        private val totalOrder: TextView = itemView.findViewById(R.id.totalOrder)
        private val dishName: TextView = itemView.findViewById(R.id.dishName)

        fun bind(order: Order, formattedDishes: String) {
            textOrderNumber.text = itemView.context.getString(R.string.order_number, order.orderId)
            statusOrder.text = order.status.getDisplayName()
            orderDate.text = order.orderDate
            totalOrder.text = CurrencyManager.convertPrice(order.totalAmount)
            dishName.text = formattedDishes
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        val formattedDishes = formattedDishesMap[order.orderId] ?: ""
        holder.bind(order, formattedDishes)

        holder.itemView.findViewById<ImageView>(R.id.deleteImageBtn).setOnClickListener {
            orderManagementListener.onDeleteOrder(order)
        }

        holder.itemView.findViewById<ImageView>(R.id.payImageBtn).setOnClickListener {
            orderManagementListener.onRepeatOrder(order)
        }

        holder.itemView.findViewById<ImageView>(R.id.changeImageBtn).setOnClickListener {
            orderManagementListener.onChangeOrder(order)
        }
    }

    fun getFormattedDishes(orderId: Long): String? {
        return formattedDishesMap[orderId]
    }

    fun updateFormattedDishes(formattedDishes: Map<Long, String>) {
        formattedDishesMap.clear()
        formattedDishesMap.putAll(formattedDishes)
        notifyDataSetChanged()
    }
}
