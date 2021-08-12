package com.bupp.wood_spoon_eaters.features.main.order_history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.OrdersHistoryActiveOrderItemBinding
import com.bupp.wood_spoon_eaters.databinding.OrdersHistoryOrderItemBinding
import com.bupp.wood_spoon_eaters.databinding.OrdersHistoryTitleItemBinding
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.views.OrderProgressBar


class OrdersHistoryAdapter(val context: Context, val listener: OrdersHistoryAdapterListener) :
    ListAdapter<OrderHistoryBaseItem, RecyclerView.ViewHolder>(DiffCallback()) {

    interface OrdersHistoryAdapterListener {
        fun onOrderClick(orderId: Long)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            OrderHistoryViewType.ORDER.ordinal -> {
                val binding = OrdersHistoryOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OrderItemViewHolder(binding)
            }
            OrderHistoryViewType.ACTIVE_ORDER.ordinal -> {
                val binding = OrdersHistoryActiveOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ActiveOrderItemViewHolder(binding)
            }
            else -> { //OrderHistoryViewType.TITLE
                val binding = OrdersHistoryTitleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OrderHistoryTitleViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(item){
            is OrderAdapterItemTitle -> {
                holder as OrderHistoryTitleViewHolder
                holder.bindItem(item)
            }
            is OrderAdapterItemOrder -> {
                holder as OrderItemViewHolder
                holder.bindItem(item)
            }
            is OrderAdapterItemActiveOrder -> {
                holder as ActiveOrderItemViewHolder
                holder.bindItem(item)
            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<OrderHistoryBaseItem>() {

        override fun areItemsTheSame(oldItem: OrderHistoryBaseItem, newItem: OrderHistoryBaseItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: OrderHistoryBaseItem, newItem: OrderHistoryBaseItem): Boolean {
            return oldItem == newItem
        }
    }



    inner class OrderItemViewHolder(val binding: OrdersHistoryOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title: TextView = binding.orderHistoryItemChef
        private val price: TextView = binding.orderHistoryItemPrice
        private val date: TextView = binding.orderHistoryItemDate
        val mainLayout = binding.orderHistoryMainLayout

        fun bindItem(data: OrderAdapterItemOrder) {
            val order = data.order
            title.text = context.getString(R.string.order_history_item_by_cook) + " ${order.restaurant?.firstName}"
            price.text = "Total: ${order.total?.formatedValue}"
            if (order.estDeliveryTime != null) {
                date.text = DateUtils.parseDateToDateAndTime(order.estDeliveryTime)
            } else {
                date.text = "${order.estDeliveryTimeText}"
            }

            mainLayout.setOnClickListener {
                order.id?.let { it1 -> listener.onOrderClick(it1) }
            }
        }
    }

    inner class ActiveOrderItemViewHolder(val binding: OrdersHistoryActiveOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title: TextView = binding.activeOrderRestaurantName
        private val subtitle: TextView = binding.activeOrderSubtitle
        private val orderPb: OrderProgressBar = binding.activeOrderPb

//        fun getForegroundFragment(): Fragment? {
//            val navHostFragment: Fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)
//            return if (navHostFragment == null) null else navHostFragment.childFragmentManager.fragments.get(0)
//        }

        fun bindItem(data: OrderAdapterItemActiveOrder) {
            val order = data.order
            title.text = order.restaurant?.getFullName() ?: ""
            val orderState = order.getOrderState()
            orderPb.setState(orderState)
//            val order = data.order
//            title.text = context.getString(R.string.order_history_item_by_cook) + " ${order.cook?.firstName}"
//            price.text = "Total: ${order.total?.formatedValue}"
//            if (order.estDeliveryTime != null) {
//                date.text = DateUtils.parseDateToDateAndTime(order.estDeliveryTime)
//            } else {
//                date.text = "${order.estDeliveryTimeText}"
//            }
        }
    }

    inner class OrderHistoryTitleViewHolder(val binding: OrdersHistoryTitleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title = binding.orderHistoryTitle
        fun bindItem(titleItem: OrderAdapterItemTitle){
            title.text = titleItem.title
        }
    }
}