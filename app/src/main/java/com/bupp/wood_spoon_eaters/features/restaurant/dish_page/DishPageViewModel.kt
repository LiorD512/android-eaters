package com.bupp.wood_spoon_eaters.features.restaurant.dish_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.databinding.QuantityViewBinding
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish

class DishPageViewModel : ViewModel() {
    val dishData = MutableLiveData<Dish>()
    val dishFullData = MutableLiveData<Dish>()

    var dishQuantity = 0
    var dishMaxQuantity = 5

    val dishQuantityState = MutableLiveData<DishQuantityState>()
    enum class DishQuantityState{
        ZERO_QUANTITY,
        REGULAR,
        MAX_QUANTITY
    }

    fun initData(dish: Dish) {
        dishData.postValue(dish)
        handleFullDish()
        updateDishQuantity(0)
    }

    fun handleFullDish(){
        handleModificationsData()
        handleDishAvailabilityData()
    }

    val modificationsData = MutableLiveData<List<String>>()
    fun handleModificationsData(){
        modificationsData.postValue(listOf<String>("Extra Cheese","No Salt","Yogurt on the side","Vegan Cheese"))
    }

    val dishAvailabilityData = MutableLiveData<List<String>>()
    fun handleDishAvailabilityData(){
        dishAvailabilityData.postValue(listOf<String>("06/21   Mon   2pm - 5pm","06/22  Tue   2pm - 5pm"))
    }

    fun updateDishQuantity(quantity: Int){
        dishQuantity = quantity
        when (quantity) {
            0 -> {
                dishQuantityState.postValue(DishQuantityState.ZERO_QUANTITY)
            }
            dishMaxQuantity -> {
                dishQuantityState.postValue(DishQuantityState.MAX_QUANTITY)
            }
            else -> {
                dishQuantityState.postValue(DishQuantityState.REGULAR)
            }
        }
    }

}
