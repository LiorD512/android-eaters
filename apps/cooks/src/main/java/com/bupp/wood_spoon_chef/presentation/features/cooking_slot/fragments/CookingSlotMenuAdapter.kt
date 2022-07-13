package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.databinding.ItemSlotReviewMenuBinding
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.calendar_menu_adapter.MenuAdapterModel

class CookingSlotMenuAdapter(
) : ListAdapter<MenuAdapterModel,
            RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemSlotReviewMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MenuItemViewHolder).bind(getItem(position))
    }
}

class MenuItemViewHolder(
    val view: ItemSlotReviewMenuBinding
) : RecyclerView.ViewHolder(view.root) {

    fun bind(item: MenuAdapterModel) {
        view.tvTitle.text = item.name
        Glide.with(view.root.context)
            .load(item.img)
            .into(view.ivMenuPicture)
        view.counter.setCounter(item.quantity.toString())
    }
}

private class DiffCallback : DiffUtil.ItemCallback<MenuAdapterModel>() {
    override fun areItemsTheSame(oldItem: MenuAdapterModel, newItem: MenuAdapterModel) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: MenuAdapterModel, newItem: MenuAdapterModel) =
        oldItem == newItem
}