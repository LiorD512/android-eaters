package com.bupp.wood_spoon_chef.presentation.features.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.analytics.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.analytics.TrackedArea.Companion.BOTTOM_TABS
import com.bupp.wood_spoon_chef.analytics.TrackedEvents
import com.bupp.wood_spoon_chef.analytics.TrackedEvents.BottomTabs.CLICK_ON_BOTTOM_TAB_ORDERS
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.network.ErrorManger
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.UserSettings
import io.shipbook.shipbooksdk.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class GetActiveCookingSlotEvent(
    val isSuccess: Boolean,
    val cookingSlots: CookingSlot?
)

enum class NavigationEventType {
    SCROLL_TO_LOGOUT
}

class MainViewModel(
    val settings: UserSettings,
    val metaDataRepository: MetaDataRepository,
    val userRepository: UserRepository,
    private val cookingSlotRepository: CookingSlotRepository,
    private val chefAnalyticsTracker: ChefAnalyticsTracker
) : BaseViewModel() {

    val navigationEvent: MutableLiveData<NavigationEventType> = MutableLiveData()
    val getActiveCookingSlot: MutableLiveData<GetActiveCookingSlotEvent> = MutableLiveData()
    val notApprovedEvent = MutableLiveData<Boolean>()

    fun getActiveCookingSlot() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = cookingSlotRepository.getActiveCookingSlot()) {
                is ResponseSuccess -> {
                    val cookingSlot = result.data
                    getActiveCookingSlot.postValue(GetActiveCookingSlotEvent(true, cookingSlot))
                }
                is ResponseError -> {
                    errorEvent.postRawValue(result.error)
                }
            }
        }
    }

    fun checkMetaDataExist() {
        if (metaDataRepository.getMetaDataObject() == null) {
            viewModelScope.launch(Dispatchers.IO) {
                metaDataRepository.initMetaData()
            }
        }
    }

    fun isChefPending(): Boolean {
        val isPending = userRepository.isPendingApproval()
        if (isPending) {
            notApprovedEvent.postValue(true)
        }
        return isPending
    }

    fun getContactUsPhoneNumber(): String {
        return metaDataRepository.getContactUsPhoneNumber()
    }

    fun getContactUsTextNumber(): String {
        return metaDataRepository.getContactUsTextNumber()
    }

    fun onHeaderSettingsClick() {
        navigationEvent.postValue(NavigationEventType.SCROLL_TO_LOGOUT)
    }

    fun trackBottomTabClick(analyticsEvent: AnalyticsEvent) {
        if (analyticsEvent.trackedArea == BOTTOM_TABS) {
            chefAnalyticsTracker.trackEvent(analyticsEvent.trackedEvent)
        }
    }
}