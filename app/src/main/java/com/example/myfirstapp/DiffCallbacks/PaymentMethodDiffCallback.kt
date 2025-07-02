package com.example.myfirstapp.DiffCallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.myfirstapp.data.Models.PaymentMethod

class PaymentMethodDiffCallback(
    private val oldList: List<PaymentMethod>,
    private val newList: List<PaymentMethod>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int) =
        oldList[oldPos].id == newList[newPos].id

    override fun areContentsTheSame(oldPos: Int, newPos: Int) =
        oldList[oldPos] == newList[newPos]
}
