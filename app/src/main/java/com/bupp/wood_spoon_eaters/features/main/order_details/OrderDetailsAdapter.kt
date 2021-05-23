package com.bupp.wood_spoon_eaters.features.main.order_details

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.OrderDetailsDishItemViewBinding
import com.bupp.wood_spoon_eaters.model.OrderItem

class OrderDetailsAdapter(val context: Context, private var dishes: List<OrderItem>) : RecyclerView.Adapter<OrderDetailsAdapter.DishViewHolder>() {

    class DishViewHolder(view: OrderDetailsDishItemViewBinding) : RecyclerView.ViewHolder(view.root) {
        val name: TextView = view.orderDetailsDishName
        val price: TextView = view.orderDetailsDishPrice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val binding = OrderDetailsDishItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DishViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val orderItem: OrderItem? = dishes[position]
        orderItem?.let{
            var count = ""
            if(it.quantity > 1){
                count = "X${it.quantity}"
            }
                holder.name.text = "${it.dish.name} $count"

            holder.price.text = it.price.formatedValue
        }
    }
}