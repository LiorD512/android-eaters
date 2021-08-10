package com.bupp.wood_spoon_eaters.features.restaurant

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageFragmentDirections
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.RestaurantInitParams
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish

class RestaurantMainViewModel : ViewModel() {

//    var currentRestaurant: RestaurantInitParams? = null
    val restaurantInitParamsLiveData = MutableLiveData<RestaurantInitParams>()
    fun initExtras(restaurantInitParams: RestaurantInitParams?) {
        restaurantInitParams?.let{
            restaurantInitParamsLiveData.postValue(it)
        }
    }


    val fragmentNavigationEvent = LiveEventData<NavDirections>()

    fun openDishPage(dish: Dish){
        val action = RestaurantPageFragmentDirections.actionRestaurantPageFragmentToDishPageFragment(dish)
        fragmentNavigationEvent.postRawValue(action)
    }


}
