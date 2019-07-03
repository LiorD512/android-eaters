package com.bupp.wood_spoon_eaters.features.main.pick_filters

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.model.SelectableIcon

class PickFiltersViewModel(val metaDataManager: MetaDataManager) : ViewModel(){


    fun getArrivalTimes(): ArrayList<SelectableIcon> {
        return metaDataManager.getCuisineList()
//        return metaDataManager.getPrepTimeList() as ArrayList<SelectableIcon>
    }

    fun getDietaryList(): ArrayList<SelectableIcon>{
        return metaDataManager.getDietaryList()
    }

}