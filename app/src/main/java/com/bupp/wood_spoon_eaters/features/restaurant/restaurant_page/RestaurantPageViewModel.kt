package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.RestaurantRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.isSameDateAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantPageViewModel(
    private val restaurantRepository: RestaurantRepository,
    private val timeManager: DeliveryTimeManager,
    private val cartManager: CartManager,
) : ViewModel() {
    var currentCookingSlot: CookingSlot? = null
    val currentCookingSlotData = MutableLiveData<CookingSlot>()

    val initialParamData = MutableLiveData<RestaurantInitParams>()
    val restaurantFullData = MutableLiveData<Restaurant>()

    var dishes: Map<Long, Dish>? = null
    var sortedCookingSlots: List<SortedCookingSlots>? = null
    val deliveryDatesData = MutableLiveData<List<SortedCookingSlots>>()
    val timePickerEvent = LiveEventData<CookingSlot>()

    var dishListData: List<DishSections> = emptyList()
    val dishListLiveData = MutableLiveData<DishListData>()

    data class DishListData(val dishes: List<DishSections>, val animateList: Boolean = true)

    val clearCartEvent = cartManager.getClearCartUiEvent()
    val orderLiveData = cartManager.getCurrentOrderData()
    val wsErrorEvent = cartManager.getWsErrorEvent()
    val floatingBtnEvent = cartManager.getFloatingCartBtnEvent()
    val onCookingSlotForceChange = cartManager.getOnCookingSlotIdChange()

    val progressData = ProgressData()

    fun handleInitialParamData(params: RestaurantInitParams) {
        initialParamData.postValue(params)
        initRestaurantFullData(params.restaurantId)
    }

    private fun initRestaurantFullData(restaurantId: Long?) {
        restaurantId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                Log.d("orderFlow - restPage","initRestaurantFullData")
                dishListData = getDishSkeletonItems()
//                dishListLiveData.postRawValue(DishListData(getDishSkeletonItems()))
                dishListLiveData.postValue(DishListData(dishListData))
                val result = restaurantRepository.getRestaurant(restaurantId)
                if (result.type == RestaurantRepository.RestaurantRepoStatus.SUCCESS) {
                    result.restaurant?.let { restaurant ->
                        restaurantFullData.postValue(restaurant)
                        dishes = restaurant.dishes.associateBy({ it.id }, { it })
                        handleDeliveryTimingSection(restaurant)
                        chooseStartingCookingSlot(restaurant, sortedCookingSlots!!)
                    }
                }
            }
        }
    }

    /** Creating  Delivery date list
     * Sorting the cooking slots by dates - cooking slot on the same date goes together  **/
    private fun handleDeliveryTimingSection(restaurant: Restaurant) {
        Log.d("orderFlow - restPage","handleDeliveryTimingSection")
        val deliveryDates = mutableListOf<SortedCookingSlots>()
        restaurant.cookingSlots.forEach { cookingSlot ->
            val  relevantDeliveryDate = deliveryDates.find { it.date.isSameDateAs(cookingSlot.orderFrom) }
            if (relevantDeliveryDate == null) {
                deliveryDates.add(SortedCookingSlots(cookingSlot.orderFrom, mutableListOf(cookingSlot)))
            } else { 
                relevantDeliveryDate.cookingSlots.add(cookingSlot)
                relevantDeliveryDate.cookingSlots.sortBy { it.orderFrom }
            }
        }
        deliveryDates.sortBy { it.date }
        this.sortedCookingSlots = deliveryDates
        this.deliveryDatesData.postValue(deliveryDates)
    }

    /**
     * Checking which cooking slot should be selected when first entering restaurant page
     */
    private fun chooseStartingCookingSlot(restaurant: Restaurant, sortedCookingSlots: List<SortedCookingSlots>) {
        Log.d("orderFlow - restPage","chooseStartingCookingSlot")
        if (cartManager.hasOpenCartInRestaurant(restaurant.id)) {
            /**  case1 : has open cart - get the cooking slot of the current order **/
            //todo - nicole - this will work? - cooking slot sections is not returned with order
            val orderCookingSlot = cartManager.getCurrentCookingSlot()
            orderCookingSlot?.let { onCookingSlotSelected(it) }
        } else {
            /**  case2 : no open cart, has chosen date from feed - search for cookingSlot that contains chosen date  **/
            timeManager.getTempDeliveryTimeDate()?.let { feedDate ->
                sortedCookingSlots.forEach { date ->
                    var feedTimeCookingSlot: CookingSlot? = null
                        if (DateUtils.isSameDay(date.date, feedDate)) {
                            feedTimeCookingSlot = date.cookingSlots[0]
                        }
//                    val feedTimeCookingSlot = date.cookingSlots.find { cookingSlot ->
//                        DateUtils.isDateInRange(feedDate, cookingSlot.orderFrom, cookingSlot.endsAt)
//                    }
                    feedTimeCookingSlot?.let {
                        /**  case2 : feed time cooking slot found
                         * 1. update cartManager to be set to the "orderFrom" date, order can be made
                         * 2. update restaurant screen ui
                         * **/
                        timeManager.setTemporaryDeliveryTimeDate(it.orderFrom)
                        onCookingSlotSelected(it)
                        return
                    }
                }
            }
            /**  case3 : no open cart, no chosen date - get first cooking slot in list **/
            sortedCookingSlots.getOrNull(0)?.cookingSlots?.getOrNull(0)?.let {
                onCookingSlotSelected(it)
            }
        }
    }

    data class CookingSlotUi(
        val cookingSlotId: Long,
        val timePickerString: String,
    )

    val onCookingSlotUiChange = MutableLiveData<CookingSlotUi>()
    fun updateCookingSlotRelatedUi(cookingSlot: CookingSlot) {
        Log.d("orderFlow - restPage","updateCookingSlotRelatedUi")
        onCookingSlotUiChange.postValue(CookingSlotUi(cookingSlot.id, getTimerPickerStr(cookingSlot)))
    }

    fun onCookingSlotSelected(cookingSlot: CookingSlot) {
        Log.d("orderFlow - restPage","onCookingSlotSelected: $cookingSlot")
        updateCookingSlotRelatedUi(cookingSlot)
        currentCookingSlot = cookingSlot
        val sortedCookingSlot = sortCookingSlotDishes(cookingSlot)
        handleDishesSection(sortedCookingSlot)
    }


    /**
     * returns the text to show on the time picker button
     */
    private fun getTimerPickerStr(selectedCookingSlot: CookingSlot?): String {
        selectedCookingSlot?.let {
            val isNow = DateUtils.isNowInRange(selectedCookingSlot.orderFrom, selectedCookingSlot.endsAt)
            val datesStr = "${DateUtils.parseDateToUsTime(selectedCookingSlot.orderFrom)} - ${DateUtils.parseDateToUsTime(selectedCookingSlot.endsAt)}"
            var uiStr = ""
            if (isNow) {
                uiStr = "Now ($datesStr)"
            } else {
                uiStr = "${selectedCookingSlot.name} ($datesStr)"
            }
            return uiStr
        }
        return ""
    }


    fun onTimePickerClicked() {
        // when picker is clicked we send from viewModel the selected cooking slot
        // so we can highlight it for the user inside the dialog
        currentCookingSlot?.let {
            timePickerEvent.postRawValue(it)
        }
    }

    /** on cooking slot selected - need to sort by available/unavailable dishes
     * 1. sort by available/unavailable dishes
     * 2. for each menuItem - match the relevant dish from dishes hashMap
     * @param cookingSlot CookingSlot the chosenCookingSlot
     */
    private fun sortCookingSlotDishes(cookingSlot: CookingSlot): CookingSlot {
        Log.d("orderFlow - restPage","sortCookingSlotDishes")
        dishes?.let { dishes ->
            val tempHash = dishes.toMutableMap()
            cookingSlot.sections.forEach { section ->
                section.menuItems.forEach { menuItem ->
                    dishes[menuItem.dishId]?.let { dish ->
                        menuItem.dish = dish
                        menuItem.cookingSlotId = cookingSlot.id
                        menuItem.availableLater = null
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
        sortedCookingSlots?.forEach { dates ->
            dates.cookingSlots.forEach { cookingSlot ->
                cookingSlot.sections.forEach { section ->
                    val menuItem = section.menuItems.find { it.dishId == dish.id }?.copy()
                    if (menuItem != null) {
                        menuItem.availableLater = AvailabilityDate(startsAt = cookingSlot.orderFrom, endsAt = cookingSlot.endsAt)
                        menuItem.cookingSlotId = cookingSlot.id
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
        Log.d("orderFlow - restPage","handleDishesSection")
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
        updateDishCountUi(dishSectionsList)
    }

    /**
     * update the dishCount of every dish in the current presented menu
     */
    private fun updateDishCountUi(dishSectionsList: List<DishSections>, animateList: Boolean = true) {
        val orderItems = cartManager.getCurrentOrderItems()
        if (cartManager.currentCookingSlotId == currentCookingSlot?.id) {
            // we need to check matching cooking slots inorder to prevent count change of diffrent menus - with same dishes
            // we need to check current cart orderItems - to update relevent dishes count to mach current order.
            val updatedSectionList = resetSectionItemsCounter(dishSectionsList)
            Log.d("orderFlow - restPage","updateDishCountUi")
            orderItems?.forEach { orderItem ->
                val dishSection = updatedSectionList.find { it.menuItem?.dishId == orderItem.dish.id }
                dishSection?.let {
                    dishSection.cartQuantity += orderItem.quantity
                    Log.d("orderFlow - restPage", "updating dish quantity - ${dishSection.cartQuantity}, ${it.menuItem?.dish?.name}")
                }
            }
            dishListData = updatedSectionList
//                dishListLiveData.postRawValue(DishListData(getDishSkeletonItems()))
            dishListLiveData.postValue(DishListData(dishListData, animateList))
//            dishListLiveData.postRawValue(DishListData(updatedSectionList, animateList))
        } else {
            Log.d("orderFlow - restPage","updateDishCountUi2")
            dishListData = dishSectionsList
            dishListLiveData.postValue(DishListData(dishListData, animateList))
        }
    }

    private fun resetSectionItemsCounter(dishSectionsList: List<DishSections>): MutableList<DishSections> {
        Log.d("orderFlow - restPage","resetSectionItemsCounter")
        val updatedSectionList = mutableListOf<DishSections>()
        dishSectionsList.forEach { dishSection ->
            if (dishSection is DishSectionSingleDish) {
                val updatedDishSection = dishSection.copy()
                // we make copy of the dish section so the adapter will refresh the item
                updatedDishSection.cartQuantity = 0
                updatedSectionList.add(updatedDishSection)
            } else {
                updatedSectionList.add(dishSection)
            }
        }
        return updatedSectionList
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
    fun onDeliveryDateChanged(date: SortedCookingSlots?) {
        date?.cookingSlots?.getOrNull(0)?.let { cookingSlot ->
            onCookingSlotSelected(cookingSlot)
        }
    }

    fun addDishToCart(quantity: Int, dishId: Long, note: String? = null) {
        Log.d("orderFlow - restPage","addDishToCart")
        currentCookingSlot?.let { currentCookingSlot ->
            val currentRestaurant = restaurantFullData.value!!
            if (cartManager.validateCartMatch(currentRestaurant, currentCookingSlot.id, currentCookingSlot.orderFrom, currentCookingSlot.endsAt)) {
                viewModelScope.launch {
                    cartManager.updateCurCookingSlotId(currentCookingSlot.id)
                    cartManager.addOrUpdateCart(quantity, dishId, note)
                }
            } else {
                cartManager.setPendingRequestParams(currentCookingSlot.id, quantity, dishId, note)
            }
        }
    }

    fun removeOrderItemsByDishId(dishId: Long?) {
        dishId?.let {
            viewModelScope.launch {
                cartManager.removeOrderItems(it)
            }
        }
    }

    /**
     * Called on any change to cart - updating UI accordingly
     */
    fun handleCartData(order: Order?) {
        Log.d("orderFlow - restPage","handleCartData")
        Log.d(TAG, "handleCartData")
        order?.let {
            dishListData.let {
                updateDishCountUi(it, false)
                updateFloatingCartButtonUi(order)
            }
        }
    }

    private fun updateFloatingCartButtonUi(order: Order) {
        cartManager.updateFloatingCartBtn(order)
    }

    private fun refreshFloatingCartButtonUi() {
        cartManager.refreshFloatingCartBtn()
    }

    fun onPerformClearCart() {
        cartManager.onCartCleared()
        viewModelScope.launch {
            cartManager.checkForPendingActions()
        }
    }

    fun refreshRestaurantUi() {
        currentCookingSlot?.let { onCookingSlotSelected(it) }
    }

    fun forceCookingSlotUiChange(cookingSlotId: Long) {
        Log.d(TAG, "forceCookingSlotUiChange")
        getCookingSlotById(cookingSlotId)?.let { onCookingSlotSelected(it) }
    }

    private fun getCookingSlotById(cookingSlotId: Long): CookingSlot? {
        val result = sortedCookingSlots?.find { it.cookingSlots.find { it.id == cookingSlotId } != null }
        return result?.cookingSlots?.find { it.id == cookingSlotId }
    }


    companion object {
        const val TAG = "wowRestaurantPageVM"
    }

}
