package com.bupp.wood_spoon_eaters.custom_views.order_item_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.custom_views.PlusMinusView
import com.bupp.wood_spoon_eaters.custom_views.adapters.IngredientsCheckoutAdapter
import com.bupp.wood_spoon_eaters.databinding.OrderItemViewBinding
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import java.text.DecimalFormat

class OrderItemsViewAdapter(val context: Context, val listener: OrderItemsViewAdapterListener)  :
    ListAdapter<OrderItem, RecyclerView.ViewHolder>(OrderItemsViewDiffCallback()) {


    class OrderItemsViewDiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem == newItem
        }
    }

    interface OrderItemsViewAdapterListener {
        fun onDishCountChange(curOrderItem: OrderItem, isOrderItemsEmpty: Boolean) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = OrderItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val orderItem = getItem(position).copy()
        holder as OrderItemViewHolder
//        val newOrderItem = OrderItem(
//            id = orderItem.id,
//            quantity = orderItem.quantity,
//            notes = orderItem.notes,
//            removedIngredients = orderItem.removedIngredients,
//            _destroy = orderItem._destroy,
//            dish = orderItem.dish,
//            price = orderItem.price,
//            menuItem = orderItem.menuItem
//        )
        holder.bindItem(context, orderItem, itemCount, listener, position)
    }

    class OrderItemViewHolder(view: OrderItemViewBinding) : RecyclerView.ViewHolder(view.root) {
        private lateinit var adapter: IngredientsCheckoutAdapter
        private val layout: LinearLayout = view.orderItemLayout
        private val priceView: TextView = view.orderItemPrice
        private val image: ImageView = view.orderItemImage
        private val name: TextView = view.orderItemName
        private val plusMinusView: PlusMinusView = view.orderItemPlusMinus
        private val note: TextView = view.orderItemNote
        private val ingredientsList = view.orderItemIngredientsRecyclerView!!

        fun bindItem(context: Context, orderItem: OrderItem, itemCount: Int, listener: OrderItemsViewAdapterListener, position: Int){
            val dish: Dish = orderItem.dish

            if(orderItem.quantity == 0){
                layout.alpha = 0.5f
            }else{
                layout.alpha = 1f
            }

            Glide.with(context).load(dish.thumbnail).apply(RequestOptions.circleCropTransform()).into(image)
            name.text = "${dish.name} x${orderItem.quantity}"

            val price = (orderItem.price.value*orderItem.quantity)
            val priceStr = DecimalFormat("##.##").format(price)
            priceView.text = "$$priceStr"
//        counterText.text = "Count: ${orderItem.quantity}"
            plusMinusView.setPlusMinusListener(object: PlusMinusView.PlusMinusInterface{
                override fun onPlusMinusChange(counter: Int, position: Int) {
                    orderItem.quantity = counter
                    onDishCountChange(counter, orderItem,  itemCount, listener)
                }
            }, position, initialCounter = orderItem.quantity, quantityLeft = orderItem.menuItem?.quantity)

            if(orderItem.notes.isNullOrEmpty()){
                note.visibility = View.GONE
            }else{
                note.visibility = View.VISIBLE
                note.text = orderItem.notes
            }


            ingredientsList.layoutManager = LinearLayoutManager(context)
            adapter = IngredientsCheckoutAdapter(context, orderItem.removedIngredients)
            ingredientsList.adapter = adapter
        }

        fun onDishCountChange(counter: Int, orderItem: OrderItem, itemCount: Int,  listener: OrderItemsViewAdapterListener) {
            var isOrderItemsEmpty = false
            val updatedOrderItem = orderItem.copy()
            updatedOrderItem.quantity = counter
            if (counter == 0) {
                isOrderItemsEmpty = itemCount == 1
                updatedOrderItem._destroy = true
            }
            listener.onDishCountChange(updatedOrderItem, isOrderItemsEmpty)
        }

    }


//    fun getAllDishPriceValue(): Double {
//        var sum: Double = 0.0
//        for(item in itemlis){
//            sum += (item.price.value*item.quantity)
//        }
//        return sum
//    }
//
//
//    fun getOrderItemsQuantity(): Int {
//        var sum: Int = 0
//        for(item in orders){
//            sum += item.quantity
//        }
//        return sum
//    }



}

//
//
//    RecyclerView.Adapter<OrderItemsViewAdapter.DishViewHolder>(){
//
//
//
//    var adapter: IngredientsCheckoutAdapter? = null
//
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
//        return DishViewHolder(
//            LayoutInflater.from(context).inflate(R.layout.order_item_view, parent, false)
//        )
//    }
//
//    override fun getItemCount(): Int {
//        return orders?.size
//    }
//
//    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
//
//    }
//
//    private fun onDishCountChange(counter: Int, position: Int, orderItem: OrderItem) {
//        var isOrderItemsEmpty = false
//        val updatedOrderItem = orders[position].copy()
//        updatedOrderItem.quantity = counter
//        if(counter == 0){
//            isOrderItemsEmpty = itemCount == 1
//            updatedOrderItem._destroy = true
//        }
//        listener.onDishCountChange(updatedOrderItem, isOrderItemsEmpty)
//    }
//
//    fun setOrderItems(orderItems: ArrayList<OrderItem>) {
//        this.orders = orderItems
//        notifyDataSetChanged()
//    }
//
//

//
//}