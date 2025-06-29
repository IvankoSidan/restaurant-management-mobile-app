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
import com.example.myfirstapp.Interfaces.FavoriteSelectedListener
import com.example.myfirstapp.Objects.CurrencyManager
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Models.Dish

class FavoriteAdapter(private val listener: FavoriteSelectedListener) : ListAdapter<Dish, FavoriteAdapter.FavoriteViewHolder>(DishDiffCallback()) {

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImageItem: ImageView = itemView.findViewById(R.id.foodImageItem)
        private val titleFoodItem: TextView = itemView.findViewById(R.id.titleFoodItem)
        private val priceFood: TextView = itemView.findViewById(R.id.priceFood)
        private val starFoodImage: TextView = itemView.findViewById(R.id.starFoodImage)
        val featuresImage: ImageView = itemView.findViewById(R.id.featuresImage)

        fun bind(dish: Dish, isFavorite: Boolean) {
            titleFoodItem.text = dish.title
            priceFood.text = CurrencyManager.convertPrice(dish.price)
            starFoodImage.text = "${dish.star}"

            Glide.with(itemView.context)
                .load(dish.imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(foodImageItem)

            featuresImage.setImageResource(if (isFavorite) R.drawable.favorite_icon_item else R.drawable.favorite_icon_border)
        }

        fun animateHeartRemoval() {
            featuresImage.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .withEndAction {
                    featuresImage.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forward_list_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val dish = getItem(position)
        holder.bind(dish, isFavorite = true)

        holder.itemView.setOnClickListener {
            listener.loadSelectedDish(dish)
        }

        holder.featuresImage.setOnClickListener {
            holder.animateHeartRemoval()
            listener.onRemoved(dish)
            removeDish(dish)
        }
    }

    fun removeDish(dish: Dish) {
        val currentMutableList = currentList.toMutableList()
        val index = currentMutableList.indexOf(dish)
        if (index != -1) {
            currentMutableList.removeAt(index)
            submitList(currentMutableList)
        }
    }
}
