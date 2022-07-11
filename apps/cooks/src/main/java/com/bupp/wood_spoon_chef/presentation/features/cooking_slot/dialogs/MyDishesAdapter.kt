package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.databinding.ListItemDishSelectionBinding

class MyDishesAdapter() : ListAdapter<Dish, RecyclerView.ViewHolder>(
    DiffCallback())
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemDishSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DishItemSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder as DishItemSelectionViewHolder
        holder.bind(item)
    }

    class DishItemSelectionViewHolder(val binding: ListItemDishSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dish: Dish) {
            binding.apply {
                listItemDishSelectionDishName.text = dish.name
                listItemDishSelectionDishPrice.text = dish.price?.formattedValue
                Glide.with(binding.root.context).load(dish.imageGallery?.first())
                    .into(listItemDishSelectionImage)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Dish>() {
        override fun areItemsTheSame(oldItem: Dish, newItem: Dish) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Dish, newItem: Dish) =
            oldItem == newItem
    }
}