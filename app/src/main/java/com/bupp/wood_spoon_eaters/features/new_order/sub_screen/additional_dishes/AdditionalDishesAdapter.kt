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
import com.bupp.wood_spoon_eaters.databinding.AdditionalDishItemHeaderBinding
import com.bupp.wood_spoon_eaters.databinding.AdditionalDishesDialogItemDishBinding
import com.bupp.wood_spoon_eaters.databinding.AdditionalDishesDialogItemOrderItemBinding
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.views.resizeable_image.ResizeableImageView


class AdditionalDishesAdapter(val context: Context, val listener: AdditionalDishesAdapterListener):
    ListAdapter<AdditionalDishData<Any>, RecyclerView.ViewHolder>(AdditionalDishesDiffCallback()), PlusMinusView.PlusMinusInterface {


    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADDITIONAL_HEADER -> {
                val binding = AdditionalDishItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdditionalHeaderViewHolder(binding)
//                 AdditionalHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.additional_dish_item_header, parent, false))
            }
            VIEW_TYPE_ORDER_ITEM -> {
                val binding = AdditionalDishesDialogItemOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OrderItemViewHolder(binding)
//                OrderItemViewHolder(LayoutInflater.from(context).inflate(R.layout.additional_dishes_dialog_item_order_item, parent, false))
            }
            else -> { // VIEW_TYPE_ADDITIONAL
                val binding = AdditionalDishesDialogItemDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DishItemViewHolder(binding)
//                DishItemViewHolder(LayoutInflater.from(context).inflate(R.layout.additional_dishes_dialog_item_dish, parent, false))
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
                    holder.plusMinusView.setPlusMinusListener(this, position, initialCounter = orderItem.quantity, quantityLeft = it.getQuantityCount())
                }

            }
            VIEW_TYPE_ADDITIONAL_HEADER -> {
                holder as AdditionalHeaderViewHolder
                val cooksName = (item.dish as Dish).cook?.getFullName()
                holder.body.text = "People who order from ${cooksName} usually added the following dishes"
            }
            VIEW_TYPE_ADDITIONAL -> {
                (holder as DishItemViewHolder).bind(item.dish as Dish, listener)
            }
        }

    }

    override fun onPlusMinusChange(counter: Int, position: Int) {
        var isOrderItemsEmpty = false
        val orderItem = (getItem(position).dish as OrderItem).copy()
        orderItem.quantity = counter
        if(counter == 0){
            val orderItems = currentList.filter { it.viewType == VIEW_TYPE_ORDER_ITEM }
            isOrderItemsEmpty = orderItems.size == 1
            orderItem._destroy = true
        }
        listener.onDishCountChange(orderItem, isOrderItemsEmpty)
    }

    class AdditionalHeaderViewHolder(binding: AdditionalDishItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val body: TextView = binding.additionalHeaderBody
    }

    class OrderItemViewHolder(binding: AdditionalDishesDialogItemOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
//        val binding = AdditionalDishesDialogItemOrderItemBinding.bind(view)
        val plusMinusView: PlusMinusView = binding.orderItemPlusMinus
        private val name: TextView = binding.orderItemName
        private val price: TextView = binding.orderItemPrice
        private val count: TextView = binding.orderItemCount
        private val img: ResizeableImageView = binding.orderItemImg

        fun bind(orderItem: OrderItem){
            price.text = orderItem.price.formatedValue
            count.text = "${orderItem.quantity}"
            img.loadResizableImage(orderItem.dish.thumbnail)
            name.text = orderItem.dish.name
        }
    }

    class DishItemViewHolder(binding: AdditionalDishesDialogItemDishBinding) : RecyclerView.ViewHolder(binding.root) {
//        val binding = AdditionalDishesDialogItemDishBinding.bind(view)
        private val addBtn: TextView = binding.additionalDishAddBtn
        private val price: TextView = binding.additionalDishPrice
        private val name: TextView = binding.additionalDishName
        private val count: TextView = binding.additionalDishCount
        private val img: ResizeableImageView = binding.additionalDishImg

        fun bind(dish: Dish, listener: AdditionalDishesAdapterListener){
            price.text = dish.price?.formatedValue
            count.text = "${dish.menuItem?.quantity ?: 0}"
            addBtn.setOnClickListener { listener.onAddBtnClick(dish) }
            img.setOnClickListener { listener.onDishClick(dish) }
            dish.thumbnail?.let{
                img.loadResizableImage(it)
            }
            name.text = dish.name
        }
    }

    class AdditionalDishesDiffCallback : DiffUtil.ItemCallback<AdditionalDishData<Any>>() {
        override fun areItemsTheSame(oldItem: AdditionalDishData<Any>, newItem: AdditionalDishData<Any>): Boolean {
            return false
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




}


