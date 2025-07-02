package com.example.myfirstapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.Interfaces.CardAdapterListener
import com.example.myfirstapp.data.Models.PaymentMethod
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Enums.CardType

/**
 * Адаптер для отображения типов банковских карт в горизонтальном списке
 *
 * @param cardTypes список типов карт для отображения
 * @param listener обработчик событий выбора карты
 */
class CardAdapter(
    private var cardTypes: List<CardType>,
    private val listener: CardAdapterListener
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageCardItem: ImageView = itemView.findViewById(R.id.imageCardItem)
        private val selectedImage: ImageView = itemView.findViewById(R.id.selectedImage)
        private val layoutCard: CardView = itemView.findViewById(R.id.constraintLayoutImage)

        fun bind(cardType: CardType, isSelected: Boolean) {
            imageCardItem.setImageResource(cardType.imageRes)

            if (cardType == CardType.OTHER) {
                selectedImage.visibility = View.GONE
                layoutCard.background = null
            } else {
                selectedImage.visibility = if (isSelected) View.VISIBLE else View.GONE
                layoutCard.background = if (isSelected) {
                    ContextCompat.getDrawable(itemView.context, R.drawable.border_selected)
                } else {
                    null
                }
            }

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                if (cardType == CardType.OTHER) {
                    listener.onAddCard()
                } else {
                    listener.onCardTypeSelected(cardType)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_card_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cardTypes[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = cardTypes.size

    fun updateSelectedCard(cardType: CardType) {
        val position = cardTypes.indexOf(cardType)
        if (position != -1) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    fun updateData(newCardTypes: List<CardType>) {
        cardTypes = newCardTypes
        notifyDataSetChanged()
    }
}
