package com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart

import androidx.lifecycle.*
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UpSaleAndCartEvents {
    object OpenUpSaleDialog : UpSaleAndCartEvents()
    object GoToCheckout : UpSaleAndCartEvents()
    object GoToSelectAddress : UpSaleAndCartEvents()
}

class UpSaleNCartViewModel(
    val cartManager: CartManager,
    val eaterDataManager: EaterDataManager
) : ViewModel() {

    private val _events = MutableSharedFlow<UpSaleAndCartEvents>()
    val events: SharedFlow<UpSaleAndCartEvents> = _events


    fun onCheckoutClick() {
        viewModelScope.launch {
            val upSaleItems = cartManager.getUpSaleItems()
            if (!upSaleItems?.items.isNullOrEmpty()) {
                _events.emit(UpSaleAndCartEvents.OpenUpSaleDialog)
            } else {
                if (eaterDataManager.hasUserSetAnAddress()) {
                    _events.emit(UpSaleAndCartEvents.GoToCheckout)
                } else {
                    _events.emit(UpSaleAndCartEvents.GoToSelectAddress)
                }
            }
        }
    }

    fun updateAddressAndProceedToCheckout() {
        viewModelScope.launch {
            val result = cartManager.updateOrderDeliveryAddressParam()
            if (result?.type == OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS) {
                viewModelScope.launch {
                    _events.emit(UpSaleAndCartEvents.GoToCheckout)
                }
            }
        }
    }

}