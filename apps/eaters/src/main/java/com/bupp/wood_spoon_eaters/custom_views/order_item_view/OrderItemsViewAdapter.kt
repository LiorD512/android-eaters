package com.bupp.wood_spoon_eaters.custom_views.order_item_view

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.OrderItemViewBinding
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import java.text.DecimalFormat

class OrderItemsViewAdapter(val context: Context, val listener: OrderItemsViewAdapterListener) : ListAdapter<OrderItem, RecyclerView.ViewHolder>(
    OrderItemsViewDiffCallback()
) {

    interface OrderItemsViewAdapterListener {
        fun onDishCountChange(curOrderItem: OrderItem, isOrderItemsEmpty: Boolean) {}
    }

    class OrderItemsViewDiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = OrderItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val orderItem = getItem(position).copy()
        holder as OrderItemViewHolder
        holder.bindItem(orderItem)
    }

    class OrderItemViewHolder(view: OrderItemViewBinding) : RecyclerView.ViewHolder(view.root) {
        private val priceView: TextView = view.orderItemPrice
        private val name: TextView = view.orderItemName
        private val counter: TextView = view.orderItemCounter
        private val note: TextView = view.orderItemNote

        @SuppressLint("SetTextI18n")
        fun bindItem(orderItem: OrderItem) {
            val dish: Dish = orderItem.dish

            counter.text = "${orderItem.quantity}"
            name.text = dish.name


            val price = orderItem.price.value ?: 0.0

            val priceStr = DecimalFormat("##.##").format(price)
            priceView.text = "$$priceStr"

            if (!orderItem.getNoteStr().isNullOrEmpty()) {
                note.visibility = View.VISIBLE

                val builder = SpannableStringBuilder(orderItem.getNoteStr())
                builder.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(itemView.context, R.color.greyish_brown)),
                    0, 17,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                note.text = builder
            }
        }

    }


}
