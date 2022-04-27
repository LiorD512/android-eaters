package com.bupp.wood_spoon_eaters.dialogs.update_required

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.repositories.*

class UpdateRequiredViewModel(val appSettingsRepository: AppSettingsRepository) : ViewModel() {

    val updateRequiredEvent: SingleLiveEvent<UpdateDialogData> = SingleLiveEvent()
    data class UpdateDialogData(val title: String, val body: String, val redirectUrl: String)

    fun getDialogData(){
        val title = appSettingsRepository.getUpdateDialogTitle()
        val body = appSettingsRepository.getUpdateDialogBody()
        val url = appSettingsRepository.getUpdateDialogUrl()
        updateRequiredEvent.postValue(UpdateDialogData(title, body, url))
    }


}