package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.adapters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.RestaurantItemCuisineBinding
import com.bupp.wood_spoon_eaters.model.CuisineLabel

class RPAdapterCuisine() : ListAdapter<CuisineLabel, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item_cuisine, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val item = getItem(position)
        holder.binding.apply {
            cuisineName.text = item.name
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = RestaurantItemCuisineBinding.bind(itemView.rootView)
    }
    private class DiffCallback : DiffUtil.ItemCallback<CuisineLabel>() {

        override fun areItemsTheSame(oldItem: CuisineLabel, newItem: CuisineLabel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CuisineLabel, newItem: CuisineLabel): Boolean {
            return oldItem.id == oldItem.id
        }
    }
}