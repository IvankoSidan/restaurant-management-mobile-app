package com.example.myfirstapp.Adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.DiffCallbacks.CategoryDiffCallback
import com.example.myfirstapp.Interfaces.DishCategoryListener
import com.example.myfirstapp.R
import com.example.myfirstapp.Utils.GlideApp
import com.example.myfirstapp.data.Models.Category
import java.util.Random

class CategoryAdapter(private val listener: DishCategoryListener) :
    ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryImage: ImageView = itemView.findViewById(R.id.imgCategory)
        private val categoryName: TextView = itemView.findViewById(R.id.textTitle)

        fun bind(category: Category, position: Int) {
            categoryName.text = category.name

            val baseUrl = "https://10.0.2.2:8443/"
            val imageUrl = if (category.imgPath.isNotEmpty()) {
                baseUrl + "uploads/" + category.imgPath
            } else {
                null
            }

            Log.d("CategoryAdapter", "Image URL: $imageUrl")

            GlideApp.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.default_background)
                .error(R.drawable.default_background)
                .into(categoryImage)

            val backgrounds = itemView.context.resources.obtainTypedArray(R.array.background_resources)
            if (position < backgrounds.length()) {
                categoryImage.setBackgroundResource(backgrounds.getResourceId(position, R.drawable.default_background))
            } else {
                val randomColorDrawable = createRandomColorDrawable(itemView.context, getRandomColor())
                categoryImage.background = randomColorDrawable
            }

            backgrounds.recycle()

            itemView.setOnClickListener {
                listener.loadDishesByCategory(category.name)
            }
        }
    }

    private fun createRandomColorDrawable(context: Context, color: Int): Drawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.cornerRadius = 10f
        gradientDrawable.setColor(color)
        return gradientDrawable
    }

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, position)
    }
}
