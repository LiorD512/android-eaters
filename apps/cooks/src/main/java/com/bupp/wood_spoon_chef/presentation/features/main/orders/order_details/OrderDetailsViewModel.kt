package com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.di.abs.LiveEventData
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.Order
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.data.repositories.OrderRepository
import com.bupp.wood_spoon_chef.domain.GetSupportNumberUseCase
import com.bupp.wood_spoon_chef.domain.IsCallSupportByCancelingOrderUseCase
import com.bupp.wood_spoon_chef.utils.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.bupp.wood_spoon_chef.domain.comon.execute
import kotlinx.coroutines.flow.*

data class CookingSlotOrdersEvent(
    val isSuccess: Boolean,
    val order: List<Order>?
)

data class UpdateCookingSlotEvent(
    val isSuccess: Boolean,
    val cookingSlot: CookingSlot?
)

sealed class OrderCancellationAction {
    class ShowCallSupportDialog(val tel: String) : OrderCancellationAction()
    class ShowCancelOrderDialog(val curOrder: Order) : OrderCancellationAction()
    object Default : OrderCancellationAction()
}

class OrderDetailsViewModel(
    val settings: UserSettings,
    private val cookingSlotRepository: CookingSlotRepository,
    private val orderRepository: OrderRepository,
    private val getSupportNumberUseCase: GetSupportNumberUseCase,
    private val isCallSupportByCancelingOrderUseCase: IsCallSupportByCancelingOrderUseCase
) : BaseViewModel() {

    private var currentCookingSlotId: Long = -1

    private var _orderCancellationActionFlow = MutableStateFlow<OrderCancellationAction>(
        OrderCancellationAction.Default
    )
    val orderCancellationActionFlow = _orderCancellationActionFlow

    val cookingSlotOrders: MutableLiveData<CookingSlotOrdersEvent> = MutableLiveData()

    val updateCookingSlot: MutableLiveData<UpdateCookingSlotEvent> = MutableLiveData()
    val statusUpdateSuccessEvent = LiveEventData<Int>()

    fun onNotAvailableSwitchChanged(shouldShow: Boolean) {
        settings.setShouldShowNotAvailableDialog(shouldShow)
    }

    fun initData(cookingSlotId: Long) {
        currentCookingSlotId = cookingSlotId
        getCookingSlotOrders()
    }

    fun getCookingSlotOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = cookingSlotRepository.getCookingSlotOrders(currentCookingSlotId)) {
                is ResponseSuccess -> {
                    val orders = response.data
                    cookingSlotOrders.postValue(CookingSlotOrdersEvent(true, orders))
                }
                is ResponseError -> {
                    errorEvent.postRawValue(response.error)
                    cookingSlotOrders.postValue(CookingSlotOrdersEvent(false, null))
                }
            }
        }
    }

    fun updateCookingSlotAvailability(slotId: Long, isAvailable: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                cookingSlotRepository.updateCookingSlotAvailability(slotId, isAvailable)) {
                is ResponseSuccess -> {
                    val cookingSlot = response.data
                    updateCookingSlot.postValue(UpdateCookingSlotEvent(true, cookingSlot))
                }
                is ResponseError -> {
                    errorEvent.postRawValue(response.error)
                    updateCookingSlot.postValue(UpdateCookingSlotEvent(false, null))
                }
            }
        }
    }

    fun updateStatus(status: Int, orderId: Long) {
        when (status) {
            Constants.PREPARATION_STATUS_IDLE -> {
                setStatusAcceptOrder(orderId)
            }
            Constants.PREPARATION_STATUS_RECEIVED -> {
                setStatusAcceptStart(orderId)
            }
            Constants.PREPARATION_STATUS_IN_PROGRESS -> {
                setStatusAcceptFinish(orderId)
            }
        }
    }

    private fun setStatusAcceptOrder(orderId: Long) {
        progressData.startProgress()

        viewModelScope.launch(Dispatchers.IO) {
            when (val response = orderRepository.setStatusAccept(orderId)) {
                is ResponseSuccess -> {
                    statusUpdateSuccessEvent.postRawValue(Constants.PREPARATION_STATUS_RECEIVED)
                }
                is ResponseError -> {
                    errorEvent.postRawValue(response.error)
                }
            }

            progressData.endProgress()
        }
    }

    private fun setStatusAcceptStart(orderId: Long) {
        progressData.startProgress()

        viewModelScope.launch(Dispatchers.IO) {
            when (val response = orderRepository.setStatusStart(orderId)) {
                is ResponseSuccess -> {
                    statusUpdateSuccessEvent.postRawValue(Constants.PREPARATION_STATUS_IN_PROGRESS)
                }
                is ResponseError -> {
                    errorEvent.postRawValue(response.error)
                }
            }

            progressData.endProgress()
        }
    }

    private fun setStatusAcceptFinish(orderId: Long) {
        progressData.startProgress()

        viewModelScope.launch(Dispatchers.IO) {
            when (val response = orderRepository.setStatusFinish(orderId)) {
                is ResponseSuccess -> {
                    statusUpdateSuccessEvent.postRawValue(Constants.PREPARATION_STATUS_COMPLETED)
                }
                is ResponseError -> {
                    errorEvent.postRawValue(response.error)
                }
            }

            progressData.endProgress()
        }
    }

    fun onCancelClick(curOrder: Order) {
        viewModelScope.launch {
            isCallSupportByCancelingOrderUseCase.execute()
                .collectLatest { isEnabled ->
                    if (!isEnabled) {
                        val first = getSupportNumberUseCase.execute().first()

                        _orderCancellationActionFlow.emit(
                            OrderCancellationAction.ShowCallSupportDialog(first)
                        )
                    } else {
                        _orderCancellationActionFlow.emit(
                            OrderCancellationAction.ShowCancelOrderDialog(curOrder = curOrder)
                        )
                    }
                }
        }
    }
}