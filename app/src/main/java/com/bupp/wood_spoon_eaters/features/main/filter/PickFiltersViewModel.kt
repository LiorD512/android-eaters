package com.bupp.wood_spoon_eaters.features.main.filter

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.model.SelectableIcon

class PickFiltersViewModel(val metaDataManager: MetaDataManager) : ViewModel(){


    fun getArrivalTimes(): ArrayList<SelectableIcon> {
        //todo - fix this shit
        return arrayListOf()
    }

    fun getDietaryList(): ArrayList<SelectableIcon>{
        return metaDataManager.getDietaryList()
    }

}