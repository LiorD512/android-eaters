package com.bupp.wood_spoon_eaters.features.order_checkout.checkout

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.launch


class CheckoutViewModel(private val cartManager: CartManager, private val paymentManager: PaymentManager, val eaterDataManager: EaterDataManager, private val eventsManager: EventsManager) :
    ViewModel() {


    //////todo - add this -> eventsManager.proceedToCheckoutEvent()

    val progressData = ProgressData()
    val getStripeCustomerCards = paymentManager.getPaymentsLiveData()
    val orderLiveData = cartManager.getCurrentOrderData()

    val timeChangeEvent = LiveEventData<List<MenuItem>>()
//    fun getDeliveryTimeLiveData() = eaterDataManager.getDeliveryTimeLiveData()

    init{
        fetCurrentOrderData()
    }

    private fun fetCurrentOrderData() {
    }

    fun updateOrderParams(orderRequest: OrderRequest, eventType: String? = null) {
        viewModelScope.launch {
            progressData.startProgress()
            val result = cartManager.updateOrderParams(orderRequest, eventType)
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
        val menuItems = cartManager.getAvailableMenuItems()
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
//
//    fun refreshUi(){
//        oldCartManager.refreshOrderUi()
//    }
//
    fun updateOrderShippingMethod(shippingService: String) {
        viewModelScope.launch {
            cartManager.updateShippingService(shippingService)
        }
    }


    data class FeesAndTaxData(val fee: String?, val tax: String?, val minOrderFee: String? = null)
    val feeAndTaxDialogData = MutableLiveData<FeesAndTaxData>()
    fun onFeesAndTaxInfoClick() {
        val curOrder = cartManager.getCurrentOrderData().value
        curOrder?.let {
            var minOrderFee: String? = null
            curOrder.minOrderFee?.value?.let {
                if (it > 0) {
                    minOrderFee = curOrder.minOrderFee.formatedValue
                }
            }
            feeAndTaxDialogData.postValue(FeesAndTaxData(curOrder.serviceFee?.formatedValue, curOrder.tax?.formatedValue, minOrderFee))
        }
    }

    companion object{
        const val TAG = "wowCheckoutVM"
    }

}
