package com.example.matthias.mvvmcustomviewexample.custom

import android.util.Log
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ToolTipViewModel: KoinComponent{

    val TAG = "ToolTipViewModelVM"
    val metaDataManager: MetaDataManager by inject()

    fun getMinOrderFeeString(): String {
        return metaDataManager.getMinOrderFeeStr()
    }

}