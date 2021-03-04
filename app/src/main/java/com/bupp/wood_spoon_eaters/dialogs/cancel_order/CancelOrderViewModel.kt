package com.bupp.wood_spoon_eaters.dialogs.cancel_order

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.google.interfaces.GoogleApi
import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.repositories.EaterDataRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            }
        }

    }

}