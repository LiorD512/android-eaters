package com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.presentation.custom_views.order_view.OrderView
import com.bupp.wood_spoon_chef.data.remote.model.Order
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.SingleOrderItemBinding

class SingleOrdersAdapter(
    val context: Context,
    val listener: SingleOrdersAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    OrderView.OrderViewListener {

    private var ordersList: MutableList<Order> = mutableListOf()
    private var lastUpdatedItemPosition: Int = -1

    interface SingleOrdersAdapterListener {
        fun onChatClick(curOrder: Order)
        fun onCancelClick(curOrder: Order)
        fun onWorldwideInfoClick()
        fun updateStatus(status: Int, orderId: Long)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curOrder: Order = ordersList[position]

        var curPreparationStatus = Constants.PREPARATION_STATUS_NULL

        when (curOrder.preparationStatus) {
            "idle" -> curPreparationStatus = Constants.PREPARATION_STATUS_IDLE
            "received" -> curPreparationStatus = Constants.PREPARATION_STATUS_RECEIVED
            "in_progress" -> curPreparationStatus = Constants.PREPARATION_STATUS_IN_PROGRESS
            "completed" -> curPreparationStatus = Constants.PREPARATION_STATUS_COMPLETED
        }
        when (curOrder.deliveryStatus) {
            "on_the_way" -> curPreparationStatus = Constants.PREPARATION_STATUS_DELIVERED
            "shipped" -> curPreparationStatus = Constants.PREPARATION_STATUS_SHIPPED
        }
        when (curOrder.status) {
            "cancelled" -> curPreparationStatus = Constants.PREPARATION_STATUS_CANCELLED
        }

        (holder as ItemViewHolder).orderView.init(
            curOrder,
            this,
            curPreparationStatus,
            curOrder.cookingSlot.isNationwide,
            position
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = SingleOrderItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    override fun onChatClick(curOrder: Order) {
        listener.onChatClick(curOrder)
    }

    override fun onCancelClick(curOrder: Order) {
        listener.onCancelClick(curOrder)
    }

    override fun onWorldwideInfoClick() {
        listener.onWorldwideInfoClick()
    }

    override fun updateStatus(status: Int, orderId: Long, itemPosition: Int) {
        lastUpdatedItemPosition = itemPosition
        listener.updateStatus(status, orderId)
    }

    fun setStatusForLastUpdatesItem(status: Int) {
        when (status) {
            Constants.PREPARATION_STATUS_RECEIVED -> {
                val order = ordersList.getOrNull(lastUpdatedItemPosition)
                order?.preparationStatus = "received"
                notifyItemChanged(lastUpdatedItemPosition)
            }
            Constants.PREPARATION_STATUS_IN_PROGRESS -> {
                val order = ordersList.getOrNull(lastUpdatedItemPosition)
                order?.preparationStatus = "in_progress"
                notifyItemChanged(lastUpdatedItemPosition)
            }
            Constants.PREPARATION_STATUS_COMPLETED -> {
                val order = ordersList.getOrNull(lastUpdatedItemPosition)
                order?.preparationStatus = "completed"
                notifyItemChanged(lastUpdatedItemPosition)
            }
        }
    }

    fun updateList(orders: MutableList<Order>) {
        ordersList.clear()
        ordersList.addAll(orders)
        notifyDataSetChanged()
    }
}

class ItemViewHolder(view: SingleOrderItemBinding) : RecyclerView.ViewHolder(view.root) {
    val orderView = view.singleOrderView
}






