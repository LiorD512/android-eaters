package com.bupp.wood_spoon_eaters.custom_views.adapters

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
import com.bupp.wood_spoon_eaters.model.Dish2
import com.bupp.wood_spoon_eaters.model.OrderItem2
import kotlinx.android.synthetic.main.order_item_view.view.*

class OrderItemsViewAdapter(val context: Context, private var orders: ArrayList<OrderItem2>) :
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

        var counter = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        return DishViewHolder(LayoutInflater.from(context).inflate(R.layout.order_item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val order: OrderItem2? = orders[position]
        val dish: Dish2 = order!!.dish

        if (order.quantity > 1) {
            holder.counter = order.quantity
        }
        Glide.with(context).load(dish.thumbnail).apply(RequestOptions.circleCropTransform()).into(holder.image)

        holder.name.text = "${dish.name} x${holder.counter}"
        holder.price.text = dish.price!!.formatedValue
        holder.counterText.text = "Count: ${holder.counter}"
        holder.note.text = order.notes

        holder.plusBtn.setOnClickListener {
            holder.counter++
            notifyDataSetChanged()
        }

        holder.minusBtn.setOnClickListener {
            if (holder.counter > 1)
                holder.counter--
            notifyDataSetChanged()
        }

        holder.ingredientsList.layoutManager = LinearLayoutManager(context)
        adapter = IngredientsCheckoutAdapter(context, dish.removedIngredients!!)
        holder.ingredientsList.adapter = adapter
    }

    fun setOrderItems(orderItems: ArrayList<OrderItem2>) {
        this.orders = orderItems
        notifyDataSetChanged()
    }


}