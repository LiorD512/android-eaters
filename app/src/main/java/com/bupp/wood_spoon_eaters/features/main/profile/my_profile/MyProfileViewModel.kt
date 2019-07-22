package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings

class MyProfileViewModel(val api: ApiService, val appSettings: AppSettings, val eaterDataManager: EaterDataManager) : ViewModel() {

    data class UserDetails(val eater: Eater , val deliveryAddress: String?)

    val userDetails: SingleLiveEvent<UserDetails> = SingleLiveEvent()

    fun fetchUserDetails(){
        userDetails.postValue(UserDetails(getCurrentEater(), getDeliveryAddress()))
    }

    private fun getCurrentEater(): Eater {
        return eaterDataManager.currentEater!!
    }

    private fun getDeliveryAddress(): String? {
        return eaterDataManager.getLastChosenAddress()?.streetLine1
    }


}
