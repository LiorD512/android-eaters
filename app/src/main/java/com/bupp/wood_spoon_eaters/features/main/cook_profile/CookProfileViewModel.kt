//package com.bupp.wood_spoon_eaters.features.main.cook_profile
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.bupp.wood_spoon_eaters.di.abs.ProgressData
//import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
//import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
//import com.bupp.wood_spoon_eaters.managers.FeedDataManager
//import com.bupp.wood_spoon_eaters.model.Cook
//import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
//import com.bupp.wood_spoon_eaters.model.Review
//import com.bupp.wood_spoon_eaters.network.ApiService
//import com.bupp.wood_spoon_eaters.repositories.FeedRepository
//import kotlinx.coroutines.launch
//
//class CookProfileViewModel(private val feedDataManager: FeedDataManager, private val feedRepository: FeedRepository, val metaDataRepository: MetaDataRepository) : ViewModel() {
//
//    var currentCook: Cook? = null
//    val progressData = ProgressData()
//
//    val getCookEvent: SingleLiveEvent<Cook?> = SingleLiveEvent()
//    fun initCookData(cookId: Long?) {
//        cookId?.let{
//            val feedRequest = feedDataManager.getLastFeedRequest()
//            val lat = feedRequest.lat
//            val lng = feedRequest.lng
//            val addressId = feedRequest.addressId
//            viewModelScope.launch {
//                    progressData.startProgress()
//                val result = feedRepository.getCookById(cookId, addressId, lat, lng)
//                when (result.type) {
//                    FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
//                        Log.d(FeedViewModel.TAG, "NetworkError")
//    //                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
//                    }
//                    FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
//                        Log.d(FeedViewModel.TAG, "GenericError")
//    //                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
//                    }
//                    FeedRepository.FeedRepoStatus.SUCCESS -> {
//                        Log.d(FeedViewModel.TAG, "Success")
//                        getCookEvent.postValue(result.cook)
//                        currentCook = result.cook
//                    }
//                    else -> {
//                        Log.d(FeedViewModel.TAG, "NetworkError")
//    //                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
//                    }
//                }
//                progressData.endProgress()
//            }
//        }
//    }
//
//    val getReviewsEvent: SingleLiveEvent<Review?> = SingleLiveEvent()
//    fun getDishReview() {
//        currentCook?.let{
//            viewModelScope.launch {
//                    progressData.startProgress()
//                val result = feedRepository.getCookReview(it.id)
//    //                progressData.endProgress()
//                when (result.type) {
//                    FeedRepository.FeedRepoStatus.SERVER_ERROR -> {
//                        Log.d(FeedViewModel.TAG, "NetworkError")
//    //                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
//                    }
//                    FeedRepository.FeedRepoStatus.SOMETHING_WENT_WRONG -> {
//                        Log.d(FeedViewModel.TAG, "GenericError")
//    //                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
//                    }
//                    FeedRepository.FeedRepoStatus.SUCCESS -> {
//                        Log.d(FeedViewModel.TAG, "Success")
//                        getReviewsEvent.postValue(result.review)
//                    }
//                    else -> {
//                        Log.d(FeedViewModel.TAG, "NetworkError")
//    //                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
//                    }
//                }
//            }
//        }
//    }
//
////    fun getDeliveryFeeString(): String {
////        return metaDataManager.getDeliveryFeeStr()
////    }
//
//
//}