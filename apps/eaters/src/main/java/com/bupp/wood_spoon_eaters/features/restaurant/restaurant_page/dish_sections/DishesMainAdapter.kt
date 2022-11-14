package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders.*
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.SwipeableBaseItemViewHolder

class DishesMainAdapter(private val listener: DishesMainAdapterListener) :
    SwipeableAdapter<DishSections>(DiffCallback()) {

    interface DishesMainAdapterListener: DishViewHolderSingleDish.DishViewHolderSingleDishListener{
        fun onDishSwipedAdd(item: DishSectionSingleDish)
        fun onDishSwipedRemove(item: DishSectionSingleDish)
    }

    override fun onDishSwipedAdd(item: DishSections) {
        listener.onDishSwipedAdd(item as DishSectionSingleDish)
    }
    override fun onDishSwipedRemove(item: DishSections) {
        listener.onDishSwipedRemove(item as DishSectionSingleDish)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SwipeableBaseItemViewHolder {
        when (viewType) {
            DishSectionSingleDish.viewType.ordinal -> {
                val binding = RestaurantItemDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return DishViewHolderSingleDish(binding)
            }
            DishSectionAvailableHeader.viewType.ordinal -> {
                val binding = RestaurantItemDishesHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return DishViewHolderAvailableHeader(binding)
            }
            DishSectionUnavailableHeader.viewType.ordinal -> {
                val binding = RestaurantItemAvailableLaterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return DishViewHolderUnavailableHeader(binding)
            }
            DishSectionSkeleton.viewType.ordinal ->{
                val binding = ItemRestaurantDishSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return DishViewHolderSkeleton(binding)
            }
            DishSectionSearchEmpty.viewType.ordinal ->{
                val binding = ItemRestaurantDishSearchEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return DishViewHolderSearchEmpty(binding)
            }
        }
       throw Exception("Specify a ViewHolder for given viewType : viewType = $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val section = getItem(position)) {
            is DishSectionSingleDish -> {
                holder as DishViewHolderSingleDish
                holder.bind(section, listener)
            }
            is DishSectionAvailableHeader -> {
                holder as DishViewHolderAvailableHeader
                holder.bind(section, listener)
            }
            is DishSectionUnavailableHeader -> {
                holder as DishViewHolderUnavailableHeader
                holder.bind(section, listener)
            }
            is DishSectionSkeleton -> {
                holder as DishViewHolderSkeleton
                holder.bind(section, listener)
            }
            else -> {}
        }
    }

    abstract class BaseItemViewHolder(view: View) : SwipeableBaseItemViewHolder(view) {
        abstract override val isSwipeable: Boolean

        abstract fun bind(
            section: DishSections,
            listener: DishesMainAdapterListener
        )
    }

    private class DiffCallback : DiffUtil.ItemCallback<DishSections>() {

        override fun areItemsTheSame(
            oldItem: DishSections,
            newItem: DishSections
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DishSections,
            newItem: DishSections
        ): Boolean {
            return oldItem == newItem
        }
    }
}