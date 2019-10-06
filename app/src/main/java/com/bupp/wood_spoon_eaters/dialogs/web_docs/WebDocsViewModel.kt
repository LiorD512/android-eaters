package com.bupp.wood_spoon_eaters.dialogs.web_docs

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.utils.Constants

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