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
import com.example.myfirstapp.Interfaces.DishCategoryListener
import com.example.myfirstapp.Objects.CurrencyManager
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Models.Dish

class BestFoodAdapter(private val listener: DishCategoryListener) : ListAdapter<Dish, BestFoodAdapter.BestFoodViewHolder>(DishDiffCallback()) {

    class BestFoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        private val priceMeal: TextView = itemView.findViewById(R.id.priceMeal)
        private val starTitle: TextView = itemView.findViewById(R.id.starTitle)
        private val dishImage: ImageView = itemView.findViewById(R.id.imageMeal)

        fun bind(dish: Dish) {
            val context = itemView.context
            titleTxt.text = dish.title
            priceMeal.text = CurrencyManager.convertPrice(dish.price)
            starTitle.text = dish.star.toString()

            Glide.with(context)
                .load(dish.imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(dishImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestFoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.best_food_item, parent, false)
        return BestFoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: BestFoodViewHolder, position: Int) {
        val dish = getItem(position)
        holder.bind(dish)
        val context = holder.itemView.context
        holder.itemView.findViewById<TextView>(R.id.timeCooking).text =
            context.getString(R.string.time_format, dish.timeValue, context.getString(R.string.min))
        holder.itemView.findViewById<TextView>(R.id.detailBtn).setOnClickListener {
            listener.loadSelectedDish(dish)
        }
    }
}
