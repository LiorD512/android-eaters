package com.bupp.wood_spoon_eaters.features.main.support_center

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.MetaDataRepository

class SupportViewModel(val metaDataRepository: MetaDataRepository) : ViewModel() {

    fun getAdminMailAddress(): String? {
        return metaDataRepository.getReportsEmailAddress()
    }
}