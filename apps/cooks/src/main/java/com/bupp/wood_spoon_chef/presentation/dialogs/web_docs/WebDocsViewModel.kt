package com.bupp.wood_spoon_chef.presentation.dialogs.web_docs

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository

class WebDocsViewModel(private val metaDataRepository: MetaDataRepository) : ViewModel() {

    fun getUrl(type: Int): String {
        return when(type){
            Constants.WEB_DOCS_TERMS ->{
                metaDataRepository.getTermsOfServiceUrl()
            }
            Constants.WEB_DOCS_PRIVACY ->{
                metaDataRepository.getPrivacyPolicyUrl()
            }
            Constants.WEB_DOCS_QA -> {
                metaDataRepository.getQAUrl()
            }
            else -> ""
        }
    }

}