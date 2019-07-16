package com.bupp.wood_spoon_eaters.custom_views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.OrderItem2
import kotlinx.android.synthetic.main.order_item_view.view.orderItemImage
import kotlinx.android.synthetic.main.order_item_view.view.orderItemName
import kotlinx.android.synthetic.main.rate_dish_item_view.view.*

class RateLastOrderAdapter(val context: Context, private var dishes: ArrayList<OrderItem2>, private val listener: RateLastOrderAdapterListener) : RecyclerView.Adapter<RateLastOrderAdapter.DishViewHolder>() {

    interface RateLastOrderAdapterListener{
        fun onDishRate()
    }
    var dishesRating: LinkedHashMap<OrderItem2, Boolean?> = linkedMapOf()

    class DishViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.orderItemImage
        val name: TextView = view.orderItemName
        val likeBtn: ImageView = view.rateDishItemAccuracyPositive
        val dislikeBtn: ImageView = view.rateDishItemAccuracyNegative
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        return DishViewHolder(LayoutInflater.from(context).inflate(R.layout.rate_dish_item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val orderItem: OrderItem2? = dishes[position]

        Glide.with(context).load(orderItem!!.dish.thumbnail).apply(RequestOptions.circleCropTransform())
            .into(holder.image)

        holder.name.text = "${orderItem.dish.name} x${orderItem.quantity}"

        holder.likeBtn.setOnClickListener {
            holder.likeBtn.isSelected = true
            holder.dislikeBtn.isSelected = false

            dishesRating[orderItem] = true
            listener.onDishRate()
        }

        holder.dislikeBtn.setOnClickListener {
            holder.likeBtn.isSelected = false
            holder.dislikeBtn.isSelected = true

            dishesRating[orderItem] = false
            listener.onDishRate()
        }

    }

    fun getRatedDishes(): LinkedHashMap<OrderItem2, Boolean?> {
        return dishesRating
    }
}