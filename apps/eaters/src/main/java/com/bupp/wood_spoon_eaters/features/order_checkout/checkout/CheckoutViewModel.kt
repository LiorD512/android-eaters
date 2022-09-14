package com.bupp.wood_spoon_eaters.features.order_checkout.checkout

import android.util.Log
import androidx.lifecycle.*
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.domain.FeatureFlagFreeDeliveryUseCase
import com.bupp.wood_spoon_eaters.domain.FeatureFlagNewAuthUseCase
import com.bupp.wood_spoon_eaters.domain.comon.execute
import com.bupp.wood_spoon_eaters.experiments.PricingExperimentParams
import com.bupp.wood_spoon_eaters.experiments.PricingExperimentUseCase
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.free_delivery.mapOrderToFreeDeliveryState
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.models.CheckoutAdapterItem
import com.bupp.wood_spoon_eaters.features.order_checkout.gift.GiftConfig
import com.bupp.wood_spoon_eaters.features.order_checkout.gift.GiftConfigUseCase
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.*
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.Utils.getErrorsMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class CheckoutViewModel(
    private val featureFlagFreeDeliveryUseCase: FeatureFlagFreeDeliveryUseCase,
    private val cartManager: CartManager,
    private val paymentManager: PaymentManager,
    val eaterDataManager: EaterDataManager,
    pricingExperimentUseCase: PricingExperimentUseCase,
    giftConfigUseCase: GiftConfigUseCase,
    appSettingsRepository: AppSettingsRepository,
    featureFlagNewAuthUseCase: FeatureFlagNewAuthUseCase
) : ViewModel() {

    private val isNewAuthFlowEnabled = featureFlagNewAuthUseCase.execute(null)
    val showContactDetailsSection = isNewAuthFlowEnabled

    val progressData = ProgressData()
    val getStripeCustomerCards = paymentManager.getPaymentsLiveData()
    val orderLiveData = cartManager.getCurrentOrderData()
    val deliveryDatesUi = cartManager.getDeliveryDatesUi()
    val freeDeliveryData =
        orderLiveData.map {
            it.mapOrderToFreeDeliveryState(
                appSettingsRepository.getCurrentFreeDeliveryThreshold(),
                featureFlagFreeDeliveryUseCase.execute(),
                true
            )
        }

    val deliveryDatesLiveData = MutableLiveData<List<DeliveryDates>>()
    val orderItemsData = MutableLiveData<List<CheckoutAdapterItem>>()

    val onCheckoutDone = LiveEventData<Boolean>()

    val wsErrorEvent = cartManager.getWsErrorEvent()
    val validationError = SingleLiveEvent<OrderValidationErrorType>()

    val pricingExperimentData: LiveData<PricingExperimentParams> = MutableLiveData(pricingExperimentUseCase.getExperimentParams())
    val giftConfigData: LiveData<GiftConfig> = MutableLiveData(giftConfigUseCase.execute())

    val userData = eaterDataManager.currentEaterFlow.asLiveData(Dispatchers.Main)

    enum class OrderValidationErrorType {
        SHIPPING_METHOD_MISSING,
        PAYMENT_METHOD_MISSING,
        SHIPPING_ADDRESS_MISSING,
        USER_DETAILS_MISSING
    }

    data class TimePickerData(val deliveryDates: List<DeliveryDates>, val selectedDate: Date? = null)

    val timeChangeEvent = LiveEventData<TimePickerData>()

    init {
        fetchOrderDeliveryTimes()
    }

    private fun refreshDeliveryTime() {
        cartManager.calcCurrentOrderDeliveryTime()
    }

    fun handleOrderItems(order: Order) {
        val list = mutableListOf<CheckoutAdapterItem>()
        val orderItems = order.orderItems
        orderItems?.forEach {
            val customCartItem = CustomOrderItem(
                orderItem = it,
                cookingSlot = order.cookingSlot,
            )
            list.add(CheckoutAdapterItem(customOrderItem = customCartItem))
        }
        orderItemsData.postValue(list)

//        order.deliverAt
    }

    fun fetchOrderDeliveryTimes(isPendingRequest: Boolean = false) {
        Log.d(TAG, "fetchOrderDeliveryTimes: $isPendingRequest")
        orderLiveData.value?.let {
            viewModelScope.launch {
                val result = cartManager.fetchOrderDeliveryTimes(it.id)
                result?.let {
                    deliveryDatesLiveData.postValue(it)
                    refreshDeliveryTime()
                }
                if (isPendingRequest) {
                    onTimeChangeClick()
                }
            }
        }
    }

    fun refreshCheckoutPage() {
        cartManager.refreshOrderLiveData()
    }

    fun updateDeliveryAt(deliveryAt: Date?) {
        cartManager.updateCurrentDeliveryAt(deliveryAt)
        updateOrderParams(OrderRequest(deliveryAt = DateUtils.parseUnixTimestamp(deliveryAt)))
    }

    fun updateOrderParams(orderRequest: OrderRequest, eventType: String? = null) {
        viewModelScope.launch {
            progressData.startProgress()
            val result = cartManager.updateOrderParams(orderRequest, eventType)
            when (result?.type) {
                OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                    cartManager.calcCurrentOrderDeliveryTime()
                }
                OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                }
                OrderRepository.OrderRepoStatus.WS_ERROR -> {
                    handleWsError(result.wsError)
                }
                else -> {
                }
            }
            progressData.endProgress()
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
        if (deliveryDatesLiveData.value != null) {
            timeChangeEvent.postRawValue(TimePickerData(deliveryDatesLiveData.value!!, cartManager.getCurrentDeliveryAt()))
        } else {
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

    val feeAndTaxDialogData = LiveEventData<FeesAndTaxData>()
    fun onFeesAndTaxInfoClick() {
        val curOrder = cartManager.getCurrentOrderData().value
        curOrder?.let {
            var minOrderFee: String? = null
            curOrder.minOrderFee?.value?.let {
                if (it > 0) {
                    minOrderFee = curOrder.minOrderFee.formatedValue
                }
            }
            feeAndTaxDialogData.postRawValue(FeesAndTaxData(curOrder.serviceFee?.formatedValue, curOrder.tax?.formatedValue, minOrderFee))
        }
    }

    private fun finalizeOrder() {
        viewModelScope.launch {
            progressData.startProgress()

            try {
                val paymentMethodId = paymentManager.getStripeCurrentPaymentMethod()
                val result = cartManager.finalizeOrder(paymentMethodId)

                when (result?.type) {
                    OrderRepository.OrderRepoStatus.FINALIZE_ORDER_SUCCESS -> {
                        cartManager.onCartCleared()
                        onCheckoutDone.postRawValue(true)
                    }
                    OrderRepository.OrderRepoStatus.FINALIZE_ORDER_FAILED -> {
                        wsErrorEvent.postRawValue(result.wsError?.getErrorsMsg() ?: "Something went wrong")
                    }
                    OrderRepository.OrderRepoStatus.WS_ERROR -> {
                        var error = result.wsError?.getErrorsMsg()
                        if (error.isNullOrEmpty()) {
                            error = "Failed to create your order please try again later"
                        }
                        wsErrorEvent.postRawValue(error)
                    }
                    else -> {
                    }
                }
            } catch (ex: Exception) {
                wsErrorEvent.postRawValue(ex.message ?: "Something went wrong")
            } finally {
                progressData.endProgress()
            }
        }
    }

    fun onPlaceOrderClick() {
        if (validateOrderData()) {
            finalizeOrder()
        }
    }

    fun validateOrderData(): Boolean {
        val paymentMethod = paymentManager.getStripeCurrentPaymentMethod()?.id
        if (paymentMethod == null) {
            validationError.postValue(OrderValidationErrorType.PAYMENT_METHOD_MISSING)
            return false
        }
        if (!eaterDataManager.hasUserSetAnAddress()) {
            validationError.postValue(OrderValidationErrorType.SHIPPING_ADDRESS_MISSING)
            return false
        }
        if (cartManager.checkShippingMethodValidation()) {
            validationError.postValue(OrderValidationErrorType.SHIPPING_METHOD_MISSING)
            return false
        }
        if (isNewAuthFlowEnabled && !eaterDataManager.hasUserSetDetails()) {
            validationError.postValue(OrderValidationErrorType.USER_DETAILS_MISSING)
            return false
        }
        return true
    }

    fun onLocationChanged() {
        viewModelScope.launch {
            cartManager.updateOrderDeliveryAddressParam()
            refreshCheckoutPage()
        }
    }

    fun updateDishInCart(quantity: Int, dishId: Long, note: String? = null, orderItemId: Long) {
        viewModelScope.launch {
            cartManager.updateDishInExistingCart(quantity, note, dishId, orderItemId)
        }
    }

    //Return true if last item in cart - for showing clear dialog popup
    fun removeSingleOrderItemId(orderItemId: Long): Boolean {
        if (cartManager.getMenuItemsCount() == 1) {
            return true
        }
        viewModelScope.launch {
            cartManager.removeOrderItems(orderItemId, true)
        }
        return false
    }

    fun clearCart() {
        viewModelScope.launch {
            cartManager.onCartCleared()
        }
    }

    companion object {
        const val TAG = "wowCheckoutVM"
    }

}
