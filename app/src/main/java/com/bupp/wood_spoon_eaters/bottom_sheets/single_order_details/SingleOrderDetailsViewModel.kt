package com.bupp.wood_spoon_eaters.bottom_sheets.single_order_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.WSError
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.launch

class SingleOrderDetailsViewModel(private val orderRepository: OrderRepository, private val eaterDataManager: EaterDataManager) : ViewModel() {

    val progressData = ProgressData()
    val errorEvent: SingleLiveEvent<List<WSError>> = SingleLiveEvent()

    val singleOrderLiveData = MutableLiveData<Order>()
    fun initSingleOrder(orderId: Long) {
        viewModelScope.launch {
            progressData.startProgress()
            val result = orderRepository.getOrderById(orderId)
            when(result.type){
                OrderRepository.OrderRepoStatus.GET_ORDER_BY_ID_SUCCESS -> {
                    result.data?.let{
                        singleOrderLiveData.postValue(it)
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
}