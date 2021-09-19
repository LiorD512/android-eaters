package com.bupp.wood_spoon_eaters.dialogs.cancel_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.EaterDataRepository
import kotlinx.coroutines.launch

class CancelOrderViewModel(val eaterDataManager: EaterDataManager) : ViewModel() {

    val cancelOrder: SingleLiveEvent<Boolean> = SingleLiveEvent()
//    data class CancelOrderEvent(val isSuccess: Boolean)

    fun cancelOrder(orderId: Long?, note: String? = null){
        viewModelScope.launch {
            val result = eaterDataManager.cancelOrder(orderId, note)
            when(result?.type){
                EaterDataRepository.EaterDataRepoStatus.CANCEL_ORDER_SUCCESS -> {
                    cancelOrder.postValue(true)
                }
                EaterDataRepository.EaterDataRepoStatus.CANCEL_ORDER_FAILED -> {
                    cancelOrder.postValue(false)
                }
                else -> {
                }
            }
        }

    }

}