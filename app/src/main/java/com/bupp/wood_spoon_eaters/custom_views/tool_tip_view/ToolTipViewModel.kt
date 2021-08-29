package com.bupp.wood_spoon_eaters.custom_views.tool_tip_view

import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ToolTipViewModel: KoinComponent{

    var isNationwide: Boolean = false
    val TAG = "ToolTipViewModelVM"
    val metaDataRepository: MetaDataRepository by inject()

    fun getMinOrderFeeString(): String {
        return metaDataRepository.getMinOrderFeeStr(isNationwide)
    }

}