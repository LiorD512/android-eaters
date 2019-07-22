package com.bupp.wood_spoon_eaters.dialogs.rating_dialog

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.Review
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings

class RatingsViewModel(private val api: ApiService, private val appSettings: AppSettings) : ViewModel() {

    val reviewList: SingleLiveEvent<ArrayList<Review>> = SingleLiveEvent()
    val ratingDetails: SingleLiveEvent<RatingsDetails> = SingleLiveEvent()

    data class RatingsDetails(val avgRating:Double,val accuracyRating: Double, val deliveryRating: Double, val tasteRating: Double)

    fun getDumbRatingDetails() {
        ratingDetails.postValue(RatingsDetails(
            avgRating = 4.21,
            accuracyRating = 4.8,
            deliveryRating = 4.6,
            tasteRating = 5.0))
    }


}