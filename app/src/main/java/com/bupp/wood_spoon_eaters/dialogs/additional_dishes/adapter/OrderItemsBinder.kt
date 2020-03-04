package com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter

import android.util.Log
import android.view.View
import mva2.adapter.ItemViewHolder
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import kotlinx.android.synthetic.main.additional_dishes_recycler_item.view.*
import mva2.adapter.ItemBinder


class OrderItemsBinder(val listener: OrderItemsAdapter.OrderItemsListener) : ItemBinder<OrderItems, OrderItemsBinder.OrderItemsViewHolder>() {


    private val TAG: String = "wowAdditionlMainAdApter"
    override fun createViewHolder(parent: ViewGroup): OrderItemsViewHolder {
        return OrderItemsViewHolder(inflate(parent, R.layout.additional_dishes_recycler_item))
    }

    override fun canBindData(item: Any): Boolean {
        return item is OrderItems
    }

    override fun bindViewHolder(holder: OrderItemsViewHolder, items: OrderItems) {
        holder.bindItems(items)
    }

    inner class OrderItemsViewHolder(itemView: View) : ItemViewHolder<OrderItems>(itemView) {
        fun bindItems(item: OrderItems) {
            Log.d(TAG, "OrderItemsViewHolder")
            val adapter = OrderItemsAdapter(itemView.context, listener)
            itemView.additionalDishItemList.layoutManager = LinearLayoutManager(itemView.context)
            itemView.additionalDishItemList.adapter = adapter

            val divider = DividerItemDecorator(ContextCompat.getDrawable(itemView.context, R.drawable.divider))
            itemView.additionalDishItemList.addItemDecoration(divider)

            adapter.submitList(item.orderItems)
        }
    }

}