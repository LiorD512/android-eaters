package com.bupp.wood_spoon_eaters.features.order_checkout.gift

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.managers.logEvent
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderGiftRequest
import com.bupp.wood_spoon_eaters.utils.Utils
import com.eatwoodspoon.analytics.events.EatersGiftEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.ibrahimsn.lib.PhoneNumberKit

data class GiftViewModelState(
    val recipientFirstName: String? = null,
    val recipientLastName: String? = null,
    val recipientPhoneNumber: String? = null,
    val notifyRecipient: Boolean = false,
    val recipientEmail: String? = null,
    val inProgress: Boolean = false,
    val errors: List<Errors> = emptyList()
) {
    enum class Errors {
        recipientFirstName,
        recipientLastName,
        recipientPhoneNumber,
        recipientEmail
    }
}

sealed class GiftViewModelEvents {
    data class Error(val message: String? = null) : GiftViewModelEvents()
    object NavigateDone : GiftViewModelEvents()
}

class GiftViewModel(
    private val cartManager: CartManager,
    private val stateMapper: GiftStateMapper,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker
) : ViewModel() {

    private val _state =
        MutableStateFlow(cartManager.getCurrentOrderData().value?.let {
            stateMapper.mapOrderToState(it)
        } ?: GiftViewModelState())
    val state: StateFlow<GiftViewModelState> = _state

    private val _events = MutableSharedFlow<GiftViewModelEvents>()
    val events: SharedFlow<GiftViewModelEvents> = _events

    private var submitWasPressed = false

    fun setValues(
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        notifyRecipient: Boolean,
        email: String?
    ) {
        _state.update {
            it.copy(
                recipientFirstName = firstName,
                recipientLastName = lastName,
                recipientPhoneNumber = phoneNumber,
                notifyRecipient = notifyRecipient,
                recipientEmail = email
            )
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        _state.update {
            it.copy(inProgress = inProgress)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private fun validateInputs(): Boolean {
        val errors = mutableListOf<GiftViewModelState.Errors>()
        with(_state.value) {
            if (recipientFirstName.isNullOrBlank()) {
                errors.add(GiftViewModelState.Errors.recipientFirstName)
            }
            if (recipientLastName.isNullOrBlank()) {
                errors.add(GiftViewModelState.Errors.recipientLastName)
            }
            if (recipientPhoneNumber.isNullOrBlank()) {
                errors.add(GiftViewModelState.Errors.recipientPhoneNumber)
            }
            if (notifyRecipient &&
                (recipientEmail.isNullOrBlank() || !Utils.isValidEmailAddress(recipientEmail)
                        )
            ) {
                errors.add(GiftViewModelState.Errors.recipientEmail)
            }
        }
        _state.update { it.copy(errors = errors.toList()) }
        return errors.isEmpty()
    }

    fun submit() {
        submitWasPressed = true
        logEvent { EatersGiftEvent.ClickAddDetailsInGiftScreenEvent(order_id = it) }
        if (validateInputs()) {

            setInProgress(true)
            viewModelScope.launch(Dispatchers.IO) {

                try {
                    val result =
                        cartManager.updateOrderGiftParams(
                            stateMapper.mapStateToOrderGiftRequest(
                                _state.value
                            ), null
                        )

                    if (result?.data != null && result.wsError.isNullOrEmpty()) {
                        _events.emit(GiftViewModelEvents.NavigateDone)
                    } else {
                        _events.emit(GiftViewModelEvents.Error(result?.wsError?.firstOrNull()?.msg))
                    }
                } catch (ex: Exception) {
                    _events.emit(GiftViewModelEvents.Error())
                } finally {
                    setInProgress(false)
                }
            }
        }
    }

    fun logEvent(event: (order_id: String) -> EatersGiftEvent) {
        val orderId = cartManager.getCurrentOrderData().value?.id?.toString() ?: ""
        eatersAnalyticsTracker.logEvent(event.invoke(orderId))
    }
}
