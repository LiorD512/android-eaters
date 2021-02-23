package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_ingredients

import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.managers.CartManager

class SingleDishIngredientViewModel(
    private val cartManager: CartManager,
) : ViewModel() {


    fun updateCurrentOrderItem(removedIngredients: List<Long>){
        cartManager.updateCurrentOrderItemRequest(OrderItemRequest(removedIngredientsIds = removedIngredients))
    }

    companion object{
        const val TAG = "wowSingleDishIngredientVM"
    }

}
