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

    var dishes: Map<Long, Dish>? = null
    val deliveryDates = MutableLiveData<List<DeliveryDate>>()
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
        restaurant?.let { restaurant ->
            restaurantFullData.postValue(restaurant)
            dishes = restaurant.dishes.associateBy({ it.id }, { it })
            handleDeliveryTimingSection(restaurant)
        }
    }

    /** Creating  Delivery date list
     * Sorting the cooking slots by dates - cooking slot on the same date goes together  **/
    private fun handleDeliveryTimingSection(restaurant: Restaurant) {
        val deliveryDates = mutableListOf<DeliveryDate>()
        restaurant.cookingSlots.forEach { cookingSlot ->
            val relevantDeliveryTime = deliveryDates.find { it.date.isSameDateAs(cookingSlot.startsAt)}
            if (relevantDeliveryTime == null) {
                deliveryDates.add(DeliveryDate(cookingSlot.startsAt, mutableListOf(cookingSlot)))
            } else {
                relevantDeliveryTime.cookingSlots.add(cookingSlot)
                relevantDeliveryTime.cookingSlots.sortBy{it.startsAt}
            }
        }
        deliveryDates.sortBy { it.date }
        this.deliveryDates.postValue(deliveryDates)
        restaurant.cookingSlots.getOrNull(0)?.let{
            sortCookingSlotDishes(it)
        }
    }


    private fun sortCookingSlotDishes(cookingSlot: CookingSlot) {
        dishes?.let { dishes ->
            val tempHash = dishes.toMutableMap()
            cookingSlot.sections.forEach { section ->
                section.menuItems.forEach { menuItem ->
                    dishes[menuItem.id]?.let { dish ->
                        menuItem.dish = dish
                        tempHash.remove(dish.id)
                    }
                }
            }
            tempHash.forEach{
                findClosestMenuItem(it.value)
            }
        }
    }

    private fun findClosestMenuItem(dish: Dish){
//        deliveryDates.value?.forEach {
//            it.cookingSlots.find { cookingSlot -> cookingSlot.sections.forEach{ dishesList -> dishesList. } }
//        }
    }

    private fun handleDishesSection(cookingSlot: CookingSlot) {
        val dishSectionsList = mutableListOf<DishSections>()
        if (cookingSlot.sections.isNotEmpty()) {
            cookingSlot.sections.forEach { section ->
                dishSectionsList.add(DishSectionAvailableHeader(section.title ?: ""))
                section.menuItems.forEach { menuItem ->
                    dishSectionsList.add(DishSectionSingleDish(menuItem))
                }
            }
        }
        if (cookingSlot.unAvailableDishes.isNotEmpty()) {
            dishSectionsList.add(DishSectionUnavailableHeader())
            cookingSlot.unAvailableDishes.forEach { menuItem ->
                dishSectionsList.add(DishSectionSingleDish(menuItem))
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
            sortCookingSlotDishes(cookingSlot)
        }
    }

    companion object {
        const val TAG = "wowRestaurantPageVM"
    }

}
