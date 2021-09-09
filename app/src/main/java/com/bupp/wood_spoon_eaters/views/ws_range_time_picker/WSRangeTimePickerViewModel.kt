package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WSRangeTimePickerViewModel: KoinComponent{

    val metaDataRepository: MetaDataRepository by inject()

    companion object{
        const val TAG = "wowWSRangeTimePickerVM"
    }

    fun getMinFutureOrderWindow(): Int{
        return metaDataRepository.getMinFutureOrderWindow()
    }

    fun getHoursInterval(): Int{
        return 30
    }

}