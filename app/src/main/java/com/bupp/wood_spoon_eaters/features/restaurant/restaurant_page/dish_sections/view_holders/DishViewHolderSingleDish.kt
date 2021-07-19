package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders

import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.RestaurantItemDishBinding
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionSingleDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections


class DishViewHolderSingleDish(view: View) : DishesMainAdapter.BaseItemViewHolder(view) {
    val binding = RestaurantItemDishBinding.bind(view)

    override fun bind(section: DishSections, listener: DishesMainAdapter.RestaurantPageMainAdapterListener) {
        section as DishSectionSingleDish
        with(binding) {
            Glide.with(root.context).load(section.dish.thumbnail).into(dishPhoto)
            dishName.text = section.dish.name
            dishPrice.text = section.dish.getPriceObj()?.formatedValue
            dishDescription.text = section.dish.description
            dishSeparator.isVisible = !section.hideSeparator
        }
    }

}