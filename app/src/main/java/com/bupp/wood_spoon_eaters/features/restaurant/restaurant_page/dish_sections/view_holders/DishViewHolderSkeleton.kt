package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders

import androidx.core.view.isVisible
import com.bupp.wood_spoon_eaters.databinding.ItemRestaurantDishSkeletonBinding
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections


class DishViewHolderSkeleton(val binding: ItemRestaurantDishSkeletonBinding) : DishesMainAdapter.BaseItemViewHolder(binding.root) {

    override val isSwipeable: Boolean = false
    override fun bind(section: DishSections, listener: DishesMainAdapter.DishesMainAdapterListener) {
        if(absoluteAdapterPosition == 0){
            binding.dishHeader.isVisible = true
        }
    }

}