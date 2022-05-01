package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.reviews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CommentAdapterItem
import com.bupp.wood_spoon_chef.data.remote.model.CommentSkeleton
import com.bupp.wood_spoon_chef.data.remote.model.ReviewBreakdown
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewsBSViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    val reviewBreakdown = MutableLiveData<ReviewData?>()
    val commentListData = MutableLiveData<List<CommentAdapterItem>?>()

    data class ReviewData(val reviewCount: Int?, val avgRating: Double?, val breakdown: ReviewBreakdown?)

    fun initData() {
        getSkeletonData()
        getCookReview()
    }

    private fun getSkeletonData() {
        val list = listOf(CommentSkeleton(), CommentSkeleton(), CommentSkeleton(), CommentSkeleton())
        commentListData.postValue(list)
    }

    private fun getCookReview() {
        viewModelScope.launch(Dispatchers.IO){
            when (val response =userRepository.getCookReview() ) {
                is ResponseSuccess -> {
                    commentListData.postValue(response.data?.comments)
                    val cook = userRepository.getCurrentChef()
                    reviewBreakdown.postValue(ReviewData(cook?.reviewCount, cook?.rating, response.data?.breakdown))
                }
                is ResponseError -> {
                    errorEvent.postRawValue(response.error)
                }
            }
        }
    }

    fun getRestaurantName(): String =
        userRepository.getCurrentChef()?.restaurantName ?: ""

}