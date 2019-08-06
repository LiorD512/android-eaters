package com.bupp.wood_spoon_eaters.dialogs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.google.interfaces.GoogleApi
import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RateLastOrderViewModel(private val api: ApiService,private val metaDataManager: MetaDataManager) : ViewModel() {

    val getLastOrder: SingleLiveEvent<LastOrderEvent> = SingleLiveEvent()
    data class LastOrderEvent(val isSuccess: Boolean = false, val order: Order? = null)

    fun getLastOrder(orderId: Long) {
        api.getOrderById(orderId).enqueue(object: Callback<ServerResponse<Order>> {
            override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
                if(response.isSuccessful){
                    val order = response.body()?.data
                    if(order != null){
                        getLastOrder.postValue(LastOrderEvent(true, order))
                    }else{
                        getLastOrder.postValue(LastOrderEvent(false,null))
                    }
                }else{
                    Log.d("wowFeedVM","postOrder fail")
                    getLastOrder.postValue(LastOrderEvent(false,null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
                Log.d("wowFeedVM","postOrder big fail")
                getLastOrder.postValue(LastOrderEvent(false,null))
            }
        })
    }


    val postRating: SingleLiveEvent<PostRatingEvent> = SingleLiveEvent()
    data class PostRatingEvent(val isSuccess: Boolean = false)
    fun postRating(orderId: Long, reviewRequest: ReviewRequest) {
        api.postReview(orderId, reviewRequest).enqueue(object: Callback<ServerResponse<Void>>{
            override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                if(response.isSuccessful){
                    Log.d("wowRateOrderVM","postReview success")
                    postRating.postValue(PostRatingEvent(true))
                }else{
                    Log.d("wowRateOrderVM","postReview fail")
                    postRating.postValue(PostRatingEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                Log.d("wowRateOrderVM","postReview big fail")
                postRating.postValue(PostRatingEvent(false))
            }

        })
    }

}