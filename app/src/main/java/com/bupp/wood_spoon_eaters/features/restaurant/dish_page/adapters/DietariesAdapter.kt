package com.bupp.wood_spoon_eaters.features.restaurant.dish_page.adapters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.DishItemAvailabilityBinding
import com.bupp.wood_spoon_eaters.databinding.DishItemDietaryBinding
import com.bupp.wood_spoon_eaters.model.CuisineLabel
import com.bupp.wood_spoon_eaters.model.DietaryIcon

class DietariesAdapter: ListAdapter<DietaryIcon, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DishItemDietaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val item = getItem(position)
        holder.binding.apply {
            Glide.with(dietaryIcon).load(item.icon)
            dietaryName.text = item.name
        }
    }

    class ViewHolder(val binding: DishItemDietaryBinding) : RecyclerView.ViewHolder(binding.root)
    private class DiffCallback : DiffUtil.ItemCallback<DietaryIcon>() {

        override fun areItemsTheSame(oldItem: DietaryIcon, newItem: DietaryIcon): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DietaryIcon, newItem: DietaryIcon): Boolean {
            return oldItem.id == oldItem.id
        }
    }
}