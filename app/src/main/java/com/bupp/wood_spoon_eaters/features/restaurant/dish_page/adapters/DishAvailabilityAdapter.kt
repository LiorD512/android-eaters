package com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.DishItemAvailabilityBinding
import com.bupp.wood_spoon_eaters.model.AvailabilityDate

class DishAvailabilityAdapter :
    ListAdapter<AvailabilityDate, RecyclerView.ViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DishItemAvailabilityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val item = getItem(position)
        holder.binding.apply {
            itemAvailabilityDate.text = item.getStartEndAtTag()
        }
    }

    class ViewHolder(val binding: DishItemAvailabilityBinding) : RecyclerView.ViewHolder(binding.root)
    private class DiffCallback : DiffUtil.ItemCallback<AvailabilityDate>() {

        override fun areItemsTheSame(oldItem: AvailabilityDate, newItem: AvailabilityDate): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AvailabilityDate, newItem: AvailabilityDate): Boolean {
            return oldItem == newItem
        }
    }
}