package com.bupp.wood_spoon_chef.presentation.dialogs.update_required

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_chef.data.repositories.AppSettingsRepository
import com.bupp.wood_spoon_chef.data.repositories.getUpdateDialogBody
import com.bupp.wood_spoon_chef.data.repositories.getUpdateDialogTitle
import com.bupp.wood_spoon_chef.data.repositories.getUpdateDialogUrl

class UpdateRequiredViewModel(private val appSettingsRepository: AppSettingsRepository) : ViewModel() {

    val updateRequiredEvent: MutableLiveData<UpdateDialogData> = MutableLiveData()
    data class UpdateDialogData(val title: String, val body: String, val redirectUrl: String)

    fun getDialogData(){
        val title = appSettingsRepository.getUpdateDialogTitle()
        val body = appSettingsRepository.getUpdateDialogBody()
        val url = appSettingsRepository.getUpdateDialogUrl()
        updateRequiredEvent.postValue(UpdateDialogData(title, body, url))
    }


}