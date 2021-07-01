package com.bupp.wood_spoon_eaters.features.main.order_history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.OrdersHistoryItemBinding
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.views.gridStackableTextView.CertificatesAdapter

class OrdersHistoryAdapter(val context: Context, val listener: OrdersHistoryAdapterListener) :
    ListAdapter<Order, RecyclerView.ViewHolder>(DiffCallback()) {

    interface OrdersHistoryAdapterListener {
        fun onOrderClick(orderId: Long)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val order = getItem(position)
        (holder as ItemViewHolder).initItem(context, order)

        holder.mainLayout.setOnClickListener {
            listener.onOrderClick(order.id!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = OrdersHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }


    class DiffCallback : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    inner class ItemViewHolder(view: OrdersHistoryItemBinding) : RecyclerView.ViewHolder(view.root) {
        fun initItem(context: Context, order: Order) {
            title.text = context.getString(R.string.order_history_item_by_cook) + " ${order.cook?.firstName}"
            price.text = "Total: ${order.total?.formatedValue}"
            if (order.estDeliveryTime != null) {
                date.text = DateUtils.parseDateToDateAndTime(order.estDeliveryTime)
            } else {
                date.text = "${order.estDeliveryTimeText}"
            }

        }

        private val title: TextView = view.orderHistoryItemChef
        private val price: TextView = view.orderHistoryItemPrice
        private val date: TextView = view.orderHistoryItemDate
        val mainLayout = view.orderHistoryMainLayout

    }
}