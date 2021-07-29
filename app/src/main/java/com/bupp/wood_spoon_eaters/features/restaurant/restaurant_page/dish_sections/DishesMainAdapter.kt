package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders.DishViewHolderSingleDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders.DishViewHolderAvailableHeader
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders.DishViewHolderSkeleton
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders.DishViewHolderUnavailableHeader

class DishesMainAdapter(private val listener: RestaurantPageMainAdapterListener) :
    SwipeableAdapter<DishSections>(DiffCallback()) {

    interface RestaurantPageMainAdapterListener {}

    override fun getItemViewType(position: Int): Int = getItem(position).viewType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseItemViewHolder {
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
        }
    }


    abstract class BaseItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(
            section: DishSections,
            listener: RestaurantPageMainAdapterListener
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
            return oldItem.viewType == oldItem.viewType
        }
    }
}