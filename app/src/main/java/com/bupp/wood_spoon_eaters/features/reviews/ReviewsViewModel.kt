package com.bupp.wood_spoon_eaters.features.reviews

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.model.Order

class ReviewsViewModel() : ViewModel() {
    val navigationEvent = LiveEventData<NavigationEvent>()
    var order: Order? = null

    enum class NavigationEvent {
        EXPERIENCE_TO_DETAILS
    }

    fun onNextClick() {
        navigationEvent.postRawValue(NavigationEvent.EXPERIENCE_TO_DETAILS)
    }

    fun onSubmitClick(){

    }

    fun initExtras(order: Order?) {
        this.order = order
    }


}