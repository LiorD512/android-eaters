package com.bupp.wood_spoon_eaters.bottom_sheets.support_center

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository

class SupportViewModel(val metaDataRepository: MetaDataRepository, val eaterDataManager: EaterDataManager) : ViewModel() {

    fun getAdminMailAddress(): String? {
        return metaDataRepository.getReportsEmailAddress()
    }

    fun getEmailSubject(): String {
        return "Query from: ${eaterDataManager.currentEater?.email}"
    }

//    fun openQAPage(): String {
//        return metaDataRepository.getQaUrl()
//    }
}