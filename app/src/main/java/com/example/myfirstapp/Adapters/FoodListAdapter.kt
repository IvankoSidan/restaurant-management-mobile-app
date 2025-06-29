package com.example.myfirstapp.Adapters

import android.util.Log
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
import com.example.myfirstapp.Interfaces.DishSelectedListener
import com.example.myfirstapp.Objects.CurrencyManager
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Models.Dish

class FoodListAdapter(private val listener: DishSelectedListener) : ListAdapter<Dish, FoodListAdapter.FoodListViewHolder>(DishDiffCallback()) {

    class FoodListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleFoodItem: TextView = itemView.findViewById(R.id.titleFoodItem)
        private val priceFood: TextView = itemView.findViewById(R.id.priceFood)
        private val timeCookingFood: TextView = itemView.findViewById(R.id.timeCookingFood)
        private val starFoodImage: TextView = itemView.findViewById(R.id.starFoodImage)
        private val foodImageItem: ImageView = itemView.findViewById(R.id.foodImageItem)

        fun bind(dish: Dish) {
            titleFoodItem.text = dish.title
            priceFood.text = CurrencyManager.convertPrice(dish.price)
            timeCookingFood.text = String.format("%d %s", dish.timeValue, itemView.context.getString(R.string.minutes))
            starFoodImage.text = dish.star.toString()

            Glide.with(itemView.context)
                .load(dish.imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(foodImageItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_item, parent, false)
        return FoodListViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodListViewHolder, position: Int) {
        val dish = getItem(position)
        holder.bind(dish)
        holder.itemView.setOnClickListener { listener.loadSelectedDish(dish) }
    }
}
