package com.bupp.wood_spoon_eaters.managers

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.WSError

class GlobalErrorManager {

    data class GlobalError(val type: GlobalErrorType, val wsError: WSError?)
    private val globalErrorLiveData = SingleLiveEvent<GlobalError?>()
    fun getGlobalErrorLiveData() = globalErrorLiveData

    enum class GlobalErrorType{
        NETWORK_ERROR,
        GENERIC_ERROR,
        WS_ERROR
    }

    fun postError(errorType: GlobalErrorType, wsError: WSError? = null) {
        globalErrorLiveData.postValue(GlobalError(errorType, wsError))
    }

    fun clear() {
        globalErrorLiveData.postValue(null)
    }


}