package com.bupp.wood_spoon_eaters.bottom_sheets.upsale_bottom_sheet

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.GridStackableTextViewItemBinding
import com.bupp.wood_spoon_eaters.databinding.UpSaleItemBinding
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.utils.waitForLayout
import com.google.android.material.imageview.ShapeableImageView

class UpSaleAdapter :
    ListAdapter<UpSaleAdapterItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = UpSaleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UpSaleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as UpSaleItemViewHolder
        itemViewHolder.bindItem(item)
    }

    fun isInCart(position: Int): Boolean {
        return getItem(position).isInCart()
    }

    fun updateItemQuantityAdd(position: Int) {
//        val currentItem = getItem(position)
        getItem(position).quantity ++
    }

    fun updateItemQuantityRemoved(position: Int) {
        if(getItem(position).quantity > 0){
            getItem(position).quantity --
        }
    }

    class UpSaleItemViewHolder(binding: UpSaleItemBinding) : RecyclerView.ViewHolder(binding.root) {
//        private val motionLayout: ConstraintLayout = binding.upSaleItemMainLayout
//        private val dishLayout: ConstraintLayout = binding.upSaleItemLayout

        private val name: TextView = binding.upSaleItemName
        private val description: TextView = binding.upSaleItemDescription
        private val price: TextView = binding.upSaleItemPrice
        private val img: ShapeableImageView = binding.upSaleItemImg


        fun bindItem(dishItem: UpSaleAdapterItem) {
            Log.d(TAG,"bindItem")
            val dish = dishItem.dish
            name.text = dish.name
            description.text = dish.description
            price.text = "$69 X${dishItem.quantity}"
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