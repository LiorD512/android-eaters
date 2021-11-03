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
import com.bupp.wood_spoon_eaters.managers.EventsManager
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
    private val eventsManager: EventsManager
) : ViewModel() {

    private var isEditMode = false
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

    fun initData(extras: DishInitParams) {
        Log.d("orderFlow - dishPage","initData")
        this.extras = extras
        if (extras.menuItem != null) {
            currentFragmentState = DishFragmentState.ADD_DISH
            handleMenuItemData(extras.menuItem)
            getFullDish(extras.menuItem.id)

            if (extras.cookingSlot?.id != extras.menuItem.cookingSlotId) {
                Log.d(TAG, "this is a different Cooking slot")
                dishMatchCookingSlot = false
            }
            eventsManager.logEvent(Constants.EVENT_CLICK_ON_DISH, getDishClicked(extras))

        }else if(extras.orderItem != null){
            currentFragmentState = DishFragmentState.UPDATE_DISH
            isEditMode = true
            handleOrderItemData(extras.orderItem)
            extras.orderItem.menuItem?.id?.let { getFullDish(it) }
            dishMatchCookingSlot = true
        }
    }

    private fun handleMenuItemData(menuItem: MenuItem) {
        Log.d("orderFlow - dishPage","handleMenuItemData")
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
        Log.d("orderFlow - dishPage","handleOrderItemData")
        orderItemData.postValue(orderItem)
        val quantityInCart = cartManager.getQuantityInCart(orderItem.dish.id)
        dishMaxQuantity = (orderItem.menuItem?.getQuantityCount() ?: 0) - quantityInCart + orderItem.quantity
        counterBtnsState.postValue(CounterBtnState(orderItem.quantity, dishMaxQuantity))
    }

    private fun getFullDish(menuItemId: Long) {
        Log.d("orderFlow - dishPage","getFullDish")
        menuItemId.let {
            skeletonProgressData.startProgress()
            viewModelScope.launch {
                val fullDishResult = cartManager.getFullDish(it)
                val fullDish = fullDishResult?.data
                if(fullDish == null){
                    networkError.postValue(true)
                }else{
                    dishFullData.postValue(fullDish!!)
                    getUserRequestData(fullDish.restaurant)
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
        Log.d("orderFlow - dishPage","updateDishQuantity")
        dishQuantity = quantity

        var overallPrice = ""
        if(isEditMode){
            orderItemData.value?.let {
                it.price.value?.let {
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
        dishQuantityChange.postValue(DishQuantityData(quantity, overallPrice))
    }

    fun onPerformClearCart() {
        Log.d("orderFlow - dishPage","onPerformClearCart")
        eventsManager.logEvent(Constants.EVENT_CLEAR_CART)
        cartManager.onCartCleared()
        viewModelScope.launch {
            val startNewCartAction = cartManager.checkForPendingActions()
            if(startNewCartAction == OrderRepository.OrderRepoStatus.ADD_NEW_DISH_SUCCESS){
                onFinishDishPage.postValue(FinishNavigation.FINISH_AND_BACK)
            }
        }
    }

    fun onDishPageCartClick(note: String? = null) {
        Log.d("orderFlow - dishPage","onDishPageCartClick")
        if(isEditMode){
            updateOrderItem(note)
        }else{
            addDishToCart(note)
        }
    }

    private fun updateOrderItem(note: String? = null){
        Log.d("orderFlow - dishPage","updateOrderItem")
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
        Log.d("orderFlow - dishPage","addDishToCart")
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
                            eventsManager.logEvent(Constants.EVENT_ADD_DISH, getAddDishData(note))
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
        Log.d("orderFlow - dishPage","onDishRemove")
        viewModelScope.launch {
            val result = cartManager.removeOrderItems(dishId)
            if (result == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                onFinishDishPage.postValue(FinishNavigation.FINISH_AND_BACK)
            }
        }
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.logPageEvent(eventType)
    }

    fun logEvent(eventName: String) {
        when(eventName){
            Constants.EVENT_CHANGE_DISH_QUANTITY -> {
                eventsManager.logEvent(eventName, getDishQuantityData())
            }
            else -> {
                eventsManager.logEvent(eventName)
            }
        }
    }

    private fun logUpdateDish() {
        eventsManager.logEvent(Constants.EVENT_UPDATE_DISH, getUpdateDishData())
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

    companion object{
        const val TAG = "dishPageVM"
    }

}
