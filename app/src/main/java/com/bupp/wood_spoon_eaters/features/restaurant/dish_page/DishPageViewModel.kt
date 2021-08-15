package com.bupp.wood_spoon_eaters.features.restaurant.dish_page

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishInitParams
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.OldCartManager
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.model.Restaurant
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class DishPageViewModel(
    val oldCartManager: OldCartManager,
    val userRepository: UserRepository,
    val cartManager: CartManager
) : ViewModel() {

    private var isEditMode = false
    lateinit var extras: DishInitParams
    private var dishMatchCookingSlot: Boolean = true
    //this param indicates if the current menuItem is matching the selected cooking slot in parent

    val progressData = ProgressData()
    val wsErrorEvent = cartManager.getWsErrorEvent()

    val menuItemData = MutableLiveData<MenuItem>()
    val orderItemData = MutableLiveData<OrderItem>()
    val dishFullData = MutableLiveData<FullDish>()
    val onFinishDishPage = MutableLiveData<Boolean>()

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
        this.extras = extras
        if(extras.menuItem != null){
            handleMenuItemData(extras.menuItem)
            getFullDish(extras.menuItem.id)

            if(extras.cookingSlot?.id != extras.menuItem.cookingSlotId){
                Log.d(TAG, "this is a different Cooking slot")
                dishMatchCookingSlot = false
            }
        }else if(extras.orderItem != null){
            isEditMode = true
            handleOrderItemData(extras.orderItem)
            extras.orderItem.menuItem?.id?.let { getFullDish(it) }
            dishMatchCookingSlot = true
        }
    }

    private fun handleMenuItemData(menuItem: MenuItem) {
        menuItemData.postValue(menuItem)
        dishMaxQuantity = menuItem.quantity
    }

    private fun handleOrderItemData(orderItem: OrderItem) {
        orderItemData.postValue(orderItem)
        dishMaxQuantity = orderItem.menuItem?.quantity ?: -1
    }

    private fun getFullDish(menuItemId: Long) {
        menuItemId.let {
            progressData.startProgress()
            viewModelScope.launch {
                val fullDishResult = cartManager.getFullDish(it)
                val fullDish = fullDishResult?.data
                fullDish?.let {
                    dishFullData.postValue(it)
                    getUserRequestData(it.restaurant)
                }
                progressData.endProgress()
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
        cartManager.onCartCleared()
        viewModelScope.launch {
            val startNewCartAction = cartManager.checkForPendingActions()
            if(startNewCartAction == OrderRepository.OrderRepoStatus.ADD_NEW_DISH_SUCCESS){
                onFinishDishPage.postValue(true)
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
                val result = cartManager.updateDishInExistingCart(quantity, note, dishId, it)
                if (result == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                    onFinishDishPage.postValue(true)
                }
            }
        }
    }

    private fun addDishToCart(note: String? = null) {
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
                    isValid = cartManager.validateCartMatch(restaurant, cookingSlotId, curCookingSlot.startsAt, curCookingSlot.endsAt)
                }
                if (isValid) {
                    viewModelScope.launch {
                        cartManager.updateCurCookingSlotId(cookingSlotId)
                        cartManager.forceCookingSlotChange(cookingSlotId)
                        val result = cartManager.addDishToNewCart(quantity, it, note)
                        if (result == OrderRepository.OrderRepoStatus.ADD_NEW_DISH_SUCCESS) {
                            onFinishDishPage.postValue(true)
                        }
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
        viewModelScope.launch {
            val result = cartManager.removeOrderItems(dishId)
            if (result == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                onFinishDishPage.postValue(true)
            }
        }
    }


    companion object{
        const val TAG = "dishPageVM"
    }

}
