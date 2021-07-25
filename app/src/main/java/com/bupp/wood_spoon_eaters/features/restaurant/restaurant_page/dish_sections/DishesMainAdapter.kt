package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders.DishViewHolderSingleDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders.DishViewHolderAvailableHeader
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders.DishViewHolderUnavailableHeader

class DishesMainAdapter(private val listener: RestaurantPageMainAdapterListener) :
    ListAdapter<DishSections, RecyclerView.ViewHolder>(DiffCallback()) {

    interface RestaurantPageMainAdapterListener {}

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseItemViewHolder {
        when (viewType) {
            DishSectionSingleDish.viewType -> {
                return DishViewHolderSingleDish(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.restaurant_item_dish, parent, false)
                )
            }
            DishSectionAvailableHeader.viewType -> {
                return DishViewHolderAvailableHeader(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.restaurant_item_dishes_header, parent, false)
                )
            }
            DishSectionUnavailableHeader.viewType -> {
                return DishViewHolderUnavailableHeader(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.restaurant_item_available_later, parent, false)
                )
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