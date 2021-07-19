package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders

import android.view.View
import com.bupp.wood_spoon_eaters.databinding.RestaurantItemDishBinding
import com.bupp.wood_spoon_eaters.databinding.RestaurantItemDishesHeaderBinding
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionAvailableHeader
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionSingleDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections


class DishViewHolderAvailableHeader(view: View) : DishesMainAdapter.BaseItemViewHolder(view) {
    val binding = RestaurantItemDishesHeaderBinding.bind(view)

    override fun bind(section: DishSections, listener: DishesMainAdapter.RestaurantPageMainAdapterListener) {
        section as DishSectionAvailableHeader
        with(binding) {
            dishHeader.text = section.header
        }
    }

}