package com.bupp.wood_spoon_eaters.bottom_sheets.reviews

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageViewModel
import com.bupp.wood_spoon_eaters.model.WSError
import com.bupp.wood_spoon_eaters.repositories.RestaurantRepository
import kotlinx.coroutines.launch

class ReviewsBSViewModel(private val restaurantRepository: RestaurantRepository) : ViewModel() {

    var restaurantId: Long = -1
    var restaurantName = ""
    var ratingHeader = ""

    val commentListData = MutableLiveData<List<CommentAdapterItem>?>()
    val errorEvent = LiveEventData<List<WSError>>()

    fun initData(restaurantId: Long, restaurantName: String, ratingHeader: String) {
        getSkeletonData()
        this.restaurantId = restaurantId
        this.restaurantName = restaurantName
        this.ratingHeader = ratingHeader
        getRestaurantReview()
    }

    fun reloadData(){
        getSkeletonData()
        getRestaurantReview()
    }

    private fun getSkeletonData() {
        val list = listOf(CommentSkeleton(), CommentSkeleton(), CommentSkeleton(), CommentSkeleton())
        commentListData.postValue(list)
    }

    private fun getRestaurantReview() {
        restaurantId.let { id ->
            viewModelScope.launch {
                val result = restaurantRepository.getCookReview(id)
                when (result.type) {
                    RestaurantRepository.RestaurantRepoStatus.EMPTY -> {
                        Log.e(RestaurantPageViewModel.TAG, "Empty")
                    }
                    RestaurantRepository.RestaurantRepoStatus.SUCCESS -> {
                        Log.e(RestaurantPageViewModel.TAG, "Success")
                        commentListData.postValue(result.review?.comments)
                    }
                    RestaurantRepository.RestaurantRepoStatus.SERVER_ERROR -> {
                        Log.e(RestaurantPageViewModel.TAG, "Server Error")
                        errorEvent.postRawValue(emptyList())
                    }
                    RestaurantRepository.RestaurantRepoStatus.SOMETHING_WENT_WRONG -> {
                        Log.e(RestaurantPageViewModel.TAG, "Something went wrong")
                        errorEvent.postRawValue(emptyList())
                    }
                }
            }
        }
    }


}