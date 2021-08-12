package com.bupp.wood_spoon_eaters.features.restaurant.dish_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.ExtrasDishPage
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.OldCartManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import kotlinx.coroutines.launch
import java.util.*

class DishPageViewModel(
    val oldCartManager: OldCartManager,
    val userRepository: UserRepository,
    val cartManager: CartManager
) : ViewModel() {

    lateinit var extras :ExtrasDishPage

    val progressData = ProgressData()
    val menuItemData = MutableLiveData<MenuItem>()
    val dishFullData = MutableLiveData<FullDish>()
    val onFinishDishPage = MutableLiveData<Boolean>()

    val clearCartEvent = cartManager.getClearCartUiEvent()

    val userRequestData = MutableLiveData<UserRequest>()
    class UserRequest(val eaterName: String,val cook: Restaurant)


    private var dishQuantity = 0
    var dishMaxQuantity = 0

    val dishQuantityState = MutableLiveData<DishQuantityState>()
    enum class DishQuantityState{
        ZERO_QUANTITY,
        REGULAR,
        MAX_QUANTITY
    }

    fun initData(extras: ExtrasDishPage) {
        this.extras = extras
        handleMenuItemData(extras.menuItem)
        getFullDish(extras.menuItem.id)
    }

    private fun handleMenuItemData(menuItem: MenuItem) {
        menuItemData.postValue(menuItem)
        dishQuantity = 0
        dishMaxQuantity = menuItem.quantity
    }

    private fun getFullDish(menuItemId: Long) {
        menuItemId.let {
            progressData.startProgress()
            viewModelScope.launch {
                val getFullDishResult = oldCartManager.getFullDish(it)
                getFullDishResult?.let {
                    dishFullData.postValue(it.fullDish)
                    getUserRequestData(it.fullDish.restaurant)
                }
                progressData.endProgress()
            }
        }
    }

    private fun getUserRequestData(restaurant: Restaurant) {
        userRequestData.postValue(UserRequest(eaterName = userRepository.getUser()?.getFullName()?:"", restaurant))
    }

    fun updateDishQuantity(quantity: Int){
        dishQuantity = quantity
        when (quantity) {
            0 -> {
                dishQuantityState.postValue(DishQuantityState.ZERO_QUANTITY)
            }
            dishMaxQuantity -> {
                dishQuantityState.postValue(DishQuantityState.MAX_QUANTITY)
            }
            else -> {
                dishQuantityState.postValue(DishQuantityState.REGULAR)
            }
        }
    }

    fun onPerformClearCart() {
        viewModelScope.launch {
            cartManager.onCartCleared()
        }
    }

    fun addDishToCart(note: String? = null) {
        val quantity = dishQuantity
        val dishId = dishFullData.value?.id
        dishId?.let{
            var isValid = false
            val restaurant = dishFullData.value?.restaurant!!
            var cookingSlotId: Long? = 0L
            if(extras.menuItem.availableLater != null){
                cookingSlotId = extras.menuItem.cookingSlotId!!
                val startsAt = extras.menuItem.availableLater?.startsAt!!
                val endsAt = extras.menuItem.availableLater?.endsAt!!
                isValid = cartManager.validateCartMatch(restaurant, cookingSlotId, startsAt, endsAt)
            }else{
                val curCookingSlot = extras.cookingSlot
                cookingSlotId = curCookingSlot?.id!!
                isValid = cartManager.validateCartMatch(restaurant, cookingSlotId, curCookingSlot.startsAt, curCookingSlot.endsAt)
            }
            if(isValid){
                viewModelScope.launch {
                    cartManager.updateCurCookingSlot(cookingSlotId)
                    val result = cartManager.addToCart(quantity, it, note)
                    if(result == OrderRepository.OrderRepoStatus.ADD_NEW_DISH_SUCCESS){
                        onFinishDishPage.postValue(true)
                    }

                }
            }
        }
    }

}
