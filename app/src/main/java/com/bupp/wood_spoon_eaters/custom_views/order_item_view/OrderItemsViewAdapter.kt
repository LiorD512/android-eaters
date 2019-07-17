package com.bupp.wood_spoon_eaters.custom_views.order_item_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.IngredientsCheckoutAdapter
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import kotlinx.android.synthetic.main.order_item_view.view.*

class OrderItemsViewAdapter(val context: Context, private var orders: ArrayList<OrderItem>) :
    RecyclerView.Adapter<OrderItemsViewAdapter.DishViewHolder>() {

    var adapter: IngredientsCheckoutAdapter? = null

    class DishViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val price: TextView = view.orderItemPrice
        val image: ImageView = view.orderItemImage
        val name: TextView = view.orderItemName
        val plusBtn: CardView = view.orderItemCountPlus
        val minusBtn: CardView = view.orderItemCountMinus
        val counterText: TextView = view.orderItemCounter
        val note: TextView = view.orderItemNote
        val ingredientsList = view.orderItemIngredientsRecyclerView!!

        var counterVal = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        return DishViewHolder(
            LayoutInflater.from(context).inflate(R.layout.order_item_view, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val orderItem: OrderItem = orders[position]
        val dish: Dish = orderItem.dish

        if (orderItem.quantity > 1) {
            holder.counterVal = orderItem.quantity
        }
        Glide.with(context).load(dish.thumbnail).apply(RequestOptions.circleCropTransform()).into(holder.image)
        holder.name.text = "${dish.name} x${holder.counterVal}"

        holder.price.text = orderItem.price.formatedValue
        holder.counterText.text = "Count: ${holder.counterVal}"

        if(orderItem.notes.isNullOrEmpty()){
            holder.note.visibility = View.GONE
        }else{
            holder.note.visibility = View.VISIBLE
            holder.note.text = orderItem.notes
        }

        holder.plusBtn.setOnClickListener {
            holder.counterVal++
            notifyDataSetChanged()
        }

        holder.minusBtn.setOnClickListener {
            if (holder.counterVal > 1)
                holder.counterVal--
            notifyDataSetChanged()
        }

        holder.ingredientsList.layoutManager = LinearLayoutManager(context)
        adapter = IngredientsCheckoutAdapter(context, orderItem.removedIndredients)
        holder.ingredientsList.adapter = adapter
    }

    fun setOrderItems(orderItems: ArrayList<OrderItem>) {
        this.orders = orderItems
        notifyDataSetChanged()
    }


}