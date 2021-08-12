package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.RestaurantRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.isSameDateAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantPageViewModel(
    private val restaurantRepository: RestaurantRepository,
    val cartManager: CartManager,
) : ViewModel() {

    //    var currentSelectedDate: DeliveryDate? = null
    private lateinit var currentCookingSlot: CookingSlot
    val currentCookingSlotData = MutableLiveData<CookingSlot>()

    val initialParamData = MutableLiveData<RestaurantInitParams>()
    val restaurantFullData = MutableLiveData<Restaurant>()

    var dishes: Map<Long, Dish>? = null
    var deliveryDates: List<DeliveryDate>? = null
    val deliveryDatesData = MutableLiveData<List<DeliveryDate>>()

    val dishListData = MutableLiveData<DishListData>()
    data class DishListData(val dishes: List<DishSections> , val animateList: Boolean = false)
    val timePickerUi = MutableLiveData<String>()

    val clearCartEvent = cartManager.getClearCartUiEvent()
    val cartLiveData = cartManager.getCurrentCartData()
    val wsErrorEvent = cartManager.getWsErrorEvent()

    val progressData = ProgressData()

    fun handleInitialParamData(params: RestaurantInitParams) {
        initialParamData.postValue(params)
        getRestaurantFullData(params.restaurantId)
    }

    private fun getRestaurantFullData(restaurantId: Long?) {
        restaurantId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                dishListData.postValue(DishListData(getDishSkeletonItems()))
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
        val deliveryDates = mutableListOf<DeliveryDate>()
        restaurant.cookingSlots.forEach { cookingSlot ->
            val relevantDeliveryDate = deliveryDates.find { it.date.isSameDateAs(cookingSlot.startsAt) }
            if (relevantDeliveryDate == null) {
                deliveryDates.add(DeliveryDate(cookingSlot.startsAt, mutableListOf(cookingSlot)))
            } else {
                relevantDeliveryDate.cookingSlots.add(cookingSlot)
                relevantDeliveryDate.cookingSlots.sortBy { it.startsAt }
            }
        }
        deliveryDates.sortBy { it.date }
        this.deliveryDates = deliveryDates
        this.deliveryDatesData.postValue(deliveryDates)
        chooseStartingCookingSlot(restaurant, deliveryDates)
    }

    /**
     * Checking which cooking slot should be selected when first entering restaurant page

     */
    private fun chooseStartingCookingSlot(restaurant: Restaurant, deliveryDates: List<DeliveryDate>) {
        if (cartLiveData.value?.restaurant?.id == restaurant.id) {
            /**  case1 : has open cart - get the cooking slot of the current order **/
            val orderCookingSlot = cartLiveData.value?.cookingSlot
            onCookingSlotSelected(deliveryDates.get(3).cookingSlots.get(0))
        } else if (false) {
            /**  case2 : no open cart, has chosen date - get closest cooking slot to the chosen date  **/

        } else {
            /**  case3 : no open cart, no chosen date - get first cooking slot in list **/
            deliveryDates.getOrNull(0)?.cookingSlots?.getOrNull(1)?.let {
                onCookingSlotSelected(it)
            }
        }
    }

    fun onCookingSlotSelected(cookingSlot: CookingSlot) {
        currentCookingSlot = cookingSlot
        currentCookingSlotData.postValue(cookingSlot)
        updateTimePickerUi(cookingSlot)
        val sortedCookingSlot = sortCookingSlotDishes(cookingSlot)
        handleDishesSection(sortedCookingSlot)
    }

    /**
     * Updating the text on the time picker button
     */
    private fun updateTimePickerUi(selectedCookingSlot: CookingSlot?) {
        selectedCookingSlot?.let {
            val isNow = DateUtils.isNowInRange(selectedCookingSlot.startsAt, selectedCookingSlot.endsAt)
            val datesStr = "${DateUtils.parseDateToUsTime(selectedCookingSlot.startsAt)} - ${DateUtils.parseDateToUsTime(selectedCookingSlot.endsAt)}"
            var uiStr = ""
            if (isNow) {
                uiStr = "Now ($datesStr)"
            } else {
                uiStr = "${selectedCookingSlot.name} ($datesStr)"
            }
            timePickerUi.postValue(uiStr)
        }
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
            cookingSlot.unAvailableDishes.clear()
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
        updateSelectedDishesCounts(dishSectionsList)
    }

    /**
     * update the dishCount of every dish in the current presented menu
     */
    private fun updateSelectedDishesCounts(dishSectionsList: List<DishSections>, animateList: Boolean = true) {
        cartLiveData.value?.orderItems?.let { orderItems ->
            dishSectionsList.forEach { dishSection ->
                if (dishSection is DishSectionSingleDish) {
                    dishSection.cartQuantity = 0
                }
            }
            orderItems.forEach { orderItem ->
                val dishSection = dishSectionsList.find { it.menuItem?.dishId == orderItem.dish.id }
                dishSection?.let {
                    dishSection.cartQuantity += orderItem.quantity
                }
            }
        }
        dishListData.postValue(DishListData(dishSectionsList))
    }

    /** Creating a skeleton items list */
    private fun getDishSkeletonItems(): List<DishSections> {
        val skeletons = mutableListOf<DishSectionSkeleton>()
        for (i in 0 until 4) {
            skeletons.add(DishSectionSkeleton())
        }
        return skeletons
    }

    /**
     * When changing the delivery date from the tabLayout
     */
    fun onDeliveryDateChanged(date: DeliveryDate?) {
        date?.cookingSlots?.getOrNull(0)?.let { cookingSlot ->
            onCookingSlotSelected(cookingSlot)
        }
    }

    fun addDishToCart(quantity: Int, dishId: Long, note: String? = null) {
        val currentCookingSlot = currentCookingSlot
        val currentRestaurant = restaurantFullData.value!!
        if (cartManager.validateCartMatch(currentRestaurant, currentCookingSlot)) {
            viewModelScope.launch {
                cartManager.updateCurCookingSlot(currentCookingSlot)
                cartManager.addOrUpdateCart(quantity, dishId, note)
            }
        }
    }

    fun removeOrderItemsByDishId(dishId: Long?){
        dishId?.let{
            viewModelScope.launch {
                cartManager.removeOrderItems(it)
            }
        }
    }

    /**
     * Called on any change to cart - updating UI accordingly
     */
    fun handleCartData(order: Order?) {
        order?.cookingSlot?.let { orderCookingSlot ->
            if (orderCookingSlot.id == currentCookingSlot?.id) {
                /** Same cooking slot - only need to update dishes count **/
                dishListData.value?.dishes?.let {
                    updateSelectedDishesCounts(it, false)
                }
            } else {
                /** Different cooking slot - need to change all screen UI accordingly **/
                onCookingSlotSelected(orderCookingSlot)
            }
        }
    }

    companion object {
        const val TAG = "wowRestaurantPageVM"
    }

}
