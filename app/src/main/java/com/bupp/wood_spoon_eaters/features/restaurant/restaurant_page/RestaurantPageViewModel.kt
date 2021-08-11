package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.RestaurantRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.isSameDateAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantPageViewModel(
    val restaurantRepository: RestaurantRepository,
) : ViewModel() {

    var currentSelectedDate: DeliveryDate? = null

//    val restaurantData = MutableLiveData<Cook>()
    val restaurantFullData = MutableLiveData<Restaurant>()

    var dishes: Map<Long, Dish>? = null
    var deliveryDates: List<DeliveryDate>? = null
    val deliveryDatesData = MutableLiveData<List<DeliveryDate>>()
    val dishesList = MutableLiveData<List<DishSections>>()
    val timePickerUi = MutableLiveData<String>()

    val progressData = ProgressData()

//    fun initData(cook: Cook?) {
//        cook?.let {
//            Log.d(TAG, "cook= $cook")
////            dishesList.postValue(getDishSkeletonItems())
//            restaurantData.postValue(it)
//            getRestaurantFullData(cook.id)
//        }
//    }

    fun getRestaurantFullData(restaurantId: Long?) {
        restaurantId?.let{
            viewModelScope.launch(Dispatchers.IO) {
                val result = restaurantRepository.getRestaurant(restaurantId)
                if (result.type == RestaurantRepository.RestaurantRepoStatus.SUCCESS) {
                    handleFullRestaurantData(result.restaurant)
                }
            }
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
        //todo - nicole please explain - relevantDeliveryTime
        val deliveryDates = mutableListOf<DeliveryDate>()
        restaurant.cookingSlots.forEach { cookingSlot ->
            val relevantDeliveryTime = deliveryDates.find { it.date.isSameDateAs(cookingSlot.startsAt) }
            if (relevantDeliveryTime == null) {
                deliveryDates.add(DeliveryDate(cookingSlot.startsAt, mutableListOf(cookingSlot)))
            } else {
                relevantDeliveryTime.cookingSlots.add(cookingSlot)
                relevantDeliveryTime.cookingSlots.sortBy { it.startsAt }
            }
        }
        deliveryDates.sortBy { it.date }
        this.deliveryDates = deliveryDates
        this.deliveryDatesData.postValue(deliveryDates)
        restaurant.cookingSlots.getOrNull(0)?.let {
            onCookingSlotSelected(it)
        }
    }

    fun onCookingSlotSelected(cookingSlot: CookingSlot) {
        val sortedCookingSlot = sortCookingSlotDishes(cookingSlot)
        handleDishesSection(sortedCookingSlot)
    }

    /** on cooking slot selected - need to sort by available/unavailable dishes
     * 1. sort by available/unavailable dishes
     * 2. for each menuItem - match the relevant dish from dishes hashMap
     * @param cookingSlot CookingSlot the chosenCookingSlot
     */
    private fun sortCookingSlotDishes(cookingSlot: CookingSlot): CookingSlot {
        dishes?.let { dishes ->
            val tempHash = dishes.toMutableMap()
            cookingSlot.sections.forEach { section ->
                section.menuItems.forEach { menuItem ->
                    dishes[menuItem.dishId]?.let { dish ->
                        menuItem.dish = dish
                        tempHash.remove(dish.id)
                    }
                }
            }
            tempHash.forEach {
                findClosestMenuItem(it.value)?.let { menuItem ->
                    dishes[menuItem.dishId]?.let { dish ->
                        menuItem.dish = dish
                    }
                    cookingSlot.unAvailableDishes.add(menuItem)
                }
            }
        }
        return cookingSlot
    }

    /** for unavailable dishes
     *  given a dish - the function searched the closest cookingSlot containing the dish and returns the relevant menuItem with AvailabilityDate
     *  AvailabilityDate will tell us the other date the dish is available at
     * @param dish Dish - the available later dish
     * @return MenuItem - the closest MenuItem found
     */
    private fun findClosestMenuItem(dish: Dish): MenuItem? {
        deliveryDates?.forEach { dates ->
            dates.cookingSlots.forEach { cookingSlot ->
                cookingSlot.sections.forEach { section ->
                    val menuItem = section.menuItems.find { it.dishId == dish.id }
                    if (menuItem != null) {
                        menuItem.availableLater = AvailabilityDate(startsAt = cookingSlot.startsAt, endsAt = cookingSlot.endsAt)
                        return menuItem
                    }
                }
            }
        }
        return null
    }

    /** on cooking slot selected - after we sorted the cooking slot
     * we need to create DishSections List for our adapter
     * forEach section we add DishSectionAvailableHeader - then we're adding the section's menu items
     * and for unAvailableDishes we add DishSectionUnavailableHeader - then we add the unavailable menuItems
     * @param cookingSlot CookingSlot the chosenCookingSlot
     */
    private fun handleDishesSection(cookingSlot: CookingSlot) {
        val dishSectionsList = mutableListOf<DishSections>()
        cookingSlot.sections.forEach { section ->
            if (section.menuItems.isNotEmpty()) {
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

    /** Creating a skeleton items list */
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
        updateTimePickerUi(date?.cookingSlots?.get(0))
    }

    private fun updateTimePickerUi(selectedCookingSlot: CookingSlot?) {
        selectedCookingSlot?.let{
            val isNow = DateUtils.isNowInRange(selectedCookingSlot.startsAt, selectedCookingSlot.endsAt)
            val datesStr = "${DateUtils.parseDateToUsTime(selectedCookingSlot.startsAt)} - ${DateUtils.parseDateToUsTime(selectedCookingSlot.endsAt)}"
            var uiStr = ""
            if(isNow){
                uiStr = "Now ($datesStr)"
            }else{
                uiStr = "${selectedCookingSlot.name} ($datesStr)"
            }
            timePickerUi.postValue(uiStr)
        }
    }

    companion object {
        const val TAG = "wowRestaurantPageVM"
    }

}
