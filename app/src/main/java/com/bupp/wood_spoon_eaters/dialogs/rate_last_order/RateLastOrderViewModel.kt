package com.bupp.wood_spoon_eaters.dialogs.rate_last_order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.ReviewRequest
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RateLastOrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    val errorEvent: SingleLiveEvent<List<WSError>> = SingleLiveEvent()
    val progressData = ProgressData()
    val getLastOrder: SingleLiveEvent<Order> = SingleLiveEvent()

    fun getLastOrder(orderId: Long) { //todo !
        viewModelScope.launch {
            progressData.startProgress()
            val result = orderRepository.getOrderById(orderId)
            when (result.type) {
                OrderRepository.OrderRepoStatus.GET_ORDER_BY_ID_SUCCESS -> {
                    result.data?.let {
                        getLastOrder.postValue(it)
                    }
                }
                OrderRepository.OrderRepoStatus.GET_ORDER_BY_ID_FAILED -> {
                    errorEvent.postValue(listOf(WSError(code = null, msg = "Error loading order...")))
                }
                OrderRepository.OrderRepoStatus.WS_ERROR -> {
                    errorEvent.postValue(result.wsError)
                }
                else -> {

                }
            }
            progressData.endProgress()
        }
    }

    val postRating: SingleLiveEvent<Boolean> = SingleLiveEvent()
    fun postRating(orderId: Long, reviewRequest: ReviewRequest) {
        viewModelScope.launch {
            progressData.startProgress()
            val result = orderRepository.postReview(orderId, reviewRequest)
            when (result.type) {
                OrderRepository.OrderRepoStatus.POST_REVIEW_SUCCESS -> {
                    postRating.postValue(true)
                }
                OrderRepository.OrderRepoStatus.POST_ORDER_FAILED -> {
                    errorEvent.postValue(listOf(WSError(code = null, msg = "Error loading order...")))
                }
                OrderRepository.OrderRepoStatus.WS_ERROR -> {
                    errorEvent.postValue(result.wsError)
                }
                else -> {

                }
            }
            progressData.endProgress()
        }
    }

}