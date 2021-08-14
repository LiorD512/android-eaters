package com.bupp.wood_spoon_eaters.features.restaurant

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageFragmentDirections
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishInitParams
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.RestaurantInitParams
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.MenuItem

class RestaurantMainViewModel : ViewModel() {

//    var currentRestaurant: RestaurantInitParams? = null
    val restaurantInitParamsLiveData = LiveEventData<RestaurantInitParams>()
    fun initExtras(restaurantInitParams: RestaurantInitParams?) {
        restaurantInitParams?.let{
            restaurantInitParamsLiveData.postRawValue(it)
        }
    }


    val fragmentNavigationEvent = LiveEventData<NavDirections>()

    fun openDishPage(menuItem: MenuItem, curCookingSlot: CookingSlot?){
        val extras = DishInitParams(menuItem = menuItem, cookingSlot = curCookingSlot)
        val action = RestaurantPageFragmentDirections.actionRestaurantPageFragmentToDishPageFragment(extras)
        fragmentNavigationEvent.postRawValue(action)
    }


}
