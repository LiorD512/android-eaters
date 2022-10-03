package com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.CartItemDishBinding
import com.bupp.wood_spoon_eaters.databinding.CartItemSubtotalBinding
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.SwipeableBaseItemViewHolder
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter

class CartAdapter(val listener: CartAdapterListener) : SwipeableAdapter<CartBaseAdapterItem>(DiffCallback()) {

    interface CartAdapterListener {
        fun onDishSwipedAdd(cartBaseAdapterItem: CartBaseAdapterItem)
        fun onDishSwipedRemove(cartBaseAdapterItem: CartBaseAdapterItem)
        fun onCartItemClicked(customCartItem: CustomOrderItem)
    }

    override fun onDishSwipedAdd(item: CartBaseAdapterItem) {
        listener.onDishSwipedAdd(item)
    }

    override fun onDishSwipedRemove(item: CartBaseAdapterItem) {
        listener.onDishSwipedRemove(item)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CartAdapterViewType.CART_DISH.ordinal -> {
                val binding = CartItemDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CartDishItemViewHolder(binding)
            }
            else -> { //CartAdapterViewType.SUB_TOTAL.ordinal
                val binding = CartItemSubtotalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CartSubTotalItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is CartAdapterItem -> {
                holder as CartDishItemViewHolder
                holder.bindItem(item, listener)
            }
            is CartAdapterSubTotalItem -> {
                holder as CartSubTotalItemViewHolder
                holder.bindItem(item)
            }
        }
    }

    class CartDishItemViewHolder(binding: CartItemDishBinding) : SwipeableBaseItemViewHolder(binding.root) {

        override val isSwipeable: Boolean = true

        private val mainLayout: ConstraintLayout = binding.cartItemMainLayout
        private val name: TextView = binding.cartItemName
        private val quantity: TextView = binding.cartItemQuantity
        private val price: TextView = binding.cartItemPrice
        private val description: TextView = binding.cartItemDescription

        @SuppressLint("SetTextI18n")
        fun bindItem(dishItem: CartAdapterItem, listener: CartAdapterListener) {
            val customCartItem = dishItem.customOrderItem.orderItem
            customCartItem.let {
                name.text = it.dish.name
                quantity.text = "${it.quantity}"
                price.text = "${it.price.formatedValue}"

                description.text = it.notes
                description.isVisible = !it.notes.isNullOrEmpty()

            }

            mainLayout.setOnClickListener {
                listener.onCartItemClicked(dishItem.customOrderItem)
            }
        }

    }

    class CartSubTotalItemViewHolder(binding: CartItemSubtotalBinding) : SwipeableBaseItemViewHolder(binding.root) {
        override val isSwipeable: Boolean = false
        private val subtotal: TextView = binding.cartSubTotalItemPrice
        @SuppressLint("SetTextI18n")
        fun bindItem(dishItem: CartAdapterSubTotalItem) {
            subtotal.text = dishItem.subTotal
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CartBaseAdapterItem>() {

        override fun areItemsTheSame(oldItem: CartBaseAdapterItem, newItem: CartBaseAdapterItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CartBaseAdapterItem, newItem: CartBaseAdapterItem): Boolean {
            return oldItem.type == newItem.type
        }
    }
}