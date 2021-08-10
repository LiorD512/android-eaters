package com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.DishItemAvailabilityBinding

class DishAvailabilityAdapter(private val listener: DishAvailabilityAdapterListener) : ListAdapter<String, RecyclerView.ViewHolder>(DiffCallback()) {

    interface DishAvailabilityAdapterListener {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DishItemAvailabilityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val item = getItem(position)
        holder.binding.apply {
            //Any listeners or binding goes here
        }
    }

    class ViewHolder(val binding: DishItemAvailabilityBinding) : RecyclerView.ViewHolder(binding.root)
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