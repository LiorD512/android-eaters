package com.bupp.wood_spoon_eaters.features.order_checkout.checkout

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.launch
import java.util.*


class CheckoutViewModel(private val cartManager: CartManager, private val paymentManager: PaymentManager, val eaterDataManager: EaterDataManager, private val eventsManager: EventsManager) :
    ViewModel() {


    //////todo - add this -> eventsManager.proceedToCheckoutEvent()

    val progressData = ProgressData()
    val getStripeCustomerCards = paymentManager.getPaymentsLiveData()
    val orderLiveData = cartManager.getCurrentOrderData()
    val deliveryDatesUi = cartManager.getDeliveryDatesUi()
    val deliveryDatesLiveData = MutableLiveData<List<DeliveryDates>>()

    val onCheckoutDone = LiveEventData<Boolean>()

    val wsErrorEvent = cartManager.getWsErrorEvent()
    val validationError = SingleLiveEvent<OrderValidationErrorType>()
    enum class OrderValidationErrorType {
        SHIPPING_METHOD_MISSING,
        PAYMENT_METHOD_MISSING
    }

    data class TimePickerData(val deliveryDates: List<DeliveryDates>, val selectedDate: Date? = null)
    val timeChangeEvent = LiveEventData<TimePickerData>()

    init{
        fetchOrderDeliveryTimes()

    }

    private fun refreshDeliveryTime() {
        cartManager.calcCurrentOrderDeliveryTime()
    }

    private fun fetchOrderDeliveryTimes(isPendingRequest: Boolean = false) {
        Log.d(TAG, "fetchOrderDeliveryTimes: $isPendingRequest")
        orderLiveData.value?.let{
            viewModelScope.launch {
                val result = cartManager.fetchOrderDeliveryTimes(it.id)
                result?.let{
                    deliveryDatesLiveData.postValue(it)
                    refreshDeliveryTime()
                }
            }
            if(isPendingRequest){
                onTimeChangeClick()
            }
        }
    }

    fun refreshCheckoutPage(){
        cartManager.refreshOrderLiveData()
    }

    fun updateOrderParams(orderRequest: OrderRequest, eventType: String? = null) {
        viewModelScope.launch {
            progressData.startProgress()
            val result = cartManager.updateOrderParams(orderRequest, eventType)
            progressData.endProgress()
            when (result?.type) {
                OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                    cartManager.calcCurrentOrderDeliveryTime()
                }
                OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                }
                OrderRepository.OrderRepoStatus.WS_ERROR -> {
                    handleWsError(result.wsError)
                }
            }
        }
    }

    private fun handleWsError(wsError: List<WSError>?) {
        var errorList = ""
        wsError?.forEach {
            errorList += "${it.msg} \n"
        }
        wsErrorEvent.postRawValue(errorList)
    }

    fun onTimeChangeClick() {
        if(deliveryDatesLiveData.value != null){
            timeChangeEvent.postRawValue(TimePickerData(deliveryDatesLiveData.value!!, cartManager.getCurrentDeliveryAt()))
        }else{
            fetchOrderDeliveryTimes(true)
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

    private fun finalizeOrder() {
        viewModelScope.launch {
            val paymentMethodId = paymentManager.getStripeCurrentPaymentMethod()?.id
            progressData.startProgress()
            val result = cartManager.finalizeOrder(paymentMethodId)
            when (result?.type) {
                OrderRepository.OrderRepoStatus.FINALIZE_ORDER_SUCCESS -> {
                    Log.d(TAG, "finalizeOrder - success")
                    cartManager.onCartCleared()
                    onCheckoutDone.postRawValue(true)
                }
                OrderRepository.OrderRepoStatus.FINALIZE_ORDER_FAILED -> {
                    Log.d(TAG, "finalizeOrder - failed")
                }
                OrderRepository.OrderRepoStatus.WS_ERROR -> {
                    Log.d(TAG, "finalizeOrder - ws error")

                }
            }
            progressData.endProgress()
        }
    }

    fun onPlaceOrderClick() {
        if (validateOrderData()) {
            finalizeOrder()
        }
    }

    private fun validateOrderData(): Boolean {
        if (cartManager.checkShippingMethodValidation()) {
            validationError.postValue(OrderValidationErrorType.SHIPPING_METHOD_MISSING)
            return false
        }
        val paymentMethod = paymentManager.getStripeCurrentPaymentMethod()?.id
        if (paymentMethod == null) {
            validationError.postValue(OrderValidationErrorType.PAYMENT_METHOD_MISSING)
            return false
        }
        return true
    }



    companion object{
        const val TAG = "wowCheckoutVM"
    }

}
