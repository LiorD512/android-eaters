package com.bupp.wood_spoon_eaters.features.restaurant.dish_page

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishInitParams
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.model.Restaurant
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class DishPageViewModel(
    val userRepository: UserRepository,
    val cartManager: CartManager
) : ViewModel() {

    private var isEditMode = false
    lateinit var extras: DishInitParams
    private var finishToFeed: Boolean = true
    private var dishMatchCookingSlot: Boolean = true
    //this param indicates if the current menuItem is matching the selected cooking slot in parent

    val progressData = ProgressData()
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
        }else if(extras.orderItem != null){
            currentFragmentState = DishFragmentState.UPDATE_DISH
            finishToFeed = extras.finishToFeed
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
                fullDish?.let {
                    dishFullData.postValue(it)
                    getUserRequestData(it.restaurant)
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
                    onFinishDishPage.postValue(getFinishPageType())
                }
            }
        }
    }

    private fun getFinishPageType(): FinishNavigation {
        Log.d("orderFlow - dishPage","getFinishPageType")
        return if(finishToFeed){
            FinishNavigation.FINISH_ACTIVITY
        }else{
            FinishNavigation.FINISH_AND_BACK
        }
    }

    private fun addDishToCart(note: String? = null) {
        Log.d("orderFlow - dishPage","addDishToCart")
        val quantity = dishQuantity
        if (quantity > 0) {
            val dishId = dishFullData.value?.id
            dishId?.let {
                var isValid = false
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

    fun onDishRemove(dishId: Long){
        Log.d("orderFlow - dishPage","onDishRemove")
        viewModelScope.launch {
            val result = cartManager.removeOrderItems(dishId)
            if (result == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                onFinishDishPage.postValue(FinishNavigation.FINISH_AND_BACK)
            }
        }
    }


    companion object{
        const val TAG = "dishPageVM"
    }

}
