package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.support_center

import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.utils.UserSettings

class SupportViewModel(val metaDataRepository: MetaDataRepository, val userSettings: UserSettings) : BaseViewModel() {

    fun getAdminMailAddress(): String {
        return metaDataRepository.getSupportEmailAddress()
    }

    fun getMailTitle(): String {
        userSettings.currentCook?.let{
            return "Cook - ${it.getFullName()} Support Request"
        }
        return ""
    }


}