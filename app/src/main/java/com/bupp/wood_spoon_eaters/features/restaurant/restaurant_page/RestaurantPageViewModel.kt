package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import com.bupp.wood_spoon_eaters.utils.isSameDateAs
import java.util.*

class RestaurantPageViewModel(
    val feedDataManager: FeedDataManager,
    val feedRepository: FeedRepository,
) : ViewModel() {

    var currentSelectedDate: DeliveryDate? = null

    val restaurantData = MutableLiveData<Cook>()
    val restaurantFullData = MutableLiveData<Restaurant>()

    var menuItems: Map<Long, Dish>? = null
    val deliveryTiming = MutableLiveData<List<DeliveryDate>>()
    val dishesList = MutableLiveData<List<DishSections>>()

    val progressData = ProgressData()

    fun initData(cook: Cook?) {
        cook?.let {
            Log.d(TAG, "cook= $cook")
            dishesList.postValue(getDishSkeletonItems())
            restaurantData.postValue(it)
            handleFullRestaurantData(getRestaurantData())
        }
    }

    fun handleFullRestaurantData(restaurant: Restaurant?) {
        Log.d(TAG, "Full restaurant= $restaurant")
        restaurant?.let {
            restaurantFullData.postValue(it)
            val restaurantSorted = sortCookingSlotsDishes(it)
            handleDeliveryTimingSection(restaurantSorted)
        }
    }

    /** Creating  Delivery date list
     * Sorting the cooking slots by dates - cooking slot on the same date goes together  **/
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
        currentSelectedDate = deliveryDates.getOrNull(0)
        deliveryDates.getOrNull(0)?.cookingSlots?.getOrNull(0)?.let { firstCookingSlot ->
            handleDishesSection(firstCookingSlot)
        }
        deliveryTiming.postValue(deliveryDates)
    }

    /** Sorting the cooking slot to **/
    private fun sortCookingSlotsDishes(restaurant: Restaurant): Restaurant {
        menuItems = restaurant.dishes.associateBy({ it.id }, { it })
        menuItems?.let{ menuItems ->
            restaurant.cookingSlots.forEach { cookingSlot ->
                val tempHash = menuItems.toMutableMap()
                cookingSlot.menuItems?.forEach { menuItem ->
                    menuItems[menuItem.id]?.let { dish ->
                        cookingSlot.availableDishes.add(dish)
                        tempHash.remove(dish.id)
                    }
                }
                cookingSlot.unAvailableDishes.addAll(tempHash.values.toList())
            }
        }
        return restaurant
    }

    private fun handleDishesSection(cookingSlot: CookingSlot) {
        dishesList.postValue(getDishSkeletonItems())
        val dishSectionsList = mutableListOf<DishSections>()
        if(cookingSlot.availableDishes.isNotEmpty()){
            dishSectionsList.add(DishSectionAvailableHeader("Menu Items"))
        }
        cookingSlot.availableDishes.forEachIndexed() { index, dish ->
            if (index == cookingSlot.availableDishes.size - 1) {
                dishSectionsList.add(DishSectionSingleDish(dish))
            } else {
                dishSectionsList.add(DishSectionSingleDish(dish))
            }
        }
        if(cookingSlot.unAvailableDishes.isNotEmpty()){
            dishSectionsList.add(DishSectionUnavailableHeader())
        }
        cookingSlot.unAvailableDishes.forEachIndexed() { index, dish ->
            if (index == cookingSlot.unAvailableDishes.size - 1) {
                dishSectionsList.add(DishSectionSingleDish(dish))
            } else {
                dishSectionsList.add(DishSectionSingleDish(dish))
            }
        }
        dishesList.postValue(dishSectionsList)
    }

    private fun getDishSkeletonItems(): List<DishSections> {
        val skeletons = mutableListOf<DishSectionSkeleton>()
        for (i in 0 until 4) {
            skeletons.add(DishSectionSkeleton())
        }
        return skeletons
    }

    fun onDeliveryDateChanged(date: DeliveryDate?) {
        currentSelectedDate = date
        date?.cookingSlots?.getOrNull(0)?.let { cookingSlot ->
            handleDishesSection(cookingSlot)
        }
    }

    companion object {
        const val TAG = "wowRestaurantPageVM"
    }

}
