package com.bupp.wood_spoon_chef.presentation.features.new_dish.bottom_sheets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.databinding.ItemDishCategoryBinding
import com.bupp.wood_spoon_chef.data.remote.model.DishCategory
import java.util.*

class DishCategoryAdapter(val listener: DishCategoryAdapterListener) :
    ListAdapter<DishCategory, DishCategoryAdapter.ViewHolder>(DiffCallback()) {

    private var selectedItemPosition: Int? = null
    var selectedCategory: DishCategory? = null

    interface DishCategoryAdapterListener{
       fun onCategorySelected(dishCategory: DishCategory?, oldItemPosition: Int? = null)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDishCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            dishCategoryName.text = item.name.capitalize(Locale.ROOT)
            dishCategorySelectedBackground.isVisible = selectedCategory?.id == item.id
            root.setOnClickListener{
                if(selectedCategory?.id == item.id){
                    selectedCategory = null
                    dishCategorySelectedBackground.isVisible = false
                    listener.onCategorySelected(selectedCategory, selectedItemPosition)
                    selectedItemPosition = position
                } else {
                    selectedCategory = item
                    dishCategorySelectedBackground.isVisible = true
                    listener.onCategorySelected(selectedCategory, selectedItemPosition)
                    selectedItemPosition = position
                }
            }

        }
    }

    class ViewHolder(val binding: ItemDishCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<DishCategory>() {

        override fun areItemsTheSame(oldItem: DishCategory, newItem: DishCategory): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DishCategory, newItem: DishCategory): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

