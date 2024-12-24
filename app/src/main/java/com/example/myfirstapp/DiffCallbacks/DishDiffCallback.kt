package com.example.myfirstapp.DiffCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.myfirstapp.data.Models.Dish


class DishDiffCallback : DiffUtil.ItemCallback<Dish>() {
    override fun areItemsTheSame(oldItem: Dish, newItem: Dish): Boolean {
        return oldItem.idDish == newItem.idDish
    }

    override fun areContentsTheSame(oldItem: Dish, newItem: Dish): Boolean {
        return oldItem == newItem
    }
}
