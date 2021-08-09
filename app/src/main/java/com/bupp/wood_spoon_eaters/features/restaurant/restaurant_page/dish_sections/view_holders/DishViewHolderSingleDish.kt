package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.view_holders

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.RestaurantItemDishBinding
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionSingleDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSections
import com.bupp.wood_spoon_eaters.model.Dish


class DishViewHolderSingleDish(val binding: RestaurantItemDishBinding) : DishesMainAdapter.BaseItemViewHolder(binding.root) {

    interface DishViewHolderSingleDishListener{
        fun onDishClick(dish: Dish)
    }

    override fun bind(section: DishSections, listener: DishesMainAdapter.DishesMainAdapterListener) {
        section as DishSectionSingleDish
        val dish = section.menuItem.dish
        with(binding) {
            dish?.let{ dish->
                Glide.with(root.context).load(dish.thumbnail).into(dishPhoto)
                dishName.text = dish.name
                dishPrice.text = dish.getPriceObj()?.formatedValue
                dishDescription.text = dish.description

                dishQuantity.text = section.quantity.toString()
                dishQuantity.isVisible = section.quantity > 0
                root.setOnClickListener(){
                    listener.onDishClick(dish)
                }
            }
        }
    }

}