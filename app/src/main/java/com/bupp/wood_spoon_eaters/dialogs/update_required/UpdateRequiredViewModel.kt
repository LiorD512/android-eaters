package com.bupp.wood_spoon_eaters.dialogs.update_required

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.MetaDataRepository

class UpdateRequiredViewModel(val metaDataRepository: MetaDataRepository) : ViewModel() {

    val updateRequiredEvent: SingleLiveEvent<UpdateDialogData> = SingleLiveEvent()
    data class UpdateDialogData(val title: String, val body: String, val redirectUrl: String)

    fun getDialogData(){
        val title = metaDataRepository.getUpdateDialogTitle()
        val body = metaDataRepository.getUpdateDialogBody()
        val url = metaDataRepository.getUpdateDialogUrl()
        updateRequiredEvent.postValue(UpdateDialogData(title, body, url))
    }


}