package com.bupp.wood_spoon_eaters.bottom_sheets.fees_and_tax_bottom_sheet

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository

class FeesAndTaxViewModel(val metaDataRepository: MetaDataRepository) : ViewModel(){

    fun getGlobalMinimumOrderFee(): String{
        return metaDataRepository.getMinOrderFeeStr(false)
    }

}
