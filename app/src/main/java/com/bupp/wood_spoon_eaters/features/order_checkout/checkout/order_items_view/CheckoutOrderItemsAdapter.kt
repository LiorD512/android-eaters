package com.bupp.wood_spoon_eaters.features.order_checkout.checkout.order_items_view

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.CheckoutItemOrderItemBinding
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.models.CheckoutAdapterItem
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.SwipeableBaseItemViewHolder
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter
import java.text.DecimalFormat

class CheckoutOrderItemsAdapter(val listener: CheckoutOrderItemsAdapterListener?) : SwipeableAdapter<CheckoutAdapterItem>(DiffCallback()) {

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

    class OrderItemViewHolder(val binding: CheckoutItemOrderItemBinding) : SwipeableBaseItemViewHolder(binding.root) {

        override val isSwipeable: Boolean = true

        @SuppressLint("SetTextI18n")
        fun bindItem(item: CheckoutAdapterItem, listener: CheckoutOrderItemsAdapterListener?) {
            val orderItem = item.customOrderItem.orderItem
            with(binding) {
                val dish: Dish = orderItem.dish

                orderItemCounter.text = "${orderItem.quantity}"
                orderItemName.text = dish.name

                val price = orderItem.price.value ?: 0.0
                val priceStr = DecimalFormat("##.##").format(price)
                orderItemPrice.text = "$$priceStr"

                orderItemNote.isVisible = !orderItem.getNoteStr().isNullOrEmpty()
                if (!orderItem.getNoteStr().isNullOrEmpty()) {
                    val builder = SpannableStringBuilder(orderItem.getNoteStr())
                    builder.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(itemView.context, R.color.greyish_brown)),
                        0, 17,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    orderItemNote.text = builder
                }
                root.setOnClickListener {
                    listener?.onDishClicked(item.customOrderItem)
                }
            }
        }
    }

    override fun onDishSwipedAdd(item: CheckoutAdapterItem) {
        listener?.onDishSwipedAdd(item)
    }

    override fun onDishSwipedRemove(item: CheckoutAdapterItem) {
        listener?.onDishSwipedRemove(item)
    }


    class DiffCallback : DiffUtil.ItemCallback<CheckoutAdapterItem>() {
        override fun areItemsTheSame(oldItem: CheckoutAdapterItem, newItem: CheckoutAdapterItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CheckoutAdapterItem, newItem: CheckoutAdapterItem): Boolean {
            return oldItem == newItem
        }
    }

}
