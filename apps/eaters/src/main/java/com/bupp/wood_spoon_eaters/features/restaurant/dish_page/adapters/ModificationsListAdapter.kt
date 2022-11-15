package com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.DishItemModificationBinding

class ModificationsListAdapter : ListAdapter<String, RecyclerView.ViewHolder>(DiffCallback()) {

    interface ModificationsListAdapterListener {
        fun onModificationClick()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            DishItemModificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val item = getItem(position)
        holder.binding.apply {
            modificationItemName.text = item
            root.setOnClickListener {
                modificationItemCheckbox.isSelected = !modificationItemCheckbox.isSelected
            }
        }
    }

    class ViewHolder(val binding: DishItemModificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class DiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
//            return oldItem.id == oldItem.id
            return oldItem == newItem
        }
    }
}