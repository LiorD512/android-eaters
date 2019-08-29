package com.bupp.wood_spoon_eaters.dialogs.web_docs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.network.google.interfaces.GoogleApi
import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants
import retrofit2.Call
import retrofit2.Response

class WebDocsViewModel(val metaDataManager: MetaDataManager) : ViewModel() {

    fun getUrl(type: Int): String {
        when(type){
            Constants.WEB_DOCS_TERMS ->{
                return metaDataManager.getTermsOfServiceUrl()
            }
            Constants.WEB_DOCS_PRIVACY ->{
                return metaDataManager.getPrivacyPolicyUrl()
            }
            else -> return ""
        }
    }


}