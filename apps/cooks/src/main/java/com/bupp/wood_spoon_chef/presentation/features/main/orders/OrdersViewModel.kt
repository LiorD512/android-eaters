package com.bupp.wood_spoon_chef.presentation.features.main.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.DateUtils
import kotlinx.coroutines.launch

class OrdersViewModel(
    val userRepository: UserRepository,
    private val cookingSlotRepository: CookingSlotRepository
) : BaseViewModel() {

    val getCookingSlotEvent: MutableLiveData<ApiOrdersEvent> = MutableLiveData()

    data class ApiOrdersEvent(val isSuccess: Boolean, val cookingSlots: List<CookingSlot>? = null)


    fun getTraceableCookingSlots() {
        viewModelScope.launch {
            progressData.startProgress()
            when (val response = cookingSlotRepository.getTraceableCookingSlot()) {
                is ResponseSuccess -> {
                    getCookingSlotEvent.postValue(ApiOrdersEvent(true, response.data))
                }
                is ResponseError -> {
                    getCookingSlotEvent.postValue(ApiOrdersEvent(false))
                    errorEvent.postRawValue(response.error)
                }
            }
            progressData.endProgress()
        }
    }

    fun getShareText(cookingSlot: CookingSlot): String {
        val inviteUrl = userRepository.getCurrentChef()?.inviteUrl ?: ""
        val text = "Hey I'm cooking on: ${DateUtils.getCookingSlotStartString(cookingSlot)}\n" +
                "Download WoodSpoon and you can order my delicious dishes: \n"
        return "$text \n $inviteUrl"
    }

    data class CancelCookingSlotEvent(val isSuccess: Boolean = true)

    val cancelCookingSlotEvent: MutableLiveData<CancelCookingSlotEvent> = MutableLiveData()
    fun cancelCookingSlot(cookingSlotId: Long) {
        viewModelScope.launch {
            progressData.startProgress()
            when (val response = cookingSlotRepository.cancelCookingSlot(cookingSlotId)) {
                is ResponseSuccess -> {
                    cancelCookingSlotEvent.postValue(CancelCookingSlotEvent(true))
                }
                is ResponseError -> {
                    errorEvent.postRawValue(response.error)
                    cancelCookingSlotEvent.postValue(CancelCookingSlotEvent(false))
                }
            }
            progressData.endProgress()
        }
    }

    fun getEmptyLayoutTitle(): String {
        return "Hey ${userRepository.getUserData().value?.firstName}"
    }


    companion object {
        const val TAG = "wowOrdersVM"
    }

}