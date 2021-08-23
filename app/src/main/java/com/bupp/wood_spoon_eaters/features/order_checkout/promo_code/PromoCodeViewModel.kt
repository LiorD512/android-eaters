package com.bupp.wood_spoon_eaters.features.order_checkout.promo_code

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.model.OrderRequest
import com.bupp.wood_spoon_eaters.model.WSError
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.launch


class PromoCodeViewModel(private val cartManager: CartManager) : ViewModel() {

    val progressData = ProgressData()
    val promoCodeEvent: SingleLiveEvent<PromoCodeEvent> = SingleLiveEvent()
    val errorEvent: SingleLiveEvent<List<WSError>> = SingleLiveEvent()
    data class PromoCodeEvent(val isSuccess: Boolean = false)

    fun savePromoCode(code: String) {
        viewModelScope.launch {
            progressData.startProgress()
            val result = cartManager.updateOrderParams(OrderRequest( promoCode = code))
            when(result?.type){
                OrderRepository.OrderRepoStatus.UPDATE_ORDER_SUCCESS -> {
                    promoCodeEvent.postValue(PromoCodeEvent(true))
                }
                OrderRepository.OrderRepoStatus.UPDATE_ORDER_FAILED -> {
                    errorEvent.postValue(listOf(WSError(code = null, msg = "promo code failed")))
                }
                OrderRepository.OrderRepoStatus.WS_ERROR -> {
                    errorEvent.postValue(result.wsError)
                }
            }
            progressData.endProgress()
        }

    }


}
