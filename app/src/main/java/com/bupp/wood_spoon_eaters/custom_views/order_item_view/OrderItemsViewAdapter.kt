package com.bupp.wood_spoon_eaters.custom_views.order_item_view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.custom_views.PlusMinusView
import com.bupp.wood_spoon_eaters.custom_views.adapters.IngredientsCheckoutAdapter
import com.bupp.wood_spoon_eaters.databinding.OrderItemViewBinding
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import java.text.DecimalFormat

class OrderItemsViewAdapter(val context: Context): ListAdapter<OrderItem, RecyclerView.ViewHolder>(OrderItemsViewDiffCallback()) {


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
        holder.bindItem(context, orderItem)
    }

    class OrderItemViewHolder(view: OrderItemViewBinding) : RecyclerView.ViewHolder(view.root) {
        private lateinit var adapter: IngredientsCheckoutAdapter
        private val priceView: TextView = view.orderItemPrice
        private val name: TextView = view.orderItemName
        private val counter: TextView = view.orderItemCounter
        private val ingredientsList = view.orderItemIngredientsRecyclerView!!

        @SuppressLint("SetTextI18n")
        fun bindItem(context: Context, orderItem: OrderItem){
            val dish: Dish = orderItem.dish

            counter.text = "x${orderItem.quantity}"
            name.text = "${dish.name} x${orderItem.quantity}"

            var price = 0.0
            orderItem.price.value?.let{
                price = it*orderItem.quantity

            }
            val priceStr = DecimalFormat("##.##").format(price)
            priceView.text = "$$priceStr"

            ingredientsList.layoutManager = LinearLayoutManager(context)
            adapter = IngredientsCheckoutAdapter(context, listOfNotNull(orderItem.getRemovedIngredients(), orderItem.getNoteStr()))
            ingredientsList.adapter = adapter
        }

    }

}
