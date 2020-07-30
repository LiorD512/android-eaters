package com.example.matthias.mvvmcustomviewexample.custom

import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import org.koin.core.KoinComponent
import org.koin.core.inject

class ToolTipViewModel: KoinComponent{

    var isNationwide: Boolean = false
    val TAG = "ToolTipViewModelVM"
    val metaDataManager: MetaDataManager by inject()

    fun getMinOrderFeeString(): String {
        return metaDataManager.getMinOrderFeeStr(isNationwide)
    }

}