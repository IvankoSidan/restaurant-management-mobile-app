package com.example.myfirstapp.Adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.Interfaces.OnPaymentListener
import com.example.myfirstapp.R
import com.example.myfirstapp.data.Enums.CardType
import com.example.myfirstapp.data.Models.PaymentMethod

class PaymentMethodAdapter(
    private val paymentMethods: MutableList<PaymentMethod>,
    private val listener: OnPaymentListener
) : RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.payment_item, parent, false)
        return PaymentMethodViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        val paymentMethod = paymentMethods[position]
        holder.bind(paymentMethod, position == selectedPosition)
    }

    override fun getItemCount(): Int = paymentMethods.size

    inner class PaymentMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageCard: ImageView = itemView.findViewById(R.id.imageCard)
        private val cardNumber: TextView = itemView.findViewById(R.id.cardNumber)
        private val selectedItemIcon: ImageView = itemView.findViewById(R.id.selectedItemIcon)
        private val typeCard: TextView = itemView.findViewById(R.id.typeCard)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                    notifyItemChanged(selectedPosition)
                    listener.onPaymentMethodClick(paymentMethods[position])
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    AlertDialog.Builder(itemView.context)
                        .setMessage(itemView.context.getString(R.string.delete_payment_method_confirmation))
                        .setPositiveButton(itemView.context.getString(R.string.yes)) { _, _ ->
                            listener.onPaymentMethodDelete(paymentMethods[position])
                        }
                        .setNegativeButton(itemView.context.getString(R.string.no), null)
                        .show()
                }
                true
            }
        }

        fun bind(paymentMethod: PaymentMethod, isSelected: Boolean) {
            // Установка изображения карты по типу
            imageCard.setImageResource(
                when (paymentMethod.cardType) {
                    CardType.VISA -> R.drawable.visa
                    CardType.MASTERCARD -> R.drawable.mastercard
                    CardType.AMEX -> R.drawable.amex
                    CardType.PSB -> R.drawable.psb
                    CardType.SBER -> R.drawable.sberbank
                    CardType.TINKOFF -> R.drawable.tincoff
                    CardType.DISCOVER -> R.drawable.discover
                    CardType.UNIONPAY -> R.drawable.unionpay
                    CardType.JCB -> R.drawable.jcb
                    CardType.OTHER -> R.drawable.add_card_icon
                }
            )

            // Форматирование номера карты
            cardNumber.text = "•••• •••• •••• ${paymentMethod.cardLastFour}"

            // Установка типа карты
            typeCard.text = when (paymentMethod.cardType) {
                CardType.VISA -> "Visa"
                CardType.MASTERCARD -> "Mastercard"
                CardType.AMEX -> "American Express"
                CardType.PSB -> "PSB"
                CardType.SBER -> "Sberbank"
                CardType.TINKOFF -> "Tinkoff"
                CardType.DISCOVER -> "Discover"
                CardType.UNIONPAY -> "UnionPay"
                CardType.JCB -> "JCB"
                CardType.OTHER -> "Other"
            }

            selectedItemIcon.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
        }
    }

    fun updateData(newPaymentMethods: List<PaymentMethod>) {
        paymentMethods.clear()
        paymentMethods.addAll(newPaymentMethods.filter { it.id != 0L && it.cardLastFour.isNotBlank() })

        selectedPosition = paymentMethods.indexOfFirst { it.isSelected }
        if (selectedPosition == -1 && paymentMethods.isNotEmpty()) {
            selectedPosition = 0
        }

        notifyDataSetChanged()
    }

    fun getSelectedPaymentMethod(): PaymentMethod? {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            paymentMethods[selectedPosition]
        } else {
            null
        }
    }
}