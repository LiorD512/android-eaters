package com.bupp.wood_spoon_eaters.dialogs.update_required

import android.content.Context
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.SourceParams
import java.lang.Exception

class UpdateRequiredViewModel(val metaDataManager: MetaDataManager) : ViewModel() {

    val updateRequiredEvent: SingleLiveEvent<UpdateDialogData> = SingleLiveEvent()
    data class UpdateDialogData(val title: String, val body: String, val redirectUrl: String)

    fun getDialogData(){
        val title = metaDataManager.getUpdateDialogTitle()
        val body = metaDataManager.getUpdateDialogBody()
        val url = metaDataManager.getUpdateDialogUrl()
        updateRequiredEvent.postValue(UpdateDialogData(title, body, url))
    }


}