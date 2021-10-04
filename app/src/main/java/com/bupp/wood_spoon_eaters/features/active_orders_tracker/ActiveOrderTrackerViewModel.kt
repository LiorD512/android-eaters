package com.bupp.wood_spoon_eaters.features.active_orders_tracker

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderUserInfo
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class ActiveOrderTrackerViewModel(val api: ApiService, val eaterDataManager: EaterDataManager, private val paymentManager: PaymentManager, private val metaDataRepository: MetaDataRepository, private val flowEventsManager: FlowEventsManager,
private val eventsManager: EventsManager) : ViewModel() {

    var orderId: Long? = null
    val traceableOrdersLiveData = eaterDataManager.getTraceableOrders()
    private var refreshRepeatedJob: Job? = null


    fun getShareText(): String {
//        val inviteUrl = eaterDataManager.currentEater?.shareCampaign?.inviteUrl
        val text = eaterDataManager.currentEater?.shareCampaign?.shareText
        return "$text \n "
    }

    val getCurrentOrderDetails: MutableLiveData<GetActiveOrdersEvent> = MutableLiveData()
    data class GetActiveOrdersEvent(val order: Order, val userInfo: OrderUserInfo? = null)

    fun getCurrentOrder(orderId: Long?) {
        orderId?.let {
            this.orderId = it
            val orders = traceableOrdersLiveData.value
            orders?.let {
                val currentOrder = it.find { it.id == orderId }
                currentOrder?.let {
                    getCurrentOrderDetails.postValue(GetActiveOrdersEvent(it, getOrderUserInfo()))
                }
            }
        }
    }

    private fun repeatRequest(): Job {
        return viewModelScope.launch {
            while (isActive) {
                //do your request
                Log.d("wowActiveOrderTrackerVM", "fetchFromServer start for id: $orderId")
                if (this@ActiveOrderTrackerViewModel.orderId != null) {
                    Log.d("wowActiveOrderTrackerVM", "fetching FromServer")
                    eaterDataManager.checkForTraceableOrders()
                    getCurrentOrder(orderId)
                }
                delay(10000)
            }
        }
    }

    private fun getOrderUserInfo(): OrderUserInfo {
        var paymentString = "Fetching data...."
        val paymentMethod = paymentManager.getStripeCurrentPaymentMethod()
        paymentMethod?.let{
            paymentString = "${paymentMethod.card?.brand} ending in ${paymentMethod.card?.last4}"
        }

        val userName = eaterDataManager.currentEater?.getFullName()
        val phoneNumber = eaterDataManager.currentEater?.phoneNumber
        val userInfo = "$userName, $phoneNumber"

        val userLocation = eaterDataManager.getLastChosenAddress()

        return OrderUserInfo(paymentString, userInfo, userLocation)
    }


    fun startSilentUpdate() {
        refreshRepeatedJob = repeatRequest()
    }

    fun endUpdates() {
        Log.d("wowActiveOrderTrackerVM", "endUpdates for id: $orderId")
        refreshRepeatedJob?.cancel()
    }

    fun sendOpenEvent() {
        eaterDataManager.logUxCamEvent(Constants.EVENT_TRACK_ORDER_CLICK)
    }

    fun getContactUsPhoneNumber(): String {
        return metaDataRepository.getContactUsPhoneNumber()
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.logPageEvent(eventType)
    }

    fun logEvent(eventName: String){
        eventsManager.logEvent(eventName)
    }

    data class FeesAndTaxData(val fee: String?, val tax: String?, val minFee: String? = null)
    val feeAndTaxDialogData = MutableLiveData<FeesAndTaxData>()
    fun onFeesAndTaxInfoClick() {
        getCurrentOrderDetails.value?.order.let{ order ->
            var minOrderFee: String? = null
            order?.minOrderFee?.value?.let {
                if (it > 0) {
                    minOrderFee = order.minOrderFee.formatedValue
                }
            }
            feeAndTaxDialogData.postValue(FeesAndTaxData(order?.serviceFee?.formatedValue, order?.tax?.formatedValue, minOrderFee))
        }
    }


}