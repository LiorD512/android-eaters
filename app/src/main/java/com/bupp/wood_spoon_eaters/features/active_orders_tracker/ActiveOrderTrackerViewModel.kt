package com.bupp.wood_spoon_eaters.features.active_orders_tracker

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderUserInfo
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.network.ApiService
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class ActiveOrderTrackerViewModel(val api: ApiService, val eaterDataManager: EaterDataManager, val paymentManager: PaymentManager) : ViewModel() {

    var orderId: Long? = null
    val traceableOrdersLiveData = eaterDataManager.getTraceableOrders()
    private val compositeDisposable: ArrayList<Disposable> = arrayListOf()
    private var disposable: Disposable? = null

    var repeatJob: Job? = null

//    val orderDetails: SingleLiveEvent<OrderDetailsEvent> = SingleLiveEvent()
//    data class OrderDetailsEvent(/*val order: Order,*/val orderProgress: Int, val arrivalTime:String, val isNewMsgs:Boolean)

    fun getShareText(): String {
        val inviteUrl = eaterDataManager.currentEater?.shareCampaign?.inviteUrl
        val text = eaterDataManager.currentEater?.shareCampaign?.shareText
        return "$text \n $inviteUrl"
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

    val paymentCardEvent = SingleLiveEvent<Boolean>()
    private fun getOrderUserInfo(): OrderUserInfo? {
        var paymentString = "Fetching data...."
        val paymentMethod = paymentManager.getStripeCurrentPaymentMethod()
        if (paymentMethod != null) {
            paymentString = "${paymentMethod.card?.brand} ending in ${paymentMethod.card?.last4}"
        }

        val userName = eaterDataManager.currentEater?.getFullName()
        val phoneNumber = eaterDataManager.currentEater?.phoneNumber
        val userInfo = "$userName, $phoneNumber"

        val userLocation = eaterDataManager.getLastChosenAddress()

        return OrderUserInfo(paymentString, userInfo, userLocation)
    }


    fun startSilentUpdate() {
        repeatJob = repeatRequest()
    }

    fun endUpdates() {
        Log.d("wowActiveOrderTrackerVM", "endUpdates for id: $orderId")
        repeatJob?.cancel()
    }


}