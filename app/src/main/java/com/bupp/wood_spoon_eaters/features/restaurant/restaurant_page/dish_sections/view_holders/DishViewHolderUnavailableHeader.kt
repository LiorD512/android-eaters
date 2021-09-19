package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders

import com.bupp.wood_spoon_eaters.databinding.RestaurantItemAvailableLaterBinding
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionUnavailableHeader
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections


class DishViewHolderUnavailableHeader(val binding: RestaurantItemAvailableLaterBinding) : DishesMainAdapter.BaseItemViewHolder(binding.root) {
    override val isSwipeable: Boolean = false

    override fun bind(section: DishSections, listener: DishesMainAdapter.DishesMainAdapterListener) {
        section as DishSectionUnavailableHeader
    }

}