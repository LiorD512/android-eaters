package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.Cook
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
    val deliveryTiming = MutableLiveData<List<DeliveryDate>>()

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
            deliveryTiming.postValue(handleDeliveryTimingSection(restaurantSorted))
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

    private fun handleDeliveryTimingSection(restaurant: Restaurant):List<DeliveryDate> {
        val deliveryDates = mutableListOf<DeliveryDate>()
        var currentDate: Date? = null
        restaurant.cookingSlots.forEach { cookingSlot ->
            if (currentDate == null) {
                currentDate = cookingSlot.startsAt
                deliveryDates.add(DeliveryDate(cookingSlot.startsAt, mutableListOf(cookingSlot)))
                return@forEach
            }
            if (cookingSlot.startsAt.isSameDateAs(currentDate)) {
                deliveryDates[deliveryDates.lastIndex].cookSlots.add(cookingSlot)
            } else {
                deliveryDates.add(DeliveryDate(cookingSlot.startsAt, mutableListOf(cookingSlot)))
                currentDate = cookingSlot.startsAt
            }
        }
        return deliveryDates
    }

    private fun handleDishesSection(restaurant: Restaurant): RPSectionDishList {
        val dishSectionsList = mutableListOf<DishListSections>()
        val currentCookingSlot = restaurant.cookingSlots.getOrNull(0)
        currentCookingSlot?.let{ cookingSlot->
            cookingSlot.availableDishes.forEach{
                dishSectionsList.add(DishSectionSingleDish(it))
            }
            dishSectionsList.add(DishSectionUnavailableHeader())
            cookingSlot.unAvailableDishes.forEach{
                dishSectionsList.add(DishSectionSingleDish(it))
            }
        }
        return RPSectionDishList(dishSectionsList)
    }

    companion object {
        const val TAG = "wowRestaurantPageVM"
    }

}
