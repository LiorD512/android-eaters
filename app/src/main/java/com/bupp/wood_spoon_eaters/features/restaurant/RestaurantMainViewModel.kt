package com.bupp.wood_spoon_eaters.features.restaurant

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.model.Cook

class RestaurantMainViewModel : ViewModel() {

    var currentRestaurant: Cook? = null
    fun initExtras(restaurant: Cook?) {
        currentRestaurant = restaurant
    }


}
