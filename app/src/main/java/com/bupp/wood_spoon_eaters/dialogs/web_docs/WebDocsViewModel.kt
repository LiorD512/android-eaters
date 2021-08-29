package com.bupp.wood_spoon_eaters.dialogs.web_docs

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository

class WebDocsViewModel(private val metaDataManager: MetaDataRepository, private val flowEventsManager: FlowEventsManager) : ViewModel() {

    fun getUrl(type: Int): String {
        when(type){
            Constants.WEB_DOCS_TERMS ->{
                flowEventsManager.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_PRIVACY_POLICY)
                return metaDataManager.getTermsOfServiceUrl()
            }
            Constants.WEB_DOCS_PRIVACY ->{
                flowEventsManager.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_PRIVACY_POLICY)
                return metaDataManager.getPrivacyPolicyUrl()
            }
            Constants.WEB_DOCS_QA ->{
                flowEventsManager.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_QA)
                return metaDataManager.getQaUrl()
            }
            else -> return ""
        }
    }


}