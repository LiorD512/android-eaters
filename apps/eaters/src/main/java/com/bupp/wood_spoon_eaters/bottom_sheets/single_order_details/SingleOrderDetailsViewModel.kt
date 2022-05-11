package com.bupp.wood_spoon_eaters.bottom_sheets.single_order_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.experiments.PricingExperimentParams
import com.bupp.wood_spoon_eaters.experiments.PricingExperimentUseCase
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.WSError
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.launch

class SingleOrderDetailsViewModel(
    private val orderRepository: OrderRepository,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker,
    pricingExperimentUseCase: PricingExperimentUseCase
) : ViewModel() {

    val progressData = ProgressData()
    val errorEvent: SingleLiveEvent<List<WSError>> = SingleLiveEvent()
    var curOrder: Order? = null

    val pricingExperimentParamsLiveData: LiveData<PricingExperimentParams> = MutableLiveData(pricingExperimentUseCase.getExperimentParams())

    val singleOrderLiveData = MutableLiveData<Order>()
    fun initSingleOrder(orderId: Long) {
        viewModelScope.launch {
            progressData.startProgress()
            val result = orderRepository.getOrderById(orderId)
            when (result.type) {
                OrderRepository.OrderRepoStatus.GET_ORDER_BY_ID_SUCCESS -> {
                    result.data?.let {
                        curOrder = it
                        singleOrderLiveData.postValue(it)
                    }
                }
                OrderRepository.OrderRepoStatus.GET_ORDER_BY_ID_FAILED -> {
                    errorEvent.postValue(listOf(WSError(code = null, msg = "Error loading order...")))
                }
                OrderRepository.OrderRepoStatus.WS_ERROR -> {
                    result.wsError?.let {
                        errorEvent.postValue(it)
                    }
                }
                else -> {

                }
            }
            progressData.endProgress()
        }
    }

    data class FeesAndTaxData(val fee: String?, val tax: String?, val minFee: String? = null)

    val feeAndTaxDialogData = MutableLiveData<FeesAndTaxData>()
    fun onFeesAndTaxInfoClick() {
        curOrder?.let {
            var minOrderFee: String? = null
            it.minOrderFee?.value?.let {
                if (it > 0) {
                    minOrderFee = curOrder?.minOrderFee?.formatedValue
                }
            }
            feeAndTaxDialogData.postValue(FeesAndTaxData(curOrder?.serviceFee?.formatedValue, curOrder?.tax?.formatedValue, minOrderFee))
        }
    }

    fun logEvent(eventName: String, params: Map<String, String>? = null) {
        eatersAnalyticsTracker.logEvent(eventName, params)
    }
}