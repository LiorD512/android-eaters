package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Restaurant
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import com.bupp.wood_spoon_eaters.utils.isSameDateAs
import java.util.*

class RestaurantPageViewModel(
    val feedDataManager: FeedDataManager,
    val feedRepository: FeedRepository,
) : ViewModel() {

    val restaurantData = MutableLiveData<Cook>()
    val restaurantFullData = MutableLiveData<Restaurant>()

    data class DeliveryDate(val date: Date, val cookingSlots: MutableList<CookingSlot>)
    val deliveryTiming = MutableLiveData<List<DeliveryDate>>()

    val dishesList = MutableLiveData<List<DishSections>>()

    val progressData = ProgressData()

    fun initData(cook: Cook?) {
        cook?.let {
            Log.d(TAG, "cook= $cook")
            restaurantData.postValue(it)
            handleFullRestaurantData(getRestaurantData())
        }
    }

    private fun handleFullRestaurantData(restaurant: Restaurant?) {
        Log.d(TAG, "Full restaurant= $restaurant")
        restaurant?.let {
            restaurantFullData.postValue(it)
            val restaurantSorted = sortCookingSlotsDishes(it)
            handleDeliveryTimingSection(restaurantSorted)
            handleDishesSection(restaurantSorted)
        }
    }

    private fun sortCookingSlotsDishes(restaurant: Restaurant): Restaurant {
        val dishesHash: Map<Long, Dish> = restaurant.dishes.associateBy({ it.id }, { it })
        restaurant.cookingSlots.forEach { cookingSlot ->
            val tempHash = dishesHash.toMutableMap()
            cookingSlot.menuItems?.forEach { menuItem ->
                dishesHash[menuItem.id]?.let { dish ->
                    cookingSlot.availableDishes.add(dish)
                    tempHash.remove(dish.id)
                }
            }
            cookingSlot.unAvailableDishes.addAll(tempHash.values.toList())
        }
        return restaurant
    }

    private fun handleDeliveryTimingSection(restaurant: Restaurant) {
        val deliveryDates = mutableListOf<DeliveryDate>()
        var currentDate: Date? = null
        restaurant.cookingSlots.forEach { cookingSlot ->
            if (currentDate == null) {
                currentDate = cookingSlot.startsAt
                deliveryDates.add(DeliveryDate(cookingSlot.startsAt, mutableListOf(cookingSlot)))
                return@forEach
            }
            if (cookingSlot.startsAt.isSameDateAs(currentDate)) {
                deliveryDates[deliveryDates.lastIndex].cookingSlots.add(cookingSlot)
            } else {
                deliveryDates.add(DeliveryDate(cookingSlot.startsAt, mutableListOf(cookingSlot)))
                currentDate = cookingSlot.startsAt
            }
        }
        deliveryTiming.postValue( deliveryDates)
    }

    private fun handleDishesSection(restaurant: Restaurant) {
        val dishSectionsList = mutableListOf<DishSections>()
        val currentCookingSlot = restaurant.cookingSlots.getOrNull(1)
        currentCookingSlot?.let{ cookingSlot->
            dishSectionsList.add(DishSectionAvailableHeader(""))
            cookingSlot.availableDishes.forEachIndexed(){ index, dish ->
                if( index == cookingSlot.availableDishes.size-1){
                    dishSectionsList.add(DishSectionSingleDish(dish,true))
                } else {
                    dishSectionsList.add(DishSectionSingleDish(dish))
                }
            }
            dishSectionsList.add(DishSectionUnavailableHeader())
            cookingSlot.unAvailableDishes.forEachIndexed(){ index, dish ->
                if( index == cookingSlot.unAvailableDishes.size-1){
                    dishSectionsList.add(DishSectionSingleDish(dish,true))
                } else {
                    dishSectionsList.add(DishSectionSingleDish(dish))
                }
            }
        }
       dishesList.postValue(dishSectionsList)
    }

    companion object {
        const val TAG = "wowRestaurantPageVM"
    }

}
