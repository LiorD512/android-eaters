package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders

import android.view.View
import androidx.core.view.isVisible
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterTitleItemBinding
import com.bupp.wood_spoon_eaters.databinding.ItemRestaurantDishSkeletonBinding
import com.bupp.wood_spoon_eaters.databinding.RestaurantItemAvailableLaterBinding
import com.bupp.wood_spoon_eaters.databinding.RestaurantItemDishBinding
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionAvailableHeader
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionSingleDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionUnavailableHeader
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections


class DishViewHolderSkeleton(val binding: ItemRestaurantDishSkeletonBinding) : DishesMainAdapter.BaseItemViewHolder(binding.root) {

    override fun bind(section: DishSections, listener: DishesMainAdapter.RestaurantPageMainAdapterListener) {
        if(layoutPosition == 0){
            binding.dishHeader.isVisible = true
        }
    }

}