package com.bupp.wood_spoon_eaters.features.upsale

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.UpSaleItemBinding
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.*
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.SwipeableBaseItemViewHolder
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter

class UpSaleAdapter(val listener: UpSaleAdapterListener) : SwipeableAdapter<UpsaleAdapterItem>(DiffCallback()) {

    interface UpSaleAdapterListener {
        fun onDishSwipedAdd(item: UpsaleAdapterItem)
        fun onDishSwipedRemove(item: UpsaleAdapterItem)
        fun onCartItemClicked(item: MenuItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = UpSaleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  UpSaleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder as UpSaleItemViewHolder
        holder.bindItem(item, listener)
    }

    class UpSaleItemViewHolder(binding: UpSaleItemBinding) :
        SwipeableBaseItemViewHolder(binding.root) {

        override val isSwipeable: Boolean = true
        private val mainLayout: ConstraintLayout = binding.upsaleItemMainLayout
        private val name: TextView = binding.upSaleItemName
        private val quantity: TextView = binding.upsaleItemQuantity
        private val description: TextView = binding.upSaleItemDescription
        private val price: TextView = binding.upSaleItemPrice
        private val img: ImageView = binding.upSaleItemImg

        fun bindItem(dishItem: UpsaleAdapterItem, listener: UpSaleAdapterListener) {
            val dish = dishItem.menuItem?.dish
            dish?.let {
                name.text = dish.name
                description.text = dish.description
                price.text = dish.price?.formatedValue
                Glide.with(itemView.context).load(dish.thumbnail?.url).into(img)
                quantity.text = dishItem.cartQuantity.toString()
                quantity.isVisible = dishItem.cartQuantity > 0
            }

            mainLayout.setOnClickListener {
                dishItem.menuItem?.let {
                    listener.onCartItemClicked(it)
                }
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<UpsaleAdapterItem>() {

        override fun areItemsTheSame(oldItem: UpsaleAdapterItem, newItem: UpsaleAdapterItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UpsaleAdapterItem, newItem: UpsaleAdapterItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onDishSwipedAdd(item: UpsaleAdapterItem) {
        listener.onDishSwipedAdd(item)
    }

    override fun onDishSwipedRemove(item: UpsaleAdapterItem) {
        listener.onDishSwipedRemove(item)
    }
}