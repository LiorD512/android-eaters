package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import android.util.Log
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WSRangeTimePickerViewModel: KoinComponent{//}, EaterDataManager.EaterDataMangerListener {

    val metaDataRepository: MetaDataRepository by inject()

    companion object{
        const val TAG = "wowWSRangeTimePickerVM"
    }

    fun getMinFutureOrderWindow(): Int{
        return metaDataRepository.getMinFutureOrderWindow()
    }

}