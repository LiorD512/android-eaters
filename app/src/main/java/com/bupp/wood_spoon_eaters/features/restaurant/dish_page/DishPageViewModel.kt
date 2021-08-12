package com.bupp.wood_spoon_eaters.features.restaurant.dish_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.ExtrasDishPage
import com.bupp.wood_spoon_eaters.managers.OldCartManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import kotlinx.coroutines.launch

class DishPageViewModel(
    val oldCartManager: OldCartManager,
    val userRepository: UserRepository
) : ViewModel() {

    lateinit var extras :ExtrasDishPage

    val progressData = ProgressData()
    val menuItemData = MutableLiveData<MenuItem>()
    val dishFullData = MutableLiveData<FullDish>()

    val userRequestData = MutableLiveData<UserRequest>()
    class UserRequest(val eaterName: String,val cook: Cook)


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
                    getUserRequestData(it.fullDish.cook)
                }
                progressData.endProgress()
            }
        }
    }

    private fun getUserRequestData(cook: Cook) {
        userRequestData.postValue(UserRequest(eaterName = userRepository.getUser()?.getFullName()?:"", cook))
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

}
