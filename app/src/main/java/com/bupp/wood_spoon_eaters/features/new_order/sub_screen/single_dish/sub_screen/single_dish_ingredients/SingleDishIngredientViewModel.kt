package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_ingredients

import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.managers.OldCartManager

class SingleDishIngredientViewModel(
    private val oldCartManager: OldCartManager,
) : ViewModel() {


    fun updateCurrentOrderItem(removedIngredients: List<Long>){
        oldCartManager.updateCurrentOrderItemRequest(OrderItemRequest(removedIngredientsIds = removedIngredients))
    }

    companion object{
        const val TAG = "wowSingleDishIngredientVM"
    }

}
