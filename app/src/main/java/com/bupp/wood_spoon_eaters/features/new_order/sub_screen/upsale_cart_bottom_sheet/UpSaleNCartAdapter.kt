package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.SwipeableBaseItemViewHolder
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter
import com.google.android.material.imageview.ShapeableImageView

class UpSaleNCartAdapter : SwipeableAdapter<CartBaseAdapterItem>(DiffCallback()) {

    interface CartAdapterListener{
        fun onCartBtnClicked()
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CartAdapterViewType.UPSALE_DISH.ordinal -> {
                val binding = UpSaleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UpSaleItemViewHolder(binding)
            }
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
            is UpsaleAdapterItem -> {
                holder as UpSaleItemViewHolder
                holder.bindItem(item)
            }
            is CartAdapterItem -> {
                holder as CartDishItemViewHolder
                holder.bindItem(item)
            }
            is CartAdapterSubTotalItem -> {
                holder as CartSubTotalItemViewHolder
                holder.bindItem(item)
            }
        }
    }

    class CartDishItemViewHolder(binding: CartItemDishBinding) : SwipeableBaseItemViewHolder(binding.root) {

        override val isSwipeable: Boolean = true

        private val name: TextView = binding.cartItemName
        private val quantity: TextView = binding.cartItemQuantity
        private val price: TextView = binding.cartItemPrice
        private val description: TextView = binding.cartItemDescription

        @SuppressLint("SetTextI18n")
        fun bindItem(dishItem: CartAdapterItem) {
            Log.d(TAG, "bindItem - cart dish")
            val dish = dishItem.menuItem?.dish
            dish?.let {
                name.text = dish.name
                quantity.text = "${dishItem.cartQuantity}"
                price.text = "$${dish.price?.formatedValue}"
                description.text = "$${dish.price?.formatedValue}" //todo - add note after we get the dish model from server
            }
        }

    }

    class UpSaleItemViewHolder(binding: UpSaleItemBinding) : SwipeableBaseItemViewHolder(binding.root) {

        override val isSwipeable: Boolean = true

        private val name: TextView = binding.upSaleItemName
        private val description: TextView = binding.upSaleItemDescription
        private val price: TextView = binding.upSaleItemPrice
        private val img: ShapeableImageView = binding.upSaleItemImg

        fun bindItem(dishItem: UpsaleAdapterItem) {
            Log.d(TAG,"bindItem - upsale")
            val dish = dishItem.menuItem?.dish
            dish?.let{
                name.text = dish.name
                description.text = dish.description
                price.text = "$${dish.price?.formatedValue} X${dishItem.cartQuantity}"
            }
        }

    }


    class CartSubTotalItemViewHolder(binding: CartItemSubtotalBinding) : SwipeableBaseItemViewHolder(binding.root) {

        override val isSwipeable: Boolean = false

        private val subtotal: TextView = binding.cartSubTotalItemPrice
//        private val cartBtn: BlueBtnCornered = binding.cartSubTotalItemBtn
        private val pb: ImageView = binding.cartSubTotalItemPb

        @SuppressLint("SetTextI18n")
        fun bindItem(dishItem: CartAdapterSubTotalItem) {
            Log.d(TAG, "bindItem - subtotal")
            subtotal.text = dishItem.subTotal

//            cartBtn.setOnClickListener {
//                listener.onCartBtnClicked()
//            }
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

    companion object {
        const val TAG = "wowCartAdapter"
    }

    override fun onDishSwipedAdd(item: CartBaseAdapterItem) {

    }

    override fun onDishSwipedRemove(item: CartBaseAdapterItem) {

    }
}