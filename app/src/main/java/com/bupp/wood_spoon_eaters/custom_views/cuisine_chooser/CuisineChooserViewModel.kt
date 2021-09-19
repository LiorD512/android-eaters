package com.bupp.wood_spoon_eaters.custom_views.cuisine_chooser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository

class CuisineChooserViewModel(
    private val metaDataRepository: MetaDataRepository,
    private val eaterDataManager: EaterDataManager,

) : ViewModel() {


    data class CuisineData(val allCuisines: List<SelectableIcon>, val selectedCuisine: List<SelectableIcon>?)
    val cuisineLiveData = MutableLiveData<CuisineData>()

    init{
        cuisineLiveData.postValue(CuisineData(getCuisineList(), getSelectedCuisineList()))
    }

    fun getCuisineList(): List<SelectableIcon> {
        return metaDataRepository.getCuisineListSelectableIcons()
    }

    fun getSelectedCuisineList(): List<SelectableIcon>? {
        return eaterDataManager.currentEater?.getSelectedCuisines()
    }



    companion object {
        const val TAG = "wowRestaurantPageVM"
    }

}
