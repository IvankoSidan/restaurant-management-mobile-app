package com.example.myfirstapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myfirstapp.DiffCallbacks.DishDiffCallback
import com.example.myfirstapp.Interfaces.ManagementCartListener
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Models.Dish

class CartAdapter(private val listener: ManagementCartListener) : ListAdapter<Dish, CartAdapter.CartViewHolder>(DishDiffCallback()) {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val feeEachItem: TextView = itemView.findViewById(R.id.feeEachItem)
        private val totalEachItem: TextView = itemView.findViewById(R.id.totalEachItem)
        private val count: TextView = itemView.findViewById(R.id.count)
        private val imageMeal: ImageView = itemView.findViewById(R.id.imageMeal)

        fun bind(dish: Dish) {
            textTitle.text = dish.title
            feeEachItem.text = String.format("%.2f $", dish.price)
            totalEachItem.text = String.format("%.2f $", dish.price * dish.quantity)
            count.text = dish.quantity.toString()

            Glide.with(itemView.context)
                .load(dish.imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageMeal)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val dish = getItem(position)
        holder.bind(dish)

        holder.itemView.findViewById<TextView>(R.id.plusBtn).setOnClickListener {
            listener.plusNumberItem(position)
            notifyDataSetChanged()
        }

        holder.itemView.findViewById<TextView>(R.id.minusBtn).setOnClickListener {
            listener.minusNumberItem(position)
            notifyDataSetChanged()
        }
    }
}
