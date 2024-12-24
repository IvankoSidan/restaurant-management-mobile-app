package com.example.myfirstapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.DiffCallbacks.CategoryDiffCallback
import com.example.myfirstapp.Interfaces.DishCategoryListener
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Models.Category

class CategoryAdapter(private val listener: DishCategoryListener) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryImage: ImageView = itemView.findViewById(R.id.imgCategory)
        private val categoryName: TextView = itemView.findViewById(R.id.textTitle)

        fun bind(category: Category, position: Int) {
            categoryName.text = category.name
            val resourceId = itemView.context.resources.getIdentifier(category.imgPath, "drawable", itemView.context.packageName)
            if (resourceId != 0) {
                categoryImage.setImageResource(resourceId)
            } else {
                categoryImage.setImageResource(R.drawable.default_background)
            }
            val backgroundResources = arrayOf(
                R.drawable.cat_1_back,
                R.drawable.cat_2_back,
                R.drawable.cat_3_back,
                R.drawable.cat_4_back,
                R.drawable.cat_5_back,
                R.drawable.cat_6_back,
                R.drawable.cat_7_back,
                R.drawable.cat_8_back
            )
            if (position in backgroundResources.indices) {
                categoryImage.setBackgroundResource(backgroundResources[position])
            } else {
                categoryImage.setBackgroundResource(R.drawable.default_background)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, position)

        holder.itemView.setOnClickListener {
            listener.loadDishesByCategory(category.name)
        }
    }
}
