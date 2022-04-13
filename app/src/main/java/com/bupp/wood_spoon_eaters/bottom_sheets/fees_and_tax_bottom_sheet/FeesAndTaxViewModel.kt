package com.bupp.wood_spoon_eaters.bottom_sheets.fees_and_tax_bottom_sheet

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.getMinOrderFeeStr

class FeesAndTaxViewModel(val appSettingsRepository: AppSettingsRepository) : ViewModel(){

    fun getGlobalMinimumOrderFee(): String{
        return appSettingsRepository.getMinOrderFeeStr(false)
    }

}
