package com.bupp.wood_spoon_eaters.features.main.my_profile

import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings

class MyProfileViewModel(val api: ApiService, val appSettings: AppSettings,val orderManager: OrderManager) : ViewModel() {

    data class UserDetails(val eater: Eater , val deliveryAddress: String?)

    val userDetails: SingleLiveEvent<UserDetails> = SingleLiveEvent()

    fun fetchUserDetails(){
        userDetails.postValue(UserDetails(getCurrentEater(), getDeliveryAddress()))
    }

    private fun getCurrentEater(): Eater {
        return appSettings.currentEater!!
    }

    private fun getDeliveryAddress(): String? {
        return orderManager.getLastOrderAddress()?.streetLine1
    }


}
