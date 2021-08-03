package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.sub_screens.upsale

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.UpSaleItemBinding
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.UpSaleAdapterItem
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.SwipeableBaseItemViewHolder
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter
import com.google.android.material.imageview.ShapeableImageView

class UpSaleAdapter : SwipeableAdapter<UpSaleAdapterItem>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = UpSaleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UpSaleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as UpSaleItemViewHolder
        itemViewHolder.bindItem(item)
    }

    class UpSaleItemViewHolder(binding: UpSaleItemBinding) : SwipeableBaseItemViewHolder(binding.root) {

        override val isSwipeable: Boolean = true

        private val name: TextView = binding.upSaleItemName
        private val description: TextView = binding.upSaleItemDescription
        private val price: TextView = binding.upSaleItemPrice
        private val img: ShapeableImageView = binding.upSaleItemImg

        fun bindItem(dishItem: UpSaleAdapterItem) {
            Log.d(TAG,"bindItem")
            val dish = dishItem.dish
            name.text = dish.name
            description.text = dish.description
            price.text = "$${dish.price?.formatedValue} X${dishItem.quantity}"
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<UpSaleAdapterItem>() {

        override fun areItemsTheSame(oldItem: UpSaleAdapterItem, newItem: UpSaleAdapterItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UpSaleAdapterItem, newItem: UpSaleAdapterItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object{
        const val TAG = "wowUpsaleAdapter"
    }
}