package com.bupp.wood_spoon_eaters.features.active_orders_tracker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderUserInfo
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng
import kotlin.math.ln
import kotlin.math.sin


class ActiveOrderTrackerViewModel(
    val api: ApiService,
    val eaterDataManager: EaterDataManager,
    private val paymentManager: PaymentManager,
    private val metaDataRepository: MetaDataRepository,
    private val flowEventsManager: FlowEventsManager,
    private val eventsManager: EventsManager
) : ViewModel() {

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
        paymentMethod?.let {
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

    fun logEvent(eventName: String) {
        eventsManager.logEvent(eventName)
    }

    data class FeesAndTaxData(val fee: String?, val tax: String?, val minFee: String? = null)

    val feeAndTaxDialogData = MutableLiveData<FeesAndTaxData>()
    fun onFeesAndTaxInfoClick() {
        getCurrentOrderDetails.value?.order.let { order ->
            var minOrderFee: String? = null
            order?.minOrderFee?.value?.let {
                if (it > 0) {
                    minOrderFee = order.minOrderFee.formatedValue
                }
            }
            feeAndTaxDialogData.postValue(FeesAndTaxData(order?.serviceFee?.formatedValue, order?.tax?.formatedValue, minOrderFee))
        }
    }

    data class MapData(val bounds: LatLngBounds, val markers: List<MarkerOptions>)



    val mapData = MutableLiveData<MapData>()
    fun onMapReady(context: Context) {
        getCurrentOrderDetails.value?.let {
            val curOrderData = it.order
            val curCourierData = it.order.courier
            val builder = LatLngBounds.Builder()

            val markers = mutableListOf<MarkerOptions>()

            val chefLat = curOrderData.restaurant?.pickupAddress?.lat
            val chefLng = curOrderData.restaurant?.pickupAddress?.lng

            val myLat = curOrderData.deliveryAddress?.lat
            val myLng = curOrderData.deliveryAddress?.lng
            myLat?.let {
                myLng?.let {
                    val myLocation = LatLng(myLat, myLng)
                    val marker = MarkerOptions().position(myLocation).icon(bitmapDescriptorFromVector(context, R.drawable.ic_pin))
                    markers.add(marker)
                    builder.include(myLocation)
                    Log.d("wowMapBinder", "myLocation $myLocation")
                }
            }
            if (curCourierData != null) {
                val courierLat = curCourierData.lat
                val courierLng = curCourierData.lng
                courierLat?.let {
                    courierLng?.let {
                        val courierLocation = LatLng(courierLat, courierLng)
                        val marker = MarkerOptions().position(courierLocation).icon(bitmapDescriptorFromVector(context, R.drawable.ic_courier_marker))
                        markers.add(marker)
                        builder.include(courierLocation)
                        Log.d("wowMapBinder", "courierLocation $courierLocation")
                    }
                }
            } else {
                chefLat?.let {
                    chefLng?.let {
                        val chefLocation = LatLng(chefLat, chefLng)
                        val marker = MarkerOptions().position(chefLocation).icon(bitmapDescriptorFromVector(context, R.drawable.ic_chef_marker))
                        markers.add(marker)
//                    mMap?.addMarker(MarkerOptions().position(chefLocation).icon(bitmapDescriptorFromVector(context, R.drawable.ic_cook_marker)))
                        builder.include(chefLocation)
                        Log.d("wowMapBinder", "chefLocation $chefLocation")
                    }
                }
            }

            val bounds = builder.build()
            mapData.postValue(MapData(bounds, markers))
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

}