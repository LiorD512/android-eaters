package com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter

import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem

//Multi section view models
data class OrderItems(val orderItems: List<OrderItem>)

data class AdditionalDishHeader(val cooksName: String)
data class AdditionalDishes(val dishes: ArrayList<Dish>)

