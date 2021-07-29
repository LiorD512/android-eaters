package com.bupp.wood_spoon_eaters.features.restaurant.dish_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish

class DishPageViewModel : ViewModel() {
    val dishData = MutableLiveData<Dish>()
    val dishFullData = MutableLiveData<Dish>()

    fun initData(dish: Dish) {
        dishData.postValue(dish)
    }

}
