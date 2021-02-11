package com.bupp.wood_spoon_eaters.dialogs.web_docs

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository

class WebDocsViewModel(private val metaDataManager: MetaDataRepository) : ViewModel() {

    fun getUrl(type: Int): String {
        when(type){
            Constants.WEB_DOCS_TERMS ->{
                return metaDataManager.getTermsOfServiceUrl()
            }
            Constants.WEB_DOCS_PRIVACY ->{
                return metaDataManager.getPrivacyPolicyUrl()
            }
            Constants.WEB_DOCS_QA ->{
                return metaDataManager.getQaUrl()
            }
            else -> return ""
        }
    }


}