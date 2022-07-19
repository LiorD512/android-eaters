package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.data.remote.model.MenuItem
import com.bupp.wood_spoon_chef.databinding.ItemSlotReviewMenuBinding

class CookingSlotMenuAdapter(
) : ListAdapter<MenuItem, RecyclerView.ViewHolder>(DiffCallback()) {

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

    fun bind(item: MenuItem) {
        view.tvTitle.text = item.dish.name
        Glide.with(view.root.context)
            .load(item.dish.imageGallery?.first())
            .centerCrop()
            .into(view.ivMenuPicture)
        view.tvPrice.text = item.dish.price?.formattedValue
        view.tvAmountOfOrder.text =  "${item.unitsSold}/${item.quantity} orders"

        view.counter.setCounter(item.quantity)
    }
}

private class DiffCallback : DiffUtil.ItemCallback<MenuItem>() {
    override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem) = oldItem == newItem

    override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem) = oldItem == newItem
}