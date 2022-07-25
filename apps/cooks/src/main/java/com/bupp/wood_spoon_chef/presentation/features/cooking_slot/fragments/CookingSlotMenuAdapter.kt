package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_chef.databinding.ItemSlotReviewMenuBinding
import com.bupp.wood_spoon_chef.databinding.ItemSlotSectionHeaderBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.MenuDishItem
import java.lang.RuntimeException

sealed class CookingSlotMenuAdapterItem(open val type: CookingSlotMenuAdapterType) {

    class MenuAdapterItem(
        val menuItem: MenuDishItem,
        override val type: CookingSlotMenuAdapterType = CookingSlotMenuAdapterType.TYPE_MENU_ITEM
    ) : CookingSlotMenuAdapterItem(type)

    class SectionHeaderAdapterItem(
        val sectionTitle: String,
        override val type: CookingSlotMenuAdapterType = CookingSlotMenuAdapterType.TYPE_SECTION_HEADER
    ) : CookingSlotMenuAdapterItem(type)

    enum class CookingSlotMenuAdapterType {
        TYPE_SECTION_HEADER,
        TYPE_MENU_ITEM
    }
}

class CookingSlotMenuAdapter :
    ListAdapter<CookingSlotMenuAdapterItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CookingSlotMenuAdapterItem.CookingSlotMenuAdapterType.TYPE_MENU_ITEM.ordinal -> {
                MenuItemViewHolder(
                    ItemSlotReviewMenuBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            CookingSlotMenuAdapterItem.CookingSlotMenuAdapterType.TYPE_SECTION_HEADER.ordinal -> {
                CategoryDishHeaderViewHolder(
                    ItemSlotSectionHeaderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                throw RuntimeException("Unsupported item type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position).type) {
            CookingSlotMenuAdapterItem.CookingSlotMenuAdapterType.TYPE_MENU_ITEM -> {
                (holder as MenuItemViewHolder).bind(
                    item = (getItem(position) as CookingSlotMenuAdapterItem.MenuAdapterItem)
                )
            }
            CookingSlotMenuAdapterItem.CookingSlotMenuAdapterType.TYPE_SECTION_HEADER -> {
                (holder as CategoryDishHeaderViewHolder).bind(
                    item = (getItem(position) as CookingSlotMenuAdapterItem.SectionHeaderAdapterItem)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal
}

class MenuItemViewHolder(
    val view: ItemSlotReviewMenuBinding
) : RecyclerView.ViewHolder(view.root) {

    fun bind(item: CookingSlotMenuAdapterItem.MenuAdapterItem) {
        view.tvTitle.text = item.menuItem.dish?.name
        Glide.with(view.root.context)
            .load(item.menuItem.dish?.imageGallery?.first())
            .centerCrop()
            .into(view.ivMenuPicture)
        view.tvPrice.text = item.menuItem.dish?.price?.formattedValue
        view.tvAmountOfOrder.text = "${item.menuItem.unitsSold}/${item.menuItem.quantity} orders"

        view.counter.setCounter(item.menuItem.quantity)
    }
}

class CategoryDishHeaderViewHolder(
    val view: ItemSlotSectionHeaderBinding
) : RecyclerView.ViewHolder(view.root) {

    fun bind(item: CookingSlotMenuAdapterItem.SectionHeaderAdapterItem) {
        view.tvSectionTitle.text = item.sectionTitle.replaceFirstChar { it.uppercase() }
    }
}

private class DiffCallback : DiffUtil.ItemCallback<CookingSlotMenuAdapterItem>() {
    override fun areItemsTheSame(
        oldItem: CookingSlotMenuAdapterItem,
        newItem: CookingSlotMenuAdapterItem
    ) =
        oldItem == newItem

    override fun areContentsTheSame(
        oldItem: CookingSlotMenuAdapterItem,
        newItem: CookingSlotMenuAdapterItem
    ) =
        oldItem == newItem
}