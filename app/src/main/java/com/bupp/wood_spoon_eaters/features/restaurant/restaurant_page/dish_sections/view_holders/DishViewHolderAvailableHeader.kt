package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders

import com.bupp.wood_spoon_eaters.databinding.RestaurantItemDishesHeaderBinding
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionAvailableHeader
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections


class DishViewHolderAvailableHeader(val binding: RestaurantItemDishesHeaderBinding) : DishesMainAdapter.BaseItemViewHolder(binding.root) {
    override val isSwipeable: Boolean = false

    override fun bind(section: DishSections, listener: DishesMainAdapter.DishesMainAdapterListener) {
        section as DishSectionAvailableHeader
        with(binding) {
            dishHeader.text = section.header
        }
    }

}