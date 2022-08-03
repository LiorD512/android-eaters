package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.databinding.ListItemDishCategoryBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.FilterAdapterSectionModel
import com.bupp.wood_spoon_chef.utils.extensions.show
import java.util.*

class FilterMenuAdapter(private val listener: FilterMenuAdapterListener) : ListAdapter<FilterAdapterSectionModel, FilterMenuAdapter.ViewHolder>(
    DiffCallback()
) {

    interface FilterMenuAdapterListener{
        fun onSectionSelected(sectionName: String, isSelected: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemDishCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            listItemDishCategoryName.text = item.sectionName.replaceFirstChar {
                it.titlecase(
                    Locale.ROOT
                )
            }
            listItemDishCategorySelectedImg.show(item.isSelected, true)
            var isItemSelected: Boolean
            root.setOnClickListener{
                isItemSelected = !item.isSelected
                listener.onSectionSelected(sectionName = item.sectionName, isSelected = isItemSelected)
            }
        }
    }

    class ViewHolder(val binding: ListItemDishCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    private class DiffCallback : DiffUtil.ItemCallback<FilterAdapterSectionModel>() {
        override fun areItemsTheSame(oldItem: FilterAdapterSectionModel, newItem: FilterAdapterSectionModel) =
            oldItem.sectionName == newItem.sectionName

        override fun areContentsTheSame(oldItem: FilterAdapterSectionModel, newItem: FilterAdapterSectionModel) =
            oldItem == newItem
    }
}