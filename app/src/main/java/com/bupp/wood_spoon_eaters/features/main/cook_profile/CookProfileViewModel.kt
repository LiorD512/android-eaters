package com.bupp.wood_spoon_eaters.features.main.cook_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.model.Review
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import kotlinx.coroutines.launch

class CookProfileViewModel(val feedRepository: FeedRepository, val metaDataRepository: MetaDataRepository) : ViewModel() {

    val progressData = ProgressData()

    val getReviewsEvent: SingleLiveEvent<Review?> = SingleLiveEvent()
    fun getDishReview(cookId: Long) {
        viewModelScope.launch {
                progressData.startProgress()
            val result = feedRepository.getCookReview(cookId)
//                progressData.endProgress()
            when (result.type) {
                FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
                    Log.d(FeedViewModel.TAG, "NetworkError")
//                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                }
                FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
                    Log.d(FeedViewModel.TAG, "GenericError")
//                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                }
                FeedRepository.FeedRepoStatus.SUCCESS -> {
                    Log.d(FeedViewModel.TAG, "Success")
                    getReviewsEvent.postValue(result.review)
                }
                else -> {
                    Log.d(FeedViewModel.TAG, "NetworkError")
//                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                }
            }
        }
    }

//    fun getDeliveryFeeString(): String {
//        return metaDataManager.getDeliveryFeeStr()
//    }


}