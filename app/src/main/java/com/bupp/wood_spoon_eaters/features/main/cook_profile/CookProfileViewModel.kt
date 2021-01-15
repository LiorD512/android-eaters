package com.bupp.wood_spoon_eaters.dialogs.web_docs

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.model.Review
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CookProfileViewModel(val api: ApiService, val metaDataRepository: MetaDataRepository) : ViewModel() {

    data class GetReviewsEvent(val isSuccess: Boolean = false, val reviews: Review? = null)
    val getReviewsEvent: SingleLiveEvent<GetReviewsEvent> = SingleLiveEvent()
    fun getDishReview(cookId: Long) {
        //nynynynyn
//        api.getDishReview(cookId).enqueue(object: Callback<ServerResponse<Review>> {
//            override fun onResponse(call: Call<ServerResponse<Review>>, response: Response<ServerResponse<Review>>) {
//                if(response.isSuccessful){
//                    val reviews = response.body()?.data
//                    Log.d("wowFeedVM","getDishReview success")
//                    getReviewsEvent.postValue(GetReviewsEvent(true, reviews))
//                }else{
//                    Log.d("wowFeedVM","getDishReview fail")
//                    getReviewsEvent.postValue(GetReviewsEvent(false))
//                }
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Review>>, t: Throwable) {
//                Log.d("wowFeedVM","getDishReview big fail: ${t.message}")
//                getReviewsEvent.postValue(GetReviewsEvent(false))
//            }
//        })
    }

//    fun getDeliveryFeeString(): String {
//        return metaDataManager.getDeliveryFeeStr()
//    }


}