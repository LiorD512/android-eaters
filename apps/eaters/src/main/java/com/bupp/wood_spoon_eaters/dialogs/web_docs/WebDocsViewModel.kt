package com.bupp.wood_spoon_eaters.dialogs.web_docs

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.repositories.*

class WebDocsViewModel(private val appSettingsRepository: AppSettingsRepository, private val flowEventsManager: FlowEventsManager) : ViewModel() {

    fun getUrl(type: Int): String {
        when(type){
            Constants.WEB_DOCS_TERMS ->{
                flowEventsManager.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_PRIVACY_POLICY)
                return appSettingsRepository.getTermsOfServiceUrl()
            }
            Constants.WEB_DOCS_PRIVACY ->{
                flowEventsManager.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_PRIVACY_POLICY)
                return appSettingsRepository.getPrivacyPolicyUrl()
            }
            Constants.WEB_DOCS_QA ->{
                flowEventsManager.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_QA)
                return appSettingsRepository.getQaUrl()
            }
            else -> return ""
        }
    }


}