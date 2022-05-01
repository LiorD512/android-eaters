package com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.dialogs.cancel_order

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.di.abs.LiveEventData
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CancellationReason
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.data.repositories.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CancelOrderViewModel(
    private val orderRepository: OrderRepository,
    private val metaDataManager: MetaDataRepository
) : BaseViewModel() {


    val cancelOrderEvent: LiveEventData<CancelOrderEvent> = LiveEventData()

    data class CancelOrderEvent(val isSuccess: Boolean)

    fun getCancellationReasons(): List<CancellationReason> {
        return metaDataManager.getCancellationReasons()
    }

    fun cancelOrder(orderId: Long, reasonId: Long? = null, note: String? = null) {
        progressData.startProgress()
        viewModelScope.launch(Dispatchers.IO) {
                when (val result = orderRepository.cancelOrder(orderId, reasonId, note)) {
                    is ResponseSuccess -> {
                        cancelOrderEvent.postRawValue(CancelOrderEvent(true))
                    }
                    is ResponseError -> {
                        cancelOrderEvent.postRawValue(CancelOrderEvent(false))
                        errorEvent.postRawValue(result.error)
                    }
                }
            progressData.endProgress()
        }
    }

}