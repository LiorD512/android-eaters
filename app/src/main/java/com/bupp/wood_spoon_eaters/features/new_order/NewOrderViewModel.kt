package com.bupp.wood_spoon_eaters.features.new_order

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.stripe.android.CustomerSession
import com.stripe.android.PaymentConfiguration

class NewOrderViewModel(val metaDataManager: MetaDataManager, val orderManager: OrderManager, val eaterDataManager: EaterDataManager) : ViewModel(),
    EphemeralKeyProvider.EphemeralKeyProviderListener {

//    var menuItemId: Long = -1
//    var isCheckout: Boolean = false
//    var isEvent: Boolean = false

//    data class NavigationEvent(val menuItemId: Long = -1, val isCheckout: Boolean = false)
//    val navigationEvent = MutableLiveData<NavigationEvent>()
//    fun setIntentParams(menuItemId: Long = -1, isCheckout: Boolean = false, isEvent: Boolean) {
//        this.menuItemId = menuItemId
//        this.isCheckout = isCheckout
//        this.isEvent = isEvent
//        navigationEvent.postValue(NavigationEvent(menuItemId, isCheckout))
//    }
//
//    val orderStatusEvent: SingleLiveEvent<OrderStatusEvent> = SingleLiveEvent()
//    data class OrderStatusEvent(val hasActiveOrder: Boolean = false)
//    fun checkOrderStatus(){
//        if(orderManager.haveCurrentActiveOrder()){
//            orderStatusEvent.postValue(OrderStatusEvent(true))
//        }else{
//            orderStatusEvent.postValue(OrderStatusEvent(false))
//        }
//    }

//    val ephemeralKeyProvider: SingleLiveEvent<EphemeralKeyProviderEvent> = SingleLiveEvent()
//    data class EphemeralKeyProviderEvent(val isSuccess: Boolean = false)
//    override fun onEphemeralKeyProviderError() {
//        ephemeralKeyProvider.postValue(EphemeralKeyProviderEvent(false))
//    }


//    fun setChosenAddress(address: Address){
//        eaterDataManager.setUserChooseSpecificAddress(true)
//        eaterDataManager.setLastChosenAddress(address)
//    }


}
