package com.bupp.wood_spoon_eaters.dialogs.rate_last_order

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RateLastOrderViewModel(private val api: ApiService,private val metaDataRepository: MetaDataRepository) : ViewModel() {

    val getLastOrder: SingleLiveEvent<LastOrderEvent> = SingleLiveEvent()
    data class LastOrderEvent(val isSuccess: Boolean = false, val order: Order? = null)

    fun getLastOrder(orderId: Long) { //todo !
//        api.getOrderById(orderId).enqueue(object: Callback<ServerResponse<Order>> {
//            override fun onResponse(call: Call<ServerResponse<Order>>, response: Response<ServerResponse<Order>>) {
//                if(response.isSuccessful){
//                    val order = response.body()?.data
//                    if(order != null){
//                        getLastOrder.postValue(LastOrderEvent(true, order))
//                    }else{
//                        getLastOrder.postValue(LastOrderEvent(false,null))
//                    }
//                }else{
//                    Log.d("wowFeedVM","postOrder fail")
//                    getLastOrder.postValue(LastOrderEvent(false,null))
//                }
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Order>>, t: Throwable) {
//                Log.d("wowFeedVM","postOrder big fail")
//                getLastOrder.postValue(LastOrderEvent(false,null))
//            }
//        })
    }


    val postRating: SingleLiveEvent<PostRatingEvent> = SingleLiveEvent()
    data class PostRatingEvent(val isSuccess: Boolean = false)
    fun postRating(orderId: Long, reviewRequest: ReviewRequest) {
        api.postReview(orderId, reviewRequest).enqueue(object: Callback<ServerResponse<Any>>{
            override fun onResponse(call: Call<ServerResponse<Any>>, response: Response<ServerResponse<Any>>) {
                if(response.isSuccessful){
                    Log.d("wowRateOrderVM","postReview success")
                    postRating.postValue(PostRatingEvent(true))
                }else{
                    Log.d("wowRateOrderVM","postReview fail")
                    postRating.postValue(PostRatingEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Any>>, t: Throwable) {
                Log.d("wowRateOrderVM","postReview big fail")
                postRating.postValue(PostRatingEvent(false))
            }

        })
    }

}