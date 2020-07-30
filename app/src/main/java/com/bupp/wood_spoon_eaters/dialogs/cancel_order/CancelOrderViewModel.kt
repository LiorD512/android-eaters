package com.bupp.wood_spoon_eaters.dialogs.cancel_order

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.google.interfaces.GoogleApi
import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelOrderViewModel(val api: ApiService) : ViewModel() {

    val cancelOrder: SingleLiveEvent<CancelOrderEvent> = SingleLiveEvent()
    data class CancelOrderEvent(val isSuccess: Boolean)

    fun cancelOrder(orderId: Long?, note: String? = null){
        orderId?.let{
            api.cancelOrder(orderId, note).enqueue(object: Callback<ServerResponse<Void>>{
                override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                    if(response.isSuccessful){
                        Log.d("wowCancelOrderVM","cancelOrder success")
                        cancelOrder.postValue(CancelOrderEvent(true))
                    }else{
                        Log.d("wowCancelOrderVM","cancelOrder fail")
                        cancelOrder.postValue(CancelOrderEvent(false))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                    Log.d("wowCancelOrderVM","cancelOrder big fail: ${t.message}")
                    cancelOrder.postValue(CancelOrderEvent(false))
                }
            })
        }
    }

}