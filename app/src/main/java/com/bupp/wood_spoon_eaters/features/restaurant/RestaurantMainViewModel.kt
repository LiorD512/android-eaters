package com.bupp.wood_spoon_eaters.features.restaurant

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageFragmentDirections
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish

class RestaurantMainViewModel : ViewModel() {

    var currentRestaurant: Cook? = null
    fun initExtras(restaurant: Cook?) {
        currentRestaurant = restaurant
    }
    val fragmentNavigationEvent = LiveEventData<NavDirections>()

    fun openDishPage(dish: Dish){
        val action = RestaurantPageFragmentDirections.actionRestaurantPageFragmentToDishPageFragment(dish)
        fragmentNavigationEvent.postRawValue(action)
    }


}
