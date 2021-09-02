package com.bupp.wood_spoon_eaters.features.order_checkout.checkout.order_items_view

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.CheckoutItemOrderItemBinding
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapterItem
import java.text.DecimalFormat

data class CheckoutAdapterItem(
    val customOrderItem: CustomOrderItem,
    override var cartQuantity: Int = 0,
    override val menuItem: MenuItem? = null,
    override val isSwipeable: Boolean = true
): SwipeableAdapterItem()

class CheckoutOrderItemsAdapter(val listener: CheckoutOrderItemsAdapterListener) : SwipeableAdapter<CheckoutAdapterItem>(DiffCallback()) {

    interface CheckoutOrderItemsAdapterListener {
        fun onDishSwipedAdd(item: CheckoutAdapterItem)
        fun onDishSwipedRemove(item: CheckoutAdapterItem)
        fun onDishClicked(dishItem: CustomOrderItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CheckoutItemOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder as OrderItemViewHolder
        holder.bindItem(item, listener)

    }

    class OrderItemViewHolder(val binding: CheckoutItemOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindItem(item: CheckoutAdapterItem, listener: CheckoutOrderItemsAdapterListener) {
            val orderItem = item.customOrderItem.orderItem
            with(binding) {
                val dish: Dish = orderItem.dish

                orderItemCounter.text = "${item.cartQuantity}"
                orderItemName.text = dish.name

                var price = 0.0
                orderItem.price.value?.let {
                    price = it

                }
                val priceStr = DecimalFormat("##.##").format(price)
                orderItemPrice.text = "$$priceStr"

                if (!orderItem.getNoteStr().isNullOrEmpty()) {
                    orderItemNote.visibility = View.VISIBLE
                    val builder = SpannableStringBuilder(orderItem.getNoteStr())
                    builder.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(itemView.context, R.color.greyish_brown)),
                        0, 17,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    orderItemNote.text = builder
                }
                root.setOnClickListener {
                    listener.onDishClicked(item.customOrderItem)
                }
            }
        }
    }

    override fun onDishSwipedAdd(item: CheckoutAdapterItem) {
        listener.onDishSwipedAdd(item)
    }

    override fun onDishSwipedRemove(item: CheckoutAdapterItem) {
        listener.onDishSwipedRemove(item)
    }


    class DiffCallback : DiffUtil.ItemCallback<CheckoutAdapterItem>() {
        override fun areItemsTheSame(oldItem: CheckoutAdapterItem, newItem: CheckoutAdapterItem): Boolean {
            return oldItem.customOrderItem.orderItem.id == newItem.customOrderItem.orderItem.id
        }

        override fun areContentsTheSame(oldItem: CheckoutAdapterItem, newItem: CheckoutAdapterItem): Boolean {
            return oldItem == newItem
        }
    }

}
