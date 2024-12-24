package com.example.myfirstapp.DiffCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.myfirstapp.data.Models.Order

class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}