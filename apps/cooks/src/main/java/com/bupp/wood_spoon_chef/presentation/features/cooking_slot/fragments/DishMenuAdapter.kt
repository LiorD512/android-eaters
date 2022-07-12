package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.databinding.ListItemMenuDishBinding

class DishMenuAdapter(private val listener: DishesMenuAdapterListener) :
    ListAdapter<Dish, RecyclerView.ViewHolder>(
        DiffCallback()
    ) {

    interface DishesMenuAdapterListener {
        fun onDeleteClick(dish: Dish, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ListItemMenuDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DishMenuItemViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder as DishMenuItemViewHolder
        holder.bind(item, listener)
    }

    class DishMenuItemViewHolder(val binding: ListItemMenuDishBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dish: Dish, listener: DishesMenuAdapterListener) {
            binding.apply {
                var quantity = 0
                listItemDishMenuDishName.text = dish.name
                listItemDishMenuDishPrice.text = dish.price?.formattedValue
                listItemMenuDishQuantityNumber.text = quantity.toString()
                Glide.with(binding.root.context).load(dish.imageGallery?.first())
                    .into(listItemDishMenuImage)
                listItemDishMenuDeleteDish.setOnClickListener {
                    listener.onDeleteClick(dish, absoluteAdapterPosition)
                }
                listItemMenuDishQuantityMinus.setOnClickListener {
                    if (quantity != 0){
                        quantity -= 1
                        listItemMenuDishQuantityNumber.text = quantity.toString()
                    }
                }
                listItemMenuDishQuantityPlus.setOnClickListener {
                    quantity += 1
                    listItemMenuDishQuantityNumber.text = quantity.toString()
                }
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