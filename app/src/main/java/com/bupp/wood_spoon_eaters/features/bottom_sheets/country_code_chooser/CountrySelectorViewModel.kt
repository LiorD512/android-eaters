package com.bupp.wood_spoon_eaters.features.bottom_sheets.country_code_chooser

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.model.Country
import com.bupp.wood_spoon_eaters.model.State
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository


class CountrySelectorViewModel(
    val metaDataRepository: MetaDataRepository
) : ViewModel() {

    val countriesData = LiveEventData<List<State>?>()

    init{
        countriesData.postRawValue(metaDataRepository.getStates())
    }

}