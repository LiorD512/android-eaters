package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.utils.DateUtils
import kotlinx.coroutines.launch


class CheckoutViewModel(private val cartManager: CartManager, private val paymentManager: PaymentManager, val eaterDataManager: EaterDataManager) :
    ViewModel() {


    val progressData = ProgressData()
    val getStripeCustomerCards = paymentManager.getPaymentsLiveData()


    val timeChangeEvent = LiveEventData<List<MenuItem>>()
    fun getDeliveryTimeLiveData() = eaterDataManager.getDeliveryTimeLiveData()

    fun simpleUpdateOrder(orderRequest: OrderRequest) {
        viewModelScope.launch {
            progressData.startProgress()
            val result = cartManager.postUpdateOrder(orderRequest)
            progressData.endProgress()
            when (result?.type) {
                OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {

                }
                OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {

                }
            }
        }
    }

    fun onTimeChangeClick() {
        val menuItems = cartManager.currentShowingDish?.availableMenuItems
        menuItems?.let{
            timeChangeEvent.postRawValue(it)
        }
    }


    val shippingMethodsEvent = MutableLiveData<List<ShippingMethod>>()
    fun onNationwideShippingSelectClick() {
        progressData.startProgress()
        viewModelScope.launch {
            val result = cartManager.getUpsShippingRates()
            when (result?.type) {
                OrderRepository.OrderRepoStatus.GET_SHIPPING_METHOD_SUCCESS -> {
                    Log.d(TAG, "onNationwideShippingSelectClick - success")
                    shippingMethodsEvent.postValue(result.data!!)
                }
                OrderRepository.OrderRepoStatus.GET_SHIPPING_METHOD_FAILED -> {
                    Log.d(TAG, "onNationwideShippingSelectClick - failed")
                }
                else -> {
                    Log.d(TAG, "onNationwideShippingSelectClick - failed")
                }
            }
            progressData.endProgress()
        }
    }

    fun refreshUi(){
        cartManager.refreshOrderUi()
    }

    fun updateOrderShippingMethod(shippingService: String) {
        viewModelScope.launch {
            cartManager.updateShippingService(shippingService)
        }

    }

    companion object{
        const val TAG = "wowCheckoutVM"
    }

}
