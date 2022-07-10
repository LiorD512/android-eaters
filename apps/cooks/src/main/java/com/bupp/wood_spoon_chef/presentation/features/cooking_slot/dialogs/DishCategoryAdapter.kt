package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.data.remote.model.DishCategory
import com.bupp.wood_spoon_chef.databinding.ListItemDishCategoryBinding
import com.bupp.wood_spoon_chef.utils.extensions.show
import java.util.*

class DishCategoryAdapter(private val listener: DishCategoryListener) : ListAdapter<DishCategory, DishCategoryAdapter.ViewHolder>(
    DiffCallback()
) {

    private var selectedItemPosition: Int? = null
    private var selectedCategory: DishCategory? = null

    interface DishCategoryListener{
        fun onCategorySelected(dishCategory: DishCategory?, oldItemPosition: Int? = null)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemDishCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            listItemDishCategoryName.text = item.name.replaceFirstChar {
                it.titlecase(
                    Locale.ROOT
                )
            }
            listItemDishCategorySelectedImg.show(selectedCategory?.id == item.id, true)
            root.setOnClickListener{
                if(selectedCategory?.id == item.id){
                    selectedCategory = null
                    listItemDishCategorySelectedImg.show(false, useInvisible = true)
                    listener.onCategorySelected(selectedCategory, selectedItemPosition)
                    selectedItemPosition = position
                } else {
                    selectedCategory = item
                    listItemDishCategorySelectedImg.show(true)
                    listener.onCategorySelected(selectedCategory, selectedItemPosition)
                    selectedItemPosition = position
                }
            }

        }
    }

    fun clearSelection(){
        selectedCategory = null
        selectedItemPosition = null
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ListItemDishCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    private class DiffCallback : DiffUtil.ItemCallback<DishCategory>() {
        override fun areItemsTheSame(oldItem: DishCategory, newItem: DishCategory) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: DishCategory, newItem: DishCategory) =
            oldItem == newItem
    }
}