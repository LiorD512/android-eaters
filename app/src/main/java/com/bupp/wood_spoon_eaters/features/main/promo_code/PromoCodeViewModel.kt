package com.bupp.wood_spoon_eaters.features.main.promo_code

import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.network.ApiService

class PromoCodeViewModel(val apiService: ApiService,val orderManager: OrderManager) : ViewModel() {



    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(val isCodeLegit: Boolean = false)

    fun savePromoCode(code: String) {
        navigationEvent.postValue(NavigationEvent(false))
    }
}
