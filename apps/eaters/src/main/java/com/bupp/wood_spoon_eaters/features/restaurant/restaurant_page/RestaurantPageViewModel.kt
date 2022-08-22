package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.*
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.managers.FeatureFlagManager
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.RestaurantRepository
import com.bupp.wood_spoon_eaters.repositories.RestaurantRepository.RestaurantRepoStatus.*
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.isSameDateAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class RestaurantPageViewModel(
    private val restaurantRepository: RestaurantRepository,
    private val cartManager: CartManager,
    private val feedDataManager: FeedDataManager,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker,
    private val metaDataManager: MetaDataRepository,
    private val featureFlagManager: FeatureFlagManager
) : ViewModel() {
    private var selectedDishId: String? = null

    var currentRestaurantId: Long = -1

    var currentCookingSlot: CookingSlot? = null
    var searchedCookingSlotId: Long? = null

    val initialParamData = MutableLiveData<RestaurantInitParams>()
    val selectedDishNavigationLifeData = SingleLiveEvent<MenuItem>()
    val restaurantFullData = MutableLiveData<Restaurant>()

    var dishes: Map<Long, Dish>? = null
    var sortedCookingSlots: List<SortedCookingSlots>? = null
    val deliveryDatesData = MutableLiveData<List<SortedCookingSlots>>()
    val timePickerEvent = LiveEventData<CookingSlot>()

    var dishListData: List<DishSections> = emptyList()
    val dishListLiveData = MutableLiveData<DishListData>()
    val dishSearchListLiveData = MutableLiveData<DishListData>()

    data class DishListData(val dishes: List<DishSections>, val animateList: Boolean = true)

    val clearCartEvent = cartManager.getClearCartUiEvent()
    val orderLiveData = cartManager.getCurrentOrderData()
    val wsErrorEvent = cartManager.getWsErrorEvent()
    val networkErrorEvent = LiveEventData<Boolean>();
    val floatingBtnEvent = cartManager.getFloatingCartBtnEvent()
    val onCookingSlotForceChange = cartManager.getOnCookingSlotIdChange()

    val progressData = ProgressData()

    init {
        featureFlagManager.fetchSplitData()
    }

    fun handleInitialParamData(params: RestaurantInitParams) {
        if (initialParamData.value == null) {
            currentRestaurantId = params.restaurantId ?: -1
            initialParamData.postValue(params)
            if (params.isFromSearch) {
                searchedCookingSlotId = params.cookingSlot?.id
            }
            initRestaurantFullData(params.restaurantId, query = params.query)
        }
        params.selectedDishId?.let {
            selectedDishId = it
            searchedCookingSlotId = params.cookingSlot?.id
        }
    }

    fun reloadPage(showSkeleton: Boolean = true) {
        initRestaurantFullData(currentRestaurantId, showSkeleton)
    }

    private fun initRestaurantFullData(
        restaurantId: Long?,
        showSkeleton: Boolean = true,
        query: String? = null
    ) = restaurantId?.let {
        viewModelScope.launch(Dispatchers.IO) {
            if (showSkeleton) {
                dishListData = getDishSkeletonItems()
                dishListLiveData.postValue(DishListData(dishListData))
            }

            val lastFeedRequest = feedDataManager.getLastFeedRequest()
            lastFeedRequest.q = query
            val result = restaurantRepository.getRestaurant(restaurantId, lastFeedRequest)

            if (result.type == SUCCESS) {
                result.restaurant?.let { restaurant ->
                    restaurant.flagUrl = metaDataManager.getCountryFlagById(restaurant.countryId)
                    restaurantFullData.postValue(restaurant)
                    dishes = restaurant.dishes.associateBy({ it.id }, { it })
                    if (checkIfUnavailableCookingSlot(restaurant).not()) {
                        handleDeliveryTimingSection(restaurant)
                        chooseStartingCookingSlot(restaurant, sortedCookingSlots!!)
                    }
                }

                navigateToSelectedDishIfExist(result)

            } else if (result.type == SERVER_ERROR) {
                networkErrorEvent.postRawValue(true)
            }
        }
    }

    private fun navigateToSelectedDishIfExist(
        result: RestaurantRepository.RestaurantResult
    ) {
        selectedDishId?.let { dishId ->
            result.restaurant?.let { restaurant ->
                restaurant.cookingSlots
                    .filter { it.id == searchedCookingSlotId }
                    .map { it.sections }
                    .map { sectionList ->
                        sectionList.forEach { section ->
                            section.menuItems.forEach { menuItem ->
                                if (menuItem.dish?.id == dishId.toLong()) {
                                    selectedDishNavigationLifeData.postValue(menuItem)
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun checkIfUnavailableCookingSlot(restaurant: Restaurant): Boolean {
        val canDeliver = restaurant.canBeDelivered ?: false
        if (restaurant.cookingSlots.isNotEmpty()) {
            if (restaurant.cookingSlots[0].isDummy()) {
                if (canDeliver) {
                    unavailableEventData.postValue(
                        UnavailableUiData(
                            UnavailableUiType.NO_COOKING_SLOT,
                            "Unfortunately, this Home kitchen is closed at the moment. Check back later!"
                        )
                    )
                } else {
                    unavailableEventData.postValue(
                        UnavailableUiData(
                            UnavailableUiType.UNAVAILABLE_IN_YOUR_LOCATION,
                            "Unfortunately, this Home kitchen does not deliver to you location."
                        )
                    )
                }
                onCookingSlotSelected(restaurant.cookingSlots[0], true)
                return true
            } else {
                unavailableEventData.postValue(
                    UnavailableUiData(UnavailableUiType.AVAILABLE)
                )
                return false
            }
        }
        return true
    }

    /** Creating  Delivery date list
     * Sorting the cooking slots by dates - cooking slot on the same date goes together  **/
    private fun handleDeliveryTimingSection(restaurant: Restaurant) {
        val deliveryDates = mutableListOf<SortedCookingSlots>()
        restaurant.cookingSlots.forEach { cookingSlot ->
            val relevantDeliveryDate =
                deliveryDates.find { it.date.isSameDateAs(cookingSlot.orderFrom) }
            if (relevantDeliveryDate == null) {
                deliveryDates.add(
                    SortedCookingSlots(
                        cookingSlot.orderFrom,
                        mutableListOf(cookingSlot)
                    )
                )
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
    private fun chooseStartingCookingSlot(
        restaurant: Restaurant,
        sortedCookingSlots: List<SortedCookingSlots>
    ) {
        if (searchedCookingSlotId != null) {
            /**  case1 : open restaurant from search page **/
            val currentCookingSlot = getCookingSlotById(searchedCookingSlotId!!)
            currentCookingSlot?.let {
                onCookingSlotSelected(currentCookingSlot, true)
            }
        } else if (cartManager.hasOpenCartInRestaurant(restaurant.id)) {
            /**  case2 : has open cart - get the cooking slot of the current order **/
            val orderCookingSlot = cartManager.getCurrentCookingSlot()
            orderCookingSlot?.let {
                val currentCookingSlot = getCookingSlotById(orderCookingSlot.id)
                currentCookingSlot?.let {
                    onCookingSlotSelected(currentCookingSlot, true)
                }
            }
        } else {
            /**  case3 : no open cart, has chosen date from feed - search for cookingSlot that contains chosen date  **/
            feedDataManager.getFeedDeliveryParams()?.let { feedDate ->
                sortedCookingSlots.forEach { date ->
                    var feedTimeCookingSlot: CookingSlot? = null
                    if (DateUtils.isSameDay(date.date, feedDate)) {
                        feedTimeCookingSlot = date.cookingSlots[0]
                    }
                    feedTimeCookingSlot?.let {
                        /**  case2 : feed time cooking slot found
                         * 1. update cartManager to be set to the "orderFrom" date, order can be made
                         * 2. update restaurant screen ui
                         * **/
                        onCookingSlotSelected(it, true)
                        return
                    }
                }
            }
            /**  case3 : no open cart, no chosen date - get first cooking slot in list **/
            sortedCookingSlots.getOrNull(0)?.cookingSlots?.getOrNull(0)?.let {
                onCookingSlotSelected(it, false)
            }
        }
    }

    data class CookingSlotUi(
        val cookingSlotId: Long,
        val timePickerString: String,
        val forceTabChange: Boolean = false
    )

    val onCookingSlotUiChange = MutableLiveData<CookingSlotUi>()
    private fun updateCookingSlotRelatedUi(cookingSlot: CookingSlot, forceTabChnage: Boolean) {
        onCookingSlotUiChange.postValue(
            CookingSlotUi(
                cookingSlotId = cookingSlot.id,
                timePickerString = getTimerPickerStr(cookingSlot),
                forceTabChange = forceTabChnage
            )
        )
    }

    enum class UnavailableUiType {
        AVAILABLE,
        NO_COOKING_SLOT,
        UNAVAILABLE_IN_YOUR_LOCATION
    }

    data class UnavailableUiData(
        val type: UnavailableUiType,
        val text: String? = null,
    )

    val unavailableEventData = MutableLiveData<UnavailableUiData>()
    fun onCookingSlotSelected(cookingSlot: CookingSlot, forceTabChange: Boolean) {
        updateCookingSlotRelatedUi(cookingSlot, forceTabChange)
        currentCookingSlot = cookingSlot
        val sortedCookingSlot = sortCookingSlotDishes(cookingSlot)
        handleDishesSection(sortedCookingSlot)
    }


    /**
     * returns the text to show on the time picker button
     */
    private fun getTimerPickerStr(selectedCookingSlot: CookingSlot?): String {
        selectedCookingSlot?.let {
            val isNow =
                DateUtils.isNowInRange(selectedCookingSlot.startsAt, selectedCookingSlot.endsAt)
            val datesStr = "from ${DateUtils.parseDateToUsTime(selectedCookingSlot.startsAt)} to ${
                DateUtils.parseDateToUsTime(selectedCookingSlot.endsAt)
            }"
            val uiStr: String = if (isNow) {
                "Available now (until ${DateUtils.parseDateToUsTime(selectedCookingSlot.endsAt)})"
            } else {
                if (DateTime(selectedCookingSlot.startsAt).hourOfDay > 12
                    && DateTime(selectedCookingSlot.endsAt).hourOfDay > 12
                    ||
                    DateTime(selectedCookingSlot.startsAt).hourOfDay < 12
                    && DateTime(selectedCookingSlot.endsAt).hourOfDay < 12
                ) {
                    "Available from ${DateUtils.parseDateToUsTimeWithoutAmPm(selectedCookingSlot.startsAt)} to ${
                        DateUtils.parseDateToUsTime(selectedCookingSlot.endsAt)
                    }"
                } else {
                    "Available $datesStr"
                }
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
        dishes?.let { dishes ->
            val tempHash = dishes.toMutableMap()
            cookingSlot.sections.forEachIndexed { sectionIndex, section ->
                section.menuItems.forEachIndexed { restaurantIndex, menuItem ->
                    dishes[menuItem.dishId]?.let { dish ->
                        menuItem.dish = dish
                        menuItem.cookingSlotId = cookingSlot.id
                        menuItem.availableLater = null

                        menuItem.sectionTitle = section.title
                        menuItem.sectionOrder = sectionIndex + 1
                        menuItem.dishOrderInSection = restaurantIndex + 1

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
                        menuItem.availableLater = AvailabilityDate(
                            startsAt = cookingSlot.orderFrom,
                            endsAt = cookingSlot.endsAt
                        )
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
        val dishSectionsList = mutableListOf<DishSections>()
        cookingSlot.sections.forEach { section ->
            if (section.menuItems.isNotEmpty()) {
                dishSectionsList.add(
                    DishSectionAvailableHeader(
                        section.title ?: "",
                        section.subtitle ?: ""
                    )
                )
                section.menuItems.forEach { menuItem ->
                    dishSectionsList.add(
                        DishSectionSingleDish(
                            menuItem,
                            isDummy = cookingSlot.isDummy()
                        )
                    )
                }
            }
        }
        if (cookingSlot.unAvailableDishes.isNotEmpty()) {
            dishSectionsList.add(DishSectionUnavailableHeader())
            cookingSlot.unAvailableDishes.forEach { menuItem ->
                dishSectionsList.add(
                    DishSectionSingleDish(
                        menuItem,
                        isDummy = cookingSlot.isDummy()
                    )
                )
            }
        }
        updateDishCountUi(dishSectionsList)
    }

    /**
     * update the dishCount of every dish in the current presented menu
     */
    private fun updateDishCountUi(
        dishSectionsList: List<DishSections>,
        animateList: Boolean = true
    ) {
        val orderItems = cartManager.getCurrentOrderItems()
        if (cartManager.currentCookingSlotId == currentCookingSlot?.id) {
            // we need to check matching cooking slots inorder to prevent count change of diffrent menus - with same dishes
            // we need to check current cart orderItems - to update relevent dishes count to mach current order.
            val updatedSectionList = resetSectionItemsCounter(dishSectionsList)
            orderItems?.forEach { orderItem ->
                val dishSection =
                    updatedSectionList.find { it.menuItem?.dishId == orderItem.dish.id }
                dishSection?.let {
                    dishSection.cartQuantity += orderItem.quantity
                }
            }
            dishListData = updatedSectionList
            dishListLiveData.postValue(DishListData(dishListData, animateList))
        } else {
            dishListData = dishSectionsList
            dishListLiveData.postValue(DishListData(dishListData, animateList))
        }
    }

    private fun resetSectionItemsCounter(dishSectionsList: List<DishSections>): MutableList<DishSections> {
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
            onCookingSlotSelected(cookingSlot, false)
            logEvent(Constants.EVENT_CHANGE_COOKING_SLOT_DATE)
        }
    }

    fun addDishToCart(quantity: Int, dishId: Long, note: String? = null) {
        currentCookingSlot?.let { currentCookingSlot ->
            val currentRestaurant = restaurantFullData.value!!
            if (cartManager.validateCartMatch(
                    currentRestaurant,
                    currentCookingSlot.id,
                    currentCookingSlot.orderFrom,
                    currentCookingSlot.endsAt
                )
            ) {
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
    fun handleCartData() {
        updateDishCountUi(dishListData, false)
    }

    fun onPerformClearCart() {
        eatersAnalyticsTracker.logEvent(Constants.EVENT_CLEAR_CART)
        cartManager.onCartCleared()
        viewModelScope.launch {
            cartManager.checkForPendingActions()
        }
    }

    fun refreshRestaurantUi() {
        currentCookingSlot?.let { onCookingSlotSelected(it, false) }
    }

    fun forceCookingSlotUiChange(cookingSlotId: Long) {
        getCookingSlotById(cookingSlotId)?.let { onCookingSlotSelected(it, true) }
    }

    private fun getCookingSlotById(cookingSlotId: Long): CookingSlot? {
        val result =
            sortedCookingSlots?.find { it.cookingSlots.find { it.id == cookingSlotId } != null }
        return result?.cookingSlots?.find { it.id == cookingSlotId }
    }

    val favoriteEvent = LiveEventData<Boolean>()
    fun addToFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = restaurantRepository.likeCook(currentRestaurantId)
            favoriteEvent.postRawValue(result.type == SUCCESS)
            logEvent(Constants.EVENT_LIKE_RESTAURANT)
        }
    }

    fun logEvent(eventName: String) {
        when (eventName) {
            Constants.EVENT_LIKE_RESTAURANT -> {
                eatersAnalyticsTracker.logEvent(eventName, getLikeRestaurantData())
            }
            Constants.EVENT_SHARE_RESTAURANT -> {
                eatersAnalyticsTracker.logEvent(eventName, getLikeRestaurantData())
            }
            Constants.EVENT_CHANGE_COOKING_SLOT_DATE -> {
                eatersAnalyticsTracker.logEvent(eventName, getCookingSlotChangeData())
            }
            else -> {
                eatersAnalyticsTracker.logEvent(eventName)
            }
        }
    }

    private fun getLikeRestaurantData(): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["home_chef_id"] = currentRestaurantId.toString()
        data["home_chef_name"] = initialParamData.value?.chefName.toString()
        return data
    }

    private fun getCookingSlotChangeData(): Map<String, String> {
        val data = mutableMapOf<String, String>()
        currentCookingSlot?.let {
            data["selected_date"] = DateUtils.parseDateToUsDate(it.startsAt)
            data["day"] = DateUtils.parseDateToDayName(it.startsAt)
        }
        return data
    }

    fun removeFromFavoriteClick() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = restaurantRepository.unlikeCook(currentRestaurantId)
            favoriteEvent.postRawValue(result.type == SUCCESS)
        }
    }

    fun initDishSearch() {
        dishSearchListLiveData.postValue(DishListData(listOf(DishSectionSearchEmpty())))
    }

    fun filterDishSearch(input: String) {
        val currentDishes = dishListLiveData.value
        val filteredList: MutableList<DishSections> = mutableListOf()
        val availableArr: MutableList<DishSections> = mutableListOf()
        val unAvailableArr: MutableList<DishSections> = mutableListOf()
        var unavailableHeaderIndex = -1
        currentDishes?.dishes?.forEachIndexed { index, dishSections ->
            if (dishSections.viewType == DishSectionsViewType.AVAILABLE_HEADER) {
                (dishSections as DishSectionAvailableHeader).subtitle = ""
                filteredList.add(dishSections)
            }
            if (dishSections.viewType == DishSectionsViewType.UNAVAILABLE_HEADER) {
                unavailableHeaderIndex = index
            }
            if (dishSections.menuItem?.dish?.name?.lowercase()
                    ?.contains(input.lowercase()) == true
            ) {
                if (unavailableHeaderIndex > -1 && index > unavailableHeaderIndex) {
                    unAvailableArr.add(dishSections)
                } else {
                    availableArr.add(dishSections)
                }
            }
        }
        if (availableArr.isEmpty()) {
            filteredList.clear()
        } else {
            filteredList.addAll(availableArr)
        }
        if (unAvailableArr.isNotEmpty()) {
            filteredList.add(DishSectionUnavailableHeader())
            filteredList.addAll(unAvailableArr)
        }
        dishSearchListLiveData.postValue(DishListData(filteredList))

    }

    companion object {
        const val TAG = "wowRestaurantPageVM"
    }

}
