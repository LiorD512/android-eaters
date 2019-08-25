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
import java.text.DecimalFormat

class OrderItemsViewAdapter(val listener: OrderItemsViewAdapterListener, val context: Context, private var orders: ArrayList<OrderItem>) :
    RecyclerView.Adapter<OrderItemsViewAdapter.DishViewHolder>() {

    interface OrderItemsViewAdapterListener{
        fun onDishCountChange()
    }

    var adapter: IngredientsCheckoutAdapter? = null

    class DishViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout = view.orderItemLayout
        val price: TextView = view.orderItemPrice
        val image: ImageView = view.orderItemImage
        val name: TextView = view.orderItemName
        val plusBtn: CardView = view.orderItemCountPlus
        val minusBtn: CardView = view.orderItemCountMinus
        val counterText: TextView = view.orderItemCounter
        val note: TextView = view.orderItemNote
        val ingredientsList = view.orderItemIngredientsRecyclerView!!

//        var counterVal = 1
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

        if(orderItem.quantity == 0){
            holder.layout.alpha = 0.5f
        }else{
            holder.layout.alpha = 1f
        }

        Glide.with(context).load(dish.thumbnail).apply(RequestOptions.circleCropTransform()).into(holder.image)
        holder.name.text = "${dish.name} x${orderItem.quantity}"

        val price = (orderItem.price.value*orderItem.quantity)
        val priceStr = DecimalFormat("##.##").format(price)
        holder.price.text = "$$priceStr"
        holder.counterText.text = "Count: ${orderItem.quantity}"

        if(orderItem.notes.isNullOrEmpty()){
            holder.note.visibility = View.GONE
        }else{
            holder.note.visibility = View.VISIBLE
            holder.note.text = orderItem.notes
        }

        holder.plusBtn.setOnClickListener {
            orderItem.quantity++
            notifyDataSetChanged()
            listener?.onDishCountChange()
        }

        holder.minusBtn.setOnClickListener {
            if (orderItem.quantity > 0){
                orderItem.quantity--
            }
            notifyDataSetChanged()
            listener?.onDishCountChange()
        }

        holder.ingredientsList.layoutManager = LinearLayoutManager(context)
        adapter = IngredientsCheckoutAdapter(context, orderItem.removedIndredients)
        holder.ingredientsList.adapter = adapter
    }

    fun setOrderItems(orderItems: ArrayList<OrderItem>) {
        this.orders = orderItems
        notifyDataSetChanged()
    }

    fun getAllDishPriceValue(): Double {
        var sum: Double = 0.0
        for(item in orders){
            sum += (item.price.value*item.quantity)
        }
        return sum
    }


}