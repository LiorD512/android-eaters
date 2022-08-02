package com.bupp.wood_spoon_chef.presentation.features.main.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.analytics.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.TrackedEvents
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.DishPricing
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.DateUtils
import io.shipbook.shipbooksdk.Log
import kotlinx.coroutines.launch

data class ApiOrdersEvent(
    val isSuccess: Boolean,
    val cookingSlots: List<CookingSlot>? = null
)

data class CancelCookingSlotEvent(
    val isSuccess: Boolean = true
)

class OrdersViewModel(
    val userRepository: UserRepository,
    private val cookingSlotRepository: CookingSlotRepository,
    private val chefAnalyticsTracker: ChefAnalyticsTracker
) : BaseViewModel() {

    val getCookingSlotEvent: MutableLiveData<ApiOrdersEvent> = MutableLiveData()
    val cancelCookingSlotEvent: MutableLiveData<CancelCookingSlotEvent> = MutableLiveData()

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

    fun cancelCookingSlot(cookingSlotId: Long) {
        viewModelScope.launch {
            progressData.startProgress()
            when (val response = cookingSlotRepository.cancelCookingSlot(cookingSlotId, null)) {
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

    fun trackAnalyticsEvent(analyticsEvent: AnalyticsEvent) {
        if (analyticsEvent.trackedArea == TrackedArea.ORDERS) {
            chefAnalyticsTracker.trackEvent(analyticsEvent.trackedEvent)
        }
    }
}