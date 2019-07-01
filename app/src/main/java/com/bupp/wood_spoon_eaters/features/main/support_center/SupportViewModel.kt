package com.bupp.wood_spoon_eaters.features.main.support_center

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.network.ApiService

class SupportViewModel(val api: ApiService) : ViewModel() {

    private var comment: String? = ""
    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(val dismissDialog: Boolean = false)

    fun setCommentToSupport(comment: String) {
        this.comment = comment
    }

    fun sendCommentToSupport() {

        navigationEvent.postValue(
            NavigationEvent(
                true
            )
        )
    }
}