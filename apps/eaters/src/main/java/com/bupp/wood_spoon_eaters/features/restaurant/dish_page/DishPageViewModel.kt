package com.bupp.wood_spoon_eaters.features.restaurant.dish_page

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.model.DishInitParams
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.model.Restaurant
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class DishPageViewModel(
    private val userRepository: UserRepository,
    private val cartManager: CartManager,
    private val flowEventsManager: FlowEventsManager,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker
) : ViewModel() {

    private var isEditMode = false
    private var isDummy: Boolean? = false
    lateinit var extras: DishInitParams
    private var dishMatchCookingSlot: Boolean = true
    //this param indicates if the current menuItem is matching the selected cooking slot in parent

    val progressData = ProgressData()
    val networkError = MutableLiveData<Boolean>()
    val skeletonProgressData = ProgressData()
    val wsErrorEvent = cartManager.getWsErrorEvent()

    val menuItemData = MutableLiveData<MenuItem>()
    val orderItemData = MutableLiveData<OrderItem>()
    val dishFullData = MutableLiveData<FullDish>()
    val counterBtnsState = MutableLiveData<CounterBtnState>()

    data class CounterBtnState(
        val initialCounter: Int = 1,
        val maxQuantity: Int = 0
    )

    enum class FinishNavigation{
        FINISH_AND_BACK,
        FINISH_ACTIVITY
    }

    var currentFragmentState : DishFragmentState = DishFragmentState.ADD_DISH
    enum class DishFragmentState{
        ADD_DISH,
        UPDATE_DISH
    }
    val onFinishDishPage = MutableLiveData<FinishNavigation>()

    val clearCartEvent = cartManager.getClearCartUiEvent()

    val userRequestData = MutableLiveData<UserRequest>()

    class UserRequest(val eaterName: String, val cook: Restaurant)

    val shakeAddToCartBtn = LiveEventData<Boolean>()

    private var dishQuantity = 0
    var dishMaxQuantity = 0

    data class DishQuantityData(
        val quantity: Int,
        val overallPrice: String
    )

    val dishQuantityChange = MutableLiveData<DishQuantityData>()
    val unavailableUiEvent = MutableLiveData<MenuItem?>()

    fun initData(extras: DishInitParams) {
        this.extras = extras

        extras.selectedDishId?.let { selectedDishId ->
            getFullDish(dishId = selectedDishId.toLong())
            return
        }

        if (extras.menuItem != null) {
            currentFragmentState = DishFragmentState.ADD_DISH

            if (extras.cookingSlot?.id != extras.menuItem.cookingSlotId) {
                dishMatchCookingSlot = false
            }

            isDummy = extras.cookingSlot?.isDummy()

            if(isDummy == true){
                unavailableUiEvent.postValue(extras.menuItem)
                getFullDish(dishId = extras.menuItem.dishId)
            } else {
                handleMenuItemData(extras.menuItem)
                getFullDish(menuItemId = extras.menuItem.id)
            }

            eatersAnalyticsTracker.logEvent(Constants.EVENT_CLICK_ON_DISH, getDishClicked(extras))

        } else if (extras.orderItem != null){
            currentFragmentState = DishFragmentState.UPDATE_DISH
            isEditMode = true
            handleOrderItemData(extras.orderItem)
            extras.orderItem.menuItem?.id?.let { getFullDish(it) }
            dishMatchCookingSlot = true
            isDummy = false
        }
    }

    private fun handleMenuItemData(menuItem: MenuItem) {
        val quantityInCart = cartManager.getQuantityInCart(menuItem.dishId)
        menuItemData.postValue(menuItem)
        dishMaxQuantity = menuItem.getQuantityCount() - quantityInCart
        counterBtnsState.postValue(CounterBtnState(1, dishMaxQuantity))
    }

    private fun getDishClicked(dishParam: DishInitParams): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["dish_name"] = dishParam.menuItem?.dish?.name.toString()
        data["dish_id"] = dishParam.menuItem?.dish?.id.toString()
        data["dish_price"] = dishParam.menuItem?.dish?.price?.formatedValue.toString()
        data["dish_tags"] = dishParam.menuItem?.tags.isNullOrEmpty().not().toString()
        data["dish_section_title"] = dishParam.dishSectionTitle.toString()
        data["dish_index"] = dishParam.dishOrderInSection.toString()
        return data
    }

    private fun handleOrderItemData(orderItem: OrderItem) {
        orderItemData.postValue(orderItem)
        val quantityInCart = cartManager.getQuantityInCart(orderItem.dish.id)
        dishMaxQuantity = (orderItem.menuItem?.getQuantityCount() ?: 0) - quantityInCart + orderItem.quantity
        counterBtnsState.postValue(CounterBtnState(orderItem.quantity, dishMaxQuantity))
    }

    private fun getFullDish(menuItemId: Long? = null, dishId: Long? = null) {
        menuItemId.let {
            skeletonProgressData.startProgress()
            viewModelScope.launch {
                val fullDishResult = when {
                    menuItemId != null -> {
                        cartManager.getFullDishByMenuItem(menuItemId)
                    }
                    dishId != null -> {
                        cartManager.getFullDishByDish(dishId)
                    }
                    else -> { null }
                }

                val fullDish = fullDishResult?.data
                if(fullDish == null){
                    networkError.postValue(true)
                } else {
                    fullDish.let{
                        dishFullData.postValue(it)
                        getUserRequestData(fullDish.restaurant)
                    }
                }
                skeletonProgressData.endProgress()
            }
        }
    }


    private fun getUserRequestData(restaurant: Restaurant) {
        // params for the ui of "Hey $eaterName... add your special request"
        userRequestData.postValue(UserRequest(eaterName = userRepository.getUser()?.getFullName() ?: "", restaurant))
    }

    fun updateDishQuantity(quantity: Int) {
        dishQuantity = quantity

        var overallPrice = ""
        if(isEditMode){
            orderItemData.value?.let {
                it.getSingleItemPrice()?.let {
                    val priceStr = DecimalFormat("##.##").format(it * quantity)
                    overallPrice = "$$priceStr"
                }
            }
        }else{
            menuItemData.value?.let {
                it.dish?.price?.value?.let {
                    val priceStr = DecimalFormat("##.##").format(it * quantity)
                    overallPrice = "$$priceStr"
                }
            }
        }

        if(isDummy == false){
            dishQuantityChange.postValue(DishQuantityData(quantity, overallPrice))
        }
    }

    fun onPerformClearCart() {
        eatersAnalyticsTracker.logEvent(Constants.EVENT_CLEAR_CART)
        cartManager.onCartCleared()
        viewModelScope.launch {
            val startNewCartAction = cartManager.checkForPendingActions()
            if(startNewCartAction == OrderRepository.OrderRepoStatus.ADD_NEW_DISH_SUCCESS){
                onFinishDishPage.postValue(FinishNavigation.FINISH_AND_BACK)
            }
        }
    }

    fun onDishPageCartClick(note: String? = null) {
        if(isEditMode){
            updateOrderItem(note)
        }else{
            addDishToCart(note)
        }
    }

    private fun updateOrderItem(note: String? = null){
        viewModelScope.launch {
            val quantity = dishQuantity
            val dishId = dishFullData.value?.id ?: -1
            orderItemData.value?.let {
                val result = cartManager.updateDishInExistingCart(quantity, note, dishId, it.id)
                if (result == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                    logUpdateDish()
                    onFinishDishPage.postValue(FinishNavigation.FINISH_AND_BACK)
                }
            }
        }
    }

    private fun addDishToCart(note: String? = null) {
        val quantity = dishQuantity
        if (quantity > 0) {
            val dishId = dishFullData.value?.id
            dishId?.let {
                var isValid: Boolean
                val restaurant = dishFullData.value?.restaurant!!
                var cookingSlotId: Long? = 0L
                if (!dishMatchCookingSlot) {
                    //this dish menuItem cooking slot is different then the one selected
                    cookingSlotId = extras.menuItem?.cookingSlotId!!
                    val startsAt = extras.menuItem?.availableLater?.startsAt!!
                    val endsAt = extras.menuItem?.availableLater?.endsAt!!
                    isValid = cartManager.validateCartMatch(restaurant, cookingSlotId, startsAt, endsAt)
                } else {
                    val curCookingSlot = extras.cookingSlot
                    cookingSlotId = curCookingSlot?.id!!
                    isValid = cartManager.validateCartMatch(restaurant, cookingSlotId, curCookingSlot.orderFrom, curCookingSlot.endsAt)
                }
                if (isValid) {
                    viewModelScope.launch {
                        progressData.startProgress()
                        cartManager.updateCurCookingSlotId(cookingSlotId)
                        val result = cartManager.addOrUpdateCart(quantity, it, note)
                        if (result == OrderRepository.OrderRepoStatus.ADD_NEW_DISH_SUCCESS){
                            cartManager.forceCookingSlotChange(cookingSlotId)
                            eatersAnalyticsTracker.logEvent(Constants.EVENT_ADD_DISH, getAddDishData(note))
                            onFinishDishPage.postValue(FinishNavigation.FINISH_AND_BACK)
                        }else if(result == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                            onFinishDishPage.postValue(FinishNavigation.FINISH_AND_BACK)
                        }
                        progressData.endProgress()
                    }
                }else{
                    cartManager.setPendingRequestParams(cookingSlotId, quantity, dishId, note)
                }
            }
        } else {
            shakeAddToCartBtn.postRawValue(true)
        }
    }

    private fun getAddDishData(note: String? = null): Map<String, String> {
        val data = mutableMapOf<String, String>()

        data["dish_name"] = dishFullData.value?.name.toString()
        data["dish_id"] = dishFullData.value?.id.toString()
        data["dish_price"] = dishFullData.value?.price?.formatedValue.toString()
        data["dish_tags"] = extras.menuItem?.tags.isNullOrEmpty().not().toString()
        data["dish_quantity"] = dishQuantity.toString()
        data["dish_special_requests"] = note.isNullOrEmpty().not().toString()

        return data
    }

    fun onDishRemove(dishId: Long){
        viewModelScope.launch {
            val result = cartManager.removeOrderItems(dishId, true)
            if (result == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                onFinishDishPage.postValue(FinishNavigation.FINISH_AND_BACK)
            }
        }
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.trackPageEvent(eventType)
    }

    fun logEvent(eventName: String) {
        when(eventName){
            Constants.EVENT_CHANGE_DISH_QUANTITY -> {
                eatersAnalyticsTracker.logEvent(eventName, getDishQuantityData())
            }
            else -> {
                eatersAnalyticsTracker.logEvent(eventName)
            }
        }
    }

    private fun logUpdateDish() {
        eatersAnalyticsTracker.logEvent(Constants.EVENT_UPDATE_DISH, getUpdateDishData())
    }

    private fun getUpdateDishData(): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["dish_name"] = dishFullData.value?.name.toString()
        data["dish_id"] = dishFullData.value?.id.toString()
        data["dish_price"] = dishFullData.value?.price?.formatedValue.toString()
        data["dish_quantity"] = dishQuantity.toString()
        return data
    }

    private fun getDishQuantityData(): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["dish_name"] = dishFullData.value?.name.toString()
        data["dish_id"] = dishFullData.value?.id.toString()
        return data
    }

    fun reloadPage() {
        initData(extras)
    }
}
