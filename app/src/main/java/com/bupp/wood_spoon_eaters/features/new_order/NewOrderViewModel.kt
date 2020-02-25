package com.bupp.wood_spoon_eaters.features.new_order

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
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

    var menuItemId: Long = -1
    var isCheckout: Boolean = false
    var isEvent: Boolean = false

    data class NavigationEvent(val menuItemId: Long = -1, val isCheckout: Boolean = false)
    val navigationEvent = MutableLiveData<NavigationEvent>()
    fun setIntentParams(menuItemId: Long = -1, isCheckout: Boolean = false, isEvent: Boolean) {
        this.menuItemId = menuItemId
        this.isCheckout = isCheckout
        this.isEvent = isEvent
        navigationEvent.postValue(NavigationEvent(menuItemId, isCheckout))
    }

    val orderStatusEvent: SingleLiveEvent<OrderStatusEvent> = SingleLiveEvent()
    data class OrderStatusEvent(val hasActiveOrder: Boolean = false)
    fun checkOrderStatus(){
        if(orderManager.haveCurrentActiveOrder()){
            orderStatusEvent.postValue(OrderStatusEvent(true))
        }else{
            orderStatusEvent.postValue(OrderStatusEvent(false))
        }
    }

    fun initNewOrder(){
        orderManager.clearCurrentOrder()
        orderManager.initNewOrder()
    }

    fun initStripe(activity: Activity) {
        PaymentConfiguration.init(metaDataManager.getStripePublishableKey())
        CustomerSession.initCustomerSession(activity, EphemeralKeyProvider(this), false)
    }


    val ephemeralKeyProvider: SingleLiveEvent<EphemeralKeyProviderEvent> = SingleLiveEvent()
    data class EphemeralKeyProviderEvent(val isSuccess: Boolean = false)
    override fun onEphemeralKeyProviderError() {
        ephemeralKeyProvider.postValue(EphemeralKeyProviderEvent(false))
    }

//    fun getListOfAddresses(): ArrayList<Address>? {
//        if(eaterDataManager.currentEater != null){
//            return eaterDataManager.currentEater!!.addresses
//        }
//        return arrayListOf()
//    }
//
//    fun getChosenAddress(): Address?{
//        return eaterDataManager.getLastChosenAddress()
//    }

    fun setChosenAddress(address: Address){
        eaterDataManager.setUserChooseSpecificAddress(true)
        eaterDataManager.setLastChosenAddress(address)
    }

//    fun clearCart() {
//        orderManager.clearCurrentOrder()
//    }

    fun loadPreviousDish() {
        navigationEvent.postValue(NavigationEvent(menuItemId = menuItemId))
    }


}
