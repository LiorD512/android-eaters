package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WSRangeTimePickerViewModel: KoinComponent{

    val appSettingsRepository: AppSettingsRepository by inject()

    companion object{
        const val TAG = "wowWSRangeTimePickerVM"
    }

    fun getMinFutureOrderWindow(): Int{
        return appSettingsRepository.getMinFutureOrderWindow()
    }

    fun getHoursInterval(): Int{
        return 30
    }

}