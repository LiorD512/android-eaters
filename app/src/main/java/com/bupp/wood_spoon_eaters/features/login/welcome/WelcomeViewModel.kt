package com.bupp.wood_spoon_eaters.features.login.welcome

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.network.ApiService


class WelcomeViewModel(val api: ApiService) : ViewModel() {

    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(val isLoginSuccess: Boolean = false)

    fun onLoginSuccess() {
        navigationEvent.postValue(NavigationEvent(true))
    }

    fun getSettings() {

    }


}