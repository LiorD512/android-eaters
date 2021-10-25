package com.bupp.wood_spoon_eaters.features.reviews

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.ReviewRequest
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.WSError
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.bupp.wood_spoon_eaters.utils.Utils.getErrorsMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewsViewModel(val orderRepository: OrderRepository, val userRepository: UserRepository) : ViewModel() {

    val navigationEvent = LiveEventData<NavigationEvent>()
    val reviewSuccess = LiveEventData<Boolean>()
    val errorEvent = LiveEventData<List<WSError>?>()
    var order: Order? = null
    val reviewRequest = ReviewRequest()

    var rating: Int? = null

    enum class NavigationEvent {
        EXPERIENCE_TO_DETAILS
    }

    fun onNextClick() {
        navigationEvent.postRawValue(NavigationEvent.EXPERIENCE_TO_DETAILS)
    }

    fun onSubmitClick(reviewText: String?, supportMessage: String?) {
        order?.let { order ->
            rating?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("wowTest", "rating =$rating, reviewText= $reviewText, supportMessage= $supportMessage")
                    val request = ReviewRequest(rating = rating, reviewText = reviewText, supportMessage = supportMessage)
                    val result = orderRepository.postReview(order.id, request)

                    if (result.type == OrderRepository.OrderRepoStatus.POST_REVIEW_SUCCESS) {
                        reviewSuccess.postRawValue(true)
                    } else if (result.type == OrderRepository.OrderRepoStatus.WS_ERROR){
                        errorEvent.postRawValue(result.wsError)
                    }
                }
            }
        }
    }

    fun ignoreTrigger() {
        order?.let { order ->
            viewModelScope.launch(Dispatchers.IO) {
                orderRepository.ignoreReview(order.id)
            }
        }
    }

    fun initExtras(order: Order?) {
        this.order = order
    }

    fun setRating(rating: Int) {
        this.rating = rating
    }

    fun getEaterName(): String {
        return userRepository.getUser()?.firstName ?: ""
    }


}