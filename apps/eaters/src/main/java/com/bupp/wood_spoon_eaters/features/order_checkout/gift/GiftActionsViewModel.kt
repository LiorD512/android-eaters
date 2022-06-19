package com.bupp.wood_spoon_eaters.features.order_checkout.gift

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.managers.logEvent
import com.bupp.wood_spoon_eaters.model.OrderGiftRequest
import com.eatwoodspoon.analytics.events.EatersGiftEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GiftActionsViewModelState(
    val inProgress: Boolean = false,
)

sealed class GiftActionsViewModelEvents {
    data class Error(val message: String? = null) : GiftActionsViewModelEvents()
    object ShowEditOptions : GiftActionsViewModelEvents()
    object NavigateDone : GiftActionsViewModelEvents()
    object NavigateEdit : GiftActionsViewModelEvents()
}

class GiftActionsViewModel(
    private val cartManager: CartManager,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker
) : ViewModel() {

    private val _state = MutableStateFlow(GiftActionsViewModelState())
    val state: StateFlow<GiftActionsViewModelState> = _state

    private val _events = MutableSharedFlow<GiftActionsViewModelEvents>()
    val events: SharedFlow<GiftActionsViewModelEvents> = _events

    fun onStarted() {
        viewModelScope.launch {
            if (cartManager.getCurrentOrderData().value?.isGift == true) {
                _events.emit(GiftActionsViewModelEvents.ShowEditOptions)
            } else {
                _events.emit(GiftActionsViewModelEvents.NavigateEdit)
            }
        }
    }


    private fun setInProgress(inProgress: Boolean) {
        _state.update {
            it.copy(inProgress = inProgress)
        }
    }

    fun onEditSelected() {
        logEvent { EatersGiftEvent.EditGiftClickedEvent(it) }
        viewModelScope.launch {
            _events.emit(GiftActionsViewModelEvents.NavigateEdit)
        }
    }

    fun onClearSelected() {
        logEvent { EatersGiftEvent.EditGiftClickedEvent(it) }
        setInProgress(true)
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val result =
                    cartManager.updateOrderGiftParams(
                        OrderGiftRequest(
                            false, null, null, null, null, null
                        ), null
                    )

                if (result?.data != null && result.wsError.isNullOrEmpty()) {
                    _events.emit(GiftActionsViewModelEvents.NavigateDone)
                } else {
                    _events.emit(GiftActionsViewModelEvents.Error(result?.wsError?.firstOrNull()?.msg))
                }
            } catch (ex: Exception) {
                _events.emit(GiftActionsViewModelEvents.Error())
            } finally {
                setInProgress(false)
            }
        }

    }

    private fun logEvent(event: (order_id: String) -> EatersGiftEvent) {
        val orderId = cartManager.getCurrentOrderData().value?.id?.toString() ?: ""
        eatersAnalyticsTracker.logEvent(event.invoke(orderId))
    }
}