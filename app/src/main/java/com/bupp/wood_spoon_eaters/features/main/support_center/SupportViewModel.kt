package com.bupp.wood_spoon_eaters.features.main.support_center

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.network.ApiService

class SupportViewModel(val metaDataManager: MetaDataManager) : ViewModel() {

    fun getAdminMailAddress(): String? {
        return metaDataManager.getReportsEmailAddress()
    }

    fun openQAPage(): String {
        return metaDataManager.getQaUrl()
    }
}