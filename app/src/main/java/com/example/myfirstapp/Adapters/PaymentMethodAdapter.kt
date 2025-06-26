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
import com.example.myfirstapp.data.Models.PaymentMethod

class PaymentMethodAdapter(
    private val paymentMethods: MutableList<PaymentMethod>,
    private val listener: OnPaymentListener
) : RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.payment_item, parent, false)
        return PaymentMethodViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
        val paymentMethod = paymentMethods[position]
        holder.bind(paymentMethod, position)
    }

    override fun getItemCount(): Int = paymentMethods.size

    inner class PaymentMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageCard: ImageView = itemView.findViewById(R.id.imageCard)
        private val cardNumber: TextView = itemView.findViewById(R.id.cardNumber)
        private val selectedItemIcon: ImageView = itemView.findViewById(R.id.selectedItemIcon)
        private val typeCard: TextView = itemView.findViewById(R.id.typeCard)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = adapterPosition
                    notifyItemChanged(selectedPosition)
                    listener.onPaymentMethodClick(paymentMethods[adapterPosition])
                }
            }

            itemView.setOnLongClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    AlertDialog.Builder(itemView.context)
                        .setMessage(itemView.context.getString(R.string.delete_payment_method_confirmation))
                        .setPositiveButton(itemView.context.getString(R.string.yes)) { _, _ ->
                            listener.onPaymentMethodDelete(paymentMethods[adapterPosition])
                        }
                        .setNegativeButton(itemView.context.getString(R.string.no), null)
                        .show()
                }
                true
            }
        }

        fun bind(paymentMethod: PaymentMethod, position: Int) {
            imageCard.setImageResource(paymentMethod.imageCard)
            cardNumber.text = paymentMethod.cardNumber
            typeCard.text = paymentMethod.typeCard
            selectedItemIcon.visibility = if (position == selectedPosition) View.VISIBLE else View.INVISIBLE
        }
    }

    fun updateData(newPaymentMethods: MutableList<PaymentMethod>) {
        paymentMethods.clear()
        paymentMethods.addAll(newPaymentMethods)
        notifyDataSetChanged()
    }
}
