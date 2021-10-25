package com.bupp.wood_spoon_eaters.features.reviews

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.ReviewRequest
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.model.Order

class ReviewsViewModel() : ViewModel() {
    val navigationEvent = LiveEventData<NavigationEvent>()
    var order: Order? = null
    val reviewRequest = ReviewRequest()

    var rating: Int? = null

    enum class NavigationEvent {
        EXPERIENCE_TO_DETAILS
    }

    fun onNextClick() {
        navigationEvent.postRawValue(NavigationEvent.EXPERIENCE_TO_DETAILS)
    }

    fun onSubmitClick(reviewText: String?, supportMessage: String?){
        rating?.let{
           Log.d("wowTest", "rating =$rating, reviewText= $reviewText, supportMessage= $supportMessage")
        }
    }

    fun initExtras(order: Order?) {
        this.order = order
    }

    fun setRating(rating: Int) {
        this.rating = rating
    }


}