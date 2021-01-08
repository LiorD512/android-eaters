package com.bupp.wood_spoon_eaters.dialogs.web_docs

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.common.Constants

class WebDocsViewModel(val metaDataRepository: MetaDataRepository) : ViewModel() {

    fun getUrl(type: Int): String {
        when(type){
            Constants.WEB_DOCS_TERMS ->{
                return metaDataRepository.getTermsOfServiceUrl()
            }
            Constants.WEB_DOCS_PRIVACY ->{
                return metaDataRepository.getPrivacyPolicyUrl()
            }
            else -> return ""
        }
    }


}