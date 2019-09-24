package com.bupp.wood_spoon_eaters.dialogs.web_docs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.SingleDishViewModel
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.model.Review
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.google.interfaces.GoogleApi
import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CookProfileViewModel(val api: ApiService, val metaDataManager: MetaDataManager) : ViewModel() {

    data class GetReviewsEvent(val isSuccess: Boolean = false, val reviews: Review? = null)
    val getReviewsEvent: SingleLiveEvent<GetReviewsEvent> = SingleLiveEvent()
    fun getDishReview(cookId: Long) {
        api.getDishReview(cookId).enqueue(object: Callback<ServerResponse<Review>> {
            override fun onResponse(call: Call<ServerResponse<Review>>, response: Response<ServerResponse<Review>>) {
                if(response.isSuccessful){
                    val reviews = response.body()?.data
                    Log.d("wowFeedVM","getDishReview success")
                    getReviewsEvent.postValue(GetReviewsEvent(true, reviews))
                }else{
                    Log.d("wowFeedVM","getDishReview fail")
                    getReviewsEvent.postValue(GetReviewsEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Review>>, t: Throwable) {
                Log.d("wowFeedVM","getDishReview big fail: ${t.message}")
                getReviewsEvent.postValue(GetReviewsEvent(false))
            }
        })
    }

    fun getDeliveryFeeString(): String {
        return metaDataManager.getDeliveryFeeStr()
    }


}