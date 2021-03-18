package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.additional_dishes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.PlusMinusView
import com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter.AdditionalDishData
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.views.DishAddonView
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.views.resizeable_image.ResizeableImageView
import kotlinx.android.synthetic.main.additional_dish_item_header.view.*
import kotlinx.android.synthetic.main.additional_dishes_dialog_item_dish.view.*
import kotlinx.android.synthetic.main.additional_dishes_dialog_item_order_item.view.*


class AdditionalDishesNewAdapter(val context: Context, val listener: AdditionalDishesAdapterListener):
    ListAdapter<AdditionalDishData<Any>, RecyclerView.ViewHolder>(AdditionalDishesDiffCallback()), DishAddonView.DishAddonListener,
    PlusMinusView.PlusMinusInterface {



    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADDITIONAL_HEADER -> {
                 AdditionalHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.additional_dish_item_header, parent, false))
            }
            VIEW_TYPE_ORDER_ITEM -> {
                OrderItemViewHolder(LayoutInflater.from(context).inflate(R.layout.additional_dishes_dialog_item_order_item, parent, false))
            }
            else -> { // VIEW_TYPE_ADDITIONAL
                DishItemViewHolder(LayoutInflater.from(context).inflate(R.layout.additional_dishes_dialog_item_dish, parent, false))
            }
        }
    }

    interface AdditionalDishesAdapterListener {
        fun onDishCountChange(curOrderItem: OrderItem, isOrderItemsEmpty: Boolean) {}
        fun onDishClick(dish: Dish) {}
        fun onAddBtnClick(dish: Dish) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item.viewType) {
            VIEW_TYPE_ORDER_ITEM -> {
                val orderItem = item.dish as OrderItem
                (holder as OrderItemViewHolder).bind(orderItem)

                orderItem.menuItem?.let{
                    holder.plusMinusView.setPlusMinusListener(this, position, initialCounter = orderItem.quantity, quantityLeft = orderItem.menuItem?.getQuantityCount())
                }

            }
            VIEW_TYPE_ADDITIONAL_HEADER -> {
                holder as AdditionalHeaderViewHolder
                val cooksName = (item.dish as Dish).cook.getFullName()
                holder.body.text = "People who order from ${cooksName} usually added the following dishes"
            }
            VIEW_TYPE_ADDITIONAL -> {
                (holder as DishItemViewHolder).bind(item.dish as Dish, listener)
            }
        }

    }


    override fun onDishCountChange(counter: Int, position: Int) {
        var isOrderItemsEmpty = false
        val updatedOrderItem = (getItem(position).dish as OrderItem).copy()
        updatedOrderItem.quantity = counter
        if(counter == 0){
            isOrderItemsEmpty = itemCount == 1
            updatedOrderItem._destroy = true
        }
        listener.onDishCountChange(updatedOrderItem, isOrderItemsEmpty)
    }

    override fun onAddBtnClick(position: Int) {
        listener.onAddBtnClick(getItem(position).dish as Dish)
    }

    override fun onDishClick(position: Int) {
        listener.onDishClick(getItem(position).dish as Dish)
    }

    class AdditionalHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val body: TextView = view.additionalHeaderBody
    }

    class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val plusMinusView: PlusMinusView = view.orderItemPlusMinus
        private val name: TextView = view.orderItemName
        private val price: TextView = view.orderItemPrice
        private val count: TextView = view.orderItemCount
        private val img: ResizeableImageView = view.orderItemImg

        fun bind(orderItem: OrderItem){
            price.text = orderItem.price.formatedValue
            count.text = "${orderItem.quantity}"
            img.loadResizableImage(orderItem.dish.thumbnail)
            name.text = orderItem.dish.name
        }
    }

    class DishItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val addBtn: TextView = view.additionalDishAddBtn
        private val price: TextView = view.additionalDishPrice
        private val name: TextView = view.additionalDishName
        private val count: TextView = view.additionalDishCount
        private val img: ResizeableImageView = view.additionalDishImg

        fun bind(dish: Dish, listener: AdditionalDishesAdapterListener){
            price.text = dish.price.formatedValue
            count.text = "${dish.menuItem?.quantity ?: 0}"
            addBtn.setOnClickListener { listener.onAddBtnClick(dish) }
            img.setOnClickListener { listener.onDishClick(dish) }
            img.loadResizableImage(dish.thumbnail)
            name.text = dish.name
        }
    }

    class AdditionalDishesDiffCallback : DiffUtil.ItemCallback<AdditionalDishData<Any>>() {
        override fun areItemsTheSame(oldItem: AdditionalDishData<Any>, newItem: AdditionalDishData<Any>): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AdditionalDishData<Any>, newItem: AdditionalDishData<Any>): Boolean {
            return oldItem == newItem
        }
    }


    companion object {
        const val VIEW_TYPE_ORDER_ITEM = 1
        const val VIEW_TYPE_ADDITIONAL_HEADER = 2
        const val VIEW_TYPE_ADDITIONAL = 3
    }

    override fun onPlusMinusChange(counter: Int, position: Int) {
        var isOrderItemsEmpty = false
        val orderItem = getItem(position).dish as OrderItem
        orderItem.quantity = counter
        if(counter == 0){
            isOrderItemsEmpty = itemCount == 1
            orderItem._destroy = true
        }
        listener.onDishCountChange(orderItem, isOrderItemsEmpty)
    }


}


