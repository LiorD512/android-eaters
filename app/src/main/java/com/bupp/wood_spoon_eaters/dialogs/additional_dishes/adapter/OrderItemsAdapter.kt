package com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.views.DishAddonView
import com.bupp.wood_spoon_eaters.model.OrderItem


class OrderItemsAdapter(val context: Context, val listener: OrderItemsListener) :
    ListAdapter<OrderItem, RecyclerView.ViewHolder>(AdditionalDishesDiffCallback()), DishAddonView.DishAddonListener {


    class AdditionalDishesDiffCallback: DiffUtil.ItemCallback<OrderItem>(){
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem == newItem
        }
    }

    interface OrderItemsListener {
        fun onDishCountChange(curOrderItem: OrderItem, isOrderItemsEmpty: Boolean) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val orderItem = getItem(position)
//        (holder as OrderItemViewHolder).item.setDish(orderItem, position)
//        holder.item.setDishAddonListener(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderItemViewHolder(LayoutInflater.from(context).inflate(R.layout.additional_dishes_dialog_item_dish, parent, false))
    }

    override fun onDishCountChange(counter: Int, position: Int) {
        var isOrderItemsEmpty = false
        val updatedOrderItem = getItem(position).copy()
        updatedOrderItem.quantity = counter
        if(counter == 0){
            isOrderItemsEmpty = itemCount == 1
            updatedOrderItem._destroy = true
        }
        listener.onDishCountChange(updatedOrderItem, isOrderItemsEmpty)
    }


    class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val item: DishAddonView = view.additionalDishItem
    }

}


